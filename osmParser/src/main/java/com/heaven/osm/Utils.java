package com.heaven.osm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Random;

/**
 * Created by ChenJie3 on 2015/10/23.
 */
public class Utils {

    static Random rand = new Random();

    // read a property file from resource folder
    static public Properties readProperties(String filename){
        Properties props = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(filename);
        try {
            props.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return props;
    }

    public static String md5(String original) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

        md.update(original.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }

        return sb.toString();
    }

    public static int randInt(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }

    public static String generateToken(long uid, String password) {
        String secret = "chenjie love cxf!";
        long rand = randInt(19800202, 19901116);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String token_raw = String.format("%d %d %s %s %d", uid, rand, secret, password, timestamp.getTime());
        return md5(token_raw);
    }

}