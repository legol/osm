/**
 * Created by legol on 6/30/2016.
 */

var osmContentStyle = {
    height:'100%',
    width:'100%',
    background: '#00ff00',
    border: '1px solid #005500',
    display:'flex',
    'flexDirection': 'row',
    'justifyContent':'space-between'
};

var OsmContent = React.createClass({
    render: function() {
        return <div id='osm_content' style={osmContentStyle}>
                    <LeftPanel></LeftPanel>
                    <MapContainer></MapContainer>
                    <RightPanel></RightPanel>
        </div>;
    }
});
