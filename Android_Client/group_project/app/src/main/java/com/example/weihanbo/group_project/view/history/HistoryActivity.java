package com.example.weihanbo.group_project.view.history;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.weihanbo.group_project.R;
import com.example.weihanbo.group_project.model.Envi;
import com.example.weihanbo.group_project.network.API;
import com.example.weihanbo.group_project.utils.IntentUtils;
import com.example.weihanbo.group_project.utils.Tools;
import com.example.weihanbo.group_project.view.SetTop;
import com.example.weihanbo.group_project.view.history.adapter.HistoryAdapter;
import com.example.weihanbo.group_project.view.history.model.HistoryModel;
import com.example.weihanbo.group_project.view.history.model.PhotoModel;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weihanbo on 2017/11/26.
 */

public class HistoryActivity   extends AppCompatActivity {

    private Activity activity;
    private Context context;
    private IntentUtils intentUtils;
    private Tools tools;
    private SharedPreferences sharedPreferences;
    private API api;
    private SetTop s;
    private List<HistoryModel> list = new ArrayList<HistoryModel>();

    private ListView listView;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.layout_history);
        activity = this;
        context = this;
        api = new API(activity,context);
        sharedPreferences = getSharedPreferences(Envi.shared,0);
        intentUtils = new IntentUtils(activity,context);
        tools = new Tools(activity,context);

        initView();
        setListener();
        getData();
    }
    private void initView(){
        s = new SetTop(activity,context);
        s.cancelVisible(true);
        s.titleVisible(true);
        s.addVisible(false);
        s.setTitle("History footprints");
        s.setCancelListener(btnListener);

        listView = (ListView)findViewById(R.id.listview);
    }
    private void setListener(){

    }
    private View.OnClickListener btnListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.cancel_ln:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
    private void getData(){
        List<PhotoModel> photoList = new ArrayList<PhotoModel>();
        for(int i=0;i<8;++i){
            photoList.add(new PhotoModel(Envi.TEST_URL,"London Bridge"));
        }
        for(int i=0;i<6;i++){
            list.add(new HistoryModel(photoList,"27/11/2017"));
        }
        setList(list);
    }
    private void setList(List<HistoryModel> list){
        adapter = new HistoryAdapter(activity,context,list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
