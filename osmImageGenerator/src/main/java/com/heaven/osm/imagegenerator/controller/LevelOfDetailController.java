package com.heaven.osm.imagegenerator.controller;

import javafx.util.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenjie3 on 2016/5/31.
 */

class LodItem{
    int lod = 100;
    public String category = ""; // node, way, relation
    Pair<String, String> tag = null;
    boolean shouldDraw = false;
    boolean needFurtherCalc = false;
}

public class LevelOfDetailController {
    private static final Logger LOGGER = Logger.getLogger(LevelOfDetailController.class);
    private static LevelOfDetailController instance = null;

    public Map<Integer, LinkedList<LodItem>> levelOfDetailItems;

    public static LevelOfDetailController sharedInstance() {
        if (instance == null) {
            instance = new LevelOfDetailController();
        }
        return instance;
    }

    public LevelOfDetailController(){
        levelOfDetailItems = new HashMap<Integer, LinkedList<LodItem>>();
        levelOfDetailItems.put(95, new LinkedList<LodItem>());
        levelOfDetailItems.put(90, new LinkedList<LodItem>());
        levelOfDetailItems.put(85, new LinkedList<LodItem>());

        LodItem item = null;

        // 95  /////////////////////////////////////////////////////////////////////////////////
        LinkedList<LodItem> lod95 = levelOfDetailItems.get(95);

        item = new LodItem();
        item.lod = 95;
        item.category = "building";
        item.tag = new Pair<String, String>("building", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod95.add(item);

        item = new LodItem();
        item.lod = 95;
        item.category = "highway";
        item.tag = new Pair<String, String>("highway", "*");
        item.shouldDraw = false; // doesn't matter
        item.needFurtherCalc = true;
        lod95.add(item);

        // 90  /////////////////////////////////////////////////////////////////////////////////
        LinkedList<LodItem> lod90 = levelOfDetailItems.get(90);
        item = new LodItem();
        item.lod = 90;
        item.category = "building";
        item.tag = new Pair<String, String>("building", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod90.add(item);

        item = new LodItem();
        item.lod = 90;
        item.category = "highway";
        item.tag = new Pair<String, String>("highway", "*");
        item.shouldDraw = false; // doesn't matter
        item.needFurtherCalc = true;
        lod90.add(item);

        item = new LodItem();
        item.lod = 90;
        item.category = "land";
        item.tag = new Pair<String, String>("landuse", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod90.add(item);

        item = new LodItem();
        item.lod = 90;
        item.category = "land";
        item.tag = new Pair<String, String>("amenity", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod90.add(item);

        item = new LodItem();
        item.lod = 90;
        item.category = "land";
        item.tag = new Pair<String, String>("leisure", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod90.add(item);

        // 85  /////////////////////////////////////////////////////////////////////////////////
        LinkedList<LodItem> lod85 = levelOfDetailItems.get(85);
        item = new LodItem();
        item.lod = 85;
        item.category = "building";
        item.tag = new Pair<String, String>("building", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod85.add(item);

        item = new LodItem();
        item.lod = 85;
        item.category = "land";
        item.tag = new Pair<String, String>("landuse", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod85.add(item);

        item = new LodItem();
        item.lod = 85;
        item.category = "land";
        item.tag = new Pair<String, String>("amenity", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod85.add(item);

        item = new LodItem();
        item.lod = 85;
        item.category = "land";
        item.tag = new Pair<String, String>("leisure", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod85.add(item);

        item = new LodItem();
        item.lod = 85;
        item.category = "highway";
        item.tag = new Pair<String, String>("highway", "*");
        item.shouldDraw = false; // doesn't matter
        item.needFurtherCalc = true;
        lod85.add(item);

    }

    public boolean shouldDrawFurtherCalc(LodItem matchedLodItem, String category, List<Pair<String, String>> tags){

        if (matchedLodItem.lod == 95){
            if (matchedLodItem.tag.getKey().compareToIgnoreCase("highway") == 0){
                for (Pair<String, String> oneTag : tags){
                    if (oneTag.getKey().compareToIgnoreCase("highway") == 0){
                        if (oneTag.getValue().compareToIgnoreCase("footway") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("cycleway") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("service") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("living_street") == 0){
                            return false;
                        }
                    }
                }
            }
        }
        else if (matchedLodItem.lod == 90) {
            if (category.compareToIgnoreCase("other") == 0){
                return false;
            }

            if (matchedLodItem.tag.getKey().compareToIgnoreCase("highway") == 0) {
                for (Pair<String, String> oneTag : tags) {
                    if (oneTag.getKey().compareToIgnoreCase("highway") == 0) {
                        if (oneTag.getValue().compareToIgnoreCase("trunk") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("motorway") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("primary") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("secondary") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("tertiary") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("motorway_link") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("trunk_link") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("primary_link") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("secondary_link") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("tertiary_link") == 0) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        else if (matchedLodItem.lod == 85) {
            if (category.compareToIgnoreCase("other") == 0){
                return false;
            }

            if (matchedLodItem.tag.getKey().compareToIgnoreCase("highway") == 0) {
                for (Pair<String, String> oneTag : tags) {
                    if (oneTag.getKey().compareToIgnoreCase("highway") == 0) {
                        if (oneTag.getValue().compareToIgnoreCase("trunk") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("motorway") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("primary") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("secondary") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("motorway_link") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("trunk_link") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("primary_link") == 0 ||
                                oneTag.getValue().compareToIgnoreCase("secondary_link") == 0) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }


        return true;
    }

    public boolean shouldDraw(int lod, String category, List<Pair<String, String>> tags){
        if (levelOfDetailItems.containsKey(lod) == false){
            return true;
        }
        else{
            LinkedList<LodItem> lodItems = levelOfDetailItems.get(lod);
            LodItem matchedLodItem = null;

            for (LodItem oneLodItem : lodItems){

                if (oneLodItem.category.compareToIgnoreCase(category) != 0){
                    continue;
                }

                for (Pair<String, String> checkingTag : tags) {
                    if (oneLodItem.tag.getKey().compareToIgnoreCase("*") == 0 ||
                            (checkingTag.getKey().compareToIgnoreCase(oneLodItem.tag.getKey()) == 0 && oneLodItem.tag.getValue().compareToIgnoreCase("*") == 0) ||
                            (checkingTag.getKey().compareToIgnoreCase(oneLodItem.tag.getKey()) == 0 && checkingTag.getValue().compareToIgnoreCase(oneLodItem.tag.getValue()) == 0)){
                        if (oneLodItem.needFurtherCalc){
                            return shouldDrawFurtherCalc(oneLodItem, category, tags);
                        }
                        return oneLodItem.shouldDraw;
                    }
                }
            }

            return true; // by default, should draw everything.
        }
    }

    public int determinLod(double scale/* meters in one centimeter */){
        if (scale >=100 ){
            return 85;
        }
        else{
            return 100;
        }
    }

}
