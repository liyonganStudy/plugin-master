package com.lya.plugindemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lya.pluginengine.PluginEngine;
import com.lya.pluginengine.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.installButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simulateInstallExternalPlugin("demo3.apk");
                simulateInstallExternalPlugin("fragment.apk");
                Toast.makeText(MainActivity.this, "安装demo3.apk和fragment.apk", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PluginEngine.getInstance().getBasicIntent("com.lya.testplugin", "com.lya.testplugin.MainActivity");
                PluginEngine.getInstance().startActivity(MainActivity.this, intent);
            }
        });

        findViewById(R.id.openActivityButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InHostActivity.class);
                startActivity(intent);
            }
        });
    }

    private void simulateInstallExternalPlugin(String apkName) {
//        String demo3Apk= "demo3.apk";

        // 文件是否已经存在？直接删除重来
        String pluginFilePath = getFilesDir().getAbsolutePath() + File.separator + apkName;
        File pluginFile = new File(pluginFilePath);
        if (pluginFile.exists()) {
            FileUtils.deleteQuietly(pluginFile);
        }
        copyAssetsFileToAppFiles(apkName, apkName);
        if (pluginFile.exists()) {
            PluginEngine.getInstance().install(pluginFilePath);
        }
    }

    /**
     * 从assets目录中复制某文件内容
     *  @param  assetFileName assets目录下的Apk源文件路径
     *  @param  newFileName 复制到/data/data/package_name/files/目录下文件名
     */
    private void copyAssetsFileToAppFiles(String assetFileName, String newFileName) {
        InputStream is = null;
        FileOutputStream fos = null;
        int buffsize = 1024;

        try {
            is = this.getAssets().open(assetFileName);
            fos = this.openFileOutput(newFileName, Context.MODE_PRIVATE);
            int byteCount = 0;
            byte[] buffer = new byte[buffsize];
            while((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
