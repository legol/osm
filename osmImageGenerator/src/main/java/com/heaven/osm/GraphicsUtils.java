package com.heaven.osm;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by ChenJie3 on 2015/10/23.
 */
public class GraphicsUtils {

    public static BufferedImage createTransparentImage(int width, int height){
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return img;
    }

    public static Graphics2D createTransparentGraphics(BufferedImage img, float alpha){
        Graphics2D g = img.createGraphics();
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g.setComposite(comp );

        return g;
    }

}