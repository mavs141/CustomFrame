package com.example.chenyijie.customframe.common;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.chenyijie.customframe.bean.ViewInfo;
import com.example.chenyijie.customframe.database.DBViewData;
import com.example.chenyijie.customframe.database.DBViewInfo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmQuery;

/**
 * 所有DB的操縱都放在這邊，方便管理
 *
 * Created by chenyijie on 2017/5/21.
 */

public class RealmManager {
    private static RealmManager instance;
    private Realm realm ;

    private RealmManager(Context context){
        Realm.init(context);
    }

    public void openRealm(){
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(KeyConst.REALM_DB_NAME)
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);
    }

    public static RealmManager getInstance(Context context){
        if(instance == null) instance = new RealmManager(context);
        return instance;
    }

    public <E extends RealmModel>RealmQuery<E> getQuery(Class<E> clazz){
        return realm.where(clazz);
    }

    /**
     * 理論上primary key不應該這樣建立，但是目前先這樣就好，視情況擴充
     * */
    private int autoGeneratePrimaryKey(){
        Number currentIdNum = realm.where(DBViewInfo.class).max(KeyConst.REALM_DB_TABLE_ID);
        return (currentIdNum == null)? 0 : currentIdNum.intValue() + 1;
    }


    public void createNewData(final List<ViewInfo> viewInfos){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DBViewData dbViewData = realm.createObject(DBViewData.class, MyApp.getInstance().getCurrentDate());
                List<DBViewInfo> dbViewInfos = new ArrayList<DBViewInfo>();
                for(ViewInfo viewInfo : viewInfos){
                    DBViewInfo dbViewInfo = getDBViewInfoFromViewInfo(viewInfo);
                    dbViewInfos.add(dbViewInfo);
                }

                dbViewData.setDBViewInfos(dbViewInfos);

                realm.copyToRealmOrUpdate(dbViewData);
            }
        });
    }

    /**
     * 新增
     * */
    public void insertDataToDBViewInfo(final List<ViewInfo> viewInfos){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for(int i = 0; i < viewInfos.size(); i++){
//                    DBViewInfo dbViewInfo = realm.createObject(DBViewInfo.class);
                    DBViewInfo dbViewInfo = realm.createObject(DBViewInfo.class
                            , autoGeneratePrimaryKey() + i);
                    dbViewInfo.setDate(MyApp.getInstance().getCurrentDate());
                    dbViewInfo.setName(viewInfos.get(i).getName());
                    dbViewInfo.setAddress(viewInfos.get(i).getAdd());
                    dbViewInfo.setLongitude(viewInfos.get(i).getLongitude());
                    dbViewInfo.setLatitude(viewInfos.get(i).getLatitude());
                    dbViewInfo.setCategory(viewInfos.get(i).getCategory());
                    dbViewInfo.setRating(viewInfos.get(i).getRating());
                    dbViewInfo.setCost(viewInfos.get(i).getCost());
                    dbViewInfo.setComment(viewInfos.get(i).getComment());
                    dbViewInfo.setBooked(false);
                    dbViewInfo.setFavorited(false);

                    realm.copyToRealmOrUpdate(dbViewInfo);
                }
            }
        });
    }

    /**
     * 更新
     * 只會更新：
     *         1. 時間
     *         2. 名字
     *         3. 類別
     *         4. 評價
     *         5. 消費
     *         6. 評論
     * */
    public void updateDataToDBViewInfo(final List<ViewInfo> viewInfos){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for(int i = 0; i < viewInfos.size(); i++){
                    // find the corresponding object in db
                    DBViewInfo dbViewInfo = realm.where(DBViewInfo.class)
                            .equalTo(KeyConst.REALM_DB_TABLE_LATITUDE, viewInfos.get(i).getLatitude())
                            .equalTo(KeyConst.REALM_DB_TABLE_LONGITUDE,viewInfos.get(i).getLongitude())
                            .findFirst();

                    if(dbViewInfo == null){
                        DBViewInfo newDBViewInfo = realm.createObject(DBViewInfo.class
                                , autoGeneratePrimaryKey() + i);
//                        DBViewInfo newDBViewInfo = realm.createObject(DBViewInfo.class);
                        newDBViewInfo.setDate(MyApp.getInstance().getCurrentDate());
                        newDBViewInfo.setName(viewInfos.get(i).getName());
                        newDBViewInfo.setAddress(viewInfos.get(i).getAdd());
                        newDBViewInfo.setLongitude(viewInfos.get(i).getLongitude());
                        newDBViewInfo.setLatitude(viewInfos.get(i).getLatitude());
                        newDBViewInfo.setCategory(viewInfos.get(i).getCategory());
                        newDBViewInfo.setRating(viewInfos.get(i).getRating());
                        newDBViewInfo.setCost(viewInfos.get(i).getCost());
                        newDBViewInfo.setComment(viewInfos.get(i).getComment());
                        newDBViewInfo.setBooked(false);
                        newDBViewInfo.setFavorited(false);

                        realm.copyToRealm(newDBViewInfo);
                    }else {
                        dbViewInfo.setDate(MyApp.getInstance().getCurrentDate());
                        dbViewInfo.setName(viewInfos.get(i).getName());
                        dbViewInfo.setAddress(viewInfos.get(i).getAdd());
                        dbViewInfo.setLongitude(dbViewInfo.getLongitude());
                        dbViewInfo.setLatitude(dbViewInfo.getLatitude());
                        dbViewInfo.setCategory(viewInfos.get(i).getCategory());
                        dbViewInfo.setRating(viewInfos.get(i).getRating());
                        dbViewInfo.setCost(viewInfos.get(i).getCost());
                        dbViewInfo.setComment(viewInfos.get(i).getComment());
                        dbViewInfo.setBooked(dbViewInfo.getBooked());
                        dbViewInfo.setFavorited(dbViewInfo.getFavorited());

                        realm.copyToRealmOrUpdate(dbViewInfo);
                    }

                }
            }
        });
    }

    /**
     * 更新我的最愛的欄位
     * */
    public void updateFavorite(@NonNull String longitude,@NonNull String latitude){
        openRealm();
        DBViewInfo dbViewInfo = realm.where(DBViewInfo.class)
                .equalTo(KeyConst.REALM_DB_TABLE_LONGITUDE,longitude)
                .equalTo(KeyConst.REALM_DB_TABLE_LATITUDE,latitude)
                .findFirst();
        realm.beginTransaction();
        dbViewInfo.setFavorited(!dbViewInfo.getFavorited());
        realm.commitTransaction();
        closeRealm();
    }

    /**
     * 更新我的收藏的欄位
     * */
    public void updateBooked(@NonNull String longitude,@NonNull String latitude){
        openRealm();
        DBViewInfo dbViewInfo = realm.where(DBViewInfo.class)
                .equalTo(KeyConst.REALM_DB_TABLE_LONGITUDE,longitude)
                .equalTo(KeyConst.REALM_DB_TABLE_LATITUDE,latitude)
                .findFirst();
        realm.beginTransaction();
        dbViewInfo.setBooked(!dbViewInfo.getBooked());
        realm.commitTransaction();
        closeRealm();
    }

    public ViewInfo getViewInfoFromDBViewInfo(DBViewInfo dbViewInfo, int distance){
        return new ViewInfo(dbViewInfo.getLatitude(), dbViewInfo.getLongitude(), dbViewInfo.getAddress()
                , dbViewInfo.getName(), dbViewInfo.getRating(), dbViewInfo.getCost(), dbViewInfo.getComment()
                , dbViewInfo.getCategory(), distance, dbViewInfo.getBooked(), dbViewInfo.getFavorited());
    }

    public DBViewInfo getDBViewInfoFromViewInfo(ViewInfo viewInfo){
        return new DBViewInfo(viewInfo.getLatitude(), viewInfo.getLongitude()
                , viewInfo.getAdd(), viewInfo.getName(), viewInfo.getRating(), viewInfo.getCost()
                , viewInfo.getComment(), viewInfo.getCategory(), viewInfo.getBooked(), viewInfo.getFavorite());
    }

    public void closeRealm(){
        if(realm != null) realm.close();
    }
}
