package com.wyk.wechattomap.base;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * @author wyk
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
