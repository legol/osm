/**
 * Created by ChenJie3 on 2016/2/18.
 */

if (!MapLoader) {
    var MapLoader = function () {
        this.data = new Object();

        var log = log4javascript.getDefaultLogger();
        log.info("MapLoader loaded...");
    };

    MapLoader.prototype = {
        init: function(){
            var log = log4javascript.getDefaultLogger();
            log.info("MapLoader initialized.");
        },

    };

    window.mapLoader = new MapLoader();
}
