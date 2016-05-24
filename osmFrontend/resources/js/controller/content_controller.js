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

                window.tileController.init();
            },
        },
    };

    window.contentController = new ContentController();
}
