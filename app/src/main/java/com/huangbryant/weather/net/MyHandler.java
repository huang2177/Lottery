package com.huangbryant.weather.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyHandler extends Handler {
    private Context context;

    public MyHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        try {
            switch (msg.what) {
                case 1:
                    ResultCallBack resultCallBack = (ResultCallBack) msg.obj;
                    resultCallBack.callBack(msg.getData().get("result").toString(), msg.arg1);
                    break;
                case 2:
                    ResultCallBack resultCallBack1 = (ResultCallBack) msg.obj;
                    resultCallBack1.failCallBack(msg.arg1);
                    break;
            }
        } catch (Exception e) {
        }
    }
}
