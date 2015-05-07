package com.kankan.kankanews.ui.item;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.New_Colums;
import com.kankan.kankanews.bean.New_Colums_Info;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class New_Activity_Colums_Info extends BaseActivity implements
		OnClickListener {

	private boolean noMoreNews = false;

	private ItnetUtils instance;
	private List<New_Colums_Info> new_colums_infos = new ArrayList<New_Colums_Info>();
	private MyAdapter myAdapter;

	private New_Colums colums;
	private String time = "";

	private TextView nodata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_colums_info);

		instance = ItnetUtils.getInstance(this);
	}

	@Override
	protected void initView() {
		listview = (PullToRefreshListView) findViewById(R.id.listview);

		nodata = (TextView) findViewById(R.id.nodata);

		colums = (New_Colums) getIntent().getSerializableExtra("colums");

		// 初始化头部
		initTitle_Right_Left_bar(colums.getProgramName(), "", "", "#ffffff",
				R.drawable.new_ic_time, R.drawable.new_ic_back, "#000000",
				"#000000");
		// 头部的左右点击事件
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);
	}

	@Override
	protected void initData() {

		myAdapter = new MyAdapter();
		listview.setAdapter(myAdapter);

		initLocalData();

	}

	@Override
	protected void setListener() {
		listview.setMode(Mode.BOTH);
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

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (listview != null) {
					listview.setRefreshing(false);
				}
			}
		}, 500);
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
			// TODO Auto-generated catch block
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.com_title_bar_left_bt:
			onBackPressed();
			break;
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			startAnimActivity2ObjForResult(New_Activity_Colums_Info_Time.class,
					"colums", AndroidConfig.Colums_Time_requestCode, colums);
			break;
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!isLoadMore) {
					new_colums_infos = new ArrayList<New_Colums_Info>();
					new_colums_infos = mnew_colums_infos;
					saveDate();
					myAdapter = new MyAdapter();
					listview.setAdapter(myAdapter);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	NewsItemHolder newsItemHolder = null;
	ViewHolderInfo holderInfo = null;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (new_colums_infos != null && new_colums_infos.size() > 0) {
				if (noMoreNews) {
					return new_colums_infos.size() + 1;
				} else {
					return new_colums_infos.size();
				}
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			if (new_colums_infos != null && new_colums_infos.size() > 0
					&& new_colums_infos.size() == position && noMoreNews) {
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
				if (itemViewType == 0) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.new_colums_info_item, null);
					newsItemHolder = new NewsItemHolder();
					newsItemHolder.titlepic = (ImageView) convertView
							.findViewById(R.id.home_news_titlepic);
					newsItemHolder.title = (TextView) convertView
							.findViewById(R.id.home_news_title);
					newsItemHolder.newstime = (TextView) convertView
							.findViewById(R.id.home_news_newstime);
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
				if (itemViewType == 0) {
					newsItemHolder = (NewsItemHolder) convertView.getTag();
				} else if (itemViewType == 1) {
					holderInfo = (ViewHolderInfo) convertView.getTag();
				}
			}

			if (itemViewType == 0) {
				final New_Colums_Info mcolums_info = new_colums_infos
						.get(position);
				mcolums_info.setTitlepic(CommonUtils.doWebpUrl(mcolums_info
						.getTitlepic()));
				final int news_type = Integer.valueOf(mcolums_info.getType());

				// imageLoader.displayImage(mcolums_info.getTitlepic(),
				// newsItemHolder.titlepic,
				// Options.getSmallImageOptions(false));
				newsItemHolder.titlepic.setTag(R.string.viewwidth,
						PixelUtil.dp2px(80));
				imageLoader.displayImage(mcolums_info.getTitlepic(),
						newsItemHolder.titlepic, ImgUtils.homeImageOptions);
				// CommonUtils.zoomImage(imageLoader,
				// mcolums_info.getTitlepic(),
				// newsItemHolder.titlepic, mContext, imageCache);

				newsItemHolder.title.setText(mcolums_info.getTitle());
				newsItemHolder.newstime.setText(TimeUtil.unix2date(
						Long.valueOf(mcolums_info.getNewstime()),
						"yyyy-MM-dd HH:mm"));

				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (news_type % 10 == 1) {
							startAnimActivityByParameter(
									New_Activity_Content_Video.class,
									mcolums_info.getId(),
									mcolums_info.getType(),
									mcolums_info.getTitleurl(),
									mcolums_info.getNewstime(),
									mcolums_info.getTitlepic(),
									mcolums_info.getTitle());
						} else if (news_type % 10 == 2) {
							startAnimActivityByParameter(
									New_Activity_Content_Web.class,
									mcolums_info.getId(),
									mcolums_info.getType(),
									mcolums_info.getTitleurl(),
									mcolums_info.getNewstime(),
									mcolums_info.getTitlepic(),
									mcolums_info.getTitle());
						}
					}
				});
			} else if (itemViewType == 1) {
				int padding_in_dp = 10; // 6 dps
				final float scale = getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
				holderInfo.info.setPadding(0, padding_in_px, 0, padding_in_px);
			}

			// TODO Auto-generated method stub
			return convertView;
		}

	}

	class NewsItemHolder {
		ImageView titlepic;
		TextView title;
		TextView newstime;
	}

	// 没有更多数据
	class ViewHolderInfo {
		MyTextView info;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == AndroidConfig.Colums_Time_resultCode) {
			time = data.getStringExtra("time");
			refreshNetDate();
		}
	}

}
