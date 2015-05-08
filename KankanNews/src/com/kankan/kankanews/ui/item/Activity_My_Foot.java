package com.kankan.kankanews.ui.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.Content_News;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.ui.view.MarqueeTextView;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ViewHolderUtil;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class Activity_My_Foot extends BaseActivity implements OnClickListener {

	private SwipeListView example_lv_list;
	private List<Content_News> mContent_News;
	private LayoutInflater inflater;
	private MyFootAdapter offlineNewAdapter;

	// 无看过记录时的界面
	private LinearLayout no_collect_layout;

	// 删除界面的 底部按钮
	private View my_collect_bottom_line;
	private LinearLayout my_collect_bottom_linearlayout;
	private MyTextView my_collect_bottom_allselect;
	private MyTextView my_collect_bottom_delete;

	int curPosition = -1;
	private boolean isShowCheckBox;

	// 记录checkbox的选择
	private HashSet<Content_News> itemSelected = new HashSet<Content_News>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_foot);

		// 统计
		initAnalytics(AndroidConfig.history_page);

		initTitle_Right_Left_bar("观看记录", "", "取消", "#ffffff",
				R.drawable.icon_delete, R.drawable.icon_black_big,"#000000","#000000");
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);

		com_title_bar_right_tv.setVisibility(View.GONE);
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	protected void initView() {
		example_lv_list = (SwipeListView) findViewById(R.id.example_lv_list);

		my_collect_bottom_line = findViewById(R.id.my_collect_bottom_line);
		my_collect_bottom_linearlayout = (LinearLayout) findViewById(R.id.my_collect_bottom_linearlayout);
		my_collect_bottom_allselect = (MyTextView) findViewById(R.id.my_collect_bottom_allselect);
		my_collect_bottom_delete = (MyTextView) findViewById(R.id.my_collect_bottom_delete);

		no_collect_layout = (LinearLayout) findViewById(R.id.no_collect_layout);
	}

	@Override
	protected void initData() {
		try {
			mContent_News = dbUtils.findAll(Selector.from(Content_News.class)
					.where("looktime", ">", "0").orderBy("looktime", true));

			if (mContent_News != null && mContent_News.size() > 0) {
				example_lv_list
						.setSwipeListViewListener(new MySwipeListViewListener());
				example_lv_list.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
				example_lv_list.setAnimationTime(100);
				example_lv_list.setOffsetLeft(mScreenWidth
						- PixelUtil.dp2px(80));
				example_lv_list.setSwipeCloseAllItemsWhenMoveList(true);

			} else {
				no_collect_layout.setVisibility(View.VISIBLE);
			}

			if (mContent_News != null) {
				offlineNewAdapter = new MyFootAdapter();
				example_lv_list.setAdapter(offlineNewAdapter);
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void setListener() {
		my_collect_bottom_allselect.setOnClickListener(this);
		my_collect_bottom_delete.setOnClickListener(this);
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
		case R.id.com_title_bar_left_bt:// 后退
			AnimFinsh();
			break;
		case R.id.com_title_bar_right_bt:// 多选删除
			if (mContent_News != null && mContent_News.size() > 0) {
				if (!isOpenItem) {
					isShowCheckBox = true;
					example_lv_list.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
					com_title_bar_right_bt.setVisibility(View.INVISIBLE);
					com_title_bar_right_tv.setVisibility(View.VISIBLE);
					my_collect_bottom_line.setVisibility(View.VISIBLE);
					my_collect_bottom_linearlayout.setVisibility(View.VISIBLE);
				} else {
					moreSelect = true;
				}
				example_lv_list.closeOpenedItems();

			}
			// example_lv_list.smoothScrollToPositionFromTop(7,0);
			break;
		case R.id.com_title_bar_right_tv:// 取消多选
			isShowCheckBox = false;
			example_lv_list.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
			com_title_bar_right_bt.setVisibility(View.VISIBLE);
			com_title_bar_right_tv.setVisibility(View.GONE);
			// 状态复原
			itemSelected.clear();
			my_collect_bottom_delete.setText("删除");
			my_collect_bottom_allselect.setText("全选");

			offlineNewAdapter.notifyDataSetChanged();

			my_collect_bottom_line.setVisibility(View.GONE);
			my_collect_bottom_linearlayout.setVisibility(View.GONE);
			break;
		// 全选按钮
		case R.id.my_collect_bottom_allselect:
			if (mContent_News != null && mContent_News.size() > 0) {
				if (my_collect_bottom_allselect.getText().equals("全选")) {
					itemSelected.addAll(mContent_News);
					my_collect_bottom_delete.setText("删除("
							+ itemSelected.size() + ")");
					my_collect_bottom_allselect.setText("取消全选");
				} else {
					itemSelected.clear();
					my_collect_bottom_delete.setText("删除");
					my_collect_bottom_allselect.setText("全选");
				}
				offlineNewAdapter.notifyDataSetChanged();
			}
			break;
		// 删除按钮
		case R.id.my_collect_bottom_delete:
			if (itemSelected != null && itemSelected.size() > 0) {
				delete();
				offlineNewAdapter.notifyDataSetChanged();
			}
			break;

		}
	}

	/**
	 * 删除
	 */
	private void delete() {
		final InfoMsgHint dialog = new InfoMsgHint(mContext, R.style.MyDialog1);
		dialog.setContent("删除", "是否要删除当前选中项？", "删除", "取消");
		dialog.setCancleListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setOKListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final ArrayList<Content_News> rest_uco = new ArrayList<Content_News>();
				for (Content_News cn : itemSelected) {
					cn.setLooktime("0");
					rest_uco.add(cn);
				}
				new Thread() {
					@Override
					public void run() {
						try {
							dbUtils.saveOrUpdateAll(rest_uco);
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
				// initData();
				itemSelected.clear();
				mContent_News.removeAll(rest_uco);
				isShowCheckBox = false;

				my_collect_bottom_line.setVisibility(View.GONE);
				my_collect_bottom_linearlayout.setVisibility(View.GONE);
				example_lv_list.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
				com_title_bar_right_bt.setVisibility(View.VISIBLE);
				com_title_bar_right_tv.setVisibility(View.GONE);

				if (mContent_News.size() <= 0) {
					no_collect_layout.setVisibility(View.VISIBLE);
				}
				my_collect_bottom_delete.setText("删除");

				dialog.dismiss();
			}
		});
		dialog.show();
	}

	class MyFootAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mContent_News.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mContent_News.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_collect, null);
			}
			final CheckBox cancle_check = ViewHolderUtil.get(convertView,
					R.id.cancle_check);
			View itemView = ViewHolderUtil.get(convertView, R.id.item);
			ImageView image = ViewHolderUtil.get(convertView, R.id.image);
			MyTextView title = ViewHolderUtil.get(convertView, R.id.title);
			final MarqueeTextView newstime = ViewHolderUtil.get(convertView,
					R.id.newstime);
			Button cancle = ViewHolderUtil.get(convertView, R.id.cancle);

			final Content_News item = (Content_News) getItem(position);

