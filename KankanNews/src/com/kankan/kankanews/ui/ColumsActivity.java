package com.kankan.kankanews.ui;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AbsListView.OnScrollListener;

import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.New_Colums;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.ui.item.New_Activity_Colums_Info;
import com.kankan.kankanews.ui.view.popup.SecondColumsBoard;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;
import com.umeng.socialize.utils.Log;

public class ColumsActivity extends BaseActivity implements OnClickListener {

	private PullToRefreshListView mColumsListView;
	private View mLoadingView;
	private View mRetryView;
	private ColumsAdapter mColumsAdapter;

	private ArrayList<New_Colums> mNewColums = new ArrayList<New_Colums>();

	private ArrayList<ArrayList<New_Colums>> mColumsList;

	private HolderViewOne mColumsViewOne;
	private HolderViewTwo mColumsViewTwo;

	protected Listener<JSONArray> mListenerArray = new Listener<JSONArray>() {
		@Override
		public void onResponse(JSONArray jsonObject) {
			onSuccessArray(jsonObject);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_colums);

	}

	@Override
	protected void initView() {
		mColumsListView = (PullToRefreshListView) this
				.findViewById(R.id.listview);
		mLoadingView = this.findViewById(R.id.activity_loading_view);
		mRetryView = this.findViewById(R.id.activity_retry_view);
		this.nightView = this.findViewById(R.id.night_view);
		initTitleLeftBar("栏目", R.drawable.new_ic_back);
	}

	@Override
	protected void initData() {
		boolean hasLocal = initLocalData();
		if (hasLocal) {
			showData();
		}
		if (CommonUtils.isNetworkAvailable(this)) {
			refreshNetDate();
		} else {
			if (hasLocal) {

			} else {
				this.mLoadingView.setVisibility(View.GONE);
				this.mRetryView.setVisibility(View.VISIBLE);
			}
		}
	}

