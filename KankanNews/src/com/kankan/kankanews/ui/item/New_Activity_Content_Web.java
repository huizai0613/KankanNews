package com.kankan.kankanews.ui.item;

import com.kankan.kankanews.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.VideoView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.BaseVideoActivity;
import com.kankan.kankanews.bean.New_News;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.sina.AccessTokenKeeper;
import com.kankan.kankanews.sina.Constants;
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.view.CustomShareBoard;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.ProgressWebView;
import com.kankan.kankanews.ui.view.VerticalBar;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;
import com.umeng.socialize.sso.UMSsoHandler;

public class New_Activity_Content_Web extends BaseVideoActivity implements
		OnClickListener {

	// 分享类
	private ShareUtil shareUtil;

	public static final int BODYPROCOTOL = 0;
	public static final int DISCLAIMER = 1;
	private ProgressWebView webView;

	private New_News new_news;
	private String mid;
	private String type;
	private String titleurl;
	private String newstime;
	private String titlePic;
	private String sharedPic;
	private String titlelist;
	private String title;

	private View xCustomView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_content_web);
	}

	@Override
	protected void initView() {
		webView = (ProgressWebView) findViewById(R.id.webView);
		video_view = (FrameLayout) findViewById(R.id.video_view);
		WebSettings webSettings = webView.getSettings();

		// 开启javascript设置
		webSettings.setJavaScriptEnabled(true);
		// 设置可以使用localStorage
		webSettings.setDomStorageEnabled(true);
		// 应用可以有数据库
		webSettings.setDatabaseEnabled(true);
		webSettings.setAppCacheMaxSize(8 * 1024 * 1024); // 缓存最多可以有8M
		webSettings.setAllowFileAccess(true); // 可以读取文件缓存(manifest生效)
		String dbPath = this.getApplicationContext()
				.getDir("database", Context.MODE_PRIVATE).getPath();
		webSettings.setDatabasePath(dbPath);
		// 应用可以有缓存
		webSettings.setAppCacheEnabled(true);
		String appCaceDir = this.getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		webSettings.setAppCachePath(appCaceDir);
		// 适应屏幕
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		if (CommonUtils.isNetworkAvailable(mContext)) {
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		} else {
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
		webView.setWebViewClient(new MyWebViewClient());
		xwebchromeclient = new xWebChromeClient();
		webView.setWebChromeClient(xwebchromeclient);

		// 初始化头部
		initTitle_Right_Left_bar("看看新闻", "", "", "#ffffff",
				R.drawable.new_ic_more, R.drawable.new_ic_back, "#000000",
				"#000000");
		// 头部的左右点击事件
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */

		UMSsoHandler ssoHandler = shareUtil.getmController().getConfig()
				.getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.com_title_bar_left_bt:
			if (webView.canGoBack()) {
				webView.goBack();
				break;
			}
			webFinish();
			break;
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			// 一键分享
			CustomShareBoard shareBoard = new CustomShareBoard(this, shareUtil,
					this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;

		}
	}

	@Override
	protected void initData() {
		// 获取上个页面传来的数据
		Intent intent = getIntent();
		mid = intent.getStringExtra("mid");
		type = intent.getStringExtra("type");
		titleurl = intent.getStringExtra("titleurl");
		titlePic = intent.getStringExtra("titlePic");
		sharedPic = intent.getStringExtra("sharedPic");
		newstime = intent.getStringExtra("newstime");
		title = intent.getStringExtra("title");

		titlelist = intent.getStringExtra("titlelist");
		// 存储数据
		new_news = new New_News();
		new_news.setId(mid);
		new_news.setType(type);
		new_news.setTitleurl(titleurl);
		new_news.setNewstime(newstime);
		new_news.setTitlepic(titlePic);
		new_news.setSharedPic(sharedPic);
		new_news.setTitlelist(titlelist);
		new_news.setLooktime(Long.toString(TimeUtil.now()));

		// 提交点击
		ItnetUtils.getInstance(mContext).addNewNewsClickData("id=" + mid);
		ItnetUtils.getInstance(mContext).getAnalyse(this, "text",
				new_news.getTitlelist(), new_news.getTitleurl());
		// 更新数据
		try {
			dbUtils.saveOrUpdate(new_news);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 初始化shareutil类
		shareUtil = new ShareUtil(new_news, mContext);

		webView.loadUrl(titleurl + "?fromkkApp=1");

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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		switch (keyCode) {
		// 音量减小
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			Log.e("KEYCODE_VOLUME_DOWN", "KEYCODE_VOLUME_DOWN");
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER,
					AudioManager.FX_FOCUS_NAVIGATION_UP);
			return true;

			// 音量增大
		case KeyEvent.KEYCODE_VOLUME_UP:
			Log.e("KEYCODE_VOLUME_UP", "KEYCODE_VOLUME_UP");
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE,
					AudioManager.FX_FOCUS_NAVIGATION_UP);
			return true;

		case KeyEvent.KEYCODE_BACK:
			if (webView.canGoBack()) {
				webView.goBack();
				return true;
			}
			if (inCustomView()) {
				hideCustomView();
				return true;
			} else {
				// 退出时加载空网址防止退出时还在播放视频
				webFinish();

			}
		}
		return true;
	}

	private void webFinish() {
		webView.loadUrl("about:blank");
		finish();
	}

	/**
	 * 判断是否是全屏
	 * 
	 * @return
	 */
	public boolean inCustomView() {
		return (xCustomView != null);
	}

	/**
	 * 全屏时按返加键执行退出全屏方法
	 */
	public void hideCustomView() {
		xwebchromeclient.onHideCustomView();
	}

	private WebChromeClient.CustomViewCallback xCustomViewCallback;

	private xWebChromeClient xwebchromeclient;

	private FrameLayout video_view;

	private Boolean islandport = true;// true表示此时是竖屏，false表示此时横屏。

	private ImageView content_video_temp_image;

	private RelativeLayout video_controller_bar;

	private RelativeLayout video_controller_full;

	private View video_controller_volume_box;

	private LinearLayout video_controller_top_bar;

	private ImageView video_controller_back;

	private ImageView video_controller_full_play;

	private ImageView video_controller_volume;

	private MyTextView video_controller_title;

	private MyTextView video_controller_totalAndCurTime;

	private SeekBar video_controller_seek_full;

	private VerticalBar video_controller_volume_seek;

	private AudioManager mAM;

	public class xWebChromeClient extends WebChromeClient {
		private Bitmap xdefaltvideo;
		private View xprogressvideo;

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				webView.getProgressbar().setVisibility(View.GONE);
			} else {
				if (webView.getProgressbar().getVisibility() == View.GONE)
					webView.getProgressbar().setVisibility(View.VISIBLE);
				webView.getProgressbar().setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		webView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.onResume();
	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {

			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			super.onPageFinished(view, url);
		}

	}

	/**
	 * 当横竖屏切换时会调用该方法
	 * 
	 * @author
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i("testwebview", "=====<<<  onConfigurationChanged  >>>=====");
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.i("webview", "   现在是横屏1");
			islandport = false;
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.i("webview", "   现在是竖屏1");
			islandport = true;
		}
	}

	@Override
	public void refresh() {
		webView.reload();
		// webView.loadUrl(titleurl + "?fromkkApp=1");
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			webView.loadUrl("about:blank");
			super.onBackPressed();
		}
	}

}
