package com.heaven.osm.imagegenerator.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by ChenJie3 on 2015/9/8.
 */
@Controller
public class ImageGenerator {
    private static final Logger LOGGER = Logger.getLogger(ImageGenerator.class);

    @RequestMapping("/test")
    @ResponseBody
    public Object test(HttpServletRequest request, HttpServletResponse response) {
        return "hello osm image generator";
    }

}
