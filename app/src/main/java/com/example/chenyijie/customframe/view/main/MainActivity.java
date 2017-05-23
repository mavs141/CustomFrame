package com.example.chenyijie.customframe.view.main;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.chenyijie.customframe.R;
import com.example.chenyijie.customframe.adapter.MainAdapter;
import com.example.chenyijie.customframe.bean.ViewInfo;
import com.example.chenyijie.customframe.common.MyApp;
import com.example.chenyijie.customframe.common.RealmManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 1. 周邊內容呈現（需要依照filter的不同，交給presenter的rx來搜尋出不同的資料)
 * 2. 我的最愛及收藏要可以和詳細內容連動
 * 3. toolbar上的按鈕點擊後要可以秀出周邊所有的點（這些點是依據第一點產生的，不是所有的資料都要show
 *
 * Created by chenyijie on 2017/5/18.
 */

public class MainActivity extends AppCompatActivity implements MainContract.View
        , MainAdapter.AdapterClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView title;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.selection_distance)
    Spinner selectionDistance;
    @BindView(R.id.selection_category)
    Spinner selectionCategory;
    @BindView(R.id.selection_order)
    Spinner selectionOrder;

    private MainContract.Presenter presenter;
    private MainAdapter adapter;
    private String distanceValue ;
    private String categoryValue ;
    private String orderValue ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        // appcompatActivity不能用requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
        getAllData();
    }

    private void init() {
        // toolbar
        title.setText(getString(R.string.main_toolbar_title));
        toolbar.setNavigationIcon(R.drawable.home_icon);
        setSupportActionBar(toolbar);

        // 篩選機制
        final String[] listDistance = {getString(R.string.selection_distance_three)
                , getString(R.string.selection_distance_five)
                , getString(R.string.selection_distance_ten)};
        final String[] listCategory = {getString(R.string.category_viewpoint)
                , getString(R.string.selection_category_amusement_park)
                , getString(R.string.selection_category_government)};
        final String[] listOrder = {getString(R.string.selection_order_distance)
                , getString(R.string.selection_order_rating)};

        distanceValue = listDistance[0];
        categoryValue = listCategory[0];
        orderValue = listOrder[0];

        generateSpinner(listDistance, selectionDistance);
        generateSpinner(listCategory, selectionCategory);
        generateSpinner(listOrder, selectionOrder);

        // recycler view的樣式
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        // 透過presenter載入資料
        presenter = new MainPresenter(this, this);
        getAllData();
    }

    /**
     * 顯示自訂的Actionbar items
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * toolbar上的item
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case android.R.id.home:
                Snackbar.make(mainContent, "點擊了home", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.main_menu_ignore:
                Snackbar.make(mainContent, "點擊了ignore", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.main_menu_navigation:
                if(MyApp.getInstance().getAllPeripheryViews() == null
                   || MyApp.getInstance().getAllPeripheryViews().size() == 0) {
                    Snackbar.make(mainContent, getString(R.string.no_periphery_views), Snackbar.LENGTH_SHORT).show();
                }else{
                    presenter.goToShowAllMapsActivity();
                }
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void generateSpinner(String[] strings, Spinner spinner){
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this
                , android.R.layout.simple_spinner_dropdown_item, strings);
        spinner.setAdapter(listAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()){
                    case R.id.selection_category:
                        categoryValue = listAdapter.getItem(position);
                        getAllData();
                        break;
                    case R.id.selection_order:
                        orderValue = listAdapter.getItem(position);
                        getAllData();
                        break;
                    case R.id.selection_distance:
                        distanceValue = listAdapter.getItem(position);
                        getAllData();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void getAllData() {
        presenter.getData(categoryValue, orderValue, distanceValue);
    }

    @Override
    public void getDataSuccess(List<ViewInfo> viewInfos) {
        Snackbar.make(mainContent, getString(R.string.loading_from_network_success)
                , Snackbar.LENGTH_SHORT).show();
        MyApp.getInstance().setAllPeripheryViews(viewInfos);

        if (adapter == null) {
            adapter = new MainAdapter(this, viewInfos);
            recyclerview.setAdapter(adapter);
            adapter.bindingClickListener(this);
        } else {
            adapter.addViews(viewInfos);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getDataFail(String errMsg) {
        Snackbar.make(mainContent, getString(R.string.loading_from_network_success)
                , Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(ViewInfo viewInfo) {
        // 將搜尋條件儲存起來，進入周邊時可以藉由這些條件進行搜尋
        presenter.goToDetailActivity(viewInfo, distanceValue, categoryValue, orderValue);
    }

    @Override
    public void onFavoriteClick(ViewInfo selectedView) {
        // 記得同步更新DB
        RealmManager.getInstance(this).updateFavorite(selectedView.getLongitude(),selectedView.getLatitude());
        selectedView.setFavorite(!selectedView.getFavorite());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBookClick(ViewInfo selectedView) {
        // 記得同步更新DB
        RealmManager.getInstance(this).updateBooked(selectedView.getLongitude(), selectedView.getLatitude());
        selectedView.setBooked(!selectedView.getBooked());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        presenter.onStop();
    }
}
