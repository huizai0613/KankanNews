package com.kankan.kankanews.ui.fragment.item;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.adapter.ImagePagerAdapter;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.New_Home_List;
import com.kankan.kankanews.bean.New_News_Click;
import com.kankan.kankanews.bean.New_News_Home;
import com.kankan.kankanews.bean.New_News_Top;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.item.New_Activity_Content_Graphic;
import com.kankan.kankanews.ui.item.New_Activity_Content_PicSet;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.item.New_Activity_Content_Web;
import com.kankan.kankanews.ui.item.New_Avtivity_Subject;
import com.kankan.kankanews.ui.view.AutoImageTag;
import com.kankan.kankanews.ui.view.AutoImageVIew;
import com.kankan.kankanews.ui.view.AutoScrollViewPager;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class New_HomeItemFragment extends BaseFragment implements
		OnPageChangeListener {

	private ItnetUtils instance;
	private View inflate;
	protected LinearLayout screnn_pb;
	protected View main_bg;
	private New_Home_List new_home_list;
	private ArrayList<New_News_Top> getmTopNewsList = new ArrayList<New_News_Top>();
	private LinkedList<New_News_Home> getmNewsList = new LinkedList<New_News_Home>();
	private int lineNum = 1;
	private TopAdapter adapter;
	private ArrayList<AutoImageTag> images;
	private ArrayList<View> points;
	private ImagePagerAdapter imagePagerAdapter;
	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	private String appclassid;
	private String sp;

	private int[] arrayid;
	private int item_position = 0;

	// 新闻点击量
	private ArrayList<New_News_Click> new_news_clicks;
	private HashMap<String, String> mClicks = new HashMap<String, String>();

	Timer mTimer;
	TimerTask mTask;
	int pageIndex = 0;
	boolean isTaskRun;
	// 是否有本地数据
	private boolean initLocalDate;

	// private LinearLayout main_bg;
	// private View default_bg;

	private boolean noMoreNews = false;

	public HashMap<String, SoftReference<Bitmap>> getImageCache() {
		return imageCache;
	}

	// 设置内容classID
	public void setAppclassidAndSP(String appclassid, String sp) {
		this.appclassid = appclassid;
		this.sp = sp;
	}

	// 提供给外层调用,刷新
	public void refresh() {
		listview.setSelection(0);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				listview.setmCurrentMode(Mode.PULL_FROM_START);
				listview.setRefreshing(false);
			}
		}, 100);

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (pagerHolder != null && pagerHolder.pager != null) {
			pagerHolder.pager.stopAutoScroll();
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		if (isVisibleToUser) {
			if (lastTime != 0) {
				if ((TimeUtil.now() - lastTime) / 60 >= 10) {
					refresh();
					lastTime = TimeUtil.now();
				}
			} else {
				lastTime = TimeUtil.now();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		inflate = inflater.inflate(R.layout.new_fragment_home_item, null);
		// 统计
		// initAnalytics(AndroidConfig.new_news_page);
		instance = ItnetUtils.getInstance(mActivity);
		listview = (PullToRefreshListView) inflate.findViewById(R.id.listview);
		screnn_pb = (LinearLayout) inflate.findViewById(R.id.screnn_pb);
		main_bg = inflate.findViewById(R.id.main_bg);
		main_bg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CommonUtils.isNetworkAvailable(mActivity)) {
					screnn_pb.setVisibility(View.VISIBLE);
					main_bg.setVisibility(View.GONE);
					refreshNetDate();
				}
			}
		});

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
						pagerHolder.pager.setSwipeScrollDurationFactor(0.5);
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		inflate.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		if (CommonUtils.isNetworkAvailable(mActivity)) {
			refreshNetDate();
		} else {
			initLocalDate = initLocalDate();
			if (!initLocalDate) {
				main_bg.setVisibility(View.VISIBLE);
			}
			screnn_pb.setVisibility(View.GONE);

		}

		// 处理推送逻辑
		// Bundle bun = mActivity.getIntent().getExtras();
		// String newsid = null;
		// String reporter = null;
		// if (bun != null) {
		// newsid = bun.getString("newsid");
		// }
		// if (bun != null) {
		// reporter = bun.getString("reporter");
		// }
		//
		// if (!TextUtils.isEmpty(newsid)) {
		// // startAnimActivityById(New_Activity_Content_Video.class, 0,
		// // "arrayid", new int[] { Integer.parseInt(newsid) });
		//
		// } else if (!TextUtils.isEmpty(reporter)) {
		//
		// Content_News content_News = null;
		// try {
		// content_News = mActivity.dbUtils.findFirst(Selector.from(
		// Content_News.class).where("uid", "=", reporter));
		// } catch (DbException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// if (content_News != null) {
		// mActivity.startAnimActivity2Obj(Activity_Reporter.class,
		// "content_News", content_News);
		// } else {
		// mActivity.startAnimActivity2Obj(Activity_Reporter.class, "ID",
		// reporter, null);
		// }
		// } else {
		return inflate;

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

	}

	@Override
	protected boolean initLocalDate() {
		try {
			List<New_News_Home> mNews = mActivity.dbUtils.findAll(Selector
					.from(New_News_Home.class)
					.where("classid", "=", appclassid));
			List<New_News_Top> mTopNews = mActivity.dbUtils
					.findAll(Selector.from(New_News_Top.class).where("classid",
							"=", appclassid));
			List<New_News_Click> mNewsClick = mActivity.dbUtils
					.findAll(Selector.from(New_News_Click.class).where(
							"classid", "=", appclassid));

			if (mNewsClick != null) {
				new_news_clicks = new ArrayList<New_News_Click>(mNewsClick);
			}
			if (mNews != null) {
				getmNewsList = new LinkedList<New_News_Home>(mNews);
			}
			if (mTopNews != null && mTopNews.size() > 0) {
				getmTopNewsList = new ArrayList<New_News_Top>(mTopNews);
			} else {
				return false;
			}
			initTopImageAndPoint();
			if (mTopNews != null && mNews != null) {
				adapter = new TopAdapter();
				listview.setAdapter(adapter);
				// 点击量
				for (int i = 0; new_news_clicks != null
						&& i < new_news_clicks.size(); i++) {
					mClicks.put(new_news_clicks.get(i).getId(), new_news_clicks
							.get(i).getClickTime());
				}
				adapter = new TopAdapter();
				listview.setAdapter(adapter);
				return true;
			}

		} catch (DbException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void addData(final boolean isClear) {

		if (!isLoadMore) {
			long time = refreshStartTime - System.currentTimeMillis();
			long sleepTime = 0;
			if (time < 1000) {
				sleepTime = 1000 - time;
			}

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (pagerHolder != null && pagerHolder.pager != null) {
						pagerHolder.pager.setOnPageChangeListener(null);
						pagerHolder.pager = null;

					}
					getmTopNewsList = new_home_list.getTop_list();
					getmNewsList = new_home_list.getHome_list();

					ArrayList<New_News_Home> arrayList = new ArrayList<New_News_Home>(
							getmNewsList);

					if (isClear) {
						// 点击量
						mClicks.clear();
						for (int i = 0; i < new_news_clicks.size(); i++) {
							mClicks.put(new_news_clicks.get(i).getId(),
									new_news_clicks.get(i).getClickTime());
						}
					}

					saveLocalDate();
					initTopImageAndPoint();
					listview.setBackgroundColor(Color.parseColor("#f5f5f5"));
					adapter = new TopAdapter();
					listview.setAdapter(adapter);
					listview.onRefreshComplete();
					screnn_pb.setVisibility(View.GONE);
				}
			}, sleepTime);

		} else {
			getmNewsList.addAll(new_home_list.getHome_list());
			// 点击量
			for (int i = 0; i < new_news_clicks.size(); i++) {
				mClicks.put(new_news_clicks.get(i).getId(), new_news_clicks
						.get(i).getClickTime());
			}

			adapter.notifyDataSetChanged();
			listview.onRefreshComplete();
			screnn_pb.setVisibility(View.GONE);
		}

	}

	// 初始化id数组
	private void init_arrayid() {
		arrayid = new int[(getmTopNewsList == null ? 0 : getmTopNewsList.size())
				+ (getmNewsList == null ? 0 : getmNewsList.size())];
		item_position = 0;
		if (getmTopNewsList != null) {
			for (New_News_Top tn : getmTopNewsList) {
				arrayid[item_position] = Integer.parseInt(tn.getId());
				item_position++;
			}
		}

		if (getmNewsList != null) {
			for (New_News_Home n : getmNewsList) {
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
			for (final New_News_Top tn : getmTopNewsList) {
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

		}
	}

	long lastTime;
	boolean refreshOK;
	long refreshStartTime;

	@Override
	protected void refreshNetDate() {

		if (CommonUtils.isNetworkAvailable(mActivity)) {
			refreshStartTime = System.currentTimeMillis();
			isLoadMore = false;
			noMoreNews = false;
			instance.getNewHomeData("", appclassid, sp, mListenerObject,
					mErrorListener);
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
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			isLoadMore = true;
			instance.getNewHomeData(getmNewsList.getLast().getNewstime(),
					appclassid, sp, mListenerObject, mErrorListener);
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
	protected void onSuccessObject(JSONObject jsonObject) {
		noMoreNews = false;
		if (new_home_list == null) {
			new_home_list = new New_Home_List();
			new_home_list.setClassID(appclassid);
		}
		refreshOK = true;
		try {
			new_home_list = new_home_list.parseJSON(jsonObject);
		} catch (NetRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (new_home_list.getHome_list().size() > 0) {
			String midtype = "";
			new_news_clicks = new ArrayList<New_News_Click>();
			for (int i = 0; i < new_home_list.getHome_list().size(); i++) {
				New_News_Home new_news = new_home_list.getHome_list().get(i);
				;

				if (Integer.parseInt(new_news.getType()) % 10 != 5
						&& Integer.parseInt(new_news.getType()) % 10 != 6) {
					New_News_Click new_news_click = new New_News_Click();
					new_news_click.setId(new_news.getMid());
					new_news_click.setType(new_news.getType());
					new_news_click.setClassid(appclassid);
					new_news_clicks.add(new_news_click);
					midtype = midtype + new_news.getMid() + ":"
							+ Integer.valueOf(new_news.getType()) % 10 + "_";
				}
			}
			midtype = midtype.substring(0, midtype.length() - 1);
			instance.getNewNewsClickData(midtype, getClickTimeListener,
					getClickTimeErrorListener);
		} else {
			new_news_clicks = new ArrayList<New_News_Click>();
			addData(false);
		}

		// addData();
	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onFailure(VolleyError error) {
		listview.onRefreshComplete();
		screnn_pb.setVisibility(View.GONE);
		String message = error.getMessage();
		if ("java.lang.Throwable: 没有数据".equals(message)) {
			if (isLoadMore) {
				if (noMoreNews == false) {
					noMoreNews = true;
				}
			} else {
				// 刷新接口有问题
			}
		} else {

			ToastUtils.Infotoast(mActivity, "网络不给力");
			if (initLocalDate) {
				refreshOK = false;
			} else {
				main_bg.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void saveLocalDate() {
		// arrayid = new int[(getmTopNewsList == null ? 0 :
		// getmTopNewsList.size())
		// + (getmNewsList == null ? 0 : getmNewsList.size())];

		new Thread() {
			public void run() {

				if (new_home_list != null) {
					try {
						if (mActivity.dbUtils.tableIsExist(New_News_Top.class)) {
							mActivity.dbUtils.delete(New_News_Top.class,
									WhereBuilder.b("classid", "=", appclassid));
						}

						if (mActivity.dbUtils.tableIsExist(New_News_Home.class)) {
							mActivity.dbUtils.delete(New_News_Home.class,
									WhereBuilder.b("classid", "=", appclassid));
						}
						if (mActivity.dbUtils
								.tableIsExist(New_News_Click.class)) {
							mActivity.dbUtils.delete(New_News_Click.class,
									WhereBuilder.b("classid", "=", appclassid));
						}

					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (getmTopNewsList != null) {
					try {
						// mActivity.dbUtils.delete(Selector.from(New_News.class).where("my_type","=","home_top_news").or("my_type","=","home_news"));
						mActivity.dbUtils.saveOrUpdateAll(getmTopNewsList);
					} catch (DbException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if (getmNewsList != null) {
					try {
						mActivity.dbUtils.saveOrUpdateAll(getmNewsList);
					} catch (DbException e) {
						e.printStackTrace();
					}
				}
				if (new_news_clicks != null) {
					try {
						mActivity.dbUtils.saveOrUpdateAll(new_news_clicks);
					} catch (DbException e) {
						e.printStackTrace();
					}
					;
				}
			};
		}.start();

	}

	ViewPagerHolder pagerHolder = null;
	NewContentHolder newHolder = null;
	NewAlbumsHolder albumsHolder = null;
	NewZhuanTiHolder newZhuanTiHolder = null;
	ViewHolderInfo holderInfo = null;

	private class TopAdapter extends BaseAdapter {

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
						: (getmNewsList
								.size() + lineNum - 1)
								/ lineNum;
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
			return 5;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			boolean hasTopNews = getmTopNewsList.size() > 0;
			int newsPosition = hasTopNews ? position - 1 : position;
			if (position == 0 && hasTopNews) {
				return 0;
			} else if (noMoreNews && getmNewsList.size() + 1 <= position) {
				return 2;//已加载全部
			} else if (Integer
					.valueOf(getmNewsList.get(newsPosition).getType()) % 10 == 2) {
				return 3;//图集
			} else if (getmNewsList.get(newsPosition).getZtype().equals("1")) {
				return 4;
			} else {
				return 1;
			}

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			int itemViewType = getItemViewType(position);
			boolean hasTopNews = getmTopNewsList.size() > 0;
			int newsPosition = hasTopNews ? position - 1 : position;
//			if (getmTopNewsList != null && getmTopNewsList.size() > 0) {
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
										(int) (mActivity.topNewW / 1.7)));
						convertView
								.setLayoutParams(new AbsListView.LayoutParams(
										RelativeLayout.LayoutParams.MATCH_PARENT,
										(int) (mActivity.topNewW / 1.7)
												+ PixelUtil.dp2px(45)));

						pagerHolder.pager
								.setOnPageChangeListener(New_HomeItemFragment.this);
						convertView.setTag(pagerHolder);
						imagePagerAdapter = new ImagePagerAdapter(mActivity,
								New_HomeItemFragment.this, images,
								getmTopNewsList);
						pagerHolder.pager.stopAutoScroll();
						if (images.size() > 1) {
							pagerHolder.pager
									.setCurrentItem(Integer.MAX_VALUE / 2
											- Integer.MAX_VALUE / 2
											% images.size());
							imagePagerAdapter.setInfiniteLoop(true);
							pagerHolder.pager.startAutoScroll(3000);
							pagerHolder.pager.setSwipeScrollDurationFactor(0.5);

						} else {
							imagePagerAdapter.setInfiniteLoop(false);
							pagerHolder.pager.stopAutoScroll();
						}
						pagerHolder.pager.setAdapter(imagePagerAdapter);
						// pagerHolder.pager.setSwipeScrollDurationFactor(2.0);

						int size = points.size();
						if (size > 1) {
							points.get(0).setBackgroundResource(
									R.drawable.point_red);
							for (View v : points) {
								pagerHolder.point_content.addView(v);
							}
							pagerHolder.point_content
									.setVisibility(View.VISIBLE);

						} else {
							pagerHolder.point_content.setVisibility(View.GONE);
						}
						pagerHolder.title.setText(getmTopNewsList.get(0)
								.getTitle());

					} else if (itemViewType == 1) {
						convertView = inflate.inflate(mActivity,
								R.layout.new_home_news_item, null);
						newHolder = new NewContentHolder();
						newHolder.titlepic = (ImageView) convertView
								.findViewById(R.id.home_news_titlepic);

						newHolder.title = (MyTextView) convertView
								.findViewById(R.id.home_news_title);
						newHolder.newstime_sign = (ImageView) convertView
								.findViewById(R.id.home_news_newstime_sign);
						newHolder.newstime = (MyTextView) convertView
								.findViewById(R.id.home_news_newstime);
						newHolder.news_type = (ImageView) convertView
								.findViewById(R.id.home_news_newstype);
						newHolder.home_news_play = (ImageView) convertView
								.findViewById(R.id.home_news_play);
						convertView.setTag(newHolder);
					} else if (itemViewType == 2) {
						holderInfo = new ViewHolderInfo();
						convertView = LayoutInflater.from(mActivity).inflate(
								R.layout.comment_nomore, null);
						holderInfo.info = (MyTextView) convertView
								.findViewById(R.id.comment_no_more);
						convertView.setTag(holderInfo);
					} else if (itemViewType == 3) {
						convertView = inflate.inflate(mActivity,
								R.layout.new_home_news_albums_item, null);
						albumsHolder = new NewAlbumsHolder();
						albumsHolder.title = (MyTextView) convertView
								.findViewById(R.id.home_albums_title);
						albumsHolder.home_albums_imgs_layout = (LinearLayout) convertView
								.findViewById(R.id.home_albums_imgs_layout);
						albumsHolder.albums_image_1 = (ImageView) convertView
								.findViewById(R.id.home_albums_img_1);
						albumsHolder.albums_image_2 = (ImageView) convertView
								.findViewById(R.id.home_albums_img_2);
						albumsHolder.albums_image_3 = (ImageView) convertView
								.findViewById(R.id.home_albums_img_3);
						// image_view_list.clear();
						// image_view_list.add(albumsHolder.albums_image_1);
						// image_view_list.add(albumsHolder.albums_image_2);
						// image_view_list.add(albumsHolder.albums_image_3);
						albumsHolder.home_albums_imgs_layout
								.setLayoutParams(new LinearLayout.LayoutParams(
										LinearLayout.LayoutParams.MATCH_PARENT,
										(int) ((mActivity.topNewW - PixelUtil
												.dp2px(10 * 4)) / 3 * 0.7)));
						convertView.setTag(albumsHolder);
					} else if (itemViewType == 4) {
						convertView = inflate.inflate(mActivity,
								R.layout.new_home_news_zhuanti_item, null);
						newZhuanTiHolder = new NewZhuanTiHolder();
						newZhuanTiHolder.title = (MyTextView) convertView
								.findViewById(R.id.title);
						newZhuanTiHolder.home_news_titlepic = (ImageView) convertView
								.findViewById(R.id.home_news_titlepic);
						newZhuanTiHolder.home_news_intro = (MyTextView) convertView
								.findViewById(R.id.home_news_intro);

						newZhuanTiHolder.home_news_titlepic
								.setLayoutParams(new LinearLayout.LayoutParams(
										(int) ((mActivity.topNewW - PixelUtil
												.dp2px(10 * 2))),
										(int) ((float) ((mActivity.topNewW - PixelUtil
												.dp2px(10 * 2)) / 3.2))));
						newZhuanTiHolder.home_news_titlepic.setTag(
								R.string.viewwidth,
								(int) ((mActivity.topNewW - PixelUtil
										.dp2px(10 * 2))));
						convertView.setTag(newZhuanTiHolder);
					}
				} else {
					if (itemViewType == 0) {
						pagerHolder = (ViewPagerHolder) convertView.getTag();
					} else if (itemViewType == 1) {
						newHolder = (NewContentHolder) convertView.getTag();
					} else if (itemViewType == 2) {
						holderInfo = (ViewHolderInfo) convertView.getTag();
					} else if (itemViewType == 3) {
						albumsHolder = (NewAlbumsHolder) convertView.getTag();
					} else if (itemViewType == 4) {
						newZhuanTiHolder = (NewZhuanTiHolder) convertView
								.getTag();
					}
				}

				if (itemViewType == 0) {
					imagePagerAdapter.notifyDataSetChanged();
				} else if (itemViewType == 1) {
					final New_News_Home news = getmNewsList.get(newsPosition);
					String clicktime = mClicks.get(news.getMid());
					clicktime = TextUtils.isEmpty(clicktime) ? "0次" : clicktime
							+ "次";
					final int news_type = Integer.valueOf(news.getType());
					newHolder.titlepic.setTag(R.string.viewwidth,
							PixelUtil.dp2px(80));
					CommonUtils.zoomImage(imageLoader, news.getTitlepic(),
							newHolder.titlepic, mActivity, imageCache);

					// imageLoader.displayImage(news.getTitlepic(),
					// newHolder.titlepic,
					// Options.getSmallImageOptions(false));
					newHolder.title.setText(news.getTitle());
					newHolder.newstime.setText(clicktime);
					switch (news_type / 10) {
					case 1:
						newHolder.news_type
								.setImageResource(R.drawable.new_icon_sign_unique);
						break;
					case 2:
						newHolder.news_type
								.setImageResource(R.drawable.new_icon_sign_tui);
						break;
					case 5:
						newHolder.news_type
								.setImageResource(R.drawable.new_icon_sign_subject);
						break;

					default:
						newHolder.news_type.setImageBitmap(null);
					}

					switch (news_type % 10) {
					case 5: // 小专题
						newHolder.newstime.setVisibility(View.INVISIBLE);
						newHolder.newstime_sign.setVisibility(View.INVISIBLE);
						newHolder.news_type
								.setImageResource(R.drawable.new_icon_sign_subject);
						break;
					case 6:
						newHolder.newstime.setVisibility(View.INVISIBLE);
						newHolder.newstime_sign
								.setImageResource(R.drawable.new_icon_newstime);
						newHolder.newstime
								.setText(TimeUtil.unix2date(
										Long.valueOf(news.getStime()), "mm:ss")
										+ " - "
										+ TimeUtil.unix2date(
												Long.valueOf(news.getEtime()),
												"mm:ss"));
						newHolder.newstime_sign.setVisibility(View.INVISIBLE);
						newHolder.news_type
								.setImageResource(R.drawable.new_icon_sign_live);
						break;
					default:

						if (clicktime.equalsIgnoreCase("false次")) {
							newHolder.newstime.setVisibility(View.INVISIBLE);
							newHolder.newstime_sign
									.setVisibility(View.INVISIBLE);
						} else {
							newHolder.newstime.setVisibility(View.VISIBLE);
							newHolder.newstime_sign.setVisibility(View.VISIBLE);
						}
						break;
					}

					// if (news_type % 10 == 1) {
					// newHolder.home_news_play.setVisibility(View.VISIBLE);
					// } else {
					// newHolder.home_news_play.setVisibility(View.GONE);
					// }

					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (news_type % 10 == 1) {
								mActivity.startAnimActivityByParameter(
										New_Activity_Content_Video.class,
										news.getMid(), news.getType(),
										news.getTitleurl(), news.getNewstime(),
										news.getTitlepic(), news.getTitle());
							} else if (news_type % 10 == 5) {
								// 专题
								mActivity.startSubjectActivityByParameter(
										New_Avtivity_Subject.class,
										news.getZtid(), news.getTitle(),
										news.getTitlepic(), news.getTitleurl());
							} else if (news_type % 10 == 6) {// 直播
								New_LivePlayFragment fragment = (New_LivePlayFragment) mActivity.fragments
										.get(1);
								fragment.setSelectPlay(true);
								fragment.setSelectPlayID(Integer.parseInt(news
										.getZtid()));
								mActivity.touchTab(mActivity.tab_two);

							} else {
								mActivity.startAnimActivityByParameter(
										New_Activity_Content_Web.class,
										news.getMid(), news.getType(),
										news.getTitleurl(), news.getNewstime(),
										news.getTitlepic(), news.getTitle());
							}
						}
					});
				} else if (itemViewType == 2) {
					int padding_in_dp = 10; // 6 dps
					final float scale = getResources().getDisplayMetrics().density;
					int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
					holderInfo.info.setPadding(0, padding_in_px, 0,
							padding_in_px);
				} else if (itemViewType == 3) {
					final New_News_Home news = getmNewsList.get(newsPosition);
					albumsHolder.title.setText(news.getTitle());
					final String[] pics = news.getTitlepic().split("::::::");
					ArrayList<ImageView> image_view_list = new ArrayList<ImageView>();

					int width = (mActivity.mScreenWidth - PixelUtil.dp2px(20) / 3);
					albumsHolder.albums_image_1
							.setScaleType(ScaleType.CENTER_CROP);
					albumsHolder.albums_image_1.setTag(R.string.viewwidth,
							width);
					albumsHolder.albums_image_1.setTag(R.string.isTop, true);
					albumsHolder.albums_image_2
							.setScaleType(ScaleType.CENTER_CROP);
					albumsHolder.albums_image_2.setTag(R.string.viewwidth,
							width);
					albumsHolder.albums_image_2.setTag(R.string.isTop, true);
					albumsHolder.albums_image_3
							.setScaleType(ScaleType.CENTER_CROP);
					albumsHolder.albums_image_3.setTag(R.string.viewwidth,
							width);
					albumsHolder.albums_image_3.setTag(R.string.isTop, true);
					image_view_list.add(albumsHolder.albums_image_1);
					image_view_list.add(albumsHolder.albums_image_2);
					image_view_list.add(albumsHolder.albums_image_3);
					for (int i = 0; i < (pics.length > 3 ? 3 : pics.length); i++) {
						// imageLoader.displayImage(pics[i + 1],
						// image_view_list.get(i),
						// Options.getSmallImageOptions(false));
						if(pics.length > i){
							CommonUtils.zoomImage(imageLoader, pics[i + 1],
									image_view_list.get(i), mActivity, imageCache);
						}
					}

					convertView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// startAnimActivityByBean(New_ActivityPicSet.class,
							// "news_content", news);
							mActivity.startAnimActivityByParameter(
									New_Activity_Content_PicSet.class,
									news.getMid(), news.getType(),
									news.getTitleurl(), news.getNewstime(),
									pics[1], news.getTitle());
						}
					});

				} else if (itemViewType == 4) {
					final New_News_Home news = getmNewsList.get(newsPosition);
					newZhuanTiHolder.title.setText(news.getTitle());
					CommonUtils.zoomImage(imageLoader, news.getTitlepic(),
							newZhuanTiHolder.home_news_titlepic, mActivity);
					// imageLoader.displayImage(news.getTitlepic(),
					// newZhuanTiHolder.home_news_titlepic,
					// Options.getSmallImageOptions(false));
					// newZhuanTiHolder.home_news_intro.setText(news.getIntro()
					// .length() > 45 ? news.getIntro().subSequence(0, 45)
					// + "..." : news.getIntro());

					newZhuanTiHolder.home_news_intro.setText(news.getIntro());

					convertView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// 专题
							mActivity.startSubjectActivityByParameter(
									New_Avtivity_Subject.class, news.getZtid(),
									news.getTitle(), news.getTitlepic(),
									news.getTitleurl());
						}
					});

				}
//			}

			return convertView;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		if (pagerHolder != null) {
			pagerHolder.title.setText(getmTopNewsList.get(arg0 % images.size())
					.getTitle());
			for (View v : points) {
				v.setBackgroundResource(R.drawable.point_gray);
			}
			points.get(arg0 % images.size()).setBackgroundResource(
					R.drawable.point_red);
		}
	}

	// 两种item类型
	private class ViewPagerHolder {
		AutoScrollViewPager pager;
		LinearLayout point_content;
		MyTextView title;
		// MyTextView labse;

	}

	private class NewContentHolder {
		ImageView titlepic;
		MyTextView title;
		ImageView newstime_sign;
		ImageView home_news_play;
		MyTextView newstime;
		ImageView news_type;
	}

	private class NewAlbumsHolder {
		MyTextView title;
		LinearLayout home_albums_imgs_layout;
		ImageView albums_image_1;
		ImageView albums_image_2;
		ImageView albums_image_3;

	}

	private class NewZhuanTiHolder {
		MyTextView title;
		ImageView home_news_titlepic;
		MyTextView home_news_intro;
	}

	// 没有更多数据
	class ViewHolderInfo {
		MyTextView info;
	}

	/*
	 * 获取新闻点击量
	 */
	// 处理网络出错
	protected ErrorListener getClickTimeErrorListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			addData(false);
			error.printStackTrace();
			listview.onRefreshComplete();
		}
	};
	// 处理网络成功
	protected Listener<JSONArray> getClickTimeListener = new Listener<JSONArray>() {
		@Override
		public void onResponse(JSONArray jsonObject) {

			try {
				// JSONArray jsonArray = new JSONArray(jsonObject.toString());
				JSONArray jsonArray = jsonObject;
				if (jsonArray != null && jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
						String clickTime = jsonObject1
								.optString(new_news_clicks.get(i).getId()
										+ "_"
										+ (Integer.valueOf(new_news_clicks.get(
												i).getType()) % 10));
						new_news_clicks.get(i).setClickTime(clickTime);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			addData(true);
			// new_news_clicks
		}
	};

	// 销毁图片
	public void recycle() {
		Set<Entry<String, SoftReference<Bitmap>>> entrySet = imageCache
				.entrySet();
		for (Entry<String, SoftReference<Bitmap>> e : entrySet) {
			Bitmap bitmap = e.getValue().get();
			if (bitmap != null) {
				bitmap.recycle();
			}
			imageCache.put(e.getKey(), new SoftReference<Bitmap>(null));
		}
	}

}