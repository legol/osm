package com.heaven.osm.controller;

import com.heaven.osm.model.GeomBox;
import com.heaven.osm.model.GeomPoint;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chenjie3 on 2016/5/17.
 */
public class OSMUtils {
    private static final Logger LOGGER = Logger.getLogger(OSMUtils.class);
    private static OSMUtils instance = null;

    public static OSMUtils sharedInstance() {
        if (instance == null) {
            instance = new OSMUtils();
        }
        return instance;
    }

    public boolean isBuilding(List<Pair<String, String>> tags){
        return isTag(tags, "building");
    }
    public boolean isLand(List<Pair<String, String>> tags){
        return isTag(tags, "landuse");
    }
    public boolean isNatural(List<Pair<String, String>> tags){
        return isTag(tags, "natural");
    }
    public boolean isHighway(List<Pair<String, String>> tags){
        return isTag(tags, "highway");
    }
    public boolean isWaterway(List<Pair<String, String>> tags){
        return isTag(tags, "waterway");
    }
    public boolean isLeisure(List<Pair<String, String>> tags){
        return isTag(tags, "leisure");
    }
    public boolean isAmenity(List<Pair<String, String>> tags){
        return isTag(tags, "amenity");
    }
    public boolean isBoundary(List<Pair<String, String>> tags){
        return isTag(tags, "boundary");
    }
    public boolean isRail(List<Pair<String, String>> tags){
        return isTag(tags, "railway");
    }
    public boolean isPower(List<Pair<String, String>> tags){
        return isTag(tags, "power");
    }

    public boolean isTag(List<Pair<String, String>> tags, String type){
        for (int i = 0; i < tags.size(); i++){
            if (tags.get(i).getKey().compareToIgnoreCase(type) == 0){
                return true;
            }
        }
        return false;
    }

    public String tagValue(List<Pair<String, String>> tags, String type){
        for (int i = 0; i < tags.size(); i++){
            if (tags.get(i).getKey().compareToIgnoreCase(type) == 0){
                return tags.get(i).getValue();
            }
        }
        return null;
    }

}
