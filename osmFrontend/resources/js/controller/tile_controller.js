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

            this.data.scale = 1.0;
            this.data.viewport = {x:0, y:0, width:$("#map_container").innerWidth(), height:$("#map_container").innerHeight()};

            this.data.tileArray = new Array();
            this.data.tileWidth = 256;
            this.data.tileHeight = 256;

//            $("#map_canvas").offset({left:-200, top:200});

            this.observeParentSizeChange();
        },

        observeParentSizeChange: function() {
            $(window).on("resize", $.proxy(this.resizeCanvas, this));
            this.resizeCanvas();
        },

        resizeCanvas: function() {
            var log = log4javascript.getDefaultLogger();
            log.info("TileController.resizeCanvas() called.");

            this.data.viewport.width = $("#map_container").innerWidth();
            this.data.viewport.height = $("#map_container").innerHeight();
            this.data.viewport.x = 0;
            this.data.viewport.y = 0;

            log.info("viewport=" + JSON.stringify(this.data.viewport));

            this.tile();
        },

        tile: function() {
            var log = log4javascript.getDefaultLogger();

            // add missing tiles and remove redundant ones.
            var t = this.data.viewport.y;
            var b = this.data.viewport.y + this.data.viewport.height;
            var l = this.data.viewport.x;
            var r = this.data.viewport.x + this.data.viewport.width;
            l = (l % this.data.tileWidth) * this.data.tileWidth;
            t = (t % this.data.tileHeight) * this.data.tileHeight;

            log.info("l, t, r, b = " + l + "," + t + "," + r + "," + b);

            while(t <= b){

                var l = this.data.viewport.x;
                l = (l % this.data.tileWidth) * this.data.tileWidth;

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
                        log.info("add tile at: " + l + ", " + t);

                        var newTile = new Tile();
                        newTile.init(l, t);
                        newTile.addTo("map_canvas");

                        this.data.tileArray.push(newTile);
                    }
                    else{
                        log.info("tile exists at: " + l + ", " + t);
                    }

                    l += this.data.tileWidth;
                }

                t += this.data.tileHeight;
            }
        },
    };

    window.tileController = new TileController();
}
