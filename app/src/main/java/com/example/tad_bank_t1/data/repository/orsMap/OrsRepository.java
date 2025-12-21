package com.example.tad_bank_t1.data.repository.orsMap;

import com.example.tad_bank_t1.data.remote.api.OrsDirectionsService;
import com.example.tad_bank_t1.data.remote.config.ApiOrsMapConfig;
import com.example.tad_bank_t1.data.remote.dto.OrsDirectionsRequest;
import com.example.tad_bank_t1.data.remote.dto.OrsDirectionsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class OrsRepository {
    private final OrsDirectionsService service;
    private final String apiKey;

    public OrsRepository(String apiKey) {
        this.apiKey = apiKey;

        service = ApiOrsMapConfig.getClient().create(OrsDirectionsService.class);
    }

    public interface Callback {
        void onSuccess(List<org.maplibre.android.geometry.LatLng> points);
        void onError(String message);
    }

    public void getWalkingRoute(org.maplibre.android.geometry.LatLng origin,
                                org.maplibre.android.geometry.LatLng dest,
                                Callback callback) {

        OrsDirectionsRequest body = new OrsDirectionsRequest(origin.getLongitude(), origin.getLatitude(),
                dest.getLongitude(), dest.getLatitude());


        service.getWalkingRoute(apiKey, body).enqueue(new retrofit2.Callback<OrsDirectionsResponse>() {
            @Override
            public void onResponse(Call<OrsDirectionsResponse> call, Response<OrsDirectionsResponse> res) {
                OrsDirectionsResponse data = res.body();
                if (!res.isSuccessful() || data == null || data.features == null || data.features.isEmpty()
                        || data.features.get(0).geometry == null || data.features.get(0).geometry.coordinates == null) {
                    callback.onError("ORS error: " + res.code());
                    return;
                }

                List<org.maplibre.android.geometry.LatLng> out = new ArrayList<>();
                for (List<Double> c : data.features.get(0).geometry.coordinates) {
                    // c = [lng, lat]
                    out.add(new org.maplibre.android.geometry.LatLng(c.get(1), c.get(0)));
                }
                callback.onSuccess(out);
            }

            @Override
            public void onFailure(Call<OrsDirectionsResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
