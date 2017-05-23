package com.example.chenyijie.customframe.view.splash;

/**
 * Created by chenyijie on 2017/5/18.
 */

public interface SplashContract {
    interface View{
        void showProgressBar();
        void hideProgressBar();
        void finishActivity();
    }

    interface Presenter{
        void loadingData();
        void onStop();
    }
}
