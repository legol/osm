/**
 * Created by legol on 6/30/2016.
 */

var mapContainerStyle = {
    height:'100%',
    width:'100%',
    border: '2px solid #0000FF',
};

var mapCanvasStyle = {
    height:'500px',
    width: '500px',
    border: '1px solid #FF0000',
    borderRadius: '0px',
    position: 'absolute',
    margin:0,
    padding:0,
};

var MapContainer = React.createClass({
    render: function() {
        return <div id='map_container' style={mapContainerStyle}>
            <div id="map_canvas" style={mapCanvasStyle}></div>
        </div>;
    },

    componentDidMount: function(){
        window.tileController.init();
    }
});
