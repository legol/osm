package com.heaven.osm.imagegenerator.controller;

import com.heaven.osm.imagegenerator.model.GenerateImageRequest;
import com.heaven.osm.imagegenerator.model.GeomBox;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


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

    @RequestMapping(value = "/generateImage")
    public void generateImage(HttpServletRequest request, @RequestBody GenerateImageRequest generateImageRequest,
                              HttpServletResponse response){

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

}
