package com.example.tad_bank_t1.data.remote.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrsDirectionsRequest {
    public List<List<Double>> coordinates; // [[lng,lat],[lng,lat]]

    // Comment: truyền đúng thứ tự lng/lat để khỏi nhầm
    public OrsDirectionsRequest(double oLng, double oLat, double dLng, double dLat) {
        coordinates = new ArrayList<>();
        coordinates.add(Arrays.asList(oLng, oLat));
        coordinates.add(Arrays.asList(dLng, dLat));
    }

}

