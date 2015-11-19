package com.kankan.kankanews.ui.item;

//import io.vov.vitamio.MediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import tv.danmaku.ijk.media.widget.VideoView;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseVideoActivity;
import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.base.view.SildingFinishLayout;
import com.kankan.kankanews.bean.New_News;
import com.kankan.kankanews.bean.New_Recommend;
import com.kankan.kankanews.bean.SuccessMsg;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.dialog.TishiMsgHint;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.ui.MainActivity;
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.view.MarqueeTextView;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.StickyScrollView;
import com.kankan.kankanews.ui.view.VideoViewController;
import com.kankan.kankanews.ui.view.VideoViewController.ControllerType;
import com.kankan.kankanews.ui.view.popup.CustomShareBoard;
import com.kankan.kankanews.ui.view.popup.FontColumsBoard;
import com.kankan.kankanews.utils.ClickUtils;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.NetUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.HttpHandler;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sso.UMSsoHandler;

public class New_Activity_Content_Video extends BaseVideoActivity implements
		OnInfoListener, OnClickListener, OnPreparedListener,
		OnCompletionListener, OnErrorListener {

	private ArrayList<New_Recommend> recommends = new ArrayList<New_Recommend>();

	private String mid;
	private String type;
	private String titleurl;
	private String newstime;
	private String titlePic;
	private String sharedPic;
	private String titlelist;

	private LinearLayout content_comment_list;
	private LinearLayout content_comment_list_list;

	/** 微博微博分享接口实例 */
	// private IWeiboShareAPI mWeiboShareAPI = null;
	// private int mShareType = SHARE_CLIENT;
	/** 最大声音 */
	private int mMaxVolume;
	/** 当前声音 */
	private int mVolume = -1;
	/** 当前进度 */
	private long mSeek;
	/** 最大进度 */
	private long mMaxSeek;
	// 手势音量
	private GestureDetector mGestureDetector;
	private View mVolumeBrightnessLayout;
	private ImageView mOperationPercent;
	private boolean isCom;// 视频播完结束
	// videoView的状态
	private boolean isPlayer;
	private int smallWidth;
	private int smallHeight;
	private boolean hasBeenPaly;
	// public LinearLayout video_pb;
	// public LinearLayout small_video_pb;

	// private boolean isFullScrenn;

	// 提交分享的类型
	private String SHARE_SINA = "1";
	private String SHARE_QQ = "2";
	private String SHARE_WIEXIN = "3";
	private String SHARE_EMAIL = "4";

	private WindowManager wm;

	// 分享类
	private ShareUtil shareUtil;

	private New_News new_news;
	private NetUtils instance;
	private LayoutInflater inflater;

	private New_News localnew_news;// 本地数据
	// private User_Collect_Offline user_Collect_Offline; // 本地的收藏和离线数据
	// private new_news new_news;
	// private LinkedList<Comment> Comments = new LinkedList<Comment>();
	// private Comment_List comment_list;
	// private Content_Count content_count;

	private RelativeLayout content_loading; // 等待loading
	private RelativeLayout rootview; // 等待loading
	private RelativeLayout smallrootview; // 等待loading
	private StickyScrollView scollView;

	// 新闻
	private MyTextView content_title;// 新闻标题
	private MyTextView content_time;// 发表时间
	private ImageView contentVideoBg;// 新闻视频
	private MyTextView content_filelength;
	private MarqueeTextView cotent_onclick;
	private MyTextView content_intro;

	// 分享数据
	private LinearLayout content_share_shina_layout;
	private LinearLayout content_share_qq_layout;
	private LinearLayout content_share_weixin_layout;
	private LinearLayout content_share_mail_layout;
	private MyTextView content_share_shina;
	private MyTextView content_share_qq;
	private MyTextView content_share_weixin;
	private MyTextView content_share_mail;
	private MyTextView content_favorite_num;

	private ImageView content_offline_img;
	private MyTextView content_offline_text;

	private ImageView content_collect_img;

	// 播放组件
	private VideoViewController video_controller;
	private ImageView contentVideoPlayer;
	private VideoView contentVideoView;

	private AudioManager mAM;
	private View full_screen_guide;
	private View player_guide;
	private boolean videoIsDownload;
	private HttpHandler downloadVideo;
	private int needSeekTo;

	private int childCount;

	private boolean isGoShare = false;

	private FontColumsBoard fontBoard;

	public HttpHandler getDownloadVideo() {
		return downloadVideo;
	}

	public void setDownloadVideo(HttpHandler downloadVideo) {
		this.downloadVideo = downloadVideo;
	}

	public boolean isFullScrenn() {
		return isFullScrenn;
	}

	public void setFullScrenn(boolean isFullScrenn) {
		this.isFullScrenn = isFullScrenn;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (isPause)
			return;
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		if (width > height) {
			// video_view.setFull(true);
			this.horizontalScreen();
		} else {
			// video_view.setFull(false);
			this.verticalScreen(true);

		}
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 定义格式
		String parten = "#.#";
		decimal = new DecimalFormat(parten);
		decorView = getWindow().getDecorView();

		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		mAM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		animation = AnimationUtils.loadAnimation(this, R.anim.paly_alpha);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				player_bg.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				player_bg.setVisibility(View.GONE);
			}
		});
		setContentView(R.layout.new_activity_content_video);

		// 初始化头部
		initTitleBarIcon(R.drawable.ic_share, R.drawable.new_ic_back,
				R.drawable.ic_close_white, R.drawable.ic_font,
				R.drawable.ic_refresh);
		// 头部的左右点击事件
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);
		setOnContentClickLinester(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void initView() {

		inflater = LayoutInflater.from(this);
		content_loading = (RelativeLayout) findViewById(R.id.content_loading);
		rootview = (RelativeLayout) findViewById(R.id.rootview);
		smallrootview = (RelativeLayout) findViewById(R.id.smallrootview);
		scollView = (StickyScrollView) findViewById(R.id.scollView);
		contentVideoView = (VideoView) findViewById(R.id.video_view);
		video_controller = (VideoViewController) findViewById(R.id.video_controller);
		// bottom_bar = (LinearLayout) findViewById(R.id.bottom_bar);
		video_pb = (LinearLayout) findViewById(R.id.video_pb);
		small_video_pb = (LinearLayout) findViewById(R.id.small_video_pb);

		player_bg = (View) findViewById(R.id.player_bg);

		/** 知道界面 */
		full_screen_guide = findViewById(R.id.full_screen_guide);
		player_guide = findViewById(R.id.player_guide);

		// 新闻
		content_title = (MyTextView) findViewById(R.id.content_title);
		content_time = (MyTextView) findViewById(R.id.content_time);
		// content_filelength = (MyTextView)
		// findViewById(R.id.content_filelength);
		content_intro = (MyTextView) findViewById(R.id.content_intro);
		// 各种数据
		// cotent_onclick = (MarqueeTextView) findViewById(R.id.cotent_onclick);
		// 视频
		contentVideoBg = (ImageView) findViewById(R.id.content_video_bg);

		// 分享数据
		content_share_shina_layout = (LinearLayout) findViewById(R.id.content_share_shina_layout);
		content_share_qq_layout = (LinearLayout) findViewById(R.id.content_share_qq_layout);
		content_share_weixin_layout = (LinearLayout) findViewById(R.id.content_share_weixin_layout);
		content_share_mail_layout = (LinearLayout) findViewById(R.id.content_share_mail_layout);
		content_share_shina = (MyTextView) findViewById(R.id.content_share_shina);
		content_share_qq = (MyTextView) findViewById(R.id.content_share_qq);
		content_share_weixin = (MyTextView) findViewById(R.id.content_share_weixin);
		content_share_mail = (MyTextView) findViewById(R.id.content_share_mail);
		// content_favorite_num = (MyTextView)
		// findViewById(R.id.content_favorite_num);

		// content_collect_img = (ImageView)
		// findViewById(R.id.content_collect_img);
		contentVideoPlayer = (ImageView) findViewById(R.id.content_video_player);

		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

		// 推荐信息
		content_comment_list = (LinearLayout) findViewById(R.id.content_comment_list);
		content_comment_list_list = (LinearLayout) findViewById(R.id.content_comment_list_list);

		nightView = findViewById(R.id.night_view);
	}

	@Override
	protected void initData() {

		// // 设置视频的寬高
		// content_video.setLayoutParams(new RelativeLayout.LayoutParams(
		// LayoutParams.MATCH_PARENT, (int) (mScreenWidth / 16 * 9)));
		smallrootview.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (mScreenWidth / 16 * 9)));
		// video_controller.setLayoutParams(new RelativeLayout.LayoutParams(
		// LayoutParams.MATCH_PARENT, (int) (mScreenWidth / 16 * 9)));
		// 初始化数据
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		video_controller.setPlayerControl(contentVideoView);
		video_controller.setActivity_Content(this);
		contentVideoView.setIsNeedRelease(false);
		// new_news = (New_News)
		// getIntent().getSerializableExtra("news_content");
		// 获取上个页面传来的数据
		Intent intent = getIntent();
		mid = intent.getStringExtra("mid");
		type = intent.getStringExtra("type");
		titleurl = intent.getStringExtra("titleurl");
		newstime = intent.getStringExtra("newstime");
		titlePic = intent.getStringExtra("titlePic");
		sharedPic = intent.getStringExtra("sharedPic");
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

		instance = NetUtils.getInstance(this);
		// 提交点击
		instance.addNewNewsClickData("id=" + mid);

		NetUtils.getInstance(mContext).getAnalyse(this, "video",
				new_news.getShareTitle(), new_news.getTitleurl());

		initLocalDate = initLocalDate(new_news.getId());

		if (CommonUtils.isNetworkAvailable(mContext)) {
			initNetDate(new_news.getId(), new_news.getType());
		} else {
			content_loading.setVisibility(View.GONE);
			if (!initLocalDate) {
				player_bg.setVisibility(View.VISIBLE);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			ToastUtils.ErrorToastNoNet(mContext);
		}

		// if (this.mApplication.getMainActivity() != null) {
		// SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout)
		// findViewById(R.id.sildingFinishLayout);
		// mSildingFinishLayout
		// .setOnSildingFinishListener(new
		// SildingFinishLayout.OnSildingFinishListener() {
		//
		// @Override
		// public void onSildingFinish() {
		// finish();
		// }
		// });
		// mSildingFinishLayout.setTouchView(mSildingFinishLayout);
		// mSildingFinishLayout.setTouchView(scollView);
		// }
		// initCollectOffline(newsid);
	}

	/** 定时隐藏 */
	private Handler mDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mVolumeBrightnessLayout.setVisibility(View.GONE);
		}
	};

	/** 手势结束 */
	private void endGesture() {
		mVolume = -1;
		// 隐藏
		mDismissHandler.removeMessages(0);
		mDismissHandler.sendEmptyMessageDelayed(0, 1000);
	}

	// 是否可以触摸调整声音
	// boolean canScrool = true;

	public void setCanScrool(boolean canScrool) {
		this.canScrool = canScrool;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (isFullScrenn && canScrool) {
			mGestureDetector.onTouchEvent(ev);
			// 处理手势结束
			switch (ev.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_UP:
				endGesture();
				break;
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	private class MyGestureListener extends SimpleOnGestureListener {

		private float fx;
		private float fy;

		@Override
		public boolean onDown(MotionEvent e) {
			fx = e.getX();
			fy = e.getY();
			return super.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float mOldX = e1.getX(), mOldY = e1.getY();
			int y = (int) e2.getRawY();
			int x = (int) e2.getRawX();

			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();

			if (Math.abs((y - fy)) < Math.abs((x - fx)) + 100) {
				onPlayerSeek(x - mOldX);
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}

		/** 滑动 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			float mOldX = 0, mOldY = 0;
			if (e1 != null)
				mOldX = e1.getX();
			if (e1 != null)
				mOldY = e1.getY();
			int y = (int) e2.getRawY();
			int x = (int) e2.getRawX();

			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();

			if (Math.abs((y - fy)) > Math.abs((x - fx)) + 100) {
				onVolumeSlide((mOldY - y) / windowHeight);
			}

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	/**
	 * 滑动改变声音大小
	 * 
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		if (mVolume == -1) {
			mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;

			mDismissHandler.removeMessages(0);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}

		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// 变更声音
		mAM.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		// 变更进度条
		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = findViewById(R.id.operation_full).getLayoutParams().width
				* index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
	}

	/**
	 * 滑动改变播放进度
	 * 
	 * @param percent
	 */
	private void onPlayerSeek(float percent) {
		long msc = 15;
		if (percent < 0) {
			msc *= -1;
		}
		msc *= 1000;
		mSeek = contentVideoView.getCurrentPosition();
		mMaxSeek = contentVideoView.getDuration();

		long index = (long) (mSeek + msc);
		if (index > mMaxSeek)
			index = mMaxSeek;
		else if (index < 0)
			index = 0;

		contentVideoView.seekTo(index);
	}

	private void initNetDate(String mid, String type) {
		instance.getNewNewsContent(mid, type, mListener, mErrorListener);
	}

	// 加载本地数据
	private boolean initLocalDate(String mid) {
		try {
			localnew_news = this.dbUtils.findFirst(Selector
					.from(New_News.class).where("id", "=", mid));

			ArrayList<New_Recommend> local_Recommends = (ArrayList<New_Recommend>) this.dbUtils
					.findAll(New_Recommend.class);

			if (localnew_news != null) {
				new_news = localnew_news;
				initContentData();
				content_loading.setBackgroundColor(Color
						.parseColor("#00000000"));

				if (local_Recommends != null && local_Recommends.size() > 0) {
					recommends = local_Recommends;
					addRecommend();
					return true;
				}

			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return false;
	}

	boolean isContainKey;

	// 保存数据到本地
	private void saveDate() {

		new Thread() {
			@Override
			public void run() {
				if (new_news != null) {
					try {
						dbUtils.saveOrUpdate(new_news);
					} catch (DbException e) {
						e.printStackTrace();
					}
				}

			}
		}.start();
	}

	@Override
	protected void setListener() {

		contentVideoView.setOnCompletionListener(this);
		contentVideoView.setOnErrorListener(this);
		contentVideoView.setOnPreparedListener(this);
		contentVideoView.setOnInfoListener(this);

		player_bg.setOnClickListener(this);
		player_bg.setOnClickListener(this);
		full_screen_guide.setOnClickListener(this);
		player_guide.setOnClickListener(this);
		video_controller.setOnClickListener(this);

		// 分享
		content_share_shina_layout.setOnClickListener(this);
		content_share_qq_layout.setOnClickListener(this);
		content_share_weixin_layout.setOnClickListener(this);
		content_share_mail_layout.setOnClickListener(this);
		contentVideoPlayer.setOnClickListener(this);

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {

		String omsid = jsonObject.optString("omsid");
		String videourl = jsonObject.optString("videourl");
		String intro = jsonObject.optString("intro");
		String newstext = jsonObject.optString("newstext");
		String titlepic = jsonObject.optString("titlepic");
		String title = jsonObject.optString("title");

		new_news.setOmsid(omsid);
		new_news.setVideourl(videourl);
		new_news.setIntro(intro);
		new_news.setNewstext(newstext);
		new_news.setTitle(title);
		// new_news.setTitlepic(titlepic);

		new_news.setLooktime(Long.toString(TimeUtil.now()));

		saveDate();

		// 初始化shareutil类
		shareUtil = new ShareUtil(new_news, mContext);
		// 请求成功后展示数据
		// if (!new_news.equals(localnew_news)) {
		video_controller.setTitle(title);
		initContentData();

		// }
		content_loading.setVisibility(View.GONE);
		// 接着请求各种数据
		// instance.getNewsContentCountsData(newsid, NListener, NErrorListener);

		full_screen_guide.setVisibility(spUtil.getFirstContent() ? View.VISIBLE
				: View.GONE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		isNetOk = true;

		// 获取推荐信息
		instance.getRecommendData(getRecommendListener,
				getRecommendErrorListener);

	}

	@Override
	protected void onFailure(VolleyError error) {
		content_loading.setVisibility(View.GONE);
		if (!initLocalDate) {
			player_bg.setVisibility(View.VISIBLE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		}
		isNetOk = false;
		ToastUtils.Errortoast(mContext, "网络不可用");
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.content_share_shina_layout:
		case R.id.content_share_qq_layout:
		case R.id.content_share_weixin_layout:
		case R.id.content_share_mail_layout:
		case R.id.title_bar_content_img:
			if (ClickUtils.isFastDoubleClick()) {
				return;
			}
			shareUtil = new ShareUtil(new_news, mContext);
			isGoShare = true;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}

		switch (v.getId()) {

		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		case R.id.title_bar_right_second_img:
			this.refresh();
			break;
		case R.id.title_bar_content_img:
			// 一键分享
			CustomShareBoard shareBoard = new CustomShareBoard(this, shareUtil,
					this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;

		case R.id.title_bar_right_img:
			fontBoard = new FontColumsBoard(this);
			fontBoard.setAnimationStyle(R.style.popwin_anim_style);
			fontBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;

		case R.id.video_controller:
			// if (video_view.isPlaying() || hasBeenPaly) {
			if (!video_controller.isShow()
					&& video_pb.getVisibility() != View.VISIBLE
					&& small_video_pb.getVisibility() != View.VISIBLE)
				video_controller.show();
			// }
			break;
		case R.id.player_bg:
			if (CommonUtils.isNetworkAvailable(mContext)) {
				player_bg.setVisibility(View.GONE);
				content_loading.setVisibility(View.VISIBLE);
				initLocalDate(new_news.getId());
				initNetDate(new_news.getId(), new_news.getType());
			} else {
				ToastUtils.ErrorToastNoNet(mContext);
			}
			break;
		// 微博分享
		case R.id.content_share_shina_layout:
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					shareUtil.directShare(SHARE_MEDIA.SINA);
				}
			}, 300);
			// sendSingleMessage();
			break;
		// qq分享
		case R.id.content_share_qq_layout:
			shareUtil.directShare(SHARE_MEDIA.QQ);
			break;
		// 微信分享
		case R.id.content_share_weixin_layout:
			shareUtil.directShare(SHARE_MEDIA.WEIXIN_CIRCLE);
			break;
		// 邮件分享
		case R.id.content_share_mail_layout:
			shareUtil.setDrawable(contentVideoBg.getDrawable());
			shareUtil.directShare(SHARE_MEDIA.EMAIL);

			break;
		// 播放视频
		case R.id.content_video_player:
			CommonUtils.clickevent(mContext, "title", new_news.getTitle(),
					AndroidConfig.video_play_event);
			playerVideo();
			break;
		case R.id.full_screen_guide:
			full_screen_guide.setVisibility(View.GONE);
			spUtil.setFirstContent(false);
			break;
		case R.id.player_guide:
			player_guide.setVisibility(View.GONE);
			small_video_pb.setVisibility(View.GONE);
			goneContentVideoTempImage();
			// spUtil.setFirstFull(false);
			noShowPB = false;
			if (contentVideoView != null) {
				if (isCom) {
					play();
					isCom = false;
				} else {
					contentVideoView.start();
				}
			}
			break;
		}
	}

	private void playerVideo() {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			if (!CommonUtils.isWifi(mContext)) {
				if (!spUtil.isFlow()) {
					final TishiMsgHint dialog = new TishiMsgHint(mContext,
							R.style.MyDialog1);
					dialog.setContent("您已设置2G/3G/4G网络下不允许播放/缓存视频", "我知道了");
					dialog.setCancleListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				} else {
					final InfoMsgHint dialog = new InfoMsgHint(mContext,
							R.style.MyDialog1);
					dialog.setContent(
							"亲，您现在使用的是运营商网络，继续使用可能会产生流量费用，建议改用WIFI网络", "",
							"继续播放", "取消");
					dialog.setCancleListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.setOKListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							goneContentVideoTempImage();
							playState();
							play();
							dialog.dismiss();
						}
					});
					dialog.show();

				}

			} else {
				video_controller.getContent_video_temp_image().setVisibility(
						View.GONE);
				goneContentVideoTempImage();
				playState();
				play();
			}
		} else {
			final TishiMsgHint dialog = new TishiMsgHint(mContext,
					R.style.MyDialog1);
			dialog.setContent("当前无可用网络", "我知道了");
			dialog.setCancleListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}

	private void play() {
		isCom = false;
		// video_view.release(true);
		contentVideoView.stopPlayback();
		video_controller.reset();
		contentVideoView.setVideoPath(new_news.getVideourl());
		contentVideoView.requestFocus();
		contentVideoView.start();
		// content_video.setVisibility(View.GONE);
	}

	@Override
	public void onBackPressed() {
		if (isFullScrenn) {
			fullScrenntoSamll();
		} else {
			AnimFinsh();
		}
	}

	// 从全屏到小屏
	public void fullScrenntoSamll() {

		CommonUtils.clickevent(mContext, "action", "缩小",
				AndroidConfig.video_fullscreen_event);

		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			// new Handler().postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			// }
			// }, 1000);

			setFullHandler.sendEmptyMessageDelayed(SET_FULL_MESSAGE, 1000);
		}
	}

	// 从小屏到全屏
	public void samllScrenntoFull() {

		CommonUtils.clickevent(mContext, "action", "放大",
				AndroidConfig.video_fullscreen_event);

		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			// new Handler().postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			// }
			// }, 1000);

			setFullHandler.sendEmptyMessageDelayed(SET_FULL_MESSAGE, 1000);
		}
	}

	/**
	 * 给页面添加数据(新闻内容)
	 */
	public void initContentData() {
		// // 时长转化
		// int filelength = Integer.valueOf(RegexUtils.checkDigit(new_news
		// .getFilelength()) ? new_news.getFilelength() : "0");
		// int hour = filelength / 3600;
		// int minute = (filelength - hour * 3600) / 60;
		// int second = (filelength - hour * 3600) % 60;
		// // 秒数取两位
		// String second_two = second < 10 ? "0" + second : second + "";
		// // 分钟取两位
		// String minute_two = minute < 10 ? "0" + minute : minute + "";
		// String file_lenght;
		//
		// if (hour == 0) {
		// file_lenght = minute_two + ":" + second_two;
		// } else {
		// file_lenght = hour + ":" + minute_two + ":" + second_two;
		// }

		Long newstime = Long.valueOf(new_news.getNewstime() != null
				&& !TextUtils.isEmpty(new_news.getNewstime()) ? new_news
				.getNewstime() : "0");
		// String news_time = String.format("%tF %<tT", newstime);
		String news_time = TimeUtil.unix2date(newstime, "yyyy-MM-dd HH:mm:ss");
		// 新闻
		content_title.setText(new_news.getShareTitle());

		FontUtils.setTextViewFontSize(this, content_title,
				R.string.news_title_text_size, spUtil.getFontSizeRadix());

		content_time.setText(news_time);
		// content_filelength.setText(file_lenght);
		// cotent_onclick.setText(new_news.getOnc......());

		if (new_news.getIntro() != null) {
			content_intro
					.setText(new_news.getIntro().equals("") ? "\u3000\u3000暂无简介 "
							: "\u3000\u3000" + new_news.getIntro());
			FontUtils.setTextViewFontSize(this, content_intro,
					R.string.news_content_text_size, spUtil.getFontSizeRadix());
			ImgUtils.imageLoader.displayImage(new_news.getTitlepic(),
					contentVideoBg, Options.getBigImageOptions(createFromPath));
		}

	}

	/**
	 * 提交分享数据
	 */
	public void Commit_Share(SHARE_MEDIA platform) {
	}

	// 处理网络出错
	protected ErrorListener ShareErrorListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			error.printStackTrace();
		}
	};
	// 处理网络成功
	protected Listener<JSONObject> ShareListener = new Listener<JSONObject>() {
		@Override
		public void onResponse(JSONObject jsonObject) {
			try {
				SuccessMsg successMsg = new SuccessMsg();
				successMsg.parseJSON(jsonObject);
			} catch (NetRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onPause() {
		// if (video_view.isPlaying()) {
		// video_view.pause();
		// isPlayer = true;
		// } else {
		// TODO
		// if (video_view.getCurrentPosition() > 0) {
		// Bitmap currentFrame = getCurrentFrame();
		// video_controller.getContent_video_temp_image().setVisibility(
		// View.VISIBLE);
		// video_controller.getContent_video_temp_image().setImageBitmap(
		// currentFrame);
		// }
		DebugLog.e("pause");

		// if (this.getRequestedOrientation() ==
		// ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// this.verticalScreen();
		// }
		if (this.rootview.getVisibility() == View.VISIBLE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			this.verticalScreen(false);
		}
		contentVideoView.pause();
		isPause = true;
		// }
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		DebugLog.e("onResume");
		isPause = false;
		isGoShare = false;
		// if (this.getRequestedOrientation() ==
		// ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// this.verticalScreen();
		// }
		initNightView(false);
		setFullHandler.sendEmptyMessageDelayed(SET_FULL_MESSAGE, 1000);
		if (contentVideoBg.getVisibility() == View.GONE)
			contentVideoView.start();
		// if (FontUtils.hasChangeFontSize())
		changeFontSize();
	}

	// private Bitmap getCurrentFrame() {
	// try {
	// Bitmap currentFrame = video_view.getCurrentFrame();
	// return currentFrame;
	// } catch (OutOfMemoryError e) {
	// ImgUtils.imageLoader.clearMemoryCache();
	// System.gc();
	// getCurrentFrame();
	// }
	// return null;
	// }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// handler.removeMessages(1);
		// video_view.release(true);
		contentVideoView.stopPlayback();
	}

	private File videoFile;
	private DecimalFormat decimal;
	private View player_bg;
	private Animation animation;
	private View decorView;
	private boolean initLocalDate;
	private boolean isNetOk;
	private Bitmap smallBitmap;
	private Drawable createFromPath;
	private boolean isPause;

	// @Override
	// public boolean onError(MediaPlayer mp, int what, int extra) {
	// ToastUtils.Errortoast(mContext, "视频播放有误,请稍候重试");
	// return true;
	// }
	@Override
	public boolean onError(IMediaPlayer mp, int what, int extra) {
		ToastUtils.Errortoast(mContext, "视频播放有误,请稍候重试");
		return true;
	}

	// @Override
	// public void onCompletion(MediaPlayer mp) {
	// if (isFullScrenn) {
	// if (getRequestedOrientation() !=
	// ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
	// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	// new Handler().postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
	// }
	// }, 3000);
	// }
	// }
	// isCom = true;
	// resetVideo(false);
	// }

	@Override
	public void onCompletion(IMediaPlayer mp) {
		// TODO Auto-generated method stub
		if (isFullScrenn) {
			// if (getRequestedOrientation() !=
			// ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			// // new Handler().postDelayed(new Runnable() {
			// // @Override
			// // public void run() {
			// //
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			// // }
			// // }, 1000);
			//
			// setFullHandler.sendEmptyMessageDelayed(SET_FULL_MESSAGE, 1000);
			// }
			fullScrenntoSamll();
		}
		isCom = true;
		resetVideo(false);
	}

	// @Override
	// public void onPrepared(MediaPlayer mp) {
	// hasBeenPaly = true;
	// }

	@Override
	public void onPrepared(IMediaPlayer mp) {
		// TODO Auto-generated method stub
		Log.e("onPrepared", "onPrepared");
		hasBeenPaly = true;
		small_video_pb.setVisibility(View.GONE);
		video_pb.setVisibility(View.GONE);
		if (this.isFullScrenn) {
			// new Handler().postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// TODO Auto-generated method stub
			// full_content_video.setVisibility(View.GONE);
			// }
			// }, 600);
		} else {
			// new Handler().postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// TODO Auto-generated method stub
			contentVideoBg.setVisibility(View.GONE);
			// }
			// }, 400);
		}
	}

	// 播放完成回到初始状态
	private void resetVideo(boolean isResetpalyer) {
		contentVideoBg.setVisibility(View.VISIBLE);
		contentVideoPlayer.setVisibility(View.VISIBLE);
		video_pb.setVisibility(View.GONE);
		small_video_pb.setVisibility(View.GONE);
		hasBeenPaly = true;
		// if (new_news != null) {
		// imageLoader.displayImage(new_news.getTitlepic(), content_video,
		// Options.getSmallImageOptions());// 视频
		// }
		if (isResetpalyer) {
			// video_view.release(true);
			contentVideoView.stopPlayback();
		}
	}

	// 播放视频状态
	private void playState() {
		video_pb.setVisibility(View.VISIBLE);
		small_video_pb.setVisibility(View.VISIBLE);
		// small_video_pb.setVisibility(View.VISIBLE);
		contentVideoPlayer.setVisibility(View.GONE);
	}

	// @Override
	// public boolean onInfo(MediaPlayer mp, int what, int extra) {
	//
	// switch (what) {
	//
	// case MediaPlayer.MEDIA_INFO_BUFFERING_START:
	// video_view.pause();
	// isload = true;
	// if (!noShowPB) {
	// video_pb.setVisibility(View.VISIBLE);
	// small_video_pb.setVisibility(View.VISIBLE);
	// video_controller.setEnabled(false);
	// noShowPB = false;
	// }
	// break;
	// case MediaPlayer.MEDIA_INFO_BUFFERING_END:
	//
	// if (!New_Activity_Content_Video.this.isPause) {
	// if (content_video.getVisibility() == View.VISIBLE) {
	// content_video.setVisibility(View.GONE);
	// }
	// isload = false;
	// if (spUtil.getFirstFull() && isFullScrenn) {
	// isPlayer = false;
	// player_guide.setVisibility(View.VISIBLE);
	// noShowPB = true;
	// } else {
	// video_view.start();
	// }
	// video_controller.setEnabled(true);
	// goneContentVideoTempImage();
	// video_pb.setVisibility(View.GONE);
	// small_video_pb.setVisibility(View.GONE);
	//
	// } else {
	// if (content_video.getVisibility() == View.VISIBLE) {
	// content_video.setVisibility(View.GONE);
	// }
	// isload = false;
	// if (spUtil.getFirstFull() && isFullScrenn) {
	// isPlayer = false;
	// player_guide.setVisibility(View.VISIBLE);
	// noShowPB = true;
	// }
	// video_controller.setEnabled(true);
	// goneContentVideoTempImage();
	// video_pb.setVisibility(View.GONE);
	// small_video_pb.setVisibility(View.GONE);
	// }
	// break;
	// }
	// return true;
	// // TODO Auto-generated method stub
	// }

	@Override
	public boolean onInfo(IMediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		switch (what) {

		case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
			Log.e("MEDIA_INFO_BUFFERING_START", "MEDIA_INFO_BUFFERING_START");
			contentVideoView.pause();
			isload = true;
			if (!noShowPB) {
				video_pb.setVisibility(View.VISIBLE);
				// small_video_pb.setVisibility(View.VISIBLE);
				video_controller.setEnabled(false);
				noShowPB = false;
			}
			break;
		case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
			Log.e("MEDIA_INFO_BUFFERING_END", "MEDIA_INFO_BUFFERING_END");
			if (!New_Activity_Content_Video.this.isPause) {
				if (contentVideoBg.getVisibility() == View.VISIBLE) {
					contentVideoBg.setVisibility(View.GONE);
				}
				isload = false;
				if (spUtil.getFirstFull() && isFullScrenn) {
					isPlayer = false;
					player_guide.setVisibility(View.VISIBLE);
					noShowPB = true;
				} else {
					contentVideoView.start();
				}
				video_controller.setEnabled(true);
				goneContentVideoTempImage();
				video_pb.setVisibility(View.GONE);
				small_video_pb.setVisibility(View.GONE);

			} else {
				if (contentVideoBg.getVisibility() == View.VISIBLE) {
					contentVideoBg.setVisibility(View.GONE);
				}
				isload = false;
				if (spUtil.getFirstFull() && isFullScrenn) {
					isPlayer = false;
					player_guide.setVisibility(View.VISIBLE);
					noShowPB = true;
				}
				video_controller.setEnabled(true);
				goneContentVideoTempImage();
				video_pb.setVisibility(View.GONE);
				small_video_pb.setVisibility(View.GONE);
			}
			break;
		}
		return true;
	}

	// 无网络提示
	public boolean isNetworkAvailable() {
		NetworkInfo info = CommonUtils.getNetworkInfo(mContext);
		if (info != null) {
			return info.isAvailable();
		} else {
			ToastUtils.ErrorToastNoNet(mContext);
			return false;
		}
	}

	private boolean noShowPB;
	private boolean isload;

	private void goneContentVideoTempImage() {
		video_controller.getContent_video_temp_image().setVisibility(View.GONE);
		BitmapDrawable drawable = (BitmapDrawable) video_controller
				.getContent_video_temp_image().getDrawable();
		if (drawable != null) {
			Bitmap bmp = drawable.getBitmap();
			if (null != bmp && !bmp.isRecycled()) {
				bmp.recycle();
				bmp = null;
			}
		}
	}

	// 屏蔽掉第一次（注册广播引起的）
	int netchagetime = 0;

	@Override
	public void refresh() {
		content_comment_list.setVisibility(View.GONE);
		content_comment_list_list.removeAllViews();
		content_loading.setVisibility(View.VISIBLE);
		if (CommonUtils.isNetworkAvailable(mContext)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			if (contentVideoView.isPlaying()) {
				contentVideoView.pause();
			}
			contentVideoBg.setVisibility(View.VISIBLE);
			goneContentVideoTempImage();
			// video_view.release(true);
			contentVideoView.stopPlayback();
			contentVideoView.setVideoURI(null);
			video_pb.setVisibility(View.GONE);
			small_video_pb.setVisibility(View.GONE);
			contentVideoPlayer.setVisibility(View.VISIBLE);
			initNetDate(new_news.getId(), new_news.getType());
			// 清除已存在的推荐信息view
			content_comment_list_list.removeAllViews();
		} else {
			initLocalDate = initLocalDate(new_news.getId());
			content_loading.setVisibility(View.GONE);
			if (!initLocalDate) {
				player_bg.setVisibility(View.VISIBLE);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			ToastUtils.ErrorToastNoNet(mContext);
		}

	}

	/*
	 * 获取新闻点击量
	 */
	// 处理网络出错
	protected ErrorListener getRecommendErrorListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			ToastUtils.ErrorToastNoNet(mContext);
		}
	};
	// 处理网络成功
	protected Listener<JSONArray> getRecommendListener = new Listener<JSONArray>() {
		@Override
		public void onResponse(JSONArray jsonObject) {

			try {
				// 先清空信息
				content_comment_list_list.removeAllViews();
				if (recommends != null && recommends.size() > 0) {
					recommends.clear();
				}
				JSONArray jsonArray = jsonObject;
				if (jsonArray != null && jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
						New_Recommend recommend = new New_Recommend();
						recommend.parseJSON(jsonObject1);
						recommends.add(recommend);
					}
					saveRecommend();
					addRecommend();
				} else {
					content_comment_list.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	protected void saveRecommend() {
		try {
			dbUtils.saveOrUpdateAll(recommends);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void addRecommend() {
		player_bg.setVisibility(View.GONE);
		content_comment_list.setVisibility(View.VISIBLE);
		for (int i = 0; i < recommends.size(); i++) {

			final New_Recommend mNew_Recommend = recommends.get(i);
			mNew_Recommend.setTitlepic(CommonUtils.doWebpUrl(mNew_Recommend
					.getTitlepic()));
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.new_recommend_item, null);
			ImageView home_news_titlepic = (ImageView) v
					.findViewById(R.id.home_news_titlepic);
			TextView home_news_title = (TextView) v
					.findViewById(R.id.home_news_title);
			ImageView home_news_newstime_sign = (ImageView) v
					.findViewById(R.id.home_news_newstime_sign);
			home_news_newstime_sign.setVisibility(View.VISIBLE);
			// TextView recommend_intro = (TextView)
			// v.findViewById(R.id.recommend_intro);
			TextView home_news_newstime = (TextView) v
					.findViewById(R.id.home_news_newstime);
			home_news_titlepic.setTag(R.string.viewwidth, PixelUtil.dp2px(100));
			// CommonUtils.zoomImage(imageLoader, mNew_Recommend.getTitlepic(),
			// home_news_titlepic, mContext);
			ImgUtils.imageLoader.displayImage(mNew_Recommend.getTitlepic(),
					home_news_titlepic, ImgUtils.homeImageOptions);

			home_news_title.setText(mNew_Recommend.getTitle());
			// home_news_newstime.setText(TimeUtil.unix2date(
			// Long.valueOf(mNew_Recommend.getNewstime()), "yyyy-MM-dd"));
			home_news_newstime.setText(mNew_Recommend.getNewsClicks());
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (contentVideoView.isPlaying()) {
						contentVideoView.pause();
					}
					contentVideoBg.setVisibility(View.VISIBLE);
					goneContentVideoTempImage();
					// video_view.release(true);
					contentVideoView.stopPlayback();
					contentVideoView.setVideoURI(null);
					video_pb.setVisibility(View.GONE);
					small_video_pb.setVisibility(View.GONE);
					contentVideoPlayer.setVisibility(View.VISIBLE);
					int news_type = Integer.valueOf(mNew_Recommend.getType());
					if (news_type % 10 == 1) {
						mContext.startAnimActivityByParameter(
								New_Activity_Content_Video.class,
								mNew_Recommend.getMid(),
								mNew_Recommend.getType(),
								mNew_Recommend.getTitleurl(),
								mNew_Recommend.getNewstime(),
								mNew_Recommend.getTitle(),
								mNew_Recommend.getTitlepic(),
								mNew_Recommend.getSharedPic(),
								mNew_Recommend.getTitle());
					} else if (news_type % 10 == 5) {
						// 专题
						mContext.startSubjectActivityByParameter(
								New_Avtivity_Subject.class,
								mNew_Recommend.getZtid(),
								mNew_Recommend.getTitle(),
								mNew_Recommend.getTitlepic(),
								mNew_Recommend.getTitleurl(),
								mNew_Recommend.getTitlepic(),
								mNew_Recommend.getSharedPic(),
								mNew_Recommend.getTitle());
					} else if (news_type % 10 == 6) {// 直播
						// New_LivePlayFragment fragment =
						// (New_LivePlayFragment)
						// ((MainActivity) mContext).fragments
						// .get(1);
						// fragment.setSelectPlay(true);
						// fragment.setSelectPlayID(Integer
						// .parseInt(mNew_Recommend.getZtid()));
						// ((MainActivity) mContext)
						// .touchTab(((MainActivity) mContext).tabLive);

					} else {
						mContext.startAnimActivityByParameter(
								New_Activity_Content_Web.class,
								mNew_Recommend.getMid(),
								mNew_Recommend.getType(),
								mNew_Recommend.getTitleurl(),
								mNew_Recommend.getNewstime(),
								mNew_Recommend.getTitle(),
								mNew_Recommend.getTitlepic(),
								mNew_Recommend.getSharedPic(),
								mNew_Recommend.getTitle());
					}
				}
			});

			content_comment_list_list.addView(v);
		}
	}

	@Override
	public void finish() {
		if (contentVideoBg != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				contentVideoBg.setBackground(null);
			} else {
				contentVideoBg.setBackgroundDrawable(null);
			}
		}
		if (this.mApplication.getMainActivity() == null) {
			Intent intent = getIntent();
			intent.setClass(this, MainActivity.class);
			this.startActivity(intent);
			overridePendingTransition(R.anim.alpha_in, R.anim.out_to_right);
		}
		System.gc();
		super.finish();
	}

	@Override
	public void shareReBack() {
		// TODO Auto-generated method stub
		super.shareReBack();
		isGoShare = false;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
	}

	@Override
	public void copy2Clip() {
		// TODO Auto-generated method stub
		ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		clip.setText(titleurl);
		ToastUtils.Infotoast(this, "已将链接复制进黏贴板");
	}

	@Override
	public void changeFontSize() {
		// TODO Auto-generated method stub
		FontUtils.setTextViewFontSize(this, content_title,
				R.string.news_title_text_size, spUtil.getFontSizeRadix());
		FontUtils.setTextViewFontSize(this, content_intro,
				R.string.news_content_text_size, spUtil.getFontSizeRadix());
		FontUtils.chagneFontSizeGlobal();
	}

	@Override
	public void chage2Day() {
		// TODO Auto-generated method stub
		nightView.setVisibility(View.GONE);
		((CrashApplication) this.getApplication()).changeMainActivityDayMode();
	}

	@Override
	public void chage2Night() {
		// TODO Auto-generated method stub
		nightView.setVisibility(View.VISIBLE);
		((CrashApplication) this.getApplication()).changeMainActivityDayMode();
	}

	private void verticalScreen(boolean isNeedPlay) {
		DebugLog.e("竖了");
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		contentVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE);
		if (contentVideoView.isPlaying()
				|| contentVideoView.getVideoURI() != null) {
			contentVideoView.pause();
			isPlayer = true;
		} else {
			video_controller.getContent_video_temp_image().setVisibility(
					View.VISIBLE);
		}

		attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setAttributes(attrs);
		// 取消全屏设置
		getWindow()
				.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		isFullScrenn = false;
		setRightFinsh(true);
		player_guide.setVisibility(View.GONE);
		video_controller.setmControllerType(ControllerType.SmallController);
		rootview.setVisibility(View.GONE);
		scollView.setVisibility(View.VISIBLE);
		// bottom_bar.setVisibility(View.VISIBLE);
		rootview.removeView(contentVideoView);
		rootview.removeView(video_controller);
		// TODO
		// video_view.setOrentation(true);
		smallrootview.addView(contentVideoView, 0);
		smallrootview.addView(video_controller, 1);
		video_controller.changeView();
		if (contentVideoView.getVideoURI() == null) {
			isPlayer = true;
		}
		if (isPlayer && isNeedPlay) {

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isPlayer = false;
					if (contentVideoView.getVideoURI() == null) {
						Log.e("video_view", "video_view");
						playState();
						play();
					} else {
						contentVideoView.start();
					}

					video_controller.show();
					// if(needSeekTo != 0)
					// video_view.seekTo(needSeekTo);
				}
			}, 100);

		}
	}

	private void horizontalScreen() {
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		if (fontBoard != null && fontBoard.isShowing())
			fontBoard.dismiss();
		DebugLog.e("heng" + this);
		contentVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH);
		CommonUtils.clickevent(mContext, "action", "放大",
				AndroidConfig.video_fullscreen_event);

		full_screen_guide.setVisibility(View.GONE);
		spUtil.setFirstContent(false);
		if (isload || spUtil.getFirstFull()) {
			isPlayer = false;
		} else {
			isPlayer = true;
		}

		// TODO
		if (contentVideoView.getVideoURI() == null) {
			goneContentVideoTempImage();
			isload = true;
			playState();
			isCom = true;
			if (!spUtil.getFirstFull()) {
				play();
			}
		}

		if (spUtil.getFirstFull()) {
			player_guide.setVisibility(View.VISIBLE);
			spUtil.setFirstFull(false);
			contentVideoView.pause();
		}
		if (smallWidth == 0) {
			smallWidth = contentVideoView.getWidth();
			smallHeight = contentVideoView.getHeight();
		}

		isFullScrenn = true;
		setRightFinsh(false);
		contentVideoBg.setVisibility(View.GONE);
		video_controller
				.setmControllerType(ControllerType.FullScrennController);
		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setAttributes(attrs);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		smallrootview.removeView(contentVideoView);
		smallrootview.removeView(video_controller);

		rootview.setVisibility(View.VISIBLE);
		scollView.setVisibility(View.GONE);
		// bottom_bar.setVisibility(View.GONE);
		// TODO
		// video_view.setOrentation(true);
		rootview.addView(contentVideoView, 0);
		rootview.addView(video_controller, 1, new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		video_controller.changeView();
		if (isPlayer) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isPlayer = false;
					if (contentVideoView != null) {
						if (isCom) {
							playState();
							// play();
							isCom = false;
						}
						// if(needSeekTo != 0)
						// video_view.seekTo(needSeekTo);
						if (!isGoShare && !spUtil.getFirstFull())
							contentVideoView.start();
					}
					video_controller.getContent_video_temp_image()
							.setVisibility(View.GONE);
				}
			}, 100);
		}
	}

	@Override
	public void netChanged() {
		// TODO Auto-generated method stub
		if (contentVideoView.getVideoURI() != null) {
			contentVideoPlayer.setVisibility(View.VISIBLE);
			if (contentVideoView.isPlaying()) {
				contentVideoView.pause();
			} else {
				contentVideoView.stopPlayback();
			}
		}
	}
}
