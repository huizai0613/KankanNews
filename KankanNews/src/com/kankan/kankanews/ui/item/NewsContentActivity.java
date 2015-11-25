package com.kankan.kankanews.ui.item;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.widget.VideoView;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseVideoActivity;
import com.kankan.kankanews.bean.NewsContent;
import com.kankan.kankanews.bean.NewsContentImage;
import com.kankan.kankanews.bean.NewsContentRecommend;
import com.kankan.kankanews.bean.NewsContentVideo;
import com.kankan.kankanews.bean.NewsHomeModuleItem;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.ui.view.VideoViewController;
import com.kankan.kankanews.ui.view.VideoViewController.ControllerType;
import com.kankan.kankanews.ui.view.popup.CustomShareBoard;
import com.kankan.kankanews.ui.view.popup.FontColumsBoard;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.StringUtils;
import com.kankanews.kankanxinwen.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.umeng.socialize.sso.UMSsoHandler;

public class NewsContentActivity extends BaseVideoActivity implements
		OnClickListener, OnInfoListener, OnCompletionListener, OnErrorListener,
		OnPreparedListener {
	private final static int _SHOW_IMAGE_ = 1;
	private final static int _CHANGE_IMAGE_PROCESS_ = 2;
	private final static int _SHOW_VIDEO_ = 3;
	// 分享类
	private ShareUtil shareUtil;
	private NewsContent mNewsContent;
	private WebView mContentWebView;
	private LinearLayout mLoadingView;

	private View mVideoRootView;
	private VideoView mVideoView;
	private VideoViewController mVideoViewController;
	private ImageView mVideoViewBG;
	private ImageView mVideoPlayView;
	private View mVideoLodingView;

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
	private AudioManager mAM;
	private WindowManager wm;
	private View mVolumeBrightnessLayout;
	private ImageView mOperationPercent;

	private NewsHomeModuleItem mModuleItem;
	// 使用列表页传过来的
	private String mNewsId;
	private String mNewsType;
	private String mNewsTitle;
	private int mWebWidth = 0;

	private boolean isPause;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		if (width > height) {
			// if (shareBoard != null && shareBoard.isShowing()) {
			// shareBoard.dismiss();
			// }
			mVideoViewController
					.setmControllerType(ControllerType.FullScrennController);
			mVideoViewController.changeView();
			setRightFinsh(false);
			CommonUtils.clickevent(mContext, "action", "放大",
					AndroidConfig.video_fullscreen_event);
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			this.getWindow().setAttributes(attrs);
			this.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			mVideoRootView.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH);
			isFullScrenn = true;
			// ((RelativeLayout.LayoutParams)
			// mFullRootView.getLayoutParams()).topMargin = 0;
			if (mVideoView != null && mVideoView.getVisibility() == View.GONE)
				mVideoView.start();
			mVideoView.getHolder().setFixedSize(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

		} else {
			mVideoViewController
					.setmControllerType(ControllerType.SmallController);
			mVideoViewController.changeView();
			setRightFinsh(true);
			isFullScrenn = false;
			// ((RelativeLayout.LayoutParams)
			// mFullRootView.getLayoutParams()).topMargin = PixelUtil
			// .dp2px(44);
			// mActivity.bottomBarVisible(View.VISIBLE);
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			this.getWindow().setAttributes(attrs);
			// 取消全屏设置
			this.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			mVideoRootView.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT,
					(int) (this.mScreenWidth / 16 * 9)));
			mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE);
		}
	}

	private Handler mHandle = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case _SHOW_IMAGE_:
				mContentWebView
						.loadUrl("javascript:showImage('data:image/webp;base64,"
								+ msg.getData().getString("_IMAGE_CONTENT_")
								+ "', '"
								+ msg.getData().getString("_IMAGE_KEY_") + "')");
				break;
			case _CHANGE_IMAGE_PROCESS_:
				mContentWebView.loadUrl("javascript:changeImageProcess('"
						+ msg.getData().getString("_PROCESS_") + "', '"
						+ msg.getData().getString("_IMAGE_KEY_") + "')");
				break;
			case _SHOW_VIDEO_:
				String videoKey = msg.getData().getString("_VIDEO_KEY_");
				final NewsContentVideo video = mNewsContent.getConponents()
						.getVideo().get(videoKey);
				int displayScale = mContext.getResources().getDisplayMetrics().densityDpi / 160;
				int left = msg.getData().getInt("_OFFSETLEFT_") * displayScale;
				RelativeLayout.LayoutParams par = (LayoutParams) mVideoRootView
						.getLayoutParams();
				String scale = video.getDisplayscale();
				if (scale.equals("16:9"))
					par.height = mWebWidth * 9 / 16;
				if (scale.equals("4:3"))
					par.height = mWebWidth * 3 / 4;
				// par.height = (int) ((mScreenWidth - left - left) / 16 * 9);
				mVideoView.setmRootViewHeight(par.height);
				par.setMargins(left, msg.getData().getInt("_OFFSETTOP_")
						* displayScale, left, 0);
				mVideoRootView.setLayoutParams(par);
				mVideoRootView.setVisibility(View.VISIBLE);
				playVideo(video);
				break;
			}
		}
	};

	private void playVideo(final NewsContentVideo video) {
		mVideoLodingView.setVisibility(View.VISIBLE);
		mVideoViewBG.setVisibility(View.VISIBLE);
		mHandle.post(new Runnable() {
			@Override
			public void run() {
				mVideoView.setVideoPath(video.getVideourl());
			}
		});
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_content);
	}

	@Override
	protected void initView() {
		mLoadingView = (LinearLayout) this
				.findViewById(R.id.content_buffering_indicator);
		mVideoRootView = this.findViewById(R.id.content_video_root_view);
		mVideoView = (VideoView) this.findViewById(R.id.content_video_view);
		mVideoViewController = (VideoViewController) this
				.findViewById(R.id.content_video_controller);
		mVideoViewBG = (ImageView) this.findViewById(R.id.content_video_bg);
		video_pb = (LinearLayout) this
				.findViewById(R.id.content_video_loading_view);
		small_video_pb = (LinearLayout) this
				.findViewById(R.id.content_video_loading_view);
		mVideoViewBG = (ImageView) this.findViewById(R.id.content_video_bg);
		mVideoPlayView = (ImageView) this
				.findViewById(R.id.content_video_player);
		mVideoLodingView = this.findViewById(R.id.content_video_loading_view);
		mVideoView.setUserAgent("KKApp");
		mContentWebView = (WebView) this.findViewById(R.id.content_web_view);

		mAM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

		nightView = findViewById(R.id.night_view);

		// 允许JavaScript执行
		WebSettings webSettings = mContentWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		mContentWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mContentWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					android.webkit.JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});
		mContentWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mLoadingView.setVisibility(View.GONE);
			}
		});

		initTitleBarIcon(R.drawable.ic_share, R.drawable.new_ic_back,
				R.drawable.ic_close_white, R.drawable.ic_font,
				R.drawable.ic_refresh);
	}

	@Override
	protected void initData() {
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		mVideoViewController.setPlayerControl(mVideoView);
		mVideoViewController.setActivity_Content(this);
		mVideoView.setIsNeedRelease(false);
		mModuleItem = (NewsHomeModuleItem) this.getIntent()
				.getSerializableExtra("_NEWS_HOME_MODEULE_ITEM_");
		if (mModuleItem == null) {
			mNewsId = this.getIntent().getStringExtra("mid");
			mNewsType = this.getIntent().getStringExtra("type");
			mNewsTitle = this.getIntent().getStringExtra("title");
		} else {
			mNewsId = mModuleItem.getO_cmsid();
			mNewsType = mModuleItem.getType();
			mNewsTitle = mModuleItem.getTitle();
		}
		refreshNetDate();
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

	/** 手势结束 */
	private void endGesture() {
		mVolume = -1;
		// 隐藏
		mDismissHandler.removeMessages(0);
		mDismissHandler.sendEmptyMessageDelayed(0, 1000);
	}

	@Override
	protected void setListener() {
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnInfoListener(this);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnClickListener(this);
		mVideoViewController.setOnClickListener(this);

		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);
		setOnContentClickLinester(this);
	}

	protected void refreshNetDate() {
		if (CommonUtils.isNetworkAvailable(this)) {
			DebugLog.e(mNewsId + " " + mNewsType);
			netUtils.getNewsContent(mNewsId, mNewsType, this.mListener,
					this.mErrorListener);
		}
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		mNewsContent = JsonUtils.toObject(jsonObject.toString(),
				NewsContent.class);
		shareUtil = new ShareUtil(mNewsContent, mContext);
		showData();
	}

	@Override
	protected void onFailure(VolleyError error) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		case R.id.title_bar_content_img:
			// 一键分享
			if (shareUtil == null)
				return;
			CustomShareBoard shareBoard = new CustomShareBoard(this, shareUtil,
					this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.title_bar_right_second_img:
			this.refresh();
			break;
		case R.id.title_bar_right_img:
			FontColumsBoard fontBoard = new FontColumsBoard(this);
			fontBoard.setAnimationStyle(R.style.popwin_anim_style);
			fontBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.content_video_controller:
			if (!mVideoViewController.isShow())
				mVideoViewController.show();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		AnimFinsh();
	}

	@Override
	protected void onPause() {
		super.onPause();
		isPause = true;
		if (this.mVideoView != null)
			this.mVideoView.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isPause = false;
	}

	public void showData() {
		mContentWebView.loadUrl("file:///android_asset/newsTemplate.html");
		mContentWebView.addJavascriptInterface(new News(), "news");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void finish() {
		closeVideo();
		System.gc();
		super.finish();
	}

	@Override
	public void copy2Clip() {
	}

	@Override
	public void changeFontSize() {
		int index = FontUtils.getFontSetIndex(spUtil.getFontSizeRadix());
		mContentWebView.loadUrl("javascript:changeFontSize('font_"
				+ FontUtils.fontSizeWeb[index] + "')");
		FontUtils.chagneFontSizeGlobal();
	}

	@Override
	public void netChanged() {
	}

	private void closeVideo() {
		if (this.mVideoView != null) {
			this.mVideoView.pause();
			this.mVideoView.stopPlayback();
		}
	}

	public String initAuthor() {
		if (this.mNewsContent.getJournalist_name() == null
				|| "".equals(this.mNewsContent.getJournalist_name().trim())
				|| Integer.parseInt(this.mNewsContent.getJournalist_id()) <= 0)
			return "";
		String template = "<div class=\"portrait\"><img src=\"%s\"></div><p>%s<i>%s</i></p>";
		return String.format(template, this.mNewsContent.getJournalist_pic(),
				this.mNewsContent.getJournalist_name(),
				this.mNewsContent.getJournalist_intro());
	}

	public String initIntro() {
		if (this.mNewsContent.getIntro() == null
				|| "".equals(this.mNewsContent.getIntro().trim())
				|| mNewsType.equals("video"))
			return "";
		String template = "<div class='line'></div><span class='tag'>核心提示</span><div class='tips-con'>%s</div>";
		return String.format(template, this.mNewsContent.getIntro());
	}

	public String initContent() {
		StringBuffer buf = new StringBuffer();
		List<String> contents = StringUtils.splitString(
				mNewsContent.getContents(), "</p>");
		for (String paragraph : contents) {
			String content = StringUtils.deleteTag(paragraph, "p").trim();
			if (content.startsWith("<!--IMAGE_")) {
				buf.append(initImage(content));
				continue;
			} else if (content.startsWith("<!--VIDEO_")) {
				buf.append(initVideo(content));
				continue;
			} else
				buf.append(paragraph);
		}
		return buf.toString();
	}

	private String initImage(String content) {
		final String imageKey = StringUtils.cleanAnnotationTag(content);
		NewsContentImage image = this.mNewsContent.getConponents().getImage()
				.get(imageKey);
		ImageSize imageSize = new ImageSize(image.getWidth(), image.getHeight());
		ImgUtils.imageLoader.loadImage(image.getImageurl(), imageSize,
				ImgUtils.homeImageOptions, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						String base = bitmaptoString(loadedImage);
						Message msg = new Message();
						msg.what = _SHOW_IMAGE_;
						Bundle bundle = new Bundle();
						bundle.putString("_IMAGE_CONTENT_", base);
						bundle.putString("_IMAGE_KEY_", imageKey);
						msg.setData(bundle);
						mHandle.sendMessage(msg);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {
						int process = (int) (Math.floor((double) current * 10
								/ total));
						if (process > 9)
							return;
						Message msg = new Message();
						msg.what = _CHANGE_IMAGE_PROCESS_;
						Bundle bundle = new Bundle();
						bundle.putString("_PROCESS_", "" + process);
						bundle.putString("_IMAGE_KEY_", imageKey);
						msg.setData(bundle);
						mHandle.sendMessage(msg);
					}
				});
		int imgHeight = this.calcuHeight(image.getWidth(), image.getHeight())
				/ PixelUtil.getScale();
		DebugLog.e(image.getWidth() + " " + image.getHeight() + " " + imgHeight
				+ "");
		String template = "<p style='text-align: center'><em><i></i><img id='%s' height='%s' width='%s' src='images/loading_0.png' /><span>%s</span></em></p>";
		return String.format(template, imageKey, imgHeight, this.mWebWidth,
				image.getTitle());
	}

	private String initVideo(String content) {
		final String videoKey = StringUtils.cleanAnnotationTag(content);
		NewsContentVideo video = this.mNewsContent.getConponents().getVideo()
				.get(videoKey);
		ImgUtils.imageLoader.loadImage(video.getTitlepic(), null,
				ImgUtils.homeImageOptions, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						String base = bitmaptoString(loadedImage);
						Message msg = new Message();
						msg.what = _SHOW_IMAGE_;
						Bundle bundle = new Bundle();
						bundle.putString("_IMAGE_CONTENT_", base);
						bundle.putString("_IMAGE_KEY_", videoKey
								+ "_video_image");
						msg.setData(bundle);
						mHandle.sendMessage(msg);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {
						int process = (int) (Math.floor((double) current * 10
								/ total));
						if (process > 9)
							return;
						Message msg = new Message();
						msg.what = _CHANGE_IMAGE_PROCESS_;
						Bundle bundle = new Bundle();
						bundle.putString("_PROCESS_", "" + process);
						bundle.putString("_IMAGE_KEY_", videoKey
								+ "_video_image");
						msg.setData(bundle);
						mHandle.sendMessage(msg);
					}
				});
		int videoImageH = 0;
		int imageH = this.mWebWidth * 3 / 4 / PixelUtil.getScale();
		String scale = video.getDisplayscale();
		if (scale.equals("16:9"))
			videoImageH = this.mWebWidth * 9 / 16 / PixelUtil.getScale();
		if (scale.equals("4:3"))
			videoImageH = this.mWebWidth * 3 / 4 / PixelUtil.getScale();
		String template = "<em id=\"%s\" class=\"video\" style=\"height:%spx\"><img id=\"%s\" width=\"%spx\" height=\"%spx\" src='images/small_no_loading.png' onclick=\"openVideo('%s')\"/><i><img src=\"images/ic_liveplay.png\" onclick=\"openVideo('%s')\"/></i></em>";
		return String.format(template, videoKey, videoImageH, videoKey
				+ "_video_image", this.mWebWidth, imageH, videoKey, videoKey);
	}

	public String initRecommend() {
		if (mNewsContent.getRecommend().size() > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("<div class=\"line\"></div><div class=\"recommend\"><h2 class=\"tit\">热门推荐</h2>");
			for (NewsContentRecommend related : mNewsContent.getRecommend()) {
				String template = "<div class=\"recomList\"><a href=\"#\" onclick=\"openNews('%s','%s','%s')\"><h3 class=\"tit\">%s</h3><span class=\"other\"><em>%s</em></span></a></div>";
				buf.append(String.format(template, related.getId(),
						related.getType(), related.getTitle(),
						related.getTitle(), related.getNewsdate()));
			}
			buf.append("</div>");
			return buf.toString();
		}
		return "";
	}

	public String bitmaptoString(Bitmap bitmap) {
		// 将Bitmap转换成Base64字符串
		StringBuffer string = new StringBuffer();
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();

		try {
			bitmap.compress(CompressFormat.JPEG, 100, bStream);
			bStream.flush();
			bStream.close();
			byte[] bytes = bStream.toByteArray();
			string.append(Base64.encodeToString(bytes, Base64.NO_WRAP));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return string.toString();
	}

	public final class News {
		News() {

		}

		@JavascriptInterface
		public void setWebWidth(int width) {
			// NewsContentActivity.this.mWebWidth =
			// NewsContentActivity.this.mScreenWidth
			// - width * PixelUtil.getScale() * 2;
			NewsContentActivity.this.mWebWidth = (int) (NewsContentActivity.this.mScreenWidth * 0.9);
			DebugLog.e(mWebWidth + "");
		}

		@JavascriptInterface
		public String initFontSize() {
			int index = FontUtils.getFontSetIndex(spUtil.getFontSizeRadix());
			return "font_" + FontUtils.fontSizeWeb[index];
		}

		@JavascriptInterface
		public String getTitle() {
			return NewsContentActivity.this.mNewsTitle;
		}

		@JavascriptInterface
		public String getDate() {
			return NewsContentActivity.this.mNewsContent.getNewsdate();
		}

		@JavascriptInterface
		public String getAuthor() {
			return initAuthor();
		}

		@JavascriptInterface
		public String getIntro() {
			// 视频不加简介
			return initIntro();
		}

		@JavascriptInterface
		public String getContent() {
			return initContent();
		}

		@JavascriptInterface
		public String getRecommend() {
			return initRecommend();
		}

		@JavascriptInterface
		public void openVideo(final String videoKey, final int left,
				final int top) {
			Message msg = new Message();
			msg.what = NewsContentActivity._SHOW_VIDEO_;
			Bundle bundle = new Bundle();
			bundle.putString("_VIDEO_KEY_", videoKey);
			bundle.putInt("_OFFSETLEFT_", left);
			bundle.putInt("_OFFSETTOP_", top);
			msg.setData(bundle);
			mHandle.sendMessage(msg);
		}

		@JavascriptInterface
		public void openNews(String id, String type, String title) {
			Intent intent = new Intent(NewsContentActivity.this,
					NewsContentActivity.class);
			intent.putExtra("mid", id);
			intent.putExtra("type", type);
			intent.putExtra("title", title);
			NewsContentActivity.this.startActivity(intent);
		}

		@JavascriptInterface
		public void showtoast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
		}

		@JavascriptInterface
		public void showBody(String body) {
			DebugLog.e(body);
		}
	}

	@Override
	public void onPrepared(IMediaPlayer mp) {
		mVideoView.start();
		mVideoViewBG.setVisibility(View.GONE);
		mVideoLodingView.setVisibility(View.GONE);
	}

	@Override
	public boolean onError(IMediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public void onCompletion(IMediaPlayer mp) {
		this.mVideoView.stopPlayback();
		this.mVideoRootView.setVisibility(View.GONE);
	}

	@Override
	public boolean onInfo(IMediaPlayer mp, int what, int extra) {
		switch (what) {
		case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
			mVideoView.pause();
			mVideoLodingView.setVisibility(View.VISIBLE);
			mVideoViewController.setEnabled(false);
			break;
		case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
			mVideoView.start();
			mVideoViewController.setEnabled(true);
			mVideoLodingView.setVisibility(View.GONE);
			mVideoViewBG.setVisibility(View.GONE);
			break;
		}
		return true;
	}

	private int calcuHeight(int originalWidth, int originalheight) {
		return this.mWebWidth * originalheight / originalWidth;
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
		mSeek = mVideoView.getCurrentPosition();
		mMaxSeek = mVideoView.getDuration();

		long index = (long) (mSeek + msc);
		if (index > mMaxSeek)
			index = mMaxSeek;
		else if (index < 0)
			index = 0;

		mVideoView.seekTo(index);
	}

	/** 定时隐藏 */
	private Handler mDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mVolumeBrightnessLayout.setVisibility(View.GONE);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (shareUtil != null) {
			UMSsoHandler ssoHandler = shareUtil.getmController().getConfig()
					.getSsoHandler(requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}
	}
}
