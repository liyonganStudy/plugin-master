package com.lya.plugindemo;

import android.app.Application;
import android.content.Context;

import com.lya.pluginengine.PluginEngine;
import com.netease.clousmusic.plugininterface.IPluginEngine;
import com.netease.clousmusic.plugininterface.PluginHost;

/**
 * Created by landy on 17/7/14.
 */

public class MyApplication extends Application implements PluginHost {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PluginEngine.getInstance().attachBaseContext(this);
    }

    @Override
    public IPluginEngine getPluginEngine() {
        return PluginEngine.getInstance();
    }
}
