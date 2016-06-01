package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.GeomBox;
import com.heaven.osm.imagegenerator.model.style.*;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.*;


/**
 * Created by chenjie3 on 2016/6/1.
 */
public class StyleBuilder {
    private static final Logger LOGGER = Logger.getLogger(StyleBuilder.class);

    private static StyleBuilder instance = null;

    public static StyleBuilder sharedInstance() {
        if (instance == null) {
            instance = new StyleBuilder();
        }
        return instance;
    }

    public NaturalStyle createNaturalStyle(int lod, java.util.List<Pair<String, String>> tags){
        NaturalStyle style = new NaturalStyle();

        String naturalValue = OSMUtils.sharedInstance().tagValue(tags, "natural");
        if (naturalValue != null && naturalValue.compareToIgnoreCase("water") == 0){
            style.innerClr = new Color(181, 208, 208);
            style.innerStroke = new BasicStroke(1);
        } else {
            style.innerClr = new Color(199, 228, 182);
            style.innerStroke = new BasicStroke(1);
        }

        style.edgeClr = null;
        style.edgeStroke = null;

        return style;
    }

    public LandStyle createLandStyle(int lod, java.util.List<Pair<String, String>> tags){
        LandStyle style = new LandStyle();

        if (OSMUtils.sharedInstance().isLeisure(tags)){
            style.innerStroke = new BasicStroke(1);
            style.innerClr = new Color(97, 240, 19);
        }else if (OSMUtils.sharedInstance().isAmenity(tags)){
            style.innerStroke = new BasicStroke(1);
            style.innerClr = new Color(246, 249, 190);
        } else {
            String landValue = OSMUtils.sharedInstance().tagValue(tags, "landuse");
            if (landValue != null &&
                    (landValue.compareToIgnoreCase("basin") == 0 || landValue.compareToIgnoreCase("reservoir") == 0)){
                style.innerStroke = new BasicStroke(1);
                style.innerClr = new Color(181, 208, 208);// must be same with waterway
            } else {
                style.innerStroke = new BasicStroke(1);
                style.innerClr = new Color(224, 222, 222);// must be same with waterway
            }
        }

        style.edgeClr = new Color(180, 180, 178);
        style.edgeStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

        return style;
    }

    public WaterStyle createWaterStyle(int lod){
        WaterStyle style = new WaterStyle();

        style.edgeClr = null;
        style.edgeStroke = null;
        style.innerClr = new Color(181, 208, 208);
        style.innerStroke = new BasicStroke(5);

        return style;
    }

    public BuildingStyle createBuildingStyle(int lod){
        BuildingStyle style = new BuildingStyle();

        style.edgeStroke = new BasicStroke(1);
        style.edgeClr = new Color(198, 186, 177);

        style.innerStroke = new BasicStroke(1);
        style.innerClr = new Color(216, 208, 197);

        return style;
    }

