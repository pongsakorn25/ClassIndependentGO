package com.example.skyeye.ClassIndependentGO;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by TPK on 21-Mar-17.
 */

public class PDFViewerLesson extends AppCompatActivity {
    private String fileName, linkPDF;
    WebView webView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);


        fileName = getIntent().getStringExtra("paste");
//        Toast.makeText(this, fileName, Toast.LENGTH_LONG).show();

        linkPDF = "https://classindependent.000webhostapp.com/" + fileName;
        webView = (WebView) findViewById(R.id.pdf_webview);
        webView.setVisibility(WebView.VISIBLE);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + linkPDF);

        webView.getSettings().setBuiltInZoomControls(true);
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new MyWebViewClient());

    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressDialog = new ProgressDialog(PDFViewerLesson.this);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
        }
    }

}
