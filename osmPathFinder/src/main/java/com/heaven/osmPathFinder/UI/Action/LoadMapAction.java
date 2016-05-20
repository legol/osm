package com.heaven.osmPathFinder.UI.Action;

import com.heaven.osmPathFinder.ServiceInvoker;
import com.heaven.osmPathFinder.UI.DoubleImageLayerPanel;
import com.heaven.osmPathFinder.UI.TestData;

import javax.imageio.ImageIO;
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


    private DoubleImageLayerPanel mapPanel = null;

    public LoadMapAction(DoubleImageLayerPanel _mapPanel){
        mapPanel = _mapPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        byte[] responseBuf = ServiceInvoker.loadMap(TestData.sharedInstance().generateImageRequest);
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
