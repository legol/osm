package com.heaven.osmPathFinder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.Utils;
import com.heaven.osmPathFinder.model.GenerateImageRequest;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;

/**
 * Created by ChenJie3 on 2016/4/14.
 */
public class ServiceInvoker {
    private static final Logger LOGGER = Logger.getLogger(ServiceInvoker.class);

    public static byte[] loadMap(GenerateImageRequest generateImageRequest){
        ObjectMapper mapper = new ObjectMapper();

        String requestInJson = null;
        try {
            requestInJson = mapper.writeValueAsString(generateImageRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error(String.format("unknown error happened while trying to send request"));
            return null;
        }

        byte[] responseBuf = Utils.httpDownload(String.format("http://127.0.0.1:8081/osmImageGenerator/generateImage"),
                requestInJson);

        return responseBuf;
    }
}
