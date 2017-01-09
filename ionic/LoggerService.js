app.service("LoggerService",
    /**
     *
     * @param $log {$log}
     * @returns {*}
     */
    function ($log) {

        var _this = this;

        var logCatPlugin = null;
        var PLUGIN_PATH = 'cordova.plugins.';
        var PLUGIN_NAME = 'LogCatPlugin';

        if (_.has(window, PLUGIN_PATH + PLUGIN_NAME)) {
            logCatPlugin = _.get(window, PLUGIN_PATH + PLUGIN_NAME);
            $log.info(PLUGIN_NAME + ' - Plug-in loaded');
            $log.info(PLUGIN_NAME + ' - Version:\n' + JSON.stringify(logCatPlugin.getVersion(), null, 4));
        } else {
            $log.info("Plugin not found: " + PLUGIN_NAME);
        }

        this.init = function() {
            //
        };

        this.startLogger = function() {
            if (!_.isNil(logCatPlugin)) {
                logCatPlugin.startLogger();
            }
        };

        return _this;
    });
