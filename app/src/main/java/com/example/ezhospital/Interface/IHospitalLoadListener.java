package com.example.ezhospital.Interface;

import java.util.List;

public interface IHospitalLoadListener {
    void onAllSalonLoadSuccess(List<String> areaNameList);
    void onAllSalonLoadFailed(String message);
}
