package com.lya.plugindemo;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.lya.pluginengine.PluginEngine;
import com.netease.clousmusic.plugininterface.IPluginEngine;
import com.netease.clousmusic.plugininterface.IPluginHost;

import java.io.Serializable;

/**
 * Created by landy on 17/7/14.
 */

public class MyApplication extends Application implements IPluginHost{
    private Handler mHandler = new Handler();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PluginEngine.getInstance().attachBaseContext(this, this);
    }

    @Override
    public void sentToHost(final String s, int i, Serializable serializable) {
        Toast.makeText(this, "宿主收到问题：" + serializable.toString(), Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PluginEngine.getInstance().sendToPlugin(s, 1, "1 + 2 = 3");
            }
        }, 1000);
    }

    @Override
    public IPluginEngine getPluginEngine() {
        return PluginEngine.getInstance();
    }
}
