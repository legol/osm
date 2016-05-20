package com.heaven.osmPathFinder.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by chenjie3 on 2016/5/20.
 */
public class DoubleImageLayerPanel extends JPanel {

    public BufferedImage image = null;
    public BufferedImage imageOverlay = null; // transparent overlay image.

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null){
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
        }

        if (imageOverlay != null){
            g.drawImage(imageOverlay, 0, 0, this.getWidth(), this.getHeight(), null);
        }
    }

}
