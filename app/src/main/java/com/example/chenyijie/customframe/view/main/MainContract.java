package com.example.chenyijie.customframe.view.main;

import com.example.chenyijie.customframe.bean.ViewInfo;

import java.util.List;

public interface MainContract {
    interface View{
        void getAllData();
        void getDataSuccess(List<ViewInfo> viewInfos);
        void getDataFail(String errMsg);
    }
    interface Presenter{
        void getData(String condition1, String condition2, String condition3);
        void goToDetailActivity(ViewInfo selectedView, String distanceFilter
                , String categoryFilter, String orderFilter);
        void goToShowAllMapsActivity();
        void onStop();
    }
}
