package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.GeomBox;
import com.heaven.osm.imagegenerator.model.GeomPoint;
import com.heaven.osm.imagegenerator.model.GraphicsPoint;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chenjie3 on 2016/5/17.
 */
public class OSMUtils {
    private static final Logger LOGGER = Logger.getLogger(OSMUtils.class);
    private static OSMUtils instance = null;

    public static OSMUtils sharedInstance() {
        if (instance == null) {
            instance = new OSMUtils();
        }
        return instance;
    }

    public void drawCenteredCircle(Graphics2D g, int x, int y, int r) {
        x = x-(r/2);
        y = y-(r/2);
        g.fillOval(x,y,r,r);
    }

    public GraphicsPoint GeomPoint2GraphicsPoint(GeomPoint geomPoint, GeomBox boundingBox, int imageWidth, int imageHeight){
        GraphicsPoint point = new GraphicsPoint();

        point.x = (int)Math.round(imageWidth * (geomPoint.longitude - boundingBox.minlon) / (boundingBox.maxlon - boundingBox.minlon));
        point.y = (int)Math.round(imageHeight - imageHeight * (geomPoint.latitude - boundingBox.minlat) / (boundingBox.maxlat - boundingBox.minlat));

        return point;
    }

    public void drawGeomPolygon(List<GeomPoint> points, GeomBox boundingBox, int imageWidth, int imageHeight,
                                Graphics2D g, Stroke edgeStroke, Color edgeClr, Stroke innerStroke, Color innerClr){
        Graphics2D g1 = (Graphics2D) g.create();

        Polygon p = new Polygon();
        for (int i = 0; i < points.size(); i++){
            GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            p.addPoint(p1.x, p1.y);
        }

        if (innerClr != null && innerStroke != null){
            g1.setStroke(innerStroke);
            g1.setColor(innerClr);
            g1.fillPolygon(p);
        }

        if (edgeClr != null && edgeStroke != null){
            g1.setStroke(edgeStroke);
            g1.setColor(edgeClr);
            g1.draw(p);
        }
    }


    public void drawGeomPolyline(List<GeomPoint> points, GeomBox boundingBox, int imageWidth, int imageHeight, Graphics2D g, Stroke stroke, Color clr){
        Graphics2D g1 = (Graphics2D) g.create();

        g1.setStroke(stroke);
        g1.setColor(clr);

        GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
        GraphicsPoint p0 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(0), boundingBox, imageWidth, imageHeight);
        polyline.moveTo(p0.x, p0.y);
        for (int i = 1; i < points.size(); i++){
            GraphicsPoint p = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);

            polyline.lineTo(p.x, p.y);
        }
        g1.draw(polyline);
    }

    public boolean isBuilding(List<Pair<String, String>> tags){
        return isTag(tags, "building");
    }
    public boolean isLand(List<Pair<String, String>> tags){
        return isTag(tags, "landuse");
    }
    public boolean isNatural(List<Pair<String, String>> tags){
        return isTag(tags, "natural");
    }
    public boolean isHighway(List<Pair<String, String>> tags){
        return isTag(tags, "highway");
    }
    public boolean isWaterway(List<Pair<String, String>> tags){
        return isTag(tags, "waterway");
    }
    public boolean isLeisure(List<Pair<String, String>> tags){
        return isTag(tags, "leisure");
    }
    public boolean isAmenity(List<Pair<String, String>> tags){
        return isTag(tags, "amenity");
    }
    public boolean isBoundary(List<Pair<String, String>> tags){
        return isTag(tags, "boundary");
    }
    public boolean isRail(List<Pair<String, String>> tags){
        return isTag(tags, "railway");
    }
    public boolean isPower(List<Pair<String, String>> tags){
        return isTag(tags, "power");
    }

    public boolean isTag(List<Pair<String, String>> tags, String type){
        for (int i = 0; i < tags.size(); i++){
            if (tags.get(i).getKey().compareToIgnoreCase(type) == 0){
                return true;
            }
        }
        return false;
    }

    public String tagValue(List<Pair<String, String>> tags, String type){
        for (int i = 0; i < tags.size(); i++){
            if (tags.get(i).getKey().compareToIgnoreCase(type) == 0){
                return tags.get(i).getValue();
            }
        }
        return null;
    }

    /*
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public static double millimetersFromPixels(double pixels){
        double dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        return (pixels * 25.4) / dpi;
    }

    public static double pixelsFromMillimeters(double millimeters){
        double dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        return (millimeters * dpi) / 25.4;
    }

    // returns the distance(in meters) represented by 1 centimeter(10mm).
    public static double calcScale(double minlon, double maxlon, double lat, int imageWidth){
        double distance = OSMUtils.distance(lat, lat,
                minlon, maxlon,
                0, 0); // distance in meters
        double pixels = imageWidth;
        double mm = OSMUtils.millimetersFromPixels(pixels);
        double scale = distance / mm * 10; // the real distance represented by 1cm

        return scale;
    }
}
