package com.example.chenyijie.customframe.view.splash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.example.chenyijie.customframe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 負責撈出等等要用的資料，依據狀況不同會有新增、更新以及直接抓DB的值（自己延伸的）
 * 如果到時候發現不需要寫ＤＢ，而是每次都撈json的話，再去presenter修改
 *
 * Created by chenyijie on 2017/5/18.
 */

public class SplashActivity extends AppCompatActivity implements SplashContract.View {
    @BindView(R.id.progrss_bar)
    ProgressBar progrssbar;
    private SplashPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        // appcompatActivity不能用requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        presenter = new SplashPresenter(this, this);
        presenter.loadingData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onStop();
    }

    @Override
    public void showProgressBar() {
        progrssbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progrssbar.setVisibility(View.GONE);
    }

    @Override
    public void finishActivity() {
        SplashActivity.this.finish();
    }
}
