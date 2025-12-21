package com.example.tad_bank_t1.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Branch;
import com.example.tad_bank_t1.data.repository.branch.BranchRepository;
import com.example.tad_bank_t1.data.repository.orsMap.OrsRepository;

import org.maplibre.android.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel xử lý logic cho màn hình Map (MapLibre + ORS).
 *
 * Yêu cầu đề bài:
 * - Hiển thị chi nhánh (marker)
 * - Hiển thị vị trí hiện tại
 * - Zoom tới vị trí hiện tại
 * - Tìm chi nhánh gần nhất
 * - Vẽ đường đi (route) từ vị trí hiện tại tới chi nhánh gần nhất
 *
 * Lưu ý: ViewModel KHÔNG lấy location trực tiếp (tránh phụ thuộc Context).
 * Fragment sẽ lấy GPS (FusedLocationProviderClient) rồi gọi updateMyLocation(...).
 */
public class MapOrsViewModel extends ViewModel {

    private final BranchRepository branchRepo;
    private final OrsRepository orsRepository;

    // Danh sách chi nhánh
    private final MutableLiveData<List<Branch>> branches = new MutableLiveData<>(new ArrayList<>());

    // Vị trí hiện tại của người dùng
    private final MutableLiveData<LatLng> myLocation = new MutableLiveData<>();

    // Chi nhánh gần nhất
    private final MutableLiveData<Branch> nearestBranch = new MutableLiveData<>();

    // Đường đi (danh sách điểm) từ user -> nearest branch
    private final MutableLiveData<List<LatLng>> routePath = new MutableLiveData<>();

    // Lỗi (nếu có)
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final MutableLiveData<Double> nearestDistanceMeters = new MutableLiveData<>(0d);

    // ======== Selected branch & distance ========
    /**
     * Chi nhánh được người dùng chọn thủ công. Khi khác null, ViewModel ưu tiên
     * vẽ đường tới chi nhánh này thay vì nearest branch. Nếu null, tự động
     * sử dụng nearest branch.
     */
    private final MutableLiveData<Branch> selectedBranch = new MutableLiveData<>();

    /** Khoảng cách từ vị trí hiện tại tới chi nhánh được chọn (mét). */
    private final MutableLiveData<Double> selectedDistanceMeters = new MutableLiveData<>();


    // Cache để tránh gọi ORS liên tục khi dữ liệu chưa thay đổi
    private Branch lastRoutedBranch;
    private LatLng lastRoutedOrigin;

    public MapOrsViewModel(BranchRepository branchRepo, OrsRepository orsRepository) {
        this.branchRepo = branchRepo;
        this.orsRepository = orsRepository;
    }

    public LiveData<List<Branch>> branches() {
        return branches;
    }

    public LiveData<LatLng> myLocation() {
        return myLocation;
    }

    public LiveData<Branch> nearestBranch() {
        return nearestBranch;
    }

    public LiveData<List<LatLng>> routePath() {
        return routePath;
    }

    public LiveData<String> error() {
        return error;
    }


    public LiveData<Double> nearestDistanceMeters() {
        return nearestDistanceMeters;
    }

    /**
     * Trả về chi nhánh được người dùng chọn thủ công. Khi null, ứng dụng
     * hiển thị thông tin nearest branch.
     */
    public LiveData<Branch> selectedBranch() {
        return selectedBranch;
    }

    /** Trả về khoảng cách tới chi nhánh được chọn (mét). */
    public LiveData<Double> selectedDistanceMeters() {
        return selectedDistanceMeters;
    }

    /**
     * Được gọi khi người dùng chọn một chi nhánh trên bản đồ. Hàm này sẽ lưu
     * chi nhánh đã chọn và yêu cầu vẽ đường từ vị trí hiện tại tới chi nhánh đó.
     * Nếu chưa có vị trí hiện tại hoặc chi nhánh null, hàm không làm gì.
     */
    public void selectBranch(Branch branch) {
        selectedBranch.setValue(branch);
        if (branch != null) {
            requestRouteToBranch(branch);
        }
    }

