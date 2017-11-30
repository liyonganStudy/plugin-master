package com.netease.clousmusic.testfragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.netease.clousmusic.plugininterface.IPluginEngine;

import java.io.Serializable;

public abstract class BaseActivity extends Activity {
//public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        try {
            IPluginEngine pluginEngine = Entry.getInstance().getPluginEngine();
            newBase = pluginEngine.getPluginContext(Constants.PACKAGE_NAME);
        } catch (Throwable throwable) {

        }

        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            IPluginEngine pluginEngine = Entry.getInstance().getPluginEngine();
            pluginEngine.handleActivityCreateBefore(this, savedInstanceState);
        } catch (Throwable throwable) {

        }
        super.onCreate(savedInstanceState);
    }

    protected void handleHostMessage(int code, Serializable serializable) {

    }
}
