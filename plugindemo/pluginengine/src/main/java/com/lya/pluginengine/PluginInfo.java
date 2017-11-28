package com.lya.pluginengine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;

import java.io.File;

/**
 * Created by liyongan on 17/11/27.
 */

public class PluginInfo {
    private String mPackageName;
    private String mPath;

    public PluginInfo(String packageName, String path) {
        mPackageName = packageName;
        mPath = path;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getPath() {
        return mPath;
    }

    public static PluginInfo parseFromPackageInfo(PackageInfo pi, String path) {
        ApplicationInfo applicationInfo = pi.applicationInfo;
        String packageName = pi.packageName;
//        String alias = null;
//        int low = 0;
//        int high = 0;
//        int ver = 0;
        Bundle metaData = applicationInfo.metaData;
        // 优先读取MetaData中的内容（如有），并覆盖上面的默认值
        if (metaData != null) {
//             获取插件别名（如有），如无则将"包名"当做插件名
//            alias = metaData.getString("com.qihoo360.plugin.name");
//             获取最低/最高协议版本（默认为应用的最小支持版本，以保证一定能在宿主中运行）
//            low = metaData.getInt("com.qihoo360.plugin.version.low");
//            high = metaData.getInt("com.qihoo360.plugin.version.high");
//             获取插件的版本号。优先从metaData中读取，如无则使用插件的VersionCode
//            ver = metaData.getInt("com.qihoo360.plugin.version.ver");
        }
        PluginInfo pluginInfo = new PluginInfo(packageName, path);
        return pluginInfo;
    }

    /**
     * 获取Dex（优化后）生成时所在的目录 <p>
     *
     * Android O之前：
     * 若为"纯APK"插件，则会位于app_p_od中；若为"p-n"插件，则会位于"app_plugins_v3_odex"中 <p>
     * 若支持同版本覆盖安装的话，则会位于app_p_c中； <p>
     *
     * Android O：
     * APK存放目录/oat/{cpuType}
     *
     * 注意：仅供框架内部使用
     * @return 优化后Dex所在目录的File对象
     */
    public File getDexParentDir() {

        // 必须使用宿主的Context对象，防止出现“目录定位到插件内”的问题
        Context context = PluginEngine.getInstance().getContext();

//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
//            return new File(getApkDir() + File.separator + "oat" + File.separator + getArtOatCpuType());
//        } else {
//        }
        return context.getDir(Constants.LOCAL_PLUGIN_APK_ODEX_SUB_DIR, 0);
    }

    /**
     * 获取APK存放目录
     *
     * @return
     */
    public String getApkDir() {
        // 必须使用宿主的Context对象，防止出现“目录定位到插件内”的问题
        Context context = PluginEngine.getInstance().getContext();
        File dir = context.getDir(Constants.LOCAL_PLUGIN_APK_SUB_DIR, 0);
        return dir.getAbsolutePath();
    }
}
