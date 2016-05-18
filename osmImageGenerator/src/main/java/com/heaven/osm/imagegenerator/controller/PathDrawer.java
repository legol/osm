package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.GeomBox;
import com.heaven.osm.imagegenerator.model.GeomPoint;
import com.heaven.osm.imagegenerator.model.GeomPointOfWay;
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
        List<GeomPointOfWay> points = PathFinder.sharedInstance().searchPath(nodeFrom, nodeTo);

    }
}
