/**
 * Created by legol on 6/30/2016.
 */
var mapContainerStyle = {
    height:'100%',
    width:'100%',
    border: '2px solid #0000FF',
};

var MapContainer = React.createClass({
    render: function() {
        return <div id='map_container' style={mapContainerStyle}>
            map goes here.
        </div>;
    }
});
