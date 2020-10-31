package com.yangguangyulu.sunoleducation.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.base.BaseMvpActivity;
import com.yangguangyulu.sunoleducation.model.EducationInfo;
import com.yangguangyulu.sunoleducation.presenter.impl.VideoListPresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.IVideoListView;
import com.yangguangyulu.sunoleducation.util.AlertDialogUtil;
import com.yangguangyulu.sunoleducation.util.Strings;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ThirdWebActivity extends BaseMvpActivity<VideoListPresenter> implements IVideoListView {
    @BindView(R.id.right_img)
    ImageView rightImg;
    @BindView(R.id.base_title_layout)
    RelativeLayout baseTitleLayout;
    @BindView(R.id.btn_back)
    TextView btnBack;
    @BindView(R.id.txt_Title)
    TextView txtTitle;

    private WebView webView;
    private String url;
    private String data = "";
    private String title = "";

    private EducationInfo educationInfo = null;

    @Override
    public VideoListPresenter initPresenter() {
        return new VideoListPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_web_activity);
        ButterKnife.bind(this);
        url = getIntent().getStringExtra("url");
        data = getIntent().getStringExtra("content");
        title = getIntent().getStringExtra("title");

        if (getIntent().hasExtra("eduInfo")) {
            educationInfo = getIntent().getParcelableExtra("eduInfo");
        }
        if (educationInfo == null) {
            onBackPressed();
        }
        webView = findViewById(R.id.webPage);
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTitle(title);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initData() {
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true); // 设置WebView属性，能够执行Javascript脚本
        webSettings.setBuiltInZoomControls(true); //必须要加，不然缩放不起作用
        webSettings.setSupportZoom(true); //支持缩放
        webSettings.setDisplayZoomControls(false);  //不显示webview缩放按钮
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.addJavascriptInterface(new JSHook(), "android");

        //解决请求链接是https，图片路径是http，图片加载不出来的问题
        //由于5.0是默认不支持mixed content的，即不支持同时加载https和http混合模式
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (!Strings.isEmptyOrNull(url)) {
            webView.loadUrl(url);
        } else {
            webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
        }
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != webView) {
            webView.clearHistory();
            webView.clearFormData();
            webView.clearCache(true);
        }
    }

    class MyWebChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog alert = AlertDialogUtil.alert(ThirdWebActivity.this, message, () -> result.confirm());
            alert.setOnCancelListener((dialog) -> result.confirm());
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress < 80) {
                startLoading(false);
            } else {
                stopLoading();
            }

            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果当前webView可以返回上一页
        //        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) { // webVIew返回上一页
        //            webView.goBack();
        //            return true;
        //        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onGetVideoList(List<EducationInfo> educationInfos, int totalCount) {
    }


    @Override
    public void onCommitAnswer(boolean success) {
        finish();
    }

    public class JSHook {
        @JavascriptInterface
        public void postCommit(String params, int educationId) {
            if (educationInfo != null) {
                mPresenter.commitStudyTimeByAnswer(educationInfo, params);
            }
        }

    }
}
