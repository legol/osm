/**
 * Created by ChenJie3 on 2016/2/18.
 */

if (!TileController) {
    var TileController = function () {
        this.data = new Object();

        var log = log4javascript.getDefaultLogger();
        log.info("TileController loaded...");
    };

    TileController.prototype = {
        init: function(){
            var log = log4javascript.getDefaultLogger();
            log.info("TileController initialized.");

            $("#map_canvas").draggable({
                drag: $.proxy(this.onDragCanvas, this)});

            this.data.scale = 1.0;
            this.data.viewport = {left:-$("#map_canvas").position().left,
                top:-$("#map_canvas").position().top,
                width:$("#map_container").innerWidth(),
                height:$("#map_container").innerHeight()};

            this.data.tileArray = new Array();
            this.data.tileWidth = 256;
            this.data.tileHeight = 256;

            this.observeParentSizeChange();

            this.moveViewport(400, 600);
        },

        onDragCanvas: function(event, ui){
            this.viewportChanged();
        },

        moveViewport: function(newL, newT){
            $("#map_canvas").position({
                my: "left top",
                at: "left-"+newL + " top-"+newT,
                of: "#map_container",
                collision: "none"
            });

            this.viewportChanged();
        },

        observeParentSizeChange: function() {
            $(window).on("resize", $.proxy(this.viewportChanged, this));
            //$("#map_container").on("resize", $.proxy(this.viewportChanged, this));
        },

        viewportChanged: function() {
            var log = log4javascript.getDefaultLogger();
            log.info("TileController.viewportChanged() called.");

            this.data.viewport = {left:-$("#map_canvas").position().left,
                top:-$("#map_canvas").position().top,
                width:$("#map_container").innerWidth(),
                height:$("#map_container").innerHeight()};

            log.info("viewport=" + JSON.stringify(this.data.viewport));

            this.tile();
        },

        tile: function() {
            var log = log4javascript.getDefaultLogger();

            // add missing tiles and remove redundant ones.
            var t = this.data.viewport.top;
            var b = this.data.viewport.top + this.data.viewport.height;
            var l = this.data.viewport.left;
            var r = this.data.viewport.left + this.data.viewport.width;
            l = Math.floor(l / this.data.tileWidth) * this.data.tileWidth;
            t = Math.floor(t / this.data.tileHeight) * this.data.tileHeight;

            log.info("l, t, r, b = " + l + "," + t + "," + r + "," + b);

            while(t <= b){

                var l = this.data.viewport.left;
                l = Math.floor(l / this.data.tileWidth) * this.data.tileWidth;

                log.info("l, t, r, b = " + l + "," + t + "," + r + "," + b);

                while(l <= r){
                    log.info("checking: " + l + ", " + t);

                    var tileExist = false;

                    for (var tileIndex = 0; tileIndex < this.data.tileArray.length; tileIndex++){
                        if (this.data.tileArray[tileIndex].data.position.left == l &&
                            this.data.tileArray[tileIndex].data.position.top == t){
                            tileExist = true;
                            break;
                        }
                    }

                    if (!tileExist){
                        var log = log4javascript.getDefaultLogger();
                        //log.info("add tile at: " + l + ", " + t);

                        var newTile = new Tile();
                        newTile.init(l, t);
                        newTile.addTo("map_canvas");

                        this.data.tileArray.push(newTile);
                    }
                    else{
                        //log.info("tile exists at: " + l + ", " + t);
                    }

                    l += this.data.tileWidth;
                }

                t += this.data.tileHeight;
            }
        },
    };

    window.tileController = new TileController();
}
