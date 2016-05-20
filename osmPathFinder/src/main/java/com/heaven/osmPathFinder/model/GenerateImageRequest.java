package com.heaven.osmPathFinder.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by chenjie3 on 2016/5/11.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class GenerateImageRequest {
    @JsonProperty("boundingBox")
    public GeomBox boundingBox;

    @JsonProperty("image_width")
    public int imageWidth;

    @JsonProperty("image_height")
    public int imageHeight;
}
