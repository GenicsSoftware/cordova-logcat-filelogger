### Plugin: cordova-logcat-filelogger

This cordova plugin writes Logcat and JavaScript messages to a file for later use. It supports log rotation and file compression for easier transfer.

### Configuration

TODO:

### Installation

TODO:

### Usage

TODO:

### Functions

TODO:


### Step by step instructions for Ionic newcomers

Create an empty ionic project if needed:
```Shell
ionic start filelogger blank
```

Navigate to your project:
```Shell
cd filelogger
```

Install the plugin:
```Shell
ionic plugin add https://github.com/kolbasa/cordova-logcat-filelogger
```

Copy ```LoggerService.js``` to ```www/js```
```Shell
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

Initialize the plugin in your ```www/js/app.js``` by appending this block:
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
LogCatPlugin.startLogger();
```

You should see the stream of Logcat data coming in.
