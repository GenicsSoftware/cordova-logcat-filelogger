package de.mj.cordova.plugin.filelogger;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LogCatPlugin extends CordovaPlugin {

    public final static String TAG = "LogCatPlugin";

    private final static String LOG_FOLDER_NAME = TAG;

    private CordovaInterface cordovaInstance = null;
    private CordovaWebView webView = null;

    private Thread loggerThread = null;

    public enum ACTION {
        START_LOGGER("startLogger");
        private final String action;
        ACTION(String action) {
            this.action = action;
        }
        @Override
        public String toString() {
            return action;
        }
    }

    @Override
    public void initialize(final CordovaInterface cordova, final CordovaWebView webView) {
        this.webView = super.webView;
        this.cordovaInstance = super.cordova;
        Log.v(TAG, "Init");
    }

    @Override
    public void onDestroy() {
        if (this.loggerThread != null) {
            this.loggerThread.interrupt();
            this.loggerThread = null;
        }

        super.onDestroy();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, action);
        if (action.equals(ACTION.START_LOGGER.toString())) {
            this.startLogging();
            return true;
        } else {
            return false;
        }
    }

    private void startLogging() {
        final LogCatFileWriter eventHandler = new LogCatFileWriter();
        final BashExecutor bashExecuter = new BashExecutor(eventHandler);
        bashExecuter.setCommand("logcat");
        this.loggerThread = new Thread(bashExecuter);
        this.loggerThread.start();
    }

}
