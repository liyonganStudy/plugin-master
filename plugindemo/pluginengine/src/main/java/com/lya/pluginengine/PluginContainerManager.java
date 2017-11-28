package com.lya.pluginengine;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import java.util.HashMap;

/**
 * Created by liyongan on 17/11/27.
 */

public class PluginContainerManager {

    private HashMap<String, ActivityState> mStates = new HashMap<>();

    public PluginContainerManager() {
        ActivityState activityState = new ActivityState();
        activityState.container = "com.lya.plugindemo.PluginContainerActivity";
        mStates.put(activityState.container, activityState);
    }

    class ActivityState {
        String container;
        String pluginName;
        String activityName;
    }

    public ComponentName loadPluginActivity(ActivityInfo activityInfo, Intent intent, String packageName, String activity) {
        ActivityState target = null;
        for (ActivityState activityState : mStates.values()) {
            activityState.pluginName = packageName;
            activityState.activityName = activity;
            target = activityState;
            break;
        }
        if (target == null) {
            return null;
        }
        String containerName = target.container;
        return new ComponentName(PluginEngine.getInstance().getContext().getPackageName(), containerName);
    }

    final ActivityState lookupByContainer(String container) {
        if (container == null) {
            return null;
        }

        HashMap<String, ActivityState> map = mStates;
        ActivityState state = map.get(container);
//        if (state != null && state.state != STATE_NONE) {
//            return new ActivityState(state);
//        }
        return null;
    }
}
