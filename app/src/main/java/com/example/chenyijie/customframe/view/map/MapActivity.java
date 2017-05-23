package com.example.chenyijie.customframe.view.map;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.chenyijie.customframe.R;
import com.example.chenyijie.customframe.common.KeyConst;
import com.example.chenyijie.customframe.common.MyApp;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * 很單純的把從activity那邊收到的所有點都pin到map上，這個頁面上沒有實作點擊（需求沒說），
 * 所以點擊後只會在pin上show出該點的名稱
 *
 * Created by chenyijie on 2017/5/20.
 */

public class MapActivity extends FragmentActivity
        implements MapContract.View, OnMapReadyCallback {

    private MapPresenter presenter;
    private GoogleMap googleMap;
    private List<Location> allPeripheryLocation = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        init();
    }

    private void init(){
        presenter = new MapPresenter(this, this);
        presenter.getAllLocations();
        // google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // 因為非同步的關係，有可能在執行onMapReady時資料還在處理中
        if(allPeripheryLocation.size() == 0) return;

        this.googleMap = map;
        Location currentLocation = MyApp.getInstance().getLocation();
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();

        for(Location location : allPeripheryLocation){
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            markerOptions.position(latlng);
            markerOptions.title(location.getProvider());
            googleMap.addMarker(markerOptions);
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, KeyConst.GOOGLE_MAP_ZOOM_SCLAE_SMALL));
    }

    @Override
    public void setLocations(List<Location> locations) {
        this.allPeripheryLocation.addAll(locations);
        onMapReady(googleMap);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        presenter.onStop();
    }
}
