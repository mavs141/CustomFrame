package com.example.chenyijie.customframe.view.map;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.chenyijie.customframe.bean.ViewInfo;
import com.example.chenyijie.customframe.common.MyApp;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by chenyijie on 2017/5/20.
 */

public class MapPresenter implements MapContract.Presenter {

    private Context context;
    private MapContract.View view;
    private CompositeSubscription compositeSubscription;

    public MapPresenter(Context context, MapContract.View view){
        this.context = context;
        this.view = view;

        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void getAllLocations() {
        Log.d("Test:::","開始獲取位置資訊");
        List<ViewInfo> allPeripheryViews = MyApp.getInstance().getAllPeripheryViews();
        Subscription subscription = Observable.from(allPeripheryViews)
                .map(new Func1<ViewInfo, Location>() {
                    @Override
                    public Location call(ViewInfo viewInfo) {
                        Location location = new Location(viewInfo.getName());
                        location.setLongitude(Double.parseDouble(viewInfo.getLongitude()));
                        location.setLatitude(Double.parseDouble(viewInfo.getLatitude()));
                        return location;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Location>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Test:::","Map Presenter "+e.getMessage());
                    }

                    @Override
                    public void onNext(List<Location> locaions) {
                        view.setLocations(locaions);
                    }
                });
        compositeSubscription.add(subscription);
    }

    @Override
    public void onStop() {
        if(compositeSubscription != null && !compositeSubscription.isUnsubscribed())
            compositeSubscription.unsubscribe();
    }
}
