package com.lya.plugindemo;

import android.app.Application;
import android.content.Context;

import com.lya.pluginengine.PluginEngine;

/**
 * Created by landy on 17/7/14.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PluginEngine.attachBaseContext(this);
    }
}