	private void refreshNetDate() {
		if (CommonUtils.isNetworkAvailable(this)) {
			netUtils.getNewColumsSecondData(this.mListenerArray, mErrorListener);
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mColumsListView.onRefreshComplete();
				}
			}, 500);
		}
	}

	@Override
	protected void setListener() {
		mRetryView.setOnClickListener(this);
		setOnLeftClickLinester(this);
		mColumsListView
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

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {

	}

	@Override
	protected void onFailure(VolleyError error) {
		this.mColumsListView.onRefreshComplete();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		case R.id.activity_retry_view:
			refreshNetDate();
			break;
		}
	}

	@Override
	protected boolean initLocalData() {

		try {
			mNewColums = (ArrayList<New_Colums>) this.dbUtils
					.findAll(New_Colums.class);
			if (mNewColums != null && mNewColums.size() > 0) {
				mColumsList = getList(mNewColums);
				return true;
			}
		} catch (DbException e) {
			Log.e(this.getClass().getName(), e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	protected void saveLocalDate() {
		try {
			this.dbUtils.saveAll(mNewColums);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	private void showData() {
		this.mRetryView.setVisibility(View.GONE);
		this.mLoadingView.setVisibility(View.GONE);
		if (mColumsAdapter == null) {
			mColumsAdapter = new ColumsAdapter(this);
			mColumsListView.setAdapter(mColumsAdapter);
		} else {
			mColumsAdapter.notifyDataSetChanged();
		}
	}

	protected void onSuccessArray(JSONArray jsonObject) {
		mNewColums = new ArrayList<New_Colums>();
		JSONArray jsonArray = jsonObject;
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					JSONObject jsonObject2 = jsonArray.optJSONObject(i);
					New_Colums colums = new New_Colums();
					colums = colums.parseJSON(jsonObject2);
					mNewColums.add(colums);
				} catch (NetRequestException e) {
					e.printStackTrace();
				}
			}
			if (mNewColums != null && mNewColums.size() > 0) {
				mColumsList = getList(mNewColums);
			}
			saveLocalDate();
			showData();
		}
		mColumsListView.onRefreshComplete();
	}

	public class ColumsAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ColumsAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mColumsList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			if (mColumsList.get(position).size() == 1
					&& mColumsList.get(position).get(0).getType().equals("1")) {
				return 0;
			} else {
				return 1;
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
					mColumsViewOne = new HolderViewOne();
					convertView = mInflater.inflate(
							R.layout.new_item_colums_one, null);
					mColumsViewOne.img = (ImageView) convertView
							.findViewById(R.id.img);
					mColumsViewOne.img
							.setLayoutParams(new LinearLayout.LayoutParams(
									(int) ((ColumsActivity.this.mScreenWidth - PixelUtil
											.dp2px(10 * 2))),
									(int) ((ColumsActivity.this.mScreenWidth - PixelUtil
											.dp2px(10 * 2)) / 4)));
					if (position == 0) {
						convertView.setPadding(0, PixelUtil.dp2px(10), 0,
								PixelUtil.dp2px(10));
					}
					convertView.setTag(mColumsViewOne);
				} else if (itemViewType == 1) {
					mColumsViewTwo = new HolderViewTwo();
					convertView = mInflater.inflate(
							R.layout.new_item_colums_two, null);
					mColumsViewTwo.v1 = (ImageView) (convertView
							.findViewById(R.id.v1));
					mColumsViewTwo.v2 = (ImageView) (convertView
							.findViewById(R.id.v2));
					mColumsViewTwo.v1
							.setLayoutParams(new LinearLayout.LayoutParams(
									(int) ((ColumsActivity.this.mScreenWidth - PixelUtil
											.dp2px(10 * 3)) / 2),
									(int) ((ColumsActivity.this.mScreenWidth - PixelUtil
											.dp2px(10 * 2)) / 4)));
					mColumsViewTwo.v2
							.setLayoutParams(new LinearLayout.LayoutParams(
									(int) ((ColumsActivity.this.mScreenWidth - PixelUtil
											.dp2px(10 * 3)) / 2),
									(int) ((ColumsActivity.this.mScreenWidth - PixelUtil
											.dp2px(10 * 2)) / 4)));
					if (position == 0) {
						convertView.setPadding(0, PixelUtil.dp2px(10), 0, 0);
					}
					convertView.setTag(mColumsViewTwo);
				}

			} else {

				if (itemViewType == 0) {
					mColumsViewOne = (HolderViewOne) convertView.getTag();
					if (position == 0) {
						convertView.setPadding(0, PixelUtil.dp2px(10), 0,
								PixelUtil.dp2px(10));
					} else {
						convertView.setPadding(0, 0, 0, PixelUtil.dp2px(10));
					}
				} else if (itemViewType == 1) {
					mColumsViewTwo = (HolderViewTwo) convertView.getTag();
					if (position == 0) {
						convertView.setPadding(0, PixelUtil.dp2px(10), 0, 0);
					} else {
						convertView.setPadding(0, 0, 0, PixelUtil.dp2px(10));
					}
				}
			}

			if (itemViewType == 0) {
				final New_Colums colum = mColumsList.get(position).get(0);
				colum.setTitlePic(CommonUtils.doWebpUrl(colum.getTitlePic()));
				ImgUtils.imageLoader
						.displayImage(colum.getTitlePic(), mColumsViewOne.img,
								Options.getSmallImageOptions(false));

				mColumsViewOne.img.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						columsClick(colum);
					}
				});

			} else if (itemViewType == 1) {
				final ArrayList<New_Colums> colums = mColumsList.get(position);
				colums.get(0).setTitlePic(
						CommonUtils.doWebpUrl(colums.get(0).getTitlePic()));
				ImgUtils.imageLoader.displayImage(colums.get(0).getTitlePic(),
						mColumsViewTwo.v1, Options.getSmallImageOptions(false));

				mColumsViewTwo.v1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						columsClick(colums.get(0));
					}
				});
				if (colums.size() == 2) {

					colums.get(1).setTitlePic(
							CommonUtils.doWebpUrl(colums.get(1).getTitlePic()));
					ImgUtils.imageLoader.displayImage(colums.get(1)
							.getTitlePic(), mColumsViewTwo.v2, Options
							.getSmallImageOptions(false));

					mColumsViewTwo.v2.setVisibility(View.VISIBLE);
					mColumsViewTwo.v2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							columsClick(colums.get(1));
						}
					});
				} else {
					mColumsViewTwo.v2.setVisibility(View.INVISIBLE);
				}
			}

			return convertView;
		}

	}

	public class HolderViewOne {
		ImageView img;
	}

	public class HolderViewTwo {
		ImageView v1;
		ImageView v2;
	}

	public ArrayList<ArrayList<New_Colums>> getList(ArrayList<New_Colums> nc) {
		ArrayList<ArrayList<New_Colums>> listss = new ArrayList<ArrayList<New_Colums>>();

		boolean isFirst = true;

		ArrayList<New_Colums> ncp = null;

		for (int i = 0; i < nc.size(); i++) {
			New_Colums new_Colums = new New_Colums();
			new_Colums = nc.get(i);
			if (new_Colums.getType().equals("0") && isFirst) {
				ncp = new ArrayList<New_Colums>();
				isFirst = false;
				ncp.add(new_Colums);
				if (i >= nc.size() - 1) {
					listss.add(ncp);
				}
			} else if (new_Colums.getType().equals("0") && !isFirst) {
				isFirst = true;
				ncp.add(new_Colums);
				listss.add(ncp);
				ncp = new ArrayList<New_Colums>();
			} else if (new_Colums.getType().equals("1")) {
				if (!isFirst) {
					listss.add(ncp);
					ncp = new ArrayList<New_Colums>();
				}
				ncp = new ArrayList<New_Colums>();
				ncp.add(new_Colums);
				listss.add(ncp);
				isFirst = true;
			}
		}

		return listss;
	}

	public void refresh() {
		if (mColumsListView != null)
			mColumsListView.setRefreshing(false);
	}

	public void columsClick(New_Colums colum) {
		if (colum.getSecondNum() == 1) {
			startAnimActivityByBean(New_Activity_Colums_Info.class, "colums",
					colum);
		} else {
			if (CommonUtils.isNetworkAvailable(this)) {
				SecondColumsBoard board = new SecondColumsBoard(this, colum);
				// board.setWidth(this.mActivity.mScreenWidth * 95 / 100);
				board.setAnimationStyle(R.style.popwin_anim_style);
				board.showAtLocation(mContext.getWindow().getDecorView(),
						Gravity.BOTTOM, 0, 0);
			}
		}
	}
}
