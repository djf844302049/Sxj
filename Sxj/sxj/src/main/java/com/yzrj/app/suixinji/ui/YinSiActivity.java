package com.yzrj.app.suixinji.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yzrj.app.suixinji.R;

public class YinSiActivity extends AppCompatActivity {

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yin_si_layout);
        webView = findViewById(R.id.webView);
        webView.getSettings().getJavaScriptEnabled();//支持javaScript脚本
        webView.setWebViewClient(new WebViewClient());//设置用自己app作为浏览器
        webView.loadUrl("http://www.baidu.com");
    }
}
