/**
 * Created by legol on 6/30/2016.
 */
var rightPanelStyle = {
    height:'100%',
    width:'50px',
    border: '1px solid #FF0000',
    marginRight:'0px',
};

var RightPanel = React.createClass({
    render: function() {
        return <div id='right_panel' style={rightPanelStyle}>right</div>;
    }
});
