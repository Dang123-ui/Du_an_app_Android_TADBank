package com.example.tad_bank_t1.data.repository.map;

import com.example.tad_bank_t1.data.remote.api.RoutesService;
import com.example.tad_bank_t1.data.remote.config.ApiMapConfig;
import com.example.tad_bank_t1.data.remote.dto.ComputeRoutesRequest;
import com.example.tad_bank_t1.data.remote.dto.ComputeRoutesResponse;
import com.google.android.gms.maps.model.*;
import com.google.maps.android.PolyUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RoutesRepository {
    private final RoutesService api;
    private final String apiKey;

    public RoutesRepository(String apiKey) {
        this.apiKey = apiKey;
        this.api = ApiMapConfig.getClient().create(RoutesService.class);
    }

    public interface Callback {
        void onSuccess(List<LatLng> path, int distanceMeters);
        void onError(String message);
    }

    public void getShortestWalkingRoute(LatLng origin, LatLng destination, Callback cb) {
        String fieldMask = "routes.distanceMeters,routes.polyline.encodedPolyline"; // docs example :contentReference[oaicite:5]{index=5}

        ComputeRoutesRequest body = new ComputeRoutesRequest(
                origin.latitude, origin.longitude,
                destination.latitude, destination.longitude
        );

        api.computeRoutes(apiKey, fieldMask, body).enqueue(new retrofit2.Callback<ComputeRoutesResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ComputeRoutesResponse> call,
                                   retrofit2.Response<ComputeRoutesResponse> res) {

                ComputeRoutesResponse data = res.body();
                if (!res.isSuccessful() || data == null || data.routes == null || data.routes.isEmpty()) {
                    cb.onError("No route / API error: " + res.code());
                    return;
                }

                // Chọn route có distanceMeters nhỏ nhất
                ComputeRoutesResponse.Route best = null;
                int bestMeters = Integer.MAX_VALUE;

                for (ComputeRoutesResponse.Route r : data.routes) {
                    int m = (r != null && r.distanceMeters != null) ? r.distanceMeters : Integer.MAX_VALUE;
                    if (m < bestMeters && r != null && r.polyline != null && r.polyline.encodedPolyline != null) {
                        bestMeters = m;
                        best = r;
                    }
                }

                if (best == null) {
                    cb.onError("No valid polyline");
                    return;
                }

                List<LatLng> points = PolyUtil.decode(best.polyline.encodedPolyline);
                cb.onSuccess(points, bestMeters);
            }

            @Override
            public void onFailure(retrofit2.Call<ComputeRoutesResponse> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }
}

