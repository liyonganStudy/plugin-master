package com.lya.pluginengine;

import android.content.Context;
import android.content.pm.ActivityInfo;

import com.lya.pluginengine.utils.LogUtils;
import com.netease.clousmusic.plugininterface.IPlugin;

import java.io.Serializable;

/**
 * Created by liyongan on 17/11/27.
 */

public class Plugin {
    private PluginInfo mPluginInfo;
    private Context mHostContext;
    private ClassLoader mHostClassLoader;
    private Loader mLoader;
    private IPlugin mPlugin;

    public Plugin(PluginInfo pluginInfo) {
        mPluginInfo = pluginInfo;
    }

    public static Plugin build(PluginInfo info) {
        return new Plugin(info);
    }

    public void attach(Context context, ClassLoader classLoader) {
        mHostClassLoader = classLoader;
        mHostContext = context;
    }

    public ClassLoader getPluginClassLoader() {
        return mLoader.getClassLoader();
    }

    public PluginContext getPluginContext() {
        return mLoader.getPkgContext();
    }

    public ActivityInfo getActivityInfo(String activityName) {
        return mLoader.getComponents().getActivity(activityName);
    }

    public Class<?> resolveActivityClass(String activityName) {
        ClassLoader cl = mLoader.getClassLoader();
        Class<?> c = null;
        try {
            c = cl.loadClass(activityName);
            LogUtils.logForClassLoader("=======PluginDexClassLoader resolveActivityClass: " + activityName + " result: " + c);
        } catch (Throwable e) {
        }
        return c;
    }

    public boolean load(int type) {
        if (mLoader == null) {
            mLoader = new Loader(mPluginInfo.getPath(), mPluginInfo.getPackageName(), mPluginInfo, mHostClassLoader);
            if (!mLoader.loadDex(mHostContext, type)) {
                return false;
            }
            if (type == Constants.LOAD_APP) {
                mPlugin = mLoader.invokeEntryCreate();
            }
        }
        return true;
    }

    public void sendToPlugin(int code, Serializable content) {
        if (mPlugin != null) {
            mPlugin.sendToPlugin(code, content);
        }
    }
}
