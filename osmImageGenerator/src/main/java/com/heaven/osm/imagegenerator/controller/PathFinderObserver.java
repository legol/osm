package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.PathFinderResultPoint;

import java.util.List;
import java.util.Set;

/**
 * Created by chenjie3 on 2016/5/20.
 */
public interface PathFinderObserver {
    public void onProgress(Set<PathFinderResultPoint> openSet, Set<PathFinderResultPoint> closedSet);
    public void onCompleted(Set<PathFinderResultPoint> openSet, Set<PathFinderResultPoint> closedSet, List<PathFinderResultPoint> path);
}