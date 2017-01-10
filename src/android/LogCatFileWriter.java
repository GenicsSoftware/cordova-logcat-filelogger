package de.mj.cordova.plugin.filelogger;

import android.util.Log;

class LogCatFileWriter extends BashExecutorEventHandler {

    private FileWriter fileWriter;

    LogCatFileWriter(final FileWriter writer) {
        this.fileWriter = writer;
    }

    @Override
    public void standardOutput(final String line) {
        if (line.contains(" E ")) {
            this.fileWriter.append(line + "\n");
        }
    }

    @Override
    public void errorOutput(String line) {
        this.fileWriter.append(line + "\n");
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
