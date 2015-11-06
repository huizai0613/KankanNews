package com.kankan.kankanews.ui.fragment;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.New_Colums;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.ui.item.New_Activity_Colums_Info;
import com.kankan.kankanews.ui.view.popup.SecondColumsBoard;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.NetUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.socialize.utils.Log;

public class New_ColumsFragment extends BaseFragment {

	private NetUtils instance;
	private View inflate;
	private ImageAdapter adapter = new ImageAdapter();

	private ArrayList<New_Colums> new_colums = new ArrayList<New_Colums>();

	private ArrayList<ArrayList<New_Colums>> lists;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		inflate = inflater.inflate(R.layout.fragment_colums, null);

//		initTitleBar(inflate, "栏目");

		listview = (PullToRefreshListView) inflate.findViewById(R.id.listview);
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

		instance = NetUtils.getInstance(mActivity);

		if (CommonUtils.isNetworkAvailable(mActivity)) {
			initNetData();
		} else {
			initLocalDate();
		}
		return inflate;
	}

	private void initNetData() {
		instance.getNewColumsSecondData(mListenerArray, mErrorListener);
	}

	@Override
	protected boolean initLocalDate() {

		try {
			new_colums = (ArrayList<New_Colums>) mActivity.dbUtils
					.findAll(New_Colums.class);
			if (new_colums != null && new_colums.size() > 0) {
				lists = getList(new_colums);
				listview.setAdapter(adapter);
				return true;
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			Log.e(this.getClass().getName(), e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	protected void saveLocalDate() {
		try {
			mActivity.dbUtils.saveAll(new_colums);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void refreshNetDate() {

		if (CommonUtils.isNetworkAvailable(mActivity)) {
			initNetData();
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
		new_colums = new ArrayList<New_Colums>();
		JSONArray jsonArray = jsonObject;
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					JSONObject jsonObject2 = jsonArray.optJSONObject(i);
					New_Colums colums = new New_Colums();
					colums = colums.parseJSON(jsonObject2);
					new_colums.add(colums);
				} catch (NetRequestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			if (new_colums != null && new_colums.size() > 0) {
				lists = getList(new_colums);
			}

			saveLocalDate();
			// adapter = new ImageAdapter();
			listview.setAdapter(adapter);
		}

		listview.onRefreshComplete();

	}

	@Override
	protected void onFailure(VolleyError error) {
		listview.onRefreshComplete();
	}

	HolderViewOne viewOne;
	HolderViewTwo viewTwo;

	/*
	 * 适配器的定义,要继承BaseAdapter
	 */
	public class ImageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return lists.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			if (lists.get(position).size() == 1
					&& lists.get(position).get(0).getType().equals("1")) {
				return 0;
			} else {
				return 1;
			}

		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int itemViewType = getItemViewType(position);
			if (convertView == null) {
				if (itemViewType == 0) {
					viewOne = new HolderViewOne();
					convertView = inflater.inflate(
							R.layout.new_item_colums_one, null);
					viewOne.img = (ImageView) convertView
							.findViewById(R.id.img);
					viewOne.img.setLayoutParams(new LinearLayout.LayoutParams(
							(int) ((mActivity.mScreenWidth - PixelUtil
									.dp2px(10 * 2))),
							(int) ((mActivity.mScreenWidth - PixelUtil
									.dp2px(10 * 2)) / 4)));
					if (position == 0) {
						convertView.setPadding(0, PixelUtil.dp2px(10), 0,
								PixelUtil.dp2px(10));
					}
					convertView.setTag(viewOne);
				} else if (itemViewType == 1) {
					viewTwo = new HolderViewTwo();
					convertView = inflater.inflate(
							R.layout.new_item_colums_two, null);
					viewTwo.v1 = (ImageView) (convertView.findViewById(R.id.v1));
					viewTwo.v2 = (ImageView) (convertView.findViewById(R.id.v2));
					viewTwo.v1.setLayoutParams(new LinearLayout.LayoutParams(
							(int) ((mActivity.mScreenWidth - PixelUtil
									.dp2px(10 * 3)) / 2),
							(int) ((mActivity.mScreenWidth - PixelUtil
									.dp2px(10 * 2)) / 4)));
					viewTwo.v2.setLayoutParams(new LinearLayout.LayoutParams(
							(int) ((mActivity.mScreenWidth - PixelUtil
									.dp2px(10 * 3)) / 2),
							(int) ((mActivity.mScreenWidth - PixelUtil
									.dp2px(10 * 2)) / 4)));
					if (position == 0) {
						convertView.setPadding(0, PixelUtil.dp2px(10), 0, 0);
					}
					convertView.setTag(viewTwo);
				}

			} else {

				if (itemViewType == 0) {
					viewOne = (HolderViewOne) convertView.getTag();
					if (position == 0) {
						convertView.setPadding(0, PixelUtil.dp2px(10), 0,
								PixelUtil.dp2px(10));
					} else {
						convertView.setPadding(0, 0, 0, PixelUtil.dp2px(10));
					}
				} else if (itemViewType == 1) {
					viewTwo = (HolderViewTwo) convertView.getTag();
					if (position == 0) {
						convertView.setPadding(0, PixelUtil.dp2px(10), 0, 0);
					} else {
						convertView.setPadding(0, 0, 0, PixelUtil.dp2px(10));
					}
				}
			}

			if (itemViewType == 0) {
				final New_Colums colum = lists.get(position).get(0);
				colum.setTitlePic(CommonUtils.doWebpUrl(colum.getTitlePic()));
				ImgUtils.imageLoader.displayImage(colum.getTitlePic(),
						viewOne.img, Options.getSmallImageOptions(false));

				viewOne.img.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						columsClick(colum);
						// startAnimActivityByBean(New_Activity_Colums_Info.class,
						// "colums", colum);
					}
				});

			} else if (itemViewType == 1) {
				final ArrayList<New_Colums> colums = lists.get(position);
				// CommonUtils.zoomImage(imageLoader,
				// N_C_S.get(0).getProgramPic(), viewTwo.v1, mActivity);

				colums.get(0).setTitlePic(
						CommonUtils.doWebpUrl(colums.get(0).getTitlePic()));
				ImgUtils.imageLoader.displayImage(colums.get(0).getTitlePic(),
						viewTwo.v1, Options.getSmallImageOptions(false));

				viewTwo.v1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						columsClick(colums.get(0));
						// startAnimActivityByBean(New_Activity_Colums_Info.class,
						// "colums", colums.get(0));
					}
				});
				if (colums.size() == 2) {

					colums.get(1).setTitlePic(
							CommonUtils.doWebpUrl(colums.get(1).getTitlePic()));
					ImgUtils.imageLoader.displayImage(colums.get(1)
							.getTitlePic(), viewTwo.v2, Options
							.getSmallImageOptions(false));

					viewTwo.v2.setVisibility(View.VISIBLE);
					viewTwo.v2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							columsClick(colums.get(1));
							// startAnimActivityByBean(
							// New_Activity_Colums_Info.class, "colums",
							// colums.get(1));
						}
					});
				} else {
					viewTwo.v2.setVisibility(View.INVISIBLE);
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
		if (listview != null)
			listview.setRefreshing(false);
	}

	public void columsClick(New_Colums colum) {
		if (colum.getSecondNum() == 1) {
			startAnimActivityByBean(New_Activity_Colums_Info.class, "colums",
					colum);
		} else {
			SecondColumsBoard board = new SecondColumsBoard(this.mActivity,
					colum);
			// board.setWidth(this.mActivity.mScreenWidth * 95 / 100);
			board.setAnimationStyle(R.style.popwin_anim_style);
			board.showAtLocation(inflate, Gravity.BOTTOM, 0, 0);
		}
	}
}
