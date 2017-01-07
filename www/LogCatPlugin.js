var exec = require('cordova/exec');
var oVersion = { ver: "0.1", date: "07.01.2017-14:49"};

var LogCatPlugin = {
    test: function (success, failure) {
        exec(success, failure, "LogCatPlugin", "test", []);
    },
    getVersion: function () {
        return oVersion;
    }
};

module.exports = LogCatPlugin;
