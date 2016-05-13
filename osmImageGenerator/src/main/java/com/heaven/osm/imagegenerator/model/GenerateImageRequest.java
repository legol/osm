package com.heaven.osm.imagegenerator.model;

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
    GeomBox boundingBox;

}
