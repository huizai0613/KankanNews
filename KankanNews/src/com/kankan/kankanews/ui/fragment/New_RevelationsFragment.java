package com.kankan.kankanews.ui.fragment;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView.ScaleType;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.New_News_Top;
import com.kankan.kankanews.bean.RevelationsHomeList;
import com.kankan.kankanews.bean.SerializableObj;
import com.kankan.kankanews.ui.view.AutoScrollViewPager;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class New_RevelationsFragment extends BaseFragment implements
		OnClickListener {

	private View inflate;
	private View retryView;
	private View loadingView;

	private PullToRefreshListView revelationsListView;
	private RevelationsHomeList revelationsHomeList;
	private String revelationsHomeListJson;

	private RevelationsListAdapter revelationsListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		inflate = inflater.inflate(R.layout.new_fragment_revelations, null);

		initview();
		initLister();
		initData();
		return inflate;

	}

	public void initview() {
		initTitleBar(inflate, "报料大厅");
		revelationsListView = (PullToRefreshListView) inflate
				.findViewById(R.id.revelations_list_view);
		retryView = inflate.findViewById(R.id.revelations_retry_view);
		loadingView = inflate.findViewById(R.id.revelations_loading_view);

		initListView();
	}

	protected void initListView() {
		// TODO Auto-generated method stub
		revelationsListView.setMode(Mode.PULL_FROM_START);
		revelationsListView.getLoadingLayoutProxy(true, false).setPullLabel(
				"下拉可以刷新");
		revelationsListView.getLoadingLayoutProxy(true, false).setReleaseLabel(
				"释放后刷新");
		revelationsListView
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
						loadMoreNetDate();
					}
				});
	}

	private void initLister() {
		retryView.setOnClickListener(this);
	}

	private void initData() {
		boolean flag = this.initLocalDate();
		if (flag) {
			showData(true);
			loadingView.setVisibility(View.GONE);
			revelationsListView.showHeadLoadingView();
		}
		refreshNetDate();
	}

	private void showData(boolean needRefresh) {
		revelationsListView.onRefreshComplete();
		if (needRefresh) {
			revelationsListAdapter = new RevelationsListAdapter();
			revelationsListView.setAdapter(revelationsListAdapter);
		} else {
			revelationsListAdapter.notifyDataSetChanged();
		}

	}

	private class ActivityPageChangeListener implements
			ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {

		}

	}

	private class ActivityViewPageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return revelationsHomeList.getActivity().size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Log.e("position", position + "");
			ImageView imageView = new ImageView(
					New_RevelationsFragment.this.mActivity);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					(int) (mActivity.mScreenWidth / 3)));
			ImgUtils.imageLoader.displayImage(revelationsHomeList.getActivity()
					.get(position).getTitlepic(), imageView,
					ImgUtils.homeImageOptions);
			container.addView(imageView);
			return imageView;
		}
	}

	private class RevelationsListTopHolder {
		AutoScrollViewPager activityViewPager;
		LinearLayout activityContent;
		MyTextView activityTitle;
	}

	private class RevelationsListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// return revelationsHomeList.getBreaknews().size() + 1;
			return 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 && revelationsHomeList.getActivity() != null
					&& revelationsHomeList.getActivity().size() != 0) {
				return 0;
			} else
				return 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int itemViewType = getItemViewType(position);

			if (convertView == null) {
				// if (itemViewType == 0) {
				convertView = inflate.inflate(mActivity,
						R.layout.item_revelations_list_activity, null);
				RevelationsListTopHolder topHolder = new RevelationsListTopHolder();
				topHolder.activityViewPager = (AutoScrollViewPager) convertView
						.findViewById(R.id.revelations_activity_viewpager);
				topHolder.activityContent = (LinearLayout) convertView
						.findViewById(R.id.revelations_activity_content);
				topHolder.activityTitle = (MyTextView) convertView
						.findViewById(R.id.revelations_activity_title);
				topHolder.activityViewPager
						.setOnPageChangeListener(new ActivityPageChangeListener());
				topHolder.activityViewPager
						.setAdapter(new ActivityViewPageAdapter());
				convertView.setTag(topHolder);
				// }
			} else {

			}
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.revelations_retry_view:
			refreshNetDate();
		}
	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {
		revelationsHomeListJson = jsonObject.toString();
		boolean needRefresh = (revelationsHomeList == null);
		// ToastUtils.Infotoast(getActivity(), jsonObject.toString());
		revelationsHomeList = JsonUtils.toObject(revelationsHomeListJson,
				RevelationsHomeList.class);
		if (revelationsHomeList != null) {
			loadingView.setVisibility(View.GONE);
			saveLocalDate();
			showData(needRefresh);
		}
	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		ToastUtils.Errortoast(getActivity(), "获取报料列表失败");
		loadingView.setVisibility(View.GONE);
		if (revelationsHomeList == null)
			retryView.setVisibility(View.VISIBLE);
	}

	@Override
	protected boolean initLocalDate() {
		// TODO Auto-generated method stub
		try {
			SerializableObj object = (SerializableObj) mActivity.dbUtils
					.findFirst(Selector.from(SerializableObj.class).where(
							"classType", "=", "RevelationsHomeList"));
			if (object != null) {
				revelationsHomeListJson = object.getJsonStr();
				revelationsHomeList = JsonUtils.toObject(
						revelationsHomeListJson, RevelationsHomeList.class);
				return true;
			} else {
				return false;
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void saveLocalDate() {
		// TODO Auto-generated method stub
		try {
			SerializableObj obj = new SerializableObj("0",
					revelationsHomeListJson, "RevelationsHomeList");
			mActivity.dbUtils.delete(SerializableObj.class,
					WhereBuilder.b("classType", "=", "RevelationsHomeList"));
			mActivity.dbUtils.save(obj);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void refreshNetDate() {
		// TODO Auto-generated method stub
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			this.netUtils.getRevelationsHomeList(mListenerObject,
					mErrorListener);
		} else {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					revelationsListView.onRefreshComplete();
				}
			}, 500);
		}
	}

	@Override
	protected void loadMoreNetDate() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		super.refresh();
		revelationsListView.setSelection(0);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				revelationsListView.setmCurrentMode(Mode.PULL_FROM_START);
				revelationsListView.setRefreshing(false);
			}
		}, 100);
	}

}
