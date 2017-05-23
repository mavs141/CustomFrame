package com.example.chenyijie.customframe.view.main;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.chenyijie.customframe.R;
import com.example.chenyijie.customframe.bean.ViewInfo;
import com.example.chenyijie.customframe.common.MyApp;
import com.example.chenyijie.customframe.view.detail.DetailActivity;
import com.example.chenyijie.customframe.view.map.MapActivity;

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
 * 這個presenter的功能很簡單，就是依據filter篩選出所有符合條件的點
 *
 * Created by chenyijie on 2017/5/18.
 */

public class MainPresenter implements MainContract.Presenter {
    private Context context;
    private MainContract.View view;
    private CompositeSubscription compositeSubscriptio;

    public MainPresenter(Context context, MainContract.View view){
        this.context = context;
        this.view = view;

        compositeSubscriptio = new CompositeSubscription();
    }

    // rating要由大到小比較合理，但原生的compare只有小到大，所以只好自己做比較
    private Integer compareRating(float num1, float num2){
        if(num1 > num2) return -1;
        else if(num1 < num2) return 1;
        else    return 0;
    }

    @Override
    public void getData(final String conditionCategory, final String conditionOrder, final String conditionDistance) {
        if(MyApp.getInstance().getViewInfos() != null){
            // 透過條件去篩選
            Subscription subscription = Observable.from(MyApp.getInstance().getViewInfos())
                    .filter(new Func1<ViewInfo, Boolean>() {
                        @Override
                        public Boolean call(ViewInfo viewInfo) {
                            return ( viewInfo.getDistance() < MyApp.getInstance().getDistanceCondition(conditionDistance)
                                    && viewInfo.getCategory().equals(conditionCategory));
                        }
                    })
                    .toSortedList(new Func2<ViewInfo, ViewInfo, Integer>() {
                        @Override
                        public Integer call(ViewInfo viewInfo, ViewInfo viewInfo2) {
                            // api level < 19
                            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                if (conditionOrder.equals(context.getString(R.string.selection_order_distance))) {
                                    return ((Integer) viewInfo.getDistance()).compareTo(viewInfo2.getDistance());
                                } else {
                                    return compareRating(viewInfo.getRating(), viewInfo2.getRating());
                                }
                            }else{
                                if(conditionOrder.equals(context.getString(R.string.selection_order_distance))){
                                    return Integer.compare(viewInfo.getDistance(), viewInfo2.getDistance());
                                }else{
                                    return compareRating(viewInfo.getRating(), viewInfo2.getRating());
                                }
                            }
                        }
                    })
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<ViewInfo>>() {

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(List<ViewInfo> viewInfos) {
                            view.getDataSuccess(viewInfos);
                        }
                    });
            compositeSubscriptio.add(subscription);
        }else{
            view.getDataFail("");
        }
    }

    @Override
    public void goToDetailActivity(ViewInfo selectedView, String distanceFilter, String categoryFilter, String orderFilter) {
        MyApp.getInstance().setSelectedView(selectedView);
        /*
            // 把搜尋條件放到bundle一起傳給detail
            Intent intent = new Intent(context, DetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(KeyConst.SELECT_DISTANCE_FILTER, distanceFilter);
            bundle.putString(KeyConst.SELECT_CATEGORY_FILTER, categoryFilter);
            bundle.putString(KeyConst.SELECT_ORDER_FILTER, orderFilter);
            intent.putExtras(bundle);
        */
        // 把filter條件放到MyApp裡面，因為在detail裡面無法改變filter
        MyApp.getInstance().setDistanceFilter(distanceFilter);
        MyApp.getInstance().setCategoryFilter(categoryFilter);
        MyApp.getInstance().setOrderFilter(orderFilter);

        context.startActivity(new Intent(context, DetailActivity.class));
    }

    @Override
    public void goToShowAllMapsActivity() {
        context.startActivity(new Intent(context, MapActivity.class));
    }

    @Override
    public void onStop(){
        if(compositeSubscriptio != null && !compositeSubscriptio.isUnsubscribed())
            compositeSubscriptio.unsubscribe();
    }
}
