package com.lya.pluginengine;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.lya.pluginengine.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liyongan on 17/11/27.
 */

public class PluginManager {
    private Context mContext;
    private Map<String, Plugin> mPlugins = new HashMap<>();
    private ClassLoader mHostClassLoader;

    public PluginManager(Context context) {
        mContext = context;
    }

    public PluginInfo pluginDownloaded(String path) {
        // 读取apk内容
        PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_META_DATA);
        if (packageInfo == null) {
            LogUtils.log("pluginDownloaded: not a valid apk. path=" + path);
            return null;
        }
        // 使用apk的MetaData初始化PluginInfo
        PluginInfo pluginInfo = PluginInfo.parseFromPackageInfo(packageInfo, path);
        // 根据PluginInfo生成Plugin对象，更新内存表
        newPluginFound(pluginInfo);
        return pluginInfo;
    }

    private void newPluginFound(PluginInfo pluginInfo) {
        Plugin plugin = mPlugins.get(pluginInfo.getPackageName());
        if (plugin == null) {
            plugin = Plugin.build(pluginInfo);
        }
        if (mHostClassLoader == null) {
            mHostClassLoader = PluginManager.class.getClassLoader();
        }
        plugin.attach(mContext, mHostClassLoader);
        mPlugins.put(pluginInfo.getPackageName(), plugin);
    }

    private Plugin loadPlugin(String pluginName, int type) {
        Plugin plugin = mPlugins.get(pluginName);
        if (plugin == null) {
            return null;
        }
        if (!plugin.load(type)) {
            return null;
        }
        return plugin;
    }

    public ActivityInfo getActivityInfo(String pkg, String activity) {
        Plugin plugin = loadPlugin(pkg, Constants.LOAD_APP);
        if (plugin == null) {
            return null;
        }
        ActivityInfo activityInfo = null;
        if (!TextUtils.isEmpty(activity)) {
            activityInfo = plugin.getActivityInfo(activity);
        } else {
            // activity 为空时，根据 Intent 匹配
//            activityInfo = IntentMatcherHelper.getActivityInfo(mContext, plugin, intent);
        }
        return activityInfo;
    }

    public PluginContext getPluginContext(String pkg) {
        Plugin plugin = loadPlugin(pkg, Constants.LOAD_APP);
        if (plugin == null) {
            return null;
        }
        return plugin.getPluginContext();
    }

    public Class<?> resolveActivityClass(String pluginName, String activityName) {
        Plugin plugin = loadPlugin(pluginName, Constants.LOAD_APP);
        if (plugin == null) {
            return null;
        }
        return plugin.resolveActivityClass(activityName);
    }

}
