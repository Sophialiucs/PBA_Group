package com.example.weihanbo.group_project.view.history.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.weihanbo.group_project.R;
import com.example.weihanbo.group_project.utils.IntentUtils;
import com.example.weihanbo.group_project.utils.Tools;
import com.example.weihanbo.group_project.view.history.model.HistoryModel;
import com.example.weihanbo.group_project.view.history.model.PhotoModel;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by weihanbo on 2017/11/26.
 */

public class HistoryAdapter   extends BaseAdapter {

    private Activity activity;
    private Context context;
    private List<HistoryModel> list;
    private LayoutInflater inflater = null;
    private IntentUtils intentUtils;
    private Tools tools;

    public HistoryAdapter(Activity activity, Context context,List<HistoryModel> list){
        this.activity = activity;
        this.context = context;
        this.list = list;
        this.intentUtils = new IntentUtils(activity,context);
        this.tools = new Tools(activity,context);
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HistoryAdapter.ViewHolder holder = null;
        if(convertView == null){
            holder = new HistoryAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.item_history,null);
            holder.tv = (TextView)convertView.findViewById(R.id.date_tv);
            holder.gridview = (GridView)convertView.findViewById(R.id.gridview);

            convertView.setTag(holder);
        }else{
            holder = (HistoryAdapter.ViewHolder) convertView.getTag();
        }
        holder.tv.setText(list.get(position).getDate());
        List<PhotoModel> photoList = list.get(position).getPhotoList();
        PhotoAdapter adapter = new PhotoAdapter(activity,context,photoList);
        holder.gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        int listViewHeight = 0;
        int adaptCount = adapter.getCount();
        for(int i=0;i<adaptCount;i++){
            View temp = adapter.getView(i,null,holder.gridview);
            temp.measure(0,0);
            listViewHeight += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = holder.gridview.getLayoutParams();
        layoutParams.width = tools.getScreenWidth();
        layoutParams.height = listViewHeight/4;
        holder.gridview.setLayoutParams(layoutParams);
        return convertView;
    }
    public class ViewHolder{
        public GridView gridview;
        public TextView tv;
    }
}


