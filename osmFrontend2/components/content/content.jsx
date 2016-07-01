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
};

var OsmContent = React.createClass({
    render: function() {
        return <div id='osm_content' style={osmContentStyle}>
                    <LeftPanel></LeftPanel>
                    <RightPanel></RightPanel>
        </div>;
    }
});
