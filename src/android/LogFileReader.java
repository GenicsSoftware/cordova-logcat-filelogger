package de.mj.cordova.plugin.filelogger;

import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import de.mj.cordova.plugin.filelogger.org.apache.commons.io.input.ReversedLinesFileReader;

class LogFileReader {
    private File logFile;
    private File logBak;
    private File logCon;
    private int entryCountToOutput;
    private String[] filterByList;
    private String[] filterOutList;

    LogFileReader(final File logFile, final File logBak, final File logCon,
                  final int entryCountToOutput, final String[] filterBy,
                  final String[] filterOut) {
        this.logFile = logFile;
        this.logBak = logBak;
        this.logCon = logCon;
        this.entryCountToOutput = entryCountToOutput;
        this.filterByList = filterBy;
        this.filterOutList = filterOut;
    }

    private boolean shouldOutput(String message) {
        if (this.filterByList == null && this.filterOutList == null) {
            return true;
        }

        message = message.replaceAll("\\s+", " ");

        boolean accept = (filterByList == null || filterByList.length == 0 ||
                (filterByList.length == 1 && filterByList[0].trim().isEmpty()));
        if (this.filterByList != null) {
            for (final String filterBy : this.filterByList) {
                if (!filterBy.trim().isEmpty() && message.contains(filterBy)) {
                    accept = true;
                    break;
                }
            }
        }

        if (this.filterOutList != null) {
            for (final String filterOut : this.filterOutList) {
                if (!filterOut.trim().isEmpty() && message.contains(filterOut)) {
                    accept = false;
                    break;
                }
            }
        }

        return accept;
    }

    private String errorEntry = "";

    List<LogEntry> getLatestEntries() throws IOException {
        ReversedLinesFileReader reverseReader = null;
        try {
            int count = 0;
            final ArrayList<LogEntry> logEntries = new ArrayList<LogEntry>();
            final File fileToRead = FileTools.prepareDownload(logFile, logBak, logCon);
            if (fileToRead == null) {
                return null;
            }

            reverseReader = new ReversedLinesFileReader(fileToRead, Charset.forName("UTF-8"));
            String line;

            do {
                line = reverseReader.readLine();
                if (line != null) {
                    if (LogEntry.isHeader(line)) {
                        if (this.shouldOutput(line + errorEntry)) {
                            logEntries.add(new LogEntry(line, errorEntry));
                            count++;
                        }
                        errorEntry = "";
                        continue;
                    }
                    errorEntry = line + (errorEntry.isEmpty() ? "" : "\n") + errorEntry;
                }
            }
            while (line != null && count < entryCountToOutput && !Thread.currentThread().isInterrupted());

            return logEntries;
        } finally {
            if (reverseReader != null) {
                try {
                    reverseReader.close();
                } catch (IOException e) {
                    Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

}
