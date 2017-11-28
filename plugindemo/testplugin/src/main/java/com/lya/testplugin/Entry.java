package com.lya.testplugin;

import com.netease.clousmusic.plugininterface.IPluginEngine;

/**
 * Created by liyongan on 17/11/28.
 */

public class Entry {
    public static IPluginEngine sPluginEngine;

    public static void create(IPluginEngine iPluginEngine) {
        sPluginEngine = iPluginEngine;
    }
}
