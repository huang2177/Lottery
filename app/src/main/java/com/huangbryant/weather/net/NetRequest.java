package com.huangbryant.weather.net;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;


import com.huangbryant.weather.utils.NetUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 编写： 黄双
 * 电话：15378412400
 * 邮箱：15378412400@163.com
 * 时间：2017/3/26 15:41
 */
public class NetRequest implements Callback {
    private ResultCallBack resultCallBack;
    private Context context;
    private Message message;
    private int type;
    private MyHandler myHandler;
    private OkHttpClient client;
    private String url;
    private Map<String, String> map;
    private Request.Builder builder;
    private FormBody.Builder requestBody;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public NetRequest(Context context, String url, Map<String, String> map, ResultCallBack resultCallBack, int type) {
        this.map = map;
        this.url = url;
        this.type = type;
        this.context = context;
        this.resultCallBack = resultCallBack;

        message = new Message();
        client = new OkHttpClient.Builder()
                .readTimeout(8000, TimeUnit.SECONDS)
                .connectTimeout(8000, TimeUnit.SECONDS)
                .build();
        builder = new Request.Builder();
        myHandler = new MyHandler(context);
        requestBody = new FormBody.Builder();
    }

    public void getAsyn() {
        if (!NetUtils.isNetworkStateed(context)) {
            return;
        }
        String mUrl = url + initParams(map);
        Request request = builder.url(mUrl).build();
        client.newCall(request).enqueue(this);
    }

    public void post() {
        if (!NetUtils.isNetworkStateed(context)) {
            return;
        }
        for (Map.Entry<String, String> ele : map.entrySet()) {
            requestBody.add(ele.getKey(), ele.getValue().toString());
        }
        Request request = new Request.Builder().url(url).post(requestBody.build()).build();
        //3，创建call对象并将请求对象添加到调度中
        client.newCall(request).enqueue(this);
    }

    public void postAsyn() {
        if (!NetUtils.isNetworkStateed(context)) {
            return;
        }
        Request.Builder builder = new Request.Builder().url(url);
        Request request = null;
        if (map == null) {
            request = builder.build();
        } else {
            for (Map.Entry<String, String> ele : map.entrySet()) {
                requestBody.add(ele.getKey(), ele.getValue());
            }
            request = builder.post(requestBody.build()).build();
        }
        client.newCall(request).enqueue(this);
    }

    private String initParams(Map<String, String> map) {
        StringBuffer params = new StringBuffer();
        params.append('?');
        for (String name : map.keySet()) {
            try {
                params.append(name).append('=').append(map.get(name)).append('&');
            } catch (Exception e) {
            }
        }
        return params.substring(0, params.length() - 1);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        try {
            message.what = 2;
            message.arg1 = type;
            message.obj = resultCallBack;
            myHandler.sendMessage(message);
        } catch (Exception e1) {
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            String result = UnicodeUtils.decode(response.body().string());
            if (response.isSuccessful() && result != null && !result.equals("")) {
                message.what = 1;
                message.obj = resultCallBack;
                message.arg1 = type;
                Bundle bundle = new Bundle();
                bundle.putString("result", result);
                message.setData(bundle);
                myHandler.sendMessage(message);
            } else {
                message.what = 2;
                message.arg1 = type;
                message.obj = resultCallBack;
                myHandler.sendMessage(message);
            }
        } catch (Exception e) {
        }
    }

}
