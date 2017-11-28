package com.lya.pluginengine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.lya.pluginengine.utils.LogUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by liyongan on 17/11/27.
 */

public class Loader {
    PackageInfo mPackageInfo;
    String mPath;
    ComponentList mComponents;
    PluginInfo mInfo;
    Resources mPkgResources;
    ClassLoader mClassLoader;
    PluginContext mPkgContext;
    private String mPluginName;

    /**
     * layout缓存：忽略表
     */
    HashSet<String> mIgnores = new HashSet<String>();

    /**
     * layout缓存：构造器表
     */
    HashMap<String, Constructor<?>> mConstructors = new HashMap<String, Constructor<?>>();

    public Loader(String path, String pluginName, PluginInfo info) {
        mPath = path;
        mPluginName = pluginName;
        mInfo = info;
    }

    public boolean isAppLoaded() {
        return true;
    }

    boolean loadDex(Context context, int load) {
        try {
            PackageManager pm = context.getPackageManager();
//            mPackageInfo = Plugin.queryCachedPackageInfo(mPath);
            if (mPackageInfo == null) {
                mPackageInfo = pm.getPackageArchiveInfo(mPath,
                        PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS | PackageManager.GET_META_DATA);
                if (mPackageInfo == null || mPackageInfo.applicationInfo == null) {
                    LogUtils.log("get package archive info null");
                    mPackageInfo = null;
                    return false;
                }
                mPackageInfo.applicationInfo.sourceDir = mPath;
                mPackageInfo.applicationInfo.publicSourceDir = mPath;
                if (TextUtils.isEmpty(mPackageInfo.applicationInfo.processName)) {
                    mPackageInfo.applicationInfo.processName = mPackageInfo.applicationInfo.packageName;
                }

                // 添加针对SO库的加载
                // 此属性最终用于ApplicationLoaders.getClassLoader，在创建PathClassLoader时成为其参数
                // 这样findLibrary可不用覆写，即可直接实现SO的加载
                // Added by Jiongxuan Zhang
//                PluginInfo pi = mPluginObj.mInfo;
//                File ld = pi.getNativeLibsDir();
//                mPackageInfo.applicationInfo.nativeLibraryDir = ld.getAbsolutePath();

                // 缓存表: pkgName -> pluginName
//                synchronized (Plugin.PKG_NAME_2_PLUGIN_NAME) {
//                    Plugin.PKG_NAME_2_PLUGIN_NAME.put(mPackageInfo.packageName, mPluginName);
//                }

                // 缓存表: pluginName -> fileName
//                synchronized (Plugin.PLUGIN_NAME_2_FILENAME) {
//                    Plugin.PLUGIN_NAME_2_FILENAME.put(mPluginName, mPath);
//                }

                // 缓存表: fileName -> PackageInfo
//                synchronized (Plugin.FILENAME_2_PACKAGE_INFO) {
//                    Plugin.FILENAME_2_PACKAGE_INFO.put(mPath, new WeakReference<PackageInfo>(mPackageInfo));
//                }
            }
            // 创建或获取ComponentList表
            // Added by Jiongxuan Zhang
//            mComponents = Plugin.queryCachedComponentList(mPath);
            if (mComponents == null) {
                // ComponentList
                mComponents = new ComponentList(mPackageInfo, mPath, mInfo);

                // 动态注册插件中声明的 receiver
//                regReceivers();

                // 缓存表：ComponentList
//                synchronized (Plugin.FILENAME_2_COMPONENT_LIST) {
//                    Plugin.FILENAME_2_COMPONENT_LIST.put(mPath, new WeakReference<>(mComponents));
//                }

                /* 只调整一次 */
                // 调整插件中组件的进程名称
//                adjustPluginProcess(mPackageInfo.applicationInfo);

                // 调整插件中 Activity 的 TaskAffinity
//                adjustPluginTaskAffinity(mPluginName, mPackageInfo.applicationInfo);
            }

//            if (load == Plugin.LOAD_INFO) {
//                return isPackageInfoLoaded();
//            }

//            mPkgResources = Plugin.queryCachedResources(mPath);
            // LOAD_RESOURCES和LOAD_ALL都会获取资源，但LOAD_INFO不可以（只允许获取PackageInfo）
            if (mPkgResources == null) {
                // Resources
                try {
                    if (BuildConfig.DEBUG) {
                        // 如果是Debug模式的话，防止与Instant Run冲突，资源重新New一个
                        Resources r = pm.getResourcesForApplication(mPackageInfo.applicationInfo);
                        mPkgResources = new Resources(r.getAssets(), r.getDisplayMetrics(), r.getConfiguration());
                    } else {
                        mPkgResources = pm.getResourcesForApplication(mPackageInfo.applicationInfo);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    return false;
                }
                if (mPkgResources == null) {
                    return false;
                }
                // 缓存表: Resources
//                synchronized (Plugin.FILENAME_2_RESOURCES) {
//                    Plugin.FILENAME_2_RESOURCES.put(mPath, new WeakReference<>(mPkgResources));
//                }
            }
//            if (load == Plugin.LOAD_RESOURCES) {
//                return isResourcesLoaded();
//            }

//            mClassLoader = Plugin.queryCachedClassLoader(mPath);
            if (mClassLoader == null) {
                // ClassLoader
                String out = mInfo.getDexParentDir().getPath();
                //changeDexMode(out);

                //
                Log.i("dex", "load " + mPath + " ...");
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
                String soDir = mPackageInfo.applicationInfo.nativeLibraryDir;
                mClassLoader = new PluginDexClassLoader(mPath, out, soDir, parent);
                Log.i("dex", "load " + mPath + " = " + mClassLoader);

                if (mClassLoader == null) {
                    return false;
                }

                // 缓存表：ClassLoader
//                synchronized (Plugin.FILENAME_2_DEX) {
//                    Plugin.FILENAME_2_DEX.put(mPath, new WeakReference<>(mClassLoader));
//                }
            }
//            if (load == Plugin.LOAD_DEX) {
//                return isDexLoaded();
//            }

            // Context
            mPkgContext = new PluginContext(context, android.R.style.Theme, mClassLoader, mPkgResources, mPluginName, this);
        } catch (Throwable e) {
            return false;
        }
        return true;
    }
}
