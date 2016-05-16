package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.*;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.geometry.Pos;
import javafx.util.Pair;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;

/**
 * Created by legol on 5/14/2016.
 */
public class OSMDrawer {
    private static final Logger LOGGER = Logger.getLogger(OSMDrawer.class);
    private static OSMDrawer instance = null;

    public static OSMDrawer sharedInstance() {
        if (instance == null) {
            instance = new OSMDrawer();
        }
        return instance;
    }

    public void drawOSM(GeomBox boundingBox, int imageWidth, int imageHeight, Graphics2D g){

        LinkedList<HashMap<String, LinkedList<GraphicsLayerElement>>> allLayers = new LinkedList<HashMap<String, LinkedList<GraphicsLayerElement>>>();
        for (int i = -5; i <= 5; i++){
            HashMap<String, LinkedList<GraphicsLayerElement>> layers = new HashMap<String, LinkedList<GraphicsLayerElement>>();
            layers.put("layer_land", new LinkedList<GraphicsLayerElement>());
            layers.put("layer_natural", new LinkedList<GraphicsLayerElement>());
            layers.put("layer_building", new LinkedList<GraphicsLayerElement>());
            layers.put("layer_water", new LinkedList<GraphicsLayerElement>());
            layers.put("layer_highway", new LinkedList<GraphicsLayerElement>());
            layers.put("layer_highway_link", new LinkedList<GraphicsLayerElement>());
            layers.put("layer_rail", new LinkedList<GraphicsLayerElement>());
            layers.put("layer_other", new LinkedList<GraphicsLayerElement>());
            layers.put("layer_tag", new LinkedList<GraphicsLayerElement>());
            layers.put("layer_boundary", new LinkedList<GraphicsLayerElement>());

            allLayers.add(layers);
        }

        List<Long> ways = PostgresqlAdapter.sharedInstance().getWaysByBoundingBox(boundingBox);
        for (int i = 0; i < ways.size(); i++){
            drawWay(ways.get(i).longValue(),
                    boundingBox, imageWidth, imageHeight,
                    g,
                    allLayers);
        }

        drawLayers(allLayers, boundingBox, imageWidth, imageHeight, g);
    }

