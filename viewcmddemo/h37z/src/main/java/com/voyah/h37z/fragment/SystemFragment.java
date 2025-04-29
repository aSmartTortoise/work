package com.voyah.h37z.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.voyah.h37z.R;
import com.voyah.h37z.databinding.FragmentSystemBinding;

public class SystemFragment extends Fragment {

    FragmentSystemBinding binding;
    private static final String mLink = "https://mapauto.baidu.com/static/privacy.html";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_system, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.backImg.setOnClickListener(v -> Toast.makeText(requireContext(), "点击了返回", Toast.LENGTH_SHORT).show());
        WebView mWebView = binding.aboutWeb;
        mWebView.setBackgroundColor(0);
        mWebView.getBackground().setAlpha(0);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setAppCacheEnabled(false);

        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.loadUrl(mLink);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String title = view.getTitle();
                if (!TextUtils.isEmpty(title) && !title.equals("about:blank")) {
                    binding.aboutContentTitle.setText(title);
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request,
                                            WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                int statusCode = errorResponse.getStatusCode();
                Log.d("xyj", "onReceivedHttpError statusCode = " + statusCode);
                if (404 == statusCode || 500 == statusCode) {
                    view.loadUrl("about:blank");
                    Toast.makeText(requireContext(), "onReceivedHttpError 出错了", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request,
                                        WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d("xyj", "onReceivedError");
                if (request.isForMainFrame()) {
                    view.loadUrl("about:blank");
                    Toast.makeText(requireContext(), "onReceivedError 出错了", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
