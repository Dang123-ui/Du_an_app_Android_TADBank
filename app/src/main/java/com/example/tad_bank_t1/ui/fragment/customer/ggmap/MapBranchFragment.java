package com.example.tad_bank_t1.ui.fragment.customer.ggmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.tad_bank_t1.BuildConfig;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Branch;
import com.example.tad_bank_t1.data.repository.branch.MockBranchRepositoryImpl;
import com.example.tad_bank_t1.data.repository.map.RoutesRepository;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.viewmodel.MapBranchViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapBranchFragment extends Fragment implements OnMapReadyCallback, UiConfig {

    private static final int REQ_LOCATION = 1001;
    private static final float NEARBY_RADIUS_METERS = 3000f;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private MapBranchViewModel vm;

    private final Map<Marker, Branch> markerMap = new HashMap<>();
    private Polyline routePolyline;
    private boolean walkWarningShown = false;

    @Override
    public String getAppBarTitle() {
        return getString(R.string.tim_kiem_chi_nhanh);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        vm = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MapBranchViewModel(
                        new MockBranchRepositoryImpl(),
                        new RoutesRepository(BuildConfig.MAPS_API_KEY_PLUS)
                );
            }
        }).get(MapBranchViewModel.class);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                if (result.getLastLocation() == null) return;
                LatLng me = new LatLng(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude());
                vm.setMyLocation(me);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_branch, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        bindObservers();

        vm.loadBranches();

        googleMap.setOnMarkerClickListener(marker -> {
            Branch b = markerMap.get(marker);
            if (b != null) {
                vm.requestRouteTo(b);
                return true;
            }
            return false;
        });

        ensureLocationPermissionThenStart();
    }

    private void bindObservers() {
        vm.branches().observe(getViewLifecycleOwner(), branches -> {
            renderBranches(branches, vm.myLocation().getValue());
        });

        vm.myLocation().observe(getViewLifecycleOwner(), me -> {
            if (me == null) return;
            renderBranches(vm.branches().getValue(), me);
        });

        vm.routePath().observe(getViewLifecycleOwner(), path -> {
            if (path == null || path.isEmpty() || googleMap == null) return;

            // Google yêu cầu cảnh báo khi hiển thị WALK route (WALK là beta). :contentReference[oaicite:6]{index=6}
            if (!walkWarningShown) {
                Toast.makeText(requireContext(),
                        "Lưu ý: tuyến đi bộ (WALK) có thể thiếu lối đi bộ/sidewalk ở một số nơi.",
                        Toast.LENGTH_LONG).show();
                walkWarningShown = true;
            }

            if (routePolyline != null) routePolyline.remove();
            routePolyline = googleMap.addPolyline(new PolylineOptions()
                    .addAll(path)
                    .width(12f));

            LatLngBounds.Builder b = new LatLngBounds.Builder();
            for (LatLng p : path) b.include(p);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(b.build(), 120));
        });

        vm.error().observe(getViewLifecycleOwner(), msg -> {
            if (msg == null) return;
            Toast.makeText(requireContext(), "Routes API: " + msg, Toast.LENGTH_LONG).show();
        });
    }

    private void renderBranches(List<Branch> branches, LatLng me) {
        if (googleMap == null || branches == null) return;

        googleMap.clear();
        markerMap.clear();

        // Re-enable myLocation sau clear
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        boolean hasPoint = false;

        if (me != null) {
            bounds.include(me);
            hasPoint = true;
        }

        for (Branch br : branches) {
            LatLng pos = new LatLng(br.getLatitude(), br.getLongitude());

            // nếu có user thì lọc nearby
            if (me != null) {
                float[] out = new float[1];
                android.location.Location.distanceBetween(me.latitude, me.longitude, pos.latitude, pos.longitude, out);
                if (out[0] > NEARBY_RADIUS_METERS) continue;
            }

            Marker m = googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(br.getName())
                    .snippet(br.getAddress()));

            if (m != null) {
                markerMap.put(m, br);
            }

            bounds.include(pos);
            hasPoint = true;
        }

        if (hasPoint) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 120));
        }
    }

    private void ensureLocationPermissionThenStart() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION);
            return;
        }
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (googleMap != null &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        LocationRequest req = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .setMinUpdateDistanceMeters(10)
                .build();

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.requestLocationUpdates(req, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (googleMap != null) ensureLocationPermissionThenStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(requireContext(),
                        "Bạn cần cấp quyền vị trí để xem chi nhánh gần nhất và vẽ đường đi.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
