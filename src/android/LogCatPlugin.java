package de.mj.cordova.filelogger;

import android.os.Environment;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

public class LogCatPlugin extends CordovaPlugin {

    private final static String TAG = "LogCatPlugin";

    private CordovaInterface cordovaInstance = null;
    private CordovaWebView webView = null;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        this.webView = super.webView;
        this.cordovaInstance = super.cordova;
        Log.v(TAG, "init");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("test")) {
            Log.v(TAG, "test");
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
