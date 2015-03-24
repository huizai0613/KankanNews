package com.kankan.kankanews.ui.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.adapter.ImagePagerAdapter;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.Content_News;
import com.kankan.kankanews.bean.MainFragmentBean;
import com.kankan.kankanews.bean.New_News_Top;
import com.kankan.kankanews.bean.News;
import com.kankan.kankanews.bean.TopNews;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.ui.item.Activity_Reporter;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.view.AutoImageTag;
import com.kankan.kankanews.ui.view.AutoImageVIew;
import com.kankan.kankanews.ui.view.AutoScrollViewPager;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.RoundImageView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class MainFragment extends BaseFragment {

	private View inflate;
	private MainFragmentBean mMainFragmentBean;
	private MainFragmentBean mainFragmentBean;
	private ArrayList<TopNews> getmTopNewsList = new ArrayList<TopNews>();
	private LinkedList<News> getmNewsList = new LinkedList<News>();
	private int lineNum = 2;
	private TopAdapter adapter;
	private ArrayList<AutoImageTag> images;
	private ArrayList<View> points;

	private int[] arrayid;
	private int item_position = 0;

	Timer mTimer;
	TimerTask mTask;
	int pageIndex = 0;
	boolean isTaskRun;
	// 是否有本地数据
	private boolean initLocalDate;

	// private LinearLayout main_bg;
	// private View default_bg;

	private boolean noMoreNews = false;

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (pagerHolder != null) {
			pagerHolder.pager.stopAutoScroll();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		inflate = inflater.inflate(R.layout.new_fragment_home_item, null);
		// 统计
		initAnalytics(AndroidConfig.new_news_page);

		listview = (PullToRefreshListView) inflate.findViewById(R.id.listview);

		// default_bg = inflate.findViewById(R.id.default_bg);
		//
		// main_bg = (LinearLayout) inflate.findViewById(R.id.main_bg);
		// main_bg.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// isLoadMore = false;
		// noMoreNews = false;
		// ItnetUtils instance = ItnetUtils.getInstance(mActivity);
		// instance.getMainData("", mListener, mErrorListener);
		// lastTime = System.currentTimeMillis();
		// }
		// });

		initListView(Mode.BOTH);
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

		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				if (pagerHolder != null && pagerHolder.pager != null
						&& images != null) {
					if (!pagerHolder.pager.isAutoScroll() && images.size() > 1) {
						pagerHolder.pager.startAutoScroll(3000);
					}
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		adapter = new TopAdapter();
		listview.setAdapter(adapter);
		initLocalDate();
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			refreshNetDate();
		} else {
			if (!initLocalDate) {

				// yexiangyu 标记

				// main_bg.setVisibility(View.VISIBLE);
			}
		}
		// // 头部的左侧点击菜单事件
		// setOnLeftClickLinester(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// ((MainActivity) getActivity()).getSide_drawer().showMenu();
		// }
		// });

		inflate.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		// 处理推送逻辑
		Bundle bun = mActivity.getIntent().getExtras();
		String newsid = null;
		String reporter = null;
		if (bun != null) {
			newsid = bun.getString("newsid");
		}
		if (bun != null) {
			reporter = bun.getString("reporter");
		}

		if (!TextUtils.isEmpty(newsid)) {
			startAnimActivityById(New_Activity_Content_Video.class, 0,
					"arrayid", new int[] { Integer.parseInt(newsid) });

		} else if (!TextUtils.isEmpty(reporter)) {

			Content_News content_News = null;
			try {
				content_News = mActivity.dbUtils.findFirst(Selector.from(
						Content_News.class).where("uid", "=", reporter));
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (content_News != null) {
				mActivity.startAnimActivity2Obj(Activity_Reporter.class,
						"content_News", content_News);
			} else {
				mActivity.startAnimActivity2Obj(Activity_Reporter.class, "ID",
						reporter, null);
			}
		} else {
			super.onCreateView(inflater, container, savedInstanceState);
		}

		return inflate;

	}

	@Override
	protected boolean initLocalDate() {

		try {
			List<News> mNews = mActivity.dbUtils.findAll(News.class);
			List<TopNews> mTopNews = mActivity.dbUtils.findAll(TopNews.class);

			if (mNews != null) {
				getmNewsList = new LinkedList<News>(mNews);
			}
			if (mTopNews != null) {
				getmTopNewsList = new ArrayList<TopNews>(mTopNews);
			}
			initTopImageAndPoint();
			if (mTopNews != null && mNews != null) {
				adapter.notifyDataSetChanged();
				initLocalDate = true;
				// default_bg.setVisibility(View.GONE);
				// yexiangyu yexiangyu 标记
				listview.setBackgroundColor(Color.parseColor("#f5f5f5"));

				// 初始化id数组
				init_arrayid();
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private void addData() {

		if (!isLoadMore) {
			getmTopNewsList = mainFragmentBean.getmTopNewsList();
			getmNewsList = mainFragmentBean.getmNewsList();
			saveLocalDate();
			initTopImageAndPoint();
			// default_bg.setVisibility(View.GONE);
			// main_bg.setVisibility(View.GONE);
			// yexiangyu 标记
			listview.setBackgroundColor(Color.parseColor("#f5f5f5"));

			adapter = new TopAdapter();
			listview.setAdapter(adapter);
		} else {
			// xuda
			if (mainFragmentBean.getmNewsList().size() == 0
					&& noMoreNews == false) {
				noMoreNews = true;
			}
			getmNewsList.addAll(mainFragmentBean.getmNewsList());
		}
		init_arrayid();
		adapter.notifyDataSetChanged();

		listview.onRefreshComplete();
	}

	// 初始化id数组
	private void init_arrayid() {
		arrayid = new int[(getmTopNewsList == null ? 0 : getmTopNewsList.size())
				+ (getmNewsList == null ? 0 : getmNewsList.size())];
		item_position = 0;
		if (getmTopNewsList != null) {
			for (TopNews tn : getmTopNewsList) {
				arrayid[item_position] = Integer.parseInt(tn.getId());
				item_position++;
			}
		}

		if (getmNewsList != null) {
			for (News n : getmNewsList) {
				arrayid[item_position] = Integer.parseInt(n.getId());
				item_position++;
			}
		}
	}

	// 初始化Viewpager的image与point
	private void initTopImageAndPoint() {
		if (getmTopNewsList != null) {
			android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					PixelUtil.dp2px(6), PixelUtil.dp2px(6));
			layoutParams.rightMargin = PixelUtil.dp2px(4);
			points = new ArrayList<View>();
			View point;
			images = new ArrayList<AutoImageTag>();
			AutoImageVIew imageView = null;
			for (final TopNews tn : getmTopNewsList) {
				// imageView = new AutoImageVIew(mActivity);
				AutoImageTag tag = new AutoImageTag(tn.getTitlepic(), true);
				tag.setId(tn.getId());
				// imageView.setmAutoImageTag(tag);
				// imageView.setScaleType(ScaleType.FIT_XY);
				images.add(tag);

				point = new View(mActivity);
				point.setLayoutParams(layoutParams);
				point.setBackgroundResource(R.drawable.point_gray);
				points.add(point);
			}

			point = new View(mActivity);
			point.setLayoutParams(layoutParams);
			point.setBackgroundResource(R.drawable.point_gray);
			points.add(point);
			point = new View(mActivity);
			point.setLayoutParams(layoutParams);
			point.setBackgroundResource(R.drawable.point_gray);
			points.add(point);
			point = new View(mActivity);
			point.setLayoutParams(layoutParams);
			point.setBackgroundResource(R.drawable.point_gray);
			points.add(point);

		}
	}

	long lastTime;
	boolean refreshOK;

	@Override
	protected void refreshNetDate() {
		if (!refreshOK || System.currentTimeMillis() - lastTime > 60 * 1000) {
			isLoadMore = false;
			noMoreNews = false;
			ItnetUtils instance = ItnetUtils.getInstance(mActivity);
			instance.getMainData("", mListenerObject, mErrorListener);
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
		instance.getMainData(getmNewsList.getLast().getNewstime(),
				mListenerObject, mErrorListener);
	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {
		if (mainFragmentBean == null) {
			mainFragmentBean = new MainFragmentBean();
		}
		refreshOK = true;
		mainFragmentBean.parseJSON(jsonObject);
		addData();
	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(VolleyError error) {
		listview.onRefreshComplete();
		// default_bg.setVisibility(View.GONE);
		//
		if (initLocalDate) {
			// main_bg.setVisibility(View.GONE);
			refreshOK = false;
			listview.setBackgroundColor(Color.parseColor("#f5f5f5"));

		} else {
			// main_bg.setVisibility(View.VISIBLE);
		}
		ToastUtils.Errortoast(mActivity, "网络不可用");
	}

	@Override
	protected void saveLocalDate() {
		// arrayid = new int[(getmTopNewsList == null ? 0 :
		// getmTopNewsList.size())
		// + (getmNewsList == null ? 0 : getmNewsList.size())];

		new Thread() {
			public void run() {
				int item_position = 0;

				if (getmTopNewsList != null) {
					try {
						mActivity.dbUtils.deleteAll(TopNews.class);
						mActivity.dbUtils.deleteAll(News.class);
					} catch (DbException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				if (getmTopNewsList != null) {
					try {
						mActivity.dbUtils.saveOrUpdateAll(getmTopNewsList);
						// tn.setLabels(tn.getLabels() + "2");
						// tn.setItitled(tn.getItitled() + "2");
						// tn.setId("31");
						// mActivity.dbUtils.save(tn);
						// tn.setItitled(tn.getItitled() + "3");
						// tn.setLabels(tn.getLabels() + "3");
						// tn.setId("32");
						// mActivity.dbUtils.save(tn);
						// tn.setItitled(tn.getItitled() + "4");
						// tn.setLabels(tn.getLabels() + "4");
						// tn.setId("33");
						// mActivity.dbUtils.save(tn);
						// tn.setId("34");
						// mActivity.dbUtils.save(tn);
						// arrayid[item_position] = Integer.parseInt(tn
						// .getId());
						// item_position++;
					} catch (DbException e) {
						e.printStackTrace();
					}

					if (getmNewsList != null) {
						try {
							mActivity.dbUtils.saveOrUpdateAll(getmNewsList);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
				}
			};
		}.start();

	}

	ViewPagerHolder pagerHolder = null;
	NewHolder newHolder = null;
	ViewHolderInfo holderInfo = null;

	private class TopAdapter extends BaseAdapter implements
			OnPageChangeListener {

		private News news;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (noMoreNews) {
				return getmTopNewsList != null && getmTopNewsList.size() > 0 ? ((getmNewsList
						.size() + lineNum - 1)
						/ lineNum + (getmTopNewsList != null ? 1 : 0)) + 1
						: (1);
			} else {
				return getmTopNewsList != null && getmTopNewsList.size() > 0 ? ((getmNewsList
						.size() + lineNum - 1)
						/ lineNum + (getmTopNewsList != null ? 1 : 0))
						: (0);
			}
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
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub

			if (position == 0) {
				return 0;
			} else if (noMoreNews && getmNewsList.size() + 1 <= position) {

				return 2;
			} else {
				return 1;
			}

			// return position == 0 ? 0 : 1;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			int itemViewType = getItemViewType(position);

			final int newPosition = (position - 1) * 2;

			if (getmTopNewsList != null && getmTopNewsList.size() > 0) {
				if (convertView == null) {
					if (itemViewType == 0) {
						convertView = inflate.inflate(mActivity,
								R.layout.mainfragment_item_topnew, null);
						pagerHolder = new ViewPagerHolder();

						pagerHolder.pager = (AutoScrollViewPager) convertView
								.findViewById(R.id.viewpager);

						pagerHolder.point_content = (LinearLayout) convertView
								.findViewById(R.id.point_content);

						pagerHolder.title = (MyTextView) convertView
								.findViewById(R.id.new_title);
						// pagerHolder.labse = (MyTextView) convertView
						// .findViewById(R.id.new_labase);

						pagerHolder.pager
								.setLayoutParams(new RelativeLayout.LayoutParams(
										RelativeLayout.LayoutParams.MATCH_PARENT,
										(int) (mActivity.topNewW / 1.5)));
						convertView
								.setLayoutParams(new AbsListView.LayoutParams(
										RelativeLayout.LayoutParams.MATCH_PARENT,
										(int) (mActivity.topNewW / 1.5)
												+ PixelUtil.dp2px(45)));

						pagerHolder.pager.setOnPageChangeListener(this);
						convertView.setTag(pagerHolder);

						// TODO
						ArrayList<New_News_Top> aaa = new ArrayList<New_News_Top>();

						ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(
								mActivity, MainFragment.this, images, aaa);
						if (images.size() > 1) {
							pagerHolder.pager
									.setCurrentItem(Integer.MAX_VALUE / 2
											- Integer.MAX_VALUE / 2
											% images.size());
							pagerHolder.pager.startAutoScroll(3000);
							pagerHolder.pager.setSwipeScrollDurationFactor(0.5);
							imagePagerAdapter.setInfiniteLoop(true);

						} else {
							imagePagerAdapter.setInfiniteLoop(false);
							pagerHolder.pager.stopAutoScroll();
						}

						pagerHolder.pager.setAdapter(imagePagerAdapter);
						// pagerHolder.pager.setSwipeScrollDurationFactor(2.0);

						int size = points.size();
						;
						if (size > 1) {
							points.get(0).setBackgroundResource(
									R.drawable.point_red);
							for (View v : points) {
								pagerHolder.point_content.addView(v);
							}
							pagerHolder.point_content
									.setVisibility(View.VISIBLE);
							// startTask();

						} else {
							// stopTask();
							pagerHolder.point_content
									.setVisibility(View.INVISIBLE);
						}
						pagerHolder.title.setText(getmTopNewsList.get(0)
								.getItitled());
						// pagerHolder.labse.setText(getmTopNewsList.get(0)
						// .getLabels());

					} else if (itemViewType == 1) {
						convertView = inflate.inflate(mActivity,
								R.layout.mainfragment_item_new, null);
						newHolder = new NewHolder();
						newHolder.parserView(1,
								convertView.findViewById(R.id.item1));
						newHolder.parserView(2,
								convertView.findViewById(R.id.item2));
						convertView.setTag(newHolder);

						// view2.
						//
						// view3.setLayoutParams(new
						// RelativeLayout.LayoutParams(
						// LayoutParams.MATCH_PARENT,
						// (int) (mActivity.newsW / 1.5)));
					} else if (itemViewType == 2) {
						holderInfo = new ViewHolderInfo();
						convertView = LayoutInflater.from(mActivity).inflate(
								R.layout.comment_nomore, null);
						holderInfo.info = (MyTextView) convertView
								.findViewById(R.id.comment_no_more);
						convertView.setTag(holderInfo);
					}
				} else {
					if (itemViewType == 0) {
						pagerHolder = (ViewPagerHolder) convertView.getTag();
					} else if (itemViewType == 1) {
						newHolder = (NewHolder) convertView.getTag();
					} else if (itemViewType == 2) {
						holderInfo = (ViewHolderInfo) convertView.getTag();
					}
				}

				if (itemViewType == 0) {
					// 顶部Viewpager 不需要更新
				} else if (itemViewType == 1) {
					if (getmNewsList.size() > newPosition) {
						news = getmNewsList.get(newPosition);
						imageLoader.displayImage(news.getSmallTitlepic(),
								newHolder.itemView1.imageView,
								Options.getSmallImageOptions(false));
						imageLoader.displayImage(news.getProfile_image_url(),
								newHolder.itemView1.roundImageView,
								Options.getSmallImageOptions(false));
						newHolder.itemView1.name.setText(news.getName());
						newHolder.itemView1.content.setText(news.getTitle());
						newHolder.itemView1.v
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										startAnimActivityById(
												New_Activity_Content_Video.class,
												(position - 1)
														* 2
														+ (getmTopNewsList == null ? 0
																: getmTopNewsList
																		.size()),
												"arrayid", arrayid);
									}
								});
					}
					if (getmNewsList.size() > newPosition + 1) {
						newHolder.itemView2.v.setVisibility(View.VISIBLE);
						news = getmNewsList.get(newPosition + 1);
						imageLoader.displayImage(news.getSmallTitlepic(),
								newHolder.itemView2.imageView,
								Options.getSmallImageOptions(false));

						imageLoader.displayImage(news.getProfile_image_url(),
								newHolder.itemView2.roundImageView,
								Options.getSmallImageOptions(false));
						newHolder.itemView2.name.setText(news.getName());
						newHolder.itemView2.content.setText(news.getTitle());
						newHolder.itemView2.v
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										startAnimActivityById(
												New_Activity_Content_Video.class,
												(position - 1)
														* 2
														+ 1
														+ (getmTopNewsList == null ? 0
																: getmTopNewsList
																		.size()),
												"arrayid", arrayid);
									}
								});
					} else {
						newHolder.itemView2.v.setVisibility(View.INVISIBLE);
					}

				} else if (itemViewType == 2) {
					int padding_in_dp = 10; // 6 dps
					final float scale = getResources().getDisplayMetrics().density;
					int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
					holderInfo.info.setPadding(0, padding_in_px, 0,
							padding_in_px);
				}
			}

			return convertView;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// if (arg0 == 0 && !isTaskRun) {
			// setCurrentItem();
			// startTask();
			// } else if (arg0 == 1 && isTaskRun)
			// stopTask();
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			if (pagerHolder != null) {
				// (position) % ListUtils.getSize(imageIdList) + 1
				TopNews topNews = getmTopNewsList.get(arg0 % images.size());
				pageIndex = arg0;
				pagerHolder.title.setText(topNews.getItitled());
				// pagerHolder.labse.setText(topNews.getLabels());

				for (View v : points) {
					v.setBackgroundResource(R.drawable.point_gray);
				}
				points.get(arg0 % images.size()).setBackgroundResource(
						R.drawable.point_red);
			}

			switch (arg0 % images.size()) {
			case 0:
				mActivity.side_drawer.removeIgnoredView(pagerHolder.pager);
				break;
			default:
				mActivity.side_drawer.addIgnoredView(pagerHolder.pager);
				break;
			}

		}

	}

	// 两种item类型
	private class ViewPagerHolder {
		AutoScrollViewPager pager;
		LinearLayout point_content;
		MyTextView title;
		// MyTextView labse;

	}

	// 两种item类型
	private class NewHolder {
		NewContentHolder itemView1;
		NewContentHolder itemView2;

		public void parserView(int position, View v) {
			if (position == 1) {

				itemView1 = new NewContentHolder();
				itemView1.v = v;
				itemView1.imageView = (ImageView) v
						.findViewById(R.id.new_imageview);

				itemView1.imageView
						.setLayoutParams(new RelativeLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								(int) (mActivity.newsW / 1.5)));

				itemView1.roundImageView = (RoundImageView) v
						.findViewById(R.id.item_iv);
				itemView1.name = (MyTextView) v.findViewById(R.id.item_tv_name);
				itemView1.content = (MyTextView) v
						.findViewById(R.id.item_tv_content);
			} else {
				itemView2 = new NewContentHolder();
				itemView2.v = v;
				itemView2.imageView = (ImageView) v
						.findViewById(R.id.new_imageview);

				itemView2.imageView
						.setLayoutParams(new RelativeLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								(int) (mActivity.newsW / 1.5)));

				itemView2.roundImageView = (RoundImageView) v
						.findViewById(R.id.item_iv);
				itemView2.name = (MyTextView) v.findViewById(R.id.item_tv_name);
				itemView2.content = (MyTextView) v
						.findViewById(R.id.item_tv_content);
			}
		}
	}

	private class NewContentHolder {
		View v;
		ImageView imageView;
		RoundImageView roundImageView;
		MyTextView name;
		MyTextView content;
	}

	// 没有更多数据
	class ViewHolderInfo {
		MyTextView info;
	}

	private class MyViewpagerAdapter extends PagerAdapter {

		private ArrayList<AutoImageVIew> images;

		public MyViewpagerAdapter(ArrayList<AutoImageVIew> images) {
			super();
			this.images = images;
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			AutoImageVIew autoImageVIew = images.get(position);
			((ViewPager) container).addView(autoImageVIew);
			autoImageVIew.loadImage();
			return autoImageVIew;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			AutoImageVIew autoImageVIew = images.get(position);
			((ViewPager) container).removeView(autoImageVIew);
			autoImageVIew.reciverImage();
		}
	}

	// /**
	// * 开启定时任务
	// */
	// private void startTask() {
	// // TODO Auto-generated method stub
	// isTaskRun = true;
	// mTimer = new Timer();
	// mTask = new TimerTask() {
	// @Override
	// public void run() {
	// pageIndex++;
	// mHandler.sendEmptyMessage(0);
	// }
	// };
	// mTimer.schedule(mTask, 4 * 1000, 2 * 1000);//
	// 这里设置自动切换的时间，单位是毫秒，2*1000表示2秒
	// }
	//
	// // 处理EmptyMessage(0)
	// @SuppressLint("HandlerLeak")
	// Handler mHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// setCurrentItem();
	// }
	// };
	//
	// /**
	// * 处理Page的切换逻辑
	// */
	// private void setCurrentItem() {
	// if (pageIndex == 0) {
	// pageIndex = images.size();
	// } else if (pageIndex == images.size()) {
	// pageIndex = 0;
	// }
	// pagerHolder.pager.setCurrentItem(pageIndex, false);// 取消动画
	// }
	//
	// /**
	// * 停止定时任务
	// */
	// private void stopTask() {
	// // TODO Auto-generated method stub
	// if (mTimer != null) {
	// mTimer.cancel();
	// }
	// isTaskRun = false;
	// }

}
