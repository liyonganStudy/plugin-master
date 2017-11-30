package com.lya.testplugin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netease.clousmusic.plugininterface.IPluginEngine;

/**
 * Created by liyongan on 17/11/29.
 */

public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context;
        try {
            IPluginEngine pluginEngine = Entry.getInstance().getPluginEngine();
            context = pluginEngine.getPluginContext(Constants.PACKAGE_NAME);
        } catch (Throwable throwable) {
            context = getContext();
        }
        return LayoutInflater.from(context).inflate(R.layout.fragment_main, container, false);
    }
}
