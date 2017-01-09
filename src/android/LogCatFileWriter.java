package de.mj.cordova.plugin.filelogger;

import android.util.Log;

public class LogCatFileWriter extends BashExecutorEventHandler {

    @Override
    public void standardOutput(String line) {
        Log.e("aaa", "7 - " + line);
    }

    @Override
    public void errorOutput(String line) {

    }

    @Override
    public void executionStarted() {

    }

    @Override
    public void executionStopped() {

    }

    @Override
    public void processKilled() {

    }
}
