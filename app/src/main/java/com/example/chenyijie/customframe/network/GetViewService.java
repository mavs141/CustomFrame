package com.example.chenyijie.customframe.network;

import com.example.chenyijie.customframe.bean.AllViews;

import retrofit2.http.GET;
import rx.Observable;

/**
 * 雖然可以直接放入ApiManager，但是為了管理方便以及方便觀看，我還是比較喜歡拉出來
 *
 * Created by chenyijie on 2017/5/18.
 */

public interface GetViewService {
    @GET("XMLReleaseALL_public/scenic_spot_C_f.json")
    Observable<AllViews> getAllViews();
}
