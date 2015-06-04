package com.kankan.kankanews.ui.item;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.widget.VideoView;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.BaseVideoActivity;
import com.kankan.kankanews.bean.New_Colums;
import com.kankan.kankanews.bean.New_Colums_Info;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.VideoViewController;
import com.kankan.kankanews.ui.view.VideoViewController.ControllerType;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class New_Activity_Colums_Info extends BaseVideoActivity implements
		OnInfoListener, OnClickListener, OnPreparedListener,
		OnCompletionListener, OnErrorListener {

	private boolean noMoreNews = false;

	private ItnetUtils instance;
	private List<New_Colums_Info> new_colums_infos = new ArrayList<New_Colums_Info>();
	private MyAdapter myAdapter;

	private New_Colums colums;
	private String time = "";

	private TextView nodata;
	private RelativeLayout rootView;
	private VideoView columsVideoView;
	private VideoViewController columsVideoController;
	private ImageView columsVideoImage;
	private LinearLayout screen_pb;
	private ImageView columsImage;
	private MyTextView columsTitle;
	private ImageView calendarBut;
	private ImageView liveShareBut;
	private ImageView backBut;

	private int curPlayNo = 0;

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
	private View mVolumeBrightnessLayout;
	private ImageView mOperationPercent;
	private WindowManager wm;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Log.e("onConfigurationChanged", "onConfigurationChanged");
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		if (width > height) {
			// if (shareBoard != null && shareBoard.isShowing()) {
			// shareBoard.dismiss();
			// isGoShare = false;
			// }
			backBut.setVisibility(View.GONE);
			setRightFinsh(false);
			CommonUtils.clickevent(mContext, "action", "放大",
					AndroidConfig.video_fullscreen_event);
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			this.getWindow().setAttributes(attrs);
			this.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			rootView.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			columsVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH);
			isFullScrenn = true;
			columsVideoView.getHolder().setFixedSize(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

		} else {
			backBut.setVisibility(View.VISIBLE);
			setRightFinsh(true);
			isFullScrenn = false;
			// mActivity.bottomBarVisible(View.VISIBLE);
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			this.getWindow().setAttributes(attrs);
			// 取消全屏设置
			this.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			rootView.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT,
					(int) (this.mScreenWidth / 16 * 9)));
			columsVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_colums_info);

	}

	@Override
	protected void initView() {
		listview = (PullToRefreshListView) findViewById(R.id.colums_info_list_view);
		nodata = (TextView) findViewById(R.id.nodata);
		rootView = (RelativeLayout) findViewById(R.id.colums_video_root_view);
		rootView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (this.mScreenWidth / 16 * 9)));
		columsVideoView = (VideoView) findViewById(R.id.colums_video_view);
		columsVideoController = (VideoViewController) findViewById(R.id.colums_video_controller);
		columsVideoImage = (ImageView) findViewById(R.id.colums_video_image);
		columsImage = (ImageView) findViewById(R.id.colums_image);
		columsTitle = (MyTextView) findViewById(R.id.colums_title);
		calendarBut = (ImageView) findViewById(R.id.calendar_but);
		liveShareBut = (ImageView) findViewById(R.id.live_share_but);
		backBut = (ImageView) findViewById(R.id.colums_info_back);

		screen_pb = (LinearLayout) findViewById(R.id.screnn_pb);
		screen_pb.setVisibility(View.GONE);
		video_pb = (LinearLayout) findViewById(R.id.video_pb);
		small_video_pb = (LinearLayout) findViewById(R.id.small_video_pb);
		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

		colums = (New_Colums) getIntent().getSerializableExtra("colums");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
	}

	@Override
	protected void initData() {
		instance = ItnetUtils.getInstance(this);

		myAdapter = new MyAdapter();
		listview.setAdapter(myAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 1 || position - 2 >= new_colums_infos.size())
					return;
				curPlayNo = position - 2;
				myAdapter.notifyDataSetChanged();
				videoPlay();
			}

		});

		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		mAM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);

		columsVideoController.setPlayerControl(columsVideoView);
		columsVideoController.setActivity_Content(this);
		columsVideoView.setIsNeedRelease(false);
		columsTitle.setText(colums.getProgramName());

		if (CommonUtils.isNetworkAvailable(this)) {
			refreshNetDate();
		} else {
			// initLocalData();
			ToastUtils.Infotoast(mContext, "暂无网络请退回重试");
		}

	}

	@Override
	protected void setListener() {
		calendarBut.setOnClickListener(this);
		liveShareBut.setOnClickListener(this);
		backBut.setOnClickListener(this);

		columsVideoView.setOnCompletionListener(this);
		columsVideoView.setOnErrorListener(this);
		columsVideoView.setOnPreparedListener(this);
		columsVideoView.setOnInfoListener(this);
		columsVideoView.setOnClickListener(this);
		columsVideoController.setOnClickListener(this);

		listview.setMode(Mode.PULL_FROM_END);
		listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				refreshNetDate();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				loadMoreNetDate();
			}
		});

		// new Handler().postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// if (listview != null) {
		// listview.setRefreshing(false);
		// }
		// }
		// }, 500);
	}

	private void initLocalData() {
		try {
			if (dbUtils.tableIsExist(New_Colums_Info.class)) {
				new_colums_infos = dbUtils.findAll(Selector.from(
						New_Colums_Info.class).where("myType", "=",
						colums.getProgramName()));
				if (new_colums_infos != null && new_colums_infos.size() > 0) {
					myAdapter.notifyDataSetChanged();
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
		}

	}

	protected void refreshNetDate() {
		isLoadMore = false;
		noMoreNews = false;
		instance.getNewColumsInfoData(colums.getId(), time, "",
				getColumsInfoListener, getColumsInfoErrorListener);
	}

	protected void loadMoreNetDate() {
		isLoadMore = true;
		if (new_colums_infos != null && new_colums_infos.size() > 0) {
			instance.getNewColumsInfoData(colums.getId(), time,
					new_colums_infos.get(new_colums_infos.size() - 1)
							.getNewstime(), getColumsInfoListener,
					getColumsInfoErrorListener);
		} else {
			listview.onRefreshComplete();
			ToastUtils.Infotoast(mContext, "暂无" + time + "记录");
		}
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {

	}

	@Override
	protected void onFailure(VolleyError error) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.colums_info_back:
			onBackPressed();
			break;
		// case R.id.colums_video_view:
		// onBackPressed();
		// break;
		case R.id.colums_video_controller:
			// if (video_view.isPlaying() || hasBeenPaly) {
			Log.e("colums_video_controller", "colums_video_controller");
			if (!columsVideoController.isShow()
					&& video_pb.getVisibility() != View.VISIBLE)
				columsVideoController.show();
			// }
			break;
		case R.id.calendar_but:
			startAnimActivity2ObjForResult(New_Activity_Colums_Info_Time.class,
					"colums", AndroidConfig.Colums_Time_requestCode, colums);
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (columsVideoView != null) {
			columsVideoView.start();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (columsVideoView != null) {
			columsVideoView.pause();
		}
	}

	/*
	 * 获取栏目新闻
	 */
	// 处理网络出错
	protected ErrorListener getColumsInfoErrorListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			error.printStackTrace();
			ToastUtils.ErrorToastNoNet(mContext);
			listview.onRefreshComplete();
		}
	};
	// 处理网络成功
	protected Listener<JSONArray> getColumsInfoListener = new Listener<JSONArray>() {
		@Override
		public void onResponse(JSONArray jsonArray) {

			if (jsonArray != null && jsonArray.length() > 0) {
				ArrayList<New_Colums_Info> mnew_colums_infos = new ArrayList<New_Colums_Info>();
				for (int i = 0; i < jsonArray.length(); i++) {
					try {
						JSONObject jsonObject = jsonArray.optJSONObject(i);
						New_Colums_Info colums_info = new New_Colums_Info();
						colums_info = colums_info.parseJSON(jsonObject);
						colums_info.setMyType(colums.getProgramName());
						mnew_colums_infos.add(colums_info);
					} catch (NetRequestException e) {
						e.printStackTrace();
					}
				}
				if (!isLoadMore) {
					new_colums_infos = new ArrayList<New_Colums_Info>();
					new_colums_infos = mnew_colums_infos;
					saveDate();
					myAdapter = new MyAdapter();
					listview.setAdapter(myAdapter);
					// TODO
					if (new_colums_infos.size() > 0) {
						ImgUtils.imageLoader.displayImage(
								new_colums_infos.get(0).getTvLogo(),
								columsImage);
						videoPlay();
					}
				} else {
					new_colums_infos.addAll(mnew_colums_infos);
					myAdapter.notifyDataSetChanged();
				}
				nodata.setVisibility(View.GONE);
			} else {
				if (!isLoadMore) {
					// ToastUtils.Infotoast(mContext, "暂无"+time+"记录");
					nodata.setVisibility(View.VISIBLE);
					nodata.setText("暂无" + time + "记录");
					new_colums_infos.clear();
				} else {
					noMoreNews = true;
					// ToastUtils.Infotoast(mContext, "暂无更多信息");
				}
				myAdapter.notifyDataSetChanged();
			}

			listview.onRefreshComplete();
			// new_news_clicks
		}
	};

	protected void saveDate() {
		try {
			if (dbUtils.tableIsExist(New_Colums_Info.class)) {
				dbUtils.delete(New_Colums_Info.class,
						WhereBuilder.b("myType", "=", colums.getProgramName()));
			}
			// dbUtils.deleteAll(New_Colums_Info.class);
			dbUtils.saveAll(new_colums_infos);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	NewsItemHolder newsItemHolder = null;
	ViewHolderInfo holderInfo = null;
	ColumsInfoDetailHolder columsInfoDetailHolder = null;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (new_colums_infos != null && new_colums_infos.size() > 0) {
				if (noMoreNews) {
					return new_colums_infos.size() + 2;
				} else {
					return new_colums_infos.size() + 1;
				}
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return 0;
			} else if (new_colums_infos != null && new_colums_infos.size() > 0
					&& new_colums_infos.size() + 1 == position && noMoreNews) {
				return 2;
			} else {
				return 1;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int itemViewType = getItemViewType(position);

			if (convertView == null) {
				if (itemViewType == 0) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.colums_info_detail_item, null);
					columsInfoDetailHolder = new ColumsInfoDetailHolder();
					columsInfoDetailHolder.detailTitle = (TextView) convertView
							.findViewById(R.id.colums_detail_title);
					columsInfoDetailHolder.showBut = (ImageView) convertView
							.findViewById(R.id.colums_detail_show);
					convertView.setTag(columsInfoDetailHolder);
				} else if (itemViewType == 1) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.new_colums_info_item, null);
					newsItemHolder = new NewsItemHolder();
					newsItemHolder.rootView = (LinearLayout) convertView
							.findViewById(R.id.home_news_root_view);
					newsItemHolder.titlepic = (ImageView) convertView
							.findViewById(R.id.home_news_titlepic);
					newsItemHolder.title = (TextView) convertView
							.findViewById(R.id.home_news_title);
					newsItemHolder.newstime = (TextView) convertView
							.findViewById(R.id.home_news_newstime);
					convertView.setTag(newsItemHolder);
				} else if (itemViewType == 2) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.comment_nomore, null);
					holderInfo = new ViewHolderInfo();
					holderInfo.info = (MyTextView) convertView
							.findViewById(R.id.comment_no_more);
					convertView.setTag(holderInfo);
				}
			} else {
				if (itemViewType == 0) {
					columsInfoDetailHolder = (ColumsInfoDetailHolder) convertView
							.getTag();
				} else if (itemViewType == 1) {
					newsItemHolder = (NewsItemHolder) convertView.getTag();
				} else if (itemViewType == 2) {
					holderInfo = (ViewHolderInfo) convertView.getTag();
				}
			}
			if (itemViewType == 0) {
				columsInfoDetailHolder.detailTitle.setText(new_colums_infos
						.get(curPlayNo).getTitle());
			} else if (itemViewType == 1) {
				final New_Colums_Info mcolums_info = new_colums_infos
						.get(position - 1);
				mcolums_info.setTitlepic(CommonUtils.doWebpUrl(mcolums_info
						.getTitlepic()));
				final int news_type = Integer.valueOf(mcolums_info.getType());

				// imageLoader.displayImage(mcolums_info.getTitlepic(),
				// newsItemHolder.titlepic,
				// Options.getSmallImageOptions(false));
				newsItemHolder.titlepic.setTag(R.string.viewwidth,
						PixelUtil.dp2px(80));
				ImgUtils.imageLoader.displayImage(mcolums_info.getTitlepic(),
						newsItemHolder.titlepic, ImgUtils.homeImageOptions);
				// CommonUtils.zoomImage(imageLoader,
				// mcolums_info.getTitlepic(),
				// newsItemHolder.titlepic, mContext, imageCache);

				newsItemHolder.title.setText(mcolums_info.getTitle());
				newsItemHolder.newstime.setText(TimeUtil.unix2date(
						Long.valueOf(mcolums_info.getNewstime()),
						"yyyy-MM-dd HH:mm"));
				if (curPlayNo == position - 1) {
					newsItemHolder.rootView.setBackgroundColor(Color.LTGRAY);
				} else {
					newsItemHolder.rootView.setBackgroundColor(getResources()
							.getColor(R.color.light_gray));
				}
				// convertView.setOnClickListener(new OnClickListener() {
				// @Override
				// public void onClick(View arg0) {
				// if (news_type % 10 == 1) {
				// startAnimActivityByParameter(
				// New_Activity_Content_Video.class,
				// mcolums_info.getId(),
				// mcolums_info.getType(),
				// mcolums_info.getTitleurl(),
				// mcolums_info.getNewstime(),
				// mcolums_info.getTitle(),
				// mcolums_info.getTitlepic(),
				// mcolums_info.getSharedPic());
				// } else if (news_type % 10 == 2) {
				// startAnimActivityByParameter(
				// New_Activity_Content_Web.class,
				// mcolums_info.getId(),
				// mcolums_info.getType(),
				// mcolums_info.getTitleurl(),
				// mcolums_info.getNewstime(),
				// mcolums_info.getTitle(),
				// mcolums_info.getTitlepic(),
				// mcolums_info.getSharedPic());
				// }
				// }
				// });
			} else if (itemViewType == 2) {
				int padding_in_dp = 10; // 6 dps
				final float scale = getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
				holderInfo.info.setPadding(0, padding_in_px, 0, padding_in_px);
			}

			return convertView;
		}
	}

	class NewsItemHolder {
		ImageView titlepic;
		TextView title;
		TextView newstime;
		LinearLayout rootView;
	}

	// 没有更多数据
	class ViewHolderInfo {
		MyTextView info;
	}

	class ColumsInfoDetailHolder {
		TextView detailTitle;
		ImageView showBut;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == AndroidConfig.Colums_Time_resultCode) {
			time = data.getStringExtra("time");
			refreshNetDate();
		}
	}

	private void videoPlay() {
		// isCom = false;
		// video_view.release(true);

		columsVideoImage.setVisibility(View.VISIBLE);
		video_pb.setVisibility(View.VISIBLE);
		if (columsVideoView != null) {
			columsVideoView.stopPlayback();
			columsVideoController.reset();
			columsVideoView.setVideoPath(new_colums_infos.get(curPlayNo)
					.getVideoUrl());
			columsVideoView.requestFocus();
			columsVideoView.start();
		}
	}

	@Override
	public void finish() {

		if (columsVideoView != null) {
			columsVideoView.stopPlayback();
			columsVideoView = null;
		}
		System.gc();
		super.finish();
	}

	@Override
	public boolean onError(IMediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public void onCompletion(IMediaPlayer mp) {
		// TODO
		if (isFullScrenn) {

		}
		columsVideoImage.setVisibility(View.VISIBLE);
		if (curPlayNo < new_colums_infos.size() - 1) {
			curPlayNo++;
			videoPlay();
			video_pb.setVisibility(View.VISIBLE);
			myAdapter.notifyDataSetChanged();
		} else {
			columsVideoView.stopPlayback();
		}

	}

	@Override
	public void onPrepared(IMediaPlayer mp) {
		columsVideoImage.setVisibility(View.GONE);
		video_pb.setVisibility(View.GONE);
	}

	@Override
	public boolean onInfo(IMediaPlayer mp, int what, int extra) {
		switch (what) {

		case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
			Log.e("MEDIA_INFO_BUFFERING_START", "MEDIA_INFO_BUFFERING_START");
			columsVideoView.pause();
			// isload = true;
			// if (!noShowPB) {
			video_pb.setVisibility(View.VISIBLE);
			// small_video_pb.setVisibility(View.VISIBLE);
			columsVideoController.setEnabled(false);
			// noShowPB = false;
			// }
			break;
		case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
			Log.e("MEDIA_INFO_BUFFERING_END", "MEDIA_INFO_BUFFERING_END");
			// if (!this.isPause) {
			// if (content_video.getVisibility() == View.VISIBLE) {
			// content_video.setVisibility(View.GONE);
			// }
			// isload = false;
			// if (spUtil.getFirstFull() && isFullScrenn) {
			// isPlayer = false;
			// player_guide.setVisibility(View.VISIBLE);
			// noShowPB = true;
			// } else {
			columsVideoView.start();
			// }
			// video_controller.setEnabled(true);
			// goneContentVideoTempImage();

			columsVideoController.setEnabled(true);
			video_pb.setVisibility(View.GONE);
			// small_video_pb.setVisibility(View.GONE);

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
			// // small_video_pb.setVisibility(View.GONE);
			// }
			break;
		}
		return true;
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
		mSeek = columsVideoView.getCurrentPosition();
		mMaxSeek = columsVideoView.getDuration();

		long index = (long) (mSeek + msc);
		if (index > mMaxSeek)
			index = mMaxSeek;
		else if (index < 0)
			index = 0;

		columsVideoView.seekTo(index);
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
}
