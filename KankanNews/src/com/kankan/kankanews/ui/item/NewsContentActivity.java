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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankan.kankanews.utils.StringUtils;
import com.kankanews.kankanxinwen.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

public class NewsContentActivity extends BaseVideoActivity implements
		OnClickListener, OnInfoListener, OnCompletionListener, OnErrorListener,
		OnPreparedListener {
	private final static int _SHOW_IMAGE_ = 1;
	private final static int _CHANGE_IMAGE_PROCESS_ = 2;
	private final static int _SHOW_VIDEO_ = 3;
	private NewsContent mContent;
	private WebView mContentWebView;
	private LinearLayout mLoadingView;
	private View mVideoRootView;
	private VideoView mVideoView;
	private String mNewsId;
	private String mNewsType;
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
				int scale = mContext.getResources().getDisplayMetrics().densityDpi / 160;
				int left = msg.getData().getInt("_OFFSETLEFT_") * scale;
				RelativeLayout.LayoutParams par = (LayoutParams) mVideoRootView
						.getLayoutParams();
				par.height = (int) ((mScreenWidth - left - left) / 16 * 9);
				mVideoView.setmRootViewHeight(par.height);
				par.setMargins(left, msg.getData().getInt("_OFFSETTOP_")
						* scale, left, 0);
				mVideoRootView.setLayoutParams(par);
				mVideoRootView.setVisibility(View.VISIBLE);
				playVideo(msg.getData().getString("_VIDEO_KEY_"));
				break;
			}
		}
	};

	private void playVideo(String videoKey) {
		final NewsContentVideo video = mContent.getConponents().getVideo()
				.get(videoKey);
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
		mVideoView.setUserAgent("KKApp");
		mContentWebView = (WebView) this.findViewById(R.id.content_web_view);

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
	}

	@Override
	protected void initData() {
		mNewsId = this.getIntent().getStringExtra("mid");
		mNewsType = this.getIntent().getStringExtra("type");
		refreshNetDate();
	}

	@Override
	protected void setListener() {
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnInfoListener(this);
		mVideoView.setOnErrorListener(this);
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
		mContent = JsonUtils.toObject(jsonObject.toString(), NewsContent.class);
		showData();
	}

	@Override
	protected void onFailure(VolleyError error) {
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onBackPressed() {
		AnimFinsh();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
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
		System.gc();
		super.finish();
	}

	@Override
	public void copy2Clip() {
	}

	@Override
	public void changeFontSize() {
	}

	@Override
	public void netChanged() {
	}

	public String initIntro() {
		if (this.mContent.getIntro() == null
				|| "".equals(this.mContent.getIntro().trim()))
			return "";
		String template = "<div class='line'></div><span class='tag'>核心提示</span><div class='tips-con'>%s</div>";
		return String.format(template, this.mContent.getIntro());
	}

	public String initContent() {
		StringBuffer buf = new StringBuffer();
		List<String> contents = StringUtils.splitString(mContent.getContents(),
				"</p>");
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
		NewsContentImage image = this.mContent.getConponents().getImage()
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
		String template = "<p style='text-align: center'><em><i></i><img id='%s' src='images/loading_0.png' /><span>%s</span></em></p>";
		return String.format(template, imageKey, image.getTitle());
	}

	private String initVideo(String content) {
		final String videoKey = StringUtils.cleanAnnotationTag(content);
		NewsContentVideo video = this.mContent.getConponents().getVideo()
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
						bundle.putString("_IMAGE_KEY_", videoKey);
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
						bundle.putString("_IMAGE_KEY_", videoKey);
						msg.setData(bundle);
						mHandle.sendMessage(msg);
					}
				});
		String template = "<p style='text-align: center'><img id='%s' src='images/loading_0.png' onclick=\"openVideo('%s')\" /><span>%s</span></p>";
		return String.format(template, videoKey, videoKey, video.getTitle());
	}

	public String initRecommend() {
		if (mContent.getRecommend().size() > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("<div class=\"line\"></div><div class=\"recommend\"><h2 class=\"tit\">热门推荐</h2>");
			for (NewsContentRecommend related : mContent.getRecommend()) {
				String template = "<div class=\"recomList\"><a href=\"#\" onclick=\"openNews('%s','%s')\"><h3 class=\"tit\">%s</h3><span class=\"other\"><em>%s</em></span></a></div>";
				buf.append(String.format(template, related.getId(),
						related.getType(), related.getTitle(),
						related.getNewsdate()));
			}
			buf.append("</div>");
			DebugLog.e(buf.toString());
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
		public String getTitle() {
			return NewsContentActivity.this.mContent.getTitle();
		}

		@JavascriptInterface
		public String getDate() {
			return NewsContentActivity.this.mContent.getNewsdate();
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
		public void openNews(String id, String type) {
			Intent intent = new Intent(NewsContentActivity.this,
					NewsContentActivity.class);
			intent.putExtra("mid", id);
			intent.putExtra("type", type);
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
		DebugLog.e("onPrepared");
		mVideoView.start();
	}

	@Override
	public boolean onError(IMediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public void onCompletion(IMediaPlayer mp) {

	}

	@Override
	public boolean onInfo(IMediaPlayer mp, int what, int extra) {
		switch (what) {
		case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
			mVideoView.pause();
			break;
		case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
			mVideoView.start();
			break;
		}
		return true;
	}
}
