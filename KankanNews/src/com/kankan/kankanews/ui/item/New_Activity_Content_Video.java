package com.kankan.kankanews.ui.item;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
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
import com.kankan.kankanews.bean.New_News;
import com.kankan.kankanews.bean.New_Recommend;
import com.kankan.kankanews.bean.SuccessMsg;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.dialog.TishiMsgHint;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.sina.AccessTokenKeeper;
import com.kankan.kankanews.sina.Constants;
import com.kankan.kankanews.ui.MainActivity;
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.view.CustomShareBoard;
import com.kankan.kankanews.ui.view.MarqueeTextView;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.StickyScrollView;
import com.kankan.kankanews.ui.view.VideoViewController;
import com.kankan.kankanews.ui.view.VideoViewController.ControllerType;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.HttpHandler;
import com.nostra13.universalimageloader.utils.L;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
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
import com.sina.weibo.sdk.utils.Utility;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class New_Activity_Content_Video extends BaseVideoActivity implements
		OnInfoListener, IWeiboHandler.Response, OnClickListener,
		OnPreparedListener, OnCompletionListener, OnErrorListener {

	private ArrayList<New_Recommend> recommends = new ArrayList<New_Recommend>();

	private String mid;
	private String type;
	private String titleurl;
	private String newstime;
	private String titlepiclist;
	private String titlelist;

	private LinearLayout content_comment_list;
	private LinearLayout content_comment_list_list;

	/** 微博微博分享接口实例 */
	private IWeiboShareAPI mWeiboShareAPI = null;
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
	private ItnetUtils instance;
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
	private ImageView content_video;// 新闻视频
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
	private ImageView video_player;
	private VideoView video_view;

	private AudioManager mAM;
	private View full_screen_guide;
	private View player_guide;
	private boolean videoIsDownload;
	private HttpHandler downloadVideo;

	private int childCount;

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

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		if (width > height) {
			if (shareBoard != null && shareBoard.isShowing()) {
				shareBoard.dismiss();
			}
			video_view.setFull(true);
			video_view.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
			CommonUtils.clickevent(mContext, "action", "放大",
					AndroidConfig.video_fullscreen_event);

			full_screen_guide.setVisibility(View.GONE);
			spUtil.setFirstContent(false);
			if (video_view.isPlaying()) {
				video_view.pause();
			}
			if (isload || spUtil.getFirstFull()) {
				isPlayer = false;
			} else {
				isPlayer = true;
			}

			if (video_view.getmUri() == null) {
				goneContentVideoTempImage();
				isload = true;
				playState();
				play();
			}

			if (spUtil.getFirstFull() && !isload) {
				isPlayer = false;
				player_guide.setVisibility(View.VISIBLE);
				noShowPB = true;
			}

			if (smallWidth == 0) {
				smallWidth = video_view.getWidth();
				smallHeight = video_view.getHeight();
			}

			isFullScrenn = true;
			setRightFinsh(false);
			content_video.setVisibility(View.GONE);
			video_controller
					.setmControllerType(ControllerType.FullScrennController);
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			getWindow().setAttributes(attrs);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

			smallrootview.removeView(video_view);
			smallrootview.removeView(video_controller);

			rootview.setVisibility(View.VISIBLE);
			scollView.setVisibility(View.GONE);
			// bottom_bar.setVisibility(View.GONE);
			video_view.setOrentation(true);
			rootview.addView(video_view, 0);
			rootview.addView(video_controller, 1,
					new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT));
			video_controller.changeView();
			if (isPlayer) {

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						isPlayer = false;
						if (video_view != null) {
							if (isCom) {
								playState();
								play();
								isCom = false;
							}
							video_view.start();
						}
						video_controller.getContent_video_temp_image()
								.setVisibility(View.GONE);
					}
				}, 100);

			}

		} else {

			video_view.setFull(false);
			video_view.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
			if (video_view.isPlaying()) {
				video_view.pause();
				isPlayer = true;
			} else {
				video_controller.getContent_video_temp_image().setVisibility(
						View.VISIBLE);
				video_controller.getContent_video_temp_image().setImageBitmap(
						video_view.getCurrentFrame());
			}

			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(attrs);
			// 取消全屏设置
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			isFullScrenn = false;
			setRightFinsh(true);
			player_guide.setVisibility(View.GONE);
			video_controller.setmControllerType(ControllerType.SmallController);
			rootview.setVisibility(View.GONE);
			scollView.setVisibility(View.VISIBLE);
			// bottom_bar.setVisibility(View.VISIBLE);
			rootview.removeView(video_view);
			rootview.removeView(video_controller);

			video_view.setOrentation(true);
			smallrootview.addView(video_view, 0);
			smallrootview.addView(video_controller, 1);
			video_controller.changeView();

			if (isPlayer) {

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						isPlayer = false;
						video_view.start();

						video_controller.show();
					}
				}, 100);

			}
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
		mMaxVolume = mMaxVolume = mAM
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

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

		// mShareType = getIntent().getIntExtra(KEY_SHARE_TYPE, SHARE_CLIENT);
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

		// upDataDownUI(true);

		// 注册广播
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(mReceiver, mFilter);

		// 初始化头部
		initTitle_Right_Left_bar("看看新闻", "", "", "#ffffff",
				R.drawable.new_ic_more, R.drawable.new_ic_back, "#000000",
				"#000000");
		// 头部的左右点击事件
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		mWeiboShareAPI.handleWeiboResponse(intent, this);

	}

	@Override
	protected void initView() {

		inflater = LayoutInflater.from(this);
		content_loading = (RelativeLayout) findViewById(R.id.content_loading);
		rootview = (RelativeLayout) findViewById(R.id.rootview);
		smallrootview = (RelativeLayout) findViewById(R.id.smallrootview);
		scollView = (StickyScrollView) findViewById(R.id.scollView);
		video_view = (VideoView) findViewById(R.id.video_view);
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
		content_video = (ImageView) findViewById(R.id.content_video);

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
		video_player = (ImageView) findViewById(R.id.video_player);
		video_view = (VideoView) findViewById(R.id.video_view);

		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

		// 推荐信息
		content_comment_list = (LinearLayout) findViewById(R.id.content_comment_list);
		content_comment_list_list = (LinearLayout) findViewById(R.id.content_comment_list_list);

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
		video_controller.setPlayerControl(video_view);
		video_controller.setActivity_Content(this);

		// new_news = (New_News)
		// getIntent().getSerializableExtra("news_content");
		// 获取上个页面传来的数据
		Intent intent = getIntent();
		mid = intent.getStringExtra("mid");
		type = intent.getStringExtra("type");
		titleurl = intent.getStringExtra("titleurl");
		newstime = intent.getStringExtra("newstime");
		titlepiclist = intent.getStringExtra("titlepiclist");
		titlelist = intent.getStringExtra("titlelist");
		// 存储数据
		new_news = new New_News();
		new_news.setId(mid);
		new_news.setType(type);
		new_news.setTitleurl(titleurl);
		new_news.setNewstime(newstime);
		new_news.setTitlepiclist(titlepiclist);
		new_news.setTitlelist(titlelist);

		// 提交点击
		ItnetUtils.getInstance(mContext).addNewNewsClickData("id=" + mid);

		instance = ItnetUtils.getInstance(this);

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
			float mOldX = e1.getX(), mOldY = e1.getY();
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
		mSeek = video_view.getCurrentPosition();
		mMaxSeek = video_view.getDuration();

		long index = (long) (mSeek + msc);
		if (index > mMaxSeek)
			index = mMaxSeek;
		else if (index < 0)
			index = 0;

		video_view.seekTo(index);
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
	private void saveDate(String mid) {

		new Thread() {
			@Override
			public void run() {
				if (new_news != null) {
					try {
						// if (!new_news.equals(localnew_news)) {
						// dbUtils.deleteById(new_news.class,
						// new_news.getId());
						dbUtils.saveOrUpdate(new_news);
						// }
						// this.dbUtils.delete(new_news.class,
						// WhereBuilder.b("mid","==",mid));
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}.start();
	}

	@Override
	protected void setListener() {

		video_view.setOnCompletionListener(this);
		video_view.setOnErrorListener(this);
		video_view.setOnPreparedListener(this);
		video_view.setOnInfoListener(this);

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
		video_player.setOnClickListener(this);

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
		new_news.setTitlepic(titlepic);

		new_news.setLooktime(Long.toString(TimeUtil.now()));

		saveDate(new_news.getId());

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
		content_loading.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.content_share_shina_layout:
		case R.id.content_share_qq_layout:
		case R.id.content_share_weixin_layout:
		case R.id.content_share_mail_layout:
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			shareUtil = new ShareUtil(new_news, mContext);
			break;
		}

		switch (v.getId()) {

		case R.id.com_title_bar_left_bt:
			onBackPressed();
			break;
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			// 一键分享
			CustomShareBoard shareBoard = new CustomShareBoard(this, shareUtil);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(mContext.getWindow().getDecorView(),
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
			sendSingleMessage();
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
			shareUtil.setDrawable(content_video.getDrawable());
			shareUtil.directShare(SHARE_MEDIA.EMAIL);

			break;
		// 播放视频
		case R.id.video_player:
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
			spUtil.setFirstFull(false);
			noShowPB = false;
			if (video_view != null) {
				if (isCom) {
					play();
					isCom = false;
				} else {
					video_view.start();
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
		video_view.release(true);
		video_controller.reset();
		video_view.setVideoPath(new_news.getVideourl());
		video_view.requestFocus();
		video_view.start();
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
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
				}
			}, 1000);
		}
	}

	// 从小屏到全屏
	public void samllScrenntoFull() {

		CommonUtils.clickevent(mContext, "action", "放大",
				AndroidConfig.video_fullscreen_event);

		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
				}
			}, 1000);
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
		content_title.setText(new_news.getTitlelist());
		content_time.setText(news_time);
		// content_filelength.setText(file_lenght);
		// cotent_onclick.setText(new_news.getOnc......());

		if (new_news.getIntro() != null) {
			content_intro.setText(new_news.getIntro().equals("") ? "暂无简介 "
					: new_news.getIntro());

			imageLoader.displayImage(new_news.getTitlepiclist(), content_video,
					Options.getBigImageOptions(createFromPath));
			// } else {
			// WebSettings webSettings = content_web_layout.getSettings();
			// if (isNetOk) {
			// webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
			// } else {
			// webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
			// }
			// // 设置适应屏幕
			// webSettings.setSupportZoom(true);
			// webSettings.setJavaScriptEnabled(true);
			// webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
			//
			// DisplayMetrics metrics = new DisplayMetrics();
			// getWindowManager().getDefaultDisplay().getMetrics(metrics);
			// int mDensity = metrics.densityDpi;
			// if (mDensity == 240) {
			// webSettings.setDefaultZoom(ZoomDensity.FAR);
			// } else if (mDensity == 160) {
			// webSettings.setDefaultZoom(ZoomDensity.MEDIUM);
			// } else if (mDensity == 120) {
			// webSettings.setDefaultZoom(ZoomDensity.CLOSE);
			// } else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
			// webSettings.setDefaultZoom(ZoomDensity.FAR);
			// } else if (mDensity == DisplayMetrics.DENSITY_TV) {
			// webSettings.setDefaultZoom(ZoomDensity.FAR);
			// }
			// // 开启 DOM storage API 功能
			// content_web_layout.getSettings().setDomStorageEnabled(true);
			// // 开启 database storage API 功能
			// content_web_layout.getSettings().setDatabaseEnabled(true);
			// // 设置数据库缓存路径
			// //
			// content_web_layout.getSettings().setDatabasePath("/data/data/a");
			// int width = PixelUtil.px2dp(mScreenWidth) - 20;
			// int height = (int) (width*0.75);
			// String data =
			// "<html><head><style>img{width:"+width+"px;height:"+height+"px;}</style></head><body>"+
			// new_news.getNewstext().replaceAll("\\\\", "")
			// +"</body></html>";
			// data = data.replaceAll("480", width+"");
			// data = data.replaceAll("361", height + "");
			// content_web_layout.loadDataWithBaseURL(null, data, "text/html",
			// "UTF-8", null);
			// // content_web_layout.loadUrl("http://www.baidu.com");
			// // content_web_layout.loadDataWithBaseURL(null,
			// //
			// "<img  src=\"http://static.statickksmg.com/image/2015/01/15/23b7d5dde85f1e2ab046dfeef112d705.jpg\"/>",
			// // "text/html", "UTF-8", null);
			// // 设置Web视图
			// content_web_layout.setWebViewClient(new WebViewClient());
		}

	}

	/**
	 * 提交分享数据
	 */
	public void Commit_Share(SHARE_MEDIA platform) {
		// if (platform == SHARE_MEDIA.SINA) {
		// content_share_shina
		// .setText((Integer.parseInt((String) content_share_shina
		// .getText()) + 1) + "");
		// instance.CommitShare(newsid, SHARE_SINA, ShareListener,
		// ShareErrorListener);
		// } else if (platform == SHARE_MEDIA.QQ) {
		// content_share_qq.setText((Integer
		// .parseInt((String) content_share_qq.getText()) + 1) + "");
		// instance.CommitShare(newsid, SHARE_QQ, ShareListener,
		// ShareErrorListener);
		// } else if (platform == SHARE_MEDIA.WEIXIN) {
		// content_share_weixin.setText((Integer
		// .parseInt((String) content_share_weixin.getText()) + 1)
		// + "");
		// instance.CommitShare(newsid, SHARE_WIEXIN, ShareListener,
		// ShareErrorListener);
		// } else if (platform == SHARE_MEDIA.EMAIL) {
		// content_share_mail.setText((Integer
		// .parseInt((String) content_share_mail.getText()) + 1) + "");
		// instance.CommitShare(newsid, SHARE_EMAIL, ShareListener,
		// ShareErrorListener);
		// }
		// instance.CommitShare(mApplication.getUser().getUid(), newsid,
		// SHARE_SINA, ShareListener,
		// ShareErrorListener);
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
		if (video_view.getCurrentPosition() > 0) {
			Bitmap currentFrame = getCurrentFrame();
			video_controller.getContent_video_temp_image().setVisibility(
					View.VISIBLE);
			video_controller.getContent_video_temp_image().setImageBitmap(
					currentFrame);
		}

		video_view.pause();
		isPause = true;
		// }
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		isPause = false;
	}

	private Bitmap getCurrentFrame() {
		try {
			Bitmap currentFrame = video_view.getCurrentFrame();
			return currentFrame;
		} catch (OutOfMemoryError e) {
			imageLoader.clearMemoryCache();
			System.gc();
			getCurrentFrame();
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// handler.removeMessages(1);
		unregisterReceiver(mReceiver);
		video_view.release(true);
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

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		ToastUtils.Errortoast(mContext, "视频播放有误,请稍候重试");
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (isFullScrenn) {
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
					}
				}, 3000);
			}
		}
		isCom = true;
		resetVideo(false);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		hasBeenPaly = true;
	}

	// 播放完成回到初始状态
	private void resetVideo(boolean isResetpalyer) {
		content_video.setVisibility(View.VISIBLE);
		video_player.setVisibility(View.VISIBLE);
		video_pb.setVisibility(View.GONE);
		small_video_pb.setVisibility(View.GONE);
		hasBeenPaly = true;
		// if (new_news != null) {
		// imageLoader.displayImage(new_news.getTitlepic(), content_video,
		// Options.getSmallImageOptions());// 视频
		// }
		if (isResetpalyer) {
			video_view.release(true);
		}
	}

	// 播放视频状态
	private void playState() {
		video_pb.setVisibility(View.VISIBLE);
		small_video_pb.setVisibility(View.VISIBLE);
		video_player.setVisibility(View.GONE);
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {

		switch (what) {

		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			video_view.pause();
			isload = true;
			if (!noShowPB) {
				video_pb.setVisibility(View.VISIBLE);
				small_video_pb.setVisibility(View.VISIBLE);
				video_controller.setEnabled(false);
				noShowPB = false;
			}
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:

			if (!New_Activity_Content_Video.this.isPause) {
				if (content_video.getVisibility() == View.VISIBLE) {
					content_video.setVisibility(View.GONE);
				}
				isload = false;
				if (spUtil.getFirstFull() && isFullScrenn) {
					isPlayer = false;
					player_guide.setVisibility(View.VISIBLE);
					noShowPB = true;
				} else {
					video_view.start();
				}
				video_controller.setEnabled(true);
				goneContentVideoTempImage();
				video_pb.setVisibility(View.GONE);
				small_video_pb.setVisibility(View.GONE);

			} else {
				if (content_video.getVisibility() == View.VISIBLE) {
					content_video.setVisibility(View.GONE);
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
		// TODO Auto-generated method stub
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

	// sina 分享
	/**
	 * 获取当前新闻的缩略图对应的 Bitmap。
	 */
	public Bitmap getThumbBitmap() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		// Bitmap decodeFile = BitmapFactory
		// .decodeFile(
		// CommonUtils.getImageCachePath(mContext)
		// .getAbsolutePath()
		// + "/"
		// + String.valueOf(new_news.getTitlepic()
		// .hashCode()), options);
		// int byteCount = decodeFile.getByteCount();
		// int height = decodeFile.getHeight();
		// long memeory=byteCount*height;
		// int width = options.outWidth;
		// int height = options.outHeight;
		//
		// if (width > height) {
		// options.inSampleSize = width / 400;
		// } else {
		// options.inSampleSize = height / 400;
		// }
		// options.inJustDecodeBounds = false;
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

	/**
	 * 创建多媒体（视频）消息对象。
	 * 
	 * @return 多媒体（视频）消息对象。
	 */
	private VideoObject getVideoObj() {
		// 创建媒体消息
		VideoObject videoObject = new VideoObject();
		videoObject.identify = Utility.generateGUID();
		videoObject.title = new_news.getTitle();
		videoObject.description = new_news.getTitle();

		// 设置 Bitmap 类型的图片到视频对象里
		videoObject.setThumbImage(getThumbBitmap());
		videoObject.actionUrl = new_news.getVideourl();
		videoObject.dataUrl = "www.weibo.com";
		videoObject.dataHdUrl = "www.weibo.com";
		videoObject.duration = 10;
		videoObject.defaultText = "Vedio 默认文案";
		return videoObject;
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
						// TODO Auto-generated method stub
						Oauth2AccessToken newToken = Oauth2AccessToken
								.parseAccessToken(bundle);
						AccessTokenKeeper.writeAccessToken(
								getApplicationContext(), newToken);
						// Toast.makeText(
						// getApplicationContext(),
						// "onAuthorizeComplete token = "
						// + newToken.getToken(), 0).show();
					}

					@Override
					public void onCancel() {
					}
				});
	}

	private CustomShareBoard shareBoard;
	private boolean noShowPB;
	private boolean isload;

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
	/**
	 * 网络广播 当有网络的时候 刷新页面数据
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
				ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
				netchagetime++;
				if (info != null && info.isAvailable() && netchagetime > 1
						&& new_news != null) {
					content_loading.setVisibility(View.VISIBLE);
					initNetDate(new_news.getId(), new_news.getType());
				}
			}
		}
	};

	@Override
	public void refresh() {
		content_comment_list.setVisibility(View.GONE);
		content_comment_list_list.removeAllViews();
		content_loading.setVisibility(View.VISIBLE);
		if (CommonUtils.isNetworkAvailable(mContext)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			if (video_view.isPlaying()) {
				video_view.pause();
			}
			content_video.setVisibility(View.VISIBLE);
			goneContentVideoTempImage();
			video_view.release(true);
			video_view.setVideoURI(null);
			video_pb.setVisibility(View.GONE);
			small_video_pb.setVisibility(View.GONE);
			video_player.setVisibility(View.VISIBLE);
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
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.new_recommend_item, null);
			ImageView home_news_titlepic = (ImageView) v
					.findViewById(R.id.home_news_titlepic);
			TextView home_news_title = (TextView) v
					.findViewById(R.id.home_news_title);
			// TextView recommend_intro = (TextView)
			// v.findViewById(R.id.recommend_intro);
			TextView home_news_newstime = (TextView) v
					.findViewById(R.id.home_news_newstime);
			home_news_titlepic.setTag(R.string.viewwidth, PixelUtil.dp2px(100));
			CommonUtils.zoomImage(imageLoader, mNew_Recommend.getTitlepic(),
					home_news_titlepic, mContext);
			home_news_title.setText(mNew_Recommend.getTitle());
			home_news_newstime.setText(TimeUtil.unix2date(
					Long.valueOf(mNew_Recommend.getNewstime()), "yyyy-MM-dd"));

			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (video_view.isPlaying()) {
						video_view.pause();
					}
					content_video.setVisibility(View.VISIBLE);
					goneContentVideoTempImage();
					video_view.release(true);
					video_view.setVideoURI(null);
					video_pb.setVisibility(View.GONE);
					small_video_pb.setVisibility(View.GONE);
					video_player.setVisibility(View.VISIBLE);
					int news_type = Integer.valueOf(mNew_Recommend.getType());
					if (news_type % 10 == 1) {
						mContext.startAnimActivityByParameter(
								New_Activity_Content_Video.class,
								mNew_Recommend.getMid(),
								mNew_Recommend.getType(),
								mNew_Recommend.getTitleurl(),
								mNew_Recommend.getNewstime(),
								mNew_Recommend.getTitlepic(),
								mNew_Recommend.getTitle());
					} else if (news_type % 10 == 5) {
						// 专题
						mContext.startSubjectActivityByParameter(
								New_Avtivity_Subject.class,
								mNew_Recommend.getZtid(),
								mNew_Recommend.getTitle(),
								mNew_Recommend.getTitlepic(),
								mNew_Recommend.getTitleurl());
					} else if (news_type % 10 == 6) {// 直播
						New_LivePlayFragment fragment = (New_LivePlayFragment) ((MainActivity) mContext).fragments
								.get(1);
						fragment.setSelectPlay(true);
						fragment.setSelectPlayID(Integer
								.parseInt(mNew_Recommend.getZtid()));
						((MainActivity) mContext)
								.touchTab(((MainActivity) mContext).tab_two);

					} else {
						mContext.startAnimActivityByParameter(
								New_Activity_Content_Web.class,
								mNew_Recommend.getMid(),
								mNew_Recommend.getType(),
								mNew_Recommend.getTitleurl(),
								mNew_Recommend.getNewstime(),
								mNew_Recommend.getTitlepic(),
								mNew_Recommend.getTitle());
					}
				}
			});

			content_comment_list_list.addView(v);
		}
	}

}
