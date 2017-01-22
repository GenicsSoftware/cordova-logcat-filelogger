package de.mj.cordova.plugin.filelogger;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import de.mj.cordova.plugin.filelogger.org.apache.commons.io.input.ReversedLinesFileReader;

class LcFileWriter extends BashExecutorEventHandler {

    private File logFile;
    private File logBak;
    private CordovaWebView webView;
    private Context context;
    private Activity cordovaActivity;
    private String[] filterBy;
    private String[] filterOut;
    private int maxFileSizeInKB;
    private boolean enableCallback;
    private CallbackContext callbackContext;

    private enum RETURN_CODE {
        LOGGING_STOPPED,
        PROCESS_KILLED,
        LOGCAT_COMMAND_RETURNED_ERROR
    }

    LcFileWriter(final File logFile, final File logBak, final CordovaInterface cordovaInstance,
                 final CordovaWebView webView, final String[] filterBy, final String[] filterOut,
                 int maxFileSizeInKB, boolean enableCallback,
                 final CallbackContext callbackContext) {
        this.logFile = logFile;
        this.logBak = logBak;
        this.cordovaActivity = cordovaInstance.getActivity();
        this.context = this.cordovaActivity.getApplicationContext();
        this.webView = webView;
        this.filterBy = filterBy;
        this.filterOut = filterOut;
        this.maxFileSizeInKB = maxFileSizeInKB;
        this.enableCallback = enableCallback;
        this.callbackContext = callbackContext;
    }

    @Override
    public void standardOutput(final String line) {
        if (line != null) {
            this.handleLine(line.replaceAll("\\s+", " ") + "\n");
        }
    }

    @Override
    public void errorOutput(String line) {
        this.handleLine(line + "\n");
        this.callbackContext.error(RETURN_CODE.LOGCAT_COMMAND_RETURNED_ERROR.name() + "\n" + line);
    }

    @Override
    public void executionStarted() {
        Log.v(LogCatPlugin.TAG, "Starting to log: " + this.logFile.getAbsolutePath());
    }

    @Override
    public void executionStopped() {
        Log.v(LogCatPlugin.TAG, "Stopping to log: " + this.logFile.getAbsolutePath());
        this.callbackContext.error(RETURN_CODE.LOGGING_STOPPED.name());
    }

    @Override
    public void processKilled() {
        Log.v(LogCatPlugin.TAG, "Logging process killed.");
        this.callbackContext.error(RETURN_CODE.PROCESS_KILLED.name());
    }

    private LogEntry getLatestEntry(final File logFile) {
        if (logFile == null || !logFile.exists()) {
            return null;
        }
        ReversedLinesFileReader reverseReader = null;
        try {
            reverseReader = new ReversedLinesFileReader(logFile, Charset.forName("UTF-8"));
            String line;
            do {
                line = reverseReader.readLine();
                if (line != null) {
                    if (LogEntry.isHeader(line)) {
                        return new LogEntry(line);
                    }
                }
            } while (line != null && !Thread.currentThread().isInterrupted());
        } catch (IOException e) {
            Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
        } finally {
            if (reverseReader != null) {
                try {
                    reverseReader.close();
                } catch (IOException e) {
                    Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
                }
            }
        }
        return null;
    }

    private boolean isNewEntry(final String header, final File oldLogFile) {
        final LogEntry entryToCheck = new LogEntry(header);
        final LogEntry latestEntry = this.getLatestEntry(oldLogFile);
        return latestEntry == null || entryToCheck.getTimestamp().after(latestEntry.getTimestamp());
    }

    private boolean isNewLine = false;

    private boolean isNewLine(final String line, final File logFile, final File logBak) {
        if (!isNewLine) {
            File latestLogFile = null;
            if (logFile != null && logFile.exists()) {
                latestLogFile = logFile;
            } else if (logBak != null && logBak.exists()) {
                latestLogFile = logBak;
            }

            isNewLine = latestLogFile == null || LogEntry.isHeader(line) && isNewEntry(line, latestLogFile);
        }
        return isNewLine;
    }

    private boolean shouldLog(String message, final String[] filterByList, final String[] filterOutList) {
        if (filterByList == null && filterOutList == null) {
            return true;
        }

        message = message.replaceAll("\\s+", " ");

        boolean accept = filterByList == null;
        if (filterByList != null) {
            for (final String filterBy : filterByList) {
                if (message.contains(filterBy)) {
                    accept = true;
                    break;
                }
            }
        }

        if (filterOutList != null) {
            for (final String filterOut : filterOutList) {
                if (message.contains(filterOut)) {
                    accept = false;
                    break;
                }
            }
        }

        return accept;
    }

    private void callBack(final String message) {
        if (this.enableCallback) {
            this.cordovaActivity.runOnUiThread(new Runnable() {
                public void run() {
                    webView.loadUrl("javascript:cordova.plugins.LogCatPlugin.onLogCatEntry('" +
                            message.replace("\n", "--linebreak--").replace("'", "\\'") + "')");
                }
            });
        }
    }

    private String messageBody = null;
    private void handleLine(final String line) {

        if (line == null || line.trim().isEmpty() || line.contains("I/System.out")) {
            return;
        }

        if (this.logFile != null && isNewLine(line, this.logFile, this.logBak)) {
            float fileSizeInKB = (float) this.logFile.length() / 1024;

            if (fileSizeInKB > (float) maxFileSizeInKB) {
                if (this.logBak != null) {
                    if (this.logBak.exists()) {
                        this.logBak.delete();
                    }
                    this.logFile.renameTo(this.logBak);
                }
            }

            if (LogEntry.isHeader(line)) {
                if (this.messageBody != null) {
                    if (this.shouldLog(this.messageBody, this.filterBy, this.filterOut)) {
                        FileTools.append(this.messageBody + "\n", this.logFile, this.context);
                        this.callBack(this.messageBody);
                    }
                }
                this.messageBody = line;
            } else {
                if (this.messageBody != null) {
                    this.messageBody += line;
                }
            }
        }
    }
}
