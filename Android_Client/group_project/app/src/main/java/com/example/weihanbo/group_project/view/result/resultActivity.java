package com.example.weihanbo.group_project.view.result;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.weihanbo.group_project.R;
import com.example.weihanbo.group_project.model.Envi;
import com.example.weihanbo.group_project.network.API;
import com.example.weihanbo.group_project.utils.IntentUtils;
import com.example.weihanbo.group_project.utils.Tools;
import com.example.weihanbo.group_project.view.SetTop;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by weihanbo on 2017/11/23.
 */

public class resultActivity  extends AppCompatActivity {

    private Activity activity;
    private Context context;
    private IntentUtils intentUtils;
    private Tools tools;
    private SharedPreferences sharedPreferences;
    private API api;
    private SetTop s;

    private TextView locationTv,contineTv;
    private WebView webView;

    private String result = "No result!";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.layout_result);
        Intent intent = getIntent();
        result = intent.getStringExtra("result");
        activity = this;
        context = this;
        api = new API(activity,context);
        sharedPreferences = getSharedPreferences(Envi.shared,0);
        intentUtils = new IntentUtils(activity,context);
        tools = new Tools(activity,context);
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"=5a2df3cd");

        initView();
        setListener();
    }
    private void initView(){
        s = new SetTop(activity,context);
        s.cancelVisible(true);
        s.titleVisible(true);
        s.addVisible(false);
        s.setTitle("Location result");
        s.setCancelListener(btnListener);

        SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer(context, null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "20");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
        mTts.startSpeaking(result, mSynListener);

        locationTv = (TextView)findViewById(R.id.location_tv);
        locationTv.setText(result);
        contineTv = (TextView)findViewById(R.id.continue_bt);
        webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("https://en.wikipedia.org/wiki/"+result);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
    private void setListener(){
        contineTv.setOnClickListener(btnListener);
    }
    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.continue_bt:
                    finish();
                    break;
                case R.id.cancel_ln:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
    private SynthesizerListener mSynListener = new SynthesizerListener(){
        public void onCompleted(SpeechError error) {}
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}
        public void onSpeakBegin() {}
        public void onSpeakPaused() {}
        public void onSpeakProgress(int percent, int beginPos, int endPos) {}
        public void onSpeakResumed() {}
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {}

    };
}