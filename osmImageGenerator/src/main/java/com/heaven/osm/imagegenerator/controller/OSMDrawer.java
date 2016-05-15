package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.*;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.geometry.Pos;
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
        Map<String, LinkedList<GraphicsLayerElement>> layers = new HashMap<String, LinkedList<GraphicsLayerElement>>();
        layers.put("layer_land", new LinkedList<GraphicsLayerElement>());
        layers.put("layer_building", new LinkedList<GraphicsLayerElement>());
        layers.put("layer_path", new LinkedList<GraphicsLayerElement>());
        layers.put("layer_tag", new LinkedList<GraphicsLayerElement>());

        List<Long> ways = PostgresqlAdapter.sharedInstance().getWaysByBoundingBox(boundingBox);
        for (int i = 0; i < ways.size(); i++){
            drawWay(ways.get(i).longValue(),
                    boundingBox, imageWidth, imageHeight,
                    g,
                    layers);
        }

//        g.setColor(Color.blue);
//        List<Long> relations = PostgresqlAdapter.sharedInstance().getTopLevelRelationsByBoundingBox(boundingBox);
//        for (int i = 0; i < relations.size(); i++){
//            drawRelation(relations.get(i).longValue(),
//                    boundingBox, imageWidth, imageHeight,
//                    g,
//                    layers);
//        }

        drawLayers(layers, boundingBox, imageWidth, imageHeight, g);
    }

    public void drawLayers(Map<String, LinkedList<GraphicsLayerElement>> layers,
                           GeomBox boundingBox, int imageWidth, int imageHeight, Graphics2D g){

        // draw background
        g.setColor(new Color(241, 238, 233));
        g.fillRect(0, 0, imageWidth, imageHeight);

        List<GraphicsLayerElement> elements;

        elements = layers.get("layer_land");
        for (int i = 0; i < elements.size(); i++){
            drawLand(elements.get(i).points, elements.get(i).tags,
                    boundingBox, imageWidth, imageHeight,
                    g);
        }

        elements = layers.get("layer_building");
        for (int i = 0; i < elements.size(); i++){
            drawBuilding(elements.get(i).points, elements.get(i).tags,
                    boundingBox, imageWidth, imageHeight,
                    g);
        }

        elements = layers.get("layer_path");
        for (int i = 0; i < elements.size(); i++){
            drawPath(elements.get(i).points, elements.get(i).tags,
                    boundingBox, imageWidth, imageHeight,
                    g);
        }

        elements = layers.get("layer_tag");
        for (int i = 0; i < elements.size(); i++){
            drawTags(elements.get(i).points, elements.get(i).tags,
                        boundingBox, imageWidth, imageHeight,
                        g);
        }
    }

    public void drawRelation(long relation,
                             GeomBox boundingBox, int imageWidth, int imageHeight,
                             Graphics2D g,
                             Map<String, LinkedList<GraphicsLayerElement>> layers){
        List<Long> relations = PostgresqlAdapter.sharedInstance().getRelationsOfRelation(relation);
        for (int i = 0; i < relations.size(); i++){
            drawRelation(relations.get(i).longValue(),
                    boundingBox, imageWidth, imageHeight,
                    g,
                    layers);
        }

        List<Long> ways = PostgresqlAdapter.sharedInstance().getWaysOfRelation(relation);
        for (int i = 0; i < ways.size(); i++){
            drawWay(ways.get(i).longValue(),
                    boundingBox, imageWidth, imageHeight,
                    g,
                    layers);
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

    public boolean isHighway(List<Pair<String, String>> tags){
        return isTag(tags, "highway");
    }

    public boolean isLeisure(List<Pair<String, String>> tags){
        return isTag(tags, "leisure");
    }

    public boolean isAmenity(List<Pair<String, String>> tags){
        return isTag(tags, "amenity");
    }

    public boolean isTag(List<Pair<String, String>> tags, String type){
        for (int i = 0; i < tags.size(); i++){
            if (tags.get(i).getKey().compareToIgnoreCase(type) == 0){
                return true;
            }
        }
        return false;
    }

    public void drawWay(long way,
                        GeomBox boundingBox, int imageWidth, int imageHeight,
                        Graphics2D g,
                        Map<String, LinkedList<GraphicsLayerElement>> layers){
        List<GeomPoint> points = PostgresqlAdapter.sharedInstance().getPointsOfWay(way);
        List<Pair<String, String>> tags = PostgresqlAdapter.sharedInstance().getTags("way", way);

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
        else{
            layers.get("layer_path").add(element);

//            if (isHighway(tags)){
//                layers.get("layer_tag").add(element);
//            }
        }
    }

    public void drawPath(List<GeomPoint> points, List<Pair<String, String>> tags,
                         GeomBox boundingBox, int imageWidth, int imageHeight,
                         Graphics2D g){

        if (isHighway(tags)){
            g.setColor(Color.red);
        }else{
            g.setColor(Color.white);
        }

        double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
        double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;

        for (int i = 0; i + 1 < points.size(); i++){
            GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            GraphicsPoint p2 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    public void drawBuilding(List<GeomPoint> points, List<Pair<String, String>> tags,
                             GeomBox boundingBox, int imageWidth, int imageHeight,
                             Graphics2D g){
        double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
        double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;

        Polygon p=new Polygon();

        for (int i = 0; i < points.size(); i++){
            GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            p.addPoint(p1.x, p1.y);
        }

        g.setColor(Color.black);
        g.draw(p);

        g.setColor(new Color(216, 208, 197));
        g.fillPolygon(p);
    }

    public void drawLand(List<GeomPoint> points, List<Pair<String, String>> tags,
                             GeomBox boundingBox, int imageWidth, int imageHeight,
                             Graphics2D g){
        double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
        double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;

        Polygon p=new Polygon();

        for (int i = 0; i < points.size(); i++){
            GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            p.addPoint(p1.x, p1.y);
        }

        g.setColor(new Color(205, 205, 205));
        g.draw(p);

        if (isLeisure(tags)){
            g.setColor(new Color(97, 240, 19)); // green
        }else if (isAmenity(tags)){
            g.setColor(new Color(246, 249, 190)); // light green
        } else{
            g.setColor(new Color(199, 199, 199)); // grey
        }

        g.fillPolygon(p);
    }

    public void drawTags(List<GeomPoint> points, List<Pair<String, String>> tags,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g){

        g.setColor(Color.black);
        for (int i = 0; i < tags.size(); i++){
            if(tags.get(i).getKey().compareToIgnoreCase("name") == 0){
                GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(0), boundingBox, imageWidth, imageHeight);

                if (p1.x > imageWidth || p1.y > imageHeight || p1.x < 0 || p1.y < 0){
                    break;
                }

                g.drawString(tags.get(i).getValue(), p1.x, p1.y);
            }
        }
    }
}
