package com.heaven.osmPathFinder.UI.Action;

import com.heaven.osmPathFinder.UI.DoubleImageLayerPanel;
import com.heaven.osmPathFinder.UI.GraphicsUtils;
import com.heaven.osmPathFinder.UI.TestData;
import com.heaven.osmPathFinder.osmcontroller.PathDrawer;
import com.heaven.osmPathFinder.osmcontroller.PathDrawerObserver;
import com.heaven.osmPathFinder.osmcontroller.PathFinderObserver;
import com.heaven.osmPathFinder.osmmodel.PathFinderResultPoint;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by chenjie3 on 2016/5/20.
 */
public class FindPathSbSAction implements ActionListener, PathDrawerObserver {

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(FindPathSbSAction.class));


    private DoubleImageLayerPanel mapPanel = null;

    public FindPathSbSAction(DoubleImageLayerPanel _mapPanel){
        mapPanel = _mapPanel;
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        mapPanel.imageOverlay = null;
        mapPanel.repaint();

        final PathDrawerObserver THIS = this;

        new Thread(new Runnable() {
            public void run() {
                PathDrawer.sharedInstance().drawPathSbS(TestData.sharedInstance().generateImageRequest.boundingBox,
                        TestData.sharedInstance().generateImageRequest.imageWidth, TestData.sharedInstance().generateImageRequest.imageHeight,
                        2498079525L, 2498079497L, THIS);
            }
        }).start();

    }

    @Override
    public void onProgress(Set<PathFinderResultPoint> openSet, Set<PathFinderResultPoint> closedSet, BufferedImage img) {
        mapPanel.imageOverlay = img;
        mapPanel.repaint();
    }

    @Override
    public void onCompleted(Set<PathFinderResultPoint> openSet, Set<PathFinderResultPoint> closedSet,
                            List<PathFinderResultPoint> path, BufferedImage img) {
        mapPanel.imageOverlay = img;
        mapPanel.repaint();
    }
}
