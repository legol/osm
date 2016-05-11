package com.heaven.osm.model;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenjie3 on 2016/5/11.
 */
public class OSMWay {
    public Map<String, String> attr;
    public List<Pair<String, String>> tag;
    public List<String> nd;

    public OSMWay(){
        tag = new LinkedList<Pair<String, String>>();
        nd = new LinkedList<String>();
    }
}
