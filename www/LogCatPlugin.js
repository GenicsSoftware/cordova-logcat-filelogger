var exec = require('cordova/exec');
var ARRAY_SEPARATOR = ';;;';
var LINEBREAK_SEPARATOR = '--linebreak--';
var oVersion = {ver: "0.1", date: "07.01.2017-14:49"};
var fnCallBack = null;

var LogCatPlugin = {
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
                opt['filterBy'] != null ? opt['filterBy'].join(ARRAY_SEPARATOR) : undefined,
                opt['filterOut'] != null ? opt['filterOut'].join(ARRAY_SEPARATOR) : undefined,
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
            opt['failure'],
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
                opt['filterBy'] != null ? opt['filterBy'].join(ARRAY_SEPARATOR) : undefined,
                opt['filterOut'] != null ? opt['filterOut'].join(ARRAY_SEPARATOR) : undefined,
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
                opt['filterBy'] != null ? opt['filterBy'].join(ARRAY_SEPARATOR) : undefined,
                opt['filterOut'] != null ? opt['filterOut'].join(ARRAY_SEPARATOR) : undefined,
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
            opt['failure'],
            'LogCatPlugin',
            'throwExampleError',
            []
        );
    },
    throwExampleFatalError: function (opt) {
        opt = opt || {};
        exec(
            opt['success'],
            opt['failure'],
            'LogCatPlugin',
            'throwExampleFatalError',
            []
        );
    },
    onLogCatEntry: function (sMessage) {
        if (fnCallBack != null) {
            try {
                var sBody = sMessage.replace(new RegExp(_.escapeRegExp(LINEBREAK_SEPARATOR), 'g'), '\n\t');
                var sHeader = sMessage.split(LINEBREAK_SEPARATOR)[0];
                var sLevel = null;
                if (sHeader.indexOf(' V/') > -1) {
                    sLevel = "VERBOSE";
                } else if (sHeader.indexOf(' D/') > -1) {
                    sLevel = "DEBUG";
                } else if (sHeader.indexOf(' I/') > -1) {
                    sLevel = "INFO";
                } else if (sHeader.indexOf(' W/') > -1) {
                    sLevel = "WARNING";
                } else if (sHeader.indexOf(' D/') > -1) {
                    sLevel = "DEBUG";
                } else if (sHeader.indexOf(' E/') > -1) {
                    sLevel = "ERROR";
                } else if (sHeader.indexOf(' F/') > -1) {
                    sLevel = "FATAL";
                }
                fnCallBack(sHeader, sBody, sLevel);
            } catch (e) {
                console.log(e);
            }
        }
    },
    getVersion: function () {
        return oVersion;
    }
};

module.exports = LogCatPlugin;
