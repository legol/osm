package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.GenerateImageRequest;
import com.heaven.osm.imagegenerator.model.GeomBox;
import com.heaven.osm.imagegenerator.model.PostgresqlAdapter;
import javafx.scene.shape.Path;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.LinkedList;


@Controller
public class ImageGenerator {
    private static final Logger LOGGER = Logger.getLogger(ImageGenerator.class);

    @RequestMapping("/test")
    @ResponseBody
    public Object test(HttpServletRequest request, HttpServletResponse response) {
        return "hello osm image generator";
    }

    @RequestMapping(value = "/getImage")
    public void getImage(HttpServletRequest request, HttpServletResponse response){

        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = loader.getResourceAsStream("test.jpg");
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            ImageIO.write(bufferedImage, "jpeg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] imgByte = jpegOutputStream.toByteArray();

        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = null;
        try {
            responseOutputStream = response.getOutputStream();
            responseOutputStream.write(imgByte);
            responseOutputStream.flush();
            responseOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // for GET request
    @RequestMapping(value = "/map")
    public void generateImage2(HttpServletRequest request, HttpServletResponse response) {
        GenerateImageRequest generateImageRequest = new GenerateImageRequest();

        generateImageRequest.imageWidth = 256;
        generateImageRequest.imageHeight = 256;
        generateImageRequest.boundingBox = new GeomBox();

        double minlat = Double.parseDouble(request.getParameter("minlat"));
        double minlot = Double.parseDouble(request.getParameter("minlon"));

        generateImageRequest.boundingBox.minlat = minlat;
        generateImageRequest.boundingBox.maxlat = minlat + 0.004;
        generateImageRequest.boundingBox.minlon = minlot;
        generateImageRequest.boundingBox.maxlon = minlot + 0.005;

        generateImage(request, generateImageRequest, response);
    }

    // for POST request
    @RequestMapping(value = "/generateImage")
    public void generateImage(HttpServletRequest request, @RequestBody GenerateImageRequest generateImageRequest,
                              HttpServletResponse response){

        LinkedList<Pair<String, String>> tags = new LinkedList<Pair<String, String>>();
        tags.add(new Pair<String, String>("building", "yes"));
        boolean shouldDraw = LevelOfDetailController.sharedInstance().shouldDraw(95, "way", tags);

        ByteArrayOutputStream jpegOutputStream = null;
        try {
            BufferedImage bufferedImage = new BufferedImage(generateImageRequest.imageWidth, generateImageRequest.imageHeight, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = bufferedImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            OSMDrawer.sharedInstance().drawOSM(generateImageRequest.boundingBox,
                    generateImageRequest.imageWidth, generateImageRequest.imageHeight,
                    g);

//            PathDrawer.sharedInstance().drawPath(generateImageRequest.boundingBox,
//                    generateImageRequest.imageWidth, generateImageRequest.imageHeight,
//                    g,
//                    3315887351L, 3508237910L);

            jpegOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpeg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] imgByte = jpegOutputStream.toByteArray();

        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = null;
        try {
            responseOutputStream = response.getOutputStream();
            responseOutputStream.write(imgByte);
            responseOutputStream.flush();
            responseOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
