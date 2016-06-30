var osmApplicationStyle = {
  height:'100%',
  width:'100%',
  display: 'flex',
  'flex-flow': 'column',
};

var Application = React.createClass({
  render: function() {
    return <div id="osm_application" style={osmApplicationStyle}>
      <OsmHeader></OsmHeader>
      <OsmContent></OsmContent>
    </div>;
  }
});

ReactDOM.render(
    <Application/>,
    document.getElementById('container')
);