    public HighwayLinkStyle createHighwayLinkStyle(int lod, String highwayValue){
        int wayWidth1 = 22;
        int wayWidth2 = 8;
        int wayWidth3 = 4;

        BasicStroke edgeStroke = null;
        BasicStroke innerStroke = null;
        Color edgeClr = null;
        Color innerClr = null;

        if (lod >= 100){
            wayWidth1 = 22;
            wayWidth2 = 8;
            wayWidth3 = 4;
        }
        else if (lod < 100 && lod >= 85){
            wayWidth1 = 8;
            wayWidth2 = 6;
            wayWidth3 = 3;
        }
        else if (lod < 85 && lod >= 80){
            wayWidth1 = 5;
            wayWidth2 = 3;
            wayWidth3 = 2;
        }
        else{
            wayWidth1 = 3;
            wayWidth2 = 1;
            wayWidth3 = 1;
        }

        if (lod >= 80){
            if (highwayValue.compareToIgnoreCase("motorway_link") == 0){
                edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                edgeClr = new Color(221, 41, 108);
                innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr = new Color(232, 143, 161);
            }
            else if (highwayValue.compareToIgnoreCase("trunk_link") == 0){
                edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                edgeClr = new Color(203, 81, 52);
                innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(249, 177, 156);
            }
            else if (highwayValue.compareToIgnoreCase("primary_link") == 0){
                edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                edgeClr = new Color(173, 123, 26);
                innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(252, 214, 164);
            }
            else if (highwayValue.compareToIgnoreCase("secondary_link") == 0){
                edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                edgeClr = new Color(124, 137, 23);
                innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(246, 250, 190);
            }
            else{
                edgeStroke = new BasicStroke(wayWidth2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                edgeClr = new Color(173, 173, 173);
                innerStroke = new BasicStroke(wayWidth2 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(254, 254, 254);
            }
        }
        else{
            if (highwayValue.compareToIgnoreCase("motorway_link") == 0){
                innerStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr = new Color(232, 143, 161);
            }
            else if (highwayValue.compareToIgnoreCase("trunk_link") == 0){
                innerStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(249, 177, 156);
            }
            else if (highwayValue.compareToIgnoreCase("primary_link") == 0){
                innerStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(252, 214, 164);
            }
            else if (highwayValue.compareToIgnoreCase("secondary_link") == 0){
                innerStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(173, 173, 173);
            }
            else{
                innerStroke = new BasicStroke(wayWidth2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(173, 173, 173);
            }
        }

        HighwayLinkStyle newStyle = new HighwayLinkStyle();
        newStyle.edgeClr = edgeClr;
        newStyle.edgeStroke = edgeStroke;
        newStyle.innerClr = innerClr;
        newStyle.innerStroke = innerStroke;

        return newStyle;
    }

    public RailStyle createRailStyle(int lod, String railwayValue){
        BasicStroke edgeStroke = null;
        BasicStroke innerStroke = null;
        Color edgeClr = null;
        Color innerClr = null;

        if (lod >= 80){
            edgeStroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            edgeClr = Color.black;
            innerStroke = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr = new Color(153, 153, 153);
        }
        else{
            innerStroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            innerClr = new Color(153, 153, 153);
        }

        RailStyle newStyle = new RailStyle();
        newStyle.edgeClr = edgeClr;
        newStyle.edgeStroke = edgeStroke;
        newStyle.innerClr = innerClr;
        newStyle.innerStroke = innerStroke;

        return newStyle;
    }

    public HighwayStyle createHighwayStyle(int lod, String highwayValue){
        int wayWidth1 = 22;
        int wayWidth2 = 8;
        int wayWidth3 = 4;

        BasicStroke edgeStroke = null;
        BasicStroke innerStroke = null;
        Color edgeClr = null;
        Color innerClr = null;

        if (lod >= 100){
            wayWidth1 = 22;
            wayWidth2 = 8;
            wayWidth3 = 4;
        }
        else if (lod < 100 && lod >= 85){
            wayWidth1 = 8;
            wayWidth2 = 6;
            wayWidth3 = 3;
        }
        else if (lod < 85 && lod >= 80){
            wayWidth1 = 5;
            wayWidth2 = 3;
            wayWidth3 = 2;
        }
        else{
            wayWidth1 = 3;
            wayWidth2 = 1;
            wayWidth3 = 1;
        }

        if (lod >= 80){
            if (highwayValue.compareToIgnoreCase("motorway") == 0){
                edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                edgeClr = new Color(221, 41, 108);
                innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr = new Color(232, 143, 161);
            }
            else if (highwayValue.compareToIgnoreCase("trunk") == 0){
                edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                edgeClr = new Color(203, 81, 52);
                innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(249, 177, 156);
            }
            else if (highwayValue.compareToIgnoreCase("primary") == 0){
                edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                edgeClr = new Color(173, 123, 26);
                innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(252, 214, 164);
            }
            else if (highwayValue.compareToIgnoreCase("secondary") == 0){
                edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                edgeClr = new Color(124, 137, 23);
                innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(246, 250, 190);
            }
            else if (highwayValue.compareToIgnoreCase("tertiary") == 0){
                edgeStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                edgeClr = new Color(173, 173, 173);
                innerStroke = new BasicStroke(wayWidth1 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(254, 254, 254);
            }
            else if (highwayValue.compareToIgnoreCase("cycleway") == 0 || highwayValue.compareToIgnoreCase("footway") == 0){
                edgeStroke = null;
                edgeClr = null;
                innerStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{9}, 0);
                innerClr = new Color(123, 121, 247);
            }
            else if (highwayValue.compareToIgnoreCase("living_street") == 0){
                edgeStroke = null;
                edgeClr = null;
                innerStroke = new BasicStroke(wayWidth3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(255, 255, 255);
            }
            else if (highwayValue.compareToIgnoreCase("service") == 0){
                edgeStroke = new BasicStroke(wayWidth3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                edgeClr = new Color(173, 173, 173);
                innerStroke = new BasicStroke(wayWidth3 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(254, 254, 254);
            }
            else{
                edgeStroke = new BasicStroke(wayWidth2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                edgeClr = new Color(173, 173, 173);
                innerStroke = new BasicStroke(wayWidth2 - 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(254, 254, 254);
            }
        }
        else{
            // lod < 70. highway won't have an edge.
            if (highwayValue.compareToIgnoreCase("motorway") == 0){
                innerStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr = new Color(232, 143, 161);
            }
            else if (highwayValue.compareToIgnoreCase("trunk") == 0){
                innerStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(249, 177, 156);
            }
            else if (highwayValue.compareToIgnoreCase("primary") == 0){
                innerStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(252, 214, 164);
            }
            else if (highwayValue.compareToIgnoreCase("secondary") == 0){
                innerStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(173, 173, 173);
            }
            else if (highwayValue.compareToIgnoreCase("tertiary") == 0){
                innerStroke = new BasicStroke(wayWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(173, 173, 173);
            }
            else if (highwayValue.compareToIgnoreCase("cycleway") == 0 || highwayValue.compareToIgnoreCase("footway") == 0){
                innerStroke = new BasicStroke(wayWidth3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{9}, 0);
                innerClr = new Color(123, 121, 247);
            }
            else if (highwayValue.compareToIgnoreCase("living_street") == 0){
                innerStroke = new BasicStroke(wayWidth2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(173, 173, 173);
            }
            else if (highwayValue.compareToIgnoreCase("service") == 0){
                innerStroke = new BasicStroke(wayWidth2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(173, 173, 173);
            }
            else{
                innerStroke = new BasicStroke(wayWidth3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                innerClr =  new Color(173, 173, 173);
            }
        }


        HighwayStyle newStyle = new HighwayStyle();
        newStyle.edgeClr = edgeClr;
        newStyle.edgeStroke = edgeStroke;
        newStyle.innerClr = innerClr;
        newStyle.innerStroke = innerStroke;

        return newStyle;
    }
}
