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
    public String geomType = ""; // node, way, relation
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
        levelOfDetailItems.put(70, new LinkedList<LodItem>());
        levelOfDetailItems.put(65, new LinkedList<LodItem>());
        levelOfDetailItems.put(60, new LinkedList<LodItem>());
        levelOfDetailItems.put(55, new LinkedList<LodItem>());
        levelOfDetailItems.put(50, new LinkedList<LodItem>());

        LinkedList<LodItem> lod95 = levelOfDetailItems.get(95);

        LodItem item = null;

        item = new LodItem();
        item.lod = 95;
        item.geomType = "way";
        item.tag = new Pair<String, String>("building", "yes");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod95.add(item);

        item = new LodItem();
        item.lod = 95;
        item.geomType = "way";
        item.tag = new Pair<String, String>("highway", "footway");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod95.add(item);

        item = new LodItem();
        item.lod = 95;
        item.geomType = "way";
        item.tag = new Pair<String, String>("highway", "cycleway");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod95.add(item);

        item = new LodItem();
        item.lod = 95;
        item.geomType = "way";
        item.tag = new Pair<String, String>("highway", "living_street");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod95.add(item);

    }

    public boolean shouldDrawFurtherCalc(LodItem matchedLodItem, String geomType, List<Pair<String, String>> tags){
        return false;
    }

    public boolean shouldDraw(int lod, String geomType, List<Pair<String, String>> tags){
        if (levelOfDetailItems.containsKey(lod) == false){
            return true;
        }
        else{
            LinkedList<LodItem> lodItems = levelOfDetailItems.get(lod);
            LodItem matchedLodItem = null;

            for (LodItem oneLodItem : lodItems){

                if (oneLodItem.geomType.compareToIgnoreCase(geomType) != 0){
                    continue;
                }

                for (Pair<String, String> checkingTag : tags) {
                    if (checkingTag.getKey().compareToIgnoreCase(oneLodItem.tag.getKey()) == 0 &&
                            checkingTag.getValue().compareToIgnoreCase(oneLodItem.tag.getValue()) == 0){

                        if (oneLodItem.needFurtherCalc){
                            return shouldDrawFurtherCalc(oneLodItem, geomType, tags);
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
            return 95;
        }
        else{
            return 100;
        }
    }

}
