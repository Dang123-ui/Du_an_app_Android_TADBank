package com.example.tad_bank_t1.ui.fragment.customer.ggmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.tad_bank_t1.BuildConfig;
import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Branch;
import com.example.tad_bank_t1.data.repository.branch.MockBranchRepositoryImpl;
import com.example.tad_bank_t1.data.repository.orsMap.OrsRepository;
import com.example.tad_bank_t1.databinding.FragmentMapBranchOrsBinding;
import com.example.tad_bank_t1.ui.base.BaseCustomFragment;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.viewmodel.MapOrsViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptor;

import org.maplibre.android.MapLibre;
import org.maplibre.android.annotations.Icon;
import org.maplibre.android.annotations.IconFactory;
import org.maplibre.android.annotations.Marker;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.annotations.Polyline;
import org.maplibre.android.annotations.PolylineOptions;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;

import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Map (MapLibre + ORS) theo yêu cầu đề bài:
 * - Hiển thị vị trí hiện tại của user
 * - Hiển thị các chi nhánh ngân hàng gần đó
 * - Gợi ý đường đi bộ ngắn nhất tới chi nhánh gần nhất
 *
 * Quy ước dự án:
 * - Fragment dùng ViewBinding (biến binding)
 * - Tách initView(), initViewModel(), setUpEvents()
 */
public class MapBranchOrsFragment extends Fragment implements UiConfig, BaseCustomFragment, OnMapReadyCallback {

    private static final int REQ_LOCATION = 1001;
    private static final String OSM_RASTER_STYLE_JSON =
            "{"
                    + "\"version\":8,"
                    + "\"sources\":{"
                    + "  \"osm\":{"
                    + "    \"type\":\"raster\","
                    + "    \"tiles\":[\"https://tile.openstreetmap.org/{z}/{x}/{y}.png\"],"
                    + "    \"tileSize\":256,"
                    + "    \"attribution\":\"© OpenStreetMap contributors\""
                    + "  }"
                    + "},"
                    + "\"layers\":["
                    + "  {\"id\":\"osm\",\"type\":\"raster\",\"source\":\"osm\"}"
                    + "]"
                    + "}";

    private FragmentMapBranchOrsBinding binding;

    // MapLibre
    private MapView mapView;
    private MapLibreMap map;
    private boolean isStyleLoaded = false;

    // Location (lấy vị trí hiện tại)
    private FusedLocationProviderClient fusedClient;

    // ViewModel xử lý logic nearest + route
    private MapOrsViewModel mapOrsVM;

    // Giữ reference để refresh UI (xoá marker/route cũ)
    private final List<Marker> branchMarkers = new ArrayList<>();
    private Marker myMarker;
    private Polyline routeLine;

    private Polyline routeLineBorder;
    private List<LatLng> lastRoutePoints = new ArrayList<>();

    // Map markerId -> Branch để xử lý click vào marker
    private final java.util.Map<Long, Branch> markerBranchMap = new java.util.HashMap<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ✅ BẮT BUỘC: init MapLibre trước khi inflate layout có MapView
        MapLibre.getInstance(requireContext().getApplicationContext());

