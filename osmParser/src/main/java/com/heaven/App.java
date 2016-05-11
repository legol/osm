package com.heaven;

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
        System.out.println( "Hello World!" );
    }
}
