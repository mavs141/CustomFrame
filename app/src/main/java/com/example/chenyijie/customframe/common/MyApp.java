package com.example.chenyijie.customframe.common;

import android.app.Application;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.chenyijie.customframe.R;
import com.example.chenyijie.customframe.bean.ViewInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 多數頁面會共通用到的東西都放這邊，方便取用
 *
 * Created by chenyijie on 2017/5/18.
 */

public class MyApp extends Application {
    private static MyApp instance;
    // 最原始的data（從json那邊parse出來的
    @Getter @Setter private List<ViewInfo> viewInfos;
    @Getter @Setter private ViewInfo selectedView;
    // 紀錄目前總共有多少viewinfo，按下navigation時用得到
    @Getter @Setter private List<ViewInfo> allPeripheryViews;
    // 拿來搜尋周邊的篩選條件
    @Getter @Setter private String distanceFilter ;
    @Getter @Setter private String categoryFilter ;
    @Getter @Setter private String orderFilter ;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;

    }

    public static MyApp getInstance(){
        return instance;
    }

    /**
     * todo : 重開機會抓不到location...
     * */
    public Location getLocation(){
        Location location = null;
        try {
            LocationManager locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true); // choose the more precious one
            try {
                location = locationManager.getLastKnownLocation(bestProvider);
                if(location == null){
                    location = LocationServiceManager.getInstance(getBaseContext()).getLocation();
                    if(location == null){
                        Log.d("Test:::","Location is still null");
                    }
                }
            }catch (SecurityException e){
                Log.d("Test:::","get loction SecurityException");
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.d("Test:::","get loction Exception");
            e.printStackTrace();
        }
        return location;
    }

    public int getDistanceCondition(String distance) {
        if (distance.equals(getString(R.string.selection_distance_three))) {
            return KeyConst.INTEGER_THREE_KM;
        }else if (distance.equals(getString(R.string.selection_distance_five))) {
            return KeyConst.INTEGER_FIVE_KM;
        }else if(distance.equals(getString(R.string.selection_distance_ten))){
            return KeyConst.INTEGER_TEN_KM;
        }else{
            return KeyConst.INTEGER_NO_LIMIT;
        }
    }

    public String getCurrentDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date current = new Date(System.currentTimeMillis());
        return dateFormat.format(current);
    }
}
