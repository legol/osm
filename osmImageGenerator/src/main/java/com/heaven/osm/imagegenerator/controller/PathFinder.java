package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.GeomPoint;
import com.heaven.osm.imagegenerator.model.GeomPointOfWay;
import com.heaven.osm.imagegenerator.model.PathFinderPoint;
import com.heaven.osm.imagegenerator.model.PostgresqlAdapter;
import javafx.scene.shape.Path;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by chenjie3 on 2016/5/18.
 */
public class PathFinder {
    private static final Logger LOGGER = Logger.getLogger(PathFinder.class);

    static class PQComp implements Comparator<PathFinderPoint> {

        public int compare(PathFinderPoint one, PathFinderPoint two) {
            return (int)(two.f - one.f);
        }
    }

    private static PathFinder instance = null;
    public static PathFinder sharedInstance() {
        if (instance == null) {
            instance = new PathFinder();
        }
        return instance;
    }

    List<GeomPointOfWay> searchPath(long nodeFrom, long nodeTo){
        // A* search. See https://en.wikipedia.org/wiki/A*_search_algorithm
        Set<Long> openSet = new HashSet<Long>();

        PQComp pqcomp = new PQComp();
        PriorityQueue<PathFinderPoint> openSetPriorityQ = new PriorityQueue<PathFinderPoint>(500000, pqcomp);

        Set<Long> closedSet = new HashSet<Long>();

        openSet.add(nodeFrom);
        PathFinderPoint pnodeFrom = new PathFinderPoint();
        pnodeFrom.geoPoint = PostgresqlAdapter.sharedInstance().getPoint(nodeFrom);
        pnodeFrom.g = 0; // distance from start point.

        

        return null;
    }
}
