package com.heaven.osmPathFinder.UI.Action;

import com.heaven.osmPathFinder.UI.DoubleImageLayerPanel;
import com.heaven.osmPathFinder.UI.GraphicsUtils;
import com.heaven.osmPathFinder.UI.TestData;
import com.heaven.osmPathFinder.osmcontroller.PathDrawer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

/**
 * Created by chenjie3 on 2016/5/20.
 */
public class FindPathSbSAction implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(FindPathSbSAction.class));


    private DoubleImageLayerPanel mapPanel = null;

    public FindPathSbSAction(DoubleImageLayerPanel _mapPanel){
        mapPanel = _mapPanel;
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        BufferedImage img = GraphicsUtils.createTransparentImage(TestData.sharedInstance().generateImageRequest.imageWidth,
                TestData.sharedInstance().generateImageRequest.imageHeight);
        Graphics2D g = GraphicsUtils.createTransparentGraphics(img, 0.5f);

        // todo

        mapPanel.imageOverlay = img;
        mapPanel.repaint();
    }

}
