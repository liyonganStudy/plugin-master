package com.lya.testplugin;

import android.app.Activity;
import android.content.Context;

import com.netease.clousmusic.plugininterface.IPluginEngine;

public abstract class BaseActivity extends Activity {
//public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        IPluginEngine pluginEngine = Entry.sPluginEngine;
        if (pluginEngine != null) {
            newBase = pluginEngine.getPluginContext(Constants.PACKAGE_NAME);
        }
        super.attachBaseContext(newBase);
    }

}
