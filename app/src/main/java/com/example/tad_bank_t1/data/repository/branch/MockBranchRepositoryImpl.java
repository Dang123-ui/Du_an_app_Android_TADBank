package com.example.tad_bank_t1.data.repository.branch;

import com.example.tad_bank_t1.data.model.Branch;

import java.util.ArrayList;
import java.util.List;

public class MockBranchRepositoryImpl implements BranchRepository{
    @Override
    public List<Branch> getBranches() {
        List<Branch> data = new ArrayList<>();
        data.add(new Branch(1, "Chi nhánh 1", "ATM", "Địa chỉ 1", "Tỉnh 1", 10.7349906, 106.6983554, "09890909", "mail1"));
        data.add(new Branch(2, "Chi nhánh 2", "ATM", "Địa chỉ 2", "Tỉnh 2", 10.7349236, 106.7006434, "09890909", "mail2"));
        data.add(new Branch(3, "Chi nhánh 3", "ATM", "Địa chỉ 3", "Tỉnh 3", 10.7325392, 106.6963412, "09890909", "mail3"));
        return data;
    }
}
