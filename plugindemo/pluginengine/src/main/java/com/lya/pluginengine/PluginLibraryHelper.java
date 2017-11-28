package com.lya.pluginengine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lya.pluginengine.utils.ReflectUtils;

/**
 * Created by liyongan on 17/11/28.
 */

public class PluginLibraryHelper {

    public void handleActivityCreateBefore(Activity activity, Bundle savedInstanceState) {
        // 对FragmentActivity做特殊处理
        if (savedInstanceState != null) {
            savedInstanceState.setClassLoader(activity.getClassLoader());
            try {
                savedInstanceState.remove("android:support:fragments");
            } catch (Throwable e) {
            }
        }
        // 对FragmentActivity做特殊处理
        Intent intent = activity.getIntent();
        if (intent != null) {
            intent.setExtrasClassLoader(activity.getClassLoader());
            activity.setTheme(getThemeId(activity, intent));
        }
    }

    private int getThemeId(Activity activity, Intent intent) {

        // 通过反射获取主题（可能获取到坑的主题，或者程序员通过代码设置的主题）
        int dynamicThemeId = getDynamicThemeId(activity);

        // 插件 manifest 中设置的 ThemeId
        int manifestThemeId = intent.getIntExtra(Constants.INTENT_KEY_THEME_ID, 0);
        //如果插件上没有主题则使用Application节点的Theme
        if (manifestThemeId == 0) {
            manifestThemeId = activity.getApplicationInfo().theme;
        }

        // 根据 manifest 中声明主题是否透明，获取默认主题
        int defaultThemeId = getDefaultThemeId();
        if (isTranslucentTheme(manifestThemeId)) {
            defaultThemeId = android.R.style.Theme_Translucent_NoTitleBar;
        }
//        if (defaultThemeId != -1) {
//            return defaultThemeId;
//        }
        int themeId;
        // 通过反射获取主题成功
        if (dynamicThemeId != -1) {
            // 如果动态主题是默认主题，说明插件未通过代码设置主题，此时应该使用 AndroidManifest 中设置的主题。
            if (dynamicThemeId == defaultThemeId) {
                // AndroidManifest 中有声明主题
                if (manifestThemeId != 0) {
                    themeId = manifestThemeId;
                } else {
                    themeId = defaultThemeId;
                }

            } else {
                // 动态主题不是默认主题，说明主题是插件通过代码设置的，使用此代码设置的主题。
                themeId = dynamicThemeId;
            }

            // 反射失败，检查 AndroidManifest 是否有声明主题
        } else {
            if (manifestThemeId != 0) {
                themeId = manifestThemeId;
            } else {
                themeId = defaultThemeId;
            }
        }
        return themeId;
    }

    private int getDynamicThemeId(Activity activity) {
        int dynamicThemeId = -1;
//        try {
//            dynamicThemeId = (int) ReflectUtils.invokeMethod(activity.getClassLoader(),
//                    "android.view.ContextThemeWrapper", "getThemeResId", activity, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return dynamicThemeId;
    }

    /**
     * 获取默认 ThemeID
     * 如果 Host 配置了使用 AppCompat，则此处通过反射调用 AppCompat 主题。
     * <p>
     * 注：Host 必须配置 AppCompat 依赖，否则反射调用会失败，导致宿主编译不过。
     */
    private static int getDefaultThemeId() {
        try {
            Class clazz = ReflectUtils.getClass("android.support.v7.appcompat.R$style");
            return (int) ReflectUtils.readStaticField(clazz, "Theme_AppCompat");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return android.R.style.Theme_NoTitleBar;
    }

    public boolean isTranslucentTheme(int theme) {
        return theme == android.R.style.Theme_Translucent
                || theme == android.R.style.Theme_Dialog
                || theme == android.R.style.Theme_Translucent_NoTitleBar
                || theme == android.R.style.Theme_Translucent_NoTitleBar_Fullscreen;
    }
}