        binding = FragmentMapBranchOrsBinding.inflate(inflater, container, false);

        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragment();
    }

    @Override
    public void onMapReady(@NonNull MapLibreMap mapLibreMap) {
        this.map = mapLibreMap;

        // Style demo MapLibre (dễ chạy, không cần token)
        map.setStyle(new Style.Builder().fromJson(OSM_RASTER_STYLE_JSON), style -> {
            isStyleLoaded = true;

            // Đăng ký listener cho click marker: user chọn chi nhánh bất kỳ
            map.setOnMarkerClickListener(marker -> {
                Branch b = markerBranchMap.get(marker.getId());
                if (b != null) {
                    mapOrsVM.selectBranch(b);
                    // Hiển thị info window ngắn gọn (tuỳ chọn)
                    marker.showInfoWindow(map, mapView);
                    return true;
                }
                return false;
            });

            // Bật tất cả gestures như Google Maps để người dùng zoom/pan/rotate thoải mái
            org.maplibre.android.maps.UiSettings ui = map.getUiSettings();
            ui.setZoomGesturesEnabled(true);
            ui.setScrollGesturesEnabled(true);
            ui.setRotateGesturesEnabled(true);
            ui.setTiltGesturesEnabled(true);
            ui.setDoubleTapGesturesEnabled(true);
            ui.setQuickZoomGesturesEnabled(true);

            mapOrsVM.loadBranches();
            ensureLocationPermissionThenFetch();
        });

    }

    // ------------------------- BaseCustomFragment -------------------------

    @Override
    public void initView() {
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public void initViewModel() {
        mapOrsVM = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MapOrsViewModel(
                        new MockBranchRepositoryImpl(),
                        new OrsRepository(BuildConfig.OPEN_ROUTE_MAP_KEY)
                );
            }
        }).get(MapOrsViewModel.class);

        // 1) Branches -> render marker
        mapOrsVM.branches().observe(getViewLifecycleOwner(), this::renderBranches);

        // 2) My location -> zoom + marker
        mapOrsVM.myLocation().observe(getViewLifecycleOwner(), this::renderMyLocation);

        // 3) Route -> polyline
        mapOrsVM.routePath().observe(getViewLifecycleOwner(), this::renderRoute);

        // 4) Nearest branch -> hiển thị nếu chưa có chi nhánh được chọn
        mapOrsVM.nearestBranch().observe(getViewLifecycleOwner(), nearest -> {
            if (nearest != null && mapOrsVM.selectedBranch().getValue() == null) {
                Toast.makeText(requireContext(), "Chi nhánh gần nhất: " + nearest.getName(), Toast.LENGTH_SHORT).show();

                binding.txtMapNearestName.setText(nearest.getName());
                binding.txtMapNearestAddress.setText(nearest.getAddress());
            }
        });

        // 5) Error
        mapOrsVM.error().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.trim().isEmpty()) {
                Toast.makeText(requireContext(), "Lỗi: " + msg, Toast.LENGTH_LONG).show();
            }
        });

        // 6) khoảng cách gần nhất -> chỉ hiển thị khi chưa chọn
        mapOrsVM.nearestDistanceMeters().observe(getViewLifecycleOwner(), meters -> {
            if (meters == null) return;
            if (mapOrsVM.selectedBranch().getValue() == null) {
                binding.txtMapNearestDistance.setText(formatDistance(meters));
            }
        });

        // 7) Selected branch -> hiển thị thông tin và quãng đường
        mapOrsVM.selectedBranch().observe(getViewLifecycleOwner(), selected -> {
            if (selected != null) {
                binding.txtMapNearestName.setText(selected.getName());
                binding.txtMapNearestAddress.setText(selected.getAddress());
                // khoảng cách sẽ cập nhật qua selectedDistanceMeters observer
            } else {
                // nếu user bỏ chọn (null) thì hiển thị nearest (nếu có)
                Branch nearest = mapOrsVM.nearestBranch().getValue();
                if (nearest != null) {
                    binding.txtMapNearestName.setText(nearest.getName());
                    binding.txtMapNearestAddress.setText(nearest.getAddress());
                }
            }
        });

        mapOrsVM.selectedDistanceMeters().observe(getViewLifecycleOwner(), meters -> {
            if (meters != null && mapOrsVM.selectedBranch().getValue() != null) {
                binding.txtMapNearestDistance.setText(formatDistance(meters));
            }
        });
    }

    @Override
    public void setUpEvents() {
        // Comment tiếng Việt: hiện tại màn này không có nút bấm.
        // Nếu bạn muốn, có thể thêm nút "My location" để zoom lại vị trí hiện tại.

        // Nút Focus route: zoom lại toàn bộ tuyến đường hiện tại
        binding.btnMapFocusRoute.setOnClickListener(v -> {
            if (lastRoutePoints != null && !lastRoutePoints.isEmpty()) {
                focusRoute(lastRoutePoints);
            } else {
                Toast.makeText(requireContext(), "Chưa có đường đi", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút zoom in/out để phóng to/thu nhỏ bản đồ
        binding.btnZoomIn.setOnClickListener(v -> {
            if (map != null) {
                map.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        binding.btnZoomOut.setOnClickListener(v -> {
            if (map != null) {
                map.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

    }

    // ------------------------- Permission + Location -------------------------

    /**
     * Kiểm tra quyền vị trí. Nếu chưa có -> xin quyền. Nếu có -> lấy vị trí và cập nhật vào ViewModel.
     */
    private void ensureLocationPermissionThenFetch() {
        boolean grantedFine = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean grantedCoarse = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        if (!grantedFine && !grantedCoarse) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_LOCATION
            );
            return;
        }

        fetchLastLocationAndUpdateVm();
    }

    /**
     * Lấy lastLocation (cache) cho nhanh và đủ demo.
     */
    private void fetchLastLocationAndUpdateVm() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedClient.getLastLocation().addOnSuccessListener(loc -> {
            if (loc == null) {
                Toast.makeText(requireContext(), "Không lấy được vị trí hiện tại", Toast.LENGTH_SHORT).show();
                return;
            }

            // Comment tiếng Việt: đưa vị trí vào ViewModel. VM sẽ tự tìm nearest + gọi ORS để lấy route.
            mapOrsVM.updateMyLocation(new LatLng(loc.getLatitude(), loc.getLongitude()));
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_LOCATION) {
            boolean granted = false;
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }

            if (granted) {
                fetchLastLocationAndUpdateVm();
            } else {
                Toast.makeText(requireContext(), "Bạn cần bật quyền vị trí để xem chi nhánh gần nhất", Toast.LENGTH_LONG).show();
            }
        }
    }

    // ------------------------- Render Map -------------------------

    /**
     * Render marker chi nhánh.
     */
    private void renderBranches(List<Branch> branchList) {
        if (map == null || !isStyleLoaded || branchList == null) return;

        // Xoá marker cũ để tránh bị nhân đôi
        for (Marker m : branchMarkers) {
            m.remove();
        }
        branchMarkers.clear();
        markerBranchMap.clear();

        for (Branch b : branchList) {
            LatLng pos = new LatLng(b.getLatitude(), b.getLongitude());
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(b.getName())
                    .snippet(b.getAddress()));
            branchMarkers.add(marker);

            // Lưu mapping để biết marker này thuộc chi nhánh nào khi click
            markerBranchMap.put(marker.getId(), b);
        }

        // Comment tiếng Việt: nếu chưa lấy được vị trí hiện tại (chưa cấp quyền / GPS null)
        // thì zoom tạm tới chi nhánh đầu tiên để user không thấy bản đồ ở "world view".
        if (mapOrsVM.myLocation().getValue() == null && !branchList.isEmpty()) {
            Branch first = branchList.get(0);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(first.getLatitude(), first.getLongitude()),
                    13
            ));
        }
    }

    /**
     * Render vị trí hiện tại + zoom camera.
     */
    private void renderMyLocation(LatLng myLoc) {
        if (map == null || !isStyleLoaded || myLoc == null) return;

        // Zoom tới vị trí hiện tại (đúng yêu cầu đề bài)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));

        // Marker vị trí của tôi
        if (myMarker != null) {
            myMarker.remove();
        }
//        myMarker = map.addMarker(new MarkerOptions()
//                .position(myLoc)
//                .title("Vị trí của tôi")
//                .snippet("Bạn đang ở đây"));
        Icon userIcon = IconFactory.getInstance(requireContext())
                .fromResource(R.drawable.ic_my_location_marker);
        myMarker = map.addMarker(new MarkerOptions()
                .position(myLoc)
                .icon(userIcon)
                .title("Vị trí của bạn")
                .snippet("Bạn đang ở đây"));

    }

    /**
     * Render route (polyline) user -> chi nhánh gần nhất.
     */
    private void renderRoute(List<LatLng> points) {
        if (map == null || !isStyleLoaded || points == null || points.isEmpty()) return;

        lastRoutePoints = points;

        // xoá cũ
        if (routeLine != null) routeLine.remove();
        if (routeLineBorder != null) routeLineBorder.remove();

        // ✅ viền trắng
        routeLineBorder = map.addPolyline(new PolylineOptions()
                .addAll(points)
                .width(12f)
                .color(android.graphics.Color.WHITE));

        // ✅ đường đỏ
        routeLine = map.addPolyline(new PolylineOptions()
                .addAll(points)
                .width(8f)
                .color(android.graphics.Color.parseColor("#FF3B30")));

        // ✅ auto zoom nhìn thấy toàn tuyến
        focusRoute(points);

        Toast.makeText(requireContext(), "Đã vẽ đường đi (" + points.size() + " điểm)", Toast.LENGTH_SHORT).show();
    }


    /*
     * focus route helper
     */
    private void focusRoute(List<LatLng> points) {
        if (map == null || points == null || points.isEmpty()) return;

        double minLat = Double.MAX_VALUE, minLng = Double.MAX_VALUE;
        double maxLat = -Double.MAX_VALUE, maxLng = -Double.MAX_VALUE;

        for (LatLng p : points) {
            minLat = Math.min(minLat, p.getLatitude());
            minLng = Math.min(minLng, p.getLongitude());
            maxLat = Math.max(maxLat, p.getLatitude());
            maxLng = Math.max(maxLng, p.getLongitude());
        }

        org.maplibre.android.geometry.LatLngBounds bounds =
                new org.maplibre.android.geometry.LatLngBounds.Builder()
                        .include(new LatLng(minLat, minLng))
                        .include(new LatLng(maxLat, maxLng))
                        .build();

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
    }

    /*
    helper format khoảng cách
     */
    private String formatDistance(double meters) {
        if (meters < 1000) return String.format("%.0f m", meters);
        return String.format("%.2f km", meters / 1000.0);
    }


    // ------------------------- UiConfig -------------------------

    @Override
    public String getAppBarTitle() {
        // Comment tiếng Việt: để tạm "Map" cho khỏi phụ thuộc string resource.
        // Nếu muốn đa ngôn ngữ thì thêm strings.xml: <string name="map">Bản đồ</string>
        return "Map";
    }

    // ------------------------- MapView lifecycle -------------------------

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        if (mapView != null) mapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mapView != null) mapView.onStop();
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        if (mapView != null) mapView.onDestroy();
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }
}
