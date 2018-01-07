package com.example.weihanbo.group_project.view.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.weihanbo.group_project.R;
import com.example.weihanbo.group_project.model.Envi;
import com.example.weihanbo.group_project.network.API;
import com.example.weihanbo.group_project.utils.IntentUtils;
import com.example.weihanbo.group_project.utils.Tools;
import com.example.weihanbo.group_project.view.home.view.imageSelectPopUpWindow;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.weihanbo.group_project.model.Envi.API_KEY;

/**
 * Created by weihanbo on 2017/11/23.
 */

public class HomeActivity  extends AppCompatActivity {

    private Activity activity;
    private Context context;
    private IntentUtils intentUtils;
    private Tools tools;
    private SharedPreferences sharedPreferences;
    private API api;
    private TextView uploadBt,historyBt,questionsBt;
    public imageSelectPopUpWindow imageSelectPopUpWindow;
    public Dialog progressDialog;

    private static String IMAGE_FILE_NAME = "productImage.jpg";
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int RESULT_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        activity = this;
        context = this;
        api = new API(activity,context);
        sharedPreferences = getSharedPreferences(Envi.shared,0);
        intentUtils = new IntentUtils(activity,context);
        tools = new Tools(activity,context);
        progressDialog = new Dialog(context, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.loading_dialog);
        progressDialog.setCancelable(true);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        msg.setText("Waiting for Server Detecting");
        initView();
        setListener();
    }
    private void initView(){
        uploadBt = (TextView)findViewById(R.id.upload_bt);
        historyBt = (TextView)findViewById(R.id.history_bt);
        questionsBt = (TextView)findViewById(R.id.question_bt);

        imageSelectPopUpWindow = new imageSelectPopUpWindow(context,selectImageOnClick);
    }
    private void setListener(){
        uploadBt.setOnClickListener(btnListener);
        historyBt.setOnClickListener(btnListener);
        questionsBt.setOnClickListener(btnListener);
    }
    private View.OnClickListener btnListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.upload_bt:
                    imageSelectPopUpWindow.showAtLocation(activity.findViewById(R.id.linear), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;
                case R.id.history_bt:
                    intentUtils.startHistoryActivity();
                    break;
                case R.id.question_bt:

                    break;
                default:
                    break;
            }
        }
    };
    private View.OnClickListener selectImageOnClick = new View.OnClickListener(){

        public void onClick(View v) {
            Tools t;
            Intent intent;
            imageSelectPopUpWindow.dismiss();
            switch (v.getId()) {
                case R.id.refund_ln1://拍照
                    IMAGE_FILE_NAME = tools.getMd5()+".jpg";
                    Log.e("IMAGE_FILE_NAME:", IMAGE_FILE_NAME.toString());
                    Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 判断存储卡是否可以用，可用进行存储
                    if (tools.hasSdcard()) {
                        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME)));
                    }
                    startActivityForResult(intentFromCapture,CAMERA_REQUEST_CODE);
                    break;
                case R.id.refund_ln2://从相册选择照片
                    Intent intentFromGallery = new Intent();
                    intentFromGallery.setType("image/*"); // 设置文件类型
                    intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intentFromGallery,IMAGE_REQUEST_CODE);
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    if (tools.hasSdcard()) {
                        File tempFile = new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
                        Bitmap b = tools.decodeSampledBitmapFromFd(tempFile.toString(),1920,1080);
                        b = Bitmap.createScaledBitmap(b, 100, 100, true);
                        getPredict(b);
                        /**
                         * has somethine need to add after finished project
                         */
                        //intentUtils.startResultActivity();


                        try {
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile));
                            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            bos.flush();
                            bos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        tools.showToast("Doesn't find SD card please find errors!");
                    }
                }

                break;
            case IMAGE_REQUEST_CODE:
                if(data!=null){
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    String imagePath = c.getString(columnIndex);
                    Bitmap b = BitmapFactory.decodeFile(imagePath);
                    b = Bitmap.createScaledBitmap(b, 100, 100, true);
                    getPredict(b);
                    //tools.startPhotoZoom(data.getData());
                }
                break;
            case RESULT_REQUEST_CODE:
                if (data != null) {
                    Drawable d = tools.getImageDrawable(data);
                    File tempFile = new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
                    tools.saveDrawable(IMAGE_FILE_NAME,d);
                    Bitmap b = tools.decodeSampledBitmapFromFd(tempFile.toString(),800,800);
                    b = Bitmap.createScaledBitmap(b, 100, 100, true);
                    getPredict(b);
                    /**
                     * has somethine need to add after finished project
                     */
                    //intentUtils.startResultActivity();

                }
                break;
            default:
                break;
        }
    }

    private void getPredict(Bitmap b) {
        String base64 = tools.bitmapToBase64(b);
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(Envi.HEADER,Envi.API_KEY);
        final String url = Envi.HOST;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("img",base64);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            entity.setContentType(new BasicHeader(Envi.HEADER,Envi.API_KEY));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("--------", "1");
        client.post(context,url,entity,"application/json",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();
                super.onSuccess(statusCode, headers, response);
                Log.e("--------", response.toString());
                String code = api.getCode(response);
                String message = api.getMessage(response);
                if(code.equals(Envi.SUCCESS)){
                    try {
                        JSONObject msg = new JSONObject(message);
                        String result = msg.getString("result");
                        //tools.showToast(result);
                        intentUtils.startResultActivity(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressDialog.dismiss();
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("--------", errorResponse.toString());
            }
        });
    }

}
