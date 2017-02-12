app.service("LoggerService",
    /**
     * @class LoggerService
     *
     * @param $log {$log}
     * @param $q {$q}
     * @returns {*}
     */
    function ($log, $q) {

        var _this = this;
        var logCatPlugin = null;
        var PLUGIN_NAME = 'LogCatPlugin';
        var PLUGIN_NOT_LOADED = 'Plugin not loaded: ' + PLUGIN_NAME;

        document.addEventListener('deviceready', function () {
            if (window.cordova != null && window.cordova.plugins != null &&
                window.cordova.plugins[PLUGIN_NAME] != null) {
                logCatPlugin = window.cordova.plugins[PLUGIN_NAME];
                $log.info('Plugin loaded: ' + PLUGIN_NAME);
            } else {
                $log.info(PLUGIN_NOT_LOADED);
            }
        }, false);

        this.init = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.init(
                        {
                            success: resolve,
                            failure: reject,
                            jsLogFileName: 'jsLog.txt',
                            logCatFileName: 'catLog.txt',
                            maxFileSizeInKB: 1024,
                            filterBy: [
                                ' W/',
                                ' E/',
                                ' F/',
                                ' Fatal'
                            ],
                            filterOut: [
                                'I/InstantRun',      // Emulation
                                'EGL_emulation',     // Emulation
                                'EGL_SWAP_BEHAVIOR', // Emulation
                                'eglCodecCommon',    // Emulation
                                ':CONSOLE',          // Console-Logs
                                'I/System.out',
                                '/LogCatPlugin'
                            ],
                            logCallBack: function (sHeader, sBody, sLevel) {
                                $log.info('[' + PLUGIN_NAME + '/' + sLevel + '] ' +
                                    sHeader + '\n' + sBody);
                            }
                        }
                    );
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.startLogger = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.startLogger(
                        {
                            success: resolve,
                            failure: reject
                        }
                    );
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.stopLogger = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.stopLogger(
                        {
                            success: resolve,
                            failure: reject
                        }
                    );
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.jsLog = function (sLog) {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.jsLog(
                        sLog,
                        {
                            success: resolve,
                            failure: reject
                        }
                    );
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.deleteLog = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.deleteLog(
                        {
                            success: resolve,
                            failure: reject
                        }
                    );
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.getJsLogPath = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.getJsLogPath(
                        {
                            success: resolve,
                            failure: reject
                        }
                    );
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.getLcLogPath = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.getLcLogPath(
                        {
                            success: resolve,
                            failure: reject
                        }
                    );
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.zipAll = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.zipAll(
                        {
                            success: resolve,
                            failure: reject
                        }
                    );
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.getLastLcEntries = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.getLastLcEntries(
                        {
                            success: resolve,
                            failure: reject,
                            filterBy: [
                                ' E/',
                                ' F/',
                                ' Fatal'
                            ],
                            filterOut: [],
                            maxEntries: 20
                        }
                    );
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.getLastJsEntries = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.getLastJsEntries(
                        {
                            success: resolve,
                            failure: reject,
                            filterBy: [],
                            filterOut: [],
                            maxEntries: 20
                        }
                    );
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.clearLogCatBuffer = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.clearLcBuffer(
                        {
                            success: resolve,
                            failure: reject
                        }
                    );
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.showInFileManager = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.showInFileManager(
                        {
                            success: resolve,
                            failure: reject
                        }
                    );
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.throwExampleError = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.throwExampleError();
                    resolve();
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        this.throwExampleFatalError = function () {
            return new $q(function (resolve, reject) {
                if (logCatPlugin != null) {
                    logCatPlugin.throwExampleFatalError();
                    resolve();
                } else {
                    reject(PLUGIN_NOT_LOADED);
                }
            });
        };

        return _this;
    });
