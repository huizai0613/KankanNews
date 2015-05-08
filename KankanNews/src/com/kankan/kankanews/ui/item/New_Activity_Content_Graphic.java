package com.kankan.kankanews.ui.item;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseVideoActivity;
import com.kankan.kankanews.bean.New_News;
import com.kankan.kankanews.bean.SuccessMsg;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.dialog.TishiMsgHint;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.sina.AccessTokenKeeper;
import com.kankan.kankanews.sina.Constants;
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.interfacz.ScrollViewListener;
import com.kankan.kankanews.ui.view.AutoImageTag;
import com.kankan.kankanews.ui.view.AutoImageVIew;
import com.kankan.kankanews.ui.view.CustomShareBoard;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.StickyScrollView;
import com.kankan.kankanews.ui.view.VideoViewController;
import com.kankan.kankanews.ui.view.VideoViewController.ControllerType;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.HttpHandler;
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
import com.umeng.socialize.bean.SHARE_MEDIA;

@SuppressLint({ "NewApi", "HandlerLeak" })
@SuppressWarnings({ "deprecation", "rawtypes" })
public class New_Activity_Content_Graphic extends BaseVideoActivity implements
		IWeiboHandler.Response, OnClickListener, OnPreparedListener,
		OnCompletionListener, OnErrorListener, OnInfoListener {
	private String mid;
	private String type;
	private String titleurl;
	private String newstime;
	private String titlepiclist;
	private String titlelist;

	/** 微博微博分享接口实例 */
	private IWeiboShareAPI mWeiboShareAPI = null;
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

	private WindowManager wm;

	// 分享类
	private ShareUtil shareUtil;

	private New_News new_news;
	private ItnetUtils instance;
	private LayoutInflater inflater;

	private New_News localnew_news;// 本地数据

	private RelativeLayout content_loading; // 等待loading
	private RelativeLayout rootview; // 等待loading
	private StickyScrollView scollView;

	// 新闻
	private MyTextView content_title;// 新闻标题
	private MyTextView content_time;// 发表时间
	private ImageView content_video;// 新闻视频
	private MyTextView content_intro;

	// 分享数据
	private LinearLayout content_share_shina_layout;
	private LinearLayout content_share_qq_layout;
	private LinearLayout content_share_weixin_layout;
	private LinearLayout content_share_mail_layout;

	private AudioManager mAM;

	// 播放组件
	private VideoViewController curVideoController;
	private ImageView curVideoPlayer;
	private VideoView curVideoView;
	private RelativeLayout curSmallrootview;

	// 播放器数组
	private ArrayList<VideoBoxHolder> viewVideoBoxs;

	private ArrayList<AutoImageVIew> imageViewBoxs;

	private View full_screen_guide;
	private View player_guide;
	private HttpHandler downloadVideo;

	private String videoUrl;

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
		if (curVideoView != null) {
			if (width > height) {
				if (shareBoard != null && shareBoard.isShowing()) {
					shareBoard.dismiss();
				}
				curVideoView.setFull(true);
				curVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
				CommonUtils.clickevent(mContext, "action", "放大",
						AndroidConfig.video_fullscreen_event);

				full_screen_guide.setVisibility(View.GONE);
				spUtil.setFirstContent(false);
				if (curVideoView.isPlaying()) {
					curVideoView.pause();
				}
				if (isload || spUtil.getFirstFull()) {
					isPlayer = false;
				} else {
					isPlayer = true;
				}
				if (curVideoView.getmUri() == null) {
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
					smallWidth = curVideoView.getWidth();
				}

				isFullScrenn = true;
				setRightFinsh(false);
				content_video.setVisibility(View.GONE);
				curVideoController
						.setmControllerType(ControllerType.FullScrennController);

				attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
				getWindow().setAttributes(attrs);
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

				curSmallrootview.removeView(curVideoView);
				curSmallrootview.removeView(curVideoController);

				rootview.setVisibility(View.VISIBLE);
				scollView.setVisibility(View.GONE);

				curVideoView.setOrentation(true);
				rootview.addView(curVideoView, 0);
				rootview.addView(curVideoController, 1,
						new RelativeLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));
				curVideoController.changeView();
				if (isPlayer) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							isPlayer = false;
							if (curVideoView != null) {
								if (isCom) {
									playState();
									play();
									isCom = false;
								}
								curVideoView.start();
							}
							curVideoController.getContent_video_temp_image()
									.setVisibility(View.GONE);
						}
					}, 100);
				}
			} else {
				curVideoView.setFull(false);
				curVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
				if (curVideoView.isPlaying()) {
					curVideoView.pause();
					isPlayer = true;
				} else {
					curVideoController.getContent_video_temp_image()
							.setVisibility(View.VISIBLE);
					curVideoController.getContent_video_temp_image()
							.setImageBitmap(curVideoView.getCurrentFrame());
				}

				attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
				getWindow().setAttributes(attrs);
				// 取消全屏设置
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
				isFullScrenn = false;
				setRightFinsh(true);
				player_guide.setVisibility(View.GONE);
				curVideoController
						.setmControllerType(ControllerType.SmallController);
				rootview.setVisibility(View.GONE);
				scollView.setVisibility(View.VISIBLE);

				rootview.removeView(curVideoView);
				rootview.removeView(curVideoController);

				curVideoView.setOrentation(true);
				curSmallrootview.addView(curVideoView, 0);
				curSmallrootview.addView(curVideoController, 1);
				curVideoController.changeView();

				if (isPlayer) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							isPlayer = false;
							curVideoView.start();
							curVideoController.show();
						}
					}, 100);
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		mAM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		viewVideoBoxs = new ArrayList<VideoBoxHolder>();
		imageViewBoxs = new ArrayList<AutoImageVIew>();

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
		setContentView(R.layout.new_activity_content_video); // setContentView(R.layout.new_activity_content_graphic);

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

	private boolean isScoll;

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
		scollView = (StickyScrollView) findViewById(R.id.scollView);

		video_pb = (LinearLayout) findViewById(R.id.video_pb);
		small_video_pb = (LinearLayout) findViewById(R.id.small_video_pb);
		player_bg = (View) findViewById(R.id.player_bg);

		/** 知道界面 */
		full_screen_guide = findViewById(R.id.full_screen_guide);
		player_guide = findViewById(R.id.player_guide);
		content_video_layout = (LinearLayout) findViewById(R.id.content_video_layout);
		// 新闻
		content_title = (MyTextView) findViewById(R.id.content_title);
		content_time = (MyTextView) findViewById(R.id.content_time);

		content_intro = (MyTextView) findViewById(R.id.content_intro);

		// 视频
		content_video = (ImageView) findViewById(R.id.content_video);

		// 分享数据
		content_share_shina_layout = (LinearLayout) findViewById(R.id.content_share_shina_layout);
		content_share_qq_layout = (LinearLayout) findViewById(R.id.content_share_qq_layout);
		content_share_weixin_layout = (LinearLayout) findViewById(R.id.content_share_weixin_layout);
		content_share_mail_layout = (LinearLayout) findViewById(R.id.content_share_mail_layout);

		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

		content_title.setFocusable(true);
		content_title.setFocusableInTouchMode(true);
		content_title.requestFocus();

		scollView.setOnTouchListener(new OnTouchListener() {
			private int lastY = 0;
			private int touchEventId = -9983761;
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					View scroller = (View) msg.obj;

					if (msg.what == touchEventId) {
						if (lastY == scroller.getScrollY()) {

							new Thread() {
								public void run() {
									for (final AutoImageVIew image : imageViewBoxs) {
										BitmapDrawable drawable = (BitmapDrawable) image
												.getDrawable();
										int top = image.getTop();
										int bottom = image.getBottom();
										if (bottom > scollView.getScrollY()
												- image.getHeight()
												- mScreenHeight
												&& top < scollView.getScrollY()
														+ scollView.getHeight()
														+ mScreenHeight) {
											Boolean isrecy = (Boolean) image
													.getTag(R.string.image_isrecy);
											if (isrecy) {
												handler.post(new Runnable() {
													@Override
													public void run() {
														// TODO Auto-generated
														// method stub
														image.loadImage();
														image.setTag(
																R.string.image_isrecy,
																false);
													}
												});
											}
										} else {
											handler.post(new Runnable() {
												@Override
												public void run() {
													image.setImageResource(R.drawable.default_news_display);
													image.setTag(
															R.string.image_isrecy,
															true);
												}
											});

										}
									}
								};
							}.start();

							System.gc();
						} else {
							handler.sendMessageDelayed(handler.obtainMessage(
									touchEventId, scroller), 1);
							lastY = scroller.getScrollY();
						}
					}
				}
			};

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				isScoll = false;
				int eventAction = event.getAction();
				int y = (int) event.getRawY();
				switch (eventAction) {
				case MotionEvent.ACTION_UP:

					handler.sendMessageDelayed(
							handler.obtainMessage(touchEventId, v), 5);

					break;
				default:
					break;
				}
				return false;
			}

		});

	}

	@Override
	protected void initData() {
		// 初始化数据
		mGestureDetector = new GestureDetector(this, new MyGestureListener());

		// 获取上个页面传来的数据
		Intent intent = getIntent();
		mid = intent.getStringExtra("mid");
		type = intent.getStringExtra("type");
		titleurl = intent.getStringExtra("titleurl");
		newstime = intent.getStringExtra("newstime");
		titlepiclist = intent.getStringExtra("titlepiclist");
		titlelist = intent.getStringExtra("titlelist");

		new_news = new New_News();
		new_news.setId(mid);
		new_news.setType(type);
		new_news.setTitleurl(titleurl);
		new_news.setNewstime(newstime);
		new_news.setTitlepiclist(titlepiclist);
		new_news.setTitlelist(titlelist);

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

	private void initNetDate(String mid, String type) {
		instance.getNewNewsContent(mid, type, mListener, mErrorListener);
	}

	// 加载本地数据
	private boolean initLocalDate(String mid) {
		try {
			localnew_news = this.dbUtils.findFirst(Selector
					.from(New_News.class).where("id", "=", mid));
			if (localnew_news != null) {
				new_news = localnew_news;
				initContentData();
				content_loading.setBackgroundColor(Color
						.parseColor("#00000000"));
				return true;
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
						e.printStackTrace();
					}
				}

			}
		}.start();
	}

	@Override
	protected void setListener() {
		player_bg.setOnClickListener(this);
		player_bg.setOnClickListener(this);
		full_screen_guide.setOnClickListener(this);
		player_guide.setOnClickListener(this);

		// 分享
		content_share_shina_layout.setOnClickListener(this);
		content_share_qq_layout.setOnClickListener(this);
		content_share_weixin_layout.setOnClickListener(this);
		content_share_mail_layout.setOnClickListener(this);

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
		// 请求成功后展示数据
		// if (!new_news.equals(localnew_news)) {
		initContentData();
		// }
		// 初始化shareutil类
		shareUtil = new ShareUtil(new_news, mContext);
		content_loading.setVisibility(View.GONE);
		// 接着请求各种数据
		// instance.getNewsContentCountsData(newsid, NListener, NErrorListener);

		if (viewVideoBoxs.size() <= 0) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			full_screen_guide
					.setVisibility(spUtil.getFirstContent() ? View.VISIBLE
							: View.GONE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		}

	}

	@Override
	protected void onFailure(VolleyError error) {
		content_loading.setVisibility(View.GONE);
		if (!initLocalDate) {
			player_bg.setVisibility(View.VISIBLE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		ToastUtils.Errortoast(mContext, "网络不可用");
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
			if (shareUtil == null) {
				shareUtil = new ShareUtil(new_news, mContext);
			}
			break;
		}

		switch (v.getId()) {
		case R.id.com_title_bar_left_bt:
			onBackPressed();
			break;
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			// 一键分享
			CustomShareBoard shareBoard = new CustomShareBoard(this, shareUtil, this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.video_controller:
			ToastUtils.ErrorToastNoNet(mContext);
			if (!curVideoController.isShow()
					&& video_pb.getVisibility() != View.VISIBLE
					&& small_video_pb.getVisibility() != View.VISIBLE)
				curVideoController.show();
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
			playState();
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
			if (curVideoView != null) {
				if (isCom) {
					play();
					isCom = false;
				} else {
					curVideoView.start();
				}
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (isFullScrenn) {
			fullScrenntoSamll();
		} else {
			if (curVideoView != null) {
				curVideoView.release(true);
			}
			AnimFinsh();
		}
	}

	/**
	 * 为当前播放器添加事件监听
	 */
	public void setCurVideoListener() {
		curVideoView.setOnCompletionListener(this);
		curVideoView.setOnErrorListener(this);
		curVideoView.setOnPreparedListener(this);
		curVideoView.setOnInfoListener(this);
		curVideoPlayer.setOnClickListener(this);
	}

	private void setOtherVideoListener(final String url) {
		int size = viewVideoBoxs.size();
		for (int i = 0; i < size; i++) {
			final int _i = i;
			viewVideoBoxs.get(i).videoPlayer
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// 重置curVideo
							resetVideo(true);
							// 重绑curVideo
							curVideoController = viewVideoBoxs.get(_i).videoController;
							curVideoPlayer = viewVideoBoxs.get(_i).videoPlayer;
							curVideoView = viewVideoBoxs.get(_i).videoView;
							curSmallrootview = viewVideoBoxs.get(_i).smallrootview;
							video_pb = viewVideoBoxs.get(_i).videoPb;
							videoUrl = url;
							// curVideo监听
							setCurVideoListener();
							// curVideo播放
							CommonUtils.clickevent(mContext, "title",
									new_news.getTitle(),
									AndroidConfig.video_play_event);
							playState();
							playerVideo();
						}
					});
		}
	}

	/**
	 * 给页面添加数据(新闻内容)
	 */
	public void initContentData() {
		content_video_layout.removeAllViews();
		viewVideoBoxs.clear();
		imageViewBoxs.clear();
		Long newstime = Long.valueOf(new_news.getNewstime() != null
				&& !TextUtils.isEmpty(new_news.getNewstime()) ? new_news
				.getNewstime() : "0");
		String news_time = TimeUtil.unix2date(newstime, "yyyy-MM-dd HH:mm:ss");
		// 新闻
		content_title.setText(new_news.getTitlelist());
		content_time.setText(news_time);

		// content_filelength.setText(file_lenght);
		// cotent_onclick.setText(new_news.getOnc......());

		content_intro.setVisibility(View.GONE);
		content_video.setVisibility(View.GONE);

		LinearLayout.LayoutParams tvl = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		int dp2px = PixelUtil.dp2px(5);
		tvl.topMargin = dp2px;
		tvl.bottomMargin = dp2px;

		LinearLayout.LayoutParams ivl = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		ivl.topMargin = dp2px;
		ivl.bottomMargin = dp2px;

		LinearLayout.LayoutParams vdl = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (mScreenWidth / 16 * 9));

		vdl.topMargin = dp2px;
		vdl.bottomMargin = dp2px;

		ArrayList<String> nT = new_news.getNewsText();
		int size = nT.size();
		for (int i = 0; i < size; i++) {
			String text = nT.get(i);
			if (text.startsWith("http")) {
				String urlToFileFormat = CommonUtils.UrlToFileFormat(text);
				if (urlToFileFormat.equalsIgnoreCase("m3u8")) { // 视频地址
					LinearLayout inflate = (LinearLayout) inflater.inflate(
							R.layout.viedo_box, null);
					viewVideoBoxs.add(findVideoBox(inflate));
					inflate.setLayoutParams(vdl);
					content_video_layout.addView(inflate);
					inflate.setOnClickListener(this);
					text = text.replaceAll("\\\\", "");
					if (viewVideoBoxs.size() == 1) {
						VideoBoxHolder videoBoxHolder = viewVideoBoxs.get(0);
						curVideoController = videoBoxHolder.videoController;
						curVideoPlayer = videoBoxHolder.videoPlayer;
						curVideoView = videoBoxHolder.videoView;
						curSmallrootview = videoBoxHolder.smallrootview;
						video_pb = videoBoxHolder.videoPb;
						videoUrl = text;
						// 设置视频的寬高
						curVideoController
								.setLayoutParams(new RelativeLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,
										(int) (mScreenWidth / 16 * 9)));

						curVideoController
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										if (!curVideoController.isShow()
												&& video_pb.getVisibility() != View.VISIBLE
												&& small_video_pb
														.getVisibility() != View.VISIBLE)
											curVideoController.show();
									}
								});

						curVideoController.setPlayerControl(curVideoView);
						curVideoController.setActivity_Content(this);
						setCurVideoListener();
					} else {
						setOtherVideoListener(text);
					}
				} else { // 图片
					String[] split = text.split(":::");
					AutoImageVIew iv = new AutoImageVIew(this);
					imageViewBoxs.add(iv);
					iv.setBackgroundColor(Color.parseColor("#000000"));
					iv.setScaleType(ScaleType.FIT_XY);
					iv.setClickable(true);
					AutoImageTag tag = new AutoImageTag(split[0].replaceAll(
							"\\\\", ""), false);
					iv.setmAutoImageTag(tag);
					// if (imageViewBoxs.size() <= 3) {

					iv.setTag(R.string.image_isrecy, true);
					iv.setTag(R.string.viewwidth,
							mScreenWidth - PixelUtil.dp2px(20));

					if (imageViewBoxs.size() <= 3) {
						iv.loadImage();
						iv.setTag(R.string.image_isrecy, false);
					} else {
						iv.setBackgroundResource(R.drawable.default_news_display);
						iv.setTag(R.string.image_isrecy, true);
					}
					int w = Integer.parseInt(split[1]);
					int h = Integer.parseInt(split[2]);
					float whb = (float) (mScreenWidth - PixelUtil.dp2px(20))
							/ (float) w;
					ivl.height = (int) (h * whb);
					content_video_layout.addView(iv, ivl);
				}
			} else {
				MyTextView tv = new MyTextView(this);
				tv.setText(text);
				tv.setLineSpacing(7, 1.2f);
				tv.setTextSize(16);
				content_video_layout.addView(tv, tvl);
			}
		}

	}

	private VideoBoxHolder findVideoBox(View v) {

		VideoBoxHolder videoBoxHolder = new VideoBoxHolder();

		videoBoxHolder.smallrootview = (RelativeLayout) v
				.findViewById(R.id.smallrootview);
		videoBoxHolder.videoView = (VideoView) v.findViewById(R.id.video_view);
		videoBoxHolder.videoController = (VideoViewController) v
				.findViewById(R.id.video_controller);
		videoBoxHolder.videoPlayer = (ImageView) v
				.findViewById(R.id.video_player);
		videoBoxHolder.videoPb = (LinearLayout) v.findViewById(R.id.video_pb);
		videoBoxHolder.videoController
				.setPlayerControl(videoBoxHolder.videoView);
		videoBoxHolder.videoController.setTitle(new_news.getTitle());
		return videoBoxHolder;
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
		if (curVideoView != null) {
			if (curVideoView.getCurrentPosition() > 0) {
				Bitmap currentFrame = getCurrentFrame();
				curVideoController.getContent_video_temp_image().setVisibility(
						View.VISIBLE);
				curVideoController.getContent_video_temp_image()
						.setImageBitmap(currentFrame);
			}
			curVideoView.pause();
		}
		// }
		super.onPause();
	}

	private Bitmap getCurrentFrame() {
		try {
			Bitmap currentFrame = curVideoView.getCurrentFrame();
			return currentFrame;
		} catch (OutOfMemoryError e) {
			ImgUtils.imageLoader.clearMemoryCache();
			System.gc();
			getCurrentFrame();
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// handler.removeMessages(1);
		unregisterReceiver(mReceiver);
	}

	private View player_bg;
	private Animation animation;
	private boolean initLocalDate;

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

	}

	// 播放完成回到初始状态
	private void resetVideo(boolean isResetpalyer) {
		content_video.setVisibility(View.VISIBLE);
		curVideoPlayer.setVisibility(View.VISIBLE);
		video_pb.setVisibility(View.GONE);
		small_video_pb.setVisibility(View.GONE);
		if (isResetpalyer) {
			curVideoView.release(true);
		}
	}

	// 播放视频状态
	private void playState() {
		video_pb.setVisibility(View.VISIBLE);
		small_video_pb.setVisibility(View.VISIBLE);
		curVideoPlayer.setVisibility(View.GONE);
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			curVideoView.pause();
			isload = true;
			if (!noShowPB) {
				video_pb.setVisibility(View.VISIBLE);
				small_video_pb.setVisibility(View.VISIBLE);
				curVideoController.setEnabled(false);
				noShowPB = false;
			}
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (content_video.getVisibility() == View.VISIBLE) {
				content_video.setVisibility(View.GONE);
			}
			isload = false;
			if (spUtil.getFirstFull() && isFullScrenn) {
				isPlayer = false;
				player_guide.setVisibility(View.VISIBLE);
				noShowPB = true;
			} else {
				curVideoView.start();
			}
			curVideoController.setEnabled(true);
			goneContentVideoTempImage();
			video_pb.setVisibility(View.GONE);
			small_video_pb.setVisibility(View.GONE);
			break;
		}
		return true;
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
						ToastUtils.Infotoast(New_Activity_Content_Graphic.this, "分享成功");
					}

					@Override
					public void onCancel() {
						ToastUtils.Infotoast(New_Activity_Content_Graphic.this, "分享取消");
					}
				});
	}

	/**
	 * 获取当前新闻的缩略图对应的 Bitmap。
	 */
	private Bitmap getThumbBitmap() {
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
//			ToastUtils.Infotoast(mContext, "分享取消");
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			ToastUtils.Infotoast(mContext, "分享失败");
			break;
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

	private void goneContentVideoTempImage() {
		curVideoController.getContent_video_temp_image().setVisibility(
				View.GONE);
		BitmapDrawable drawable = (BitmapDrawable) curVideoController
				.getContent_video_temp_image().getDrawable();
		if (drawable != null) {
			Bitmap bmp = drawable.getBitmap();
			if (null != bmp && !bmp.isRecycled()) {
				bmp.recycle();
				bmp = null;
			}
		}
	}

	private void play() {
		isCom = false;
		curVideoView.release(true);
		curVideoController.reset();
		curVideoView.setVideoPath(videoUrl);
		curVideoView.requestFocus();
		curVideoView.start();
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
			float mOldX = e1.getX();
			int y = (int) e2.getRawY();
			int x = (int) e2.getRawX();

			if (Math.abs((y - fy)) < Math.abs((x - fx)) + 100) {
				onPlayerSeek(x - mOldX);
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}

		/** 滑动 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			float mOldY = e1.getY();
			int y = (int) e2.getRawY();
			int x = (int) e2.getRawX();

			Display disp = getWindowManager().getDefaultDisplay();
			int windowHeight = disp.getHeight();

			if (Math.abs((y - fy)) > Math.abs((x - fx)) + 100) {
				onVolumeSlide((mOldY - y) / windowHeight);
			}

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
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
		mSeek = curVideoView.getCurrentPosition();
		mMaxSeek = curVideoView.getDuration();

		long index = (long) (mSeek + msc);
		if (index > mMaxSeek)
			index = mMaxSeek;
		else if (index < 0)
			index = 0;

		curVideoView.seekTo(index);
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

	/** 定时隐藏 */
	private Handler mDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mVolumeBrightnessLayout.setVisibility(View.GONE);
		}
	};
	private LinearLayout content_video_layout;

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
							play();
							dialog.dismiss();
						}
					});
					dialog.show();

				}
			} else {
				curVideoController.getContent_video_temp_image().setVisibility(
						View.GONE);
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

	class VideoBoxHolder {
		RelativeLayout smallrootview;
		VideoView videoView;
		VideoViewController videoController;
		ImageView videoPlayer;
		LinearLayout videoPb;
	}

	@Override
	public void refresh() {
		content_loading.setVisibility(View.VISIBLE);
		if (CommonUtils.isNetworkAvailable(mContext))
			initNetDate(mid, type);
	}

}
