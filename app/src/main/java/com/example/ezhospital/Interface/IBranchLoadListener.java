package com.example.ezhospital.Interface;

import com.example.ezhospital.Model.Department;

import java.util.List;

public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<Department> departmentList);
    void onBranchLoadFailed(String message);
}
