package com.example.tad_bank_t1.data.remote.dto;

import java.util.List;

public class OrsDirectionsResponse {
    public List<Feature> features;

    public static class Feature {
        public Geometry geometry;
    }

    public static class Geometry {
        public List<List<Double>> coordinates; // [[lng,lat], [lng,lat], ...]
    }
}