//			imageLoader.displayImage(item.getSmallTitlepic(), image,
//					Options_Item.getSmallImageOptions());
			
			ImgUtils.imageLoader.displayImage(item.getTitlepic(), image,
					Options.getSmallImageOptions(true));

			title.setText(item.getTitle());

			newstime.setText(TimeUtil.unix2date(
					Long.valueOf(item.getLooktime()), "yyyy-MM-dd HH:mm:ss"));

			cancle_check.setVisibility(isShowCheckBox ? View.VISIBLE
					: View.GONE);
			// checkbox监听
			// cancle_check
			// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
			// @Override
			// public void onCheckedChanged(CompoundButton buttonView,
			// boolean isChecked) {
			// if (cancle_check.isChecked()) {
			// itemSelected.add(user_Collect_Offline);
			// } else {
			// itemSelected.remove(user_Collect_Offline);
			// }
			// // 改变删除里面的个数
			// if (itemSelected != null && itemSelected.size() > 0) {
			// my_collect_bottom_delete.setText("删除("
			// + itemSelected.size() + ")");
			// } else {
			// my_collect_bottom_delete.setText("删除");
			// }
			//
			// }
			// });

			cancle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					example_lv_list.closeOpenedItems();
					mContent_News.remove(item);
					// 另开线程 操作数据库
					new Thread() {
						public void run() {
							try {
								item.setLooktime("0");
								dbUtils.saveOrUpdate(item);
							} catch (DbException e) {
								e.printStackTrace();
							}
						};
					}.start();
					if (mContent_News.size() <= 0) {
						no_collect_layout.setVisibility(View.VISIBLE);
					}
					offlineNewAdapter.notifyDataSetChanged();
				}
			});

			// item的点击事件
			itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!isOpenItem) {
						if (isShowCheckBox) {
							if (cancle_check.isChecked()) {
								cancle_check.setChecked(false);
								itemSelected.remove(item);
							} else {
								cancle_check.setChecked(true);
								itemSelected.add(item);
							}
							// 改变删除里面的个数
							if (itemSelected != null && itemSelected.size() > 0) {
								my_collect_bottom_delete.setText("删除("
										+ itemSelected.size() + ")");
							} else {
								my_collect_bottom_delete.setText("删除");
							}
							my_collect_bottom_allselect.setText("全选");
						} else {
							// 点击跳转到新闻 页面
							int[] i = { Integer.valueOf(item.getMid()) };
							startAnimActivityById(New_Activity_Content_Video.class, 0,
									"arrayid", i);
						}
						// 判断选中个数来现实全选还是取消全选
						if (itemSelected.size() == mContent_News.size()) {
							my_collect_bottom_allselect.setText("取消全选");
						} else {
							my_collect_bottom_allselect.setText("全选");
						}
					} else {
						// 点击跳转到新闻 页面
						int[] i = { Integer.valueOf(item.getMid()) };
						startAnimActivityById(New_Activity_Content_Video.class, 0,
								"arrayid", i);
					}
				}
			});

			// checkbox初始化
			if (itemSelected.contains(item)) {
				cancle_check.setChecked(true);
			} else {
				cancle_check.setChecked(false);
			}

			// 给最后一个item加上padding 10dp
			int padding_in_dp = 10; // 6 dps
			final float scale = getResources().getDisplayMetrics().density;
			int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
			if (position == mContent_News.size() - 1) {
				convertView.setPadding(0, padding_in_px, 0, padding_in_px);
			} else {
				convertView.setPadding(0, padding_in_px, 0, 0);
			}

			return convertView;
		}
	}

	boolean isOpenItem;
	private boolean moreSelect;

	// listView 事件
	class MySwipeListViewListener extends BaseSwipeListViewListener {

		@Override
		public void onClosed(int position, boolean fromRight) {
			// TODO Auto-generated method stub
			super.onClosed(position, fromRight);

			// if (curPosition == position) {
			// setRightFinsh(true);
			// curPosition = -1;
			// }
			if (moreSelect) {
				moreSelect = false;
				isShowCheckBox = true;
				example_lv_list.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
				com_title_bar_right_bt.setVisibility(View.INVISIBLE);
				com_title_bar_right_tv.setVisibility(View.VISIBLE);
				my_collect_bottom_line.setVisibility(View.VISIBLE);
				my_collect_bottom_linearlayout.setVisibility(View.VISIBLE);
			}
			isOpenItem = false;
			setRightFinsh(true);
		}

		@Override
		public void onOpened(int position, boolean toRight) {
			super.onOpened(position, toRight);

			isOpenItem = true;
			setRightFinsh(false);
			// if (curPosition != -1) {
			// example_lv_list.closeAnimate(curPosition);
			// }
			// curPosition = position;
			// setRightFinsh(false);
		}

	}

}
