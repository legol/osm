/**
 * Created by ChenJie3 on 2016/2/18.
 */

if (!MainController) {
    var MainController = function () {
        this.data = new Object();
        var log = log4javascript.getDefaultLogger();
        log.info("MainController loaded...");
    };

    MainController.prototype = {
        main: {
            init: function (data) {
                var log = log4javascript.getDefaultLogger();
                log.info("MainController initialized.");

                var r = new Render();
                r.render($("#main_container"), "resources/templates/main/main.html", data);
            },
        },
    };

    window.mainController = new MainController();
}

