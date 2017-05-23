package com.example.chenyijie.customframe.view.detail;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.example.chenyijie.customframe.R;
import com.example.chenyijie.customframe.bean.ViewInfo;
import com.example.chenyijie.customframe.common.KeyConst;
import com.example.chenyijie.customframe.common.MyApp;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 1. 計算出目前點選的點周邊的景點，目前需求是3KM、景點、距離，但是也可以依照main activity的filter
 *    來改變篩選條件(目前註解掉了)。
 *
 * 2. 最下面的周邊也有和1.相同的功能，會依照我點選的點去找出周遭前十個景點，所以得到的結果也許會和main activity
 *    不同（因為基準點改變了
 *
 * Created by chenyijie on 2017/5/18.
 */

public class DetailPresenter implements DetailContract.Presenter {
    private Context context;
    private DetailContract.View view;
    private ViewInfo selectedView ;
    private CompositeSubscription compositeSubscription;

    public DetailPresenter(Context context, DetailContract.View view){
        this.context = context;
        this.view = view;
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void loadDetailData() {
        selectedView = MyApp.getInstance().getSelectedView();
        view.setSelectedView(selectedView);

        view.setSelectedView(selectedView);
        view.setImage(context.getResources().getDrawable(R.drawable.default_image));
        view.setTitle(selectedView.getName());
        view.setCost(context.getResources().getString(R.string.detail_cost, selectedView.getCost()));
        view.setCategory(selectedView.getCategory());
        view.setAvgRatingTitle(context.getResources().getString(R.string.detail_average_rating_title
                , selectedView.getComment()));
        view.setAvgRating(selectedView.getRating());
        view.setAddress(selectedView.getAdd());
        view.setLatLng(getLatLng(selectedView.getLongitude(), selectedView.getLatitude()));
        view.setPhoneNum(context.getString(R.string.detail_phone_num));
        view.setActiveTime(context.getString(R.string.detail_active_time));
        view.setRestDay(context.getString(R.string.detail_rest_day));
        view.setDistance(context.getString(R.string.detail_distance, selectedView.getDistance()));
    }

    private LatLng getLatLng(String lng, String lat){
        Double latitude = Double.parseDouble(lat);
        Double longtitude = Double.parseDouble(lng);
        return new LatLng(latitude, longtitude);
    }

    @Override
    public void findPeripheryViews() {
        // 先取出篩選條件
//        final String distanceFilter = MyApp.getInstance().getDistanceFilter();
//        final String categoryFilter = MyApp.getInstance().getCategoryFilter();
//        final String orderFilter = MyApp.getInstance().getOrderFilter();
        final String distanceFilter = context.getString(R.string.selection_distance_three);
        final String categoryFilter = context.getString(R.string.category_viewpoint);
        final String orderFilter = context.getString(R.string.selection_order_distance);
        // 基準點
        final int selectedDistance = selectedView.getDistance();
        // 先取得全部的列表
        Subscription subscription = Observable.from(MyApp.getInstance().getViewInfos())
                // 然後把被選中的點的distance記住，接著去比較任何一點和該點的距離
                // 假設現在filter為3km，則找出所有相減過後絕對值為3000的點
                .filter(new Func1<ViewInfo, Boolean>() {
                    @Override
                    public Boolean call(ViewInfo viewInfo) {
                        int absValue = Math.abs(viewInfo.getDistance() - selectedDistance);
                        return ( absValue != 0 // 0 代表自己
                                && absValue < MyApp.getInstance().getDistanceCondition(distanceFilter)
                                && viewInfo.getCategory().equals(categoryFilter));
                    }
                })
                // 只取10筆
                .take(KeyConst.MAX_PERIPHERY_COUNT)
                // 把結果都依照距離/評比順序排序
                .toSortedList(new Func2<ViewInfo, ViewInfo, Integer>() {
                    @Override
                    public Integer call(ViewInfo viewInfo, ViewInfo viewInfo2) {
                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            if (orderFilter.equals(context.getString(R.string.selection_order_distance))) {
                                return ((Integer) viewInfo.getDistance()).compareTo(viewInfo2.getDistance());
                            } else {
                                return Float.compare(viewInfo.getRating(), viewInfo2.getRating());
                            }
                        }else{
                            if(orderFilter.equals(context.getString(R.string.selection_order_distance))){
                                return Integer.compare(viewInfo.getDistance(), viewInfo2.getDistance());
                            }else{
                                return Float.compare(viewInfo.getRating(), viewInfo2.getRating());
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ViewInfo>>() {
                    @Override
                    public void onCompleted() {
                        Log.d("Test:::","Detail Presenter "+ "success");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Test:::","Detail Presenter "+ e.getMessage());
                    }

                    @Override
                    public void onNext(List<ViewInfo> viewInfos) {
                        for(ViewInfo viewInfo: viewInfos){
                            Location location = new Location(viewInfo.getName());
                            location.setLatitude(Double.parseDouble(viewInfo.getLatitude()));
                            location.setLongitude(Double.parseDouble(viewInfo.getLongitude()));
                        }
                        view.getPeripheryViews(viewInfos);
                    }
                });

        compositeSubscription.add(subscription);
    }

    // 因為從detail跳到另外一個detail不會遇到篩選條件改變的狀況，所以直接給selectedView即可
    @Override
    public void goToDetailActivity(ViewInfo selectedView) {
        MyApp.getInstance().setSelectedView(selectedView);
        context.startActivity(new Intent(context, DetailActivity.class));
    }

    @Override
    public void onStop(){
        if(compositeSubscription != null && !compositeSubscription.isUnsubscribed())
            compositeSubscription.unsubscribe();
    }

}
