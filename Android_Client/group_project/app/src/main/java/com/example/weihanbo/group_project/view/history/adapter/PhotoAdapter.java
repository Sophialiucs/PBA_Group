package com.example.weihanbo.group_project.view.history.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.weihanbo.group_project.R;
import com.example.weihanbo.group_project.utils.IntentUtils;
import com.example.weihanbo.group_project.utils.Tools;
import com.example.weihanbo.group_project.view.history.model.PhotoModel;
import com.facebook.drawee.view.SimpleDraweeView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by weihanbo on 2017/11/26.
 */

public class PhotoAdapter  extends BaseAdapter {

    private Activity activity;
    private Context context;
    private List<PhotoModel> list;
    private LayoutInflater inflater = null;
    private IntentUtils intentUtils;
    private Tools tools;

    public PhotoAdapter(Activity activity, Context context,List<PhotoModel> list){
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
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_photo,null);
            holder.tv = (TextView)convertView.findViewById(R.id.item_tv);
            holder.iv = (SimpleDraweeView)convertView.findViewById(R.id.item_img);
            ViewGroup.LayoutParams para;
            para = holder.iv.getLayoutParams();
            para.width = tools.getScreenWidth()/4-2;
            para.height = tools.getScreenWidth()/4-2;
            holder.iv.setLayoutParams(para);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(list.get(position).getTitle());
        Uri uri = Uri.parse(list.get(position).getUrl());
        holder.iv.setImageURI(uri);
        return convertView;
    }
    public class ViewHolder{
        public SimpleDraweeView iv;
        public TextView tv;
    }
}

