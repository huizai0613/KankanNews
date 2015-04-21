package com.kankan.kankanews.ui.fragment;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.receiver.AlarmReceiver;
import com.kankan.kankanews.sina.AccessTokenKeeper;
import com.kankan.kankanews.sina.Constants;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.view.CustomShareBoard;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankan.kankanews.utils.XunaoLog;
import com.kankan.kankannews.bean.interfaz.CanSharedBySina;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;
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

public class New_LivePlayFragment extends BaseFragment implements
		OnInfoListener, IWeiboHandler.Response, OnCompletionListener, OnErrorListener, OnClickListener,
		OnPreparedListener, CanSharedBySina {

	private View inflate;
	private RelativeLayout smallrootview;
	private RelativeLayout rootview;
	private VideoView video_view;
	private View video_view_click;
	private View main_bg;
	private ImageView video_player;
	private ImageView smallscrenn_but;
	private MyTextView livePlayTitle;
	private LinearLayout video_pb;
	private LinearLayout screnn_pb;
	private ImageView fullscrenn_but;
	private ArrayList<New_LivePlay> mLivePlayList = new ArrayList<New_LivePlay>();
	private MyAdapter myAdapter;
	private ImageView liveShareBut;
	

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
	/** 微博微博分享接口实例 */
	private IWeiboShareAPI mWeiboShareAPI = null;
	
	private ShareUtil shareUtil = null;

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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int width = mActivity.wm.getDefaultDisplay().getWidth();
		int height = mActivity.wm.getDefaultDisplay().getHeight();
		WindowManager.LayoutParams attrs = mActivity.getWindow()
				.getAttributes();
		if (width > height) {
			if (shareBoard != null && shareBoard.isShowing()) {
				shareBoard.dismiss();
			}
			fullscrenn_but.setVisibility(View.GONE);
			smallscrenn_but.setVisibility(View.VISIBLE);
			mActivity.bottomBarVisible(View.GONE);
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			mActivity.getWindow().setAttributes(attrs);
			mActivity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			smallrootview.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			video_view.setFull(true);
			isFullstate = true;
			video_view.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
		} else {
			fullscrenn_but.setVisibility(View.VISIBLE);
			smallscrenn_but.setVisibility(View.GONE);
			mActivity.bottomBarVisible(View.VISIBLE);
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			mActivity.getWindow().setAttributes(attrs);
			// 取消全屏设置
			mActivity.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			smallrootview.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT,
					(int) (mActivity.mScreenWidth / 16 * 9)));
			video_view.setFull(false);
			isFullstate = false;
			video_view.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isFirst && mActivity.curTouchTab == mActivity.tab_two) {
			if (CommonUtils.isNetworkAvailable(mActivity)) {
				// if (!isSelectPlay) {
				// Uri getmUri = video_view.getmUri();
				// if (getmUri != null) {
				// video_view.release(true);
				// video_view.setVideoURI(getmUri);
				// }
				// } else {
				refreshNetDate();
				// }
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
		video_view = (VideoView) inflate.findViewById(R.id.video_view);
		video_view_click = inflate.findViewById(R.id.video_view_click);
		main_bg = inflate.findViewById(R.id.main_bg);
		video_player = (ImageView) inflate.findViewById(R.id.video_player);
		livePlayTitle = (MyTextView) inflate.findViewById(R.id.livePlayTitle);
		video_pb = (LinearLayout) inflate.findViewById(R.id.video_pb);
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
		
		// 初始化头部
//		initTitle_Right_Left_bar(inflate, "看看直播", "", "", "#ffffff", R.drawable.new_ic_more, 0,
//				"#000000", "#000000");
		
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
		

		// mShareType = getIntent().getIntExtra(KEY_SHARE_TYPE, SHARE_CLIENT);
		// 创建微博分享接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this.getActivity(), Constants.APP_KEY);
		// 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
		// 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
		// NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
		mWeiboShareAPI.registerApp();
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
		// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
		// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
		// 失败返回 false，不调用上述回调
		if (savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(this.getActivity().getIntent(), this);
		}
		
		return inflate;
	}

	public void initViewLayout() {
		smallrootview.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				(int) (mActivity.mScreenWidth / 16 * 9)));
	}

	public void initLinsenter() {
		video_view.setOnErrorListener(this);
		video_view.setOnCompletionListener(this);
		video_view.setOnPreparedListener(this);
		video_view.setOnInfoListener(this);
		video_view.setOnClickListener(this);
		video_player.setOnClickListener(this);
		video_view_click.setOnClickListener(this);
		fullscrenn_but.setOnClickListener(this);
		smallscrenn_but.setOnClickListener(this);
		liveShareBut.setOnClickListener(this);
// 		头部的左右点击事件
//		setOnRightClickLinester(this);
	}

	@Override
	protected boolean initLocalDate() {
		try {
			localDate = mActivity.dbUtils.findAll(New_LivePlay.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
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
					video_view.pause();
					video_player.setVisibility(View.VISIBLE);
					Uri getmUri = video_view.getmUri();
					if (getmUri != null) {
						video_view.release(true);
					}
					video_view.setVideoPath(news.getStreamurl());
					mActivity
							.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
					nowLiveNew = news;
					break;
				} else {
					if (Integer.parseInt(news.getZid()) == selectPlayID) {
						unStart = false;
						livePlayTitle.setText("正在播放:" + news.getTitle());
						isSelectPlay = false;
						video_view.release(true);
						video_view.setVideoPath(news.getStreamurl());
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
								// 更改直播频道
								video_view.release(true);
								video_view.setVideoPath(new_LivePlay
										.getStreamurl());
								video_player.setVisibility(View.VISIBLE);
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
	public void onPrepared(MediaPlayer mp) {

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		video_player.setVisibility(View.VISIBLE);
		ToastUtils.Errortoast(mActivity, "视频播放有误请重新刷新");
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		video_player.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			XunaoLog.yLog().d("视频开始加载");
			video_pb.setVisibility(View.VISIBLE);
			// video_view.pause();
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			XunaoLog.yLog().d("视频加载完毕");
			video_pb.setVisibility(View.GONE);
			if (mActivity.curTouchTab == mActivity.tab_two) {
				video_view.start();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						video_view.pause();
					}
				}, 100);
			}
			// video_view.start();
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		switch (id) {
		case R.id.live_share_but:
			
			shareUtil = new ShareUtil(nowLiveNew, this.getActivity());
			// 一键分享
			shareBoard = new CustomShareBoard((BaseActivity)this.mActivity, shareUtil, this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(this.getActivity().getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;
		}
		
		switch (id) {
		case R.id.video_player:// 播放视频
			video_view.start();
			video_player.setVisibility(View.GONE);
			break;
		case R.id.video_view_click:// 暂停
			if (video_view.isPlaying()) {
				video_view.pause();
			}
			video_player.setVisibility(View.VISIBLE);
			break;
		case R.id.fullscrenn_but:// 大屏
			mActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		case R.id.smallscrenn_but:// 小屏
			mActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (video_view != null) {
			if (video_view.isPlaying()) {
				video_view.pause();
			}
		}
		if (video_player != null) {
			video_player.setVisibility(View.VISIBLE);
		}

	}

	public VideoView getVideoView() {
		// video_view.release(true);
		return video_view;
	}

	// 刷新
	public void refresh() {
		listview.setRefreshing(false);
	}
	
	@Override
	public void onResponse(BaseResponse arg0) {
		switch (arg0.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			ToastUtils.Infotoast(this.mActivity, "分享成功");
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			ToastUtils.Infotoast(this.mActivity, "分享取消");
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			ToastUtils.Infotoast(this.mActivity, "分享失败");
			break;
		}

	}
	
	public void sendSingleMessage() {
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 1. 初始化微博的分享消息
				WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
				// 创建媒体消息
				// weiboMultiMessage.mediaObject = getVideoObj();
				TextObject textObject = new TextObject();
				textObject.text = nowLiveNew.getTitlelist() + "-看看新闻 "
						+ nowLiveNew.getTitleurl() + " （分享自@看看新闻网） ";
				ImageObject imageObject = new ImageObject();
				Bitmap shareImg = ImgUtils.getNetImage(nowLiveNew.getTitlepic());
				if(shareImg == null){
					BitmapDrawable draw=(BitmapDrawable) getResources().getDrawable(R.drawable.ic_logo);
					shareImg=draw.getBitmap();
				}
				imageObject.setImageObject(shareImg);
				weiboMultiMessage.textObject = textObject;
				weiboMultiMessage.imageObject = imageObject;
				// 2. 初始化从第三方到微博的消息请求
				SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
				// 用transaction唯一标识一个请求
				request.transaction = String.valueOf(System.currentTimeMillis());
				request.multiMessage = weiboMultiMessage;

				AuthInfo authInfo = new AuthInfo(New_LivePlayFragment.this.mActivity, Constants.APP_KEY,
						Constants.REDIRECT_URL, Constants.SCOPE);
				Oauth2AccessToken accessToken = AccessTokenKeeper
						.readAccessToken(New_LivePlayFragment.this.mActivity.getApplicationContext());
				String token = "";
				if (accessToken != null) {
					token = accessToken.getToken();
				}
				boolean hasSucceed = mWeiboShareAPI.sendRequest(New_LivePlayFragment.this.mActivity, request, authInfo, token,
						new WeiboAuthListener() {

							@Override
							public void onWeiboException(WeiboException arg0) {
								ToastUtils.Infotoast(New_LivePlayFragment.this.mActivity, "分享失败");
							}

							@Override
							public void onComplete(Bundle bundle) {
								// TODO Auto-generated method stub
								Oauth2AccessToken newToken = Oauth2AccessToken
										.parseAccessToken(bundle);
								AccessTokenKeeper.writeAccessToken(
										New_LivePlayFragment.this.mActivity.getApplicationContext(), newToken);
								// Toast.makeText(
								// getApplicationContext(),
								// "onAuthorizeComplete token = "
								// + newToken.getToken(), 0).show();
								ToastUtils.Infotoast(New_LivePlayFragment.this.mActivity, "分享成功");
							}

							@Override
							public void onCancel() {
								ToastUtils.Infotoast(New_LivePlayFragment.this.mActivity, "分享取消");
							}
						});
//				mWeiboShareAPI.handleWeiboResponse(arg0, arg1)
//				if(hasSucceed)
//					ToastUtils.Infotoast(New_LivePlayFragment.this.mActivity, "分享成功");
			}
			
		}).start();
		
	}

}
