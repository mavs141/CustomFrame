package com.example.chenyijie.customframe.view.map;

import android.location.Location;

import java.util.List;

/**
 * Created by chenyijie on 2017/5/20.
 */

public interface MapContract {
    interface View{
        void setLocations(List<Location> locations);
    }
    interface Presenter{
        void getAllLocations();
        void onStop();
    }
}
