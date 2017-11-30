package com.lya.plugindemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.lya.pluginengine.PluginEngine;

public class InHostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_host);

        installFragment("com.netease.clousmusic.testfragment");
//        installFragment("com.lya.testplugin");
    }

    private void installFragment(String packageName) {
        //代码使用插件Fragment
        ClassLoader d1ClassLoader = PluginEngine.getInstance().getPluginClassLoader(packageName);//获取插件的ClassLoader
        try {
            Fragment fragment = d1ClassLoader.loadClass(packageName + ".MainFragment").asSubclass(Fragment.class).newInstance();//使用插件的Classloader获取指定Fragment实例
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment).commit();//添加Fragment到UI
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
