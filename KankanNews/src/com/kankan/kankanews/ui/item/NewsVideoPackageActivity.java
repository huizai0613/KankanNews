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
import com.kankan.kankanews.bean.NewsHomeModule;
import com.kankan.kankanews.bean.NewsHomeModuleItem;
import com.kankan.kankanews.bean.interfaz.CanSharedObject;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.dialog.TishiMsgHint;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.VideoViewController;
import com.kankan.kankanews.ui.view.VideoViewController.ControllerType;
import com.kankan.kankanews.ui.view.popup.CustomShareBoard;
import com.kankan.kankanews.ui.view.popup.FontColumsBoard;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
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

public class NewsVideoPackageActivity extends BaseVideoActivity implements
		OnInfoListener, OnClickListener, OnPreparedListener,
		OnCompletionListener, OnErrorListener {

	private boolean noMoreNews = false;
	private boolean isPause;
	private String mLastNewsTime = "";

	private ShareUtil shareUtil = null;
	private CustomShareBoard shareBoard;

	private NewsHomeModule mNewsVPModule;
	private NewsHomeModuleItem mHomeModuleItem;
	// private String mAppClassId;
	private NewsVideoPkgListAdapter mListAdapter;

	private PullToRefreshListView mListView;
	private TextView nodata;
	private LinearLayout mFullRootView;
	private RelativeLayout mVideoRootView;
	private VideoView mVideoPkgVideoView;
	private VideoViewController mVideoPkgVideoController;
	private ImageView mVideoPkgVideoImage;
	private ImageView mVideoPkgVideoStart;
	private LinearLayout screen_pb;

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
	private View mRetryView;

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
			mVideoPkgVideoController
					.setmControllerType(ControllerType.FullScrennController);
			mVideoPkgVideoController.changeView();
			setRightFinsh(false);
			CommonUtils.clickevent(mContext, "action", "放大",
					AndroidConfig.video_fullscreen_event);
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			this.getWindow().setAttributes(attrs);
			this.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			mVideoRootView.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mVideoPkgVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH);
			isFullScrenn = true;
			((RelativeLayout.LayoutParams) mFullRootView.getLayoutParams()).topMargin = 0;
			if (mVideoPkgVideoView != null
					&& mVideoPkgVideoImage.getVisibility() == View.GONE)
				mVideoPkgVideoView.start();
			mVideoPkgVideoView.getHolder().setFixedSize(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		} else {
			mVideoPkgVideoController
					.setmControllerType(ControllerType.SmallController);
			mVideoPkgVideoController.changeView();
			setRightFinsh(true);
			isFullScrenn = false;
			((RelativeLayout.LayoutParams) mFullRootView.getLayoutParams()).topMargin = PixelUtil
					.dp2px(44);
			// mActivity.bottomBarVisible(View.VISIBLE);
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			this.getWindow().setAttributes(attrs);
			// 取消全屏设置
			this.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			mVideoRootView.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT,
					(int) (this.mScreenWidth / 16 * 9)));
			mVideoPkgVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_package);
	}

	@Override
	protected void initView() {
		mListView = (PullToRefreshListView) findViewById(R.id.video_package_list_view);
		nodata = (TextView) findViewById(R.id.nodata);
		mVideoRootView = (RelativeLayout) findViewById(R.id.video_package_video_root_view);
		mVideoRootView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (this.mScreenWidth / 16 * 9)));
		mVideoPkgVideoView = (VideoView) findViewById(R.id.video_package_video_view);
		mVideoPkgVideoController = (VideoViewController) findViewById(R.id.video_package_video_controller);
		mVideoPkgVideoImage = (ImageView) findViewById(R.id.video_package_video_image);
		mVideoPkgVideoStart = (ImageView) findViewById(R.id.video_package_video_start);
		mFullRootView = (LinearLayout) findViewById(R.id.play_root_view);

		screen_pb = (LinearLayout) findViewById(R.id.screnn_pb);
		screen_pb.setVisibility(View.GONE);
		video_pb = (LinearLayout) findViewById(R.id.video_pb);
		small_video_pb = (LinearLayout) findViewById(R.id.small_video_pb);
		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
		nightView = findViewById(R.id.night_view);
		mRetryView = findViewById(R.id.main_bg);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		initTitleBar("", R.drawable.new_ic_back, R.drawable.ic_share);
	}

	@Override
	protected void initData() {
		mListAdapter = new NewsVideoPkgListAdapter();
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (view == null || mVideoPkgVideoView == null)
					return;
				if (position - 1 == mNewsVPModule.getList().size())
					return;
				curPlayNo = position - 1;
				mListAdapter.notifyDataSetChanged();
				view.setSelected(true);
				videoPlay();
			}
		});
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		mAM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		mVideoPkgVideoController.setPlayerControl(mVideoPkgVideoView);
		mVideoPkgVideoController.setActivity_Content(this);
		mVideoPkgVideoView.setIsNeedRelease(false);
		mHomeModuleItem = (NewsHomeModuleItem) this.getIntent()
				.getSerializableExtra("_NEWS_HOME_MODEULE_ITEM_");

		if (CommonUtils.isNetworkAvailable(this)) {
			refreshNetDate();
		} else {
			this.mRetryView.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void setListener() {
		this.mRetryView.setOnClickListener(this);
		mVideoPkgVideoStart.setOnClickListener(this);
		mVideoPkgVideoView.setOnCompletionListener(this);
		mVideoPkgVideoView.setOnErrorListener(this);
		mVideoPkgVideoView.setOnPreparedListener(this);
		mVideoPkgVideoView.setOnInfoListener(this);
		mVideoPkgVideoView.setOnClickListener(this);
		mVideoPkgVideoController.setOnClickListener(this);

		mListView.setMode(Mode.PULL_FROM_END);
		mListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						refreshNetDate();
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						loadMoreNetDate();
					}
				});
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);
		setOnContentClickLinester(this);
	}

	protected void refreshNetDate() {
		isLoadMore = false;
		noMoreNews = false;
		netUtils.getNewsList(mHomeModuleItem.getAppclassid(), mLastNewsTime,
				getVideoPackageListener, getColumsInfoErrorListener);
	}

	protected void loadMoreNetDate() {
		isLoadMore = true;
		if (mNewsVPModule != null && mNewsVPModule.getList().size() > 0) {
			netUtils.getNewsList(mNewsVPModule.getAppclassid(), mNewsVPModule
					.getList().get(mNewsVPModule.getList().size() - 1)
					.getNewstime(), getVideoPackageListener,
					getColumsInfoErrorListener);
		} else {
			mListView.onRefreshComplete();
			ToastUtils.Infotoast(mContext, "没有更多新闻可以加载");
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
		case R.id.main_bg:
			if (CommonUtils.isNetworkAvailable(mContext)) {
				refreshNetDate();
			}
			break;
		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		case R.id.title_bar_right_img:
			shareUtil = new ShareUtil(new CanSharedObject() {
				@Override
				public void setSharedPic(String sharepic) {
					mNewsVPModule.getList().get(0).getSharedPic();
				}

				@Override
				public String getTitleurl() {
					return mNewsVPModule.getShare_url();
				}

				@Override
				public String getTitlepic() {
					return mNewsVPModule.getList().get(0).getTitlepic();
				}

				@Override
				public String getTitle() {
					return mNewsVPModule.getList().get(0).getTitle();
				}

				@Override
				public String getSharedPic() {
					return mNewsVPModule.getList().get(0).getSharedPic();
				}

				@Override
				public String getShareTitle() {
					return mNewsVPModule.getList().get(0).getShareTitle();
				}

				@Override
				public String getIntro() {
					return mNewsVPModule.getList().get(0).getIntro();
				}

				@Override
				public String getShareIntro() {
					return mNewsVPModule.getList().get(0).getIntro();
				}
			}, mContext);
			shareBoard = new CustomShareBoard(this, shareUtil, this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.video_package_video_start:
			videoPlay();
			mVideoPkgVideoStart.setVisibility(View.GONE);
			break;
		case R.id.video_package_video_controller:
			if (!mVideoPkgVideoController.isShow()
					&& video_pb.getVisibility() != View.VISIBLE)
				mVideoPkgVideoController.show();
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isPause = false;
		setFullHandler.sendEmptyMessageDelayed(SET_FULL_MESSAGE, 1000);
		if (mVideoPkgVideoView != null) {
			mVideoPkgVideoView.start();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
		if (mVideoPkgVideoView != null) {
			mVideoPkgVideoView.pause();
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
			mListView.onRefreshComplete();
		}
	};
	// 处理网络成功
	protected Listener<JSONObject> getVideoPackageListener = new Listener<JSONObject>() {
		@Override
		public void onResponse(JSONObject jsonObject) {
			if (jsonObject != null && !jsonObject.equals("")) {
				NewsHomeModule tmpModule = JsonUtils.toObject(
						jsonObject.toString(), NewsHomeModule.class);
				mRetryView.setVisibility(View.GONE);
				if (!tmpModule.getTitle().trim().equals(""))
					NewsVideoPackageActivity.this.setContentTextView(tmpModule
							.getTitle());
				if (!isLoadMore) {
					curPlayNo = 0;
					mNewsVPModule = tmpModule;
					saveDate();
					mListAdapter = new NewsVideoPkgListAdapter();
					mListView.setAdapter(mListAdapter);
					// TODO
					if (mNewsVPModule.getList().size() > 0) {
						for (int i = 0; i < mNewsVPModule.getList().size(); i++) {
							if (mNewsVPModule.getList().get(i).getO_cmsid()
									.equals(mHomeModuleItem.getO_cmsid())) {
								curPlayNo = i;
								break;
							}
						}
						mVideoPkgVideoStart.setVisibility(View.GONE);
						videoPlay();
					}
				} else {
					if (tmpModule.getList().size() == 0) {
						noMoreNews = true;
					} else {
						mNewsVPModule.getList().addAll(tmpModule.getList());
						if (mVideoPkgVideoImage.getVisibility() == View.VISIBLE) {
							curPlayNo++;
							mVideoPkgVideoStart.setVisibility(View.GONE);
							videoPlay();
							video_pb.setVisibility(View.VISIBLE);
						}
					}
					mListAdapter.notifyDataSetChanged();
				}
				nodata.setVisibility(View.GONE);
			} else {
				if (!isLoadMore) {
					ToastUtils.Infotoast(mContext, "该日期暂无内容");
				} else {
					noMoreNews = true;
				}
				mListAdapter.notifyDataSetChanged();
			}

			mListView.onRefreshComplete();
		}
	};

	protected void saveDate() {
		// try {
		// if (dbUtils.tableIsExist(New_Colums_Info.class)) {
		// dbUtils.delete(New_Colums_Info.class,
		// WhereBuilder.b("classId", "=", colums.getClassId()));
		// }
		// // dbUtils.deleteAll(New_Colums_Info.class);
		// dbUtils.saveAll(new_colums_infos);
		// } catch (DbException e) {
		// e.printStackTrace();
		// }
	}

	NewsItemHolder newsItemHolder = null;
	ViewHolderInfo holderInfo = null;
	static ColumsInfoDetailHolder columsInfoDetailHolder = null;

	class NewsVideoPkgListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mNewsVPModule != null && mNewsVPModule.getList().size() > 0) {
				if (noMoreNews) {
					return mNewsVPModule.getList().size() + 1;
				} else {
					return mNewsVPModule.getList().size();
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
			if (mNewsVPModule != null && mNewsVPModule.getList().size() > 0
					&& mNewsVPModule.getList().size() == position && noMoreNews) {
				return 1;
			} else {
				return 0;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int itemViewType = getItemViewType(position);

			if (convertView == null) {
				if (itemViewType == -1) {
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
				} else if (itemViewType == 0) {
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
				} else if (itemViewType == 1) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.comment_nomore, null);
					holderInfo = new ViewHolderInfo();
					holderInfo.info = (MyTextView) convertView
							.findViewById(R.id.comment_no_more);
					convertView.setTag(holderInfo);
				}
			} else {
				if (itemViewType == -1) {
					columsInfoDetailHolder = (ColumsInfoDetailHolder) convertView
							.getTag();
				} else if (itemViewType == 0) {
					newsItemHolder = (NewsItemHolder) convertView.getTag();
				} else if (itemViewType == 1) {
					holderInfo = (ViewHolderInfo) convertView.getTag();
				}
			}
			if (itemViewType == -1) {
				columsInfoDetailHolder.detailTitle.setText(mNewsVPModule
						.getList().get(curPlayNo).getTitle());
				columsInfoDetailHolder.detailTime.setText(TimeUtil.unix2date(
						Long.valueOf(mNewsVPModule.getList().get(curPlayNo)
								.getNewstime()), "yyyy-MM-dd HH:mm"));
				columsInfoDetailHolder.detailCal.setText(mNewsVPModule
						.getList().get(curPlayNo).getNewstime()
						+ "期");
				mLastNewsTime = mNewsVPModule.getList().get(curPlayNo)
						.getNewstime();
				if (mNewsVPModule.getList().get(curPlayNo).getIntro() == null
						|| mNewsVPModule.getList().get(curPlayNo).getIntro()
								.trim().equals("")) {
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
					columsInfoDetailHolder.detailContent.setText(mNewsVPModule
							.getList().get(curPlayNo).getIntro());
					columsInfoDetailHolder.detailContentOmit
							.setText(mNewsVPModule.getList().get(curPlayNo)
									.getIntro());
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

			} else if (itemViewType == 0) {
				setItemTitleFontSize();
				final NewsHomeModuleItem moduleItem = mNewsVPModule.getList()
						.get(position);
				moduleItem.setTitlepic(CommonUtils.doWebpUrl(moduleItem
						.getTitlepic()));

				newsItemHolder.titlepic.setTag(R.string.viewwidth,
						PixelUtil.dp2px(80));
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(moduleItem.getTitlepic()),
						newsItemHolder.titlepic, ImgUtils.homeImageOptions);

				newsItemHolder.title.setText(moduleItem.getTitle());
				newsItemHolder.newstime.setText(TimeUtil.unix2date(
						Long.valueOf(moduleItem.getNewstime()),
						"yyyy-MM-dd HH:mm"));
				if (curPlayNo == position) {
					newsItemHolder.rootView.setBackgroundColor(getResources()
							.getColor(R.color.thin_gray));
				} else {
					newsItemHolder.rootView.setBackgroundColor(getResources()
							.getColor(R.color.white));
				}
			} else if (itemViewType == 1) {
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
		if (shareUtil != null) {
			UMSsoHandler ssoHandler = shareUtil.getmController().getConfig()
					.getSsoHandler(requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}
	}

	public void setItemTitleFontSize() {
		FontUtils.setTextViewFontSize(this, newsItemHolder.title,
				R.string.home_news_text_size, spUtil.getFontSizeRadix());
	}

	private void videoPlay() {
		mVideoPkgVideoImage.setVisibility(View.VISIBLE);
		video_pb.setVisibility(View.VISIBLE);

		if (CommonUtils.isNetworkAvailable(this)) {
			if (!CommonUtils.isWifi(this)) {
				if (!spUtil.isFlow()) {
					final TishiMsgHint dialog = new TishiMsgHint(this,
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
					final InfoMsgHint dialog = new InfoMsgHint(this,
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
							if (mVideoPkgVideoView != null) {
								mVideoPkgVideoView.stopPlayback();
								mVideoPkgVideoController.reset();
								mVideoPkgVideoView
										.setVideoPath(mNewsVPModule.getList()
												.get(curPlayNo).getVideourl());
								mVideoPkgVideoController.setTitle(mNewsVPModule
										.getList().get(curPlayNo).getTitle());
								mVideoPkgVideoView.requestFocus();
								mVideoPkgVideoView.start();
							}
							dialog.dismiss();
						}
					});
					dialog.show();

				}

			} else {
				if (mVideoPkgVideoView != null) {
					Log.e("new_colums_infos.get(curPlayNo).getVideoUrl()",
							mNewsVPModule.getList().get(curPlayNo)
									.getVideourl());
					mVideoPkgVideoView.stopPlayback();
					mVideoPkgVideoController.reset();
					mVideoPkgVideoView.setVideoPath(mNewsVPModule.getList()
							.get(curPlayNo).getVideourl());
					mVideoPkgVideoController.setTitle(mNewsVPModule.getList()
							.get(curPlayNo).getTitle());
					mVideoPkgVideoView.requestFocus();
					mVideoPkgVideoView.start();

					NetUtils.getInstance(mContext).getAnalyse(
							this,
							"column",
							mNewsVPModule.getList().get(curPlayNo).getTitle(),
							mNewsVPModule.getList().get(curPlayNo)
									.getTitleurl());
				}
			}
		} else {
			final TishiMsgHint dialog = new TishiMsgHint(this,
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

	@Override
	public void finish() {
		if (mVideoPkgVideoView != null) {
			mVideoPkgVideoView.stopPlayback();
			mVideoPkgVideoView = null;
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
		mVideoPkgVideoImage.setVisibility(View.VISIBLE);
		if (curPlayNo < mNewsVPModule.getList().size() - 1) {
			curPlayNo++;
			videoPlay();
			video_pb.setVisibility(View.VISIBLE);
			mListAdapter.notifyDataSetChanged();
		} else {
			mVideoPkgVideoStart.setVisibility(View.VISIBLE);
			mVideoPkgVideoView.stopPlayback();
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
				mVideoPkgVideoImage.setVisibility(View.GONE);
			}
		}, 600);
	}

	@Override
	public boolean onInfo(IMediaPlayer mp, int what, int extra) {
		switch (what) {
		case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
			mVideoPkgVideoView.pause();
			video_pb.setVisibility(View.VISIBLE);
			mVideoPkgVideoController.setEnabled(false);
			break;
		case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (!this.isPause)
				mVideoPkgVideoView.start();
			mVideoPkgVideoController.setEnabled(true);
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
		mSeek = mVideoPkgVideoView.getCurrentPosition();
		mMaxSeek = mVideoPkgVideoView.getDuration();

		long index = (long) (mSeek + msc);
		if (index > mMaxSeek)
			index = mMaxSeek;
		else if (index < 0)
			index = 0;

		mVideoPkgVideoView.seekTo(index);
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
		clip.setText(mNewsVPModule.getList().get(curPlayNo).getTitleurl());
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

}
