package com.example.chenyijie.customframe.view.splash;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.example.chenyijie.customframe.R;
import com.example.chenyijie.customframe.common.ListManager;
import com.example.chenyijie.customframe.common.MyApp;
import com.example.chenyijie.customframe.common.RealmManager;
import com.example.chenyijie.customframe.database.DBViewInfo;
import com.example.chenyijie.customframe.view.main.MainActivity;
import com.example.chenyijie.customframe.bean.AllViews;
import com.example.chenyijie.customframe.network.ApiManager;
import com.example.chenyijie.customframe.bean.ViewInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * APP開啟時的畫面，我把loading重的工作（透過API抓資料、寫DB）放在這邊
 *
 * Created by chenyijie on 2017/5/18.
 */

public class SplashPresenter implements SplashContract.Presenter {
    final private int ARRAY_SPLIT_COUNT = 1000;
    final private int minRating = 2, maxRating = 10;
    final private int minCost = 100, maxCost = 500;
    final private int minComment = 1, maxComment = 100;
    final private int[] categoryStringIDs = {R.string.category_government, R.string.category_amusement_park
            , R.string.category_viewpoint};

    private Random rand ;
    private Location currentLoc;
    private Context context;
    private SplashContract.View view;
    private CompositeSubscription compositeSubscription;

    public SplashPresenter(Context context, SplashContract.View view){
        this.context = context;
        this.view = view;

        init();
    }

    private void init(){

        rand = new Random();
        compositeSubscription = new CompositeSubscription();

        currentLoc = MyApp.getInstance().getLocation();
    }

    // 注意這邊不用計算距離，為了保持行為一致，我們從DB取資料出來再一次計算就好
    private ViewInfo createCompleteViewInfo(ViewInfo viewInfo){
        ViewInfo info = viewInfo;
        info.setRating(getRandomFloat(minRating, maxRating));
        info.setComment(getRandomInt(minComment, maxComment));
        info.setCost(getRandomInt(minCost, maxCost));
        info.setCategory(getRandomString(categoryStringIDs));
//        info.setDistance(calculateDistance(info.getLongitude(), info.getLatitude(), info.getName()));
        info.setBooked(false);
        info.setFavorite(false);
        return info;
    }

    private String getRandomString(int[] strings){
        int min = 0;
        int max = strings.length - 1;
        return context.getString(strings[getRandomInt(min, max)]);
    }

    private int getRandomInt(int min, int max){
        return (rand.nextInt((max - min) + 1) + min);
    }

    private float getRandomFloat(int min, int max){
        int randomNum = getRandomInt(min, max);
        return (float)(randomNum / 2.0);
    }

    /**
     * =======================
     * 整理一下loadingData的行為，簡單來說可以分成下列幾個階段
     *  1: 取資料
     *      這階段又可以分成 (1) 新增 => 第一次開啟APP
     *                     (2) 更新 => 資料已過期，需要重新下載，但原本紀錄的資訊（我的最愛、收藏得留著）
     *                                目前暫時先把更新資料的時間定為一天
     *                     (3) 沒事
     *      不論資料來源為何，一律把這階段讀到的資料寫到RealmDB內
     *  2: 讀資料
     *      這階段很單純，反正就是把前一階段的資料從DB內取出來
     *  3: 計算距離
     *      由於每次開APP的時候位置都不一定一樣，所以就讓他開啟時再去計算距離
     *  4: 放到MyApp內
     *      這些資料整理好後，放到MyApp裡面，以供後面使操作
     *  =======================
     *
     * */
    @Override
    public void loadingData() {
        view.showProgressBar();
        RealmManager.getInstance(context).openRealm();
        DBViewInfo dbViewInfo = RealmManager.getInstance(context).getQuery(DBViewInfo.class).findFirst();
        if(dbViewInfo == null){
            // 資料庫還不存在，從網路讀資料，最後再把資料寫進DB
            Log.d("Test:::", "read data from network");
            loadDataFromNetwork(DB_STATUS.NEED_INSERT);
        }else{
            Log.d("Test:::", "read data from DB");
            // 同一天，就直接讀DB吧
            if(dbViewInfo.getDate().equals(MyApp.getInstance().getCurrentDate())) {
                loadDataFromDB();
            }else{ // 不同天，要更新DB
                Log.d("Test:::", "update data from network");
                loadDataFromNetwork(DB_STATUS.NEED_UPDATE);
            }
        }
    }

