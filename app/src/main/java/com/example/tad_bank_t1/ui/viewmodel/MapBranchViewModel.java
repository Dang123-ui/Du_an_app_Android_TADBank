package com.example.tad_bank_t1.ui.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Branch;
import com.example.tad_bank_t1.data.repository.branch.BranchRepository;
import com.example.tad_bank_t1.data.repository.map.RoutesRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MapBranchViewModel extends ViewModel {

    private final BranchRepository branchRepo;
    private final RoutesRepository routesRepo;

    private final MutableLiveData<List<Branch>> branches = new MutableLiveData<>();
    private final MutableLiveData<LatLng> myLocation = new MutableLiveData<>();
    private final MutableLiveData<Branch> nearest = new MutableLiveData<>();
    private final MutableLiveData<List<LatLng>> routePath = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private Branch lastRoutedBranch;
    private LatLng lastRoutedOrigin;


    public MapBranchViewModel(BranchRepository branchRepo, RoutesRepository routesRepo) {
        this.branchRepo = branchRepo;
        this.routesRepo = routesRepo;
    }

    public LiveData<List<Branch>> branches() { return branches; }
    public LiveData<LatLng> myLocation() { return myLocation; }
    public LiveData<Branch> nearest() { return nearest; }
    public LiveData<List<LatLng>> routePath() { return routePath; }
    public LiveData<String> error() { return error; }

    public void loadBranches() {
        branches.setValue(branchRepo.getBranches());
    }

    public void setMyLocation(LatLng latLng) {
        myLocation.setValue(latLng);

        List<Branch> list = branches.getValue();
        if (list == null || list.isEmpty()) return;

        Branch best = findNearest(latLng, list);
        nearest.setValue(best);

        // auto route (throttle)
        if (best != null && shouldReRoute(latLng, best)) {
            requestRouteTo(best);
        }
    }

    public void requestRouteTo(Branch branch) {
        LatLng origin = myLocation.getValue();
        if (origin == null || branch == null) return;

        LatLng dest = new LatLng(branch.getLatitude(), branch.getLongitude());
        routesRepo.getShortestWalkingRoute(origin, dest, new RoutesRepository.Callback() {
            @Override
            public void onSuccess(List<LatLng> path, int distanceMeters) {
                lastRoutedOrigin = origin;
                lastRoutedBranch = branch;
                routePath.postValue(path);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
            }
        });
    }

    private boolean shouldReRoute(LatLng origin, Branch branch) {
        if (lastRoutedBranch == null || lastRoutedOrigin == null) return true;
        if (lastRoutedBranch.getBranchId() != branch.getBranchId()) return true;

        float[] out = new float[1];
        Location.distanceBetween(
                lastRoutedOrigin.latitude, lastRoutedOrigin.longitude,
                origin.latitude, origin.longitude,
                out
        );
        return out[0] >= 50f; // user đi > 50m thì gọi lại
    }

    private Branch findNearest(LatLng me, List<Branch> list) {
        Branch best = null;
        float bestD = Float.MAX_VALUE;

        for (Branch b : list) {
            float[] out = new float[1];
            Location.distanceBetween(me.latitude, me.longitude, b.getLatitude(), b.getLongitude(), out);
            if (out[0] < bestD) {
                bestD = out[0];
                best = b;
            }
        }
        return best;
    }
}
