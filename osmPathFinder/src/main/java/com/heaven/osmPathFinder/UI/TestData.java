package com.heaven.osmPathFinder.UI;

import com.heaven.osmPathFinder.model.GenerateImageRequest;
import com.heaven.osmPathFinder.model.GeomBox;

/**
 * Created by chenjie3 on 2016/5/20.
 */
public class TestData {
    private static TestData instance = null;

    public GenerateImageRequest generateImageRequest = null;

    public TestData(){
        generateImageRequest = new GenerateImageRequest();
        generateImageRequest.imageHeight = 1024;
        generateImageRequest.imageWidth = 1024;
        generateImageRequest.boundingBox = new GeomBox();
        generateImageRequest.boundingBox.minlat = 40.0528;
        generateImageRequest.boundingBox.maxlat = 40.0809;
        generateImageRequest.boundingBox.minlon = 116.3042;
        generateImageRequest.boundingBox.maxlon = 116.3446;
    }

    public static TestData sharedInstance() {
        if (instance == null) {
            instance = new TestData();
        }
        return instance;
    }

}
