package de.mj.cordova.plugin.filelogger;

import android.content.Context;
import android.util.Log;

import java.io.*;

class FileTools {
    static File rollFile(final File file, final String suffix) {
        return rollFile(file, suffix, null);
    }

    static File rollFile(final File file, final String suffix, final String extension) {
        try {
            final String fileName = file.getName();
            final String[] fileNameParts = fileName.split("\\.");
            final String baseName = fileNameParts[fileNameParts.length - 2];
            final String ext = extension == null ? fileNameParts[fileNameParts.length - 1] : extension;
            return new File(file.getParent() + File.separator + baseName + (suffix == null ? "" : suffix) + "." + ext);
        } catch (Exception e) {
            Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    static boolean copyFile(final File file1, final File file2) {
        return copyFile(file1, file2, false);
    }

    private static boolean copyFile(final File file1, final File file2, final boolean append) {
        InputStream inStream = null;
        OutputStream outStream = null;
        try {

            inStream = new FileInputStream(file1);
            outStream = new FileOutputStream(file2, append);

            int byteLength;
            byte[] buf = new byte[1024];
            while ((byteLength = inStream.read(buf)) > 0) {
                outStream.write(buf, 0, byteLength);
            }
            return true;
        } catch (IOException e) {
            Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
                }
            }
        }
        return false;
    }

    private static File mergeLogFiles(final File logFile, final File bakLogFile, final File targetFile) {
        copyFile(bakLogFile, targetFile, false);
        copyFile(logFile, targetFile, true);
        return targetFile;
    }

    static void append(final String data, final File file, final Context context) {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(context.openFileOutput(file.getName(), Context.MODE_APPEND));
            writer.write(data);
        } catch (IOException e) {
            Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

    static File prepareDownload(File logFile, File logBak, File logCon) {
        boolean hasLog = logFile != null && logFile.exists();
        boolean hasRollingLog = logBak != null && logBak.exists();

        if (logCon != null && logCon.exists()) {
            logCon.delete();
        }

        File fileToDownload = null;
        if (hasLog && hasRollingLog) {
            FileTools.mergeLogFiles(logFile, logBak, logCon);
            fileToDownload = logCon;
        } else if (hasLog) {
            fileToDownload = logFile;
        } else if (hasRollingLog) {
            fileToDownload = logBak;
        }

        return fileToDownload;
    }
}
