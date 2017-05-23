package com.example.chenyijie.customframe.view.detail;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.chenyijie.customframe.R;
import com.example.chenyijie.customframe.bean.ViewInfo;
import com.example.chenyijie.customframe.common.KeyConst;
import com.example.chenyijie.customframe.common.MyApp;
import com.example.chenyijie.customframe.common.RealmManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 這個頁面主要是負責某一點的詳細內容呈現，除了單純的塞值以外
 * 比較特別的就是Google Map的應用以及周邊景點
 *
 * Created by chenyijie on 2017/5/18.
 */

public class DetailActivity extends AppCompatActivity
        implements DetailContract.View, OnMapReadyCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView title;
    @BindView(R.id.detail_content)
    LinearLayout detailContent;
    @BindView(R.id.detail_image)
    ImageView detailImage;
    @BindView(R.id.detail_title)
    TextView detailTitle;
    @BindView(R.id.detail_category)
    TextView detailCategory;
    @BindView(R.id.detail_average_rating_title)
    TextView detailAverageRatingTitle;
    @BindView(R.id.detail_average_rating)
    RatingBar detailAverageRating;
    @BindView(R.id.detail_personal_rating)
    RatingBar detailPersonalRating;
    @BindView(R.id.detail_cost)
    TextView detailCost;
    @BindView(R.id.detail_activie_time)
    TextView detailActivieTime;
    @BindView(R.id.detail_rest_day)
    TextView detailRestDay;
    @BindView(R.id.detail_phone_number)
    TextView detailPhoneNumber;
    @BindView(R.id.detail_address)
    TextView detailAddress;
    @BindView(R.id.map_distance)
    TextView mapDistance;
    @BindView(R.id.periphery_layout_scroll_view)
    HorizontalScrollView periphery_layout_scroll_view;
    @BindView(R.id.periphery_layout)
    LinearLayout peripheryLayout;

    private GoogleMap googleMap;
    private DetailPresenter presenter;
    private LatLng selectedLatLng;
    private List<ViewInfo> peripheryViews;
    private ViewInfo selectedView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        // appcompatActivity不能用requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        init();
    }

    private void init(){
        initPresenter();
        initToolbar();
        initMap();
    }

    private void initPresenter(){
        presenter = new DetailPresenter(this, this);
        presenter.loadDetailData();
        presenter.findPeripheryViews();
    }

    /**
     * 特別要注意的是原生的setTitle會建立靠左偏移的title，
     * 如果要放在中間，得自己建立一個textview
     *
     * */
    private void initToolbar(){
        title.setText(getString(R.string.detail_toolbar_title));
        toolbar.setNavigationIcon(R.drawable.back_icon);
        setSupportActionBar(toolbar);
    }

    /**
     * 顯示自訂的Actionbar items
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    /**
     * 可以初始化toolbar上按鈕的icon
     * */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem favoriteItem = menu.findItem(R.id.detail_menu_favorite);
        if(selectedView.getFavorite()){
            favoriteItem.setIcon(getResources().getDrawable(R.drawable.favorite_added));
        }else{
            favoriteItem.setIcon(getResources().getDrawable(R.drawable.favorite_notadded));
        }
        MenuItem bookItem = menu.findItem(R.id.detail_menu_book);
        if(selectedView.getBooked()) {
            bookItem.setIcon(getResources().getDrawable(R.drawable.collection_pressed));
        }else{
            bookItem.setIcon(getResources().getDrawable(R.drawable.collection_normal));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * toolbar上的item
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.detail_menu_book:
                if(selectedView.getBooked()){
                    menuItem.setIcon(getResources().getDrawable(R.drawable.collection_normal));
                }else{
                    menuItem.setIcon(getResources().getDrawable(R.drawable.collection_pressed));
                }
                // 記得同步更新DB
                RealmManager.getInstance(this).updateBooked(
                        selectedView.getLongitude(), selectedView.getLatitude());
                selectedView.setBooked(!selectedView.getBooked());
                return true;
            case R.id.detail_menu_favorite:
                if(selectedView.getFavorite()){
                    menuItem.setIcon(getResources().getDrawable(R.drawable.favorite_notadded));
                }else{
                    menuItem.setIcon(getResources().getDrawable(R.drawable.favorite_added));
                }
                // 記得同步更新DB
                RealmManager.getInstance(this).updateFavorite(
                        selectedView.getLongitude(), selectedView.getLatitude());
                selectedView.setFavorite(!selectedView.getFavorite());
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void setImage(Drawable drawable) {
        this.detailImage.setImageDrawable(drawable);
    }

    @Override
    public void setTitle(String title) {
        this.detailTitle.setText(title);
    }

    @Override
    public void setPersonalRating(String personalRating) {}

    @Override
    public void setAvgRatingTitle(String avgRatingTitle) {
        this.detailAverageRatingTitle.setText(avgRatingTitle);
    }

    @Override
    public void setAvgRating(float avgRating) {
        this.detailAverageRating.setRating(avgRating);
    }

    @Override
    public void setCategory(String category) {
        this.detailCategory.setText(category);
    }

    @Override
    public void setPhoneNum(String phoneNum) {
        this.detailPhoneNumber.setText(phoneNum);
    }

    @Override
    public void setCost(String cost) {
        this.detailCost.setText(cost);
    }

    @Override
    public void setActiveTime(String activeTime) {
        this.detailActivieTime.setText(activeTime);
    }

    @Override
    public void setRestDay(String restDay) {
        this.detailRestDay.setText(restDay);
    }

    @Override
    public void setAddress(String address) {
        this.detailAddress.setText(address);
    }

    @Override
    public void setLatLng(LatLng latLng) {
        this.selectedLatLng = latLng;
    }

    @Override
    public void setDistance(String distance) {
        mapDistance.setText(distance);
    }

    /**
     * 周邊景點的尋找方式：
     *
     * 進入詳細頁面時身上會帶著一個被選中的點(selectedView)還有過濾條件（但目前寫死），
     * 利用這個view以及條件，在presenter裡面用Rx的方式找出應該周邊景點
     *
     * */
    @Override
    public void getPeripheryViews(List<ViewInfo> peripheryViews) {
        if(peripheryViews == null || peripheryViews.size() == 0){
            this.peripheryViews = null;
            hidePeripheryView();
        }else{
            this.peripheryViews = new ArrayList<>(peripheryViews);
            showPeripheryView();
        }
    }

    private void hidePeripheryView(){
        periphery_layout_scroll_view.setVisibility(View.GONE);
    }

    private void showPeripheryView(){
        periphery_layout_scroll_view.setVisibility(View.VISIBLE);

        for(final ViewInfo peripheryView : peripheryViews){
            LinearLayout peripheryItem = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.periphery_item, null);
            peripheryItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.goToDetailActivity(peripheryView);
                }
            });
            ImageView view = (ImageView) peripheryItem.findViewById(R.id.periphery_image);
            view.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
            TextView text = (TextView) peripheryItem.findViewById(R.id.periphery_title);
            text.setText(peripheryView.getName());

            peripheryLayout.addView(peripheryItem);
        }
    }

    @Override
    public void setSelectedView(ViewInfo selectedView) {
        this.selectedView = selectedView;
    }

    /**
     * 使用google map
     * */
    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * 點擊地圖後會直接帶資訊到Google map上
     * */
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.addMarker(new MarkerOptions().position(this.selectedLatLng)
                .title(this.detailTitle.getText().toString()));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // 決定地圖的顯示方式
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, KeyConst.GOOGLE_MAP_ZOOM_SCLAE_LARGE));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Location source = MyApp.getInstance().getLocation();
                Location dest = convertLatLngToLocation(latLng);
                // 跳轉到Google map
                Intent intent = new Intent(Intent.ACTION_VIEW
                        , Uri.parse("http://maps.google.com/maps?"
                        + "saddr="+ source.getLatitude() + "," + dest.getLongitude()
                        + "&daddr=" + dest.getLatitude() + "," + dest.getLongitude()));
                intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
    }

    private Location convertLatLngToLocation(LatLng latLng){
        Location location = new Location(detailTitle.getText().toString());
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        presenter.onStop();
    }
}
