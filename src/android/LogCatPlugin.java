package de.mj.cordova.plugin.filelogger;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LogCatPlugin extends CordovaPlugin {

    public final static String TAG = "LogCatPlugin";
    private final static String LOG_FOLDER_NAME = TAG;
    private final static String DEFAULT_LOG_FILENAME = TAG + "Log.txt";

    private CordovaInterface cordovaInstance = null;
    private CordovaWebView webView = null;
    private Thread loggerThread = null;
    private List<String> logInclusions;
    private List<String> logExclusions;

    public enum ACTION {

        START_LOGGER("startLogger"),
        SET_INCLUSION("setInclusion"),
        SET_EXCLUSION("setExclusion");

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
        this.logInclusions = new ArrayList<String>();
        this.logExclusions = new ArrayList<String>();
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
        if (action.equals(ACTION.SET_INCLUSION.toString())) {
            logInclusions.add(args.optString(0));
        } else if (action.equals(ACTION.SET_INCLUSION.toString())) {
            logExclusions.add(args.optString(0));
        } else if (action.equals(ACTION.START_LOGGER.toString())) {
            Log.v("aaaa", "72 - " + args.length());
            Log.v("aaa", "73 - " + args.optString(0));
            // this.startLogging();
        } else {
            return false;
        }
        return true;
    }

    private void startLogging() {
        final FileWriter fileWriter = new FileWriter("test.txt", TAG, cordova.getActivity().getApplicationContext());
        final LogCatFileWriter eventHandler = new LogCatFileWriter(fileWriter);

        final BashExecutor bashExecuter = new BashExecutor(eventHandler);
        bashExecuter.setCommand("logcat");
        this.loggerThread = new Thread(bashExecuter);
        this.loggerThread.start();
    }

}
