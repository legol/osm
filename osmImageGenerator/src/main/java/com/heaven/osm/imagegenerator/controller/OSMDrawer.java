package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.*;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
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

            layers.put("layer_debug", new LinkedList<GraphicsLayerElement>());

            allLayers.add(layers);
        }

        List<Long> ways = PostgresqlAdapter.sharedInstance().getWaysByBoundingBox(boundingBox);
        for (int i = 0; i < ways.size(); i++){
            drawWay(ways.get(i).longValue(),
                    boundingBox, imageWidth, imageHeight,
                    g,
                    allLayers);
        }

        // real drawing goes here
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

            // highwayline_edge -> highway_edge -> highwayline_inner -> highway_inner
            elements = layers.get("layer_highway_link");
            for (int i = 0; i < elements.size(); i++){
                drawHighwayLink(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g, 0);
            }

            elements = layers.get("layer_highway");
            for (int i = 0; i < elements.size(); i++){
                drawHighway(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g, 0);
            }

            elements = layers.get("layer_highway_link");
            for (int i = 0; i < elements.size(); i++){
                drawHighwayLink(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g, 1);
            }

            elements = layers.get("layer_highway");
            for (int i = 0; i < elements.size(); i++){
                drawHighway(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g, 1);
            }

            elements = layers.get("layer_rail");
            for (int i = 0; i < elements.size(); i++){
                drawRail(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g, 0);
            }
            for (int i = 0; i < elements.size(); i++){
                drawRail(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g, 1);
            }

            elements = layers.get("layer_other");
            for (int i = 0; i < elements.size(); i++){
                drawOther(elements.get(i).points, elements.get(i).tags,
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

        // for each run, we need a new TagDrawer. That's because it stores a list of rectangle and that list needs to be cleared.
        TagDrawer tagDrawer = new TagDrawer();

        // after drawing the graphics elements, the tags are drawn.
        for (int layerIdx = 0; layerIdx < allLayers.size(); layerIdx++) {
            HashMap<String, LinkedList<GraphicsLayerElement>> layers = allLayers.get(allLayers.size() - 1 - layerIdx);
            List<GraphicsLayerElement> elements;

            elements = layers.get("layer_tag");
            for (int i = 0; i < elements.size(); i++) {
                drawTags(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g,
                        tagDrawer);
            }
        }

        // draw debug info
//        for (int layerIdx = 0; layerIdx < allLayers.size(); layerIdx++) {
//            HashMap<String, LinkedList<GraphicsLayerElement>> layers = allLayers.get(allLayers.size() - 1 - layerIdx);
//            List<GraphicsLayerElement> elements;
//
//            elements = layers.get("layer_debug");
//            for (int i = 0; i < elements.size(); i++) {
//                drawDebugInfo(elements.get(i),
//                            boundingBox, imageWidth, imageHeight,
//                            g);
//            }
//        }
    }

    public void drawDebugInfo(GraphicsLayerElement element,
                                GeomBox boundingBox, int imageWidth, int imageHeight,
                                Graphics2D g){
        Graphics2D g1 = (Graphics2D)g.create();
        g1.setColor(Color.red);

        GraphicsPoint p = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(element.points.get(0), boundingBox, imageWidth, imageHeight);

        g1.drawString(element.debugInfo, p.x, p.y);
    }

    public void drawWay(long way,
                        GeomBox boundingBox, int imageWidth, int imageHeight,
                        Graphics2D g,
                        LinkedList<HashMap<String, LinkedList<GraphicsLayerElement>>> allLayers){
        List<GeomPoint> points = PostgresqlAdapter.sharedInstance().getPointsOfWay(way);
        List<Pair<String, String>> tags = PostgresqlAdapter.sharedInstance().getTags("way", way);

        String layerTagValue = OSMUtils.sharedInstance().tagValue(tags, "layer");
        int layerIdx = layerTagValue == null ? 0 : Integer.parseInt(layerTagValue) + 5; // layer is between -5..5. see http://wiki.openstreetmap.org/wiki/Map_Features#Highway
        if (layerIdx < 0) layerIdx = 0;
        if (layerIdx >= allLayers.size()) layerIdx = allLayers.size() - 1;

        HashMap<String, LinkedList<GraphicsLayerElement>> layers = allLayers.get(layerIdx);

        GraphicsLayerElement element = new GraphicsLayerElement();
        element.points = points;
        element.tags = tags;
        element.debugInfo = String.format("%d", way);

        if (OSMUtils.sharedInstance().isBuilding(tags)){
            layers.get("layer_building").add(element);
        }
        else if (OSMUtils.sharedInstance().isLand(tags) || OSMUtils.sharedInstance().isLeisure(tags) || OSMUtils.sharedInstance().isAmenity(tags)){
            layers.get("layer_land").add(element);
            layers.get("layer_tag").add(element);
        }
        else if (OSMUtils.sharedInstance().isWaterway(tags)){
            layers.get("layer_water").add(element);
            layers.get("layer_tag").add(element);
        }
        else if (OSMUtils.sharedInstance().isNatural(tags)){
            layers.get("layer_natural").add(element);
        }
        else if (OSMUtils.sharedInstance().isHighway(tags)) {
            String highwayValue = OSMUtils.sharedInstance().tagValue(tags, "highway");

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

            layers.get("layer_debug").add(element);
        }
        else if (OSMUtils.sharedInstance().isRail(tags)){
            layers.get("layer_rail").add(element);
        }
        else if (OSMUtils.sharedInstance().isBoundary(tags)){
            layers.get("layer_boundary").add(element);
        }
        else{
            if (!OSMUtils.sharedInstance().isPower(tags)){
                layers.get("layer_other").add(element);
            }
        }
    }

    public void drawHighway(List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g, int layer){
        double distance = OSMUtils.distance(boundingBox.minlat, boundingBox.minlat,
                boundingBox.minlon, boundingBox.maxlon,
                0, 0); // distance in meters
        double pixels = imageWidth;
        double mm = OSMUtils.millimetersFromPixels(pixels);
        double ruler = distance / mm * 10; // the real distance represented by 1cm

        int wayWidth1 = 22;
        int wayWidth2 = 8;
        int wayWidth3 = 4;
        if (ruler >= 100){
            wayWidth1 = 8;
            wayWidth2 = 6;
            wayWidth3 = 2;
        }

        String highwayValue = OSMUtils.sharedInstance().tagValue(tags, "highway");

        Graphics2D g1 = (Graphics2D) g.create();

        BasicStroke edgeStroke = null;
        BasicStroke innerStroke = null;
        Color edgeClr = null;
        Color innerClr = null;

        if (highwayValue.compareToIgnoreCase("motorway") == 0){
            edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(221, 41, 108);
            innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr = new Color(232, 143, 161);
        }
        else if (highwayValue.compareToIgnoreCase("trunk") == 0){
            edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(203, 81, 52);
            innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(249, 177, 156);
        }
        else if (highwayValue.compareToIgnoreCase("primary") == 0){
            edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(173, 123, 26);
            innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(252, 214, 164);
        }
        else if (highwayValue.compareToIgnoreCase("secondary") == 0){
            edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(124, 137, 23);
            innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(246, 250, 190);
        }
        else if (highwayValue.compareToIgnoreCase("cycleway") == 0 || highwayValue.compareToIgnoreCase("footway") == 0){
            edgeStroke = null;
            edgeClr = null;
            innerStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{9}, 0);
            innerClr = new Color(123, 121, 247);
        }
        else if (highwayValue.compareToIgnoreCase("living_street") == 0){
            edgeStroke = null;
            edgeClr = null;
            innerStroke = new BasicStroke(wayWidth3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(255, 255, 255);
        }
        else if (highwayValue.compareToIgnoreCase("tertiary") == 0){
            edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(173, 173, 173);
            innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(254, 254, 254);
        }
        else{
            edgeStroke = new BasicStroke(wayWidth2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(173, 173, 173);
            innerStroke = new BasicStroke(wayWidth2 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(254, 254, 254);
        }

        if (layer == 0){
            if (edgeStroke != null && edgeClr != null){
                g1.setStroke(edgeStroke);
                g1.setColor(edgeClr);

                GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());

                GraphicsPoint p0 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(0), boundingBox, imageWidth, imageHeight);
                polyline.moveTo(p0.x, p0.y);
                for (int i = 1; i < points.size(); i++){
                    GraphicsPoint p = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
                    polyline.lineTo(p.x, p.y);
                }

                g1.draw(polyline);
            }
        }
        else if (layer == 1){

            if (innerStroke != null && innerClr != null){
                g1.setStroke(innerStroke);
                g1.setColor(innerClr);
                GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
                GraphicsPoint p0 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(0), boundingBox, imageWidth, imageHeight);
                polyline.moveTo(p0.x, p0.y);
                for (int i = 1; i < points.size(); i++){
                    GraphicsPoint p = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);

                    polyline.lineTo(p.x, p.y);
                }
                g1.draw(polyline);
            }
        }

        // for debug
