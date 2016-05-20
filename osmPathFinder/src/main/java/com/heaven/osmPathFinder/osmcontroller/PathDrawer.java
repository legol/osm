package com.heaven.osmPathFinder.osmcontroller;

import com.heaven.osmPathFinder.model.GeomBox;
import com.heaven.osmPathFinder.osmmodel.GraphicsPoint;
import com.heaven.osmPathFinder.osmmodel.PathFinderResultPoint;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.List;

/**
 * Created by chenjie3 on 2016/5/18.
 */
public class PathDrawer {
    private static final Logger LOGGER = Logger.getLogger(PathDrawer.class);

    private static PathDrawer instance = null;

    public static PathDrawer sharedInstance() {
        if (instance == null) {
            instance = new PathDrawer();
        }
        return instance;
    }

    public void drawPath(GeomBox boundingBox, int imageWidth, int imageHeight, Graphics2D g,
                         long nodeFrom, long nodeTo) {
        List<PathFinderResultPoint> points = PathFinder.sharedInstance().searchPath(nodeFrom, nodeTo);

        Graphics2D g1 = (Graphics2D) g.create();

        g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g1.setColor(Color.blue);

        BasicStroke pathStroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        g1.setStroke(pathStroke);

        double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
        double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;
        for (int i = 0; i + 1 < points.size(); i++){
            GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i).geoPoint, boundingBox, imageWidth, imageHeight);
            GraphicsPoint p2 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i + 1).geoPoint, boundingBox, imageWidth, imageHeight);

            g1.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
}
