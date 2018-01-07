package com.example.weihanbo.group_project.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.weihanbo.group_project.R;
import com.example.weihanbo.group_project.model.Envi;
import com.example.weihanbo.group_project.network.API;
import com.example.weihanbo.group_project.utils.IntentUtils;
import com.example.weihanbo.group_project.utils.Tools;
import com.facebook.drawee.backends.pipeline.Fresco;

public class MainActivity extends AppCompatActivity {

    private Activity activity;
    private Context context;
    private IntentUtils intentUtils;
    private Tools tools;
    private SharedPreferences sharedPreferences;
    private API api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        activity = this;
        context = this;
        api = new API(activity,context);
        sharedPreferences = getSharedPreferences(Envi.shared,0);
        intentUtils = new IntentUtils(activity,context);
        tools = new Tools(activity,context);
        init();
    }
    private void init(){
        new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                intentUtils.startHomeActivity();
                finish();
            }
        }.sendEmptyMessageDelayed(0, 1500);
    }
}
