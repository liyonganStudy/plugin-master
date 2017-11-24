package com.lya.pluginengine;

import android.app.Application;

import com.lya.pluginengine.utils.PatchClassLoaderUtils;

/**
 * Created by liyongan on 17/11/24.
 */

public class PluginEngine {

    /**
     * 宿主Application在attachBaseContext中调用
     * @param base 宿主Application
     */
    public static void attachBaseContext(Application base) {
        PatchClassLoaderUtils.patch(base);
    }
}
