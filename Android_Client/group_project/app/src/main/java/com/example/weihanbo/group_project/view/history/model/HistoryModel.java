package com.example.weihanbo.group_project.view.history.model;

import java.util.List;

/**
 * Created by weihanbo on 2017/11/26.
 */

public class HistoryModel {
    private List<PhotoModel> photoList;
    private String date;
    public HistoryModel(List<PhotoModel> photoList,String date){
        this.photoList = photoList;
        this.date = date;
    }
    public List<PhotoModel> getPhotoList(){
        return this.photoList;
    }
    public String getDate(){
        return this.date;
    }
}
