package com.example.ezhospital.Interface;

import com.example.ezhospital.Model.Banner;

import java.util.List;

public interface IBannerLoadListener {

    void onBannerLoadSuccess(List<Banner> banners);
    void onBannerLoadFailed(String message);
}
