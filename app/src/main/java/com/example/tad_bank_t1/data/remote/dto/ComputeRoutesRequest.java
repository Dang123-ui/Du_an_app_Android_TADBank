package com.example.tad_bank_t1.data.remote.dto;

public class ComputeRoutesRequest {
    public Waypoint origin;
    public Waypoint destination;
    public String travelMode;                 // "WALK"
    public boolean computeAlternativeRoutes;  // true để chọn route ngắn nhất theo distanceMeters
    public String units;                      // "METRIC" (optional)

    public ComputeRoutesRequest(double oLat, double oLng, double dLat, double dLng) {
        origin = new Waypoint(oLat, oLng);
        destination = new Waypoint(dLat, dLng);
        travelMode = "WALK";                  // RouteTravelMode.WALK :contentReference[oaicite:3]{index=3}
        computeAlternativeRoutes = true;
        units = "METRIC";
        // IMPORTANT: đừng set routingPreference khi WALK (chỉ hợp lệ với DRIVE/TWO_WHEELER). :contentReference[oaicite:4]{index=4}
    }

    public static class Waypoint {
        public Location location;
        public Waypoint(double lat, double lng) { location = new Location(lat, lng); }
    }

    public static class Location {
        public LatLng latLng;
        public Location(double lat, double lng) { latLng = new LatLng(lat, lng); }
    }

    public static class LatLng {
        public double latitude;
        public double longitude;
        public LatLng(double lat, double lng) { latitude = lat; longitude = lng; }
    }
}
