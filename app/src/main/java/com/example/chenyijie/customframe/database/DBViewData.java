package com.example.chenyijie.customframe.database;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenyijie on 2017/5/23.
 */

public class DBViewData extends RealmObject {
    private String date;
    private List<DBViewInfo> dbViewInfos;

    public DBViewData(){}

    public DBViewData(String date, List<DBViewInfo> dbViewInfos){
        this.date = date;
        this.dbViewInfos = dbViewInfos;
    }

    public void setDate(String date){ this.date = date; }

    public String getDate(){ return this.date; }

    public void setDBViewInfos(List<DBViewInfo> dbViewInfos){ this.dbViewInfos = dbViewInfos; }

    public List<DBViewInfo> getDBViewInfos(){ return this.dbViewInfos; }

}
