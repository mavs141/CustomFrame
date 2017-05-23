package com.example.chenyijie.customframe.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.chenyijie.customframe.R;
import com.example.chenyijie.customframe.bean.ViewInfo;
import com.example.chenyijie.customframe.common.KeyConst;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenyijie on 2017/5/18.
 */

public class MainAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ViewInfo> viewInfoList;
    private AdapterClickListener viewClick;

    /**
     * 所有跟adapter點擊有關的動作都在這邊統一
     * 透過callback在activity管理
     * */
    public interface AdapterClickListener {
        void onItemClick(ViewInfo selectedView);
        void onFavoriteClick(ViewInfo selectedView);
        void onBookClick(ViewInfo selectedView);
    }

    public MainAdapter(Context context, List<ViewInfo> viewInfoList) {
        this.context = context;
        this.viewInfoList = viewInfoList;
    }

    public void addViews(List<ViewInfo> viewInfoList) {
        this.viewInfoList.clear();
        this.viewInfoList.addAll(viewInfoList);
        this.notifyDataSetChanged();
    }

    public void bindingClickListener(AdapterClickListener listener) {
        this.viewClick = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_item, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MainViewHolder mainViewHolder = (MainViewHolder) holder;
        ViewInfo selectedView = viewInfoList.get(position);

        mainViewHolder.viewImage.setImageDrawable(context.getResources().getDrawable(R.drawable.default_image));
        mainViewHolder.viewName.setText(selectedView.getName());
        mainViewHolder.viewAddress.setText(selectedView.getAdd());
        mainViewHolder.viewCategory.setText(selectedView.getCategory());
        mainViewHolder.viewCost.setText(context.getString(R.string.adapter_cost
                , selectedView.getCost()));
        mainViewHolder.viewComment.setText(context.getString(R.string.adapter_comment
                , selectedView.getComment()));
        mainViewHolder.viewDistance.setText(context.getString(R.string.adapter_distance
                , selectedView.getDistance()));
        mainViewHolder.viewRating.setStepSize(KeyConst.RATING_BAR_STEP);
        mainViewHolder.viewRating.setRating(selectedView.getRating());
        // 以下這些隨時需要依照位置改變的選項，不能直接用selectedView，因為selectedView直接指向最後一筆
        if(viewInfoList.get(position).getBooked()) {
            mainViewHolder.btnBook.setBackground(context.getResources().getDrawable(R.drawable.collection_pressed));
        }else{
            mainViewHolder.btnBook.setBackground(context.getResources().getDrawable(R.drawable.collection_normal));
        }
        if(viewInfoList.get(position).getFavorite()) {
            mainViewHolder.btnFavorite.setBackground(context.getResources().getDrawable(R.drawable.favorite_added));
        }else{
            mainViewHolder.btnFavorite.setBackground(context.getResources().getDrawable(R.drawable.favorite_notadded));
        }

        if(viewClick != null){
            mainViewHolder.viewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewClick.onItemClick(viewInfoList.get(position));
                }
            });
            mainViewHolder.btnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewClick.onBookClick(viewInfoList.get(position));
                }
            });
            mainViewHolder.btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewClick.onFavoriteClick(viewInfoList.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (viewInfoList == null) return 0;
        return viewInfoList.size();
    }

    class MainViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.view_image)
        ImageView viewImage;
        @BindView(R.id.view_name)
        TextView viewName;
        @BindView(R.id.btn_book)
        Button btnBook;
        @BindView(R.id.btn_favorite)
        Button btnFavorite;
        @BindView(R.id.view_rating)
        RatingBar viewRating;
        @BindView(R.id.view_comment)
        TextView viewComment;
        @BindView(R.id.view_cost)
        TextView viewCost;
        @BindView(R.id.view_address)
        TextView viewAddress;
        @BindView(R.id.view_distance)
        TextView viewDistance;
        @BindView(R.id.view_category)
        TextView viewCategory;
        @BindView(R.id.view_item)
        CardView viewItem;

        private MainViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
