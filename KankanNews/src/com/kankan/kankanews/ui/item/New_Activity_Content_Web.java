package com.kankan.kankanews.ui.item;

import io.vov.vitamio.utils.StringUtils;

import java.io.ByteArrayOutputStream;

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
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.sina.AccessTokenKeeper;
import com.kankan.kankanews.sina.Constants;
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
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;

public class New_Activity_Content_Web extends BaseVideoActivity implements
		OnClickListener, IWeiboHandler.Response {

	/** 微博微博分享接口实例 */
	private IWeiboShareAPI mWeiboShareAPI = null;

	// 分享类
	private ShareUtil shareUtil;

	public static final int BODYPROCOTOL = 0;
	public static final int DISCLAIMER = 1;
	private ProgressWebView webView;
	// private ProgressBar loading;

	private New_News new_news;
	private String mid;
	private String type;
	private String titleurl;
	private String newstime;
	private String titlepiclist;
	private String titlelist;

	private View xCustomView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_content_web);
		// mAM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		// 创建微博分享接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
		// 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
		// 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
		// NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
		mWeiboShareAPI.registerApp();
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
		// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
		// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
		// 失败返回 false，不调用上述回调
		if (savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
		}
	}

	@Override
	protected void initView() {
		// loading = (ProgressBar) findViewById(R.id.loading);
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
		// TODO
		// String dbPath = CommonUtils.getImageCachePath(mContext).getPath();
		String dbPath = this.getApplicationContext()
				.getDir("database", Context.MODE_PRIVATE).getPath();
		webSettings.setDatabasePath(dbPath);
		// 应用可以有缓存
		webSettings.setAppCacheEnabled(true);
		String appCaceDir = this.getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		webSettings.setAppCachePath(appCaceDir);
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

		// initVideoFull();

	}

	//
	// private void initVideoFull() {
	// content_video_temp_image = (ImageView)
	// findViewById(R.id.content_video_temp_image);
	// video_controller_bar = (RelativeLayout)
	// findViewById(R.id.video_controller_bar);
	// video_controller_full = (RelativeLayout)
	// findViewById(R.id.video_controller_full);
	// video_controller_volume_box =
	// findViewById(R.id.video_controller_volume_box);
	// video_controller_top_bar = (LinearLayout)
	// findViewById(R.id.video_controller_top_bar);
	// video_controller_back = (ImageView)
	// findViewById(R.id.video_controller_back);
	// video_controller_full_play = (ImageView)
	// findViewById(R.id.video_controller_full_play);
	// video_controller_volume = (ImageView)
	// findViewById(R.id.video_controller_volume);
	// video_controller_title = (MyTextView)
	// findViewById(R.id.video_controller_title);
	// video_controller_totalAndCurTime = (MyTextView)
	// findViewById(R.id.video_controller_totalAndCurTime);
	// video_controller_seek_full = (SeekBar)
	// findViewById(R.id.video_controller_seek_full);
	// video_controller_volume_seek = (VerticalBar)
	// findViewById(R.id.video_controller_volume_seek);
	// }
	//
	// private boolean isShow;// 控制条是否显示
	// private boolean isShowVolume; // 音量
	// private static final int sDefaultTimeout = 5000;
	// private static final int FADE_OUT = 1;
	// private static final int SHOW_PROGRESS = 2;
	// private boolean mDragging;
	// private boolean mInstantSeeking = false;
	// private int mMaxVolume;
	// private int mVolume;
	// private long mDuration;
	//
	// private Handler mHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// long pos;
	// switch (msg.what) {
	// case FADE_OUT:
	// hide();
	// break;
	// case SHOW_PROGRESS:
	// pos = setProgress();
	// if (!mDragging && isShow) {
	// msg = obtainMessage(SHOW_PROGRESS);
	// sendMessageDelayed(msg, 1000 - (pos % 1000));
	// updatePausePlay();
	// }
	// break;
	// }
	// }
	// };
	//
	// public void show() {
	// show(sDefaultTimeout);
	// }
	//
	// public void show(int timeout) {
	// if (!isShow) {
	// setV(View.VISIBLE);
	// }
	// updatePausePlay();
	// mHandler.sendEmptyMessage(SHOW_PROGRESS);
	// if (timeout != 0) {
	// mHandler.removeMessages(FADE_OUT);
	// mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
	// timeout);
	// }
	// }
	//
	// private VerticalBar.OnSeekBarChangeListener mVSeekListener = new
	// VerticalBar.OnSeekBarChangeListener() {
	//
	// @Override
	// public void onProgressChanged(VerticalBar VerticalSeekBar,
	// int progress, boolean fromUser) {
	// int index = (int) (progress / 100.00 * mMaxVolume);
	// if (index > mMaxVolume)
	// index = mMaxVolume;
	// else if (index < 0)
	// index = 0;
	// // 变更声音
	// mAM.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
	// }
	//
	// @Override
	// public void onStartTrackingTouch(VerticalBar VerticalSeekBar) {
	//
	// mDragging = true;
	// show(3600000);
	// mHandler.removeMessages(SHOW_PROGRESS);
	// }
	//
	// @Override
	// public void onStopTrackingTouch(VerticalBar VerticalSeekBar) {
	// show(sDefaultTimeout);
	// mHandler.removeMessages(SHOW_PROGRESS);
	// mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
	// mDragging = false;
	// mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
	//
	// }
	// };
	//
	// private OnSeekBarChangeListener mSeekListener = new
	// OnSeekBarChangeListener() {
	// public void onStartTrackingTouch(SeekBar bar) {
	// mDragging = true;
	// show(3600000);
	// mHandler.removeMessages(SHOW_PROGRESS);
	// if (mInstantSeeking)
	// mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
	//
	// setRightFinsh(false);
	//
	// }
	//
	// public void onProgressChanged(SeekBar bar, int progress,
	// boolean fromuser) {
	// if (!fromuser)
	// return;
	//
	// long newposition = (mDuration * progress) / 1000;
	// String time = StringUtils.generateTime(newposition);
	// if (mInstantSeeking) {
	// video.seekTo((int) newposition);
	// }
	// video_controller_totalAndCurTime.setText(StringUtils
	// .generateTime(newposition)
	// + "/"
	// + StringUtils.generateTime(mDuration));
	//
	// }

	// public void onStopTrackingTouch(SeekBar bar) {
	// if (!mInstantSeeking) {
	// long seek = (mDuration * bar.getProgress()) / 1000;
	// video.seekTo((int) seek);
	// }
	// show(sDefaultTimeout);
	// mHandler.removeMessages(SHOW_PROGRESS);
	// mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
	// mDragging = false;
	// mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
	// if (isFullScrenn())
	// setRightFinsh(true);
	//
	// }
	// };
	//
	// private void updatePausePlay() {
	//
	// if (video_controller_full_play == null)
	// return;
	//
	// if (video.isPlaying())
	// video_controller_full_play
	// .setImageResource(R.drawable.icon_pause_small);
	// else
	// video_controller_full_play
	// .setImageResource(R.drawable.icon_play_big);
	//
	// }
	//
	// VideoView video;
	//
	// private long setProgress() {
	// if (video == null)
	// return 0;
	//
	// long position = video.getCurrentPosition();
	// long duration = video.getDuration();
	// if (video_controller_seek_full != null) {
	// if (duration > 0) {
	// long pos = 1000L * position / duration;
	// video_controller_seek_full.setProgress((int) pos);
	// }
	// }
	// // int percent = video.getBufferPercentage();
	// // video_controller_seek.setSecondaryProgress(percent * 10);
	// return duration;
	// }
	//
	// public void hide() {
	// if (isShow) {
	// mHandler.removeMessages(SHOW_PROGRESS);
	// setV(View.GONE);
	// video_controller_volume
	// .setBackgroundResource(R.drawable.volume_icon_up);
	// video_controller_volume_box.setVisibility(View.GONE);
	// setCanScrool(true);
	// isShowVolume = false;
	// }
	// }
	//
	// private void volume() {
	// mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
	// video_controller_volume_seek
	// .setProgress((int) (((mVolume * 1.00) / (mMaxVolume * 1.00)) * 100));
	//
	// if (isShowVolume) {
	// video_controller_volume_box.setVisibility(View.GONE);
	// isShowVolume = false;
	// video_controller_volume
	// .setBackgroundResource(R.drawable.volume_icon_up);
	// setCanScrool(true);
	// } else {
	// isShowVolume = true;
	// video_controller_volume_box.setVisibility(View.VISIBLE);
	// video_controller_volume
	// .setBackgroundResource(R.drawable.volume_button_touch);
	// setCanScrool(false);
	// }
	//
	// }
	//
	// public void setV(int visibility) {
	// video_controller_bar.setVisibility(visibility);
	// switch (visibility) {
	// case View.VISIBLE:
	// isShow = true;
	// break;
	// default:
	// isShow = false;
	// break;
	// }
	// updatePausePlay();
	// }
	//
	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.com_title_bar_left_bt:
			webFinish();
			break;
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			// 一键分享
			CustomShareBoard shareBoard = new CustomShareBoard(this, shareUtil);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;
		// case R.id.video_controller_bar:
		// hide();
		// break;
		// case R.id.video_controller_player:
		// if (video.isPlaying())
		// video.pause();
		// else {
		// if (content_video_temp_image != null) {
		// content_video_temp_image.setVisibility(View.GONE);
		// }
		// video.start();
		// // activity_Content.video_pb.setVisibility(View.GONE);
		// // activity_Content.small_video_pb.setVisibility(View.GONE);
		// }
		// updatePausePlay();
		// break;
		// case R.id.video_controller_full_play:
		// if (video.isPlaying())
		// video.pause();
		// else {
		// if (content_video_temp_image != null) {
		// content_video_temp_image.setVisibility(View.GONE);
		// }
		// video.start();
		// }
		// updatePausePlay();
		// break;
		//
		// case R.id.video_controller_volume:// 显示音量
		// volume();
		// break;
		// case R.id.video_controller_back:// 后退
		// fullScrenntoSamll();
		// break;
		// case R.id.video_controller_full_screen:// 全屏
		// samllScrenntoFull();
		// break;
		}
	}

	@Override
	protected void initData() {
		// 获取上个页面传来的数据
		Intent intent = getIntent();
		mid = intent.getStringExtra("mid");
		type = intent.getStringExtra("type");
		titleurl = intent.getStringExtra("titleurl");
		titlepiclist = intent.getStringExtra("titlepiclist");
		newstime = intent.getStringExtra("newstime");

		titlelist = intent.getStringExtra("titlelist");
		// 存储数据
		new_news = new New_News();
		new_news.setId(mid);
		new_news.setType(type);
		new_news.setTitleurl(titleurl);
		new_news.setNewstime(newstime);
		new_news.setTitlepiclist(titlepiclist);
		new_news.setTitlelist(titlelist);
		new_news.setLooktime(Long.toString(TimeUtil.now()));
		
		//提交点击
		ItnetUtils.getInstance(mContext).addNewNewsClickData("id="+mid);
		

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

	public void sendSingleMessage() {
		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
		// 创建媒体消息
		// weiboMultiMessage.mediaObject = getVideoObj();
		TextObject textObject = new TextObject();
		textObject.text = new_news.getTitlelist() + "-看看新闻 "
				+ new_news.getTitleurl() + " （分享自@看看新闻网） ";
		ImageObject imageObject = new ImageObject();
		imageObject.setImageObject(getThumbBitmap());
		weiboMultiMessage.textObject = textObject;
		weiboMultiMessage.imageObject = imageObject;
		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMultiMessage;

		AuthInfo authInfo = new AuthInfo(this, Constants.APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);
		Oauth2AccessToken accessToken = AccessTokenKeeper
				.readAccessToken(getApplicationContext());
		String token = "";
		if (accessToken != null) {
			token = accessToken.getToken();
		}
		mWeiboShareAPI.sendRequest(this, request, authInfo, token,
				new WeiboAuthListener() {

					@Override
					public void onWeiboException(WeiboException arg0) {
					}

					@Override
					public void onComplete(Bundle bundle) {
						Oauth2AccessToken newToken = Oauth2AccessToken
								.parseAccessToken(bundle);
						AccessTokenKeeper.writeAccessToken(
								getApplicationContext(), newToken);
					}

					@Override
					public void onCancel() {
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
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
	
	private void webFinish(){
		webView.loadUrl("about:blank");
		finish();
		onBackPressed();
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

		// @Override
		// // 播放网络视频时全屏会被调用的方法
		// public void onShowCustomView(View view,
		// WebChromeClient.CustomViewCallback callback) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// webView.setVisibility(View.GONE);
		// // 如果一个视图已经存在，那么立刻终止并新建一个
		// if (xCustomView != null) {
		// callback.onCustomViewHidden();
		// return;
		// }
		//
		// video = (VideoView) view;
		// video.setMediaController(null);
		// video_view.addView(video, 0);
		// xCustomView = view;
		// xCustomViewCallback = callback;
		// video_view.setVisibility(View.VISIBLE);
		// }
		//
		// @Override
		// // 视频播放退出全屏会被调用的
		// public void onHideCustomView() {
		// if (xCustomView == null)// 不是全屏播放状态
		// return;
		//
		// // Hide the custom view.
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// xCustomView.setVisibility(View.GONE);
		//
		// // Remove the custom view from its container.
		// video_view.removeView(xCustomView);
		// xCustomView = null;
		// video_view.setVisibility(View.GONE);
		// xCustomViewCallback.onCustomViewHidden();
		// //
		// webView.setVisibility(View.VISIBLE);
		// }

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

	/**
	 * 获取当前新闻的缩略图对应的 Bitmap。
	 */
	private Bitmap getThumbBitmap() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap decodeFile = BitmapFactory.decodeFile(CommonUtils
				.getImageCachePath(mContext)
				+ "/"
				+ CommonUtils.generate(new_news.getTitlepiclist()));
		
		if (decodeFile == null) {
			decodeFile = BitmapFactory.decodeFile(CommonUtils
					.getImageCachePath(mContext)
					+ "/"
					+ "big_"
					+ CommonUtils.generate(new_news.getTitlepiclist()));
		}
		
		int byteCount = decodeFile.getRowBytes();
		int height2 = decodeFile.getHeight();
		long mem = height2 * byteCount;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		if (mem > 100 * 1024 * 8) {
			decodeFile.compress(CompressFormat.JPEG, 80, bao);
		} else if (mem < 100 * 1024 * 8 && mem > 80 * 1024 * 8) {
			decodeFile.compress(CompressFormat.JPEG, 90, bao);
		} else {
			decodeFile.compress(CompressFormat.JPEG, 100, bao);
		}
		if (decodeFile != null && !decodeFile.isRecycled()) {
			decodeFile.recycle();
		}
		byte[] byteArray = bao.toByteArray();
		Bitmap decodeByteArray = BitmapFactory.decodeByteArray(byteArray, 0,
				byteArray.length);
		return decodeByteArray;
	}

	@Override
	public void onResponse(BaseResponse arg0) {
		switch (arg0.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			ToastUtils.Infotoast(mContext, "分享成功");
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			ToastUtils.Infotoast(mContext, "分享取消");
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			ToastUtils.Infotoast(mContext, "分享失败");
			break;
		}
	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {

			// loading.setVisibility(View.VISIBLE);

			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			// loading.setVisibility(View.GONE);

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
		webView.loadUrl(titleurl + "?fromkkApp=1");
	}

}
