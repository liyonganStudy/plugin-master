package com.netease.clousmusic.plugininterface;

import java.io.Serializable;

/**
 * Created by liyongan on 17/11/29.
 */

public interface IPluginHost {
    void sentToHost(String pluginName, int code, Serializable content);

    IPluginEngine getPluginEngine();
}
