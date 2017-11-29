package com.lya.pluginengine.utils;

import android.util.Log;

/**
 * Created by liyongan on 17/9/5.
 */

public class LogUtils {
    public static void log(String content) {
        Log.d("lya", content);
    }

    public static void logForClassLoader(String content) {
        Log.d("lyaClassloader", content);
    }

    public static void logStackTrace(String title) {
        StackTraceElement elements[] = Thread.currentThread().getStackTrace();
        for (StackTraceElement item : elements) {
            if (item.isNativeMethod()) {
                continue;
            }
            String cn = item.getClassName();
            String mn = item.getMethodName();
            String filename = item.getFileName();
            int line = item.getLineNumber();
            log(cn + "." + mn + "(" + filename + ":" + line + ")" + "\n");
        }
        log("\n\n");
    }
}
