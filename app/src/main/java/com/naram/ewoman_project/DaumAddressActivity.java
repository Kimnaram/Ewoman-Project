package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DaumAddressActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "IP ADDRESS";

    public WebView wv_search_address;

    public TextView tv_address_view;

    private ProgressBar progress;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_address);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        tv_address_view = findViewById(R.id.tv_address_view);
        progress = findViewById(R.id.web_progress);

        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();
    }

    public void init_webView() {
        // WebView 설정
        wv_search_address = (WebView) findViewById(R.id.wv_search_address);
        // JavaScript 허용
//        wv_search_address.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        WebSettings webSettings = wv_search_address.getSettings();

//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setLoadsImagesAutomatically(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setSupportMultipleWindows(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setSafeBrowsingEnabled(false);
//        webSettings.setGeolocationEnabled(true);
//        webSettings.setDomStorageEnabled(true);

        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
//        wv_search_address.addJavascriptInterface(new AndroidBridge(), "TestApp");
        // web client 를 chrome 으로 설정
        wv_search_address.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // SSL 에러가 발생해도 계속 진행
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }

            // 페이지 로딩 시작시 호출
            @Override
            public void onPageStarted(WebView view,String url , Bitmap favicon){
                progress.setVisibility(View.VISIBLE);

            }

            //페이지 로딩 종료시 호출
            public void onPageFinished(WebView view,String Url){
                progress.setVisibility(View.GONE);

            }

        });

        //ssl 인증이 없는 경우 해결을 위한 부분
        wv_search_address.setWebChromeClient(new WebChromeClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        wv_search_address.setWebViewClient(new SslWebViewConnect());
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        wv_search_address.addJavascriptInterface(new AndroidBridge(), "TestApp");
        wv_search_address.setWebChromeClient(new WebChromeClient());
        webSettings.setDomStorageEnabled(true);

        // webview url load. php 파일 주소
        wv_search_address.loadUrl("https://" + IP_ADDRESS + "/ewoman-php/daum_address.php");

    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tv_address_view.setText(String.format("(%s) %s %s", arg1, arg2, arg3));

                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    init_webView();
                }
            });
        }
    }

    public class SslWebViewConnect extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // SSL 에러가 발생해도 계속 진행!
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl("http://" + IP_ADDRESS + "/ewoman-php/daum_address.php");
            return true;// 응용프로그램이 직접 url를 처리함
        }
    }

}
