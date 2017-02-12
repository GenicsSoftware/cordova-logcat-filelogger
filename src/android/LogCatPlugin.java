package de.mj.cordova.plugin.filelogger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogCatPlugin extends CordovaPlugin {

    public final static String TAG = "LogCatPlugin";

    private final static String EXTERNAL_STORAGE_FOLDER = TAG;
    private final static String DEFAULT_LC_FILENAME = TAG + "Log.txt";
    private final static String DEFAULT_JS_FILENAME = TAG + "JsLog.txt";
    private final static String DEFAULT_ZIP_FILENAME = TAG + ".zip";
    private final static String ARRAY_SEPARATOR = "--entry--";
    private final static String LOGCAT_COMMAND = "logcat -v long";
    private final static String LOGCAT_CLEAR_BUFFER_COMMAND = "logcat -c";
    private final static String LOG_ROLLING_EXTENSION = "bak";
    private final static String LOG_CON_SUFFIX = "_con";

    private final static int DEFAULT_MAX_FILESIZE_IN_KB = 1024;
    private final static int DEFAULT_MAX_ENTRIES_TO_OUTPUT = 10;
    private final static int PERMISSION_REQUEST_CODE = 1;

    private CordovaInterface cordovaInstance = null;
    private CordovaWebView webView = null;
    private Context context = null;

    private int maxFileSizeInKB;
    private boolean enableCallback;

    private BashExecutor bashExecuter;
    private JsFileWriter jsFileWriter = null;
    private Thread loggerThread = null;

    private File jsFile;
    private File jsBak;
    private File jsCon;

    private File lcFile;
    private File lcBak;
    private File lcCon;

    private File zipFile;

    private File externalStorage;
    private File internalStorage;

    private String[] filterBy;
    private String[] filterOut;


    public enum ACTION {
        INIT_LOGGER
                ("init"),
        START_LOGGER
                ("startLogger"),
        STOP_LOGGER
                ("stopLogger"),
        JS_LOG
                ("jsLog"),
        DELETE_LOG
                ("deleteLog"),
        GET_JS_LOG_PATH
                ("getJsLogPath"),
        GET_LC_LOG_PATH
                ("getLcLogPath"),
        GET_LAST_LC_ENTRIES
                ("getLastLcEntries"),
        GET_LAST_JS_ENTRIES
                ("getLastJsEntries"),
        ZIP_ALL
                ("zipAll"),
        SHOW_IN_FILE_MANAGER
                ("showInFileManager"),
        CLEAR_LC_BUFFER
                ("clearLcBuffer"),
        THROW_EXAMPLE_ERROR
                ("throwExampleError"),
        THROW_EXAMPLE_FATAL_ERROR
                ("throwExampleFatalError");

        private final String action;

        ACTION(final String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }

    private enum RETURN_CODE {
        NO_LOG_FILES_FOUND,
        CANT_CREATE_ZIP,
        CANT_OPEN_LOG,
        PLUGIN_NOT_INITIALIZED,
        NO_ENTRIES_FOUND,
        NO_EXTERNAL_STORAGE_PERMISSIONS,
        COULD_NOT_DELETE_FILE,
        NO_FILE_MANAGER_FOUND
    }

    @Override
    public void initialize(final CordovaInterface cordova, final CordovaWebView webView) {
        Log.v(TAG, "Init");
        this.webView = super.webView;
        this.cordovaInstance = super.cordova;
        this.context = cordovaInstance.getActivity().getApplicationContext();
        this.internalStorage = this.context.getFilesDir();
        this.externalStorage = new File(Environment.getExternalStorageDirectory() +
                File.separator + EXTERNAL_STORAGE_FOLDER);
    }

    @Override
    public boolean execute(final String action, final JSONArray args,
                           final CallbackContext callbackContext) throws JSONException {

        if (action.equals(ACTION.INIT_LOGGER.toString())) {
            this.initLogger(args, callbackContext);
        } else if (action.equals(ACTION.START_LOGGER.toString())) {
            this.startLogging(callbackContext);
        } else if (action.equals(ACTION.SHOW_IN_FILE_MANAGER.toString())) {
            this.showInFileManager(callbackContext);
        } else if (action.equals(ACTION.DELETE_LOG.toString())) {
            this.deleteLogFiles(callbackContext);
        } else if (action.equals(ACTION.THROW_EXAMPLE_ERROR.toString())) {
            this.throwExampleError(callbackContext);
        } else if (action.equals(ACTION.THROW_EXAMPLE_FATAL_ERROR.toString())) {
            this.throwExampleFatalError();
        } else if (action.equals(ACTION.STOP_LOGGER.toString())) {
            this.stopLogging(callbackContext);
        } else if (action.equals(ACTION.ZIP_ALL.toString())) {
            this.zipAll(callbackContext);
        } else if (action.equals(ACTION.CLEAR_LC_BUFFER.toString())) {
            this.clearLogCatBuffer(callbackContext);
        } else if (action.equals(ACTION.GET_LC_LOG_PATH.toString())) {
            this.getJcLogPath(callbackContext);
        } else if (action.equals(ACTION.GET_JS_LOG_PATH.toString())) {
            this.getJsLogPath(callbackContext);
        } else if (action.equals(ACTION.JS_LOG.toString())) {
            this.writeToJsLog(args);
        } else if (action.equals(ACTION.GET_LAST_LC_ENTRIES.toString())) {
            this.getLastLcEntries(args, callbackContext);
        } else if (action.equals(ACTION.GET_LAST_JS_ENTRIES.toString())) {
            this.getLastJsEntries(args, callbackContext);
        } else {
            return false;
        }

        return true;
    }

    private void getJsLogPath(final CallbackContext callbackContext) {
        final File fileToCopy = FileTools.prepareDownload(lcFile, lcBak, lcCon);
        if (fileToCopy != null && fileToCopy.exists()) {
            callbackContext.success(fileToCopy.getAbsolutePath());
        } else {
            callbackContext.error(RETURN_CODE.NO_LOG_FILES_FOUND.name());
        }
    }

    private void getJcLogPath(final CallbackContext callbackContext) {
        final File fileToCopy = FileTools.prepareDownload(jsFile, jsBak, jsCon);
        if (fileToCopy != null && fileToCopy.exists()) {
            callbackContext.success(fileToCopy.getAbsolutePath());
        } else {
            callbackContext.error(RETURN_CODE.NO_LOG_FILES_FOUND.name());
        }
    }

    private void getLastLcEntries(final JSONArray args, final CallbackContext callbackContext) {
        getLogEntries(lcFile, lcBak, lcCon, args, callbackContext);
    }

    private void getLastJsEntries(final JSONArray args, final CallbackContext callbackContext) {
        getLogEntries(jsFile, jsBak, jsCon, args, callbackContext);
    }

    private void getLogEntries(final File log, final File logBak, final File logCat,
                               final JSONArray args, final CallbackContext callbackContext) {
        cordovaInstance.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                final String[] filterBy = args.optString(0) == null ?
                        null : args.optString(0).split(ARRAY_SEPARATOR);
                final String[] filterOut = args.optString(1) == null ?
                        null : args.optString(1).split(ARRAY_SEPARATOR);
                int maxEntries = args.optString(2) != null ?
                        args.optInt(2) : DEFAULT_MAX_ENTRIES_TO_OUTPUT;
                final LogFileReader reader = new LogFileReader(log, logBak,
                        logCat, maxEntries, filterBy, filterOut);

                final List<LogEntry> entries;
                try {
                    entries = reader.getLatestEntries();
                    if (entries == null || entries.isEmpty()) {
                        callbackContext.error(RETURN_CODE.NO_ENTRIES_FOUND.name());
                    } else {
                        String output = "";
                        for (final LogEntry entry : entries) {
                            output += entry.toString() + "\n\n";
                        }
                        callbackContext.success(output);
                    }
                } catch (IOException e) {
                    callbackContext.error(RETURN_CODE.CANT_OPEN_LOG.name());
                    Log.v(LogCatPlugin.TAG, Log.getStackTraceString(e));
                }
            }
        });
    }

    private void clearLogCatBuffer(final CallbackContext callbackContext) {
        final BashExecutor executor = new BashExecutor();
        executor.setCommand(LOGCAT_CLEAR_BUFFER_COMMAND);
        cordovaInstance.getThreadPool().execute(executor);
        callbackContext.success();
    }

    private void throwExampleError(final CallbackContext callbackContext) {
        cordovaInstance.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    Log.v(LogCatPlugin.TAG,
                            "Should appear after the NullPointerException");
                    callbackContext.success();
                } catch (InterruptedException e) {
                    //
                }
            }
        });
        ((String) null).length();
    }

    private void throwExampleFatalError() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, null, Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    private void deleteLogFiles(final CallbackContext callbackContext) {
        try {
            if (this.lcFile.exists()) {
                this.lcFile.delete();
            }

            if (this.lcBak.exists()) {
                this.lcBak.delete();
            }

            if (this.lcCon.exists()) {
                this.lcCon.delete();
            }

            if (this.jsFile.exists()) {
                this.jsFile.delete();
            }

            if (this.jsBak.exists()) {
                this.jsBak.delete();
            }

            if (this.jsCon.exists()) {
                this.jsCon.delete();
            }

            callbackContext.success();
        } catch (Exception e) {
            callbackContext.error(RETURN_CODE.COULD_NOT_DELETE_FILE.name());
        }
    }

    private void zipAll(final CallbackContext callbackContext) {
        cordovaInstance.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (zipFile == null) {
                    callbackContext.error(RETURN_CODE.PLUGIN_NOT_INITIALIZED.name());
                }

                final File logCatToZip = FileTools.prepareDownload(lcFile, lcBak, lcCon);
                final File jsFileToZip = FileTools.prepareDownload(jsFile, jsBak, jsCon);

                if (zipFile != null && zipFile.exists()) {
                    zipFile.delete();
                }

                final ArrayList<File> filesToZip = new ArrayList<File>();

                if (logCatToZip != null && logCatToZip.exists()) {
                    filesToZip.add(logCatToZip);
                }

                if (jsFileToZip != null && jsFileToZip.exists()) {
                    filesToZip.add(jsFileToZip);
                }

                if (!filesToZip.isEmpty()) {
                    final File[] fileList = filesToZip.toArray(new File[filesToZip.size()]);
                    final Runnable zipThread = new Runnable() {
                        @Override
                        public void run() {
                            final File zippedLog = Zipper.zipLog(fileList, zipFile);
                            if (zippedLog != null) {
                                callbackContext.success(zippedLog.getAbsolutePath());
                            } else {
                                callbackContext.error(RETURN_CODE.CANT_CREATE_ZIP.name());
                            }
                        }
                    };
                    cordovaInstance.getThreadPool().execute(zipThread);
                } else {
                    callbackContext.error(RETURN_CODE.NO_LOG_FILES_FOUND.name());
                }
            }
        });
    }

    private void showInFileManager(final CallbackContext callbackContext) {
        if (this.checkStoragePermission()) {
            final Runnable showFileManager = new Runnable() {
                @Override
                public void run() {
                    prepareFilesToShow(callbackContext);
                    callbackContext.success();
                }
            };
            cordovaInstance.getThreadPool().execute(showFileManager);
        } else {
            callbackContext.error(RETURN_CODE.NO_EXTERNAL_STORAGE_PERMISSIONS.name());
        }
    }

    private void prepareFilesToShow(final CallbackContext callbackContext) {
        if (!this.externalStorage.exists()) {
            this.externalStorage.mkdir();
        }

        if (this.externalStorage.exists()) {
            File targetFile = null;
            File jsTargetFile = null;

            final File fileToCopy = FileTools.prepareDownload(lcFile, lcBak, lcCon);
            if (fileToCopy != null) {
                targetFile = new File(externalStorage,
                        fileToCopy.getName().replace(LOG_CON_SUFFIX, ""));
            }

            final File jsFileToCopy = FileTools.prepareDownload(jsFile, jsBak, jsCon);
            if (jsFileToCopy != null) {
                jsTargetFile = new File(externalStorage,
                        jsFileToCopy.getName().replace(LOG_CON_SUFFIX, ""));
            }

            if (targetFile != null && targetFile.exists()) {
                targetFile.delete();
            }

            if (jsTargetFile != null && jsTargetFile.exists()) {
                jsTargetFile.delete();
            }

            boolean copiedCatLog = targetFile != null &&
                    FileTools.copyFile(fileToCopy, targetFile);
            boolean copiedJsLog = jsTargetFile != null &&
                    FileTools.copyFile(jsFileToCopy, jsTargetFile);

            if (copiedCatLog || copiedJsLog) {
                final Uri dirUri = Uri.fromFile(this.externalStorage);
                this.openDirectory(dirUri, callbackContext);
            }
        }
    }

    private void openDirectory(final Uri dirUri, final CallbackContext callbackContext) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(dirUri, "resource/folder");

        final PackageManager packageManager = cordovaInstance.getActivity().getPackageManager();
        if (intent.resolveActivityInfo(packageManager, 0) != null) {
            cordovaInstance.getActivity().startActivity(intent);
            callbackContext.success();
        } else {
            callbackContext.error(RETURN_CODE.NO_FILE_MANAGER_FOUND.name());
        }
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(cordovaInstance.getActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                Log.v(TAG, "External Storage permission is revoked");
                final String STORAGE_PERMISSIONS = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                ActivityCompat.requestPermissions(cordovaInstance.getActivity(),
                        new String[]{STORAGE_PERMISSIONS}, PERMISSION_REQUEST_CODE);
                return false;
            }
        } else {
            return true;
        }
    }

    private void initLogger(final JSONArray args, final CallbackContext callbackContext) {
        try {
            final String jsFileName = args.optString(0) != null ?
                    args.optString(0) : DEFAULT_JS_FILENAME;
            final String lcFileName = args.optString(1) != null ?
                    args.optString(1) : DEFAULT_LC_FILENAME;
            this.maxFileSizeInKB = args.optString(2) != null ?
                    args.optInt(2) : DEFAULT_MAX_FILESIZE_IN_KB;
            this.filterBy = args.optString(3) == null ?
                    null : args.optString(3).split(ARRAY_SEPARATOR);
            this.filterOut = args.optString(4) == null ?
                    null : args.optString(4).split(ARRAY_SEPARATOR);
            this.enableCallback = args.optBoolean(5);

            if (jsFileName != null) {
                this.jsFile = new File(this.internalStorage, jsFileName);
                this.jsBak = FileTools.rollFile(this.jsFile, null, LOG_ROLLING_EXTENSION);
                this.jsCon = FileTools.rollFile(this.jsFile, LOG_CON_SUFFIX);

                if (this.jsFileWriter == null) {
                    this.jsFileWriter = new JsFileWriter(this.jsFile, this.jsBak,
                            this.cordovaInstance, this.maxFileSizeInKB);
                }
            }

            if (lcFileName != null) {
                this.lcFile = new File(this.internalStorage, lcFileName);
                this.lcBak = FileTools.rollFile(this.lcFile, null, LOG_ROLLING_EXTENSION);
                this.lcCon = FileTools.rollFile(this.lcFile, LOG_CON_SUFFIX);
            }

            this.zipFile = new File(this.internalStorage, DEFAULT_ZIP_FILENAME);
            callbackContext.success();
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void writeToJsLog(final JSONArray args) {
        if (this.jsFileWriter != null) {
            this.jsFileWriter.log(args.optString(0));
        }
    }

    private void startLogging(final CallbackContext callbackContext) {
        if (this.loggerThread == null && this.bashExecuter == null) {
            final LcFileWriter eventHandler = new LcFileWriter(
                    this.lcFile, this.lcBak, this.cordovaInstance,
                    this.webView, this.filterBy, this.filterOut,
                    this.maxFileSizeInKB, this.enableCallback,
                    callbackContext);
            this.bashExecuter = new BashExecutor(eventHandler);
            this.bashExecuter.setCommand(LOGCAT_COMMAND);
            this.loggerThread = new Thread(bashExecuter);
            this.loggerThread.start();
        }
    }

    private void stopLogging(final CallbackContext callbackContext) {
        if (this.bashExecuter != null) {
            this.bashExecuter.killProcess();
            this.bashExecuter = null;
        }

        if (this.loggerThread != null) {
            this.loggerThread.interrupt();
            this.loggerThread = null;
        }

        if (callbackContext != null) {
            callbackContext.success();
        }
    }

    @Override
    public void onDestroy() {
        this.stopLogging(null);
        Log.v(TAG, "Destroyed");
        super.onDestroy();
    }
}
