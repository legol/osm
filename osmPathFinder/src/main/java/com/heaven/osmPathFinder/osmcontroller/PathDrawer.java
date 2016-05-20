package com.heaven.osmPathFinder.osmcontroller;

import com.heaven.osmPathFinder.UI.GraphicsUtils;
import com.heaven.osmPathFinder.UI.TestData;
import com.heaven.osmPathFinder.model.GeomBox;
import com.heaven.osmPathFinder.osmmodel.GraphicsPoint;
import com.heaven.osmPathFinder.osmmodel.PathFinderResultPoint;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.List;
import java.util.Set;

/**
 * Created by chenjie3 on 2016/5/18.
 */
public class PathDrawer implements PathFinderObserver{
    private static final Logger LOGGER = Logger.getLogger(PathDrawer.class);

    private static PathDrawer instance = null;

    PathDrawerObserver ob = null;

    public static PathDrawer sharedInstance() {
        if (instance == null) {
            instance = new PathDrawer();
        }
        return instance;
    }

    public void drawPath(GeomBox boundingBox, int imageWidth, int imageHeight, Graphics2D g,
                         long nodeFrom, long nodeTo) {
        List<PathFinderResultPoint> points = PathFinder.sharedInstance().searchPath(nodeFrom, nodeTo, null);

        drawFinalPath(points, boundingBox, imageWidth, imageHeight, g);
    }

    public void drawPathSbS(GeomBox boundingBox, int imageWidth, int imageHeight,
                            long nodeFrom, long nodeTo,
                            PathDrawerObserver  _ob) {
        ob = _ob;
        PathFinder.sharedInstance().searchPath(nodeFrom, nodeTo, this);
    }

    public void drawFinalPath(List<PathFinderResultPoint> points, GeomBox boundingBox, int imageWidth, int imageHeight, Graphics2D g){
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

    @Override
    public void onProgress(Set<PathFinderResultPoint> openSet, Set<PathFinderResultPoint> closedSet) {
        BufferedImage img = GraphicsUtils.createTransparentImage(TestData.sharedInstance().generateImageRequest.imageWidth,
                TestData.sharedInstance().generateImageRequest.imageHeight);

        Graphics2D g = GraphicsUtils.createTransparentGraphics(img, 0.6f);
        g.setColor(Color.blue);

        int radius = 6;
        for (PathFinderResultPoint point : openSet){
            GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(point.geoPoint,
                    TestData.sharedInstance().generateImageRequest.boundingBox,
                    TestData.sharedInstance().generateImageRequest.imageWidth,
                    TestData.sharedInstance().generateImageRequest.imageHeight);

            g.fillOval(p1.x - radius, p1.y - radius, radius*2, radius*2);
        }

        g.setColor(Color.red);
        radius = 4;
        for (PathFinderResultPoint point : closedSet){
            GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(point.geoPoint,
                    TestData.sharedInstance().generateImageRequest.boundingBox,
                    TestData.sharedInstance().generateImageRequest.imageWidth,
                    TestData.sharedInstance().generateImageRequest.imageHeight);

            g.fillOval(p1.x - radius, p1.y - radius, radius*2, radius*2);
        }

        g.dispose();

        ob.onProgress(openSet, closedSet, img);
    }

    @Override
    public void onCompleted(Set<PathFinderResultPoint> openSet, Set<PathFinderResultPoint> closedSet, List<PathFinderResultPoint> path) {
        BufferedImage img = GraphicsUtils.createTransparentImage(TestData.sharedInstance().generateImageRequest.imageWidth,
                TestData.sharedInstance().generateImageRequest.imageHeight);

        Graphics2D g = GraphicsUtils.createTransparentGraphics(img, 0.8f);
        g.setColor(Color.blue);

        int radius = 6;
        for (PathFinderResultPoint point : openSet){
            GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(point.geoPoint,
                    TestData.sharedInstance().generateImageRequest.boundingBox,
                    TestData.sharedInstance().generateImageRequest.imageWidth,
                    TestData.sharedInstance().generateImageRequest.imageHeight);

            g.fillOval(p1.x - radius, p1.y - radius, radius*2, radius*2);
        }

        g.setColor(Color.red);
        radius = 4;
        for (PathFinderResultPoint point : closedSet){
            GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(point.geoPoint,
                    TestData.sharedInstance().generateImageRequest.boundingBox,
                    TestData.sharedInstance().generateImageRequest.imageWidth,
                    TestData.sharedInstance().generateImageRequest.imageHeight);

            g.fillOval(p1.x - radius, p1.y - radius, radius*2, radius*2);
        }

        drawFinalPath(path, TestData.
                sharedInstance().generateImageRequest.boundingBox,
                TestData.sharedInstance().generateImageRequest.imageWidth,
                TestData.sharedInstance().generateImageRequest.imageHeight,
                g);

        g.dispose();

        ob.onCompleted(openSet, closedSet, path, img);
    }
}
