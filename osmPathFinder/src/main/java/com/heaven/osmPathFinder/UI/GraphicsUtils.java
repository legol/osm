package com.heaven.osmPathFinder.UI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Random;

/**
 * Created by ChenJie3 on 2015/10/23.
 */
public class GraphicsUtils {

    public static BufferedImage createTransparentImage(int width, int height){
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return img;
    }

    public static Graphics2D createTransparentGraphics(BufferedImage img, float alpha){
        Graphics2D g = img.createGraphics();
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g.setComposite(comp );

        return g;
    }

}