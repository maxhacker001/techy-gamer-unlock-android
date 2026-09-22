package com.techygamer.unlock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public final class MainActivity extends Activity {
    private static final String START_URL =
            "https://techy-gamer-unlock-yetq2x.v2.appdeploy.ai/";

    private WebView webView;
    private ProgressBar progress;
    private TextView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.rgb(245, 248, 255));
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(245, 248, 255));
        setContentView(root);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(245, 248, 255));
        root.addView(webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        progress = new ProgressBar(this);
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                dp(28),
                dp(28)
        );
        progressParams.gravity = Gravity.CENTER;
        root.addView(progress, progressParams);

        errorView = new TextView(this);
        errorView.setText("Unable to connect. Tap to retry.");
        errorView.setTextColor(Color.rgb(39, 52, 76));
        errorView.setTextSize(14);
        errorView.setGravity(Gravity.CENTER);
        errorView.setPadding(dp(24), dp(20), dp(24), dp(20));
        errorView.setVisibility(View.GONE);
        errorView.setOnClickListener(v -> loadStartUrl());

        FrameLayout.LayoutParams errorParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        errorParams.gravity = Gravity.CENTER;
        root.addView(errorView, errorParams);

        configureWebView();
        loadStartUrl();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setUserAgentString(
                settings.getUserAgentString() + " TechyGamerUnlock/1.0"
        );

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress.setVisibility(newProgress >= 95 ? View.GONE : View.VISIBLE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request
            ) {
                return handleUrl(request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(url);
            }

            @Override
            public void onReceivedError(
                    WebView view,
                    WebResourceRequest request,
                    WebResourceError error
            ) {
                if (request.isForMainFrame()) {
                    showError();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                errorView.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
            }
        });

        webView.setDownloadListener((url, userAgent, contentDisposition,
                                     mimeType, contentLength) -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (ActivityNotFoundException ignored) {
            }
        });
    }

    private boolean handleUrl(String url) {
        Uri uri = Uri.parse(url);
        String host = uri.getHost();

        if (host == null) {
            return false;
        }

        boolean sameApp = host.equals(
                "techy-gamer-unlock-yetq2x.v2.appdeploy.ai"
        );

        boolean google = host.equals("accounts.google.com")
                || host.equals("oauth2.googleapis.com");

        boolean social = host.contains("youtube.com")
                || host.contains("youtu.be")
                || host.equals("t.me")
                || host.equals("telegram.me")
                || host.contains("instagram.com")
                || host.contains("tiktok.com")
                || host.contains("facebook.com")
                || host.contains("x.com")
                || host.contains("discord.com")
                || host.contains("whatsapp.com");

        if (sameApp || google || social) {
            return false;
        }

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        } catch (ActivityNotFoundException ignored) {
            return false;
        }
    }

    private void loadStartUrl() {
        errorView.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        webView.loadUrl(START_URL);
    }

    private void showError() {
        progress.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private int dp(int value) {
        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
    }
}
