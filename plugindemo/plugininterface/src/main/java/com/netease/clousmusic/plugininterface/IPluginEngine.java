package com.netease.clousmusic.plugininterface;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by liyongan on 17/11/28.
 */

public interface IPluginEngine {
    Context getPluginContext(String plugin);

    void handleActivityCreateBefore(Activity activity, Bundle savedInstanceState);
}
