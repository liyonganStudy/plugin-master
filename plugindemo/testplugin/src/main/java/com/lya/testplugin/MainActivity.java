package com.lya.testplugin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Entry.getInstance().bindToHost(this);
        } catch (Throwable throwable) {

        }
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Entry.getInstance().sendToHost(1, "1 + 2 = ?");
            }
        });
    }

    @Override
    protected void handleHostMessage(int code, Serializable serializable) {
        super.handleHostMessage(code, serializable);
        Toast.makeText(this, "从宿主收到答案： " + serializable.toString(), Toast.LENGTH_SHORT).show();
    }
}
