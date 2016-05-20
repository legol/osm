package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.PathFinderResultPoint;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;

/**
 * Created by chenjie3 on 2016/5/20.
 */
public interface PathDrawerObserver {
    public void onProgress(Set<PathFinderResultPoint> openSet, Set<PathFinderResultPoint> closedSet, BufferedImage img);
    public void onCompleted(Set<PathFinderResultPoint> openSet, Set<PathFinderResultPoint> closedSet, List<PathFinderResultPoint> path, BufferedImage img);
}
