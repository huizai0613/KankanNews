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
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.receiver.AlarmReceiver;
import com.kankan.kankanews.sina.AccessTokenKeeper;
import com.kankan.kankanews.sina.Constants;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.view.CustomShareBoard;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankan.kankanews.utils.XunaoLog;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;

public class New_LivePlayFragment extends BaseFragment implements
		OnInfoListener, OnCompletionListener, OnErrorListener, OnClickListener,
		OnPreparedListener, CanSharedBySina {

	private View inflate;
	private RelativeLayout smallrootview;
	private RelativeLayout rootview;
	private VideoView liveVideoView;
	private View livePause;
	private View main_bg;
	private ImageView liveStart;
	private ImageView smallscrenn_but;
	private MyTextView livePlayTitle;
	private MyTextView fullScreenLivePlayTitle;
	private LinearLayout mVideoLoadingLayout;
	private LinearLayout screnn_pb;
	private ImageView fullscrenn_but;
	private ArrayList<New_LivePlay> mLivePlayList = new ArrayList<New_LivePlay>();
	private MyAdapter myAdapter;
	private ImageView liveShareBut;
	
	private static final int BUFFER_START = 11;
	private static final int BUFFER_PROGRESS = 12;
	private static final int BUFFER_COMPLETE = 13;
	

	private boolean isFullstate;

	private int curPosition;
	private boolean isShow;
	private boolean isSelectPlay;
	private int selectPlayID;
	private boolean isFirst = true;
	private AlarmManager manager;
	private List<New_LivePlay> localDate;
	
	private New_LivePlay nowLiveNew;

	private static CustomShareBoard shareBoard;
	
	private ShareUtil shareUtil = null;
	
	private OrientationEventListener mOrientationListener; // 屏幕方向改变监听器
	private int startRotation;
	
	Handler orientationHandler = new Handler(){
		public void handleMessage(Message msg) {
			startRotation = -2;
			mOrientationListener.enable();
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
			//全屏
			if (shareBoard != null && shareBoard.isShowing()) {
				shareBoard.dismiss();
			}
			fullscrenn_but.setVisibility(View.GONE);
			smallscrenn_but.setVisibility(View.VISIBLE);
			fullScreenLivePlayTitle.setVisibility(View.VISIBLE);
			mActivity.bottomBarVisible(View.GONE);
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			mActivity.getWindow().setAttributes(attrs);
			mActivity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			smallrootview.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			liveVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH);
			isFullstate = true;
			liveVideoView.getHolder().setFixedSize(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		} else {
			fullscrenn_but.setVisibility(View.VISIBLE);
			smallscrenn_but.setVisibility(View.GONE);
			fullScreenLivePlayTitle.setVisibility(View.GONE);
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
			} else {
				ToastUtils.ErrorToastNoNet(mActivity);
			}
		} else {
			isFirst = false;
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		livePlayTitle = (MyTextView) inflate.findViewById(R.id.livePlayTitle);
		fullScreenLivePlayTitle = (MyTextView) inflate.findViewById(R.id.fullScreenLivePlayTitle);
//		loadingText = (MyTextView) inflate.findViewById(R.id.video_loading_text);
//		mVideoLoadingLayout = (LinearLayout) inflate.findViewById(R.id.mVideoLoadingLayout);
		mVideoLoadingLayout = (LinearLayout) inflate.findViewById(R.id.buffering_indicator);
		liveVideoView.setMediaBufferingIndicator(mVideoLoadingLayout);
		liveVideoView.setUserAgent("KKApp");
		screnn_pb = (LinearLayout) inflate.findViewById(R.id.screnn_pb);
		fullscrenn_but = (ImageView) inflate.findViewById(R.id.fullscrenn_but);
		smallscrenn_but = (ImageView) inflate
				.findViewById(R.id.smallscrenn_but);
		liveShareBut = (ImageView) inflate
				.findViewById(R.id.live_share_but);
		listview = (PullToRefreshListView) inflate.findViewById(R.id.listview);
		initListView(Mode.PULL_DOWN_TO_REFRESH);
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
		
		listview.setAdapter(new MyAdapter());
		initViewLayout();
		initLinsenter();

		initLocalDate();
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			refreshNetDate();
		} else {
			screnn_pb.setVisibility(View.GONE);
			main_bg.setVisibility(View.VISIBLE);
		}
		
		mOrientationListener = new OrientationEventListener(this.mActivity) {
            @Override
            public void onOrientationChanged(int rotation) {

            	if (startRotation == -2) {//初始化角度
					startRotation = rotation;
				}
            	//变化角度大于30时，开启自动旋转，并关闭监听
            	int r = Math.abs(startRotation - rotation);
            	r = r > 180 ? 360 - r : r;
            	if (r > 30) {
            		//开启自动旋转，响应屏幕旋转事件
            		mActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            		this.disable();
				}
            }
		};
		return inflate;
	}

	public void initViewLayout() {
		smallrootview.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				(int) (mActivity.mScreenWidth / 16 * 9)));
	}

	public void initLinsenter() {
		liveVideoView.setOnErrorListener(this);
//		liveVideoView.setOnCompletionListener(this);
		liveVideoView.setOnPreparedListener(this);
//		liveVideoView.setOnInfoListener(this);
		liveVideoView.setOnClickListener(this);
		livePause.setOnClickListener(this);
		liveStart.setOnClickListener(this);
		fullscrenn_but.setOnClickListener(this);
		smallscrenn_but.setOnClickListener(this);
		liveShareBut.setOnClickListener(this);
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
			showData();
		}
	}

	private void showData() {
		boolean unStart = false;
		for (New_LivePlay news : mLivePlayList) {

			if (isSelectPlay
					&& Integer.parseInt(news.getZid()) == selectPlayID) {
				unStart = true;
			}

			if (!news.getType().equals("直播预告")) {
				if (!isSelectPlay) {
					livePlayTitle.setText("正在播放:" + news.getTitle());
					fullScreenLivePlayTitle.setText("正在播放:" + news.getTitle());
//					video_view.pause();
					liveStart.setVisibility(View.GONE); 
					liveVideoView.stopPlayback();
//					Uri getmUri = video_view.
//					if (getmUri != null) {
//						video_view.release(true);
//					}
					liveVideoView.setVideoPath(news.getStreamurl());
					mActivity
							.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
					nowLiveNew = news;
					break;
				} else {
					if (Integer.parseInt(news.getZid()) == selectPlayID) {
						unStart = false;
						livePlayTitle.setText("正在播放:" + news.getTitle());
						fullScreenLivePlayTitle.setText("正在播放:" + news.getTitle());
						isSelectPlay = false;
						liveVideoView.stopPlayback();
						liveVideoView.setVideoPath(news.getStreamurl());
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
			return mLivePlayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			int itemViewType = getItemViewType(position);
			final New_LivePlay new_LivePlay = mLivePlayList.get(position);

			if (convertView == null) {
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
				mViewHolderLive.new_item_liveplay_live_tv = (MyTextView) convertView
						.findViewById(R.id.new_item_liveplay_live_tv);
				mViewHolderLive.new_item_liveplay_live_but = (MyTextView) convertView
						.findViewById(R.id.new_item_liveplay_live_but);
				mViewHolderLive.new_item_liveplay_live_ic = (ImageView) convertView
						.findViewById(R.id.new_item_liveplay_live_ic);
				mViewHolderLive.new_item_liveplay_live_bg = convertView
						.findViewById(R.id.new_item_liveplay_live_bg);
				mViewHolderLive.new_item_liveplay_live_status = convertView
						.findViewById(R.id.new_item_liveplay_live_statue);
				convertView.setTag(mViewHolderLive);
			} else {
				mViewHolderLive = (ViewHolderLive) convertView.getTag();
			}

			mViewHolderLive.new_item_liveplay_live_title.setText(new_LivePlay
					.getTitle());
			mViewHolderLive.new_item_liveplay_live_time.setText(new_LivePlay
					.getTime());
			mViewHolderLive.new_item_liveplay_live_but.setText("");
			if (!new_LivePlay.getType().equals("直播预告")) {
				mViewHolderLive.new_item_liveplay_live_status
						.setBackgroundResource(R.drawable.ic_live);
				mViewHolderLive.new_item_liveplay_live_but
						.setBackgroundResource(R.drawable.playlive);
				if (new_LivePlay.getCatename().equals("新闻综合")) {
					mViewHolderLive.new_item_liveplay_but
							.setVisibility(View.GONE);
					mViewHolderLive.new_item_liveplay_live_ic
							.setVisibility(View.VISIBLE);
					mViewHolderLive.new_item_liveplay_live_tv
							.setVisibility(View.GONE);
					mViewHolderLive.new_item_liveplay_live_ic
							.setImageResource(R.drawable.xwzh);
					mViewHolderLive.new_item_liveplay_live_bg
							.setBackgroundResource(R.drawable.livebg3);
					mViewHolderLive.new_item_liveplay_content
							.setVisibility(View.GONE);
				} else if (new_LivePlay.getCatename().equals("东方卫视")) {
					mViewHolderLive.new_item_liveplay_but
							.setVisibility(View.GONE);
					mViewHolderLive.new_item_liveplay_live_tv
							.setVisibility(View.GONE);
					mViewHolderLive.new_item_liveplay_live_ic
							.setVisibility(View.VISIBLE);
					mViewHolderLive.new_item_liveplay_live_ic
							.setImageResource(R.drawable.dfws);
					mViewHolderLive.new_item_liveplay_live_bg
							.setBackgroundResource(R.drawable.livebg4);
					mViewHolderLive.new_item_liveplay_content
					.setVisibility(View.GONE);
				} else {
					mViewHolderLive.new_item_liveplay_live_ic
							.setVisibility(View.GONE);
					mViewHolderLive.new_item_liveplay_live_tv
							.setVisibility(View.VISIBLE);
					mViewHolderLive.new_item_liveplay_live_bg
							.setBackgroundResource(R.drawable.livebg2);
					mViewHolderLive.new_item_liveplay_but
							.setVisibility(View.VISIBLE);
					mViewHolderLive.new_item_liveplay_live_tv.setText("正在直播");
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
								fullScreenLivePlayTitle.setText("正在播放:" + new_LivePlay.getTitle());
								// 更改直播频道
//								video_view.release(true);
								liveVideoView.stopPlayback();
								liveVideoView.setVideoPath(new_LivePlay
										.getStreamurl());
//								setVideoLoadingLayoutVisibility(View.VISIBLE);
								nowLiveNew = new_LivePlay;
							}
						});
			} else {

				mViewHolderLive.new_item_liveplay_live_status
						.setBackgroundResource(R.drawable.ic_next);

				if (new_LivePlay.isOrder()) {
					mViewHolderLive.new_item_liveplay_live_but
							.setBackgroundResource(R.drawable.ic_unyuyue);

				} else {
					mViewHolderLive.new_item_liveplay_live_but
							.setBackgroundResource(R.drawable.ic_yuyue);
					mViewHolderLive.new_item_liveplay_live_but.setText("");
				}

				mViewHolderLive.new_item_liveplay_live_ic
						.setVisibility(View.GONE);
				mViewHolderLive.new_item_liveplay_live_tv
						.setVisibility(View.VISIBLE);
				mViewHolderLive.new_item_liveplay_live_bg
						.setBackgroundResource(R.drawable.livebg2);
				mViewHolderLive.new_item_liveplay_but
						.setVisibility(View.VISIBLE);
				mViewHolderLive.new_item_liveplay_live_tv.setText("直播预告");

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

				mViewHolderLive.new_item_liveplay_content.setText(new_LivePlay.getIntro());
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
//										calendar.add(Calendar.SECOND, 10);

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
										new_LivePlay.setOrder(true);
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
		MyTextView new_item_liveplay_live_tv;
		MyTextView new_item_liveplay_but;
		MyTextView new_item_liveplay_live_but;
		ImageView new_item_liveplay_live_ic;
		View new_item_liveplay_live_bg;
		View new_item_liveplay_live_status;

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
			shareUtil = new ShareUtil(nowLiveNew, this.getActivity());
			// 一键分享
			shareBoard = new CustomShareBoard((BaseActivity)this.mActivity, shareUtil, this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(this.getActivity().getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;
		}
		
		switch (id) {
		case R.id.live_start:// 播放视频
			liveVideoView.start();
			liveStart.setVisibility(View.GONE);
			break;
		case R.id.live_pause:// 暂停
			liveVideoPause();
			break;
		case R.id.fullscrenn_but:// 大屏
			mActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			orientationHandler.sendEmptyMessageDelayed(0, 1000);
			break;
		case R.id.smallscrenn_but:// 小屏
			mActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			orientationHandler.sendEmptyMessageDelayed(0, 1000);
			break;
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
		listview.setRefreshing(false);
	}

	@Override
	public void onPrepared(IMediaPlayer mp) {
		// TODO Auto-generated method stub
//		liveVideoView.start();
	}
	
	public void liveVideoPause(){
		if (liveVideoView.isPlaying()) {
			liveVideoView.pause();
		}
		if (liveStart != null) {
			liveStart.setVisibility(View.VISIBLE);
		}
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