//        g1.setColor(Color.red);
//        g1.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
//        for (int i = 0; i < points.size(); i++){
//            GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
//
//            drawCenteredCircle(g1, p1.x, p1.y, wayWidth1 / 2);
//        }
    }

    public void drawCenteredCircle(Graphics2D g, int x, int y, int r) {
        x = x-(r/2);
        y = y-(r/2);
        g.fillOval(x,y,r,r);
    }

    public void drawHighwayLink(List<GeomPoint> points, List<Pair<String, String>> tags,
                                GeomBox boundingBox, int imageWidth, int imageHeight,
                                Graphics2D g, int layer){
        double distance = OSMUtils.distance(boundingBox.minlat, boundingBox.minlat,
                boundingBox.minlon, boundingBox.maxlon,
                0, 0); // distance in meters
        double pixels = imageWidth;
        double mm = OSMUtils.millimetersFromPixels(pixels);
        double ruler = distance / mm * 10; // the real distance represented by 1cm

        int wayWidth1 = 16;
        int wayWidth2 = 8;
        int wayWidth3 = 4;
        if (ruler >= 100){
            wayWidth1 = 8;
            wayWidth2 = 6;
            wayWidth3 = 2;
        }

        String highwayValue = OSMUtils.sharedInstance().tagValue(tags, "highway");

        Graphics2D g1 = (Graphics2D) g.create();

        BasicStroke edgeStroke = null;
        BasicStroke innerStroke = null;
        Color edgeClr = null;
        Color innerClr = null;

        if (highwayValue.compareToIgnoreCase("motorway_link") == 0){
            edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(221, 41, 108);
            innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr = new Color(232, 143, 161);
        }
        else if (highwayValue.compareToIgnoreCase("trunk_link") == 0){
            edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(203, 81, 52);
            innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(249, 177, 156);
        }
        else if (highwayValue.compareToIgnoreCase("primary_link") == 0){
            edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(173, 123, 26);
            innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(252, 214, 164);
        }
        else if (highwayValue.compareToIgnoreCase("secondary_link") == 0){
            edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(124, 137, 23);
            innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(246, 250, 190);
        }
        else{
            edgeStroke = new BasicStroke(wayWidth2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = new Color(173, 173, 173);
            innerStroke = new BasicStroke(wayWidth2 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr =  new Color(254, 254, 254);
        }

        if (layer == 0){
            if (edgeStroke != null && edgeClr != null){
                g1.setStroke(edgeStroke);
                g1.setColor(edgeClr);

                GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());

                GraphicsPoint p0 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(0), boundingBox, imageWidth, imageHeight);
                polyline.moveTo(p0.x, p0.y);
                for (int i = 1; i < points.size(); i++){
                    GraphicsPoint p = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
                    polyline.lineTo(p.x, p.y);
                }

                g1.draw(polyline);
            }
        }
        else if (layer == 1){
            if (innerStroke != null && innerClr != null){
                g1.setStroke(innerStroke);
                g1.setColor(innerClr);
                GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
                GraphicsPoint p0 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(0), boundingBox, imageWidth, imageHeight);
                polyline.moveTo(p0.x, p0.y);
                for (int i = 1; i < points.size(); i++){
                    GraphicsPoint p = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);

                    polyline.lineTo(p.x, p.y);
                }
                g1.draw(polyline);
            }
        }

        // for debug
