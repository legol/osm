package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.*;
import com.heaven.osm.imagegenerator.model.style.*;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.awt.*;
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

        double scale = OSMUtils.calcScale(boundingBox.minlon, boundingBox.maxlon, boundingBox.minlat, imageWidth);
        int lod = LevelOfDetailController.sharedInstance().determinLod(scale);

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
            categorizeWay(ways.get(i).longValue(),
                        boundingBox, imageWidth, imageHeight,
                        allLayers);
        }

        // real drawing goes here
        drawLayers(lod, allLayers, boundingBox, imageWidth, imageHeight, g);
    }

    public void drawLayers(int lod, LinkedList<HashMap<String, LinkedList<GraphicsLayerElement>>> allLayers,
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
                drawLand(lod, elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }

            elements = layers.get("layer_natural");
            for (int i = 0; i < elements.size(); i++){
                drawNatural(lod, elements.get(i).points, elements.get(i).tags,
                            boundingBox, imageWidth, imageHeight,
                            g);
            }

            elements = layers.get("layer_building");
            for (int i = 0; i < elements.size(); i++){
                drawBuilding(lod, elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }

            elements = layers.get("layer_water");
            for (int i = 0; i < elements.size(); i++){
                drawWater(lod, elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }

            // highwayline_edge -> highway_edge -> highwayline_inner -> highway_inner
            elements = layers.get("layer_highway_link");
            for (int i = 0; i < elements.size(); i++){
                drawHighwayLink(lod, elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g, 0);
            }

            elements = layers.get("layer_highway");
            for (int i = 0; i < elements.size(); i++){
                drawHighway(lod, elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g, 0);
            }

            elements = layers.get("layer_highway_link");
            for (int i = 0; i < elements.size(); i++){
                drawHighwayLink(lod, elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g, 1);
            }

            elements = layers.get("layer_highway");
            for (int i = 0; i < elements.size(); i++){
                drawHighway(lod, elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g, 1);
            }

            elements = layers.get("layer_rail");
            for (int i = 0; i < elements.size(); i++){
                drawRail(lod, elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g, 0);
            }
            for (int i = 0; i < elements.size(); i++){
                drawRail(lod, elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g, 1);
            }

            elements = layers.get("layer_other");
            for (int i = 0; i < elements.size(); i++){
                drawOther(lod, elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
            }

            elements = layers.get("layer_boundary");
            for (int i = 0; i < elements.size(); i++){
                drawBoundary(lod, elements.get(i).points, elements.get(i).tags,
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
                drawTags(lod, elements.get(i).points, elements.get(i).tags,
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

    public void categorizeWay(long way,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
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

    public void drawHighway(int lod,
                            List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g, int layer){
        if(!LevelOfDetailController.sharedInstance().shouldDraw(lod, "highway", tags)){
            return;
        }

        String highwayValue = OSMUtils.sharedInstance().tagValue(tags, "highway");
        HighwayStyle highwayStyle = StyleBuilder.sharedInstance().createHighwayStyle(lod, highwayValue);

        if (layer == 0){
            if (highwayStyle.edgeStroke != null && highwayStyle.edgeClr != null){
                OSMUtils.sharedInstance().drawGeomPolyline(points, boundingBox, imageWidth, imageHeight, g, highwayStyle.edgeStroke, highwayStyle.edgeClr);
            }
        }
        else if (layer == 1){
            if (highwayStyle.innerStroke != null && highwayStyle.innerClr != null){
                OSMUtils.sharedInstance().drawGeomPolyline(points, boundingBox, imageWidth, imageHeight, g, highwayStyle.innerStroke, highwayStyle.innerClr);
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

    public void drawHighwayLink(int lod,
                                List<GeomPoint> points, List<Pair<String, String>> tags,
                                GeomBox boundingBox, int imageWidth, int imageHeight,
                                Graphics2D g, int layer){
        if(!LevelOfDetailController.sharedInstance().shouldDraw(lod, "highway_link", tags)){
            return;
        }

        String highwayValue = OSMUtils.sharedInstance().tagValue(tags, "highway");
        HighwayLinkStyle style = StyleBuilder.sharedInstance().createHighwayLinkStyle(lod, highwayValue);

        if (layer == 0){
            if (style.edgeStroke != null && style.edgeClr != null){
                OSMUtils.sharedInstance().drawGeomPolyline(points, boundingBox, imageWidth, imageHeight, g, style.edgeStroke, style.edgeClr);
            }
        }
        else if (layer == 1){
            if (style.innerStroke != null && style.innerClr != null){
                OSMUtils.sharedInstance().drawGeomPolyline(points, boundingBox, imageWidth, imageHeight, g, style.innerStroke, style.innerClr);
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


    public void drawRail(int lod,
                         List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g, int layer){
        if(!LevelOfDetailController.sharedInstance().shouldDraw(lod, "rail", tags)){
            return;
        }

        String railValue = OSMUtils.sharedInstance().tagValue(tags, "railway");
        if (railValue != null && railValue.compareToIgnoreCase("subway") == 0){

            RailStyle style = StyleBuilder.sharedInstance().createRailStyle(lod, railValue);
            if (layer == 0){
                if (style.edgeStroke != null && style.edgeClr != null){
                    OSMUtils.sharedInstance().drawGeomPolyline(points, boundingBox, imageWidth, imageHeight, g, style.edgeStroke, style.edgeClr);
                }
            }
            else if (layer == 1){
                if (style.innerStroke != null && style.innerClr != null){
                    OSMUtils.sharedInstance().drawGeomPolyline(points, boundingBox, imageWidth, imageHeight, g, style.innerStroke, style.innerClr);
                }
            }
        }
    }

    public void drawBoundary(int lod,
                             List<GeomPoint> points, List<Pair<String, String>> tags,
                                GeomBox boundingBox, int imageWidth, int imageHeight,
                                Graphics2D g){
        if(!LevelOfDetailController.sharedInstance().shouldDraw(lod, "boundary", tags)){
            return;
        }

        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{ 21.0f, 9.0f, 3.0f, 9.0f }, 0);
        OSMUtils.sharedInstance().drawGeomPolyline(points, boundingBox, imageWidth, imageHeight, g, dashed, new Color(206, 154, 202));
    }

    public void drawOther(int lod,
                          List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g){
        if(!LevelOfDetailController.sharedInstance().shouldDraw(lod, "other", tags)){
            return;
        }

        OSMUtils.sharedInstance().drawGeomPolyline(points, boundingBox, imageWidth, imageHeight, g, new BasicStroke(1), Color.lightGray);
    }

    public void drawBuilding(int lod,
                             List<GeomPoint> points, List<Pair<String, String>> tags,
                             GeomBox boundingBox, int imageWidth, int imageHeight,
                             Graphics2D g){
        if(!LevelOfDetailController.sharedInstance().shouldDraw(lod, "building", tags)){
            return;
        }

        BuildingStyle style = StyleBuilder.sharedInstance().createBuildingStyle(lod);
        OSMUtils.sharedInstance().drawGeomPolygon(points, boundingBox, imageWidth, imageHeight, g, style.edgeStroke, style.edgeClr, style.innerStroke, style.innerClr);
    }

    public void drawWater(int lod,
                          List<GeomPoint> points, List<Pair<String, String>> tags,
                         GeomBox boundingBox, int imageWidth, int imageHeight,
                         Graphics2D g){
        if(!LevelOfDetailController.sharedInstance().shouldDraw(lod, "water", tags)){
            return;
        }

        WaterStyle style = StyleBuilder.sharedInstance().createWaterStyle(lod);

        String waterValue = OSMUtils.sharedInstance().tagValue(tags, "waterway");
        if (waterValue != null && waterValue.compareToIgnoreCase("riverbank") == 0){
            OSMUtils.sharedInstance().drawGeomPolygon(points, boundingBox, imageWidth, imageHeight, g, style.edgeStroke, style.edgeClr, style.innerStroke, style.innerClr);
        }else{
            OSMUtils.sharedInstance().drawGeomPolyline(points, boundingBox, imageWidth, imageHeight, g, style.innerStroke, style.innerClr);
        }
    }

    public void drawLand(int lod,
                         List<GeomPoint> points, List<Pair<String, String>> tags,
                             GeomBox boundingBox, int imageWidth, int imageHeight,
                             Graphics2D g){
        if(!LevelOfDetailController.sharedInstance().shouldDraw(lod, "land", tags)){
            return;
        }

        LandStyle style = StyleBuilder.sharedInstance().createLandStyle(lod, tags);
        OSMUtils.sharedInstance().drawGeomPolygon(points, boundingBox, imageWidth, imageHeight, g, style.edgeStroke, style.edgeClr, style.innerStroke, style.innerClr);
    }

    public void drawNatural(int lod,
                            List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g){
        if(!LevelOfDetailController.sharedInstance().shouldDraw(lod, "natural", tags)){
            return;
        }

        NaturalStyle style = StyleBuilder.sharedInstance().createNaturalStyle(lod, tags);
        OSMUtils.sharedInstance().drawGeomPolygon(points, boundingBox, imageWidth, imageHeight, g, style.edgeStroke, style.edgeClr, style.innerStroke, style.innerClr);
    }


    public void drawTags(int lod,
                         List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g,
                            TagDrawer tagDrawer){
        tagDrawer.drawTag(tags, points, boundingBox, imageWidth, imageHeight, g);
    }
}
