package com.example.chenyijie.customframe.view.detail;

import android.graphics.drawable.Drawable;

import com.example.chenyijie.customframe.bean.ViewInfo;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by chenyijie on 2017/5/18.
 */

public interface DetailContract {
    interface View{
        void setImage(Drawable drawable);
        void setTitle(String title);
        void setPersonalRating(String personalRating);
        void setAvgRatingTitle(String avgRatingTitle);
        void setAvgRating(float avgRating);
        void setCategory(String category);
        void setPhoneNum(String phoneNum);
        void setCost(String cost);
        void setActiveTime(String activeTime);
        void setRestDay(String restDay);
        void setAddress(String address);
        void setLatLng(LatLng latLng);
        void setDistance(String distance);
        void getPeripheryViews(List<ViewInfo> peripheryViews);
        void setSelectedView(ViewInfo selectedView);
    }

    interface Presenter{
        void loadDetailData();
        void findPeripheryViews();
        void goToDetailActivity(ViewInfo selectedView);
        void onStop();
    }
}
