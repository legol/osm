package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.GeomBox;
import com.heaven.osm.imagegenerator.model.GeomPoint;
import com.heaven.osm.imagegenerator.model.GraphicsPoint;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

class DrawnString{
    public String str;
    public AffineTransform transform;
    public Rectangle rect;

    public DrawnString(){
        rect = new Rectangle();
    }
}

/**
 * Created by chenjie3 on 2016/5/17.
 */
public class TagDrawer {
    private static final Logger LOGGER = Logger.getLogger(TagDrawer.class);
    private static TagDrawer instance = null;

    private List<DrawnString> drawnStrings = new LinkedList<DrawnString>();

    public boolean intersectsWithOthers(DrawnString drawnString){

        Area a = new Area(drawnString.rect);
        if (drawnString.transform != null){
            a = a.createTransformedArea(drawnString.transform);
        }

        for (int i = 0; i < drawnStrings.size(); i++){
            DrawnString other = drawnStrings.get(i);

            Area b = new Area(other.rect);
            if (other.transform != null){
                b = b.createTransformedArea(other.transform);
            }

            b.intersect(a);
            if (!b.isEmpty()){
                return true;
            }
        }

        return false;
    }

    public void drawLandTag(List<Pair<String, String>> tags, List<GeomPoint> points,
                            GeomBox boundingBox, int imageWidth, int imageHeight,
                            Graphics2D g){
        Graphics2D g1 = (Graphics2D)g.create();

        Rectangle imageRect = new Rectangle(0, 0, imageWidth, imageHeight);

        String name = OSMUtils.sharedInstance().tagValue(tags, "name");
        if (name == null){
            return;
        }

        g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // find the center point of the land
        GraphicsPoint pCenter = new GraphicsPoint();
        pCenter.x = 0;
        pCenter.y = 0;
        for (int i = 0; i < points.size(); i++){
            GraphicsPoint p = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);

            pCenter.x += p.x;
            pCenter.y += p.y;
        }
        pCenter.x /= points.size();
        pCenter.y /= points.size();

        int strWidth = g1.getFontMetrics().stringWidth(name);
        int strHeight = g1.getFontMetrics().getHeight();

        DrawnString drawnString = new DrawnString();
        drawnString.str = name;
        drawnString.transform = null;
        drawnString.rect.x = pCenter.x - strWidth / 2;
        drawnString.rect.y = pCenter.y;
        drawnString.rect.width = strWidth;
        drawnString.rect.height = strHeight;

        if (imageRect.intersects(drawnString.rect) && intersectsWithOthers(drawnString)){
            LOGGER.info(String.format("%s intersects with other tags, skip drawing.", name));
            return;
        }

        g1.setColor(Color.black);
        g1.drawString(name, pCenter.x - strWidth / 2, pCenter.y);

        drawnStrings.add(drawnString);
    }

    public void drawHighwayTag(List<Pair<String, String>> tags, List<GeomPoint> points,
                               GeomBox boundingBox, int imageWidth, int imageHeight,
                               Graphics2D g){
        Graphics2D g1 = (Graphics2D)g.create();

        String name = OSMUtils.sharedInstance().tagValue(tags, "name");
        if (name == null){
            return;
        }

        Rectangle imageRect = new Rectangle(0, 0, imageWidth, imageHeight);

        g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g1.setColor(Color.black);

        for (int i = 0; i + 1 < points.size(); i++){
            GraphicsPoint p0 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i), boundingBox, imageWidth, imageHeight);
            GraphicsPoint p1 = OSMUtils.sharedInstance().GeomPoint2GraphicsPoint(points.get(i + 1), boundingBox, imageWidth, imageHeight);

            AffineTransform at = new AffineTransform();
            at.setToRotation(p1.x - p0.x, p1.y - p0.y, p0.x, p0.y);

            int strWidth = g1.getFontMetrics().stringWidth(name);
            int strHeight = g1.getFontMetrics().getHeight();

            DrawnString drawnString = new DrawnString();
            drawnString.str = name;
            drawnString.transform = at;
            drawnString.rect.x = p0.x;
            drawnString.rect.y = p0.y;
            drawnString.rect.width = strWidth;
            drawnString.rect.height = strHeight;

            if (!imageRect.intersects(drawnString.rect) || intersectsWithOthers(drawnString)){
                LOGGER.info(String.format("%s intersects with other tags, skip drawing.", name));
                continue;
            }

            g1.setTransform(at);
            g1.drawString(name, p0.x, p0.y);

            drawnStrings.add(drawnString);
        }
    }

    // return value indicates whether the tag is drawn
    public void drawTag(List<Pair<String, String>> tags, List<GeomPoint> points,
                                GeomBox boundingBox, int imageWidth, int imageHeight,
                                Graphics2D g){

        Graphics2D g1 = (Graphics2D)g.create();

        String name = OSMUtils.sharedInstance().tagValue(tags, "name");
        if (name == null){
            return;
        }

        if (OSMUtils.sharedInstance().isLand(tags)){
            drawLandTag(tags, points, boundingBox, imageWidth, imageHeight, g1);
        }
        else if (OSMUtils.sharedInstance().isWater(tags) || OSMUtils.sharedInstance().isHighway(tags)){
            drawHighwayTag(tags, points, boundingBox, imageWidth, imageHeight, g1);
        }
    }

}
