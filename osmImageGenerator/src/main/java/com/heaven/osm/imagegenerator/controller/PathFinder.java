package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.GeomPoint;
import com.heaven.osm.imagegenerator.model.GeomPointOfWay;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by chenjie3 on 2016/5/18.
 */
public class PathFinder {
    private static final Logger LOGGER = Logger.getLogger(PathFinder.class);

    private static PathFinder instance = null;

    public static PathFinder sharedInstance() {
        if (instance == null) {
            instance = new PathFinder();
        }
        return instance;
    }

    List<GeomPointOfWay> searchPath(long nodeFrom, long nodeTo){
        return null;
    }
}
