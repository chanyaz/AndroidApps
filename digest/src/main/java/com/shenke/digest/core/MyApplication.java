package com.shenke.digest.core;


import android.app.Application;
import android.content.Context;
import android.os.RemoteException;

import com.amap.api.maps2d.MapsInitializer;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {
    public static MyApplication INSTANCE;
    public static String cacheDir = "";
    public static Context mAppContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("digest.realm").build();//自定义realm配置
        Realm.setDefaultConfiguration(config);
        INSTANCE = this;
        try {
            MapsInitializer.initialize(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        /**
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */

        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            cacheDir = getApplicationContext().getExternalCacheDir().toString();

        } else {
            cacheDir = getApplicationContext().getCacheDir().toString();
        }

    }

    private boolean ExistSDCard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static MyApplication getInstance() {
        return INSTANCE;
    }
}