    public void drawLayers(LinkedList<HashMap<String, LinkedList<GraphicsLayerElement>>> allLayers,
                           GeomBox boundingBox, int imageWidth, int imageHeight, Graphics2D g){

        Graphics2D g1 = (Graphics2D)g.create();

        // draw background
        g1.setColor(new Color(241, 238, 233));
        g1.fillRect(0, 0, imageWidth, imageHeight);

        for (int layerIdx = 0; layerIdx < allLayers.size(); layerIdx++){
            HashMap<String, LinkedList<GraphicsLayerElement>> layers = allLayers.get(layerIdx);

            List<GraphicsLayerElement> elements;

            elements = layers.get("layer_land");
            for (int i = 0; i < elements.size(); i++){
                drawLand(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }

            elements = layers.get("layer_natural");
            for (int i = 0; i < elements.size(); i++){
                drawNatural(elements.get(i).points, elements.get(i).tags,
                            boundingBox, imageWidth, imageHeight,
                            g);
            }

            elements = layers.get("layer_building");
            for (int i = 0; i < elements.size(); i++){
                drawBuilding(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }

            elements = layers.get("layer_water");
            for (int i = 0; i < elements.size(); i++){
                drawWater(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }

            elements = layers.get("layer_highway");
            for (int i = 0; i < elements.size(); i++){
                drawHighway(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }

            elements = layers.get("layer_highway_link");
            for (int i = 0; i < elements.size(); i++){
                drawHighwayLink(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }

            elements = layers.get("layer_rail");
            for (int i = 0; i < elements.size(); i++){
                drawRail(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }

            elements = layers.get("layer_other");
            for (int i = 0; i < elements.size(); i++){
                drawOther(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }

            elements = layers.get("layer_tag");
            for (int i = 0; i < elements.size(); i++){
                drawTags(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }

            elements = layers.get("layer_boundary");
            for (int i = 0; i < elements.size(); i++){
                drawBoundary(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }
        }

    }

    public GraphicsPoint GeomPoint2GraphicsPoint(GeomPoint geomPoint, GeomBox boundingBox, int imageWidth, int imageHeight){
        GraphicsPoint point = new GraphicsPoint();

        point.x = (int)Math.round(imageWidth * (geomPoint.longitude - boundingBox.minlon) / (boundingBox.maxlon - boundingBox.minlon));
        point.y = (int)Math.round(imageHeight - imageHeight * (geomPoint.latitude - boundingBox.minlat) / (boundingBox.maxlat - boundingBox.minlat));

        return point;
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
    public boolean isWater(List<Pair<String, String>> tags){
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

    public void drawWay(long way,
                        GeomBox boundingBox, int imageWidth, int imageHeight,
                        Graphics2D g,
                        LinkedList<HashMap<String, LinkedList<GraphicsLayerElement>>> allLayers){
        List<GeomPoint> points = PostgresqlAdapter.sharedInstance().getPointsOfWay(way);
        List<Pair<String, String>> tags = PostgresqlAdapter.sharedInstance().getTags("way", way);

        String layerTagValue = tagValue(tags, "layer");
        int layerIdx = layerTagValue == null ? 0 : Integer.parseInt(layerTagValue) + 5; // layer is between -5..5. see http://wiki.openstreetmap.org/wiki/Map_Features#Highway
        if (layerIdx < 0) layerIdx = 0;
        if (layerIdx >= allLayers.size()) layerIdx = allLayers.size() - 1;

        HashMap<String, LinkedList<GraphicsLayerElement>> layers = allLayers.get(layerIdx);

        GraphicsLayerElement element = new GraphicsLayerElement();
        element.points = points;
        element.tags = tags;

        if (isBuilding(tags)){
            layers.get("layer_building").add(element);
        }
        else if (isLand(tags) || isLeisure(tags) || isAmenity(tags)){
            layers.get("layer_land").add(element);

            layers.get("layer_tag").add(element);
        }
        else if (isWater(tags)){
            layers.get("layer_water").add(element);
            layers.get("layer_tag").add(element);
        }
        else if (isNatural(tags)){
            layers.get("layer_natural").add(element);
        }
        else if (isHighway(tags)) {
            String highwayValue = tagValue(tags, "highway");

            if (highwayValue.compareToIgnoreCase("motorway_link") == 0 ||
                    highwayValue.compareToIgnoreCase("trunk_link") == 0 ||
                    highwayValue.compareToIgnoreCase("primary_link") == 0 ||
                    highwayValue.compareToIgnoreCase("secondary_link") == 0 ||
                    highwayValue.compareToIgnoreCase("tertiary_link") == 0){
                layers.get("layer_highway_link").add(element);
            }
            else{
                layers.get("layer_highway").add(element);
                layers.get("layer_tag").add(element);
            }

        }
        else if (isRail(tags)){
            layers.get("layer_rail").add(element);
        }
        else if (isBoundary(tags)){
            layers.get("layer_boundary").add(element);
        }
        else{
            if (!isPower(tags)){
                layers.get("layer_other").add(element);
            }
        }
    }

    public void drawHighway(List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g){
        String highwayValue = tagValue(tags, "highway");

        Graphics2D g1 = (Graphics2D) g.create();

        BasicStroke edgeStroke = null;
        BasicStroke innerStroke = null;
        Color edgeClr = null;
        Color innerClr = null;

        if (highwayValue.compareToIgnoreCase("motorway") == 0){
            edgeStroke = new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(221, 41, 108);
            innerStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            innerClr = new Color(232, 143, 161);
        }
        else if (highwayValue.compareToIgnoreCase("trunk") == 0){
            edgeStroke = new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(203, 81, 52);
            innerStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(249, 177, 156);
        }
        else if (highwayValue.compareToIgnoreCase("primary") == 0){
            edgeStroke = new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(173, 123, 26);
            innerStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(252, 214, 164);
        }
        else if (highwayValue.compareToIgnoreCase("secondary") == 0){
            edgeStroke = new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(124, 137, 23);
            innerStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(246, 250, 190);
        }
        else if (highwayValue.compareToIgnoreCase("cycleway") == 0 || highwayValue.compareToIgnoreCase("footway") == 0){
            edgeStroke = null;
            edgeClr = null;
            innerStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{9}, 0);
            innerClr = new Color(123, 121, 247);
        }
        else{
            edgeStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(173, 173, 173);
            innerStroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(254, 254, 254);
        }

        if (edgeStroke != null && edgeClr != null){
            g1.setStroke(edgeStroke);
            g1.setColor(edgeClr);

            double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
            double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;
            for (int i = 0; i + 1 < points.size(); i++){
                GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
                GraphicsPoint p2 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

                g1.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        g1.setStroke(innerStroke);
        g1.setColor(innerClr);
        for (int i = 0; i + 1 < points.size(); i++){
            GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            GraphicsPoint p2 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

            g1.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    public void drawHighwayLink(List<GeomPoint> points, List<Pair<String, String>> tags,
                                GeomBox boundingBox, int imageWidth, int imageHeight,
                                Graphics2D g){
        String highwayValue = tagValue(tags, "highway");

        Graphics2D g1 = (Graphics2D) g.create();

        BasicStroke edgeStroke = null;
        BasicStroke innerStroke = null;
        Color edgeClr = null;
        Color innerClr = null;

        if (highwayValue.compareToIgnoreCase("motorway_link") == 0){
            edgeStroke = new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(221, 41, 108);
            innerStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            innerClr = new Color(232, 143, 161);
        }
        else if (highwayValue.compareToIgnoreCase("trunk_link") == 0){
            edgeStroke = new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(203, 81, 52);
            innerStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(249, 177, 156);
        }
        else if (highwayValue.compareToIgnoreCase("primary_link") == 0){
            edgeStroke = new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(173, 123, 26);
            innerStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(252, 214, 164);
        }
        else if (highwayValue.compareToIgnoreCase("secondary_link") == 0){
            edgeStroke = new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(124, 137, 23);
            innerStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(246, 250, 190);
        }
        else{
            edgeStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(173, 173, 173);
            innerStroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(254, 254, 254);
        }

        g1.setStroke(edgeStroke);
        g1.setColor(edgeClr);

        double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
        double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;
        for (int i = 0; i + 1 < points.size(); i++){
            GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            GraphicsPoint p2 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

            g1.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        g1.setStroke(innerStroke);
        g1.setColor(innerClr);
        for (int i = 0; i + 1 < points.size(); i++){
            GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            GraphicsPoint p2 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

            g1.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }


    public void drawRail(List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g){

        String railValue = tagValue(tags, "railway");
        if (railValue != null && railValue.compareToIgnoreCase("subway") == 0){
            Graphics2D g1 = (Graphics2D) g.create();

            BasicStroke edgeStroke = null;
            BasicStroke innerStroke = null;
            Color edgeClr = null;
            Color innerClr = null;

            edgeStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = Color.black;
            innerStroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            innerClr = new Color(153, 153, 153);

            g1.setStroke(edgeStroke);
            g1.setColor(edgeClr);

            double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
            double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;
            for (int i = 0; i + 1 < points.size(); i++){
                GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
                GraphicsPoint p2 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

                g1.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            g1.setStroke(innerStroke);
            g1.setColor(innerClr);
            for (int i = 0; i + 1 < points.size(); i++){
                GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
                GraphicsPoint p2 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

                g1.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    public void drawBoundary(List<GeomPoint> points, List<Pair<String, String>> tags,
                                GeomBox boundingBox, int imageWidth, int imageHeight,
                                Graphics2D g){
        Graphics2D g1 = (Graphics2D) g.create();

        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{ 21.0f, 9.0f, 3.0f, 9.0f }, 0);
        g1.setStroke(dashed);
        g1.setColor(new Color(206, 154, 202));

        double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
        double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;

        for (int i = 0; i + 1 < points.size(); i++){
            GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            GraphicsPoint p2 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

            g1.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    public void drawOther(List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g){
        Graphics2D g1 = (Graphics2D)g.create();

        g1.setColor(Color.white);

        double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
        double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;

        for (int i = 0; i + 1 < points.size(); i++){
            GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            GraphicsPoint p2 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

            g1.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    public void drawBuilding(List<GeomPoint> points, List<Pair<String, String>> tags,
                             GeomBox boundingBox, int imageWidth, int imageHeight,
                             Graphics2D g){
        Graphics2D g1 = (Graphics2D)g.create();

        double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
        double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;

        Polygon p=new Polygon();

        for (int i = 0; i < points.size(); i++){
            GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            p.addPoint(p1.x, p1.y);
        }

        g1.setColor(Color.black);
        g1.draw(p);

        g1.setColor(new Color(216, 208, 197));
        g1.fillPolygon(p);
    }

    public void drawWater(List<GeomPoint> points, List<Pair<String, String>> tags,
                         GeomBox boundingBox, int imageWidth, int imageHeight,
                         Graphics2D g){

        Graphics2D g1 = (Graphics2D) g.create();

        g1.setColor(new Color(181, 208, 208));

        String waterValue = tagValue(tags, "waterway");
        if (waterValue != null && waterValue.compareToIgnoreCase("riverbank") == 0){
            Polygon p=new Polygon();

            for (int i = 0; i < points.size(); i++){
                GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
                p.addPoint(p1.x, p1.y);
            }

            g1.fillPolygon(p);
        }else{
            BasicStroke wideStroke = new BasicStroke(5.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            g1.setStroke(wideStroke);

            double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
            double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;

            for (int i = 0; i + 1 < points.size(); i++){
                GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
                GraphicsPoint p2 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

                g1.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    public void drawLand(List<GeomPoint> points, List<Pair<String, String>> tags,
                             GeomBox boundingBox, int imageWidth, int imageHeight,
                             Graphics2D g){
        Graphics2D g1 = (Graphics2D)g.create();

        double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
        double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;

        Polygon p=new Polygon();

        for (int i = 0; i < points.size(); i++){
            GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            p.addPoint(p1.x, p1.y);
        }

        Stroke landEdge = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        g1.setColor(new Color(180, 180, 178));
        g1.draw(p);

        if (isLeisure(tags)){
            g1.setColor(new Color(97, 240, 19)); // green
        }else if (isAmenity(tags)){
            g1.setColor(new Color(246, 249, 190)); // light green
        } else {
            String landValue = tagValue(tags, "landuse");
            if (landValue != null &&
                    (landValue.compareToIgnoreCase("basin") == 0 || landValue.compareToIgnoreCase("reservoir") == 0)){
                g1.setColor(new Color(181, 208, 208)); // must be same with waterway
            } else {
                g1.setColor(new Color(224, 222, 222)); // grey
            }
        }

        g1.fillPolygon(p);
    }

    public void drawNatural(List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g){
        Graphics2D g1 = (Graphics2D)g.create();

        double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
        double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;

        Polygon p=new Polygon();

        for (int i = 0; i < points.size(); i++){
            GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            p.addPoint(p1.x, p1.y);
        }

        String naturalValue = tagValue(tags, "natural");
        if (naturalValue != null && naturalValue.compareToIgnoreCase("water") == 0){
            g1.setColor(new Color(181, 208, 208)); // must be same with waterway
        } else {
            g1.setColor(new Color(199, 228, 182));
        }

        g1.fillPolygon(p);
    }


    public void drawTags(List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g){

        Graphics2D g1 = (Graphics2D)g.create();

        String name = tagValue(tags, "name");
        if (name == null){
            name = "";
        }

        g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

//        if (isLand(tags)){
//            // find the center point of the land
//            GraphicsPoint pCenter = new GraphicsPoint();
//            pCenter.x = 0;
//            pCenter.y = 0;
//            for (int i = 0; i < points.size(); i++){
//                GraphicsPoint p = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
//
//                pCenter.x += p.x;
//                pCenter.y += p.y;
//            }
//            pCenter.x /= points.size();
//            pCenter.y /= points.size();
//
//            int strWidth = g.getFontMetrics().stringWidth(str);
//            int strHeight = g.getFontMetrics().getHeight();
//
//            g1.setColor(Color.white);
//            g1.drawString(str, pCenter.x - strWidth / 2 + 2, pCenter.y + 2);
//
//            g1.setColor(Color.black);
//            g1.drawString(str, pCenter.x - strWidth / 2, pCenter.y);
//        }

        if (isWater(tags)){
            g1.setColor(Color.black);

            for (int i = 0; i + 1 < points.size(); i++){
                GraphicsPoint p0 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
                GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

                AffineTransform at = new AffineTransform();
                at.setToRotation(p1.x - p0.x, p1.y - p0.y, p0.x, p0.y);
                g1.setTransform(at);
                g1.drawString(name, p0.x, p0.y);
            }
        }else if (isHighway(tags)){
            g1.setColor(Color.black);

            for (int i = 0; i + 1 < points.size(); i++){
                GraphicsPoint p0 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
                GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

                AffineTransform at = new AffineTransform();
                at.setToRotation(p1.x - p0.x, p1.y - p0.y, p0.x, p0.y);
                g1.setTransform(at);
                g1.drawString(name, p0.x, p0.y);
            }
        }
//        else{
//            g1.setColor(Color.black);
//
//            GraphicsPoint p = GeomPoint2GraphicsPoint(points.get(0), boundingBox, imageWidth, imageHeight);
//            g1.drawString(name, p.x, p.y);
//        }
    }
}
