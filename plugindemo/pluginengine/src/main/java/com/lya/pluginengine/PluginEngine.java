package com.lya.pluginengine;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.lya.pluginengine.utils.LogUtils;
import com.lya.pluginengine.utils.PatchClassLoaderUtils;
import com.netease.clousmusic.plugininterface.IPluginEngine;

import java.io.File;

/**
 * Created by liyongan on 17/11/24.
 */

public class PluginEngine implements IPluginEngine {
    private static PluginEngine sInstance;
    private PluginManager mPluginManager;
    private PluginContainerManager mContainerManager;
    private PluginLibraryHelper mPluginLibraryHelper;
    private Context mContext;

    public static PluginEngine getInstance() {
        if (sInstance == null) {
            sInstance = new PluginEngine();
        }
        return sInstance;
    }

    private PluginEngine() {
    }

    /**
     * 宿主Application在attachBaseContext中调用
     * @param base 宿主Application
     */
    public void attachBaseContext(Application base) {
        PatchClassLoaderUtils.patch(base);
        mPluginManager = new PluginManager(base);
        mContainerManager = new PluginContainerManager();
        mPluginLibraryHelper = new PluginLibraryHelper();
        mContext = base;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 尝试先加载插件中class
     * @param name
     * @return
     */
    public Class<?> loadClass(String name, boolean resolve) {
        if (mContainerManager.isContainerActivity(name)) {
            return mContainerManager.resolveActivityClass(name);
        }
        return null;
    }

    public Class<?> resolveActivityClass(String plugin, String activityName) {
        return mPluginManager.resolveActivityClass(plugin, activityName);
    }

    /**
     * 安装插件
     * @param path 插件下载地址
     * @return 插件相关信息
     */
    public PluginInfo install(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException();
        }
        // 判断文件合法性
        File file = new File(path);
        if (!file.exists()) {
            LogUtils.log("install: File not exists. path=" + path);
            return null;
        } else if (!file.isFile()) {
            LogUtils.log("install: File not exists. path=" + path);
            return null;
        }
        return mPluginManager.pluginDownloaded(path);
    }

    public boolean startActivity(Context context, Intent intent) {
        String pkg = intent.getComponent().getPackageName();
        String activity = intent.getComponent().getClassName();
        ActivityInfo activityInfo = mPluginManager.getActivityInfo(pkg, activity);
        if (activityInfo == null) {
            return false;
        }
        intent.putExtra(Constants.INTENT_KEY_THEME_ID, activityInfo.theme);
        ComponentName containerComponentName = mContainerManager.loadPluginActivity(activityInfo, intent, pkg, activity);
        if (containerComponentName == null) {
            return false;
        }
        intent.setComponent(containerComponentName);
        context.startActivity(intent);
        return true;
    }

    public Intent getBasicIntent(String packageName, String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, className));
        return intent;
    }

    @Override
    public Context getPluginContext(String plugin) {
        return mPluginManager.getPluginContext(plugin);
    }

    public void handleActivityCreateBefore(Activity activity, Bundle savedInstanceState) {
        mPluginLibraryHelper.handleActivityCreateBefore(activity, savedInstanceState);
    }

}