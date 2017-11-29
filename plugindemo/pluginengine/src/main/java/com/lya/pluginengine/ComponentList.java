/*
 * Copyright (C) 2005-2017 Qihoo 360 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.lya.pluginengine;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Environment;
import android.text.TextUtils;

import com.lya.pluginengine.manifestparser.ApkCommentReader;
import com.lya.pluginengine.manifestparser.ApkParser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * 用来快速获取四大组件和Application的系统Info的List
 * NOTE 每个Plugin对象维护一份ComponentList，且在第一次加载PackageInfo时被生成
 * 参考Replugin ComponetList
 *
 * @see ActivityInfo
 * @see ServiceInfo
 * @see ProviderInfo
 * @see ApplicationInfo
 */
public class ComponentList {
    /**
     * Class类名 - Activity的Map表
     */
    private HashMap<String, ActivityInfo> mActivities = new HashMap<>();

    /**
     * Application对象
     */
    private ApplicationInfo mApplication = null;

    /**
     * 初始化ComponentList对象 <p>
     * 注意：仅框架内部使用
     */
    public ComponentList(PackageInfo pi, String path, PluginInfo pli) {
        if (pi.activities != null) {
            for (ActivityInfo ai : pi.activities) {
                ai.applicationInfo.sourceDir = path;
                if (ai.processName == null) {
                    ai.processName = ai.applicationInfo.processName;
                }
                if (ai.processName == null) {
                    ai.processName = ai.packageName;
                }
                mActivities.put(ai.name, ai);
            }
        }
        // 不知道下面code作用？
        // 解析 Apk 中的 AndroidManifest.xml
//        String manifest = getManifestFromApk(path);
        // 生成组件与 IntentFilter 的对应关系
//        ManifestParser.INS.parse(pli, manifest);

        mApplication = pi.applicationInfo;

        if (mApplication.dataDir == null) {
            mApplication.dataDir = Environment.getDataDirectory() + File.separator + "data" + File.separator + mApplication.packageName;
        }
    }

    /**
     * 从 APK 中获取 Manifest 内容
     *
     * @param apkFile apk 文件路径
     * @return apk 中 AndroidManifest 中的内容
     */
    private static String getManifestFromApk(String apkFile) {
        // 先从 Apk comment 中解析 AndroidManifest
        String manifest = ApkCommentReader.readComment(apkFile);
        if (!TextUtils.isEmpty(manifest)) {
            return manifest;
        }
        // 解析失败时，再从 apk 中解析
        ApkParser parser = null;
        try {
            parser = new ApkParser(apkFile);
            manifest = parser.getManifestXml();
            return manifest;

        } catch (IOException t) {
            t.printStackTrace();
        } finally {
            if (parser != null) {
                try {
                    parser.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 获取ActivityInfo对象
     */
    public ActivityInfo getActivity(String className) {
        return mActivities.get(className);
    }

    /**
     * 获取Application对象
     */
    public ApplicationInfo getApplication() {
        return mApplication;
    }
}
