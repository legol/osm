package com.heaven.osm.controller;

import com.heaven.osm.model.GeomBox;
import com.heaven.osm.model.GeomPoint;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by chenjie3 on 2016/5/18.
 */
public class ConnectivityCalculator {
    private static final Logger LOGGER = Logger.getLogger(ConnectivityCalculator.class);

    public void run() {
        LOGGER.info("start calculating...");

        List<Long> ways = PostgresqlAdapter.sharedInstance().getHighways();
        for (int i = 0; i < ways.size(); i++){
            long way_ref = ways.get(i).longValue();

            List<GeomPoint> points = PostgresqlAdapter.sharedInstance().getPointsOfWay(way_ref);

            List<Pair<String, String>> tags = PostgresqlAdapter.sharedInstance().getTags("way", way_ref);
            String  onewayValue = OSMUtils.sharedInstance().tagValue(tags, "oneway");
            boolean isOneway = (onewayValue != null && onewayValue.compareToIgnoreCase("yes") == 0);

            for (int j = 0; j + 1< points.size(); j++){
                if(!PostgresqlAdapter.sharedInstance().saveConnectivity(points.get(j), points.get(j + 1), way_ref)){
                    LOGGER.error(String.format("failed to save bounding box of way_ref=%d", way_ref));
                }

                if (!isOneway){
                    if(!PostgresqlAdapter.sharedInstance().saveConnectivity(points.get(j + 1), points.get(j), way_ref)){
                        LOGGER.error(String.format("failed to save bounding box of way_ref=%d", way_ref));
                    }
                }
            }
        }


        LOGGER.info("done calculation.");
    }
}
