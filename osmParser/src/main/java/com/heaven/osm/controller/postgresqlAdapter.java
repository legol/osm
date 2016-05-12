package com.heaven.osm.controller;


import com.heaven.osm.Utils;
import com.heaven.osm.model.OSMNode;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by chenjie3 on 2016/5/11.
 */
public class PostgresqlAdapter {

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

    public boolean saveNode(OSMNode node){
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
            if (node.tag.size() > 0){
                statement = conn.prepareStatement("INSERT INTO node_tag(nd_ref, k, v) VALUES (?, ?, ?)");

                for (int i = 0; i < node.tag.size(); i++){
                    String k = node.tag.get(i).getKey();
                    String v = node.tag.get(i).getValue();

                    statement.setLong(1, id);
                    statement.setString(2, k);
                    statement.setString(3, v);

                    rowsAffacted = statement.executeUpdate();
                    if (rowsAffacted == 0) {
                        statement.close();
                        conn.close();
                        return false;
                    }
                }
            }

            conn.commit();
            conn.setAutoCommit(true);

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

        return true;
    }
}
