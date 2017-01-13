package de.mj.cordova.plugin.filelogger;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileWriter {

    private String loggerTag;
    private File targetFile;
    private Context context;

    public FileWriter(final String targetFileName, final String loggerTag, final Context context) {
        this.targetFile = new File(context.getFilesDir() + File.separator + targetFileName);
        Log.v(loggerTag, "Writing to: " + targetFile.getAbsolutePath());
        // targetFile.delete();
        this.loggerTag = loggerTag;
        this.context = context;
    }

    void append(final String data) {
        try {
            final OutputStreamWriter osw = new OutputStreamWriter(this.context.openFileOutput(targetFile.getName(), Context.MODE_APPEND));
            osw.write(data);
            osw.close();
        } catch (IOException e) {
            Log.e(loggerTag, "File write failed: " + e.toString());
        }
    }

}
