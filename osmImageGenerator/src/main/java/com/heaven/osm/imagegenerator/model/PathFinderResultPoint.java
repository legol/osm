package com.heaven.osm.imagegenerator.model;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;

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
