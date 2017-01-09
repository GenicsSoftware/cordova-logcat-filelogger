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

    private final static String TAG = "LogCatPlugin";

    private final static String LOG_FOLDER_NAME = TAG;

    private CordovaInterface cordovaInstance = null;
    private CordovaWebView webView = null;

    public enum ACTION {
        START_LOGGER("startLogger");
        private final String action;
        ACTION(String action) {
            this.action = action;
        }
        @Override
        public String toString() {
            Log.v(TAG, action);
            return action;
        }
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        this.webView = super.webView;
        this.cordovaInstance = super.cordova;
        Log.v(TAG, "Init");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, action);
        if (action.equals(ACTION.START_LOGGER.toString())) {
            Log.v(TAG, ACTION.START_LOGGER.toString());

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
