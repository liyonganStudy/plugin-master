package com.lya.testplugin;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.netease.clousmusic.plugininterface.IPluginEngine;

//public abstract class BaseActivity extends Activity {
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        IPluginEngine pluginEngine = Entry.sPluginEngine;
        if (pluginEngine != null) {
            newBase = pluginEngine.getPluginContext(Constants.PACKAGE_NAME);
        }
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        IPluginEngine pluginEngine = Entry.sPluginEngine;
        if (pluginEngine != null) {
            pluginEngine.handleActivityCreateBefore(this, savedInstanceState);
        }
        super.onCreate(savedInstanceState);
    }
}
