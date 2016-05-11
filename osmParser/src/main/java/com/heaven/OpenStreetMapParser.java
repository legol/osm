package com.heaven;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

/**
 * Created by chenjie3 on 2016/5/11.
 */
class OSMHandler extends DefaultHandler {

    public OSMHandler() {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    }


    @Override
    public void notationDecl (String name, String publicId, String systemId) throws SAXException {
    }

    @Override
    public void unparsedEntityDecl (String name, String publicId,
                                    String systemId, String notationName) throws SAXException {
    }

    @Override
    public void startDocument () throws SAXException {
    }

    @Override
    public void endDocument () throws SAXException {
    }

    @Override
    public void startPrefixMapping (String prefix, String uri) throws SAXException {
    }

    @Override
    public void endPrefixMapping (String prefix) throws SAXException {
    }

    @Override
    public void ignorableWhitespace (char ch[], int start, int length) throws SAXException {
    }

    @Override
    public void processingInstruction (String target, String data) throws SAXException {
    }

    @Override
    public void skippedEntity (String name) throws SAXException {
    }

    @Override
    public void warning (SAXParseException e) throws SAXException {
    }

    @Override
    public void error (SAXParseException e) throws SAXException {
    }
}

public class OpenStreetMapParser {
    private static final Logger LOGGER = Logger.getLogger(OpenStreetMapParser.class);

    SAXParserFactory factory = SAXParserFactory.newInstance();

    OpenStreetMapParser(){
    }

    public void run(String osmFile){
        try {

            SAXParser parser = factory.newSAXParser();
            File file = new File(osmFile);
            OSMHandler handler = new OSMHandler();

            parser.parse(file, handler);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
