package com.heaven.osmPathFinder.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by chenjie3 on 2016/5/11.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class GeomBox {
    @JsonProperty("minlon")
    public double minlon;

    @JsonProperty("minlat")
    public double minlat;

    @JsonProperty("maxlon")
    public double maxlon;

    @JsonProperty("maxlat")
    public double maxlat;
}
