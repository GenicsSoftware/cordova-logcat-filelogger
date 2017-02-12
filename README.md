### Plugin: cordova-logcat-filelogger

This cordova plugin writes Logcat and JavaScript messages to a file for later use. It supports log rotation and file compression for easier transfer.

![](https://github.com/kolbasa/cordova-logcat-filelogger/blob/images/logcat-inspect2.gif)

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

### All Functions

TODO:


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
