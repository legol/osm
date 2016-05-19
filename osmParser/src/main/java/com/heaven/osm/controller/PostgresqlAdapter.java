package com.heaven.osm.controller;


import com.heaven.osm.Utils;
import com.heaven.osm.model.*;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

    private boolean saveTag(List<Pair<String, String>> tag, Connection conn, String tagType, long id){
        if (tag.size() > 0) {
            PreparedStatement statement = null;
            try {

                if (tagType.compareToIgnoreCase("node") == 0){
                    statement = conn.prepareStatement("INSERT INTO node_tag(nd_ref, k, v) VALUES (?, ?, ?)");
                }
                else if (tagType.compareToIgnoreCase("way") == 0){
                    statement = conn.prepareStatement("INSERT INTO way_tag(way_ref, k, v) VALUES (?, ?, ?)");
                }
                else if (tagType.compareToIgnoreCase("relation") == 0){
                    statement = conn.prepareStatement("INSERT INTO relation_tag(relation_ref, k, v) VALUES (?, ?, ?)");
                }
                else{
                    LOGGER.error("unknown tag type:" + tagType);
                    return false;
                }

                for (int i = 0; i < tag.size(); i++) {
                    String k = tag.get(i).getKey();
                    String v = tag.get(i).getValue();

                    statement.setLong(1, id);
                    statement.setString(2, k);
                    statement.setString(3, v);

                    int rowsAffacted = statement.executeUpdate();
                    if (rowsAffacted == 0) {
                        return false;
                    }
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public boolean saveNode(OSMNode node) {
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = cpds.getConnection();

            conn.setAutoCommit(false); // make sure the node and its tags are inserted in the same time.

            // save node
            statement = conn.prepareStatement("INSERT INTO node(id, visible, version, changeset, \"timestamp\", \"user\", uid, wgs84long_lat) " +
                    "VALUES " +
                    "(?, ?, ?, ?, to_timestamp(?, 'YYYY-MM-DD HH24:MI:SS '), ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326))");

            long id = node.attr.containsKey("id") ? Long.parseLong(node.attr.get("id")) : 0L;
            boolean visible = node.attr.containsKey("visible") ? Boolean.parseBoolean(node.attr.get("visible")) : true;
            long version = node.attr.containsKey("version") ? Long.parseLong(node.attr.get("version")) : 0L;
            long changeset = node.attr.containsKey("changeset") ? Long.parseLong(node.attr.get("changeset")) : 0L;
            String timestamp = node.attr.containsKey("timestamp") ? node.attr.get("timestamp") : "";
            timestamp = timestamp.replace('T', ' ').replace('Z', ' ');
            String user = node.attr.containsKey("user") ? node.attr.get("user") : "";
            long uid = node.attr.containsKey("uid") ? Long.parseLong(node.attr.get("uid")) : 0L;
            double lon = node.attr.containsKey("lon") ? Double.parseDouble(node.attr.get("lon")) : 0.0;
            double lat = node.attr.containsKey("lat") ? Double.parseDouble(node.attr.get("lat")) : 0.0;

            statement.setLong(1, id);
            statement.setBoolean(2, visible);
            statement.setLong(3, version);
            statement.setLong(4, changeset);
            statement.setString(5, timestamp);
            statement.setString(6, user);
            statement.setLong(7, uid);
            statement.setDouble(8, lon);
            statement.setDouble(9, lat);

            int rowsAffacted = statement.executeUpdate();
            if (rowsAffacted == 0) {
                statement.close();
                conn.close();
                return false;
            }
            statement.close();

            // save node_tag
            if (saveTag(node.tag, conn, "node", id) == false){
                statement.close();
                conn.close();
                return false;
            }

            // save to db
            conn.commit();
            conn.setAutoCommit(true);

            statement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();

            try {
                statement.close();
                conn.close();

                return false;
            } catch (SQLException e1) {
                e1.printStackTrace();

                return false;
            }
        }

        return true;
    }

    public boolean saveWay(OSMWay way) {
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = cpds.getConnection();

            conn.setAutoCommit(false); // make sure the way, way_tag and way_nd are inserted in the same time.

            // save way
            statement = conn.prepareStatement("INSERT INTO way(id, visible, version, changeset, \"timestamp\", \"user\", uid) " +
                    " VALUES " +
                    "(?, ?, ?, ?, to_timestamp(?, 'YYYY-MM-DD HH24:MI:SS '), ?, ?)");

            long id = way.attr.containsKey("id") ? Long.parseLong(way.attr.get("id")) : 0L;
            boolean visible = way.attr.containsKey("visible") ? Boolean.parseBoolean(way.attr.get("visible")) : true;
            long version = way.attr.containsKey("version") ? Long.parseLong(way.attr.get("version")) : 0L;
            long changeset = way.attr.containsKey("changeset") ? Long.parseLong(way.attr.get("changeset")) : 0L;
            String timestamp = way.attr.containsKey("timestamp") ? way.attr.get("timestamp") : "";
            timestamp = timestamp.replace('T', ' ').replace('Z', ' ');
            String user = way.attr.containsKey("user") ? way.attr.get("user") : "";
            long uid = way.attr.containsKey("uid") ? Long.parseLong(way.attr.get("uid")) : 0L;

            statement.setLong(1, id);
            statement.setBoolean(2, visible);
            statement.setLong(3, version);
            statement.setLong(4, changeset);
            statement.setString(5, timestamp);
            statement.setString(6, user);
            statement.setLong(7, uid);

            int rowsAffacted = statement.executeUpdate();
            if (rowsAffacted == 0) {
                statement.close();
                conn.close();
                return false;
            }
            statement.close();

            // save way_tag
            if (saveTag(way.tag, conn, "way", id) == false){
                statement.close();
                conn.close();
                return false;
            }
            statement.close();

            // save way_nd
            if (way.nd.size() > 0) {
                statement = conn.prepareStatement("INSERT INTO way_nd(way_ref, nd_ref, \"order\") VALUES (?, ?, ?)");

                for (int i = 0; i < way.nd.size(); i++){
                    long nd_ref = Long.parseLong(way.nd.get(i));

                    statement.setLong(1, id);
                    statement.setLong(2, nd_ref);
                    statement.setLong(3, i);

                    rowsAffacted = statement.executeUpdate();
                    if (rowsAffacted == 0) {
                        statement.close();
                        conn.close();
                        return false;
                    }
                }
            }

            // save to db
            conn.commit();
            conn.setAutoCommit(true);

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();

            try {
                conn.close();
                statement.close();

                return false;
            } catch (SQLException e1) {
                e1.printStackTrace();

                return false;
            }
        }

        return true;
    }

    public boolean saveRelation(OSMRelation relation) {
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = cpds.getConnection();

            conn.setAutoCommit(false); // make sure the way, way_tag and way_nd are inserted in the same time.

            // save way
            statement = conn.prepareStatement("INSERT INTO relation(id, visible, version, changeset, \"timestamp\", \"user\", uid) VALUES (?, ?, ?, ?, to_timestamp(?, 'YYYY-MM-DD HH24:MI:SS '), ?, ?)");

            long id = relation.attr.containsKey("id") ? Long.parseLong(relation.attr.get("id")) : 0L;
            boolean visible = relation.attr.containsKey("visible") ? Boolean.parseBoolean(relation.attr.get("visible")) : true;
            long version = relation.attr.containsKey("version") ? Long.parseLong(relation.attr.get("version")) : 0L;
            long changeset = relation.attr.containsKey("changeset") ? Long.parseLong(relation.attr.get("changeset")) : 0L;
            String timestamp = relation.attr.containsKey("timestamp") ? relation.attr.get("timestamp") : "";
            timestamp = timestamp.replace('T', ' ').replace('Z', ' ');
            String user = relation.attr.containsKey("user") ? relation.attr.get("user") : "";
            long uid = relation.attr.containsKey("uid") ? Long.parseLong(relation.attr.get("uid")) : 0L;

            statement.setLong(1, id);
            statement.setBoolean(2, visible);
            statement.setLong(3, version);
            statement.setLong(4, changeset);
            statement.setString(5, timestamp);
            statement.setString(6, user);
            statement.setLong(7, uid);

            int rowsAffacted = statement.executeUpdate();
            if (rowsAffacted == 0) {
                statement.close();
                conn.close();
                return false;
            }
            statement.close();

            // save relation_tag
            if (saveTag(relation.tag, conn, "relation", id) == false){
                statement.close();
                conn.close();
                return false;
            }
            statement.close();

            // save relation_member
            if (relation.member.size() > 0) {
                statement = conn.prepareStatement("INSERT INTO relation_member(relation_ref, type, ref, role) VALUES (?, ?, ?, ?)");

                for (int i = 0; i < relation.member.size(); i++){
                    OSMMember member = relation.member.get(i);

                    statement.setLong(1, id);
                    statement.setString(2, member.type);
                    statement.setLong(3, Long.parseLong(member.ref));
                    statement.setString(4, member.role);

                    rowsAffacted = statement.executeUpdate();
                    if (rowsAffacted == 0) {
                        statement.close();
                        conn.close();
                        return false;
                    }
                }
            }

            // save to db
            conn.commit();
            conn.setAutoCommit(true);

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();

            try {
                conn.close();
                statement.close();

                return false;
            } catch (SQLException e1) {
                e1.printStackTrace();

                return false;
            }
        }

        return true;
    }

    // return all nodes that are related to a relation.
    public List<GeomPoint> getPointsOfRelation(long relation_ref){
        List<GeomPoint> points = new LinkedList<GeomPoint>();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            // 1. select all nodes that are on the ways of the relation.
            statement = conn.prepareStatement("select ST_X(node.wgs84long_lat) as lon, ST_Y(node.wgs84long_lat) as lat from node " +
                    "right join " +
                    "(select distinct way_nd.nd_ref as nd_ref from way_nd where way_nd.way_ref in " +
                    "(select relation_member.ref as way_ref from relation_member where relation_member.type='way' and relation_member.relation_ref=?)) as nodes " +
                    "on nodes.nd_ref=node.id");

            statement.setLong(1, relation_ref);
            rs = statement.executeQuery();
            while (rs.next()){
                GeomPoint point = new GeomPoint();
                point.longitude = rs.getDouble("lon");
                point.latitude = rs.getDouble("lat");

                points.add(point);
            }
            statement.close();

            // 2. select all nodes that are referenced by the relation
            statement = conn.prepareStatement("select ST_X(node.wgs84long_lat) as lon, ST_Y(node.wgs84long_lat) as lat from node " +
                    "right join " +
                    "(select relation_member.ref as nd_ref from relation_member where relation_member.type='node' and relation_member.relation_ref=?) as nodes " +
                    "on nodes.nd_ref=node.id");

            statement.setLong(1, relation_ref);
            rs = statement.executeQuery();
            while (rs.next()){
                GeomPoint point = new GeomPoint();
                point.longitude = rs.getDouble("lon");
                point.latitude = rs.getDouble("lat");

                points.add(point);
            }
            statement.close();

            // 3. select all relation,aka relation_ref2, that are referenced by this relation
            // 4. for all relation_ref2, call this function again.
            statement = conn.prepareStatement("select relation_member.ref as relation_ref2 from relation_member where relation_member.type='relation' and relation_member.relation_ref=?");

            statement.setLong(1, relation_ref);
            rs = statement.executeQuery();
            while (rs.next()){
                List<GeomPoint> newPoints = getPointsOfRelation(rs.getLong("relation_ref2"));

                points.addAll(newPoints);
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

        return points;
    }

    // return all relation_ref
    public List<Long> getRelations(){
        List<Long> relations = new LinkedList<Long>();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            // 1. select all nodes that are on the ways of the relation.
            statement = conn.prepareStatement("select relation.id as relation_ref from relation");
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

    // save bounding box of a relation
    public boolean saveRelationBoundingBox(long relation_ref, GeomBox boundingBox){
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = cpds.getConnection();

            conn.setAutoCommit(false); // make sure the node and its tags are inserted in the same time.

            // save bounding_box
            statement = conn.prepareStatement("insert into relation_bounding_box(relation_ref, minlon, minlat, maxlon, maxlat) values (?, ?, ?, ?, ?)");

            statement.setLong(1, relation_ref);
            statement.setDouble(2, boundingBox.minlon);
            statement.setDouble(3, boundingBox.minlat);
            statement.setDouble(4, boundingBox.maxlon);
            statement.setDouble(5, boundingBox.maxlat);

            int rowsAffacted = statement.executeUpdate();
            if (rowsAffacted == 0) {
                statement.close();
                conn.close();
                return false;
            }
            statement.close();

            // save to db
            conn.commit();
            conn.setAutoCommit(true);

            statement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();

            try {
                statement.close();
                conn.close();

                return false;
            } catch (SQLException e1) {
                e1.printStackTrace();

                return false;
            }
        }

        return true;
    }

    public List<Long> getWays(){
        List<Long> ways = new LinkedList<Long>();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            // 1. select all nodes that are on the ways of the relation.
            statement = conn.prepareStatement("select way.id as way_ref from way");
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

    public List<Long> getHighways(){
        List<Long> ways = new LinkedList<Long>();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            // 1. select all nodes that are on the ways of the relation.
            statement = conn.prepareStatement("select distinct way_ref from way_tag where k='highway'");
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

    public List<GeomPoint> getPointsOfWay(long way_ref){
        List<GeomPoint> points = new LinkedList<GeomPoint>();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            // select all nodes that are on the way.
            statement = conn.prepareStatement("select id as nd_ref, ST_X(node.wgs84long_lat) as lon, ST_Y(node.wgs84long_lat) as lat from node " +
                    "right join " +
                    "(select way_nd.nd_ref from way_nd where way_nd.way_ref=? order by \"order\" desc) as nodes " +
                    "on node.id = nodes.nd_ref");
            statement.setLong(1, way_ref);
            rs = statement.executeQuery();
            while (rs.next()){
                GeomPoint point = new GeomPoint();
                point.nodeId = rs.getLong("nd_ref");
                point.longitude = rs.getDouble("lon");
                point.latitude = rs.getDouble("lat");

                points.add(point);
            }

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();

            try {
                statement.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return points;
    }

    public boolean saveWayBoundingBox(long way_ref, GeomBox boundingBox){
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = cpds.getConnection();

            conn.setAutoCommit(false); // make sure the node and its tags are inserted in the same time.

            // save bounding_box
            statement = conn.prepareStatement("insert into way_bounding_box(way_ref, minlon, minlat, maxlon, maxlat) values (?, ?, ?, ?, ?)");

            statement.setLong(1, way_ref);
            statement.setDouble(2, boundingBox.minlon);
            statement.setDouble(3, boundingBox.minlat);
            statement.setDouble(4, boundingBox.maxlon);
            statement.setDouble(5, boundingBox.maxlat);

            int rowsAffacted = statement.executeUpdate();
            if (rowsAffacted == 0) {
                statement.close();
                conn.close();
                return false;
            }
            statement.close();

            // save to db
            conn.commit();
            conn.setAutoCommit(true);

            statement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();

            try {
                statement.close();
                conn.close();

                return false;
            } catch (SQLException e1) {
                e1.printStackTrace();

                return false;
            }
        }

        return true;
    }

    public boolean saveConnectivity(GeomPoint p1, GeomPoint p2, long way_ref){
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = cpds.getConnection();

            conn.setAutoCommit(false); // make sure the node and its tags are inserted in the same time.

            // save bounding_box
            statement = conn.prepareStatement("insert into connectivity(nd_ref1, nd_ref2, way_ref, nd1_wgs84long_lat, nd2_wgs84long_lat) values " +
                    "(?, ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326), ST_SetSRID(ST_MakePoint(?, ?), 4326))");

            statement.setLong(1, p1.nodeId);
            statement.setLong(2, p2.nodeId);
            statement.setLong(3, way_ref);
            statement.setDouble(4, p1.longitude);
            statement.setDouble(5, p1.latitude);
            statement.setDouble(6, p2.longitude);
            statement.setDouble(7, p2.latitude);

            int rowsAffacted = statement.executeUpdate();
            if (rowsAffacted == 0) {
                statement.close();
                conn.close();
                return false;
            }
            statement.close();

            // save to db
            conn.commit();
            conn.setAutoCommit(true);

            statement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();

            try {
                statement.close();
                conn.close();

                return false;
            } catch (SQLException e1) {
                e1.printStackTrace();

                return false;
            }
        }

        return true;
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
