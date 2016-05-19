package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.GeomPoint;
import com.heaven.osm.imagegenerator.model.PathFinderResultPoint;
import com.heaven.osm.imagegenerator.model.PostgresqlAdapter;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

class PathFinderPoint {
    public GeomPoint geoPoint;
    public double f; // f = g + h
    public double g; // distance from the start point
    public Set<Pair<Long, Long>> neighbors; // through which way to get to the neighbor. key=nd_ref, value=way_ref

    public PathFinderPoint() {
        geoPoint = new GeomPoint();
        f = Double.MAX_VALUE;
        g = Double.MAX_VALUE;
        neighbors = new HashSet<Pair<Long, Long>>();
    }
}

class NodeInfoMap{
    private static final Logger LOGGER = Logger.getLogger(NodeInfoMap.class);

    public HashMap<Long, PathFinderPoint> nodeInfoMap = new HashMap<Long, PathFinderPoint>();

    public PathFinderPoint getPoint(long nodeRef){
        return nodeInfoMap.get(nodeRef);
    }

    public Set<Pair<Long, Long>> getNeighbors(long nodeId){
        PathFinderPoint point = nodeInfoMap.get(nodeId);
        if (point == null){
            LOGGER.error("trying to get neighbors of a non exist node");
            return null;
        }

        return point.neighbors;
    }

    public void add(long nodeId){
        if (nodeInfoMap.containsKey(nodeId)){
            return;
        }

        PathFinderPoint point = new PathFinderPoint();
        point.geoPoint = PostgresqlAdapter.sharedInstance().getPoint(nodeId);
        point.f = Double.MAX_VALUE;
        point.g = Double.MAX_VALUE;
        point.neighbors = PostgresqlAdapter.sharedInstance().getNeighbors(nodeId);

        nodeInfoMap.put(nodeId, point);
    }

    public double FScore(long nodeId){
        PathFinderPoint point = nodeInfoMap.get(nodeId);
        if (point == null){
            LOGGER.error("trying to get F score of a non exist node");
            return Double.MAX_VALUE;
        }

        return point.f;
    }

    public double GScore(long nodeId){
        PathFinderPoint point = nodeInfoMap.get(nodeId);
        if (point == null){
            LOGGER.error("trying to get G score of a non exist node");
            return Double.MAX_VALUE;
        }

        return point.g;
    }

    public void setFScore(long nodeId, double f){
        PathFinderPoint point = nodeInfoMap.get(nodeId);
        if (point == null){
            LOGGER.error("trying to set F score of a non exist node");
            return;
        }

        point.f = f;
    }



    public void setGScore(long nodeId, double g){
        PathFinderPoint point = nodeInfoMap.get(nodeId);
        if (point == null){
            LOGGER.error("trying to set G score of a non exist node");
            return;
        }

        point.f = g;
    }

    public GeomPoint getGeomInfo(long nodeId){
        PathFinderPoint point = nodeInfoMap.get(nodeId);
        if (point == null){
            LOGGER.error("trying to get geometry info of a non exist node");
            return null;
        }

        return point.geoPoint;
    }

    public void clear(){
        nodeInfoMap.clear();
    }
}

// a set and a priority queue
class OpenSet{
    private static final Logger LOGGER = Logger.getLogger(OpenSet.class);

    public Set<Long> set;

    public OpenSet(){
        set = new HashSet<Long>();
    }

    public boolean isEmpty(){
        return set.isEmpty();
    }

    public void add(long nodeId){
        PathFinder.sharedInstance().nodeInfoMap.add(nodeId);
        set.add(nodeId);
    }

    public boolean contains(long nodeId){
        return set.contains(nodeId);
    }

    public long lowestFValuePoint(){
        double minF = Double.MAX_VALUE;
        long resultNodeRef = 0;

        for (long nodeRef : set) {
            double currentF = PathFinder.sharedInstance().nodeInfoMap.FScore(nodeRef);
            if (currentF <= minF){
                minF = currentF;
                resultNodeRef = nodeRef;
            }
        }

        return resultNodeRef;
    }

    public void remove(long nodeRef){
        set.remove(nodeRef);
    }
}

// a set
class ClosedSet {
    private static final Logger LOGGER = Logger.getLogger(ClosedSet.class);

    public Set<Long> set = new HashSet<Long>();

    public void add(long nodeId){
        PathFinder.sharedInstance().nodeInfoMap.add(nodeId);

        set.add(nodeId);
    }

    public void remove(long nodeId){
        set.remove(nodeId);
    }

    public boolean contains(long nodeId){
        return set.contains(nodeId);
    }
}

// a map
class CameFrom{
    private static final Logger LOGGER = Logger.getLogger(CameFrom.class);

    public HashMap<Long, Pair<Long, Long>> cameFrom = new HashMap<Long, Pair<Long, Long>>();

    public void set(long nodeId, long cameFromNodeId, long way_ref){
        cameFrom.put(nodeId, new Pair<Long, Long>(cameFromNodeId, way_ref));
    }

