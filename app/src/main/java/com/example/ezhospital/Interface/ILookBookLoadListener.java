package com.example.ezhospital.Interface;

import com.example.ezhospital.Model.Banner;

import java.util.List;

public interface ILookBookLoadListener {

    void onLookbookLoadSuccess(List<Banner> banners);
    void onLookbookLoadFailed(String message);
}
