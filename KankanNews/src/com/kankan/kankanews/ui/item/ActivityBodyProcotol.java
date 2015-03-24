package com.kankan.kankanews.ui.item;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankanews.kankanxinwen.R;

public class ActivityBodyProcotol extends BaseActivity {

	public static final int BODYPROCOTOL = 0;
	public static final int DISCLAIMER = 1;
	private WebView webView;
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_set_body_procotol);

	}

	@Override
	protected void initView() {
		webView = (WebView) findViewById(R.id.webView);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.setWebViewClient(new MyWebViewClient());
		type = getIntent().getIntExtra("TYPE", 0);

		switch (type) {
		case BODYPROCOTOL:
			initTitle_Left_bar("用户协议", "", "#000000", R.drawable.icon_black_big);
			break;
		case DISCLAIMER:
			initTitle_Left_bar("免责声明", "", "#000000", R.drawable.icon_black_big);
			break;
		}

		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AnimFinsh();
			}
		});

	}

	@Override
	protected void initData() {

		switch (type) {
		case BODYPROCOTOL:
			webView.loadUrl("http://sina.cn/");
			break;
		case DISCLAIMER:
			webView.loadUrl("http://m.baidu.com/");
			break;
		}

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AnimFinsh();
	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {

			loading_dialog.show();

			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			loading_dialog.dismiss();

			super.onPageFinished(view, url);
		}

	}
}
