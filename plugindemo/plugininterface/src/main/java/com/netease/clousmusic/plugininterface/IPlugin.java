package com.netease.clousmusic.plugininterface;

import java.io.Serializable;

/**
 * Created by liyongan on 17/11/29.
 */

public interface IPlugin {
    void sendToPlugin(int command, Serializable content);

    void attach(IPluginHost iPluginHost);
}
