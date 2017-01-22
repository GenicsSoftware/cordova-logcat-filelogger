package de.mj.cordova.plugin.filelogger;

import android.content.Context;
import android.util.Log;

import org.apache.cordova.CordovaInterface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class JsFileWriter {

    private File logFile;
    private File logBak;
    private Context context;
    private CordovaInterface cordovaInstance = null;
    private int maxFileSizeInKB;
    private QueueMonitor<String> queue;
    private static final SimpleDateFormat HEADER_DATE_INPUT_FORMAT =
            new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.ENGLISH);


    JsFileWriter(final File logFile, final File logBak,
                 final CordovaInterface cordovaInstance, int maxFileSizeInKB) {
        this.logFile = logFile;
        this.logBak = logBak;
        this.cordovaInstance = cordovaInstance;
        this.context = this.cordovaInstance.getActivity().getApplicationContext();
        this.maxFileSizeInKB = maxFileSizeInKB;
        this.queue = new QueueMonitor<String>(10);
        this.init();
    }

    void log(final String logMessage) {
        if (logMessage != null) {
            try {
                this.queue.put(logMessage);
            } catch (InterruptedException e) {
                Log.e(LogCatPlugin.TAG, Log.getStackTraceString(e));
            }
        }
    }

    private void init() {
        this.cordovaInstance.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Log.v(LogCatPlugin.TAG, "Starting to log js: " + logFile.getAbsolutePath());
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        handleLine(queue.get());
                    }
                } catch (InterruptedException e) {
                    Log.e(LogCatPlugin.TAG, Log.getStackTraceString(e));
                }
            }
        });
    }

    private void handleLine(final String logMessage) {
        if (logMessage == null || logMessage.trim().isEmpty()) {
            return;
        }

        if (this.logFile != null) {
            float fileSizeInKB = (float) this.logFile.length() / 1024;
            if (fileSizeInKB > (float) maxFileSizeInKB) {
                if (this.logBak != null) {
                    if (this.logBak.exists()) {
                        this.logBak.delete();
                    }
                    this.logFile.renameTo(this.logBak);
                }
            }
            FileTools.append("[ " + HEADER_DATE_INPUT_FORMAT.format(new Date()) +
                    " 0:0 V/Chromium ] \n" + logMessage + "\n\n", this.logFile, this.context);
        }

    }
}
