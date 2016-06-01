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
        levelOfDetailItems.put(80, new LinkedList<LodItem>());
        levelOfDetailItems.put(75, new LinkedList<LodItem>());

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

        // 80  /////////////////////////////////////////////////////////////////////////////////
        LinkedList<LodItem> lod80 = levelOfDetailItems.get(80);
        item = new LodItem();
        item.lod = 80;
        item.category = "building";
        item.tag = new Pair<String, String>("building", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod80.add(item);

        item = new LodItem();
        item.lod = 80;
        item.category = "land";
        item.tag = new Pair<String, String>("landuse", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod80.add(item);

        item = new LodItem();
        item.lod = 80;
        item.category = "land";
        item.tag = new Pair<String, String>("amenity", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod80.add(item);

        item = new LodItem();
        item.lod = 80;
        item.category = "land";
        item.tag = new Pair<String, String>("leisure", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod80.add(item);

        // 75  /////////////////////////////////////////////////////////////////////////////////
        LinkedList<LodItem> lod75 = levelOfDetailItems.get(75);
        item = new LodItem();
        item.lod = 75;
        item.category = "building";
        item.tag = new Pair<String, String>("building", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod75.add(item);

        item = new LodItem();
        item.lod = 75;
        item.category = "land";
        item.tag = new Pair<String, String>("landuse", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod75.add(item);

        item = new LodItem();
        item.lod = 75;
        item.category = "land";
        item.tag = new Pair<String, String>("amenity", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod75.add(item);

        item = new LodItem();
        item.lod = 75;
        item.category = "land";
        item.tag = new Pair<String, String>("leisure", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod75.add(item);

        item = new LodItem();
        item.lod = 75;
        item.category = "highway";
        item.tag = new Pair<String, String>("highway", "cycleway");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod75.add(item);

        item = new LodItem();
        item.lod = 75;
        item.category = "highway";
        item.tag = new Pair<String, String>("highway", "footway");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod75.add(item);
    }

    public boolean shouldDrawFurtherCalc(LodItem matchedLodItem, String category, List<Pair<String, String>> tags){
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

        return 75;
//        if (scale >=100 ){
//            return 75;
//        }
//        else{
//            return 100;
//        }
    }

}
