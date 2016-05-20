package com.heaven.osmPathFinder.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by chenjie3 on 2016/5/20.
 */
public class MapPanel extends JPanel {

    public BufferedImage image = null;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null){
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
        }
    }

}
