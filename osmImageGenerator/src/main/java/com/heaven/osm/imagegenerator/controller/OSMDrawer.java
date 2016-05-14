package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.GeomBox;
import com.heaven.osm.imagegenerator.model.GeomPoint;
import com.heaven.osm.imagegenerator.model.GraphicsPoint;
import com.heaven.osm.imagegenerator.model.PostgresqlAdapter;
import com.sun.corba.se.impl.orbutil.graph.Graph;
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

        List<Long> drawnWays = new LinkedList<Long>(); // ways that have been drawn.

        List<Long> relations = PostgresqlAdapter.sharedInstance().getTopLevelRelationsByBoundingBox(boundingBox);
        for (int i = 0; i < relations.size(); i++){
            drawRelation(relations.get(i).longValue(), boundingBox, imageWidth, imageHeight, g, drawnWays);
        }

        List<Long> ways = PostgresqlAdapter.sharedInstance().getWaysByBoundingBox(boundingBox);
        for (int i = 0; i < ways.size(); i++){
            if (!drawnWays.contains(ways.get(i).longValue())){
                drawWay(ways.get(i).longValue(), boundingBox, imageWidth, imageHeight, g, drawnWays);
            }
        }

        return;
    }

    public void drawRelation(long relation,
                             GeomBox boundingBox, int imageWidth, int imageHeight,
                             Graphics2D g,
                             List<Long>drawnWays){

    }

    public GraphicsPoint GeomPoint2GraphicsPoint(GeomPoint geomPoint, GeomBox boundingBox, int imageWidth, int imageHeight){
        GraphicsPoint point = new GraphicsPoint();

        point.x = (int)Math.round(imageWidth * (geomPoint.longitude - boundingBox.minlon) / (boundingBox.maxlon - boundingBox.minlon));
        point.y = (int)Math.round(imageHeight - imageHeight * (geomPoint.latitude - boundingBox.minlat) / (boundingBox.maxlat - boundingBox.minlat));

        return point;
    }

    public void drawWay(long way,
                        GeomBox boundingBox, int imageWidth, int imageHeight,
                        Graphics2D g,
                        List<Long>drawnWays){
        List<GeomPoint> points = PostgresqlAdapter.sharedInstance().getPointsOfWay(way);

        double deltaLongitude = boundingBox.maxlon - boundingBox.minlon;
        double deltaLatitude = boundingBox.maxlat - boundingBox.minlat;
        for (int i = 0; i + 1 < points.size(); i++){
            GraphicsPoint p1 = GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            GraphicsPoint p2 = GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        drawnWays.add(way);
    }
}
