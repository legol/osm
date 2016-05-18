package com.heaven.osm.imagegenerator.model;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.osm.Utils;
import com.heaven.osm.imagegenerator.model.*;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by chenjie3 on 2016/5/11.
 */
public class PostgresqlAdapter {
    private static final Logger LOGGER = Logger.getLogger(PostgresqlAdapter.class);

    private static PostgresqlAdapter instance = null;

    private ComboPooledDataSource cpds = null;
    private Properties props = null;

    protected PostgresqlAdapter() {
        props = Utils.readProperties("datasource.properties");
        if (props == null) {
            return;
        }

        cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(props.getProperty("driverClass")); //loads the jdbc driver
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl(props.getProperty("jdbcUrl"));
        cpds.setUser(props.getProperty("username"));
        cpds.setPassword(props.getProperty("password"));
    }

    public static PostgresqlAdapter sharedInstance() {
        if (instance == null) {
            instance = new PostgresqlAdapter();
        }
        return instance;
    }

    // not used
    public List<Long> getTopLevelRelationsByBoundingBox(GeomBox boundingBox){

        List<Long> relations = new LinkedList<Long>();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("select relation_ref from relation_bounding_box where not(? < minlat or ? < minlon or maxlat < ? or maxlon < ?) and " +
                    " relation_ref in (select relation_ref from top_level_relation)");
            statement.setDouble(1, boundingBox.maxlat);
            statement.setDouble(2, boundingBox.maxlon);
            statement.setDouble(3, boundingBox.minlat);
            statement.setDouble(4, boundingBox.minlon);

            rs = statement.executeQuery();
            while (rs.next()){
                relations.add(rs.getLong("relation_ref"));
            }

            statement.close();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                statement.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return relations;
    }

    public List<Long> getWaysByBoundingBox(GeomBox boundingBox){

        List<Long> ways = new LinkedList<Long>();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("select way_ref from way_bounding_box where not(? < minlat or ? < minlon or maxlat < ? or maxlon < ?)");

            statement.setDouble(1, boundingBox.maxlat);
            statement.setDouble(2, boundingBox.maxlon);
            statement.setDouble(3, boundingBox.minlat);
            statement.setDouble(4, boundingBox.minlon);

            rs = statement.executeQuery();
            while (rs.next()){
                ways.add(rs.getLong("way_ref"));
            }

            statement.close();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                statement.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return ways;
    }

    public List<GeomPoint> getPointsOfWay(long way){
        List<GeomPoint> points = new LinkedList<GeomPoint>();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("select id as nd_ref, ST_AsGeoJson(wgs84long_lat) as point_json from node right join (select nd_ref from way_nd where way_ref=?) as way_nodes " +
                    "on " +
                    "node.id=way_nodes.nd_ref ");
            statement.setLong(1, way);

            rs = statement.executeQuery();
            while (rs.next()){
                String pointInJson = rs.getString("point_json");

                ObjectMapper mapper = new ObjectMapper();
                PostgisGeom parsedPoint = mapper.readValue(pointInJson, PostgisGeom.class);

                GeomPoint newPoint = new GeomPoint();
                newPoint.nodeId = rs.getLong("nd_ref");
                newPoint.longitude = parsedPoint.coordinates[0];
                newPoint.latitude = parsedPoint.coordinates[1];

                points.add(newPoint);
            }

            statement.close();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                statement.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return points;
    }

    // not used
    public List<Long> getWaysOfRelation(long relation){
        List<Long> ways = new LinkedList<Long>();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("select ref as way_ref from relation_member where relation_ref=? and type='way'");
            statement.setLong(1, relation);

            rs = statement.executeQuery();
            while (rs.next()){
                ways.add(rs.getLong("way_ref"));
            }

            statement.close();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                statement.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return ways;
    }

    // not used
    public List<Long> getRelationsOfRelation(long relation){
        List<Long> relations = new LinkedList<Long>();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("select ref as relation_ref from relation_member where relation_ref=? and type='relation'");
            statement.setLong(1, relation);

            rs = statement.executeQuery();
            while (rs.next()){
                relations.add(rs.getLong("relation_ref"));
            }

            statement.close();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                statement.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return relations;
    }

    public List<Pair<String, String>> getTags(String tagType, long id){
        List<Pair<String, String>> tags = new LinkedList<Pair<String, String>>();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            if (tagType.compareToIgnoreCase("node") == 0){
                statement = conn.prepareStatement("select k, v from node_tag where nd_ref=?");
            }
            else if (tagType.compareToIgnoreCase("way") == 0){
                statement = conn.prepareStatement("select k, v from way_tag where way_ref=?");
            }
            else if (tagType.compareToIgnoreCase("relation") == 0){
                statement = conn.prepareStatement("select k, v from relation_ref where relation_ref=?");
            }
            else{
                LOGGER.error("unknown tag type:" + tagType);
                return null;
            }

            statement.setLong(1, id);
            rs = statement.executeQuery();
            while (rs.next()){
                tags.add(new Pair<String, String>(rs.getString("k"), rs.getString("v")));
            }

            statement.close();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                statement.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return tags;
    }
}
