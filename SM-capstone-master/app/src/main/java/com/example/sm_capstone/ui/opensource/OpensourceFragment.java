package com.example.sm_capstone.ui.opensource;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.sm_capstone.R;

public class OpensourceFragment extends Fragment {

    private SendViewModel sendViewModel;
    private WebView web;
    private WebSettings websetting;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_opensource, container, false);

        web=root.findViewById(R.id.webView);
        web.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        websetting = web.getSettings(); //세부 세팅 등록
        websetting.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        websetting.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        websetting.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        websetting.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        websetting.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        websetting.setSupportZoom(true); // 화면 줌 허용 여부
        websetting.setBuiltInZoomControls(true); // 화면 확대 축소 허용 여부
        websetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        websetting.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        websetting.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        web.loadUrl("file:///android_asset/opensource.html");


        return root;
    }
}