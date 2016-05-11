package com.heaven.osm.controller;


import com.heaven.osm.Utils;
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
}
