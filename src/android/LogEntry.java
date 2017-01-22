package de.mj.cordova.plugin.filelogger;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

class LogEntry {

    private Date timestamp;
    private int processId;
    private int threadId;
    private LOG_LEVEL logLevel;
    private String processName;
    private String body;

    private static final String HEADER_START = "[ ";
    private static final String HEADER_END = " ]";

    private static final SimpleDateFormat HEADER_DATE_OUTPUT_FORMAT =
            new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
    private static final SimpleDateFormat HEADER_DATE_INPUT_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS", Locale.ENGLISH);
    private static final SimpleDateFormat ISO_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);

    String getBody() {
        return body;
    }

    String getProcessName() {
        return processName;
    }

    LOG_LEVEL getLogLevel() {
        return logLevel;
    }

    int getThreadId() {
        return threadId;
    }

    int getProcessId() {
        return processId;
    }

    Date getTimestamp() {
        return timestamp;
    }

    String getISOTimestamp() {
        if (timestamp != null) {
            return ISO_DATE_FORMAT.format(timestamp);
        }
        return null;
    }

    private enum LOG_LEVEL {
        VERBOSE("V"),
        DEBUG("D"),
        INFO("I"),
        WARNING("W"),
        ERROR("E"),
        FATAL("F");

        private final String level;

        LOG_LEVEL(final String level) {
            this.level = level;
        }

        @Override
        public String toString() {
            return level;
        }
    }

    LogEntry(final String header) {
        this(header, null);
    }

    LogEntry(final String header, final String body) {
        this.extractHeaderInfo(header);
        this.normalizeContent(body);
    }

    private static String replaceNthOccurrence(final String replaceIn, final String toReplace,
                                               final String replaceWith, final int n) {
        int index = replaceIn.indexOf(toReplace);
        if (index == -1) {
            return replaceIn;
        }
        for (int x = 1; x < n; x++) {
            index = replaceIn.indexOf(toReplace, index + 1);
            if (index == -1) {
                return replaceIn;
            }
        }
        return replaceIn.substring(0, index) + replaceWith +
                replaceIn.substring(index + toReplace.length(), replaceIn.length());
    }

    private void extractHeaderInfo(String header) {
        if (header == null) {
            return;
        }

        header = header.trim();
        header = replaceNthOccurrence(header, ":", " ", 3).trim();

        final String[] attributes = header.split("\\s+");
        final String date = Calendar.getInstance().get(Calendar.YEAR) + "-" + attributes[1] + " " + attributes[2];

        try {
            this.timestamp = HEADER_DATE_INPUT_FORMAT.parse(date);
        } catch (ParseException e) {
            Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
        }

        try {
            this.processId = Integer.parseInt(attributes[3].replace(":", ""));
            this.threadId = Integer.parseInt(attributes[4]);
        } catch (Exception e) {
            Log.e(LogCatPlugin.TAG, "Header: " + header);
            Log.e(LogCatPlugin.TAG, Log.getStackTraceString(e));
        }


        final String[] levelAndName = attributes[5].split("/");

        if (levelAndName.length > 0) {
            final String levelString = levelAndName[0];
            for (final LOG_LEVEL level : LOG_LEVEL.values()) {
                if (levelString.equals(level.toString())) {
                    this.logLevel = level;
                    break;
                }
            }
        }

        if (levelAndName.length > 1) {
            this.processName = levelAndName[1];
        }

    }

    private void normalizeContent(final String body) {
        // TODO: It would be nice to compress the body content somehow
        this.body = body;
    }


    static boolean isHeader(String line) {
        if (line == null) {
            return false;
        }
        line = replaceNthOccurrence(line, ":", " ", 3).trim();
        boolean hasHeaderFormat = line.startsWith(HEADER_START) && line.endsWith(HEADER_END);
        boolean hasLogLevel = false;
        boolean hasAllAttributes = line.split("\\s+").length == 7;
        for (final LOG_LEVEL level : LOG_LEVEL.values()) {
            if (line.contains(" " + level.toString() + "/")) {
                hasLogLevel = true;
                break;
            }
        }

        return (hasHeaderFormat && hasLogLevel && hasAllAttributes);
    }

    @Override
    public String toString() {
        return HEADER_START + HEADER_DATE_OUTPUT_FORMAT.format(this.timestamp) +
                " " + this.processId + ": " + this.threadId + " " +
                this.logLevel.toString() + "/" + this.processName +
                HEADER_END + "\n" + this.body;
    }

}
