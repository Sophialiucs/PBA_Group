package com.example.weihanbo.group_project.model;

/**
 * Created by weihanbo on 2017/11/23.
 */

public class Envi {
    public static final String shared = "shared";

    public static final String TEST_URL = "http://g.hiphotos.baidu.com/image/pic/item/b3119313b07eca80131de3e6932397dda1448393.jpg";

    public static final String HOST = "https://cube-4ace8561-b15a-4b31-b82f-188ec5855e12.api.sherlockml.io/predict";

    public static final String API_KEY = "4i9XsmuNrhWez2sFs49n9RLdd5qHKu7v9BhpicXGls5nlobcXX";

    public static final String HEADER = "SherlockML-UserAPI-Key";

    public static final String SUCCESS = "200";

    public static String getImg(String img){
        return "img="+img;
    }
}