    // nd_ref, way_ref
    public Pair<Long, Long> getCameFrom(long nodeRef){
        return cameFrom.get(nodeRef);
    }
}

/**
 * Created by chenjie3 on 2016/5/18.
 */
public class PathFinder {
    private static final Logger LOGGER = Logger.getLogger(PathFinder.class);

    private static PathFinder instance = null;

    NodeInfoMap nodeInfoMap = new NodeInfoMap();

    public static PathFinder sharedInstance() {
        if (instance == null) {
            instance = new PathFinder();
        }
        return instance;
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

    double distance(long nodeFrom, long nodeTo){
        GeomPoint pointFrom = nodeInfoMap.getGeomInfo(nodeFrom);
        GeomPoint pointTo = nodeInfoMap.getGeomInfo(nodeTo);

        return distance(pointFrom.latitude, pointTo.latitude, pointFrom.longitude, pointTo.longitude, 0, 0);
    }

    double heuristicCostEstimation(long nodeFrom, long nodeTo){
        return distance(nodeFrom, nodeTo);
    }

    // A* search. See https://en.wikipedia.org/wiki/A*_search_algorithm
    List<PathFinderResultPoint> searchPath(long nodeFrom, long nodeTo){
        nodeInfoMap.clear();
        nodeInfoMap.add(nodeFrom);
        nodeInfoMap.add(nodeTo);

        // The set of currently discovered nodes still to be evaluated.
        // Initially, only the start node is known.
        OpenSet openSet = new OpenSet();

        // The set of nodes already evaluated.
        ClosedSet closedSet = new ClosedSet();

        // For each node, which node it can most efficiently be reached from.
        // If a node can be reached from many nodes, cameFrom will eventually contain the
        // most efficient previous step.
        CameFrom cameFrom = new CameFrom();

        openSet.add(nodeFrom);

        nodeInfoMap.setGScore(nodeFrom, 0);// The cost of going from start to start is zero.
        nodeInfoMap.setFScore(nodeFrom, heuristicCostEstimation(nodeFrom, nodeTo));// For the first node, that value is completely heuristic.

        while (!openSet.isEmpty()){
            long nodeCurrent = openSet.lowestFValuePoint();
            if (nodeCurrent == nodeTo){
                return constructPath(cameFrom, nodeCurrent, nodeFrom);
            }

            openSet.remove(nodeCurrent); // will remove nodeCurrent
            closedSet.add(nodeCurrent);

            //for each neighbor of current
            Set<Pair<Long, Long>> neighbors = nodeInfoMap.getNeighbors(nodeCurrent);
            for (Pair<Long, Long> neighbor: neighbors) {
                if (closedSet.contains(neighbor.getKey())){
                    continue; // Ignore the neighbor which is already evaluated.
                }

                nodeInfoMap.add(neighbor.getKey());

                // The distance from start to a neighbor
                double tentativeGScore = nodeInfoMap.GScore(nodeCurrent) + distance(nodeCurrent, neighbor.getKey());
                if (!openSet.contains(neighbor.getKey())){
                    openSet.add(neighbor.getKey());
                }
                else{
                    if (tentativeGScore >= nodeInfoMap.GScore(neighbor.getKey())){
                        continue; // This is not a better path.
                    }
                }

                // This path is the best until now. Record it!
                cameFrom.set(neighbor.getKey(), nodeCurrent, neighbor.getValue());
                nodeInfoMap.setGScore(neighbor.getKey(), tentativeGScore);
                nodeInfoMap.setFScore(neighbor.getKey(), tentativeGScore + heuristicCostEstimation(neighbor.getKey(), nodeTo));
            }
        }

        LOGGER.error(String.format("can't find a path from %d to %d", nodeFrom, nodeTo));
        return null;
    }

    public List<PathFinderResultPoint> constructPath(CameFrom cameFrom, long lastNodeRef, long firstNodeRef){
        List<PathFinderResultPoint> path = new LinkedList<PathFinderResultPoint>();
        Deque<PathFinderResultPoint> pathReversed = new ConcurrentLinkedDeque<PathFinderResultPoint>();

        long currentNodeRef = lastNodeRef;

        GeomPoint geomPoint = nodeInfoMap.getGeomInfo(currentNodeRef);
        pathReversed.addFirst(new PathFinderResultPoint(geomPoint, 0));
        while (currentNodeRef != firstNodeRef){

            Pair<Long, Long> cameFromNodeAndWay = cameFrom.getCameFrom(currentNodeRef);
            currentNodeRef = cameFromNodeAndWay.getKey();

            geomPoint = nodeInfoMap.getGeomInfo(currentNodeRef);
            pathReversed.addFirst(new PathFinderResultPoint(geomPoint, cameFromNodeAndWay.getValue()));
        }

        Iterator<PathFinderResultPoint> itr = pathReversed.iterator();
        while (itr.hasNext()){
            path.add(itr.next());
        }

        return path;
    }
}
