package com.example.tad_bank_t1.ui.fragment.customer.ggmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Branch;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MapBranchFragment extends Fragment implements OnMapReadyCallback, UiConfig {
    private GoogleMap googleMap;



    @Override
    public String getAppBarTitle() {
        return getString(R.string.tim_kiem_chi_nhanh);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_branch, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    public List<Branch> getData() {
        List<Branch> data = new ArrayList<>();
        data.add(new Branch(1, "Chi nhánh 1", "ATM", "Địa chỉ 1", "Tỉnh 1", 10.7349906, 106.6983554, "09890909", "0989098mail"));
        data.add(new Branch(2, "Chi nhánh 2", "ATM", "Địa chỉ 2", "Tỉnh 2", 10.7349236, 106.7006434, "09890909", "0989098mail"));
        data.add(new Branch(3, "Chi nhánh 3", "ATM", "Địa chỉ 3", "Tỉnh 3", 10.7325392, 106.6963412, "09890909", "0989"));

        return data;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        Log.d("MAP_DEBUG", "Map is ready!");
// Hiển thị vị trí hiện tại
        enableMyLocation();

        // Load danh sách chi nhánh từ cơ sở dữ liệu
        List<Branch> branches = getData();

        // Thêm marker cho từng chi nhánh
        for (Branch b : branches) {
            LatLng pos = new LatLng(b.getLatitude(), b.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(b.getName())
                    .snippet(b.getAddress()));

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        }
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }
}