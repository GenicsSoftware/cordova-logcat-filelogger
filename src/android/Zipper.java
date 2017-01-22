package de.mj.cordova.plugin.filelogger;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class Zipper {

    static File zipLog(final File[] filesToZip, final File targetZipFile) {
        FileOutputStream fileOs = null;
        ZipOutputStream zipOs = null;
        FileInputStream fileIs = null;

        try {
            fileOs = new FileOutputStream(targetZipFile);
            zipOs = new ZipOutputStream(fileOs);
            for (final File fileToZip: filesToZip) {
                final ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOs.putNextEntry(zipEntry);
                fileIs = new FileInputStream(fileToZip);
                int byteLength;
                byte[] buf = new byte[1024];
                while ((byteLength = fileIs.read(buf)) > 0) {
                    zipOs.write(buf, 0, byteLength);
                }
                zipOs.closeEntry();
            }
            return targetZipFile;
        } catch (IOException e) {
            Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
        } finally {
            if (fileIs != null) {
                try {
                    fileIs.close();
                } catch (IOException e) {
                    Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
                }
            }
            if (zipOs != null) {
                try {
                    zipOs.closeEntry();
                } catch (IOException e) {
                    Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
                }
                try {
                    zipOs.close();
                } catch (IOException e) {
                    Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
                }
            }
            if (fileOs != null) {
                try {
                    fileOs.close();
                } catch (IOException e) {
                    Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
                }
            }
        }
        return null;
    }
}
