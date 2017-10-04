package com.huangbryant.weather.weather.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.huangbryant.weather.R;
import com.huangbryant.weather.weather.model.ImageWeather;
import com.huangbryant.weather.weather.utils.ScreenUtils;
import com.huangbryant.weather.weather.utils.binding.Bind;
import com.huangbryant.weather.weather.utils.binding.ViewBinder;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class ImageWeatherAdapter extends RecyclerView.Adapter<ImageWeatherAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "ImageWeatherAdapter";
    private static Context mContext;
    private List<ImageWeather> mImageList;
    private OnItemClickListener mClickListener;
    List<Integer>list;

    public ImageWeatherAdapter(List<ImageWeather> imageList) {
        mImageList = imageList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        list= Arrays.asList(R.drawable.header_image_weather,
                R.drawable.header_manage_city,
                R.drawable.header_navigation,
                R.drawable.header_sunrise,
                R.drawable.header_sunset,
                R.drawable.header_weather_day_cloudy,
                R.drawable.header_weather_day_fog,
                R.drawable.header_weather_day_rain,
                R.drawable.header_weather_day_snow,
                R.drawable.header_weather_day_sunny,
                R.drawable.header_weather_night_cloudy,
                R.drawable.header_weather_night_rain,
                R.drawable.header_weather_night_snow,
                R.drawable.header_weather_night_sunny,
                R.drawable.header_weather_night_sunny
                );
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_holder_image_weather, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        holder.llPraiseContainer.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            holder.item.setTag(mImageList.get(position));
            holder.llPraiseContainer.setTag(mImageList.get(position));
            holder.tvLocation.setText(mImageList.get(position).getLocation().getDistrict() + mImageList.get(position).getLocation().getStreet());
            holder.tvPraiseNum.setText(mImageList.get(position).getPraise() == 0L ? "" : String.valueOf(mImageList.get(position).getPraise()));
            Random random=new Random();
            holder.ivImage.setImageResource(list.get(random.nextInt(14)));
            final String url = mImageList.get(position).getImageUrl();
            holder.tvLocation.setTag(url);
            Glide.with(mContext)
                    .load(url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            if (!TextUtils.isEmpty(url) && url.equals(holder.tvLocation.getTag())) {
                                holder.ivImage.setImageBitmap(resource);
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item:
                mClickListener.onItemClick(v, v.getTag());
                break;
            case R.id.ll_praise_container:
                ImageWeather imageWeather = (ImageWeather) v.getTag();
                praise(v, imageWeather);
                break;
        }
    }

    private void praise(final View v, final ImageWeather imageWeather) {
        imageWeather.increment("praise");
        imageWeather.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    imageWeather.setPraise(imageWeather.getPraise() + 1);
                    TextView tvPraiseNum = (TextView) v.findViewById(R.id.tv_praise_num);
                    tvPraiseNum.setText(String.valueOf(imageWeather.getPraise()));
                } else {
                    Log.e(TAG, "praise fail", e);
                }
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item)
        public CardView item;
        @Bind(R.id.iv_image)
        public ImageView ivImage;
        @Bind(R.id.tv_location)
        public TextView tvLocation;
        @Bind(R.id.ll_praise_container)
        public LinearLayout llPraiseContainer;
        @Bind(R.id.tv_praise_num)
        public TextView tvPraiseNum;

        public ViewHolder(View itemView) {
            super(itemView);
            ViewBinder.bind(this, itemView);
            ScreenUtils.init(mContext);
            int minHeight = ScreenUtils.getScreenWidth() / 2 - ScreenUtils.dp2px(4) * 2;
            ivImage.setMinimumHeight(minHeight);
        }
    }
}
