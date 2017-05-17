package com.shenke.digest.core;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.shenke.digest.entity.Cache;

/**
 * Created by Cloud on 2017/5/17.
 */

public class BaseActivity extends AppCompatActivity {

    public Cache aCache;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aCache = Cache.get(BaseActivity.this);

    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }


}

