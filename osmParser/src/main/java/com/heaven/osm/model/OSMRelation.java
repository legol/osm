package com.heaven.osm.model;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OSMRelation {
    public Map<String, String> attr;
    public List<Pair<String, String>> tag;
    public List<OSMMember> member;

    public OSMRelation(){
        tag = new LinkedList<Pair<String, String>>();
        member = new LinkedList<OSMMember>();
    }

}
