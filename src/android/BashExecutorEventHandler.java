package de.mj.cordova.plugin.filelogger;

public abstract class BashExecutorEventHandler {
    public abstract void standardOutput(String line);
    public abstract void errorOutput(String line);
    public abstract void executionStarted();
    public abstract void executionStopped();
    public abstract void processKilled();
}

