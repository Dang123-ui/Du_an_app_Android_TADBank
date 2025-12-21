package com.example.tad_bank_t1.data.repository.branch;

import com.example.tad_bank_t1.data.model.Branch;

import java.util.ArrayList;
import java.util.List;

public class MockBranchRepositoryImpl implements BranchRepository{
    @Override
    public List<Branch> getBranches() {
        List<Branch> data = new ArrayList<>();
        // Lotte Mart Quận 7
        data.add(new Branch(
                "lotte_quan7", "Chi nhánh Lotte Quận 7", "BRANCH",
                "Lotte Mart, Tân Phong, Quận 7, TP.HCM",
                "Ho Chi Minh City", 10.7400827, 106.7019863,
                "02812345678", "lotteq7@example.com"
        ));
        // Crescent Mall
        data.add(new Branch(
                "crescent_mall", "Chi nhánh Crescent Mall", "BRANCH",
                "Crescent Mall, Tôn Dật Tiên, Phú Mỹ Hưng, Quận 7, TP.HCM",
                "Ho Chi Minh City", 10.7308782, 106.7170376,
                "02823456789", "crescent@example.com"
        ));
        // GO! Nguyễn Thị Thập
        data.add(new Branch(
                "go_nguyen_thi_thap", "Chi nhánh GO! Nguyễn Thị Thập", "BRANCH",
                "GO! Nguyễn Thị Thập, Quận 7, TP.HCM",
                "Ho Chi Minh City", 10.7375523, 106.7255176,
                "02834567890", "go_nguyenthithap@example.com"
        ));
        // Đại học RMIT Nam Sài Gòn
        data.add(new Branch(
                "rmit_south_saigon", "Chi nhánh Đại học RMIT", "BRANCH",
                "Đại học RMIT, Nguyễn Văn Linh, Tân Phong, Quận 7, TP.HCM",
                "Ho Chi Minh City", 10.7292848, 106.6939146,
                "02845678901", "rmit@example.com"
        ));
        // Sân cầu lông T793
        data.add(new Branch(
                "t793", "Chi nhánh T793", "BRANCH",
                "Sân cầu lông T793, Quận 7, TP.HCM",
                "Ho Chi Minh City", 10.7471861, 106.6985080,
                "02856789012", "t793@example.com"
        ));
        https://www.google.com/maps/@10.7351117,106.700835,18z?entry=ttu&g_ep=EgoyMDI1MTIwOS4wIKXMDSoASAFQAw%3D%3D
        // đường D6
        data.add(new Branch(
                "duongD6", "Chi nhánh Đường D6", "BRANCH",
                "Đường D6, Tân Hưng , Quận 7, TP.HCM",
                "Ho Chi Minh City", 10.7333359, 106.6970632,
                "02856789012", "t793@example.com"
        ));
        return data;
    }

}
