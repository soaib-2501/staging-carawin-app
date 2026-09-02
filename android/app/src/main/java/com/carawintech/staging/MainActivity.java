package com.carawintech.staging;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPullToRefresh();
        setupBackPressed();
    }

    private void setupBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                WebView webView = bridge.getWebView();

                // Pehle webview ke apne history mein peeche jao
                if (webView.canGoBack()) {
                    webView.goBack();
                } else if (doubleBackToExitPressedOnce) {
                    // Webview history khatam — ab double-back-to-exit
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(MainActivity.this, R.string.back_to_exit, Toast.LENGTH_SHORT).show();

                    new Handler(Looper.getMainLooper()).postDelayed(
                            () -> doubleBackToExitPressedOnce = false, 2000);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void setupPullToRefresh() {
        WebView webView = this.bridge.getWebView();
        ViewGroup parent = (ViewGroup) webView.getParent();
        int index = parent.indexOfChild(webView);

        SwipeRefreshLayout swipeRefreshLayout = new SwipeRefreshLayout(this);
        parent.removeView(webView);
        swipeRefreshLayout.addView(webView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        parent.addView(swipeRefreshLayout, index, webView.getLayoutParams());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            webView.reload();
            new Handler(Looper.getMainLooper()).postDelayed(
                    () -> swipeRefreshLayout.setRefreshing(false), 1500);
        });
    }


}