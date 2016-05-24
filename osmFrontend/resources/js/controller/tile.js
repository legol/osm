/**
 * Created by ChenJie3 on 2016/2/18.
 */

if (!Tile) {
    var Tile = function () {
        this.data = new Object();

        this.data.position = new Object();

        var log = log4javascript.getDefaultLogger();
        log.info("Tile loaded...");


    };

    Tile.prototype = {
        init: function (_left, _top) {
            var log = log4javascript.getDefaultLogger();
            log.info("Tile initialized.");

            this.data.position.left = _left;
            this.data.position.top = _top;

            this.data.div = document.createElement('div');
            this.data.div.innerText = this.getId();
            this.data.div.id = this.getId();
            this.data.div.className = 'tile';
        },

        getId: function(){
            return 'tile_' + this.data.position.left + '_' + this.data.position.top;
        },

        addTo : function(parentId){
            document.getElementById(parentId).appendChild(this.data.div);

            var jDiv = $("#" + this.getId());
            jDiv.position({
                my: "left top",
                at: "left+" + this.data.position.left + " top+" + this.data.position.top,
                of: "#" + parentId,
                collision: "none"
            });
        },

        removeFromParent: function(){

        },
    };
}

