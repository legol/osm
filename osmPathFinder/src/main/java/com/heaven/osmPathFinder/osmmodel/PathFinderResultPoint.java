package com.heaven.osmPathFinder.osmmodel;

/**
 * Created by chenjie3 on 2016/5/18.
 */
public class PathFinderResultPoint {
    public GeomPoint geoPoint;
    public long way_ref;

    public PathFinderResultPoint(GeomPoint _geomPoint, long _way_ref) {
        geoPoint = _geomPoint;
        way_ref = _way_ref;
    }
}
