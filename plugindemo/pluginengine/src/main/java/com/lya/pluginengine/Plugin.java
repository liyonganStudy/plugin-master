package com.lya.pluginengine;

import android.content.Context;

/**
 * Created by liyongan on 17/11/27.
 */

public class Plugin {
    private PluginInfo mPluginInfo;
    private Context mHostContext;
    private ClassLoader mHostClassLoader;
    private boolean mInitialized;
    private Loader mLoader;

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

    public Loader getLoader() {
        return mLoader;
    }

    public ClassLoader getClassLoader() {
        return mLoader.mClassLoader;
    }

    public boolean load(int type) {
        if (mInitialized) {

        }
        mInitialized = true;
        if (mLoader == null) {
            mLoader = new Loader(mPluginInfo.getPath(), mPluginInfo.getPackageName(), mPluginInfo);
            if (!mLoader.loadDex(mHostContext, type)) {
                return false;
            }
            if (type == Constants.LOAD_APP) {

            }
        }
        if (type == Constants.LOAD_APP) {
            return mLoader.isAppLoaded();
        }
        return false;
    }
}
