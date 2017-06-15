package com.jiayang.jyelock.view;

import android.content.Intent;
import android.view.View;

import com.jiayang.jyelock.R;
import com.jiayang.jyelock.base.BaseActivity;

import butterknife.OnClick;

/**
 * Created by Administrator on 2017/6/14.
 */

public class WelcomeActivity extends BaseActivity {
    @Override
    protected int initLayout() {
        return R.layout.activity_welcome;
    }
    @OnClick({R.id.httpBtn, R.id.uploadImgBtn, R.id.uploadFileBtn, R.id.downloadBtn,R.id.downloadPointBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.httpBtn:
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                break;
//            case R.id.uploadImgBtn:
//                startActivity(new Intent(WelcomeActivity.this, UploadImageActivity.class));
//                break;
//            case R.id.uploadFileBtn:
//                startActivity(new Intent(WelcomeActivity.this, UploadFileActivity.class));
//                break;
//            case R.id.downloadBtn:
//                startActivity(new Intent(WelcomeActivity.this, DownloadActivity.class));
//                break;
//            case R.id.downloadPointBtn:
//                startActivity(new Intent(WelcomeActivity.this, DownloadBreakpointsActivity.class));
//                break;
        }
    }
}
