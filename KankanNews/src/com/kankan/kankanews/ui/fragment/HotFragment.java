package com.kankan.kankanews.ui.fragment;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.Hot_News;
import com.kankan.kankanews.bean.Hot_NewsList;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.ui.MainActivity;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;

public class HotFragment extends BaseFragment implements OnItemClickListener {

	private View inflate;

	private LinkedList<Hot_News> hot_News_list = new LinkedList<Hot_News>();

	private Hot_NewsList hot_NewsList;

	private MyAdapt myAdapt;
	// private LinearLayout main_bg;
	// private int lastId;
	// 是否有本地数据
	private boolean initLocalDate;

	// private View default_bg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);

		inflate = inflater.inflate(R.layout.hotfragment, null);
		// 统计
		initAnalytics(AndroidConfig.new_hot_page);

		initTitle_Left_bar(inflate, R.drawable.icon_title_menu,
				R.drawable.icon_title_logo, R.drawable.icon_title_search);

		hot_NewsList = new Hot_NewsList();

		listview = (PullToRefreshListView) inflate.findViewById(R.id.listview);

		// default_bg = inflate.findViewById(R.id.default_bg);
		//
		// main_bg = (LinearLayout) inflate.findViewById(R.id.main_bg);
		//
		// main_bg.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// isLoadMore = false;
		// ItnetUtils instance = ItnetUtils.getInstance(mActivity);
		// instance.getHotData("", mListener, mErrorListener);
		// lastTime = System.currentTimeMillis();
		// }
		// });

		initListView(Mode.BOTH);
		listview.setMode(Mode.PULL_DOWN_TO_REFRESH);

		listview.setOnItemClickListener(this);
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
				// TODO Auto-generated method stub
			}
		});

		myAdapt = new MyAdapt();
		listview.setAdapter(myAdapt);

		initLocalDate();

		if (CommonUtils.isNetworkAvailable(mActivity)) {
			refreshNetDate();
		} else {
			if (!initLocalDate) {
				// main_bg.setVisibility(View.VISIBLE);
			}
		}

		// 头部的左侧点击菜单事件
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainActivity) getActivity()).getSide_drawer().showMenu();
			}
		});

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				refreshNetDate();
			}
		}, 1000);
		listview.setRefreshing(false);
		inflate.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		return inflate;
	}

	@Override
	protected boolean initLocalDate() {

		try {
			List<Hot_News> mHot_News = mActivity.dbUtils
					.findAll(Hot_News.class);
			if (mHot_News != null) {
				hot_News_list = new LinkedList<Hot_News>(mHot_News);
				initLocalDate = true;
				// yexiangyu yexiangyu 标记
				listview.setBackgroundColor(Color.parseColor("#f5f5f5"));

				myAdapt.notifyDataSetChanged();
				// 初始化id数组
				// init_arrayid();
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	long lastTime;
	boolean refreshOK;

	@Override
	protected void refreshNetDate() {
		if (!refreshOK || System.currentTimeMillis() - lastTime > 60 * 1000) {
			isLoadMore = false;
			ItnetUtils instance = ItnetUtils.getInstance(mActivity);
			instance.getHotData("", mListenerObject, mErrorListener);
			lastTime = System.currentTimeMillis();
		} else {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					listview.onRefreshComplete();
				}
			}, 500);

		}

	}

	@Override
	protected void loadMoreNetDate() {
		isLoadMore = true;
		ItnetUtils instance = ItnetUtils.getInstance(mActivity);
		instance.getHotData(hot_News_list.getLast().getId(), mListenerObject,
				mErrorListener);
	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {
		if (hot_NewsList == null) {
			hot_NewsList = new Hot_NewsList();
		}

		try {
			hot_NewsList.parseJSON(jsonObject);
		} catch (NetRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		refreshOK = true;
		addData();
	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
		// TODO Auto-generated method stub

	}

	private void addData() {
		if (!isLoadMore) {
			// getmTopNewsList = mainFragmentBean.getmTopNewsList();
			hot_News_list = hot_NewsList.getHot_Newss();
			saveLocalDate();
			// yexiangyu 标记
			listview.setBackgroundColor(Color.parseColor("#f5f5f5"));
		} else {
			hot_News_list.addAll(hot_NewsList.getHot_Newss());
		}

		myAdapt.notifyDataSetChanged();
		listview.onRefreshComplete();
	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		ToastUtils.Errortoast(mActivity, "网络不可用");
		if (initLocalDate) {
			// yexiangyu 标记
			refreshOK = false;
			listview.setBackgroundColor(Color.parseColor("#f5f5f5"));

			// main_bg.setVisibility(View.GONE);
		} else {
			// main_bg.setVisibility(View.VISIBLE);
		}
		listview.onRefreshComplete();
	}

	@Override
	protected void saveLocalDate() {
		new Thread() {
			public void run() {
				if (hot_News_list != null) {
					try {
						mActivity.dbUtils.deleteAll(Hot_News.class);
						mActivity.dbUtils.saveOrUpdateAll(hot_News_list);
					} catch (DbException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	public class MyAdapt extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return hot_News_list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return hot_News_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		View view;
		HotHolder holder;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Hot_News hot_News_Item = hot_News_list.get(position);

			if (convertView == null) {
				view = inflate.inflate(mActivity,
						R.layout.hotfragment_item_new, null);

				holder = new HotHolder();

				holder.hot_imageview = (ImageView) view
						.findViewById(R.id.hot_imageview);

				holder.hot_imageview
						.setLayoutParams(new RelativeLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								(int) (mActivity.mScreenWidth / 1.5)));

				holder.hot_position = (MyTextView) view
						.findViewById(R.id.hot_position);
				holder.hot_title = (MyTextView) view
						.findViewById(R.id.hot_title);
				holder.hot_onclick = (MyTextView) view
						.findViewById(R.id.hot_onclick);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (HotHolder) view.getTag();
			}

			holder.hot_position.setText((position + 1) + "");
			holder.hot_title.setText(hot_News_Item.getTitle());
			holder.hot_onclick.setText(CommonUtils.NumFormat(hot_News_Item
					.getOnclick()));

			imageLoader.displayImage(hot_News_Item.getTitlepic(),
					holder.hot_imageview, Options.getBigImageOptions(null));

			return view;
		}
	}

	private class HotHolder {
		ImageView hot_imageview;
		MyTextView hot_position;
		MyTextView hot_title;
		MyTextView hot_onclick;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// String newid = hot_News_list.get(position-1).getId()+"";
		int length = hot_News_list.size();
		int[] arrayid = new int[length];
		for (int i = 0; i < length; i++) {
			arrayid[i] = Integer.parseInt(hot_News_list.get(i).getId());
		}

		startAnimActivityById(New_Activity_Content_Video.class, position - 1,
				"arrayid", arrayid);
	}

}
