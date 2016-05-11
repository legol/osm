package com.heaven;

import com.heaven.osm.controller.OpenStreetMapParser;
import com.heaven.osm.controller.PostgresqlAdapter;
import org.apache.log4j.Logger;

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

        if (args.length != 1){
            LOGGER.error("missing input file.");
            System.out.println( "missing input file!" );
            return;
        }

        PostgresqlAdapter.sharedInstance().test();

        OpenStreetMapParser parser = new OpenStreetMapParser();
        parser.run(args[0]);
    }
}