    /**
     * Tính toán khoảng cách và gửi yêu cầu định tuyến tới ORS cho chi nhánh được chọn.
     * Hàm này sẽ cập nhật selectedDistanceMeters và routePath. Nếu hai điểm quá gần (<5m),
     * không gửi yêu cầu ORS để tránh lỗi, chỉ cập nhật selectedDistanceMeters và clear route.
     */
    private void requestRouteToBranch(Branch branch) {
        LatLng origin = myLocation.getValue();
        if (origin == null || branch == null) return;

        double meters = haversineMeters(
                origin.getLatitude(), origin.getLongitude(),
                branch.getLatitude(), branch.getLongitude()
        );
        selectedDistanceMeters.setValue(meters);

        // cập nhật cache để tránh gọi lại với cùng origin + branch
        lastRoutedBranch = branch;
        lastRoutedOrigin = origin;

        if (meters < 5.0) {
            // quá gần: không cần tính route, chỉ clear polyline
            routePath.postValue(new ArrayList<>());
            return;
        }

        LatLng dest = new LatLng(branch.getLatitude(), branch.getLongitude());
        orsRepository.getWalkingRoute(origin, dest, new OrsRepository.Callback() {
            @Override
            public void onSuccess(List<LatLng> points) {
                routePath.postValue(points);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
            }
        });
    }

    /**
     * Load danh sách chi nhánh (mock hoặc real repo).
     */
    public void loadBranches() {
        try {
            List<Branch> data = branchRepo.getBranches();
            branches.setValue(data);
            // Sau khi có branches, thử compute nearest + route nếu đã có location
            computeNearestAndRouteIfPossible();
        } catch (Exception e) {
            error.setValue(e.getMessage());
        }
    }

    /**
     * Fragment gọi hàm này sau khi lấy được vị trí hiện tại.
     */
    public void updateMyLocation(LatLng location) {
        myLocation.setValue(location);
        computeNearestAndRouteIfPossible();
    }

    /**
     * Tìm chi nhánh gần nhất và gọi ORS để lấy route.
     */
    private void computeNearestAndRouteIfPossible() {
        LatLng origin = myLocation.getValue();
        List<Branch> branchList = branches.getValue();

        if (origin == null || branchList == null || branchList.isEmpty()) return;

        // Nếu user đã chọn chi nhánh, ưu tiên vẽ đường tới chi nhánh đó
        Branch selected = selectedBranch.getValue();
        if (selected != null) {
            requestRouteToBranch(selected);
            return;
        }

        // Nếu chưa chọn, tìm nearest branch và vẽ đường tới nó
        Branch nearest = findNearestBranch(origin, branchList);
        if (nearest == null) return;

        nearestBranch.setValue(nearest);

        // Kiểm tra cache để tránh gọi lại ORS khi origin và branch không đổi
        if (lastRoutedBranch != null
                && lastRoutedOrigin != null
                && safeEquals(lastRoutedBranch.getBranchId(), nearest.getBranchId())
                && isSameLocation(lastRoutedOrigin, origin)) {
            return;
        }

        lastRoutedBranch = nearest;
        lastRoutedOrigin = origin;

        LatLng dest = new LatLng(nearest.getLatitude(), nearest.getLongitude());
        orsRepository.getWalkingRoute(origin, dest, new OrsRepository.Callback() {
            @Override
            public void onSuccess(List<LatLng> points) {
                routePath.postValue(points);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
            }
        });
    }

    // hàm tiện ích
    private boolean safeEquals(String a, String b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    private Branch findNearestBranch(LatLng origin, List<Branch> branchList) {
        Branch nearest = null;
        double bestMeters = Double.MAX_VALUE;

        for (Branch b : branchList) {
            double meters = haversineMeters(
                    origin.getLatitude(), origin.getLongitude(),
                    b.getLatitude(), b.getLongitude()
            );
            if (meters < bestMeters) {
                bestMeters = meters;
                nearest = b;
            }
        }

        // ✅ bắn distance ra UI
        if (bestMeters != Double.MAX_VALUE) {
            nearestDistanceMeters.postValue(bestMeters);
        }
        return nearest;
    }


    /**
     * So sánh vị trí gần bằng nhau (độ lệch rất nhỏ).
     */
    private boolean isSameLocation(LatLng a, LatLng b) {
        double dLat = Math.abs(a.getLatitude() - b.getLatitude());
        double dLng = Math.abs(a.getLongitude() - b.getLongitude());
        return dLat < 1e-6 && dLng < 1e-6;
    }

    /**
     * Haversine distance (mét).
     */
    private double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000.0; // bán kính Trái Đất (m)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
