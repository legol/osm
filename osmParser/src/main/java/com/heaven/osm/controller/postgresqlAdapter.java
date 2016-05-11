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

    public void saveNode(OSMNode node){
//        'INSERT INTO "node"(' .
//        '"id", "visible", "version", "changeset", "timestamp", "user", "uid", "wgs84long_lat")' .
//        'VALUES' .
//        '($1, $2, $3, $4, to_timestamp($5, \'YYYY-MM-DD HH24:MI:SS \'), $6, $7, ST_SetSRID(ST_MakePoint($8, $9), 4326))');
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = cpds.getConnection();

            statement = conn.prepareStatement("INSERT INTO node(id, visible, version, changeset, timestamp, user, uid, wgs84long_lat) " +
            "VALUES " +
            "(?, ?, ?, ?, to_timestamp(?, \\'YYYY-MM-DD HH24:MI:SS \\'), ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326))");

            String id = node.attr.containsKey("id") ? node.attr.get("id") : "";
            String visible = node.attr.containsKey("visible") ? node.attr.get("id") : "";

            statement.setString(1, id);
            statement.setString(2, lr.getToken());


            int rowsAffacted = statement.executeUpdate();
            if (rowsAffacted == 0) {
                statement.close();
                conn.close();
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

    }
}
