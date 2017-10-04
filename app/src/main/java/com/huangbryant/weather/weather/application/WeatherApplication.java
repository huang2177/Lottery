package com.huangbryant.weather.weather.application;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.huangbryant.weather.weather.utils.ScreenUtils;

import cn.bmob.v3.Bmob;

public class WeatherApplication extends Application {
    private static Resources sRes;

    @Override
    public void onCreate() {
        super.onCreate();

        sRes = getResources();
        ScreenUtils.init(this);

    }

    public static void updateNightMode(boolean on) {
        DisplayMetrics dm = sRes.getDisplayMetrics();
        Configuration config = sRes.getConfiguration();
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        sRes.updateConfiguration(config, dm);
    }
}
