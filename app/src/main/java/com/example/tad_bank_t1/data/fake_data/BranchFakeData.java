package com.example.tad_bank_t1.data.fake_data;

import com.example.tad_bank_t1.data.model.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchFakeData {
    private static List<Branch> data = new ArrayList<>();
    public static List<Branch> getData() {
        data.add(new Branch(1, "Chi nhánh 1", "ATM", "Địa chỉ 1", "Tỉnh 1", 10.7349906, 106.6983554, "09890909", "0989098mail"));
        data.add(new Branch(2, "Chi nhánh 2", "ATM", "Địa chỉ 2", "Tỉnh 2", 10.7349236, 106.7006434, "09890909", "0989098mail"));
        data.add(new Branch(3, "Chi nhánh 3", "ATM", "Địa chỉ 3", "Tỉnh 3", 10.7325392, 106.6963412, "09890909", "0989"));

        return data;
    }
}