//        g1.setColor(Color.red);
//        g1.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
//        for (int i = 0; i < points.size(); i++){
//            GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
//
//            drawCenteredCircle(g1, p1.x, p1.y, 10);
//        }
    }


    public void drawRail(List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g, int layer){

        String railValue = OSMUtils.sharedInstance().tagValue(tags, "railway");
        if (railValue != null && railValue.compareToIgnoreCase("subway") == 0){
            Graphics2D g1 = (Graphics2D) g.create();

            BasicStroke edgeStroke = null;
            BasicStroke innerStroke = null;
            Color edgeClr = null;
            Color innerClr = null;

            edgeStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = Color.black;
            innerStroke = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr = new Color(153, 153, 153);

            if (layer == 0){
                if (edgeStroke != null && edgeClr != null){
                    g1.setStroke(edgeStroke);
                    g1.setColor(edgeClr);

                    GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());

                    GraphicsPoint p0 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(0), boundingBox, imageWidth, imageHeight);
                    polyline.moveTo(p0.x, p0.y);
                    for (int i = 1; i < points.size(); i++){
                        GraphicsPoint p = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
                        polyline.lineTo(p.x, p.y);
                    }

                    g1.draw(polyline);
                }
            }
            else if (layer == 1){
                if (innerStroke != null && innerClr != null){
                    g1.setStroke(innerStroke);
                    g1.setColor(innerClr);
                    GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
                    GraphicsPoint p0 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(0), boundingBox, imageWidth, imageHeight);
                    polyline.moveTo(p0.x, p0.y);
                    for (int i = 1; i < points.size(); i++){
                        GraphicsPoint p = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);

                        polyline.lineTo(p.x, p.y);
                    }
                    g1.draw(polyline);
                }
            }
        }
    }

    public void drawBoundary(List<GeomPoint> points, List<Pair<String, String>> tags,
                                GeomBox boundingBox, int imageWidth, int imageHeight,
                                Graphics2D g){
        Graphics2D g1 = (Graphics2D) g.create();

        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{ 21.0f, 9.0f, 3.0f, 9.0f }, 0);
        g1.setStroke(dashed);
        g1.setColor(new Color(206, 154, 202));

        GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
        GraphicsPoint p0 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(0), boundingBox, imageWidth, imageHeight);
        polyline.moveTo(p0.x, p0.y);
        for (int i = 1; i < points.size(); i++){
            GraphicsPoint p = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);

            polyline.lineTo(p.x, p.y);
        }
        g1.draw(polyline);
    }

    public void drawOther(List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g){
        Graphics2D g1 = (Graphics2D)g.create();

        g1.setColor(Color.white);

        GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
        GraphicsPoint p0 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(0), boundingBox, imageWidth, imageHeight);
        polyline.moveTo(p0.x, p0.y);
        for (int i = 1; i < points.size(); i++){
            GraphicsPoint p = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);

            polyline.lineTo(p.x, p.y);
        }
        g1.draw(polyline);
    }

    public void drawBuilding(List<GeomPoint> points, List<Pair<String, String>> tags,
                             GeomBox boundingBox, int imageWidth, int imageHeight,
                             Graphics2D g){
        Graphics2D g1 = (Graphics2D)g.create();

        Polygon p=new Polygon();

        for (int i = 0; i < points.size(); i++){
            GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            p.addPoint(p1.x, p1.y);
        }

        g1.setColor(new Color(216, 208, 197));
        g1.fillPolygon(p);

        g1.setColor(new Color(198, 186, 177));
        g1.draw(p);
    }

    public void drawWater(List<GeomPoint> points, List<Pair<String, String>> tags,
                         GeomBox boundingBox, int imageWidth, int imageHeight,
                         Graphics2D g){

        Graphics2D g1 = (Graphics2D) g.create();

        g1.setColor(new Color(181, 208, 208));

        String waterValue = OSMUtils.sharedInstance().tagValue(tags, "waterway");
        if (waterValue != null && waterValue.compareToIgnoreCase("riverbank") == 0){
            Polygon p=new Polygon();

            for (int i = 0; i < points.size(); i++){
                GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
                p.addPoint(p1.x, p1.y);
            }

            g1.fillPolygon(p);
        }else{
            BasicStroke wideStroke = new BasicStroke(5.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            g1.setStroke(wideStroke);

            GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
            GraphicsPoint p0 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(0), boundingBox, imageWidth, imageHeight);
            polyline.moveTo(p0.x, p0.y);
            for (int i = 1; i < points.size(); i++){
                GraphicsPoint p = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);

                polyline.lineTo(p.x, p.y);
            }
            g1.draw(polyline);
        }
    }

    public void drawLand(List<GeomPoint> points, List<Pair<String, String>> tags,
                             GeomBox boundingBox, int imageWidth, int imageHeight,
                             Graphics2D g){
        Graphics2D g1 = (Graphics2D)g.create();

        Polygon p=new Polygon();

        for (int i = 0; i < points.size(); i++){
            GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            p.addPoint(p1.x, p1.y);
        }

        if (OSMUtils.sharedInstance().isLeisure(tags)){
            g1.setColor(new Color(97, 240, 19)); // green
        }else if (OSMUtils.sharedInstance().isAmenity(tags)){
            g1.setColor(new Color(246, 249, 190)); // light green
        } else {
            String landValue = OSMUtils.sharedInstance().tagValue(tags, "landuse");
            if (landValue != null &&
                    (landValue.compareToIgnoreCase("basin") == 0 || landValue.compareToIgnoreCase("reservoir") == 0)){
                g1.setColor(new Color(181, 208, 208)); // must be same with waterway
            } else {
                g1.setColor(new Color(224, 222, 222)); // grey
            }
        }
        g1.fillPolygon(p);

        Stroke landEdge = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        g1.setColor(new Color(180, 180, 178));
        g1.draw(p);
    }

    public void drawNatural(List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g){
        Graphics2D g1 = (Graphics2D)g.create();

        Polygon p=new Polygon();

        for (int i = 0; i < points.size(); i++){
            GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            p.addPoint(p1.x, p1.y);
        }

        String naturalValue = OSMUtils.sharedInstance().tagValue(tags, "natural");
        if (naturalValue != null && naturalValue.compareToIgnoreCase("water") == 0){
            g1.setColor(new Color(181, 208, 208)); // must be same with waterway
        } else {
            g1.setColor(new Color(199, 228, 182));
        }

        g1.fillPolygon(p);
    }


    public void drawTags(List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g,
                            TagDrawer tagDrawer){
        tagDrawer.drawTag(tags, points, boundingBox, imageWidth, imageHeight, g);
    }
}
