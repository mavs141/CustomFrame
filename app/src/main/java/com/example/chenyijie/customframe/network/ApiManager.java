package com.example.chenyijie.customframe.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    private static final String URL = "http://gis.taiwan.net.tw/";
    private GetViewService service;
    private static ApiManager instance;

    public static ApiManager getInstance(){
        if(instance == null) instance = new ApiManager();
        return instance;
    }

    public GetViewService getViews(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(GetViewService.class);
        return service;
    }
}
