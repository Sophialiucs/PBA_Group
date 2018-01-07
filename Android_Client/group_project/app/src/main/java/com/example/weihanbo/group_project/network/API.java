package com.example.weihanbo.group_project.network;

import android.app.Activity;
import android.content.Context;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.AccessibleObject;

/**
 * Created by weihanbo on 2017/11/23.
 */

public class API {
    private Activity activity;
    private Context context;
    public API(Activity activity,Context context){
        this.activity = activity;
        this.context = context;
    }


    /**
     * Get Image Prediction result API
     * @param base64 image after base64 encoding
     * @return post request parameters
     */
    public RequestParams GET_PREDICT(String base64){
        RequestParams params = new RequestParams();
        try {
            params.put("img",base64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public String getCode(JSONObject result){
        String code = "";
        try {
            code = result.getString("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }

    public String getMessage(JSONObject result){
        String message = "";
        try {
            message = result.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

}
