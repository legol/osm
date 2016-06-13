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
//        levelOfDetailItems.put(45, new LinkedList<LodItem>()); // only country boundary
//        levelOfDetailItems.put(40, new LinkedList<LodItem>()); // only continent boundary

        LodItem item = null;

        // 95  /////////////////////////////////////////////////////////////////////////////////
        // no building
        LinkedList<LodItem> lod95 = new LinkedList<LodItem>();
        levelOfDetailItems.put(95, lod95);

        item = new LodItem();
        item.lod = 95;
        item.category = "building";
        item.tag = new Pair<String, String>("building", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod95.add(item);

        // 90  /////////////////////////////////////////////////////////////////////////////////
        // no landuse, amenity, leisure
        LinkedList<LodItem> lod90 = (LinkedList<LodItem>)(lod95.clone());
        levelOfDetailItems.put(90, lod90);

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

        item = new LodItem();
        item.lod = 90;
        item.category = "other";
        item.tag = new Pair<String, String>("*", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod90.add(item);

        // 85  /////////////////////////////////////////////////////////////////////////////////
        // line thinner, controlled by StyleBuilder
        LinkedList<LodItem> lod85 = (LinkedList<LodItem>)(lod90.clone());
        levelOfDetailItems.put(85, lod85);

        // 80  /////////////////////////////////////////////////////////////////////////////////
        // single layered line, controlled by StyleBuilder
        LinkedList<LodItem> lod80 = (LinkedList<LodItem>)(lod85.clone());
        levelOfDetailItems.put(80, lod80);

        // 75  /////////////////////////////////////////////////////////////////////////////////
        // no cycleway, footway
        LinkedList<LodItem> lod75 = (LinkedList<LodItem>)(lod80.clone());
        levelOfDetailItems.put(75, lod75);

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

        // 70  /////////////////////////////////////////////////////////////////////////////////
        // no any highway less than tertiary
        LinkedList<LodItem> lod70 = (LinkedList<LodItem>)(lod75.clone());
        levelOfDetailItems.put(70, lod70);

        item = new LodItem();
        item.lod = 70;
        item.category = "highway";
        item.tag = new Pair<String, String>("highway", "unclassified");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod70.add(item);

        item = new LodItem();
        item.lod = 70;
        item.category = "highway";
        item.tag = new Pair<String, String>("highway", "residential");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod70.add(item);

        item = new LodItem();
        item.lod = 70;
        item.category = "highway";
        item.tag = new Pair<String, String>("highway", "service");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod70.add(item);

        // 65  /////////////////////////////////////////////////////////////////////////////////
        // no tertiary
        LinkedList<LodItem> lod65 = (LinkedList<LodItem>)(lod70.clone());
        levelOfDetailItems.put(65, lod65);

        item = new LodItem();
        item.lod = 65;
        item.category = "highway";
        item.tag = new Pair<String, String>("highway", "tertiary");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod65.add(item);

        // 60  /////////////////////////////////////////////////////////////////////////////////
        // no secondary
        LinkedList<LodItem> lod60 = (LinkedList<LodItem>)(lod65.clone());
        levelOfDetailItems.put(60, lod60);

        item = new LodItem();
        item.lod = 60;
        item.category = "highway";
        item.tag = new Pair<String, String>("highway", "secondary");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod60.add(item);

        // 55  /////////////////////////////////////////////////////////////////////////////////
        // no primary
        LinkedList<LodItem> lod55 = (LinkedList<LodItem>)(lod60.clone());
        levelOfDetailItems.put(55, lod55);

        item = new LodItem();
        item.lod = 55;
        item.category = "highway";
        item.tag = new Pair<String, String>("highway", "primary");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod55.add(item);

        // 50  /////////////////////////////////////////////////////////////////////////////////
        // no way at all, only province boundary, country boundary
        LinkedList<LodItem> lod50 = (LinkedList<LodItem>)(lod80.clone());
        levelOfDetailItems.put(50, lod50);

        item = new LodItem();
        item.lod = 50;
        item.category = "highway";
        item.tag = new Pair<String, String>("highway", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod50.add(item);

        item = new LodItem();
        item.lod = 50;
        item.category = "highway_link";
        item.tag = new Pair<String, String>("highway", "*");
        item.shouldDraw = false;
        item.needFurtherCalc = false;
        lod50.add(item);
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

        // scale                lod
        // 20       -------     100
        // 30       -------     100
        // 50       -------     100
        // 100      -------     100
        // 300      -------     95
        // 500      -------     90
        // 1000     -------     85
        // 2000     -------     80
        // 5000     -------     75
        // 10000    -------     70
        // 20000    -------     65
        // 30000    -------     65
        // 50000    -------     60
        // 100000   -------     55
        // 300000   -------     55
        // 500000   -------     50
        // 1000000
        // 3000000
        // 5000000
        // 10000000

        if (scale <= 100){
            return 100;
        }
        else if (scale <= 300){
            return 95;
        }
        else if (scale <= 500){
            return 90;
        }
        else if (scale <= 1000){
            return 85;
        }
        else if (scale <= 2000){
            return 80;
        }
        else if (scale <= 5000){
            return 75;
        }
        else if (scale <= 10000){
            return 70;
        }
        else if (scale <= 20000){
            return 65;
        }
        else if (scale <= 50000){
            return 60;
        }
        else if (scale <= 300000){
            return 55;
        }
        else if (scale <= 500000){
            return 50;
        }
        else{
            return 50;
        }

    }

}
