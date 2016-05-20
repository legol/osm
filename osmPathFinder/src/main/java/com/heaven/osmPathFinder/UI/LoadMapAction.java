package com.heaven.osmPathFinder.UI;

import com.heaven.osmPathFinder.ServiceInvoker;
import com.heaven.osmPathFinder.model.GenerateImageRequest;
import com.heaven.osmPathFinder.model.GeomBox;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Logger;

/**
 * Created by chenjie3 on 2016/5/20.
 */
public class LoadMapAction implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(LoadMapAction.class));


    private MapPanel mapPanel = null;

    public LoadMapAction(MapPanel _mapPanel){
        mapPanel = _mapPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GenerateImageRequest generateImageRequest = new GenerateImageRequest();

        generateImageRequest.imageHeight = 1024;
        generateImageRequest.imageWidth = 1024;
        generateImageRequest.boundingBox = new GeomBox();
        generateImageRequest.boundingBox.minlat = 40.0528;
        generateImageRequest.boundingBox.maxlat = 40.0809;
        generateImageRequest.boundingBox.minlon = 116.3042;
        generateImageRequest.boundingBox.maxlon = 116.3446;

        byte[] responseBuf = ServiceInvoker.loadMap(generateImageRequest);
        InputStream in = new ByteArrayInputStream(responseBuf);
        try {
            BufferedImage bufferedImage = ImageIO.read(in);
            mapPanel.image = bufferedImage;
            mapPanel.repaint();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

//        try {
//            FileOutputStream fos = null;
//            fos = new FileOutputStream("C:\\Projects\\osm\\testData\\111.jpg");
//            fos.write(responseBuf);
//            fos.close();
//
//            mapPanel.image = ImageIO.read(new File("C:\\Projects\\osm\\testData\\test.jpg"));
//            mapPanel.repaint();
//
//        } catch (FileNotFoundException e1) {
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }

    }
}
