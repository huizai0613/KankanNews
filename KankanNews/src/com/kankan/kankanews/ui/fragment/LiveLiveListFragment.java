package com.kankan.kankanews.ui.fragment;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.iss.view.pulltorefresh.PullToRefreshPinnedSectionListView;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.Keyboard;
import com.kankan.kankanews.bean.LiveLiveList;
import com.kankan.kankanews.bean.LiveLiveObj;
import com.kankan.kankanews.bean.RevelationsActicityObjBreakNewsList;
import com.kankan.kankanews.bean.SerializableObj;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.receiver.AlarmReceiver;
import com.kankan.kankanews.ui.RevelationsBreakNewsMoreActivity;
import com.kankan.kankanews.ui.view.BorderTextView;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.PinnedSectionListView;
import com.kankan.kankanews.ui.view.PinnedSectionListView.PinnedSectionListAdapter;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class LiveLiveListFragment extends BaseFragment implements
		OnClickListener {
	private View inflate;
	private LiveHomeFragment mHomeFragment;
	private PullToRefreshPinnedSectionListView mLiveListView;
	private LiveListViewAdapter mLiveListViewAdapter;
	private LiveLiveList mLiveLiveList;
	private LinearLayout mRetryView;
	private LinearLayout mLoadingView;

	private AlarmManager mAlarmManager;

	private Set<String> showIntroSet = new HashSet<String>();

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		DebugLog.e("LiveLiveListFragment onPause");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		DebugLog.e("LiveLiveListFragment onResume");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		initView();
		initDate();
		initLinsenter();
		return inflate;
	}

	private void initLinsenter() {
		mRetryView.setOnClickListener(this);
		mLiveListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
	}

	private void initView() {
		mAlarmManager = (AlarmManager) mActivity
				.getSystemService(Context.ALARM_SERVICE);
		inflate = inflater.inflate(R.layout.fragment_live_live_list, null);
		mLiveListView = (PullToRefreshPinnedSectionListView) inflate
				.findViewById(R.id.live_list_view);
		((PinnedSectionListView) mLiveListView.getRefreshableView())
				.setShadowVisible(true);
		mRetryView = (LinearLayout) inflate.findViewById(R.id.live_live_retry);
		mLoadingView = (LinearLayout) inflate
				.findViewById(R.id.live_live_loading);
		mLiveListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				refreshNetDate();
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.live_live_retry:
			refreshNetDate();
			break;
		default:
			break;
		}
	}

	private void initDate() {
		// boolean flag = initLocalDate();
		// if (flag) {
		// showData();
		// mRetryView.setVisibility(View.GONE);
		// mLoadingView.setVisibility(View.GONE);
		// } else {
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			mLoadingView.setVisibility(View.VISIBLE);
			mRetryView.setVisibility(View.GONE);
			refresh();
		} else {
			mLoadingView.setVisibility(View.GONE);
			mRetryView.setVisibility(View.VISIBLE);
		}
		// }
	}

	@Override
	protected boolean initLocalDate() {
		// try {
		// SerializableObj object = (SerializableObj) this.mActivity.dbUtils
		// .findFirst(Selector.from(SerializableObj.class).where(
		// "classType", "=", "LiveLiveList"));
		// if (object != null) {
		// mLiveLiveListJson = object.getJsonStr();
		// mLiveLiveList = JsonUtils.toObject(mLiveLiveListJson,
		// LiveLiveList.class);
		// return true;
		// } else {
		// return false;
		// }
		// } catch (DbException e) {
		// DebugLog.e(e.getLocalizedMessage());
		// }
		return false;
	}

	@Override
	protected void saveLocalDate() {
		// try {
		// SerializableObj obj = new SerializableObj(UUID.randomUUID()
		// .toString(), mLiveLiveListJson, "LiveLiveList");
		// this.mActivity.dbUtils.delete(SerializableObj.class,
		// WhereBuilder.b("classType", "=", "LiveLiveList"));
		// this.mActivity.dbUtils.save(obj);
		// } catch (DbException e) {
		// DebugLog.e(e.getLocalizedMessage());
		// }
	}

	@Override
	protected void refreshNetDate() {
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			netUtils.getLiveList(this.mListenerObject, this.mErrorListener);
		} else {
			mLiveListView.postDelayed(new Runnable() {
				@Override
				public void run() {
					mLiveListView.onRefreshComplete();
				}
			}, 500);
			mLiveLiveList = null;
			showData();
			mRetryView.setVisibility(View.VISIBLE);
			mLoadingView.setVisibility(View.GONE);

		}
	}

	@Override
	protected void loadMoreNetDate() {
	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {
		mLiveListView.onRefreshComplete();
		mRetryView.setVisibility(View.GONE);
		mLoadingView.setVisibility(View.GONE);
		showIntroSet = new HashSet<String>();
		mLiveLiveList = (LiveLiveList) JsonUtils.toObject(
				jsonObject.toString(), LiveLiveList.class);
		showData();
	}

	private void showData() {
		// if (mLiveListViewAdapter != null) {
		// mLiveListViewAdapter.notifyDataSetChanged();
		// } else {
		mLiveListViewAdapter = new LiveListViewAdapter(this.mActivity);
		mLiveListView.setAdapter(mLiveListViewAdapter);
		// }
	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
	}

	private class Item {

		public static final int ITEM = 0;
		public static final int SECTION = 1;

		public final int type;
		public final String liveType;
		public final LiveLiveObj liveObj;

		public int sectionPosition;
		public int listPosition;

		public Item(int type, LiveLiveObj liveObj, String liveType) {
			this.type = type;
			this.liveObj = liveObj;
			this.liveType = liveType;
		}

	}

	private class LiveListViewAdapter extends ArrayAdapter<Item> implements
			PinnedSectionListAdapter {

		public LiveListViewAdapter(Context context) {
			super(context, R.layout.section_live_fragment_list_view,
					android.R.id.text1);
			boolean flagLive = false;
			boolean flagPre = false;
			if (mLiveLiveList == null)
				return;
			for (char i = 0; i < mLiveLiveList.getLive().size(); i++) {
				if (!flagLive) {
					Item section = new Item(Item.SECTION, mLiveLiveList
							.getLive().get(i), mLiveLiveList.getLive().get(i)
							.getType());
					add(section);
					flagLive = true;
				}
				Item item = new Item(Item.ITEM, mLiveLiveList.getLive().get(i),
						mLiveLiveList.getLive().get(i).getType());
				add(item);
			}
			for (char i = 0; i < mLiveLiveList.getTrailer().size(); i++) {
				if (!flagPre) {
					Item section = new Item(Item.SECTION, mLiveLiveList
							.getTrailer().get(i), mLiveLiveList.getTrailer()
							.get(i).getType());
					add(section);
					flagPre = true;
				}
				Item item = new Item(Item.ITEM, mLiveLiveList.getTrailer().get(
						i), mLiveLiveList.getTrailer().get(i).getType());
				add(item);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Item item = getItem(position);
			ViewGroup layout = null;
			if (item.type == Item.SECTION) {
				layout = (LinearLayout) inflate.inflate(mActivity,
						R.layout.section_live_fragment_list_view, null);
				ImageView imageView = (ImageView) layout
						.findViewById(R.id.section_list_view_image);
				if ("正在直播".equals(item.liveType)) {
					imageView.setBackgroundResource(R.drawable.live_living);
				}
				if ("直播预告".equals(item.liveType)) {
					imageView.setBackgroundResource(R.drawable.live_preview);
				}
				layout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});
			} else {
				final LiveLiveObj liveObj = item.liveObj;
				liveObj.setOrder(liveIsPreviewed(liveObj));
				layout = (LinearLayout) inflate.inflate(mActivity,
						R.layout.item_live_fragment_list_view, null);
				View separation = layout
						.findViewById(R.id.live_live_list_separation);
				View separationLine = layout
						.findViewById(R.id.live_live_list_separation_line);
				if (position == mLiveLiveList.getLive().size()
						&& mLiveLiveList.getTrailer().size() > 0) {
					separation.setVisibility(View.VISIBLE);
					separationLine.setVisibility(View.GONE);
				} else {
					separation.setVisibility(View.GONE);
					separationLine.setVisibility(View.VISIBLE);
				}
				View introBut = layout
						.findViewById(R.id.live_live_list_intro_but);
				ImageView titlePic = (ImageView) layout
						.findViewById(R.id.live_live_list_titlepic);
				titlePic.getLayoutParams().height = (mActivity.mScreenWidth - PixelUtil
						.dp2px(25)) / 4;
				ImageView yuyueBut = (ImageView) layout
						.findViewById(R.id.live_live_list_yuyue);
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(liveObj.getWidepic()), titlePic,
						ImgUtils.homeImageOptions);
				ImageView liveType = (ImageView) layout
						.findViewById(R.id.live_live_list_livetype);
				MyTextView title = (MyTextView) layout
						.findViewById(R.id.live_live_list_livetitle);
				title.setText(liveObj.getTitle());
				MyTextView intro = (MyTextView) layout
						.findViewById(R.id.live_live_list_intro);
				intro.setText(liveObj.getIntro());
				// TODO 初始化详情
				LinearLayout keyboardIconContent = (LinearLayout) layout
						.findViewById(R.id.live_live_list_keyboard_content);

				ImageView arrowshow = (ImageView) layout
						.findViewById(R.id.live_live_list_arrowshow);
				if (showIntroSet.contains(liveObj.getId())) {
					intro.setVisibility(View.VISIBLE);
					arrowshow.setImageResource(R.drawable.ic_arrowdown);
				} else {
					intro.setVisibility(View.GONE);
					arrowshow.setImageResource(R.drawable.ic_arrowshow);
				}
				MyTextView time = (MyTextView) layout
						.findViewById(R.id.live_live_list_livetime);
				time.setText(liveObj.getTime());
				introBut.setTag(intro);
				introBut.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						MyTextView intro = (MyTextView) v.getTag();
						ImageView arrowshow = (ImageView) v
								.findViewById(R.id.live_live_list_arrowshow);
						if (showIntroSet.contains(liveObj.getId())) {
							intro.setVisibility(View.GONE);
							arrowshow.setImageResource(R.drawable.ic_arrowshow);
							showIntroSet.remove(liveObj.getId());
						} else {
							intro.setVisibility(View.VISIBLE);
							arrowshow.setImageResource(R.drawable.ic_arrowdown);
							showIntroSet.add(liveObj.getId());
						}
						mLiveListViewAdapter.notifyDataSetChanged();
					}
				});
				if ("正在直播".equals(liveObj.getType())) {
					liveType.setBackgroundResource(R.drawable.ic_live);
					liveType.getBackground().setAlpha(170); 
					yuyueBut.setVisibility(View.GONE);
					keyboardIconContent.setVisibility(View.VISIBLE);
					List<Keyboard> keyboardList = liveObj.getKeyboard();
					keyboardIconContent.removeAllViews();
					for (Keyboard keyboard : keyboardList) {
						TextView view = new BorderTextView(
								LiveLiveListFragment.this.mActivity,
								keyboard.getColor());
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						view.setLayoutParams(params);
						view.setGravity(Gravity.CENTER);
						int px3 = PixelUtil.dp2px(3);
						view.setPadding(px3, px3, px3, px3);
						view.setText(keyboard.getText());
						FontUtils.setTextViewFontSize(
								LiveLiveListFragment.this.mActivity, view,
								R.string.live_border_text_view_text_size, 1);
						view.setTextColor(Color.parseColor(keyboard.getColor()));
						keyboardIconContent.addView(view);
					}
					layout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (CommonUtils
									.isNetworkAvailable(LiveLiveListFragment.this.mActivity)) {
								if (!CommonUtils
										.isWifi(LiveLiveListFragment.this.mActivity)) {
									final InfoMsgHint dialog = new InfoMsgHint(
											LiveLiveListFragment.this.mActivity,
											R.style.MyDialog1);
									dialog.setContent(
											"亲，您现在使用的是运营商网络，继续使用可能会产生流量费用，建议改用WIFI网络",
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
											LiveLiveListFragment.this
													.getHomeFragment()
													.playLive(liveObj);
											LiveLiveListFragment.this
													.getHomeFragment()
													.setSharedObj(liveObj);
											dialog.dismiss();
										}
									});
									dialog.show();
								} else {
									LiveLiveListFragment.this.getHomeFragment()
											.playLive(liveObj);
									LiveLiveListFragment.this.getHomeFragment()
											.setSharedObj(liveObj);
								}
							}
						}
					});
				} else if ("直播预告".equals(liveObj.getType())) {
					liveType.setBackgroundResource(R.drawable.ic_next);
					liveType.getBackground().setAlpha(170); 
					yuyueBut.setVisibility(View.VISIBLE);
					keyboardIconContent.setVisibility(View.VISIBLE);
					if (liveObj.isOrder()) {
						yuyueBut.setImageResource(R.drawable.ic_unyuyue);
					} else {
						yuyueBut.setImageResource(R.drawable.ic_yuyue);
					}
					layout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
						}
					});
					yuyueBut.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							livePreview(liveObj);
						}
					});
				}
			}
			return layout;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return getItem(position).type;
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {
			return viewType == Item.SECTION;
		}

	}

	@Override
	protected void onFailure(VolleyError error) {
		mLiveListView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mLiveListView.onRefreshComplete();
			}
		}, 500);
		mLiveLiveList = null;
		showData();
		mRetryView.setVisibility(View.VISIBLE);
		mLoadingView.setVisibility(View.GONE);
	}

	public LiveHomeFragment getHomeFragment() {
		return mHomeFragment;
	}

	public void setHomeFragment(LiveHomeFragment homeFragment) {
		this.mHomeFragment = homeFragment;
	}

	public void livePreview(LiveLiveObj liveObj) {
		String[] split = liveObj.getDatetime().split(":::");
		long date2unix2 = TimeUtil.date2unix2(split[0]);
		long currentTimeMillis = System.currentTimeMillis();
		long date2unix22 = TimeUtil.date2unix2(TimeUtil
				.getTime(currentTimeMillis));
		long time = date2unix2 - date2unix22;
		if (time > 0) {
			if (!liveObj.isOrder()) {
				// 预约
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(SystemClock.elapsedRealtime());
				calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
				calendar.add(Calendar.SECOND, (int) (time / 1000));
				// calendar.add(Calendar.SECOND,
				// 10);

				Intent intent = new Intent(mActivity, AlarmReceiver.class);
				intent.putExtra("LIVE", liveObj);
				PendingIntent sender = PendingIntent.getBroadcast(mActivity,
						Integer.parseInt(liveObj.getId()), intent, 0);
				// 进行闹铃注册
				mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						calendar.getTimeInMillis(), sender);
				liveObj.setOrder(true);
				ToastUtils.Infotoast(mActivity, "预约设置成功");
				try {
					mActivity.dbUtils.saveOrUpdate(liveObj);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// 取消预约
				Intent intent = new Intent(mActivity, AlarmReceiver.class);
				intent.putExtra("LIVE", liveObj);
				PendingIntent broadcast = PendingIntent.getBroadcast(mActivity,
						Integer.parseInt(liveObj.getId()), intent, 0);
				mAlarmManager.cancel(broadcast);
				ToastUtils.Infotoast(mActivity, "预约设置取消");
				liveObj.setOrder(false);
				try {
					mActivity.dbUtils.deleteById(LiveLiveObj.class,
							liveObj.getId());
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mLiveListViewAdapter.notifyDataSetChanged();
		} else {
			ToastUtils.Infotoast(mActivity, "节目已经开始,请刷新观看");
		}
	}

	public boolean liveIsPreviewed(LiveLiveObj liveObj) {
		try {
			Object obj = mActivity.dbUtils.findById(LiveLiveObj.class,
					liveObj.getId());
			return obj != null;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			DebugLog.e(e.getLocalizedMessage());
			return false;
		}
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		super.refresh();
		mLiveListView.setRefreshing();
		this.refreshNetDate();
	}

}
