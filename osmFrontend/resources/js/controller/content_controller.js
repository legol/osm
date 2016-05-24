/**
 * Created by ChenJie3 on 2016/2/18.
 */

if (!ContentController) {
    var ContentController = function () {
        this.data = new Object();
        var log = log4javascript.getDefaultLogger();
        log.info("ContentController loaded...");
    };

    ContentController.prototype = {
        main: {
            init: function (data) {
                var log = log4javascript.getDefaultLogger();
                log.info("ContentController initialized.");

                window.contentController.canvas.init();
            },
        },

        canvas: {
            init: function(){
                this.observeParentSizeChange();
            },

            observeParentSizeChange: function() {
                $(window).on("resize", $.proxy(this.resizeCanvas, this));
                this.resizeCanvas();
            },

            resizeCanvas: function() {
                var log = log4javascript.getDefaultLogger();
                log.info("ContentController.canvas.resizeCanvas() called.");

                var canvas = document.getElementById('map_canvas');

                // this is the image size. Image size can be different from the HTML canvas element size. By doing this, we made sure the image won't be scaled.
                canvas.width = $("#map_canvas").innerWidth();
                canvas.height = $("#map_canvas").innerHeight();

                this.paint();
            },

            paint: function() {
                var log = log4javascript.getDefaultLogger();
                log.info("ContentController.canvas.paint() called.");


                var canvas = document.getElementById('map_canvas');
                var context = canvas.getContext('2d');

                context.beginPath();
                context.moveTo(0, 0);
                context.lineTo(500, 500);
                context.stroke();
            },
        },
    };

    window.contentController = new ContentController();
}
