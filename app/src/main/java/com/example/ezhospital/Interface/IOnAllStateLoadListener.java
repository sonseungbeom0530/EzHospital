package com.example.ezhospital.Interface;

import com.example.ezhospital.Model.Floor;

import java.util.List;

public interface IOnAllStateLoadListener {
    void onAllStateLoadSuccess(List<Floor> floorList);
    void onAllStateLoadFailed(String message);
}
