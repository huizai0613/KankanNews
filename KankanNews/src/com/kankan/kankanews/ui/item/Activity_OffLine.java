package com.kankan.kankanews.ui.item;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.download.MyRequestCallBack;
import com.kankan.kankanews.bean.Content_News;
import com.kankan.kankanews.bean.User_Collect_Offline;
import com.kankan.kankanews.bean.Comparator.ComparatorUserCO;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.dialog.TishiMsgHint;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.ui.view.MarqueeTextView;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.HttpHandler;

public class Activity_OffLine extends BaseActivity implements OnClickListener {

	private ItnetUtils instance;
	private SwipeListView example_lv_list;
	private ArrayList<Content_News> mContent_News;
	private LayoutInflater inflater;
	private OfflineNewAdapter offlineNewAdapter;
	private HashMap<String, View> mViewMap = new HashMap<String, View>();
	private ArrayList<Content_News> selectNews = new ArrayList<Content_News>();

	private HashMap<String, Thread> threads = new HashMap<String, Thread>();

	// 用来控制CheckBox的选中状况
	private HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offline);

		// tongji
		initAnalytics(AndroidConfig.my_offline_page);

		initTitle_Right_Left_bar("离线任务", "", "取消", "#ffffff",
				R.drawable.icon_delete, R.drawable.icon_black_big, "#000000","#000000");
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);

		com_title_bar_right_tv.setVisibility(View.GONE);
		setIsSelected(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void initView() {
		example_lv_list = (SwipeListView) findViewById(R.id.example_lv_list);
		error_bg = (View) findViewById(R.id.error_bg);

		bottom_bar = findViewById(R.id.bottom_bar);
		bottom_allselect = (MyTextView) findViewById(R.id.bottom_allselect);
		bottom_delete = (MyTextView) findViewById(R.id.bottom_delete);

	}

	@Override
	protected void initData() {
		instance = ItnetUtils.getInstance(this);
		inflater = LayoutInflater.from(mContext);
		try {

			List<User_Collect_Offline> mUser_Collect_Offline = dbUtils
					.findAll(Selector.from(User_Collect_Offline.class)
							.where("isOffline", "==", true)
							.orderBy("OfflineTime", true));
			if (mUser_Collect_Offline != null) {
				for (User_Collect_Offline u : mUser_Collect_Offline) {
					if (!mApplication.mUser_Collect_Offlines.containsKey(u
							.getId())) {
						mApplication.mUser_Collect_Offlines.put(u.getId(), u);
					}
				}
			}
			entrySet = mApplication.mUser_Collect_Offlines.entrySet();

			if (mApplication.mUser_Collect_Offlines != null
					&& mApplication.mUser_Collect_Offlines.size() > 0) {
				ArrayList<User_Collect_Offline> arrayList = new ArrayList<User_Collect_Offline>();

				for (Entry<String, User_Collect_Offline> e : entrySet) {
					arrayList.add(e.getValue());
				}

				ComparatorUserCO comparatorUserCO = new ComparatorUserCO();

				Collections.sort(arrayList, comparatorUserCO);

				mContent_News = new ArrayList<Content_News>();
				Content_News findById = null;
				for (User_Collect_Offline e : arrayList) {
					if (e.isOffline()) {
						findById = dbUtils.findFirst(Selector.from(
								Content_News.class)
								.where("mid", "=", e.getId()));
						if (findById != null) {
							mContent_News.add(findById);
						}
					}
				}
				example_lv_list
						.setSwipeListViewListener(new MySwipeListViewListener());
				example_lv_list.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
				example_lv_list.setAnimationTime(100);
				example_lv_list.setOffsetLeft(mScreenWidth
						- PixelUtil.dp2px(80));
				example_lv_list.setSwipeCloseAllItemsWhenMoveList(true);

			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mContent_News != null && mContent_News.size() > 0) {
			offlineNewAdapter = new OfflineNewAdapter();
			example_lv_list.setAdapter(offlineNewAdapter);
			// upDataDownUI(true);
		} else {
			// 没有离线
			error_bg.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void setListener() {

		bottom_allselect.setOnClickListener(this);
		bottom_delete.setOnClickListener(this);

	}

	@Override
	public void setRightFinsh(boolean isRightFinsh) {
		this.isRightFinsh = isRightFinsh;
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {

	}

	@Override
	protected void onFailure(VolleyError error) {
	}

	// 初始化isSelected的数据
	private void setIsSelected(boolean select) {
		for (int i = 0; mContent_News != null && i < mContent_News.size(); i++) {
			isSelected.put(i, select);
		}
	}

	class OfflineNewAdapter extends BaseAdapter {
		ViewHolder holder;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mContent_News.size();
		}

		@Override
		public User_Collect_Offline getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final Content_News item = mContent_News.get(position);
			final User_Collect_Offline user_Collect_Offline = mApplication.mUser_Collect_Offlines
					.get(item.getMid());

			final HttpHandler httpHandler = mApplication.mHttpHandlereds
					.get(item.getMid());

			final File videoFile = CommonUtils.getVideoFile(mContext, item);

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_offline, null);
				holder = new ViewHolder();
				holder.itemView = convertView.findViewById(R.id.item);
				holder.image = (ImageView) convertView.findViewById(R.id.image);
				holder.title = (MyTextView) convertView
						.findViewById(R.id.title);
				holder.statu = (MarqueeTextView) convertView
						.findViewById(R.id.statu);
				holder.cancle_check = (CheckBox) convertView
						.findViewById(R.id.cancle_check);
				holder.cancle = (Button) convertView.findViewById(R.id.cancle);
				holder.statu.setTag(item.getMid());
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ImageView image = holder.image;
			View itemView = holder.itemView;
			MyTextView title = holder.title;
			final MarqueeTextView statu = holder.statu;
			final CheckBox cancle_check = holder.cancle_check;
			Button cancle = holder.cancle;

			if (user_Collect_Offline != null) {

				if (statu != null) {
					switch (mApplication.mUser_Collect_Offlines.get(
							item.getMid()).getType()) {
					case User_Collect_Offline.DOWNLOADBAD:// 文件损坏
						statu.setText("文件损坏,点击重新下载");
						break;
					case User_Collect_Offline.DOWNLOADED:// 下载完成
						statu.setText("下载完成");
						break;
					case User_Collect_Offline.DOWNLOADING:// 正在下载

						if (!threads.containsKey(item.getMid())) {
							addDownThread(item, user_Collect_Offline, statu);
						}

						break;
					case User_Collect_Offline.DOWNLOADPAUSE:// 等待下载
						statu.setText("等待下载");
						if (!threads.containsKey(item.getMid())) {
							addPauseThread(item);
						}

						break;
					case User_Collect_Offline.DOWNLOADSTOP:// 暂停下载
						if (videoFile != null) {
							double percent = 0.0;
							double totalM = user_Collect_Offline.getTotalM();
							if (totalM != 0.0) {
								percent = (Math.rint(((videoFile.length() * 1.0
										/ totalM * 1.0)) * 10) / 10) * 100;
							}
							double cur = Math
									.rint((videoFile.length() / (1024.00 * 1024.00)) * 10) / 10;
							double total = Math
									.rint((totalM / (1024.00 * 1024.00)) * 10) / 10;
							statu.setText("已暂停:" + cur + "/" + total + "M "
									+ percent + "%");
						} else {
							statu.setText("已暂停");
						}
						break;
					}
				}
			}

			// imageLoader.displayImage(item.getSmallTitlepic(), image,
			// Options_Item.getSmallImageOptions());

			ImgUtils.imageLoader.displayImage(item.getTitlepic(), image,
					Options.getSmallImageOptions(true));
			title.setText(item.getTitle());

			// 根据isSelected来设置checkbox的选中状况
			cancle_check.setChecked(isSelected.get(position));
			cancle_check.setClickable(false);
			cancle_check.setVisibility(isShowCheckBox ? View.VISIBLE
					: View.GONE);

			itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (!isOpenItem) {
						if (isShowCheckBox) {
							cancle_check.toggle();
							// 将CheckBox的选中状况记录下来
							isSelected.put(position, cancle_check.isChecked());

							if (cancle_check.isChecked()) {
								selectNews.add(item);
							} else {
								selectNews.remove(item);
							}

							bottom_delete.setText(selectNews.size() > 0 ? "删除("
									+ selectNews.size() + ")" : "删除");
							isAllSelect = selectNews.size() >= mContent_News
									.size();
							bottom_allselect.setText(isAllSelect ? "取消全选"
									: "全选");

						} else {
							switch (user_Collect_Offline.getType()) {

							case User_Collect_Offline.DOWNLOADBAD:// 文件损坏重新下载
								if (videoFile != null) {
									synchronized (Activity_OffLine.this) {
										if (videoFile.exists()) {
											videoFile.delete();
										}
									}
								}
								startDownLoad(item, user_Collect_Offline);
								break;
							case User_Collect_Offline.DOWNLOADING:// 下载中,暂停
								if (httpHandler != null) {
									httpHandler.cancel();
								}
								mApplication.mUser_Collect_Offlines.get(
										item.getMid()).setType(
										User_Collect_Offline.DOWNLOADSTOP);
								offlineNewAdapter.notifyDataSetChanged();
								break;
							case User_Collect_Offline.DOWNLOADED:// 下载完成,直接播放
								File videoFile2 = CommonUtils.getVideoFile(
										mContext, item);
								if (videoFile2 != null) {
									CommonUtils.clickevent(
											mContext,
											"title",
											item.getTitle(),
											AndroidConfig.video_offline_play_event);

									startAnimActivity2ObjForResult(
											Activity_OffLinePlay.class, "news",
											1, position + "", item);

								} else {
									ToastUtils.Errortoast(mContext,
											"亲!视频文件已不存在");
									mContent_News.remove(item);

									mApplication.mUser_Collect_Offlines.get(
											item.getMid()).setOffline(false);

									if (mApplication.mHttpHandlereds.get(item
											.getMid()) != null) {
										mApplication.mHttpHandlereds.get(
												item.getMid()).cancel();
									} else {
										saveUserCollectOffline(mApplication.mUser_Collect_Offlines
												.get(item.getMid()));
									}
								}
								offlineNewAdapter.notifyDataSetChanged();
								if (mContent_News.size() <= 0) {
									error_bg.setVisibility(View.VISIBLE);
								}

								break;
							case User_Collect_Offline.DOWNLOADSTOP:// 暂停下载,开始下载
								startDownLoad(item, user_Collect_Offline);
								break;
							}
						}
					}
				}

			});
			cancle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mContent_News.remove(item);

					mApplication.mUser_Collect_Offlines.get(item.getMid())
							.setOffline(false);

					mApplication.mRequestCallBackPauses.remove(item.getMid());

					if (mApplication.mHttpHandlereds.get(item.getMid()) != null) {
						mApplication.mHttpHandlereds.get(item.getMid())
								.cancel();
					} else {
						saveUserCollectOffline(mApplication.mUser_Collect_Offlines
								.get(item.getMid()));
					}
					if (videoFile != null) {
						synchronized (Activity_OffLine.this) {
							if (videoFile.exists()) {
								videoFile.delete();
							}
						}
					}
					example_lv_list.closeOpenedItems();
					offlineNewAdapter.notifyDataSetChanged();
					if (mContent_News.size() <= 0) {
						error_bg.setVisibility(View.VISIBLE);
					}
				}
			});

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

		class ViewHolder {
			CheckBox cancle_check;
			View itemView;
			ImageView image;
			MyTextView title;
			MarqueeTextView statu;
			Button cancle;
		}

	}

	// 添加等待更新线程
	private void addPauseThread(final Content_News item) {
		Thread mThread = new Thread() {
			public void run() {
				while (true) {
					SystemClock.sleep(1000);
					if (mApplication.mUser_Collect_Offlines.get(item.getMid()) != null
							&& mApplication.mUser_Collect_Offlines.get(
									item.getMid()).getType() != User_Collect_Offline.DOWNLOADPAUSE) {
						threads.remove(item.getMid());
						handler.post(new Runnable() {
							@Override
							public void run() {
								offlineNewAdapter.notifyDataSetChanged();
							}
						});
						return;
					}
				}
			}
		};
		threads.put(item.getMid(), mThread);
		mThread.start();
	}

	// 添加下载更新线程
	private void addDownThread(final Content_News item,
			final User_Collect_Offline user_Collect_Offline,
			final MarqueeTextView statu) {
		Thread thread = new Thread() {
			double percent = 0.0;
			File videoFile;

			public void run() {
				while (true) {
					SystemClock.sleep(500);
					if (videoFile != null && videoFile.exists()) {
						synchronized (Activity_OffLine.this) {
							double totalM = user_Collect_Offline.getTotalM();
							if (totalM != 0.0) {
								percent = (Math.rint(((videoFile.length() * 1.0
										/ totalM * 1.0)) * 10) / 10) * 100;
							}
							final double cur = Math
									.rint((videoFile.length() / (1024.00 * 1024.00)) * 10) / 10;
							final double total = Math
									.rint((totalM / (1024.00 * 1024.00)) * 10) / 10;
							handler.post(new Runnable() {
								@Override
								public void run() {
									String id = (String) statu.getTag();
									if (id.equals(item.getMid())) {
										statu.setText("缓冲中:" + cur + "/"
												+ total + "M " + percent + "%");
									}
								}
							});

							if (mApplication.mUser_Collect_Offlines.get(item
									.getMid()) != null
									&& mApplication.mUser_Collect_Offlines.get(
											item.getMid()).getType() != User_Collect_Offline.DOWNLOADING) {
								threads.remove(item.getMid());
								handler.post(new Runnable() {

									@Override
									public void run() {
										offlineNewAdapter
												.notifyDataSetChanged();
									}
								});
								return;
							}
						}
					} else {
						videoFile = CommonUtils.getVideoFile(mContext, item);
					}
				}
			};
		};
		threads.put(item.getMid(), thread);
		thread.start();
	}

	// int curPosition = -1;
	private boolean isShowCheckBox;
	private boolean moreSelect;
	private boolean isCanRightFinsh;

	boolean isOpenItem;

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
				isShowCheckBox = true;
				example_lv_list.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
				com_title_bar_right_bt.setVisibility(View.INVISIBLE);
				com_title_bar_right_tv.setVisibility(View.VISIBLE);
				bottom_bar.setVisibility(View.VISIBLE);
				isOpenItem = false;
				moreSelect = false;
			}
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

	boolean isAllSelect;
	boolean isAllUnSelect;

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.com_title_bar_left_bt:// 后退
			AnimFinsh();
			break;
		case R.id.bottom_allselect:// 全选
			isAllSelect = !isAllSelect;
			if (isAllSelect) {
				for (Content_News n : mContent_News) {

					if (!selectNews.contains(n)) {
						selectNews.add(n);
					}
				}
			} else {
				selectNews.clear();
			}
			bottom_delete.setText(selectNews.size() > 0 ? "删除("
					+ selectNews.size() + ")" : "删除");
			setIsSelected(isAllSelect);
			bottom_allselect.setText(isAllSelect ? "取消全选" : "全选");
			offlineNewAdapter.notifyDataSetChanged();
			break;
		case R.id.bottom_delete:// 删除
			if (selectNews.size() > 0) {
				deleteNews();
			}
			break;
		case R.id.com_title_bar_right_bt:// 多选删除
			if (offlineNewAdapter != null && mContent_News.size() > 0) {
				if (!isOpenItem) {
					example_lv_list.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
					isShowCheckBox = true;
					com_title_bar_right_bt.setVisibility(View.INVISIBLE);
					com_title_bar_right_tv.setVisibility(View.VISIBLE);
					bottom_bar.setVisibility(View.VISIBLE);
				} else {
					moreSelect = true;
				}
				example_lv_list.closeOpenedItems();
			}
			break;
		case R.id.com_title_bar_right_tv:// 取消多选
			setIsSelected(false);
			selectNews.clear();
			isShowCheckBox = false;
			bottom_allselect.setText("全选");
			bottom_delete.setText("删除");
			bottom_bar.setVisibility(View.GONE);
			example_lv_list.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
			com_title_bar_right_bt.setVisibility(View.VISIBLE);
			com_title_bar_right_tv.setVisibility(View.GONE);
			break;
		}

	}

	private void deleteNews() {
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
				for (Content_News news : selectNews) {
					mContent_News.remove(news);
					mApplication.mUser_Collect_Offlines.get(news.getMid())
							.setOffline(false);
					mApplication.mRequestCallBackPauses.remove(news.getMid());
					if (mApplication.mHttpHandlereds.get(news.getMid()) != null) {
						mApplication.mHttpHandlereds.get(news.getMid())
								.cancel();
					} else {
						saveUserCollectOffline(mApplication.mUser_Collect_Offlines
								.get(news.getMid()));
					}
					File file = new File(CommonUtils
							.getVideoCachePath(mContext), CommonUtils
							.UrlToFileName(news.getMp4url()));
					if (file.exists()) {
						file.delete();
					}
				}
				selectNews.clear();
				isShowCheckBox = false;
				bottom_bar.setVisibility(View.GONE);
				example_lv_list.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
				com_title_bar_right_bt.setVisibility(View.VISIBLE);
				com_title_bar_right_tv.setVisibility(View.GONE);
				if (mContent_News.size() <= 0) {
					error_bg.setVisibility(View.VISIBLE);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	Handler handler = new Handler() {
	};
	private Set<Entry<String, User_Collect_Offline>> entrySet;
	private View error_bg;
	private View bottom_bar;
	private MyTextView bottom_allselect;
	private MyTextView bottom_delete;

	int noUpdataNum;

	// public void upDataDownUI(boolean isAuto) {
	// if (isUpdate) {
	// noUpdataNum = 0;
	// offlineNewAdapter.notifyDataSetChanged();
	// } else {
	// if (noUpdataNum > 5)
	// isUpdate = true;
	// noUpdataNum++;
	// }
	// if (isAuto)
	// handler.sendEmptyMessageDelayed(1, 1000);
	// }

	// 保存收藏和离线数据到本地
	public void saveUserCollectOffline(
			final User_Collect_Offline user_collect_offline) {

		new Thread() {
			@Override
			public void run() {
				try {
					dbUtils.saveOrUpdate(user_collect_offline);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

	}

	// 开始下载视频
	private void startDownLoad(final Content_News item,
			final User_Collect_Offline user_Collect_Offline) {

		if (CommonUtils.isNetworkAvailable(mContext)) {
			if (!CommonUtils.isWifi(mContext)) {
				if (!spUtil.isFlow()) {
					final TishiMsgHint dialog = new TishiMsgHint(mContext,
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
					final InfoMsgHint dialog = new InfoMsgHint(mContext,
							R.style.MyDialog1);
					dialog.setContent(
							"亲，您现在使用的是运营商网络，继续使用可能会产生流量费用，建议改用WIFI网络", "",
							"继续下载", "取消");
					dialog.setCancleListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.setOKListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							startDown(item, user_Collect_Offline);
							dialog.dismiss();
						}
					});
					dialog.show();

				}
			} else {
				startDown(item, user_Collect_Offline);
			}
		} else {
			final TishiMsgHint dialog = new TishiMsgHint(mContext,
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

	private void startDown(final Content_News item,
			final User_Collect_Offline user_Collect_Offline) {
		MyRequestCallBack myRequestCallBack = new MyRequestCallBack(item,
				dbUtils);
		HttpHandler start = myRequestCallBack.start();

		mApplication.mUser_Collect_Offlines.get(item.getMid()).setOffline(true);
		if (start == null) {// 等待下载
			mApplication.mUser_Collect_Offlines.get(item.getMid()).setType(
					User_Collect_Offline.DOWNLOADPAUSE);
			saveUserCollectOffline(user_Collect_Offline);
		} else {
			mApplication.mHttpHandlereds.put(item.getMid(), start);
			mApplication.mUser_Collect_Offlines.get(item.getMid()).setType(
					User_Collect_Offline.DOWNLOADING);
		}
		offlineNewAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if (resultCode == 20) {
			int position = arg2.getIntExtra("POSITION", 10000);
			if (position != 10000) {
				Content_News content_News = mContent_News.get(position);
				mApplication.mUser_Collect_Offlines.get(content_News.getMid())
						.setType(User_Collect_Offline.DOWNLOADBAD);
				saveUserCollectOffline(mApplication.mUser_Collect_Offlines
						.get(content_News.getMid()));
				offlineNewAdapter.notifyDataSetChanged();
			}
		}

	}
}
