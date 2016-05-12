package com.heaven.osm.controller;

import com.heaven.osm.model.OSMMember;
import com.heaven.osm.model.OSMNode;
import com.heaven.osm.model.OSMRelation;
import com.heaven.osm.model.OSMWay;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by chenjie3 on 2016/5/11.
 */
class OSMHandler extends DefaultHandler {
    private static final Logger LOGGER = Logger.getLogger(OSMHandler.class);

    private Stack<String> parseStack = new Stack<String>();

    private OSMNode currentNode = new OSMNode();
    private OSMWay currentWay = new OSMWay();
    private OSMRelation currentRelation = new OSMRelation();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (parseStack.size() == 1){ // top level is "osm"
            if (qName.compareToIgnoreCase("node") == 0){
                currentNode = new OSMNode();
                currentNode.attr = processAttribute(attributes);
            }
            else if(qName.compareToIgnoreCase("way") == 0){
                currentWay = new OSMWay();
                currentWay.attr = processAttribute(attributes);
            }
            else if (qName.compareToIgnoreCase("relation") == 0) {
                currentRelation = new OSMRelation();
                currentRelation.attr = processAttribute(attributes);
            }
            else if (qName.compareToIgnoreCase("bounds") == 0){
                // do nothing
            }
            else{
                LOGGER.error("unknown element:" + qName);
            }
        }
        else if (parseStack.size() == 2) {
            // "tag" under "node"
            // or
            // "tag" under "way", "nd" under "way"
            // or
            // "tag" under "relation", "member" under "relation"
            if (qName.compareToIgnoreCase("tag") == 0){
                if (parseStack.peek().compareToIgnoreCase("node") == 0){
                    processTag(currentNode.tag, attributes);
                }
                else if (parseStack.peek().compareToIgnoreCase("way") == 0) {
                    processTag(currentWay.tag, attributes);
                }
                else if (parseStack.peek().compareToIgnoreCase("relation") == 0) {
                    processTag(currentRelation.tag, attributes);
                }
                else {
                    LOGGER.error("unknown element:" + qName);
                }
            }
            else if (qName.compareToIgnoreCase("nd") == 0){
                if (parseStack.peek().compareToIgnoreCase("way") == 0) {
                    processNd(currentWay.nd, attributes);
                }
                else {
                    LOGGER.error("unknown element:" + qName);
                }
            }
            else if (qName.compareToIgnoreCase("member") == 0){
                if (parseStack.peek().compareToIgnoreCase("relation") == 0) {
                    processMember(currentRelation.member, attributes);
                }
                else {
                    LOGGER.error("unknown element:" + qName);
                }
            }
        }

        parseStack.push(qName);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (parseStack.size() == 2) { // top level is "osm"

            if (qName.compareToIgnoreCase("node") == 0) {
                saveNode(currentNode);
                currentNode = null;
            } else if (qName.compareToIgnoreCase("way") == 0) {
                saveWay(currentWay);
                currentWay = null;
            } else if (qName.compareToIgnoreCase("relation") == 0) {
                saveRelation(currentRelation);
                currentRelation = null;
            }
        }

        parseStack.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void startDocument () throws SAXException {
        LOGGER.info("start processing.");
    }

    @Override
    public void endDocument () throws SAXException {
        LOGGER.info("complete processing.");
    }

    @Override
    public void warning (SAXParseException e) throws SAXException {
    }

    @Override
    public void error (SAXParseException e) throws SAXException {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected Map<String, String> processAttribute(Attributes attributes){

        Map<String, String> attr = new HashMap<String, String>();

        for (int i = 0; i < attributes.getLength(); i++){
            String attr_type = attributes.getQName(i);
            String attr_value = attributes.getValue(i);

            attr.put(attr_type, attr_value);
        }

        return attr;
    }

    protected void processTag(List<Pair<String, String>> tag, Attributes attributes){
        String k = attributes.getValue("k");
        String v = attributes.getValue("v");

        tag.add(new Pair<String, String>(k, v));
    }

    protected void processNd(List<String> nd, Attributes attributes){
        String ref = attributes.getValue("ref");

        nd.add(ref);
    }

    protected void processMember(List<OSMMember> member, Attributes attributes){
        OSMMember newMember = new OSMMember();

        newMember.ref = attributes.getValue("ref");
        newMember.type = attributes.getValue("type");
        newMember.role = attributes.getValue("role");

        member.add(newMember);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    protected void saveNode(OSMNode node){
        PostgresqlAdapter.sharedInstance().saveNode(node);
    }

    protected void saveWay(OSMWay way){

    }

    protected void saveRelation(OSMRelation relation){

    }
}

public class OpenStreetMapParser {
    private static final Logger LOGGER = Logger.getLogger(OpenStreetMapParser.class);

    SAXParserFactory factory = SAXParserFactory.newInstance();

    public OpenStreetMapParser(){
    }

    public void run(String osmFile){
        try {

            SAXParser parser = factory.newSAXParser();
            org.xml.sax.InputSource input = new InputSource(new FileReader(osmFile));
            OSMHandler handler = new OSMHandler();

            parser.parse(input, handler);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
