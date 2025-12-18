package com.example.tad_bank_t1.data.remote.dto;

import java.util.List;

public class ComputeRoutesResponse {
    public List<Route> routes;

    public static class Route {
        public Integer distanceMeters;
        public Polyline polyline;
    }

    public static class Polyline {
        public String encodedPolyline;
    }
}
