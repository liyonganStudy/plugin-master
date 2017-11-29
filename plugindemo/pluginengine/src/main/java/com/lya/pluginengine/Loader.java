package com.lya.pluginengine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.lya.pluginengine.utils.LogUtils;
import com.netease.clousmusic.plugininterface.IPluginEngine;

import java.lang.reflect.Method;

/**
 * Created by liyongan on 17/11/27.
 */

public class Loader {
    private String mPath;
    private ComponentList mComponents;
    private PluginInfo mInfo;
    private Resources mPkgResources;
    private ClassLoader mClassLoader;
    private PluginContext mPkgContext;
    private String mPluginName;
    private ClassLoader mHostClassLoader;

    public Loader(String path, String pluginName, PluginInfo info, ClassLoader hostClassLoader) {
        mPath = path;
        mPluginName = pluginName;
        mInfo = info;
        mHostClassLoader = hostClassLoader;
    }

    public void invokeEntryCreate() {
        try {
            String className = mPluginName + "." + "Entry";
            Class<?> c = mClassLoader.loadClass(className);
            Class<?> params[] = {IPluginEngine.class};
            Method createMethod = c.getDeclaredMethod("create", params);
            createMethod.invoke(null, PluginEngine.getInstance());
        } catch (Throwable e) {
        }
    }

    boolean loadDex(Context context, int load) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(mPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
            if (packageInfo == null || packageInfo.applicationInfo == null) {
                LogUtils.log("get package archive info null");
                return false;
            }
            packageInfo.applicationInfo.sourceDir = mPath;
            packageInfo.applicationInfo.publicSourceDir = mPath;
            if (TextUtils.isEmpty(packageInfo.applicationInfo.processName)) {
                packageInfo.applicationInfo.processName = packageInfo.applicationInfo.packageName;
            }
            if (mComponents == null) {
                mComponents = new ComponentList(packageInfo, mPath, mInfo);
                // 调整插件中 Activity 的 TaskAffinity
//                adjustPluginTaskAffinity(mPluginName, mPackageInfo.applicationInfo);
            }
            if (mPkgResources == null) {
                try {
                    if (BuildConfig.DEBUG) {
                        // 如果是Debug模式的话，防止与Instant Run冲突，资源重新New一个
                        Resources r = pm.getResourcesForApplication(packageInfo.applicationInfo);
                        mPkgResources = new Resources(r.getAssets(), r.getDisplayMetrics(), r.getConfiguration());
                    } else {
                        mPkgResources = pm.getResourcesForApplication(packageInfo.applicationInfo);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    return false;
                }
                if (mPkgResources == null) {
                    return false;
                }
            }
            if (mClassLoader == null) {
                String out = mInfo.getDexParentDir().getPath();
                ClassLoader parent;
                if (BuildConfig.DEBUG) {
                    // 因为Instant Run会替换parent为IncrementalClassLoader，所以在DEBUG环境里
                    // 需要替换为BootClassLoader才行
                    // Added by yangchao-xy & Jiongxuan Zhang
                    parent = ClassLoader.getSystemClassLoader();
                } else {
                    // 线上环境保持不变
                    parent = getClass().getClassLoader().getParent(); // TODO: 这里直接用父类加载器
                }
                String soDir = packageInfo.applicationInfo.nativeLibraryDir;
                mClassLoader = new PluginDexClassLoader(mPath, out, soDir, parent, mHostClassLoader);
            }
            mPkgContext = new PluginContext(context, android.R.style.Theme, mClassLoader, mPkgResources, mPluginName, this);
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    public ClassLoader getClassLoader() {
        return mClassLoader;
    }

    public String getPath() {
        return mPath;
    }

    public PluginContext getPkgContext() {
        return mPkgContext;
    }

    public ComponentList getComponents() {
        return mComponents;
    }
}