    /**
     * 第一階段中如果需要透過網路抓資料（新增/或更新)，都透過ApiManager去取資料，再用Rx來整理資料
     *
     * 1. 從網路上抓json資料
     * 2. 將json資料中的data一取
     * 3. 加上額外的資訊進去（不過不用寫距離
     * 4. 寫進DB
     * 5. 把list存到MyApp內
     * */
    private void loadDataFromNetwork(final DB_STATUS dbStatus){
        Subscription subscription = ApiManager.getInstance().getViews().getAllViews()
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<AllViews, Observable<ViewInfo>>() {
                    @Override
                    public Observable<ViewInfo> call(AllViews allViews) {
                        return Observable.from(allViews.getViews().getInfos());
                    }
                })
                .map(new Func1<ViewInfo, ViewInfo>() {
                    @Override
                    public ViewInfo call(ViewInfo viewInfo) {
                        return createCompleteViewInfo(viewInfo);

                    }
                })
                .subscribeOn(Schedulers.computation())
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ViewInfo>>() {
                    @Override
                    public void onCompleted() {
                        view.hideProgressBar();
                        Log.d("Test:::", "Loading data from network Success");
                        goToMainActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideProgressBar();
                        Log.d("Test:::", "Loading data from network Fail " + e.getMessage());
                        goToMainActivity();
                    }

                    @Override
                    public void onNext(List<ViewInfo> viewInfoList) {
                        // 把全部的list分段再寫入DB
                        List<List<ViewInfo>> parts = ListManager.getInstance().chopped(viewInfoList, ARRAY_SPLIT_COUNT);
                        if(dbStatus == DB_STATUS.NEED_INSERT){
                            for (List<ViewInfo> viewInfos : parts) {
                                RealmManager.getInstance(context).insertDataToDBViewInfo(viewInfos);
                            }
//                            RealmManager.getInstance(context).createNewData(viewInfoList);
                        }else if(dbStatus == DB_STATUS.NEED_UPDATE){
                            for (List<ViewInfo> viewInfos : parts) {
                                RealmManager.getInstance(context).updateDataToDBViewInfo(viewInfos);
                            }
                        }
                        // 再把DB的資料放到MyApp裡面使用
                        MyApp.getInstance().setViewInfos(getDataFromDB());
                    }
                });
        compositeSubscription.add(subscription);
    }

    /**
     * 特別要注意的是，因為距離每次都不一定（依照開啟時位置而定）
     * 所以沒有寫進DB，所以這邊需要計算距離以後放入view info
     * */
    private void loadDataFromDB(){
        // 再把DB的資料放到MyApp裡面使用
        MyApp.getInstance().setViewInfos(getDataFromDB());
        view.hideProgressBar();
        goToMainActivity();
    }

    /**
     * 把資料從DB讀出來
     * */
    private List<ViewInfo> getDataFromDB(){
        RealmQuery<DBViewInfo> query = RealmManager.getInstance(context).getQuery(DBViewInfo.class);
        RealmResults<DBViewInfo> realmResults = query.findAll();
        List<ViewInfo> viewInfos = new ArrayList<>();
        for(DBViewInfo dbViewInfo : realmResults){
            viewInfos.add(RealmManager.getInstance(context).getViewInfoFromDBViewInfo(dbViewInfo
                    , calculateDistance(dbViewInfo.getLongitude(), dbViewInfo.getLatitude(), dbViewInfo.getName())));
        }
        return viewInfos;
    }

    /**
     * @return 回傳兩個location之間的距離
     * */
    private int calculateDistance(String lng, String lat, String name){
        double distance ;
        // 建立新的location
        Location loc = new Location(name);
        loc.setLongitude(Double.parseDouble(lng));
        loc.setLatitude(Double.parseDouble(lat));
        if(currentLoc == null){
            distance = 0;
        }else {
            distance = currentLoc.distanceTo(loc);
        }

        return ((int)(distance));
    }

    private void goToMainActivity(){
        context.startActivity(new Intent(context, MainActivity.class));
        view.finishActivity();
    }

    @Override
    public void onStop(){
        if(compositeSubscription != null && !compositeSubscription.isUnsubscribed())
            compositeSubscription.unsubscribe();
        RealmManager.getInstance(context).closeRealm();
    }

    private enum DB_STATUS{
        NEED_UPDATE,
        NEED_INSERT
    }
}
