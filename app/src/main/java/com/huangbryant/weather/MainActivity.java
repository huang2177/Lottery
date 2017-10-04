package com.huangbryant.weather;


import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.google.gson.Gson;
import com.huangbryant.weather.net.NetRequest;
import com.huangbryant.weather.net.ResultCallBack;
import com.huangbryant.weather.utils.ActivityManager;
import com.huangbryant.weather.utils.Logger;
import com.huangbryant.weather.utils.ToastUtil;
import com.huangbryant.weather.weather.activity.AboutActivity;
import com.huangbryant.weather.weather.activity.BaseActivity;
import com.huangbryant.weather.weather.activity.ImageWeatherActivity;
import com.huangbryant.weather.weather.activity.ManageCityActivity;
import com.huangbryant.weather.weather.activity.SettingActivity;
import com.huangbryant.weather.weather.adapter.DailyForecastAdapter;
import com.huangbryant.weather.weather.adapter.HourlyForecastAdapter;
import com.huangbryant.weather.weather.adapter.SuggestionAdapter;
import com.huangbryant.weather.weather.api.Api;
import com.huangbryant.weather.weather.application.SpeechListener;
import com.huangbryant.weather.weather.constants.Extras;
import com.huangbryant.weather.weather.constants.RequestCode;
import com.huangbryant.weather.weather.model.CityInfo;
import com.huangbryant.weather.weather.model.Weather;
import com.huangbryant.weather.weather.model.WeatherData;
import com.huangbryant.weather.weather.utils.ACache;
import com.huangbryant.weather.weather.utils.ImageUtils;
import com.huangbryant.weather.weather.utils.NetworkUtils;
import com.huangbryant.weather.weather.utils.PermissionReq;
import com.huangbryant.weather.weather.utils.SnackbarUtils;
import com.huangbryant.weather.weather.utils.Utils;
import com.huangbryant.weather.weather.widget.ScrollListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends BaseActivity implements ResultCallBack,
        AMapLocationListener
        , NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener
        , View.OnClickListener {
    private WebView webView;
    private WebSettings webSettings;
    public static boolean isForeground = false;
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    private LinearLayout layout;
    private static final String TAG = "WeatherActivity";

    private ACache mACache;
    private AMapLocationClient mLocationClient;
    private SpeechSynthesizer mSpeechSynthesizer;
    private SpeechListener mSpeechListener;
    private CityInfo mCity;
    private DrawerLayout mDrawerLayout;
    private AppBarLayout mAppBar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView ivWeatherImage;
    private Toolbar toolbar;
    private SwipeRefreshLayout mRefreshLayout;
    private NestedScrollView mScrollView;
    private LinearLayout llWeatherContainer;
    private ImageView ivWeatherIcon;
    private TextView tvTemp;
    private TextView tvMaxTemp;
    private TextView tvMinTemp;
    private TextView tvMoreInfo;
    private ScrollListView lvHourlyForecast;
    private ScrollListView lvDailyForecast;
    private ScrollListView lvSuggestion;
    private FloatingActionButton fabSpeech;
    private NavigationView mNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityManager.addActivity(this);
        ActivityManager.getInstance().setCurrentActivity(this);
        getNetData();
    }

    private void initWheatherView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setVisibility(View.VISIBLE);
        mAppBar = (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ivWeatherImage = (ImageView) findViewById(R.id.iv_weather_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        llWeatherContainer = (LinearLayout) findViewById(R.id.ll_weather_container);
        ivWeatherIcon = (ImageView) findViewById(R.id.iv_icon);
        tvTemp = (TextView) findViewById(R.id.tv_temp);
        tvMaxTemp = (TextView) findViewById(R.id.tv_max_temp);
        tvMinTemp = (TextView) findViewById(R.id.tv_min_temp);
        tvMoreInfo = (TextView) findViewById(R.id.tv_more_info);
        lvHourlyForecast = (ScrollListView) findViewById(R.id.lv_hourly_forecast);
        lvDailyForecast = (ScrollListView) findViewById(R.id.lv_daily_forecast);
        lvSuggestion = (ScrollListView) findViewById(R.id.lv_suggestion);
        fabSpeech = (FloatingActionButton) findViewById(R.id.fab_speech);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        mACache = ACache.get(getApplicationContext());
        mCity = (CityInfo) mACache.getAsObject(Extras.CITY);
        if (mCity == null) {
            CityInfo cityInfo = new CityInfo("正在定位", true);
            cache(cityInfo);
            mCity = (CityInfo) mACache.getAsObject(Extras.CITY);
        }
        collapsingToolbar.setTitle(mCity.name);
        setListene();
        checkIfRefresh(mCity);

        Utils.voiceAnimation(fabSpeech, false);
    }

    private void initNomalView(String url) {
        Logger.LOG_ENABLE = false;
        layout = (LinearLayout) findViewById(R.id.webview_layout);
        layout.setVisibility(View.VISIBLE);

        webView = new WebView(this);
        layout.addView(webView);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDefaultTextEncodingName("UTF-8");

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    protected void setListene() {
        mNavigationView.setNavigationItemSelectedListener(this);
        fabSpeech.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }

    private void checkIfRefresh(CityInfo city) {
        Weather weather = (Weather) mACache.getAsObject(city.name);
        if (weather != null) {
            updateView(weather);
        } else {
            llWeatherContainer.setVisibility(View.GONE);
        }
        if (weather == null || Utils.shouldRefresh(this)) {
            Utils.setRefreshingOnCreate(mRefreshLayout);
            onRefresh();
        }
    }

    private void updateView(Weather weather) {
        ivWeatherImage.setImageResource(ImageUtils.getWeatherImage(weather.now.cond.txt));
        ivWeatherIcon.setImageResource(ImageUtils.getIconByCode(this, weather.now.cond.code));
        tvTemp.setText(getString(R.string.tempC, weather.now.tmp));
        tvMaxTemp.setText(getString(R.string.now_max_temp, weather.daily_forecast.get(0).tmp.max));
        tvMinTemp.setText(getString(R.string.now_min_temp, weather.daily_forecast.get(0).tmp.min));
        StringBuilder sb = new StringBuilder();
        sb.append("体感")
                .append(weather.now.fl)
                .append("°");
        if (weather.aqi != null && !TextUtils.isEmpty(weather.aqi.city.qlty)) {
            sb.append("  ")
                    .append(weather.aqi.city.qlty.contains("污染") ? "" : "空气")
                    .append(weather.aqi.city.qlty);
        }
        sb.append("  ")
                .append(weather.now.wind.dir)
                .append(weather.now.wind.sc)
                .append(weather.now.wind.sc.endsWith("风") ? "" : "级");
        tvMoreInfo.setText(sb.toString());
        lvHourlyForecast.setAdapter(new HourlyForecastAdapter(weather.hourly_forecast));
        lvDailyForecast.setAdapter(new DailyForecastAdapter(weather.daily_forecast));
        lvSuggestion.setAdapter(new SuggestionAdapter(weather.suggestion));
    }

    public void getNetData() {
        Map<String, String> map = new HashMap();
        map.put("type", "android");
        map.put("appid", "test");
        NetRequest netRequest = new NetRequest(this, AppContent.URL, map, this, 0);
        netRequest.getAsyn();
    }

    @Override
    protected void onResume() {
        isForeground = true;
        JPushInterface.onResume(this);
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    public void callBack(String data, int type) {
        Gson gson = new Gson();
        NetDataBean dataBean = gson.fromJson(data, NetDataBean.class);
        if (dataBean != null && dataBean.getRt_code().equals("200")) {
            if (dataBean.getData().getShow_url().equals("1")) {
                initNomalView(dataBean.getData().getUrl());
            } else {
                initWheatherView();
            }
        }
    }

    @Override
    public void failCallBack(int type) {
        ToastUtil.topTSnackbar("服务器出错了，请稍后重试！");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRefresh() {
        if (mCity.isAutoLocate) {
            locate();
        } else {
            fetchDataFromNetWork(mCity);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_speech:
                speech();
                break;
        }
    }

    private void locate() {
        PermissionReq.with(this)
                .permissions(android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        if (mLocationClient == null) {
                            mLocationClient = Utils.initAMapLocation(MainActivity.this, MainActivity.this);
                        }
                        mLocationClient.startLocation();
                    }

                    @Override
                    public void onDenied() {
                        onLocated(null);
                        SnackbarUtils.show(MainActivity.this, getString(R.string.no_permission, "位置信息", "获取当前位置"));
                    }
                })
                .request();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mLocationClient.stopLocation();
            if (aMapLocation.getErrorCode() == 0 && !TextUtils.isEmpty(aMapLocation.getCity())) {
                // 定位成功回调信息，设置相关消息
                onLocated(Utils.formatCity(aMapLocation.getCity(), aMapLocation.getDistrict()));
            } else {
                // 定位失败
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                onLocated(null);
                SnackbarUtils.show(fabSpeech, R.string.locate_fail);
            }
        }
    }

    private void onLocated(String city) {
        mCity.name = TextUtils.isEmpty(city) ? (TextUtils.equals(mCity.name, "正在定位") ? "上海" : mCity.name) : city;
        cache(mCity);

        collapsingToolbar.setTitle(mCity.name);
        fetchDataFromNetWork(mCity);
    }

    private void fetchDataFromNetWork(final CityInfo city) {
        // HE_KEY是更新天气需要的key，需要从和风天气官网申请后方能更新天气
        Api.getIApi().getWeather(city.name, AppContent.HE_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<WeatherData>() {
                    @Override
                    public void accept(WeatherData weatherData) throws Exception {
                        boolean success = weatherData.weathers.get(0).status.equals("ok");
                        if (!success) {
                            throw Exceptions.propagate(new Throwable(weatherData.weathers.get(0).status));
                        }
                    }
                })
                .map(new Function<WeatherData, Weather>() {
                    @Override
                    public Weather apply(@NonNull WeatherData weatherData) throws Exception {
                        return weatherData.weathers.get(0);
                    }
                })
                .doOnNext(new Consumer<Weather>() {
                    @Override
                    public void accept(Weather weather) throws Exception {
                        mACache.put(city.name, weather);
                        Utils.saveRefreshTime(MainActivity.this);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Weather weather) {
                        updateView(weather);
                        llWeatherContainer.setVisibility(View.VISIBLE);
                        SnackbarUtils.show(fabSpeech, R.string.update_tips);
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "update weather fail", e);
                        if (NetworkUtils.errorByNetwork(e)) {
                            SnackbarUtils.show(fabSpeech, R.string.network_error);
                        } else {
                            SnackbarUtils.show(fabSpeech, "update weather error: " + e.getMessage());
                        }
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void cache(CityInfo city) {
        ArrayList<CityInfo> cityList = (ArrayList<CityInfo>) mACache.getAsObject(Extras.CITY_LIST);
        if (cityList == null) {
            cityList = new ArrayList<>();
        }
        CityInfo oldAutoLocate = null;
        for (CityInfo cityInfo : cityList) {
            if (cityInfo.isAutoLocate) {
                oldAutoLocate = cityInfo;
                break;
            }
        }
        if (oldAutoLocate != null) {
            oldAutoLocate.name = city.name;
        } else {
            cityList.add(city);
        }
        mACache.put(Extras.CITY, city);
        mACache.put(Extras.CITY_LIST, cityList);
    }

    private void speech() {
        PermissionReq.with(this)
                .permissions(android.Manifest.permission.READ_PHONE_STATE)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        Weather weather = (Weather) mACache.getAsObject(mCity.name);
                        if (weather == null) {
                            return;
                        }
                        if (mSpeechSynthesizer == null) {
                            mSpeechListener = new SpeechListener(MainActivity.this);
                            mSpeechSynthesizer = new SpeechSynthesizer(MainActivity.this, "holder", mSpeechListener);
                            mSpeechSynthesizer.setApiKey(AppContent.BD_TTS_API_KEY, AppContent.BD_TTS_SECRET_KEY);
                            mSpeechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        }
                        String text = Utils.voiceText(MainActivity.this, weather.daily_forecast.get(0));
                        mSpeechSynthesizer.speak(text);
                    }

                    @Override
                    public void onDenied() {
                        SnackbarUtils.show(MainActivity.this, getString(R.string.no_permission, "手机状态", "进行语音播报"));
                    }
                })
                .request();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        mDrawerLayout.closeDrawers();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                item.setChecked(false);
            }
        }, 500);
        switch (item.getItemId()) {
            case R.id.action_image_weather:
                startImageWeather();
                return true;
            case R.id.action_location:
                startActivityForResult(new Intent(this, ManageCityActivity.class), RequestCode.REQUEST_CODE);
                return true;
            case R.id.action_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.action_share:
                share();
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return false;
    }

    private void startImageWeather() {
        PermissionReq.with(this)
                .permissions(android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        startActivity(new Intent(MainActivity.this, ImageWeatherActivity.class));
                    }

                    @Override
                    public void onDenied() {
                        SnackbarUtils.show(MainActivity.this, getString(R.string.no_permission, "位置信息", "打开实景天气"));
                    }
                })
                .request();
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        CityInfo city = (CityInfo) data.getSerializableExtra(Extras.CITY);
        if (mCity.equals(city)) {
            return;
        }

        mCity = city;
        collapsingToolbar.setTitle(mCity.name);
        mScrollView.scrollTo(0, 0);
        mAppBar.setExpanded(true, false);
        llWeatherContainer.setVisibility(View.GONE);
        mRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.cancel();
            mSpeechListener.release();
        }
        super.onDestroy();
    }
}