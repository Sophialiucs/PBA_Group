package com.example.weihanbo.group_project.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weihanbo.group_project.R;

/**
 * Created by weihanbo on 2017/11/23.
 */

public class SetTop {
    private Activity activity;
    private Context context;
    private LinearLayout cancelLn,addLn;
    private TextView titleTv,addTv;
    private ImageView addIv;
    public SetTop(Activity activity,Context context){
        this.activity = activity;
        this.context = context;
        cancelLn = (LinearLayout)activity.findViewById(R.id.cancel_ln);
        addLn = (LinearLayout)activity.findViewById(R.id.add_ln);
        titleTv = (TextView)activity.findViewById(R.id.title_tv);
        addIv = (ImageView)activity.findViewById(R.id.add_iv);
        addTv = (TextView)activity.findViewById(R.id.add_tv);
    }
    public void setTitle(String title){
        titleTv.setText(title);
    }
    public void titleVisible(boolean b){
        if(b){
            titleTv.setVisibility(View.VISIBLE);
        }else{
            titleTv.setVisibility(View.GONE);
        }
    }
    public void cancelVisible(boolean b){
        if(b){
            cancelLn.setVisibility(View.VISIBLE);
        }else{
            cancelLn.setVisibility(View.GONE);
        }
    }
    public void addVisible(boolean b){
        if(b){
            addLn.setVisibility(View.VISIBLE);
        }else{
            addLn.setVisibility(View.GONE);
        }
    }
    public void setAddIv(Drawable d){
        if(d == null){
            addIv.setVisibility(View.GONE);
        }else{
            addIv.setImageDrawable(d);
            addIv.setVisibility(View.VISIBLE);
        }
    }
    public void setAddTv(String s){
        addTv.setText(s);
    }
    public void setCancelListener(View.OnClickListener listener){
        cancelLn.setOnClickListener(listener);
    }
    public void setAddListener(View.OnClickListener listener){
        addLn.setOnClickListener(listener);
    }
}
