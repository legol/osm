package com.heaven.osm.controller;


import com.heaven.osm.Utils;
import com.heaven.osm.model.OSMMember;
import com.heaven.osm.model.OSMNode;
import com.heaven.osm.model.OSMRelation;
import com.heaven.osm.model.OSMWay;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    // insert into node("id", "wgs84long_lat") values (10, ST_SetSRID(ST_MakePoint(123.4, 567.8), 4326))
    public void test() {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("select id, ST_AsText(wgs84long_lat) as pos from node");
            rs = statement.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("id") + ":" + rs.getString("pos"));
            }

            statement.close();
            rs.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();

            try {
                statement.close();
                rs.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
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
                statement = conn.prepareStatement("INSERT INTO way_nd(way_ref, nd_ref) VALUES (?, ?)");

                for (int i = 0; i < way.nd.size(); i++){
                    long nd_ref = Long.parseLong(way.nd.get(i));

                    statement.setLong(1, id);
                    statement.setLong(2, nd_ref);

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

}
