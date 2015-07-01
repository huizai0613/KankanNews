package com.kankan.kankanews.ui.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.widget.VideoView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.New_LivePlay;
import com.kankan.kankanews.bean.interfaz.CanSharedBySina;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.dialog.TishiMsgHint;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.receiver.AlarmReceiver;
import com.kankan.kankanews.sina.AccessTokenKeeper;
import com.kankan.kankanews.sina.Constants;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.view.CustomShareBoard;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.VideoViewController;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.SharePreferenceUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankan.kankanews.utils.XunaoLog;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class New_LivePlayFragment extends BaseFragment implements
		OnInfoListener, OnCompletionListener, OnErrorListener, OnClickListener,
		OnPreparedListener, CanSharedBySina {

	public SharePreferenceUtil spUtil;
	private View inflate;
	private RelativeLayout smallrootview;
	private RelativeLayout rootview;
	private VideoView liveVideoView;
	// private VideoViewController liveVideoController;
	private View livePause;
	private View main_bg;
	private ImageView liveStart;
	private ImageView fullLiveStart;
	private ImageView smallscrenn_but;
	private ImageView liveVideoImage;
	private MyTextView livePlayTitle;
	private MyTextView fullScreenLivePlayTitle;
	private LinearLayout mVideoLoadingLayout;
	private LinearLayout fullScreenLayout;
	private LinearLayout screnn_pb;
	private ImageView fullscrenn_but;
	private ArrayList<New_LivePlay> mLivePlayList = new ArrayList<New_LivePlay>();
	private MyAdapter myAdapter;
	private ImageView liveShareBut;

	private static final int BUFFER_START = 11;
	private static final int BUFFER_PROGRESS = 12;
	private static final int BUFFER_COMPLETE = 13;

	private boolean isFullstate;
	private boolean isFullShowLayout = false;

	private int curPosition;
	private boolean isShow;
	private boolean isSelectPlay;
	private int selectPlayID;
	public boolean isFirst = true;
	private AlarmManager manager;
	private List<New_LivePlay> localDate;

	private New_LivePlay nowLiveNew;

	private static CustomShareBoard shareBoard;

	private ShareUtil shareUtil = null;

	private OrientationEventListener mOrientationListener; // 屏幕方向改变监听器
	private int startRotation;

	private static final int HIDE_VIDEO_CONTROLLER = 1;

	private static final int FULL_SCREEN_CHANGE = 2;

	private static final int hideTimeOut = 5000;

	public Handler orientationHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HIDE_VIDEO_CONTROLLER:
				fullScreenLayout.setVisibility(View.GONE);
				fullLiveStart.setVisibility(View.GONE);
				isFullShowLayout = false;
				break;
			case FULL_SCREEN_CHANGE:
				startRotation = -2;
				mOrientationListener.enable();
				break;
			}
		};
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int width = mActivity.wm.getDefaultDisplay().getWidth();
		int height = mActivity.wm.getDefaultDisplay().getHeight();
		WindowManager.LayoutParams attrs = mActivity.getWindow()
				.getAttributes();
		if (width > height) {
			// 全屏
			if (shareBoard != null && shareBoard.isShowing()) {
				shareBoard.dismiss();
			}
			fullscrenn_but.setVisibility(View.GONE);
			mActivity.bottomBarVisible(View.GONE);
			liveStart.setVisibility(View.GONE);
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			mActivity.getWindow().setAttributes(attrs);
			mActivity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			smallrootview.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			liveVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH);
			isFullstate = true;
			updateFullStartBut(true);
			if (!liveVideoView.isPlaying()) {
				fullScreenLayout.setVisibility(View.VISIBLE);
				fullLiveStart.setVisibility(View.VISIBLE);
				orientationHandler.sendEmptyMessageDelayed(
						HIDE_VIDEO_CONTROLLER, hideTimeOut);
			}
			liveVideoView.start();
			liveVideoView.getHolder().setFixedSize(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
		} else {
			fullscrenn_but.setVisibility(View.VISIBLE);
			fullScreenLayout.setVisibility(View.GONE);
			fullLiveStart.setVisibility(View.GONE);
			if (!liveVideoView.isPlaying())
				liveStart.setVisibility(View.VISIBLE);
			isFullShowLayout = false;
			mActivity.bottomBarVisible(View.VISIBLE);
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			mActivity.getWindow().setAttributes(attrs);
			// 取消全屏设置
			mActivity.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			smallrootview.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT,
					(int) (mActivity.mScreenWidth / 16 * 9)));
			isFullstate = false;
			liveVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isFirst && mActivity.curTouchTab == mActivity.tab_two) {
			if (CommonUtils.isNetworkAvailable(mActivity)) {

				refreshNetDate();
				isFirst = true;
			} else {
				ToastUtils.ErrorToastNoNet(mActivity);
				screnn_pb.setVisibility(View.GONE);
				main_bg.setVisibility(View.VISIBLE);
			}
		} else {
			// isFirst = false;
			if (liveVideoView != null) {
				liveVideoView.start();
				liveStart.setVisibility(View.GONE);
			}
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		spUtil = this.mActivity.mApplication.getSpUtil();
		initView();
		initDate();
		initViewLayout();
		initLinsenter();
		initLocalDate();

		if (CommonUtils.isNetworkAvailable(mActivity)) {
			refreshNetDate();
			isFirst = true;
		} else {
			screnn_pb.setVisibility(View.GONE);
			main_bg.setVisibility(View.VISIBLE);
		}

		return inflate;
	}

	public void initView() {
		manager = (AlarmManager) mActivity
				.getSystemService(Context.ALARM_SERVICE);
		inflate = inflater.inflate(R.layout.new_fragment_liveplay, null);
		smallrootview = (RelativeLayout) inflate
				.findViewById(R.id.smallrootview);
		rootview = (RelativeLayout) inflate.findViewById(R.id.rootview);
		liveVideoView = (VideoView) inflate.findViewById(R.id.live_video_view);

		livePause = inflate.findViewById(R.id.live_pause);
		main_bg = inflate.findViewById(R.id.main_bg);
		liveStart = (ImageView) inflate.findViewById(R.id.live_start);
		fullLiveStart = (ImageView) inflate.findViewById(R.id.full_live_start);
		liveVideoImage = (ImageView) inflate
				.findViewById(R.id.live_video_image);
		livePlayTitle = (MyTextView) inflate.findViewById(R.id.livePlayTitle);
		fullScreenLivePlayTitle = (MyTextView) inflate
				.findViewById(R.id.fullScreenLivePlayTitle);
		// liveVideoController = (VideoViewController) inflate
		// .findViewById(R.id.live_video_controller);
		mVideoLoadingLayout = (LinearLayout) inflate
				.findViewById(R.id.buffering_indicator);
		mVideoLoadingLayout.bringToFront();
		liveVideoView.setMediaBufferingIndicator(mVideoLoadingLayout);
		fullScreenLayout = (LinearLayout) inflate
				.findViewById(R.id.fullscreen_layout);
		liveVideoView.setUserAgent("KKApp");
		screnn_pb = (LinearLayout) inflate.findViewById(R.id.screnn_pb);
		fullscrenn_but = (ImageView) inflate.findViewById(R.id.fullscrenn_but);
		smallscrenn_but = (ImageView) inflate
				.findViewById(R.id.smallscrenn_but);
		liveShareBut = (ImageView) inflate.findViewById(R.id.live_share_but);
		listview = (PullToRefreshListView) inflate.findViewById(R.id.listview);

		initListView(Mode.PULL_DOWN_TO_REFRESH);
	}

	public void initDate() {
		listview.setAdapter(new MyAdapter());
		// liveVideoController.setPlayerControl(liveVideoView);
		// liveVideoController.setActivity_Content(this.mActivity);
	}

	public void initViewLayout() {
		smallrootview.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				(int) (mActivity.mScreenWidth / 16 * 9)));
	}

	public void initLinsenter() {
		liveVideoView.setOnErrorListener(this);
		// liveVideoView.setOnCompletionListener(this);
		liveVideoView.setOnPreparedListener(this);
		// liveVideoView.setOnInfoListener(this);
		// liveVideoView.setOnClickListener(this);
		livePause.setOnClickListener(this);
		liveStart.setOnClickListener(this);
		fullLiveStart.setOnClickListener(this);
		fullscrenn_but.setOnClickListener(this);
		smallscrenn_but.setOnClickListener(this);
		liveShareBut.setOnClickListener(this);
		// liveVideoController.setOnClickListener(this);
		listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				String time = TimeUtil.getTime(new Date());
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
						"最后更新:" + time);
				refreshNetDate();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				loadMoreNetDate();
			}
		});

		main_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				screnn_pb.setVisibility(View.VISIBLE);
				main_bg.setVisibility(View.GONE);
				refreshNetDate();
			}
		});
		mOrientationListener = new OrientationEventListener(this.mActivity) {
			@Override
			public void onOrientationChanged(int rotation) {

				if (startRotation == -2) {// 初始化角度
					startRotation = rotation;
				}
				// 变化角度大于30时，开启自动旋转，并关闭监听
				int r = Math.abs(startRotation - rotation);
				r = r > 180 ? 360 - r : r;
				if (r > 30) {
					// 开启自动旋转，响应屏幕旋转事件
					mActivity
							.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
					this.disable();
				}
			}
		};
	}

	@Override
	protected boolean initLocalDate() {
		try {
			localDate = mActivity.dbUtils.findAll(New_LivePlay.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	protected void saveLocalDate() {

	}

	@Override
	protected void refreshNetDate() {
		initLocalDate();
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			ItnetUtils instance = ItnetUtils.getInstance(mActivity);
			instance.getNewLivePlayData(mListenerArray, mErrorListener);
		} else {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					listview.onRefreshComplete();
				}
			}, 500);
		}

	}

	@Override
	protected void loadMoreNetDate() {

	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {
	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
		// TODO Auto-generated method stub
		if (jsonObject != null) {
			int length = jsonObject.length();
			mLivePlayList = new ArrayList<New_LivePlay>();
			New_LivePlay livePlay = null;
			for (int i = 0; i < length; i++) {
				livePlay = new New_LivePlay();
				try {
					livePlay.parseJSON(jsonObject.getJSONObject(i));
					mLivePlayList.add(livePlay);
				} catch (NetRequestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			screnn_pb.setVisibility(View.GONE);
			main_bg.setVisibility(View.GONE);
			playerVideo();
		}
	}

	private void playerVideo() {
		if (CommonUtils.isNetworkAvailable(this.mActivity)) {
			if (!CommonUtils.isWifi(this.mActivity)) {
				if (!spUtil.isFlow()) {
					final TishiMsgHint dialog = new TishiMsgHint(
							this.mActivity, R.style.MyDialog1);
					dialog.setContent("您已设置2G/3G/4G网络下不允许播放/缓存视频", "我知道了");
					dialog.setCancleListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				} else {
					final InfoMsgHint dialog = new InfoMsgHint(this.mActivity,
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
							showData();
							dialog.dismiss();
						}
					});
					dialog.show();

				}

			} else {
				showData();
			}
		} else {
			final TishiMsgHint dialog = new TishiMsgHint(this.mActivity,
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

	private void showData() {
		boolean unStart = false;
		for (New_LivePlay news : mLivePlayList) {

			if (isSelectPlay && Integer.parseInt(news.getZid()) == selectPlayID) {
				unStart = true;
			}

			if (!news.getType().equals("直播预告")) {
				if (!isSelectPlay) {
					livePlayTitle.setText("正在播放:" + news.getTitle());
					fullScreenLivePlayTitle.setText("正在播放:" + news.getTitle());
					liveStart.setVisibility(View.GONE);
					mVideoLoadingLayout.setVisibility(View.GONE);
					// video_view.pause();
					liveStart.setVisibility(View.GONE);
					liveVideoView.stopPlayback();
					// Uri getmUri = video_view.
					// if (getmUri != null) {
					// video_view.release(true);
					// }
					liveVideoView.setVideoPath(news.getStreamurl());

					ItnetUtils.getInstance(this.mActivity).getAnalyse(
							this.mActivity, "live", news.getTitle(),
							news.getTitleurl());

					mActivity
							.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
					nowLiveNew = news;
					break;
				} else {
					if (Integer.parseInt(news.getZid()) == selectPlayID) {
						unStart = false;
						livePlayTitle.setText("正在播放:" + news.getTitle());
						fullScreenLivePlayTitle.setText("正在播放:"
								+ news.getTitle());
						liveStart.setVisibility(View.GONE);
						isSelectPlay = false;
						liveVideoView.stopPlayback();
						liveVideoView.setVideoPath(news.getStreamurl());

						ItnetUtils.getInstance(this.mActivity).getAnalyse(
								this.mActivity, "live", news.getTitle(),
								news.getTitleurl());

						mVideoLoadingLayout.setVisibility(View.GONE);
						mActivity
								.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
						break;
					}
				}

			}
		}
		if (localDate != null) {
			for (New_LivePlay news : mLivePlayList) {
				for (New_LivePlay localNews : localDate) {
					if (news.getZid().equalsIgnoreCase(localNews.getZid())) {
						news.setOrder(localNews.isOrder());
					}
				}
			}
		}

		if (isSelectPlay) {
			isSelectPlay = false;
			if (unStart) {
				ToastUtils.Errortoast(mActivity, "您点击的直播还未开始");
			} else {
				ToastUtils.Errortoast(mActivity, "您点击的直播已经结束");
			}
			showData();
		}

		myAdapter = new MyAdapter();
		listview.setAdapter(myAdapter);
		screnn_pb.setVisibility(View.GONE);
		listview.onRefreshComplete();
	}

	@Override
	protected void onFailure(VolleyError error) {
		screnn_pb.setVisibility(View.GONE);
		main_bg.setVisibility(View.VISIBLE);
	}

	public class MyAdapter extends BaseAdapter {
		ViewHolder mViewHolder;
		ViewHolderLive mViewHolderLive;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mLivePlayList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			// return mLivePlayList.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			New_LivePlay new_LivePlay = mLivePlayList.get(position);
			if (new_LivePlay.getAppBgPic() != null
					&& !new_LivePlay.getAppBgPic().trim().equals(""))
				return 0;
			return 1;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			int itemViewType = getItemViewType(position);
			final New_LivePlay new_LivePlay = mLivePlayList.get(position);

			// if (convertView == null) {
			convertView = inflate.inflate(mActivity,
					R.layout.new_item_liveplay_live, null);
			mViewHolderLive = new ViewHolderLive();
			mViewHolderLive.new_item_liveplay_live_title = (MyTextView) convertView
					.findViewById(R.id.new_item_liveplay_live_title);
			mViewHolderLive.new_item_liveplay_but = (MyTextView) convertView
					.findViewById(R.id.new_item_liveplay_but);
			mViewHolderLive.new_item_liveplay_live_time = (MyTextView) convertView
					.findViewById(R.id.new_item_liveplay_live_time);
			mViewHolderLive.new_item_liveplay_content = (MyTextView) convertView
					.findViewById(R.id.new_item_liveplay_content);
			// mViewHolderLive.new_item_liveplay_live_tv = (MyTextView)
			// convertView
			// .findViewById(R.id.new_item_liveplay_live_tv);
			mViewHolderLive.new_item_liveplay_live_but = (MyTextView) convertView
					.findViewById(R.id.new_item_liveplay_live_but);
			// mViewHolderLive.new_item_liveplay_live_ic = (ImageView)
			// convertView
			// .findViewById(R.id.new_item_liveplay_live_ic);
			mViewHolderLive.new_item_liveplay_live_bg = (RelativeLayout) convertView
					.findViewById(R.id.new_item_liveplay_live_bg);
			mViewHolderLive.new_item_liveplay_live_bg_img = (ImageView) convertView
					.findViewById(R.id.new_item_liveplay_live_bg_img);

			// ViewGroup.LayoutParams linearParams = (ViewGroup.LayoutParams)
			// mViewHolderLive.new_item_liveplay_live_bg_img
			// .getLayoutParams();
			//
			//
			// linearParams.height =
			// New_LivePlayFragment.this.mActivity.mScreenWidth / 3;
			// mViewHolderLive.new_item_liveplay_live_bg_img.setLayoutParams(linearParams);

			// mViewHolderLive.new_item_liveplay_live_status = convertView
			// .findViewById(R.id.new_item_liveplay_live_statue);
			convertView.setTag(mViewHolderLive);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					return;
				}
			});
			// } else {
			// mViewHolderLive = (ViewHolderLive) convertView.getTag();
			// }

			mViewHolderLive.new_item_liveplay_live_title.setText(new_LivePlay
					.getTitle());
			mViewHolderLive.new_item_liveplay_live_time.setText(new_LivePlay
					.getTime());
			mViewHolderLive.new_item_liveplay_live_but.setText("");
			if (!new_LivePlay.getType().equals("直播预告")) {
				// mViewHolderLive.new_item_liveplay_live_status
				// .setBackgroundResource(R.drawable.ic_live);
				mViewHolderLive.new_item_liveplay_live_but
						.setBackgroundResource(R.drawable.playlive);
				if (new_LivePlay.getAppBgPic() != null
						&& !new_LivePlay.getAppBgPic().trim().equals("")) {
					mViewHolderLive.new_item_liveplay_but
							.setVisibility(View.GONE);
					// mViewHolderLive.new_item_liveplay_live_ic
					// .setVisibility(View.GONE);
					// mViewHolderLive.new_item_liveplay_live_tv
					// .setVisibility(View.GONE);

					// mViewHolderLive.new_item_liveplay_live_ic
					// .setImageResource(R.drawable.xwzh);
					// mViewHolderLive.new_item_liveplay_live_bg
					// .setBackgroundResource(R.drawable.livebg3);
					// Log.e("getAppBgPic", new_LivePlay.getAppBgPic());
					// Bitmap bgMap = ImgUtils.getNetImage(new_LivePlay
					// .getAppBgPic());
					// mViewHolderLive.new_item_liveplay_live_bg
					// .setBackground(new BitmapDrawable(getResources(),
					// bgMap));
					// mViewHolderLive.new_item_liveplay_content
					// .setVisibility(View.GONE);
					// mViewHolderLive.new_item_liveplay_live_bg
					// .setImageBitmap(null);
					// ImgUtils.imageLoader.loadImage(new_LivePlay.getAppBgPic(),
					// ImgUtils.homeImageOptions,
					// new ImageLoadingListener() {
					//
					// @Override
					// public void onLoadingStarted(String imageUri,
					// View view) {
					// // TODO Auto-generated method stub
					// Log.e("onLoadingStarted", "onLoadingStarted");
					// mViewHolderLive.new_item_liveplay_live_bg
					// .setBackgroundResource(R.drawable.livebg2);
					// }
					//
					// @Override
					// public void onLoadingFailed(String imageUri,
					// View view, FailReason failReason) {
					// // TODO Auto-generated method stub
					// Log.e("onLoadingFailed", "onLoadingFailed");
					// mViewHolderLive.new_item_liveplay_live_bg
					// .setBackgroundResource(R.drawable.livebg2);
					// }
					//
					// @Override
					// public void onLoadingComplete(String imageUri,
					// View view, Bitmap loadedImage) {
					// // TODO Auto-generated method stub
					// Log.e("onLoadingComplete", "onLoadingComplete");
					//
					// if (Build.VERSION.SDK_INT >=
					// Build.VERSION_CODES.JELLY_BEAN) {
					// mViewHolderLive.new_item_liveplay_live_bg
					// .setBackground(new BitmapDrawable(
					// getResources(), loadedImage));
					// } else {
					// mViewHolderLive.new_item_liveplay_live_bg
					// .setBackgroundDrawable(new BitmapDrawable(
					// getResources(), loadedImage));
					// }
					// }
					//
					// @Override
					// public void onLoadingCancelled(String imageUri,
					// View view) {
					// Log.e("onLoadingCancelled", "onLoadingCancelled");
					// // TODO Auto-generated method stub
					// }
					// });
					ImgUtils.imageLoader.displayImage(
							new_LivePlay.getAppBgPic(),
							mViewHolderLive.new_item_liveplay_live_bg_img,
							ImgUtils.liveImageOptions);
					// mViewHolderLive.new_item_liveplay_live_status
					// .setVisibility(View.GONE);
					// }
					// if (new_LivePlay.getCatename().equals("新闻综合")) {
					// mViewHolderLive.new_item_liveplay_but
					// .setVisibility(View.GONE);
					// mViewHolderLive.new_item_liveplay_live_ic
					// .setVisibility(View.VISIBLE);
					// mViewHolderLive.new_item_liveplay_live_tv
					// .setVisibility(View.GONE);
					// mViewHolderLive.new_item_liveplay_live_ic
					// .setImageResource(R.drawable.xwzh);
					// mViewHolderLive.new_item_liveplay_live_bg
					// .setBackgroundResource(R.drawable.livebg3);
					// mViewHolderLive.new_item_liveplay_content
					// .setVisibility(View.GONE);
					// } else if (new_LivePlay.getCatename().equals("东方卫视")) {
					// mViewHolderLive.new_item_liveplay_but
					// .setVisibility(View.GONE);
					// mViewHolderLive.new_item_liveplay_live_tv
					// .setVisibility(View.GONE);
					// mViewHolderLive.new_item_liveplay_live_ic
					// .setVisibility(View.VISIBLE);
					// mViewHolderLive.new_item_liveplay_live_ic
					// .setImageResource(R.drawable.dfws);
					// mViewHolderLive.new_item_liveplay_live_bg
					// .setBackgroundResource(R.drawable.livebg4);
					// mViewHolderLive.new_item_liveplay_content
					// .setVisibility(View.GONE);
				} else {
					// mViewHolderLive.new_item_liveplay_live_status
					// .setVisibility(View.VISIBLE);
					// mViewHolderLive.new_item_liveplay_live_ic
					// .setVisibility(View.GONE);
					// mViewHolderLive.new_item_liveplay_live_tv
					// .setVisibility(View.VISIBLE);
					// mViewHolderLive.new_item_liveplay_live_bg
					// .setBackgroundResource(R.drawable.kklive_live_bg);
					mViewHolderLive.new_item_liveplay_live_bg_img
							.setImageResource(R.drawable.kklive_live_bg);
					mViewHolderLive.new_item_liveplay_but
							.setVisibility(View.VISIBLE);
					// mViewHolderLive.new_item_liveplay_live_tv.setText("正在直播");
					if (curPosition == position) {
						if (isShow) {
							mViewHolderLive.new_item_liveplay_content
									.setVisibility(View.VISIBLE);
							Drawable ic_arrowdown = getResources().getDrawable(
									R.drawable.ic_arrowdown);
							ic_arrowdown.setBounds(0, 0,
									ic_arrowdown.getMinimumWidth(),
									ic_arrowdown.getMinimumHeight());
							mViewHolderLive.new_item_liveplay_but
									.setCompoundDrawables(null, null,
											ic_arrowdown, null);
						} else {
							Drawable ic_arrowshow = getResources().getDrawable(
									R.drawable.ic_arrowshow);
							ic_arrowshow.setBounds(0, 0,
									ic_arrowshow.getMinimumWidth(),
									ic_arrowshow.getMinimumHeight());
							mViewHolderLive.new_item_liveplay_but
									.setCompoundDrawables(null, null,
											ic_arrowshow, null);

							mViewHolderLive.new_item_liveplay_content
									.setVisibility(View.GONE);

						}
					} else {
						Drawable ic_arrowshow = getResources().getDrawable(
								R.drawable.ic_arrowshow);
						ic_arrowshow.setBounds(0, 0,
								ic_arrowshow.getMinimumWidth(),
								ic_arrowshow.getMinimumHeight());
						mViewHolderLive.new_item_liveplay_but
								.setCompoundDrawables(null, null, ic_arrowshow,
										null);
						mViewHolderLive.new_item_liveplay_content
								.setVisibility(View.GONE);
					}

					final boolean new_item_liveplay_content_show = mViewHolderLive.new_item_liveplay_content
							.getVisibility() == View.VISIBLE;

					mViewHolderLive.new_item_liveplay_content
							.setText(new_LivePlay.getIntro());
					mViewHolderLive.new_item_liveplay_but
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									curPosition = position;

									if (new_item_liveplay_content_show) {
										isShow = false;
									} else {
										isShow = true;
									}
									if (position == mLivePlayList.size() - 1) {
										listview.setScroolBottom(true);
									} else {
										listview.setSelection(position);
										listview.setScroolBottom(false);
									}
									myAdapter.notifyDataSetChanged();
								}
							});

				}
				mViewHolderLive.new_item_liveplay_live_but
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								livePlayTitle.setText("正在播放:"
										+ new_LivePlay.getTitle());
								fullScreenLivePlayTitle.setText("正在播放:"
										+ new_LivePlay.getTitle());
								// 更改直播频道
								// video_view.release(true);
								mVideoLoadingLayout.setVisibility(View.GONE);
								liveVideoView.stopPlayback();
								liveVideoImage.setVisibility(View.VISIBLE);
								liveStart.setVisibility(View.GONE);
								liveVideoView.setVideoPath(new_LivePlay
										.getStreamurl());

								ItnetUtils
										.getInstance(
												New_LivePlayFragment.this.mActivity)
										.getAnalyse(
												New_LivePlayFragment.this.mActivity,
												"live",
												new_LivePlay.getTitle(),
												new_LivePlay.getTitleurl());

								// setVideoLoadingLayoutVisibility(View.VISIBLE);
								nowLiveNew = new_LivePlay;
							}
						});
			} else {
				// mViewHolderLive.new_item_liveplay_live_status
				// .setVisibility(View.VISIBLE);

				// mViewHolderLive.new_item_liveplay_live_status
				// .setBackgroundResource(R.drawable.ic_next);

				if (new_LivePlay.isOrder()) {
					mViewHolderLive.new_item_liveplay_live_but
							.setBackgroundResource(R.drawable.ic_unyuyue);

				} else {
					mViewHolderLive.new_item_liveplay_live_but
							.setBackgroundResource(R.drawable.ic_yuyue);
					mViewHolderLive.new_item_liveplay_live_but.setText("");
				}

				// mViewHolderLive.new_item_liveplay_live_ic
				// .setVisibility(View.GONE);
				// mViewHolderLive.new_item_liveplay_live_tv
				// .setVisibility(View.VISIBLE);
				// mViewHolderLive.new_item_liveplay_live_bg
				// .setBackgroundResource(R.drawable.yugao_live_bg);

				mViewHolderLive.new_item_liveplay_live_bg_img
						.setImageResource(R.drawable.yugao_live_bg);
				mViewHolderLive.new_item_liveplay_but
						.setVisibility(View.VISIBLE);
				// mViewHolderLive.new_item_liveplay_live_tv.setText("直播预告");

				if (curPosition == position) {
					if (isShow) {
						mViewHolderLive.new_item_liveplay_content
								.setVisibility(View.VISIBLE);
						Drawable ic_arrowdown = getResources().getDrawable(
								R.drawable.ic_arrowdown);
						ic_arrowdown.setBounds(0, 0,
								ic_arrowdown.getMinimumWidth(),
								ic_arrowdown.getMinimumHeight());
						mViewHolderLive.new_item_liveplay_but
								.setCompoundDrawables(null, null, ic_arrowdown,
										null);
					} else {
						Drawable ic_arrowshow = getResources().getDrawable(
								R.drawable.ic_arrowshow);
						ic_arrowshow.setBounds(0, 0,
								ic_arrowshow.getMinimumWidth(),
								ic_arrowshow.getMinimumHeight());
						mViewHolderLive.new_item_liveplay_but
								.setCompoundDrawables(null, null, ic_arrowshow,
										null);

						mViewHolderLive.new_item_liveplay_content
								.setVisibility(View.GONE);

					}
				} else {
					Drawable ic_arrowshow = getResources().getDrawable(
							R.drawable.ic_arrowshow);
					ic_arrowshow.setBounds(0, 0,
							ic_arrowshow.getMinimumWidth(),
							ic_arrowshow.getMinimumHeight());
					mViewHolderLive.new_item_liveplay_but.setCompoundDrawables(
							null, null, ic_arrowshow, null);
					mViewHolderLive.new_item_liveplay_content
							.setVisibility(View.GONE);
				}

				final boolean new_item_liveplay_content_show1 = mViewHolderLive.new_item_liveplay_content
						.getVisibility() == View.VISIBLE;

				mViewHolderLive.new_item_liveplay_content.setText(new_LivePlay
						.getIntro());
				mViewHolderLive.new_item_liveplay_but
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								curPosition = position;
								if (new_item_liveplay_content_show1) {
									isShow = false;
								} else {
									isShow = true;
								}
								if (position == mLivePlayList.size() - 1) {
									listview.setScroolBottom(true);
								} else {
									listview.setSelection(position);
									listview.setScroolBottom(false);
								}
								myAdapter.notifyDataSetChanged();
							}
						});

				mViewHolderLive.new_item_liveplay_live_but
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								String[] split = new_LivePlay.getDatetime()
										.split(":::");

								long date2unix2 = TimeUtil.date2unix2(split[0]);
								long currentTimeMillis = System
										.currentTimeMillis();
								long date2unix22 = TimeUtil.date2unix2(TimeUtil
										.getTime(currentTimeMillis));
								long time = date2unix2 - date2unix22;
								if (time > 0) {
									if (!new_LivePlay.isOrder()) {
										// 预约
										Calendar calendar = Calendar
												.getInstance();
										calendar.setTimeInMillis(SystemClock
												.elapsedRealtime());
										calendar.setTimeZone(TimeZone
												.getTimeZone("GMT+8"));
										calendar.add(Calendar.SECOND,
												(int) (time / 1000));
										// calendar.add(Calendar.SECOND, 10);

										Intent intent = new Intent(mActivity,
												AlarmReceiver.class);
										intent.putExtra("LIVE", new_LivePlay);
										PendingIntent sender = PendingIntent
												.getBroadcast(
														mActivity,
														Integer.parseInt(new_LivePlay
																.getZid()),
														intent, 0);
										// 进行闹铃注册
										manager.set(
												AlarmManager.ELAPSED_REALTIME_WAKEUP,
												calendar.getTimeInMillis(),
												sender);
										new_LivePlay.setOrder(true);
										ToastUtils.Infotoast(mActivity,
												"预约设置成功");
									} else {
										// 取消预约
										Intent intent = new Intent(mActivity,
												AlarmReceiver.class);
										intent.putExtra("LIVE", new_LivePlay);
										PendingIntent broadcast = PendingIntent
												.getBroadcast(
														mActivity,
														Integer.parseInt(new_LivePlay
																.getZid()),
														intent, 0);
										manager.cancel(broadcast);
										ToastUtils.Infotoast(mActivity,
												"预约设置取消");
										new_LivePlay.setOrder(false);
									}
									try {
										mActivity.dbUtils
												.saveOrUpdate(new_LivePlay);
										initLocalDate();
									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									myAdapter.notifyDataSetChanged();
								} else {
									ToastUtils.Infotoast(mActivity,
											"节目已经开始,请刷新观看");
								}
							}
						});

			}

			return convertView;
		}
	}

	// 直播item
	private class ViewHolderLive {
		MyTextView new_item_liveplay_content;
		MyTextView new_item_liveplay_live_title;
		MyTextView new_item_liveplay_live_time;
		// MyTextView new_item_liveplay_live_tv;
		MyTextView new_item_liveplay_but;
		MyTextView new_item_liveplay_live_but;
		// ImageView new_item_liveplay_live_ic;
		RelativeLayout new_item_liveplay_live_bg;
		// View new_item_liveplay_live_status;
		ImageView new_item_liveplay_live_bg_img;

	}

	// 预告itemÏ
	private class ViewHolder {
		MyTextView new_item_liveplay_title;
		MyTextView new_item_liveplay_time;
		MyTextView new_item_liveplay_content;
		MyTextView new_item_liveplay_but;
	}

	@Override
	public boolean onError(IMediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		liveStart.setVisibility(View.VISIBLE);
		DebugLog.e("onError", what + " " + extra);
		ToastUtils.Errortoast(mActivity, "视频播放有误请重新刷新");
		return true;
	}

	@Override
	public void onCompletion(IMediaPlayer mp) {
		liveStart.setVisibility(View.VISIBLE);
		liveVideoPause();
	}

	@Override
	public boolean onInfo(IMediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.live_share_but:
			nowLiveNew.setSharedPic(null);
			shareUtil = new ShareUtil(nowLiveNew, this.mActivity);
			this.mActivity.shareUtil = shareUtil;
			// 一键分享
			shareBoard = new CustomShareBoard((BaseActivity) this.mActivity,
					shareUtil, this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(this.getActivity().getWindow()
					.getDecorView(), Gravity.BOTTOM, 0, 0);
			isFirst = true;
			break;
		case R.id.live_start:// 播放视频
			liveVideoView.start();
			liveStart.setVisibility(View.GONE);
			break;
		case R.id.full_live_start:// 播放视频
			if (liveVideoView.isPlaying()) {
				liveVideoView.pause();
				updateFullStartBut(false);
			} else {
				liveVideoView.start();
				updateFullStartBut(true);
			}
			break;
		case R.id.live_pause:// 暂停
			if (!liveVideoView.isPlaying())
				break;
			if (isFullstate) {
				if (isFullShowLayout) {
					fullScreenLayout.setVisibility(View.GONE);
					fullLiveStart.setVisibility(View.GONE);
					isFullShowLayout = false;
					orientationHandler.removeMessages(HIDE_VIDEO_CONTROLLER);
				} else {
					fullScreenLayout.setVisibility(View.VISIBLE);
					fullLiveStart.setVisibility(View.VISIBLE);
					isFullShowLayout = true;
					orientationHandler.sendEmptyMessageDelayed(
							HIDE_VIDEO_CONTROLLER, hideTimeOut);
				}
			} else {
				liveVideoPause();
			}
			break;
		case R.id.fullscrenn_but:// 大屏
			mActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			orientationHandler
					.sendEmptyMessageDelayed(FULL_SCREEN_CHANGE, 1000);
			break;
		case R.id.smallscrenn_but:// 小屏
			mActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			orientationHandler
					.sendEmptyMessageDelayed(FULL_SCREEN_CHANGE, 1000);
			break;
		// case R.id.live_video_controller:
		// // if (video_view.isPlaying() || hasBeenPaly) {
		// if (!liveVideoController.isShow())
		// // && video_pb.getVisibility() != View.VISIBLE
		// // && small_video_pb.getVisibility() != View.VISIBLE)
		// liveVideoController.show();
		// // }
		// break;
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		liveVideoPause();
	}

	public VideoView getVideoView() {
		// video_view.release(true);
		return liveVideoView;
	}

	// 刷新
	public void refresh() {
		if (listview != null)
			listview.setRefreshing(false);
	}

	@Override
	public void onPrepared(IMediaPlayer mp) {
		// TODO Auto-generated method stub
		// liveVideoView.start();
		liveVideoView.pause();
		liveVideoImage.setVisibility(View.GONE);
		if (mActivity.curTouchTab == mActivity.tab_two)
			liveVideoView.start();
	}

	public void liveVideoPause() {
		// if (liveVideoView.isPlaying()) {
		if (liveVideoView != null) {
			liveVideoView.pause();
		}
		// }
		if (liveStart != null) {
			liveStart.setVisibility(View.VISIBLE);
		}
	}

	public void updateFullStartBut(boolean isplaying) {
		if (isplaying)
			fullLiveStart.setImageResource(R.drawable.ic_stopplay);
		else
			fullLiveStart.setImageResource(R.drawable.ic_liveplay);
	}

	public boolean isSelectPlay() {
		return isSelectPlay;
	}

	public void setSelectPlay(boolean isSelectPlay) {
		this.isSelectPlay = isSelectPlay;
	}

	public int getSelectPlayID() {
		return selectPlayID;
	}

	public void setSelectPlayID(int selectPlayID) {
		this.selectPlayID = selectPlayID;
	}

	public boolean isFullstate() {
		return isFullstate;
	}

}
