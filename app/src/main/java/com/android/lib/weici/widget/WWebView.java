package com.android.lib.weici.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

/**
 * Created by weici on 2020/12/2.
 * Describe:
 */
public class WWebView extends WebView {
    public WWebView(Context context) {
        super(context);
        setView();
    }

    public WWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setView();
    }

    public WWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setView();
    }

    private void setView(){
        removeJavascriptInterface("searchBoxJavaBridge");
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");

        setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && (url.contains("weici") || url.contains("victorstudy.com"))) {
                    return super.shouldInterceptRequest(view, url);
                }
                return new WebResourceResponse(null, null, null);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String string = request.getUrl().getHost();
                if (!TextUtils.isEmpty(string) && (string.contains("weici") || string.contains("victorstudy.com"))) {
                    return super.shouldInterceptRequest(view, request);
                }
                return new WebResourceResponse(null, null, null);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN_MR1){
            //addJavascriptInterface(new JavaScriptInterface(), "weicinativejs");
            JavaScriptInterface();
        }

        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                WWebView.this.onProgressChanged(newProgress);
            }
        });

        WebSettings webSetting = getSettings();
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSetting.setBlockNetworkImage(false);//解决图片不显示
        webSetting.setAllowFileAccess(false);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(false);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(false);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setSavePassword(false);
    }

    protected void onProgressChanged(int progress) {
    }
    protected void JavaScriptInterface() {
    }
}
