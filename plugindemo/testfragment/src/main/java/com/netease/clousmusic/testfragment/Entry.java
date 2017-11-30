package com.netease.clousmusic.testfragment;

import com.netease.clousmusic.plugininterface.IPlugin;
import com.netease.clousmusic.plugininterface.IPluginEngine;
import com.netease.clousmusic.plugininterface.IPluginHost;

import java.io.Serializable;

/**
 * Created by liyongan on 17/11/28.
 */

public class Entry implements IPlugin {
    private static Entry sInstance;
    private Entry() {}
    private IPluginHost mPluginHost;
    private IPluginEngine mPluginEngine;
    private BaseActivity mActivity;

    public static Entry getInstance() {
        if (sInstance == null) {
            sInstance = new Entry();
        }
        return sInstance;
    }

    public void bindToHost(BaseActivity activity) {
        mActivity = activity;
    }

    public void sendToHost(int i, Serializable serializable) {
        if (mPluginHost != null) {
            mPluginHost.sentToHost(Constants.PACKAGE_NAME, i, serializable);
        }
    }

    @Override
    public void sendToPlugin(int i, Serializable serializable) {
        if (mActivity != null) {
            mActivity.handleHostMessage(i, serializable);
        }
    }

    @Override
    public void attach(IPluginHost iPluginHost) {
        mPluginHost = iPluginHost;
        mPluginEngine = iPluginHost.getPluginEngine();
    }

    public IPluginEngine getPluginEngine() {
        return mPluginEngine;
    }

    public IPluginHost getPluginHost() {
        return mPluginHost;
    }
}
