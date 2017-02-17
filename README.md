### Plugin: cordova-logcat-filelogger

This cordova plugin writes Logcat and JavaScript messages to a file for later use. It supports log rotation and file compression for easier transfer.

![](https://github.com/kolbasa/cordova-logcat-filelogger/blob/images/logcat-inspect.gif)

### Installing the plugin

```
cordova plugin add https://github.com/kolbasa/cordova-logcat-filelogger
```

### Configuring the plugin

```javascript
document.addEventListener('deviceready', function () {
    var logCatPlugin = window.cordova.plugins.LogCatPlugin;
    logCatPlugin.init(
        {
            success: function() {
                // initialized callback
            },
            failure: function() {
                // reject callback
            },
            jsLogFileName: 'jsLog.txt',
            logCatFileName: 'catLog.txt',
            maxFileSizeInKB: 1024,
            // Strings that should be included
            filterBy: [
                ' W/', // Like log levels
                ' E/',
                ' F/',
                ' Fatal'
            ],
            // Strings you want to filter out, like emulation spam
            filterOut: [
                'I/InstantRun',      // Emulation
                'EGL_emulation',     // Emulation
                ':CONSOLE',          // Console-Logs
                'I/System.out',      // Java output
                '/LogCatPlugin',
                'D/SystemWebChromeClient',
                'javascript:cordova.plugins.LogCatPlugin'
            ],
            logCallBack: function (sHeader, sBody, sLevel) {
                // Log callback for your javascript application
                console.log(sLevel, sHeader, sBody);
            }
        }
    );
}, false);
```

### Usage

Start the file logger:
```javascript
var logCatPlugin = window.cordova.plugins.LogCatPlugin;
logCatPlugin.startLogger(
    {
        success: function() {
            // initialized callback
        },
        failure: function() {
            // reject callback
        }
    }
);
```

Get the log files:
```javascript
var logCatPlugin = window.cordova.plugins.LogCatPlugin;
logCatPlugin.getLcLogPath(
    {
        success: function(sLogPath) {
            // Process the logfiles
            // e.g. /data/data/com.ionicframework.filelogger/files/catLog.txt
        },
        failure: function() {
            // reject callback
        }
    }
);
```

### Methods

| Method Name | Arguments | Notes
|---|---|---|
| [`startLogger`]() | `{`<br>`success:fn,`<br>`failure:fn`<br>`}` | Starts to write all Logcat data to your specified file. Possible reject codes: `'LOGGING_STOPPED'`, `'PROCESS_KILLED'`, `'LOGCAT_COMMAND_RETURNED_ERROR'`|
| [`stopLogger`]() | `{ success:fn }` | Stops the Logger. |
| [`deleteLog`]() | `{ success:fn, failure:fn }` | Deletes all files including the JavaScript log. It is not necessary to stop the Logger first. Possible reject codes: `'COULD_NOT_DELETE_FILE'`. |
| [`jsLog`]() | `jsLog`, `{ success:fn, failure:fn }` | Write your JavaScript log into a separated log file. |
| [`getLcLogPath`]() | `{ success:fn, failure:fn }` | Returns the Logcat logfile path. |
| [`getJsLogPath`]() | `{ success:fn, failure:fn }` | Returns the JavaScript logfile path. |
| [`zipAll`]() | `{ success:fn, failure:fn }` | Compresses all logfiles to one zip-archive and returns the file path. |
| [`throwExampleError`]() | `{ success:fn }` | Throw a NullPointerException for debugging purposes. |
| [`throwExampleFatalError`]() | `none` | Throw a fatal error to crash your application. |
| [`getLastLcEntries`]() | `{ success:fn, failure:fn, filterBy:arr, filterOut:arr, maxEntries:int }` | Returns a string that contains Logcat entries with size `maxEntries`. You can filter them with `filterBy` and `filterOut`. |
| [`getLastJsEntries`]() | `{ success:fn, failure:fn, filterBy:arr, filterOut:arr, maxEntries:int }` | Returns a string that contains JavaScript entries with size `maxEntries`. You can filter them with `filterBy` and `filterOut`. |
| [`clearLcBuffer`]() | `{ success:fn, failure:fn }` | Clears the Logcat Buffer with the command: `logcat -c`. |
| [`showInFileManager`]() | `{ success:fn, failure:fn }` | Copies all log files to the external storage and opens the directory in the systems file browser. |

### Step by step instructions for Ionic newcomers

Create an empty ionic project if needed:
```
ionic start filelogger blank
```

Navigate to your project:
```
cd filelogger
```

Install the plugin:
```
ionic plugin add https://github.com/kolbasa/cordova-logcat-filelogger
```

Copy ```LoggerService.js``` to ```www/js```
```
cp plugins/cordova-plugin-logcat/ionic/LoggerService.js www/js/LoggerService.js
```

Add ```<script src="js/LoggerService.js"></script>``` to your ```www/index.html```.
It should look like this:
```html
<!-- [...] -->
<script src="js/app.js"></script>
<script src="js/LoggerService.js"></script>
<!-- [...] -->
```

Initialize the plugin in your ```www/js/app.js``` by appending this ```run``` block:
```javascript
.run(
    /**
     * @param $log {$log}
     * @param LoggerService {LoggerService}
     */
    function ($log, LoggerService) {
        document.addEventListener('deviceready', function () {
            LoggerService.init()
                .catch(function (err) {
                    $log.error("Something went wrong", err);
                })
        }, false);
    }
)
```


Create the android build:
```
ionic platform add android
ionic platform build android
```

Run your build:
```
ionic run android
```

Open the inspect window in Chrome (type in ```chrome://inspect/#devices```) and select your device/emulator.

Select ```console``` in the Toolbar and type in the console:

```javascript
LogCatPlugin.startLogger()
```

You should see the stream of Logcat data coming in.
