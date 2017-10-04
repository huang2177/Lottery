package com.huangbryant.weather.weather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huangbryant.weather.R;
import com.huangbryant.weather.weather.utils.binding.Bind;
import com.huangbryant.weather.weather.utils.binding.ViewBinder;


public class CityViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.view_holder_city)
    public LinearLayout item;
    @Bind(R.id.tv_city)
    public TextView tvCity;
    @Bind(R.id.iv_locate)
    public ImageView ivLocate;
    @Bind(R.id.tv_remark)
    public TextView tvRemark;

    public CityViewHolder(View itemView) {
        super(itemView);
        ViewBinder.bind(this, itemView);
    }
}
