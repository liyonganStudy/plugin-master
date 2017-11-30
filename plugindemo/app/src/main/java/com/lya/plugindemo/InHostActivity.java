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

//        installFragment("com.netease.clousmusic.testfragment");// 这个插件中fragment是provided，会自动找宿主中framgnet
        installFragment("com.lya.testplugin"); // 这个插件中framgnet插件中也有，需要在PluginDexClassLoader中特别处理
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
