package com.heaven;

import com.heaven.osm.controller.BoundingBoxCalculator;
import com.heaven.osm.controller.OpenStreetMapParser;
import com.heaven.osm.controller.PostgresqlAdapter;
import com.heaven.osm.model.GeomPoint;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger LOGGER = Logger.getLogger(App.class);


    public static void main( String[] args )
    {
        LOGGER.info("app launch.");

        if (args[0].compareToIgnoreCase("osm2pg") == 0){
            // osm2pg C:\Projects\osm\testData\beijing-circle-6.osm
            LOGGER.info("parse " + args[1] + " and save it to postgreSQL.");
            OpenStreetMapParser parser = new OpenStreetMapParser();
            parser.run(args[0]);
        }
        else if(args[0].compareToIgnoreCase("calc_bounding_box") == 0){
            // calc_bounding_box
            LOGGER.info("calculate bounding box... ");
            BoundingBoxCalculator calc = new BoundingBoxCalculator();
            calc.run();
        }
    }
}
