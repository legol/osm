package com.heaven.osm.controller;

import com.heaven.osm.model.GeomBox;
import com.heaven.osm.model.GeomPoint;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by legol on 5/12/2016.
 */
public class BoundingBoxCalculator {
    private static final Logger LOGGER = Logger.getLogger(BoundingBoxCalculator.class);

    public GeomBox calcBoundingBox(List<GeomPoint> points){
        GeomBox boundingBox = new GeomBox();

        boundingBox.minlon = Double.MAX_VALUE;
        boundingBox.minlat = Double.MAX_VALUE;
        boundingBox.maxlon = Double.MIN_VALUE;
        boundingBox.maxlat = Double.MIN_VALUE;

        for (int i = 0; i < points.size(); i++){
            GeomPoint point = points.get(i);

            if (point.latitude > boundingBox.maxlat){
                boundingBox.maxlat = point.latitude;
            }
            if (point.latitude < boundingBox.minlat){
                boundingBox.minlat = point.latitude;
            }
            if (point.longitude > boundingBox.maxlon){
                boundingBox.maxlon = point.longitude;
            }
            if (point.longitude < boundingBox.minlon){
                boundingBox.minlon = point.longitude;
            }
        }

        return boundingBox;
    }

    public void run(){
        LOGGER.info("start calculating...");

        LOGGER.info("calculating relation bounding box...");
        List<Long> relations = PostgresqlAdapter.sharedInstance().getRelations();
        for (int i = 0; i < relations.size(); i++){
            long relation_ref = relations.get(i).longValue();

            List<GeomPoint> points = PostgresqlAdapter.sharedInstance().getPointsOfRelation(relation_ref);
            GeomBox boundingBox = calcBoundingBox(points);

            if(!PostgresqlAdapter.sharedInstance().saveRelationBoundingBox(relation_ref, boundingBox)){
                LOGGER.error(String.format("failed to save bounding box of relation_ref=%d", relation_ref));
            }
        }
        LOGGER.info("calculate relation bounding box completed.");

        LOGGER.info("calculating way bounding box...");
        List<Long> ways = PostgresqlAdapter.sharedInstance().getWays();
        for (int i = 0; i < ways.size(); i++){
            long way_ref = ways.get(i).longValue();

            List<GeomPoint> points = PostgresqlAdapter.sharedInstance().getPointsOfWay(way_ref);
            GeomBox boundingBox = calcBoundingBox(points);

            if(!PostgresqlAdapter.sharedInstance().saveWayBoundingBox(way_ref, boundingBox)){
                LOGGER.error(String.format("failed to save bounding box of way_ref=%d", way_ref));
            }
        }
        LOGGER.info("calculate way bounding box completed.");


        LOGGER.info("calculation completed.");
    }
}
