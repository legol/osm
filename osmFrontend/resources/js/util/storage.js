/**
 * Created by ChenJie3 on 2016/3/4.
 */

if (!Storage) {
    var Storage = function () {
    };

    Storage.prototype.getStorage = function (type) {
        if (type.toLowerCase() == "session"){
            return window.sessionStorage;
        }
        else if (type.toLowerCase() == "local"){
            return window.localStorage;
        }
        else {
            return null;
        }
    };
}
