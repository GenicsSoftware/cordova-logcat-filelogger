package de.mj.cordova.plugin.filelogger;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

class BashExecutor implements Runnable {
    private String command;
    private boolean commandStillRunning;
    private Process process = null;
    private BashExecutorEventHandler eventHandler;

    BashExecutor() {
        //
    }

    BashExecutor(BashExecutorEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public void run() {
        if (command == null) {
            throw new NullPointerException("NO COMMAND GIVEN");
        }
        commandStillRunning = true;
        try {
            if (eventHandler != null) {
                eventHandler.executionStarted();
            }

            process = Runtime.getRuntime().exec(command);

            getProcessOutput();
        } catch (Exception e) {
            Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
        } finally {
            commandStillRunning = false;
            if (eventHandler != null) {
                eventHandler.executionStopped();
            }
        }
    }

    private void getProcessOutput() {
        try {
            if (process != null) {
                Thread thread1 = new Thread(new ProcessStandardOutput());
                Thread thread2 = new Thread(new ProcessErrorOutput());
                thread1.start();
                thread2.start();
                thread1.join();
                thread2.join();
            }
        } catch (Exception e) {
            Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
        }
    }

    private class ProcessStandardOutput extends Thread {
        public void run() {
            try {
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                String currentLine;
                while ((currentLine = stdInput.readLine()) != null && process != null) {
                    if (eventHandler != null) {
                        eventHandler.standardOutput(currentLine);
                    }
                }
            } catch (Exception e) {
                Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
            }
        }
    }

    private class ProcessErrorOutput extends Thread {
        public void run() {
            try {
                BufferedReader stdError = new BufferedReader(new InputStreamReader(
                        process.getErrorStream()));

                String currentLine;
                while ((currentLine = stdError.readLine()) != null) {
                    if (eventHandler != null) {
                        eventHandler.errorOutput(currentLine);
                    }
                }
            } catch (Exception e) {
                Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
            }
        }
    }

    boolean killProcess() {
        try {
            if (process != null) {
                if (eventHandler != null) {
                    eventHandler.processKilled();
                }
                process.destroy();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
        }
        return false;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isCommandStillRunning() {
        return commandStillRunning;
    }

    public void setCommandStillRunning(boolean commandStillRunning) {
        this.commandStillRunning = commandStillRunning;
    }

}
