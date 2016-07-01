/**
 * Created by legol on 6/30/2016.
 */
var rightPanelStyle = {
    height:'100%',
    width:'50px',
    background: '#00ff00',
    border: '1px solid #005500',
};

var RightPanel = React.createClass({
    render: function() {
        return <div id='right_panel' style={rightPanelStyle}>right</div>;
    }
});
