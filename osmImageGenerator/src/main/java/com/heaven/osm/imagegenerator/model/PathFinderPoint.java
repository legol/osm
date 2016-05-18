package com.heaven.osm.imagegenerator.model;

/**
 * Created by chenjie3 on 2016/5/18.
 */
public class PathFinderPoint {
    public GeomPoint geoPoint;
    public double f; // f = g + h
    public double g; // distance from the start point

    public PathFinderPoint() {
        geoPoint = new GeomPoint();
        f = Double.MAX_VALUE;
        g = Double.MAX_VALUE;
    }
}
