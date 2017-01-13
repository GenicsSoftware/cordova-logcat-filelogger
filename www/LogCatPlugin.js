var exec = require('cordova/exec');
var oVersion = { ver: "0.1", date: "07.01.2017-14:49"};

var LogCatPlugin = {
    init: function (opt) {
        opt = opt || {};
        exec(
            opt.success,
            opt.failure,
            'LogCatPlugin',
            'init',
            [
                opt.jsLogFileName,
                opt.logCatFileName,
                opt.maxFileSize,
                opt.filterBy != null ? opt.filterBy.join(';;;') : undefined,
                opt.filterOut != null ? opt.filterOut.join(';;;') : undefined
            ]
        );
    },
    startLogger: function (opt) {
        opt = opt || {};
        exec(
            opt.success,
            opt.failure,
            'LogCatPlugin',
            'startLogger',
            []
        );
    },
    stopLogger: function () {
        opt = opt || {};
        exec(
            opt.success,
            opt.failure,
            'LogCatPlugin',
            'stopLogger',
            []
        );
    },
    jsLog: function (string) {
        opt = opt || {};
        exec(
            opt.success,
            opt.failure,
            'LogCatPlugin',
            'jsLog',
            [
                string
            ]
        );
    },
    deleteLog: function () {
        opt = opt || {};
        exec(
            opt.success,
            opt.failure,
            'LogCatPlugin',
            'deleteLog',
            []
        );
    },
    getJsLog: function (fnCallback) {
        opt = opt || {};
        exec(
            opt.success,
            opt.failure,
            'LogCatPlugin',
            'getJsLog',
            []
        );
    },
    getLogCat: function (fnCallback) {
        opt = opt || {};
        exec(
            opt.success,
            opt.failure,
            'LogCatPlugin',
            'getLogCat',
            []
        );
    },
    getVersion: function () {
        return oVersion;
    }
};

module.exports = LogCatPlugin;
