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

        loadMap: function(lonIdx, latIdx, divId) {
            var latBase = 40.0552;
            var lonBase = 116.2882;
            var latDelta = 0.004;
            var lonDelta = 0.005;

            var minLat = latBase - latIdx * latDelta;
            var minLon = lonBase + lonIdx * lonDelta;
            var maxLat = minLat + latDelta;
            var maxLon = minLon + lonDelta;

            var img = document.createElement("IMG");
            img.className = 'tile_img';
            img.id = divId + '_img';
            document.getElementById(divId).appendChild(img);

            $img = $(img); // jQuery-ize it.
            $img.attr(
                'src',
                "http://127.0.0.1:8081/osmImageGenerator/map?minlat=" + minLat + "&minlon=" + minLon + "&maxlon=" + maxLon + "&maxlat=" + maxLat
            );
            $img.attr(
                'draggable',
                "false"
            );

            //$divDebug = $("#" + divId + "_debug");
            //$divDebug.text($divDebug.text() + " (" + minLon + ", " + minLat + ")");
        },
    };

    window.mapLoader = new MapLoader();
}
