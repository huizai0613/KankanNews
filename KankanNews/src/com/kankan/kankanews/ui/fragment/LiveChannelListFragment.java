package com.kankan.kankanews.ui.fragment;

import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.LiveChannelList;
import com.kankan.kankanews.bean.LiveChannelObj;
import com.kankan.kankanews.bean.LiveLiveList;
import com.kankan.kankanews.bean.SerializableObj;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class LiveChannelListFragment extends BaseFragment implements
		OnClickListener {
	private View inflate;
	private LiveHomeFragment homeFragment;
	private PullToRefreshListView mLiveChannelView;
	private LiveChannelViewAdapter mLiveChannelViewAdapter;
	private LiveChannelList mLiveChannelList;
	private String mLiveChannelListJson;
	private LinearLayout mRetryView;
	private LinearLayout mLoadingView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		initView();
		initData();
		initLinsenter();
		return inflate;
	}

	private void initLinsenter() {
		mRetryView.setOnClickListener(this);
		mLiveChannelView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
	}

	private void initView() {
		inflate = inflater.inflate(R.layout.fragment_live_channel_list, null);
		mLiveChannelView = (PullToRefreshListView) inflate
				.findViewById(R.id.live_channel_view);
		mRetryView = (LinearLayout) inflate
				.findViewById(R.id.live_channel_retry);
		mLoadingView = (LinearLayout) inflate
				.findViewById(R.id.live_channel_loading);
		initListView();
	}

	private void initData() {
		boolean flag = initLocalDate();
		if (flag) {
			showData();
			mRetryView.setVisibility(View.GONE);
			mLoadingView.setVisibility(View.GONE);
		} else {
			mRetryView.setVisibility(View.GONE);
			mLoadingView.setVisibility(View.VISIBLE);
		}
		refreshNetDate();
	}

	protected void initListView() {
		mLiveChannelView.setMode(Mode.PULL_FROM_START);
		mLiveChannelView.getLoadingLayoutProxy(true, false).setPullLabel(
				"下拉可以刷新");
		mLiveChannelView.getLoadingLayoutProxy(true, false).setRefreshingLabel(
				"刷新中…");
		mLiveChannelView.getLoadingLayoutProxy(true, false).setReleaseLabel(
				"释放后刷新");
		mLiveChannelView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						String time = TimeUtil.getTime(new Date());
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后更新:" + time);
						refreshNetDate();
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
					}
				});
	}

	private void showData() {
		if (mLiveChannelViewAdapter != null) {
			mLiveChannelViewAdapter.notifyDataSetChanged();
		} else {
			mLiveChannelViewAdapter = new LiveChannelViewAdapter();
			mLiveChannelView.setAdapter(mLiveChannelViewAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.live_channel_retry:
			refreshNetDate();
			break;
		default:
			break;
		}
	}

	@Override
	protected boolean initLocalDate() {
		try {
			SerializableObj object = (SerializableObj) this.mActivity.dbUtils
					.findFirst(Selector.from(SerializableObj.class).where(
							"classType", "=", "LiveChannelList"));
			if (object != null) {
				mLiveChannelListJson = object.getJsonStr();
				mLiveChannelList = JsonUtils.toObject(mLiveChannelListJson,
						LiveChannelList.class);
				return true;
			} else {
				return false;
			}
		} catch (DbException e) {
			DebugLog.e(e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	protected void saveLocalDate() {
		try {
			SerializableObj obj = new SerializableObj(UUID.randomUUID()
					.toString(), mLiveChannelListJson, "LiveChannelList");
			this.mActivity.dbUtils.delete(SerializableObj.class,
					WhereBuilder.b("classType", "=", "LiveChannelList"));
			this.mActivity.dbUtils.save(obj);
		} catch (DbException e) {
			DebugLog.e(e.getLocalizedMessage());
		}
	}

	@Override
	protected void refreshNetDate() {
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			netUtils.getChannelList(this.mListenerObject, this.mErrorListener);
		} else {
			mLiveChannelView.postDelayed(new Runnable() {
				@Override
				public void run() {
					mLiveChannelView.onRefreshComplete();
				}
			}, 500);
			if (mLiveChannelList == null) {
				mRetryView.setVisibility(View.VISIBLE);
				mLoadingView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void loadMoreNetDate() {
	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {
		mLiveChannelView.onRefreshComplete();
		mRetryView.setVisibility(View.GONE);
		mLoadingView.setVisibility(View.GONE);
		mLiveChannelListJson = jsonObject.toString();
		mLiveChannelList = (LiveChannelList) JsonUtils.toObject(
				mLiveChannelListJson, LiveChannelList.class);
		saveLocalDate();
		showData();
	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
	}

	@Override
	protected void onFailure(VolleyError error) {
		mLiveChannelView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mLiveChannelView.onRefreshComplete();
			}
		}, 500);
		if (mLiveChannelList == null) {
			mRetryView.setVisibility(View.VISIBLE);
			mLoadingView.setVisibility(View.GONE);
		}
	}

	public class LiveChannelViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mLiveChannelList.getFm().size()
					+ mLiveChannelList.getTv().size();
		}

		@Override
		public Object getItem(int position) {
			if (position < mLiveChannelList.getTv().size())
				return mLiveChannelList.getTv().get(position);
			else
				return mLiveChannelList.getFm().get(
						position - mLiveChannelList.getTv().size());
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final LiveChannelObj channel = (LiveChannelObj) getItem(position);
			convertView = inflate.inflate(mActivity,
					R.layout.item_live_fragment_channel_view, null);
			ImageView titlePic = (ImageView) convertView
					.findViewById(R.id.live_channel_list_titlepic);
			ImgUtils.imageLoader.displayImage(channel.getTitlepic(), titlePic,
					ImgUtils.homeImageOptions);
			MyTextView title = (MyTextView) convertView
					.findViewById(R.id.live_channel_list_livetitle);
			title.setText(channel.getTitle());
			MyTextView nextInfo = (MyTextView) convertView
					.findViewById(R.id.live_channel_list_next_info);
			nextInfo.setText("即将开始：" + channel.getTrailer_stime() + " "
					+ channel.getTrailer());
			View separation = convertView
					.findViewById(R.id.live_channel_list_separation);
			View separationLine = convertView
					.findViewById(R.id.live_channel_list_separation_line);
			if (position == mLiveChannelList.getTv().size() - 1) {
				separation.setVisibility(View.VISIBLE);
				separationLine.setVisibility(View.GONE);
			} else {
				separation.setVisibility(View.GONE);
				separationLine.setVisibility(View.VISIBLE);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					LiveChannelListFragment.this.getHomeFragment().playLive(
							channel);
				}
			});
			return convertView;
		}
	}

	public LiveHomeFragment getHomeFragment() {
		return homeFragment;
	}

	public void setHomeFragment(LiveHomeFragment homeFragment) {
		this.homeFragment = homeFragment;
	}
}
