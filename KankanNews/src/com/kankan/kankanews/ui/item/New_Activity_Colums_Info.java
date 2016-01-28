package com.kankan.kankanews.ui.item;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import android.content.ClipboardManager;
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
import android.view.Gravity;
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
import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.base.view.SildingFinishLayout;
import com.kankan.kankanews.bean.New_Colums;
import com.kankan.kankanews.bean.New_Colums_Info;
import com.kankan.kankanews.bean.New_Colums_Second;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.dialog.TishiMsgHint;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.VideoViewController;
import com.kankan.kankanews.ui.view.VideoViewController.ControllerType;
import com.kankan.kankanews.ui.view.popup.CustomShareBoard;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.NetUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.umeng.socialize.sso.UMSsoHandler;

public class New_Activity_Colums_Info extends BaseVideoActivity implements
		OnInfoListener, OnClickListener, OnPreparedListener,
		OnCompletionListener, OnErrorListener {

	private boolean noMoreNews = false;
	private boolean isPause;

	private ShareUtil shareUtil = null;
	private static CustomShareBoard shareBoard;

	private NetUtils instance;
	private List<New_Colums_Info> new_colums_infos = new ArrayList<New_Colums_Info>();
	private MyAdapter myAdapter;

	private New_Colums colums;
	private New_Colums_Second secondColums;
	private String time = "";

	private TextView nodata;
	private RelativeLayout rootView;
	private VideoView columsVideoView;
	private VideoViewController columsVideoController;
	private ImageView columsVideoImage;
	private ImageView columsVideoStart;
	private LinearLayout screen_pb;
	private ImageView columsImage;
	private MyTextView columsTitle;
	private ImageView calendarBut;
	private ImageView columsShareBut;
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

	private SildingFinishLayout mSildingFinishLayout;

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
			columsVideoController
					.setmControllerType(ControllerType.FullScrennController);
			columsVideoController.changeView();
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
			columsVideoView.setmRootViewHeight((int) (this.mScreenWidth));
			columsVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH);
			isFullScrenn = true;
			if (columsVideoView != null
					&& columsVideoImage.getVisibility() == View.GONE)
				columsVideoView.start();
			columsVideoView.getHolder().setFixedSize(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

		} else {
			columsVideoController
					.setmControllerType(ControllerType.SmallController);
			columsVideoController.changeView();
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
			columsVideoView
					.setmRootViewHeight((int) (this.mScreenWidth / 16 * 9));
			columsVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_colums_info);
		// mSildingFinishLayout = (SildingFinishLayout)
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
		// mSildingFinishLayout.setTouchView(listview);
	}

	@Override
	protected void initView() {
		listview = (PullToRefreshListView) findViewById(R.id.colums_info_list_view);
		nodata = (TextView) findViewById(R.id.nodata);
		rootView = (RelativeLayout) findViewById(R.id.colums_video_root_view);
		columsVideoView = (VideoView) findViewById(R.id.colums_video_view);
		columsVideoController = (VideoViewController) findViewById(R.id.colums_video_controller);
		columsVideoImage = (ImageView) findViewById(R.id.colums_video_image);
		columsVideoStart = (ImageView) findViewById(R.id.colums_video_start);
		columsImage = (ImageView) findViewById(R.id.colums_image);
		columsTitle = (MyTextView) findViewById(R.id.colums_title);
		calendarBut = (ImageView) findViewById(R.id.calendar_but);
		columsShareBut = (ImageView) findViewById(R.id.colums_share_but);
		backBut = (ImageView) findViewById(R.id.colums_info_back);

		screen_pb = (LinearLayout) findViewById(R.id.screnn_pb);
		screen_pb.setVisibility(View.GONE);
		video_pb = (LinearLayout) findViewById(R.id.video_pb);
		small_video_pb = (LinearLayout) findViewById(R.id.small_video_pb);
		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
		nightView = findViewById(R.id.night_view);

		colums = (New_Colums) getIntent().getSerializableExtra("colums");
		secondColums = (New_Colums_Second) getIntent().getSerializableExtra(
				"secondColum");
		if (colums == null) {
			colums = new New_Colums();
			colums.setClassId(secondColums.getId());
			colums.setTitle(secondColums.getName());
		}
		rootView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (this.mScreenWidth / 16 * 9)));
		columsVideoView.setmRootViewHeight((int) (this.mScreenWidth / 16 * 9));

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
	}

	@Override
	protected void initData() {
		instance = NetUtils.getInstance(this);

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
				view.setSelected(true);
				if (columsInfoDetailHolder.showBut != null)
					columsInfoDetailHolder.showBut
							.setBackgroundResource(R.drawable.ic_arrowshow);
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
		columsTitle.setText(colums.getTitle());

		if (CommonUtils.isNetworkAvailable(this)) {
			refreshNetDate();
		} else {
			// initLocalData();
		}

	}

	@Override
	protected void setListener() {
		calendarBut.setOnClickListener(this);
		columsShareBut.setOnClickListener(this);
		backBut.setOnClickListener(this);
		columsVideoStart.setOnClickListener(this);
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

	protected boolean initLocalData() {
		try {
			if (dbUtils.tableIsExist(New_Colums_Info.class)) {
				new_colums_infos = dbUtils.findAll(Selector.from(
						New_Colums_Info.class).where("classId", "=",
						colums.getClassId()));
				if (new_colums_infos != null && new_colums_infos.size() > 0) {
					myAdapter.notifyDataSetChanged();
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return false;
	}

	protected void refreshNetDate() {
		isLoadMore = false;
		noMoreNews = false;
		instance.getNewColumsInfoData(colums.getClassId(), time, "",
				getColumsInfoListener, getColumsInfoErrorListener);
	}

	protected void loadMoreNetDate() {
		isLoadMore = true;
		if (new_colums_infos != null && new_colums_infos.size() > 0) {
			instance.getNewColumsInfoData(colums.getClassId(), time,
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
		case R.id.colums_video_start:
			videoPlay();
			columsVideoStart.setVisibility(View.GONE);
			break;
		case R.id.colums_share_but:
			if (new_colums_infos.size() == 0
					&& curPlayNo >= new_colums_infos.size())
				break;
			shareUtil = new ShareUtil(new_colums_infos.get(curPlayNo), this);
			// 一键分享
			shareBoard = new CustomShareBoard((BaseActivity) this, shareUtil,
					this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(this.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case R.id.colums_info_back:
			onBackPressed();
			break;
		case R.id.colums_video_controller:
			if (!columsVideoController.isShow()
					&& video_pb.getVisibility() != View.VISIBLE)
				columsVideoController.show();
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
		isPause = false;
		setFullHandler.sendEmptyMessageDelayed(SET_FULL_MESSAGE, 1000);
		if (columsVideoView != null) {
			columsVideoView.start();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
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
						colums_info.setClassId(colums.getClassId());
						mnew_colums_infos.add(colums_info);
					} catch (NetRequestException e) {
						e.printStackTrace();
					}
				}
				if (!isLoadMore) {
					curPlayNo = 0;
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
						columsVideoStart.setVisibility(View.GONE);
						videoPlay();
					}
				} else {
					new_colums_infos.addAll(mnew_colums_infos);
					if (columsVideoImage.getVisibility() == View.VISIBLE) {
						curPlayNo++;
						columsVideoStart.setVisibility(View.GONE);
						videoPlay();
						video_pb.setVisibility(View.VISIBLE);
					}
					myAdapter.notifyDataSetChanged();
				}
				nodata.setVisibility(View.GONE);
			} else {
				if (!isLoadMore) {
					// ToastUtils.Infotoast(mContext, "暂无"+time+"记录");
					// nodata.setVisibility(View.VISIBLE);
					// nodata.setText("暂无" + time + "记录");
					// columsVideoView.stopPlayback();
					// columsVideoImage.setVisibility(View.VISIBLE);
					// new_colums_infos.clear();
					ToastUtils.Infotoast(mContext, "该日期暂无内容");
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
						WhereBuilder.b("classId", "=", colums.getClassId()));
			}
			// dbUtils.deleteAll(New_Colums_Info.class);
			dbUtils.saveAll(new_colums_infos);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	NewsItemHolder newsItemHolder = null;
	ViewHolderInfo holderInfo = null;
	static ColumsInfoDetailHolder columsInfoDetailHolder = null;

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
					columsInfoDetailHolder.detailTitleRootView = (RelativeLayout) convertView
							.findViewById(R.id.colums_detail_title_root_view);
					columsInfoDetailHolder.detailTitle = (TextView) convertView
							.findViewById(R.id.colums_detail_title);
					columsInfoDetailHolder.showBut = (ImageView) convertView
							.findViewById(R.id.colums_detail_show);
					columsInfoDetailHolder.detailTime = (TextView) convertView
							.findViewById(R.id.colums_detail_time);
					columsInfoDetailHolder.detailCal = (TextView) convertView
							.findViewById(R.id.colums_detail_cal);
					columsInfoDetailHolder.detailContentOmit = (TextView) convertView
							.findViewById(R.id.colums_detail_content_omit);
					columsInfoDetailHolder.detailContent = (TextView) convertView
							.findViewById(R.id.colums_detail_content);
					convertView.setTag(columsInfoDetailHolder);
				} else if (itemViewType == 1) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.new_colums_info_item, null);
					newsItemHolder = new NewsItemHolder();
					newsItemHolder.rootView = convertView
							.findViewById(R.id.conlums_item_root_view);
					newsItemHolder.titlepic = (ImageView) convertView
							.findViewById(R.id.conlums_item_titlepic);
					newsItemHolder.title = (TextView) convertView
							.findViewById(R.id.conlums_item_title);
					newsItemHolder.newstime = (TextView) convertView
							.findViewById(R.id.conlums_item_newstime);
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
				setInfoDetailHolderFontSize();
				columsInfoDetailHolder.detailTitle.setText(new_colums_infos
						.get(curPlayNo).getTitle());
				columsInfoDetailHolder.detailTime.setText(TimeUtil.unix2date(
						Long.valueOf(new_colums_infos.get(curPlayNo)
								.getNewstime()), "yyyy-MM-dd HH:mm"));
				columsInfoDetailHolder.detailCal.setText(new_colums_infos.get(
						curPlayNo).getEpisode()
						+ "期");
				time = new_colums_infos.get(curPlayNo).getEpisode();
				if (new_colums_infos.get(curPlayNo).getIntro() == null
						|| new_colums_infos.get(curPlayNo).getIntro().trim()
								.equals("")) {
					columsInfoDetailHolder.showBut.setVisibility(View.GONE);
					columsInfoDetailHolder.detailContentOmit
							.setVisibility(View.GONE);
					columsInfoDetailHolder.detailContent
							.setVisibility(View.GONE);
					columsInfoDetailHolder.detailTitleRootView
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
								}
							});
				} else {
					columsInfoDetailHolder.showBut.setVisibility(View.VISIBLE);
					columsInfoDetailHolder.detailContentOmit
							.setVisibility(View.VISIBLE);
					columsInfoDetailHolder.detailContent
							.setVisibility(View.GONE);
					columsInfoDetailHolder.detailContent
							.setText(new_colums_infos.get(curPlayNo).getIntro());
					columsInfoDetailHolder.detailContentOmit
							.setText(new_colums_infos.get(curPlayNo).getIntro());
					columsInfoDetailHolder.detailTitleRootView
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									if (columsInfoDetailHolder.detailContentOmit
											.getVisibility() == View.GONE) {
										columsInfoDetailHolder.showBut
												.setBackgroundResource(R.drawable.ic_arrowshow);
										columsInfoDetailHolder.detailContentOmit
												.setVisibility(View.VISIBLE);
										columsInfoDetailHolder.detailContent
												.setVisibility(View.GONE);
									} else {
										columsInfoDetailHolder.showBut
												.setBackgroundResource(R.drawable.ic_arrowdown);
										columsInfoDetailHolder.detailContentOmit
												.setVisibility(View.GONE);
										columsInfoDetailHolder.detailContent
												.setVisibility(View.VISIBLE);
									}
								}
							});
				}

			} else if (itemViewType == 1) {
				setItemTitleFontSize();
				final New_Colums_Info mcolums_info = new_colums_infos
						.get(position - 1);
				mcolums_info.setTitlepic(CommonUtils.doWebpUrl(mcolums_info
						.getTitlepic()));
				final int news_type = Integer.valueOf(mcolums_info.getType());

				newsItemHolder.titlepic.setTag(R.string.viewwidth,
						PixelUtil.dp2px(80));
				ImgUtils.imageLoader.displayImage(mcolums_info.getTitlepic(),
						newsItemHolder.titlepic, ImgUtils.homeImageOptions);

				newsItemHolder.title.setText(mcolums_info.getTitle());
				newsItemHolder.newstime.setText(TimeUtil.unix2date(
						Long.valueOf(mcolums_info.getNewstime()),
						"yyyy-MM-dd HH:mm"));
				if (curPlayNo == position - 1) {
					newsItemHolder.rootView.setBackgroundColor(getResources()
							.getColor(R.color.thin_gray));
				} else {
					newsItemHolder.rootView.setBackgroundColor(getResources()
							.getColor(R.color.white));
				}
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
		View rootView;
	}

	// 没有更多数据
	class ViewHolderInfo {
		MyTextView info;
	}

	class ColumsInfoDetailHolder {
		TextView detailTitle;
		ImageView showBut;
		TextView detailTime;
		TextView detailCal;
		TextView detailContentOmit;
		TextView detailContent;
		RelativeLayout detailTitleRootView;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == AndroidConfig.Colums_Time_resultCode) {
			time = data.getStringExtra("time");
			refreshNetDate();
		}
		if (shareUtil != null) {
			UMSsoHandler ssoHandler = shareUtil.getmController().getConfig()
					.getSsoHandler(requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}
	}

	public void setInfoDetailHolderFontSize() {
		// TODO Auto-generated method stub
		FontUtils
				.setTextViewFontSize(this, columsInfoDetailHolder.detailTitle,
						R.string.colums_info_title_text_size,
						spUtil.getFontSizeRadix());
		FontUtils.setTextViewFontSize(this,
				columsInfoDetailHolder.detailContentOmit,
				R.string.colums_info_detail_text_size,
				spUtil.getFontSizeRadix());
		FontUtils.setTextViewFontSize(this,
				columsInfoDetailHolder.detailContent,
				R.string.colums_info_detail_text_size,
				spUtil.getFontSizeRadix());
	}

	public void setItemTitleFontSize() {
		// TODO Auto-generated method stub
		FontUtils.setTextViewFontSize(this, newsItemHolder.title,
				R.string.home_news_text_size, spUtil.getFontSizeRadix());
	}

	private void videoPlay() {
		columsVideoImage.setVisibility(View.VISIBLE);
		video_pb.setVisibility(View.VISIBLE);

		if (CommonUtils.isNetworkAvailable(this)) {
			if (!CommonUtils.isWifi(this)) {
				final InfoMsgHint dialog = new InfoMsgHint(this,
						R.style.MyDialog1);
				dialog.setContent("亲，您现在使用的是运营商网络，继续使用可能会产生流量费用，建议改用WIFI网络",
						"", "继续播放", "取消");
				dialog.setCancleListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.setOKListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (columsVideoView != null) {
							columsVideoView.stopPlayback();
							columsVideoController.reset();
							columsVideoView.setVideoPath(new_colums_infos.get(
									curPlayNo).getVideoUrl());
							columsVideoController.setTitle(new_colums_infos
									.get(curPlayNo).getTitle());
							columsVideoView.requestFocus();
							columsVideoView.start();

							NetUtils.getInstance(mContext).getAnalyse(
									New_Activity_Colums_Info.this,
									"video",
									new_colums_infos.get(curPlayNo).getTitle(),
									new_colums_infos.get(curPlayNo)
											.getTitleurl());
						}
						dialog.dismiss();
					}
				});
				dialog.show();
			} else {
				if (columsVideoView != null) {
					columsVideoView.stopPlayback();
					columsVideoController.reset();
					columsVideoView.setVideoPath(new_colums_infos
							.get(curPlayNo).getVideoUrl());
					columsVideoController.setTitle(new_colums_infos.get(
							curPlayNo).getTitle());
					columsVideoView.requestFocus();
					columsVideoView.start();

					NetUtils.getInstance(mContext).getAnalyse(
							New_Activity_Colums_Info.this, "video",
							new_colums_infos.get(curPlayNo).getTitle(),
							new_colums_infos.get(curPlayNo).getTitleurl());
				}
			}
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
		columsVideoImage.setVisibility(View.VISIBLE);
		if (curPlayNo < new_colums_infos.size() - 1) {
			curPlayNo++;
			videoPlay();
			video_pb.setVisibility(View.VISIBLE);
			myAdapter.notifyDataSetChanged();
		} else {
			columsVideoStart.setVisibility(View.VISIBLE);
			columsVideoView.stopPlayback();
			if (isFullScrenn) {
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
		}

	}

	@Override
	public void onPrepared(IMediaPlayer mp) {
		video_pb.setVisibility(View.GONE);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				columsVideoImage.setVisibility(View.GONE);
			}
		}, 600);
	}

	@Override
	public boolean onInfo(IMediaPlayer mp, int what, int extra) {
		switch (what) {
		case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
			columsVideoView.pause();
			video_pb.setVisibility(View.VISIBLE);
			columsVideoController.setEnabled(false);
			break;
		case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (!this.isPause)
				columsVideoView.start();
			columsVideoController.setEnabled(true);
			video_pb.setVisibility(View.GONE);
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
			float mOldX = 0, mOldY = 0;
			if (e1 != null) {
				mOldX = e1.getX();
				mOldY = e1.getY();
			}
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
		// else if (ev.getRawY() > (this.mScreenWidth / 16 * 9 + 100)) {
		// if (this.mApplication.getMainActivity() != null) {
		// boolean flag = mSildingFinishLayout.onTouch(ev);
		// if (flag)
		// return flag;
		// }
		// }
		return super.dispatchTouchEvent(ev);
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

	@Override
	public void copy2Clip() {
		// TODO Auto-generated method stub
		ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		clip.setText(new_colums_infos.get(curPlayNo).getTitleurl());
		ToastUtils.Infotoast(this, "已将链接复制进黏贴板");
	}

	@Override
	public void shareReBack() {
		// TODO Auto-generated method stub
		super.shareReBack();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
	}

	@Override
	public void netChanged() {
		// TODO Auto-generated method stub

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

			setFullHandler.sendEmptyMessageDelayed(SET_FULL_MESSAGE, 1000);
		}
	}

	// 从小屏到全屏
	public void samllScrenntoFull() {

		CommonUtils.clickevent(mContext, "action", "放大",
				AndroidConfig.video_fullscreen_event);

		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

			setFullHandler.sendEmptyMessageDelayed(SET_FULL_MESSAGE, 1000);
		}
	}

}
