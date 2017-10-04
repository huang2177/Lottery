package com.huangbryant.weather.weather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


import com.huangbryant.weather.R;
import com.huangbryant.weather.weather.constants.Extras;
import com.huangbryant.weather.weather.constants.RequestCode;
import com.huangbryant.weather.weather.model.ImageWeather;
import com.huangbryant.weather.weather.model.Location;
import com.huangbryant.weather.weather.utils.KeyboardUtils;
import com.huangbryant.weather.weather.utils.ScreenUtils;
import com.huangbryant.weather.weather.utils.SnackbarUtils;
import com.huangbryant.weather.weather.utils.Utils;
import com.huangbryant.weather.weather.utils.binding.Bind;
import com.huangbryant.weather.weather.widget.TagLayout;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class UploadImageActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "UploadImageActivity";
    @Bind(R.id.iv_weather_image)
    private ImageView ivWeatherImage;
    @Bind(R.id.tv_location)
    private TextView tvLocation;
    @Bind(R.id.tag)
    private TagLayout tagLayout;
    @Bind(R.id.et_say)
    private EditText etSay;
    @Bind(R.id.btn_upload)
    private Button btnUpload;
    private ImageWeather imageWeather = new ImageWeather();
    private ProgressDialog mProgressDialog;
    private String path;

    public static void start(Activity activity, Location location, String path) {
        Intent intent = new Intent(activity, UploadImageActivity.class);
        intent.putExtra(Extras.IMAGE_PATH, path);
        intent.putExtra(Extras.LOCATION, location);
        activity.startActivityForResult(intent, RequestCode.REQUEST_UPLOAD);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        path = getIntent().getStringExtra(Extras.IMAGE_PATH);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int imageWidth = ScreenUtils.getScreenWidth() - ScreenUtils.dp2px(12) * 2;
        int imageHeight = (int) ((float) bitmap.getHeight() / bitmap.getWidth() * imageWidth);
        ivWeatherImage.setMinimumHeight(imageHeight);
        ivWeatherImage.setImageBitmap(bitmap);

        Location location = (Location) getIntent().getSerializableExtra(Extras.LOCATION);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        String userName;
        if (!TextUtils.isEmpty(deviceId) && deviceId.length() == 15) {
            userName = getString(R.string.user_name, deviceId.substring(7));
        } else {
            userName = "马儿";
        }
        imageWeather.setLocation(location);
        imageWeather.setCity(Utils.formatCity(location.getCity()));
        imageWeather.setUserName(userName);
        imageWeather.setPraise(0L);
        tvLocation.setText(location.getAddress());

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        KeyboardUtils.showKeyboard(etSay);
    }

    @Override
    protected void setListener() {
        btnUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                upload();
                break;
        }
    }

    private void upload() {
        mProgressDialog.setMessage(getString(R.string.uploading_image));
        mProgressDialog.show();
        final BmobFile file = new BmobFile(new File(path));
        file.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    mProgressDialog.setMessage(getString(R.string.publishing));
                    imageWeather.setImageUrl(file.getFileUrl());
                    imageWeather.setSay(etSay.getText().toString());
                    imageWeather.setTag(tagLayout.getTag());
                    imageWeather.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                mProgressDialog.cancel();
                                Toast.makeText(UploadImageActivity.this, getString(R.string.publish_success,
                                        imageWeather.getLocation().getCity()), Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Log.e(TAG, "upload object fail", e);
                                mProgressDialog.cancel();
                                SnackbarUtils.show(UploadImageActivity.this, getString(R.string.publish_fail, e.getMessage()));
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "upload image fail", e);
                    mProgressDialog.cancel();
                    SnackbarUtils.show(UploadImageActivity.this, getString(R.string.upload_image_fail, e.getMessage()));
                }
            }
        });
    }
}
