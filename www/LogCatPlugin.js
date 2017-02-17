    var exec = require('cordova/exec');

    var ARRAY_SEPARATOR = '--entry--';
    var LINEBREAK_SEPARATOR = '--linebreak--';
    var fnCallBack = null;

    var LogCatPlugin =  {
        init: function (opt) {
            opt = opt || {};

            if (opt['logCallBack'] != null) {
                fnCallBack = opt['logCallBack'];
            }

            exec(
                opt['success'],
                opt['failure'],
                'LogCatPlugin',
                'init',
                [
                    opt['jsLogFileName'],
                    opt['logCatFileName'],
                    opt['maxFileSizeInKB'],
                    opt['filterBy'] == null ? undefined :
                        opt['filterBy'].join(ARRAY_SEPARATOR),
                    opt['filterOut'] == null ? undefined :
                        opt['filterOut'].join(ARRAY_SEPARATOR),
                    opt['logCallBack'] != null
                ]
            );
        },
        startLogger: function (opt) {
            opt = opt || {};
            exec(
                opt['success'],
                opt['failure'],
                'LogCatPlugin',
                'startLogger',
                []
            );
        },
        stopLogger: function (opt) {
            opt = opt || {};
            exec(
                opt['success'],
                'LogCatPlugin',
                'stopLogger',
                []
            );
        },
        jsLog: function (string, opt) {
            opt = opt || {};
            exec(
                opt['success'],
                opt['failure'],
                'LogCatPlugin',
                'jsLog',
                [
                    string
                ]
            );
        },
        deleteLog: function (opt) {
            opt = opt || {};
            exec(
                opt['success'],
                opt['failure'],
                'LogCatPlugin',
                'deleteLog',
                []
            );
        },
        getJsLogPath: function (opt) {
            opt = opt || {};
            exec(
                opt['success'],
                opt['failure'],
                'LogCatPlugin',
                'getJsLogPath',
                []
            );
        },
        getLcLogPath: function (opt) {
            opt = opt || {};
            exec(
                opt['success'],
                opt['failure'],
                'LogCatPlugin',
                'getLcLogPath',
                []
            );
        },
        getLastLcEntries: function (opt) {
            opt = opt || {};
            exec(
                opt['success'],
                opt['failure'],
                'LogCatPlugin',
                'getLastLcEntries',
                [
                    opt['filterBy'] == null ? undefined :
                        opt['filterBy'].join(ARRAY_SEPARATOR),
                    opt['filterOut'] == null ? undefined :
                        opt['filterOut'].join(ARRAY_SEPARATOR),
                    opt['maxEntries']
                ]
            );
        },
        getLastJsEntries: function (opt) {
            opt = opt || {};
            exec(
                opt['success'],
                opt['failure'],
                'LogCatPlugin',
                'getLastJsEntries',
                [
                    opt['filterBy'] == null ? undefined :
                        opt['filterBy'].join(ARRAY_SEPARATOR),
                    opt['filterOut'] == null ? undefined :
                        opt['filterOut'].join(ARRAY_SEPARATOR),
                    opt['maxEntries']
                ]
            );
        },
        zipAll: function (opt) {
            opt = opt || {};
            exec(
                opt['success'],
                opt['failure'],
                'LogCatPlugin',
                'zipAll',
                []
            );
        },
        clearLcBuffer: function (opt) {
            opt = opt || {};
            exec(
                opt['success'],
                opt['failure'],
                'LogCatPlugin',
                'clearLcBuffer',
                []
            );
        },
        showInFileManager: function (opt) {
            opt = opt || {};
            exec(
                opt['success'],
                opt['failure'],
                'LogCatPlugin',
                'showInFileManager',
                []
            );
        },
        throwExampleError: function (opt) {
            opt = opt || {};
            exec(
                opt['success'],
                'LogCatPlugin',
                'throwExampleError',
                []
            );
        },
        throwExampleFatalError: function () {
            exec(
                'LogCatPlugin',
                'throwExampleFatalError',
                []
            );
        },
        onLogCatEntry: function (sMessage) {
            if (fnCallBack != null) {
                try {
                    var aSplitted = sMessage.split(LINEBREAK_SEPARATOR);
                    var sHeader = aSplitted[0];
                    var sBody = '\t' + aSplitted.splice(1).join('\n\t');
                    var sLevel = null;
                    if (sHeader.indexOf(' V/') > -1) {
                        sLevel = 'VERBOSE';
                    } else if (sHeader.indexOf(' D/') > -1) {
                        sLevel = 'DEBUG';
                    } else if (sHeader.indexOf(' I/') > -1) {
                        sLevel = 'INFO';
                    } else if (sHeader.indexOf(' W/') > -1) {
                        sLevel = 'WARNING';
                    } else if (sHeader.indexOf(' D/') > -1) {
                        sLevel = 'DEBUG';
                    } else if (sHeader.indexOf(' E/') > -1) {
                        sLevel = 'ERROR';
                    } else if (sHeader.indexOf(' F/') > -1) {
                        sLevel = 'FATAL';
                    }
                    fnCallBack(sHeader, sBody, sLevel);
                } catch (e) {
                    console.log(e);
                }
            }
        }
    };

module.exports = LogCatPlugin;
