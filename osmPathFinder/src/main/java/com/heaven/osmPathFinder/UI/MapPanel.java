package com.heaven.osmPathFinder.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by chenjie3 on 2016/5/20.
 */
public class MapPanel extends JPanel {

    public BufferedImage image = null;

    private BufferedImage createOverlayImage() {
        BufferedImage img = new BufferedImage(1024, 1024,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(6f));
        g2.setColor(Color.red);
        int circleCount = 10;
        for (int i = 0; i < circleCount + 1; i++) {
            int x1 = (i * 1024) / (circleCount);
            int y1 = 0;
            int x2 = 1024 - x1;
            int y2 = 1024;
            float alpha = (float)i / circleCount;
            if (alpha > 1f) {
                alpha = 1f;
            }
            // int rule = AlphaComposite.CLEAR;
            int rule = AlphaComposite.SRC_OVER;
            Composite comp = AlphaComposite.getInstance(rule , alpha );
            g2.setComposite(comp );
            g2.drawLine(x1, y1, x2, y2);
        }
        g2.dispose();
        return img;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null){
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);

            BufferedImage overlay = createOverlayImage();
            g.drawImage(overlay, 0, 0, this.getWidth(), this.getHeight(), null);
        }
    }

}
