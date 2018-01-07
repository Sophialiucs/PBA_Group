package com.example.weihanbo.group_project.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.weihanbo.group_project.view.history.HistoryActivity;
import com.example.weihanbo.group_project.view.home.HomeActivity;
import com.example.weihanbo.group_project.view.result.resultActivity;

/**
 * Created by weihanbo on 2017/11/23.
 */

public class IntentUtils {
    private Activity activity;
    private Context context;
    public IntentUtils(Activity activity,Context context){
        this.activity = activity;
        this.context = context;
    }

    private void startActivity(Intent intent){
        context.startActivity(intent);
    }

    private void startActivityForResult(Intent intent,int request){
        activity.startActivityForResult(intent,request);
    }

    public void startHomeActivity(){
        Intent intent = new Intent(context, HomeActivity.class);
        startActivity(intent);
    }

    public void startResultActivity(String result){
        Intent intent = new Intent(context, resultActivity.class);
        intent.putExtra("result",result);
        startActivity(intent);
    }

    public void startHistoryActivity(){
        Intent intent = new Intent(context, HistoryActivity.class);
        startActivity(intent);
    }
}
