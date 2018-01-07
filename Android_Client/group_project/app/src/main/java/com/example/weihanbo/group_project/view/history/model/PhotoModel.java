package com.example.weihanbo.group_project.view.history.model;

/**
 * Created by weihanbo on 2017/11/26.
 */

public class PhotoModel {
    private String url;
    private String title;
    public PhotoModel(String url,String title){
        this.url = url;
        this.title = title;
    }
    public String getUrl(){
        return this.url;
    }
    public String getTitle(){
        return this.title;
    }
}
