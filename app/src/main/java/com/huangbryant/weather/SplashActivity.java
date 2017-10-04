package com.huangbryant.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.huangbryant.weather.net.NetRequest;
import com.huangbryant.weather.net.ResultCallBack;
import com.huangbryant.weather.utils.StatusUtils;
import com.huangbryant.weather.weather.activity.WeatherActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kobe on 2017/10/3.
 */

public class SplashActivity extends Activity implements ResultCallBack {
    private MyThread mMyThread = new MyThread();
    private final String URL = "http://app.412988.com/Lottery_server/check_and_get_url.php";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusUtils.setStatusBar(this, false, false);
        StatusUtils.setStatusTextColor(false, this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.splash_layout);
        //getNetData();
        initNightMode();
        mMyThread.start();
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(2000);
                goMain();
            } catch (Exception e) {
            }
        }
    }
    private void initNightMode() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        MyApplication.updateNightMode(hour >= 19 || hour < 7);
    }
    public void goMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void startWeather() {
        Intent intent = new Intent(this, WeatherActivity.class);
        startActivity(intent);
        finish();
    }

    public void getNetData() {
        Map<String, String> map = new HashMap();
        map.put("type", "android");
        map.put("appid", "test");
        NetRequest netRequest = new NetRequest(this, URL, map, this, 0);
        netRequest.getAsyn();
    }

    @Override
    public void callBack(String data, int type) {
        Gson gson = new Gson();
        Log.e("------",data+"----");
        NetDataBean dataBean = gson.fromJson(data, NetDataBean.class);
        if (dataBean != null && dataBean.getRt_code().equals("200")) {
            startWeather();
//            if (dataBean.getData().getShow_url().equals("1")) {
//            } else {
//                startWeather();
//            }
        }
    }

    @Override
    public void failCallBack(int type) {

    }
}
