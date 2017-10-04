package com.huangbryant.weather;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.huangbryant.weather.utils.Logger;

import java.util.Calendar;

import cn.bmob.v3.Bmob;
import cn.jpush.android.api.JPushInterface;

/**
 * For developer startup JPush SDK
 * <p>
 * 一般建议在自定义 Application 类里初始化。也可以在主 Activity 里。
 */
public class MyApplication extends Application {
    private static Resources sRes;
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onCreate() {
        Logger.d(TAG, "[ExampleApplication] onCreate");
        super.onCreate();

        sRes = getResources();

        JPushInterface.init(this);            // 初始化 JPush
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        Bmob.initialize(this, AppContent.BMOB_KEY);
    }


    public static void updateNightMode(boolean on) {
        DisplayMetrics dm = sRes.getDisplayMetrics();
        Configuration config = sRes.getConfiguration();
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        sRes.updateConfiguration(config, dm);
    }
}
