package com.kankan.kankanews.ui.fragment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.kankan.kankanews.adapter.RecyclingPagerAdapter;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.Keyboard;
import com.kankan.kankanews.bean.NewsHome;
import com.kankan.kankanews.bean.NewsHomeModule;
import com.kankan.kankanews.bean.NewsHomeModuleItem;
import com.kankan.kankanews.bean.SerializableObj;
import com.kankan.kankanews.ui.ColumsActivity;
import com.kankan.kankanews.ui.MeSetActivity;
import com.kankan.kankanews.ui.SearchMainActivity;
import com.kankan.kankanews.ui.item.NewsAlbumActivity;
import com.kankan.kankanews.ui.item.NewsContentActivity;
import com.kankan.kankanews.ui.item.NewsListActivity;
import com.kankan.kankanews.ui.item.NewsOutLinkActivity;
import com.kankan.kankanews.ui.item.NewsTopicActivity;
import com.kankan.kankanews.ui.item.NewsTopicListActivity;
import com.kankan.kankanews.ui.item.NewsVideoPackageActivity;
import com.kankan.kankanews.ui.view.BorderTextView;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class NewsHomeFragment extends BaseFragment implements OnClickListener,
		OnPageChangeListener {

	private View inflate;
	private View mLogoView;
	private View mLoadingView;
	private View mRetryView;
	private View mGoColumsBut;
	private View mGoMeSetBut;
	private PullToRefreshListView mNewsHomeListView;

	private String mNewsHomeListJson;
	private NewsHome mNewsHome;
	private boolean mIsNeedRefresh = true;

	private NewsHomeSwiperHeadAdapter mNewsHomeSwiperHeadAdapter;
	private NewsHomeListAdapter mNewsHomeListAdapter;
	private SwiperHeadHolder mSwiperHeadHolder;
	private MatrixHolder mMatrixHolder;
	private MatrixListHolder mMatrixListHolder;
	private GalleryHolder mGalleryHolder;
	private TopicOneHolder mTopicOneHolder;
	private TopicTwoHolder mTopicTwoHolder;
	private OutLinkHolder mOutLinkHolder;
	private VoteHolder mVoteHolder;

	private int mLastVisibleItem = 2;

	private String[] VOTE_ANSWER_PREFIX = { "A.", "B.", "C.", "D.", "E.", "F." };

	private String[] VOTE_ANSWER_COLOR = { "#5cc644", "#51bce4", "#ff9a16",
			"#ec3838", "#00FFFF", "#FF00FF" };

	private Map<String, Integer> mRandomNumMap = new HashMap<String, Integer>();

	private DecimalFormat mVoteNumDF = new DecimalFormat("0.00");

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		inflate = inflater.inflate(R.layout.fragment_news_home, null);
		initView();
		initData();
		initLinsenter();
		return inflate;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void initView() {
		mGoColumsBut = inflate.findViewById(R.id.go_colums_but);
		mGoMeSetBut = inflate.findViewById(R.id.go_me_set_but);
		mNewsHomeListView = (PullToRefreshListView) inflate
				.findViewById(R.id.news_home_listview);
		mLoadingView = inflate.findViewById(R.id.activity_loading_view);
		mRetryView = inflate.findViewById(R.id.activity_retry_view);
		mLogoView = inflate.findViewById(R.id.home_logo_but);
		initListView();
	}

	protected void initListView() {
		mNewsHomeListView.setMode(Mode.PULL_FROM_START);
		mNewsHomeListView.getLoadingLayoutProxy(true, false).setPullLabel(
				"下拉可以刷新");
		mNewsHomeListView.getLoadingLayoutProxy(true, false)
				.setRefreshingLabel("刷新中…");
		mNewsHomeListView.getLoadingLayoutProxy(true, false).setReleaseLabel(
				"释放后刷新");
		mNewsHomeListView
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
		mNewsHomeListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态
					int position = view.getFirstVisiblePosition();
					if (1 == position) {
						if (mLastVisibleItem > 1) {
							mNewsHomeListView.setSelection(1);
							mLastVisibleItem = 1;
							break;
						} else {
							mNewsHomeListView.setSelection(2);
							mLastVisibleItem = 2;
							break;
						}
					}
					mLastVisibleItem = position;
					break;
				case OnScrollListener.SCROLL_STATE_FLING:// 滚动状态
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	private void initData() {
		boolean hasLocal = initLocalDate();
		if (hasLocal) {
			showData();
		}
		if (CommonUtils.isNetworkAvailable(this.mActivity) && mIsNeedRefresh) {
			// refreshNetDate();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mNewsHomeListView.setmCurrentMode(Mode.PULL_FROM_START);
					mNewsHomeListView.setRefreshing(false);
				}
			}, 100);
		} else {
			if (hasLocal) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mNewsHomeListView.setSelection(2);
					}
				}, 200);
			} else {
				this.mLoadingView.setVisibility(View.GONE);
				this.mRetryView.setVisibility(View.VISIBLE);
			}
		}
	}

	private void initLinsenter() {
		mRetryView.setOnClickListener(this);
		mGoColumsBut.setOnClickListener(this);
		mGoMeSetBut.setOnClickListener(this);
		mLogoView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.go_colums_but:
			this.startActivity(new Intent(this.mActivity, ColumsActivity.class));
			this.mActivity.overridePendingTransition(R.anim.in_from_right,
					R.anim.alpha_out);
			break;
		case R.id.go_me_set_but:
			this.startActivity(new Intent(this.mActivity, MeSetActivity.class));
			this.mActivity.overridePendingTransition(R.anim.in_from_right,
					R.anim.alpha_out);
			break;
		case R.id.activity_retry_view:
			if (CommonUtils.isNetworkAvailable(this.mActivity)) {
				this.mRetryView.setVisibility(View.GONE);
				this.mLoadingView.setVisibility(View.VISIBLE);
				refreshNetDate();
			}
			break;
		case R.id.home_logo_but:
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mNewsHomeListView.setSelection(2);
				}
			}, 100);
			break;
		}
	}

	private void showData() {
		this.mRetryView.setVisibility(View.GONE);
		this.mLoadingView.setVisibility(View.GONE);
		if (mNewsHomeListAdapter == null) {
			mNewsHomeListAdapter = new NewsHomeListAdapter();
			mNewsHomeListView.setAdapter(mNewsHomeListAdapter);
		} else {
			mNewsHomeListAdapter.notifyDataSetChanged();
		}
		if (mNewsHomeSwiperHeadAdapter != null) {
			mNewsHomeSwiperHeadAdapter.setItemList(mNewsHome.getModule_list()
					.get(0).getList());
			mNewsHomeSwiperHeadAdapter.notifyDataSetChanged();
		}
		// new Handler().postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// mNewsHomeListView.setSelection(2);
		// }
		// }, 200);
	}

	@Override
	public void refresh() {
		this.mNewsHomeListView.setSelection(0);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mNewsHomeListView.setmCurrentMode(Mode.PULL_FROM_START);
				mNewsHomeListView.setRefreshing(false);
			}
		}, 100);
	}

	@Override
	protected boolean initLocalDate() {
		try {
			SerializableObj object = (SerializableObj) this.mActivity.dbUtils
					.findFirst(Selector.from(SerializableObj.class).where(
							"classType", "=", "NewsHome"));
			if (object != null) {
				mNewsHomeListJson = object.getJsonStr();
				mNewsHome = JsonUtils.toObject(mNewsHomeListJson,
						NewsHome.class);
				if (TimeUtil.isListSaveTimeOK(object.getSaveTime()))
					mIsNeedRefresh = false;
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
					.toString(), mNewsHomeListJson, "NewsHome",
					new Date().getTime());
			this.mActivity.dbUtils.delete(SerializableObj.class,
					WhereBuilder.b("classType", "=", "NewsHome"));
			this.mActivity.dbUtils.save(obj);
		} catch (DbException e) {
			DebugLog.e(e.getLocalizedMessage());
		}
	}

	@Override
	protected void refreshNetDate() {
		if (CommonUtils.isNetworkAvailable(this.mActivity)) {
			netUtils.getNewsHomeList(mListenerObject, mErrorListener);
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mNewsHomeListView.onRefreshComplete();
				}
			}, 500);
		}
	}

	@Override
	protected void loadMoreNetDate() {
	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {
		if (jsonObject != null && !jsonObject.toString().trim().equals("")) {
			mRandomNumMap = new HashMap<String, Integer>();
			this.mNewsHomeListJson = jsonObject.toString();
			mNewsHome = JsonUtils.toObject(this.mNewsHomeListJson,
					NewsHome.class);
			if (judgeObject()) {
				showData();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mNewsHomeListView.setSelection(2);
					}
				}, 200);
				saveLocalDate();
			}
		}
		mNewsHomeListView.onRefreshComplete();
	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
	}

	@Override
	protected void onFailure(VolleyError error) {
	}

	private class SwiperHeadHolder {
		ViewPager imgViewPager;
		TextView title;
		LinearLayout pointRootView;
	}

	private class MatrixHolder {
		ImageView icon;
		TextView title;
		View[] rootView = new View[4];
		// ImageView img0;
		// MyTextView title0;
		// ImageView img1;
		// MyTextView title1;
		// ImageView img2;
		// MyTextView title2;
		// ImageView img3;
		// MyTextView title3;
		View change;
		ImageView changeIcon;
		View more;
	}

	private class MatrixListHolder {
		View more;
		ImageView icon;
		TextView title;
		View rootView0;
		ImageView img0;
		ImageView icon0;
		TextView title0;
		TextView intro0;
		View rootView1;
		ImageView img1;
		ImageView icon1;
		TextView title1;
		TextView click1;
		View rootView2;
		ImageView img2;
		ImageView icon2;
		TextView title2;
		TextView click2;
	}

	private class GalleryHolder {
		TextView title;
		LinearLayout rootView;
	}

	private class TopicOneHolder {
		ImageView titlePic;
		TextView title;
		TextView intro;
	}

	private class TopicTwoHolder {
		ImageView titlePic0;
		ImageView titlePic1;
	}

	private class OutLinkHolder {
		ImageView titlePic;
		TextView title;
		LinearLayout keyboardIconContent;
	}

	private class VoteHolder {
		ImageView icon;
		TextView title;
		View change;
		View changeIcon;
		TextView quetions;
		LinearLayout rootView;
	}

	public class NewsHomeListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mNewsHome.getModule_list().size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (position == 0)
				return null;
			return mNewsHome.getModule_list().get(position - 1);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * 0 头图 1 2*2矩阵 2 1+2列表 3 横向图片展示 4 单一专题 5 横向双专题 6 投票 7 外链
		 */
		@Override
		public int getItemViewType(int position) {
			if (position == 0)
				return -1;
			NewsHomeModule module = mNewsHome.getModule_list()
					.get(position - 1);
			if ("swiper-head".equals(module.getType())) {
				return 0;
			} else if ("matrix".equals(module.getType())
					&& module.getNum() == 4) {
				return 1;
			} else if ("matrix".equals(module.getType())
					&& module.getNum() == 3) {
				return 2;
			} else if ("swiper-video".equals(module.getType())) {
				return 3;
			} else if ("topic".equals(module.getType())) {
				return 4;
			} else if ("topicpackage".equals(module.getType())) {
				return 5;
			} else if ("outlink".equals(module.getType())) {
				return 6;
			} else if ("vote".equals(module.getType())) {
				return 7;
			}
			return -2;
		}

		@Override
		public int getViewTypeCount() {
			return 9;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			int itemType = getItemViewType(position);
			if (itemType == -1) {
				if (convertView == null) {
					convertView = inflate.inflate(mActivity,
							R.layout.item_news_home_search, null);
					View searchView = convertView
							.findViewById(R.id.item_news_home_search_view);
					searchView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							NewsHomeFragment.this.startActivity(new Intent(
									NewsHomeFragment.this.mActivity,
									SearchMainActivity.class));
							NewsHomeFragment.this.mActivity
									.overridePendingTransition(R.anim.alpha_in,
											R.anim.alpha_out);
						}
					});
				}
				return convertView;
			}
			final NewsHomeModule module = mNewsHome.getModule_list().get(
					position - 1);
			if (convertView == null) {
				if (itemType == 0) {
					// TODO
					mSwiperHeadHolder = new SwiperHeadHolder();
					convertView = inflate.inflate(mActivity,
							R.layout.item_news_home_swiper_head, null);
					mSwiperHeadHolder.title = (TextView) convertView
							.findViewById(R.id.news_home_swiper_head_title);
					FontUtils.setTextViewFontSizeDIP(NewsHomeFragment.this,
							mSwiperHeadHolder.title,
							R.string.home_news_text_size, 1);
					mSwiperHeadHolder.imgViewPager = (ViewPager) convertView
							.findViewById(R.id.news_home_swiper_head_view_pager);
					mSwiperHeadHolder.imgViewPager
							.setLayoutParams(new RelativeLayout.LayoutParams(
									RelativeLayout.LayoutParams.MATCH_PARENT,
									(int) (mActivity.mScreenWidth / 6.4 * 3)));
					mSwiperHeadHolder.imgViewPager
							.setOnPageChangeListener(NewsHomeFragment.this);
					mNewsHomeSwiperHeadAdapter = new NewsHomeSwiperHeadAdapter(
							module.getList());
					mSwiperHeadHolder.imgViewPager
							.setAdapter(mNewsHomeSwiperHeadAdapter);
					mSwiperHeadHolder.imgViewPager.setCurrentItem(0);
					mSwiperHeadHolder.pointRootView = (LinearLayout) convertView
							.findViewById(R.id.news_home_swiper_head_point_root_view);
					if (module.getList().size() > 1) {
						mSwiperHeadHolder.pointRootView
								.setVisibility(View.VISIBLE);
						android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
								PixelUtil.dp2px(6), PixelUtil.dp2px(6));
						layoutParams.rightMargin = PixelUtil.dp2px(4);
						List<View> points = new ArrayList<View>();
						for (int i = 0; i < module.getList().size(); i++) {
							View point = new View(mActivity);
							point.setLayoutParams(layoutParams);
							point.setBackgroundResource(R.drawable.point_white);
							mSwiperHeadHolder.pointRootView.addView(point);
							points.add(point);
						}
						points.get(0).setBackgroundResource(
								R.drawable.point_red);
						mSwiperHeadHolder.pointRootView.setTag(points);
					} else {
						mSwiperHeadHolder.pointRootView
								.setVisibility(View.GONE);
					}

					convertView.setTag(mSwiperHeadHolder);
				} else if (itemType == 1) {
					mMatrixHolder = new MatrixHolder();
					convertView = inflate.inflate(mActivity,
							R.layout.item_news_home_matrix, null);
					mMatrixHolder.change = convertView
							.findViewById(R.id.item_news_home_matrix_change);
					mMatrixHolder.changeIcon = (ImageView) convertView
							.findViewById(R.id.item_news_home_matrix_change_icon);
					mMatrixHolder.more = convertView
							.findViewById(R.id.item_news_home_matrix_more);
					mMatrixHolder.icon = (ImageView) convertView
							.findViewById(R.id.item_news_home_matrix_icon);
					mMatrixHolder.title = (TextView) convertView
							.findViewById(R.id.item_news_home_matrix_title);
					mMatrixHolder.rootView[0] = convertView
							.findViewById(R.id.item_news_home_matrix_0);
					mMatrixHolder.rootView[1] = convertView
							.findViewById(R.id.item_news_home_matrix_1);
					mMatrixHolder.rootView[2] = convertView
							.findViewById(R.id.item_news_home_matrix_2);
					mMatrixHolder.rootView[3] = convertView
							.findViewById(R.id.item_news_home_matrix_3);
					RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							(int) ((mActivity.mScreenWidth - PixelUtil
									.dp2px(12.5f * 3)) / 2 * 0.75));
					for (int i = 0; i < 4; i++) {
						ImageView image = (ImageView) mMatrixHolder.rootView[i]
								.findViewById(R.id.item_news_home_matrix_item_image);
						TextView title = (TextView) mMatrixHolder.rootView[i]
								.findViewById(R.id.item_news_home_matrix_item_title);
						image.setLayoutParams(layoutParams);
						FontUtils.setTextViewFontSizeDIP(NewsHomeFragment.this,
								title, R.string.home_news_matrix_text_size, 1);
					}
					convertView.setTag(mMatrixHolder);
				} else if (itemType == 2) {
					mMatrixListHolder = new MatrixListHolder();
					convertView = inflate.inflate(mActivity,
							R.layout.item_news_home_matrix_list, null);
					mMatrixListHolder.more = convertView
							.findViewById(R.id.item_news_home_matrix_list_more);
					mMatrixListHolder.title = (TextView) convertView
							.findViewById(R.id.item_news_home_matrix_list_title);
					mMatrixListHolder.icon = (ImageView) convertView
							.findViewById(R.id.item_news_home_matrix_list_icon);
					mMatrixListHolder.rootView0 = convertView
							.findViewById(R.id.item_news_home_matrix_list_0);
					mMatrixListHolder.img0 = (ImageView) convertView
							.findViewById(R.id.item_news_home_matrix_list_image0);
					mMatrixListHolder.icon0 = (ImageView) convertView
							.findViewById(R.id.item_news_home_matrix_list_icon0);
					RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							(int) ((double) (mActivity.mScreenWidth - PixelUtil
									.dp2px(12.5f * 2)) * 285 / 600));
					mMatrixListHolder.img0.setLayoutParams(layoutParams);
					mMatrixListHolder.title0 = (MyTextView) convertView
							.findViewById(R.id.item_news_home_matrix_list_title0);
					mMatrixListHolder.intro0 = (MyTextView) convertView
							.findViewById(R.id.item_news_home_matrix_list_intro0);
					mMatrixListHolder.rootView1 = convertView
							.findViewById(R.id.item_news_home_matrix_list_1);
					mMatrixListHolder.img1 = (ImageView) convertView
							.findViewById(R.id.item_news_home_matrix_list_image1);
					mMatrixListHolder.icon1 = (ImageView) convertView
							.findViewById(R.id.item_news_home_matrix_list_icon1);
					mMatrixListHolder.title1 = (MyTextView) convertView
							.findViewById(R.id.item_news_home_matrix_list_title1);
					mMatrixListHolder.click1 = (MyTextView) convertView
							.findViewById(R.id.item_news_home_matrix_list_click1);
					mMatrixListHolder.click2 = (MyTextView) convertView
							.findViewById(R.id.item_news_home_matrix_list_click2);
					mMatrixListHolder.rootView2 = convertView
							.findViewById(R.id.item_news_home_matrix_list_2);
					mMatrixListHolder.img2 = (ImageView) convertView
							.findViewById(R.id.item_news_home_matrix_list_image2);
					mMatrixListHolder.icon2 = (ImageView) convertView
							.findViewById(R.id.item_news_home_matrix_list_icon2);
					mMatrixListHolder.title2 = (MyTextView) convertView
							.findViewById(R.id.item_news_home_matrix_list_title2);
					convertView.setTag(mMatrixListHolder);
				} else if (itemType == 3) {
					mGalleryHolder = new GalleryHolder();
					convertView = inflate.inflate(mActivity,
							R.layout.item_news_home_gallery, null);
					mGalleryHolder.title = (TextView) convertView
							.findViewById(R.id.item_news_home_gallery_title_item);
					mGalleryHolder.rootView = (LinearLayout) convertView
							.findViewById(R.id.item_news_home_gallery_root_view);
					convertView.setTag(mGalleryHolder);
				} else if (itemType == 4) {
					mTopicOneHolder = new TopicOneHolder();
					convertView = inflate.inflate(mActivity,
							R.layout.item_news_home_topic_one, null);
					mTopicOneHolder.titlePic = (ImageView) convertView
							.findViewById(R.id.item_news_home_topic_one_image);
					RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							(int) (mActivity.mScreenWidth / 3.2));
					mTopicOneHolder.titlePic.setLayoutParams(layoutParams);
					mTopicOneHolder.title = (TextView) convertView
							.findViewById(R.id.item_news_home_topic_one_title);
					mTopicOneHolder.intro = (TextView) convertView
							.findViewById(R.id.item_news_home_topic_one_intro);
					convertView.setTag(mTopicOneHolder);
				} else if (itemType == 5) {
					mTopicTwoHolder = new TopicTwoHolder();
					convertView = inflate.inflate(mActivity,
							R.layout.item_news_home_topic_two, null);
					mTopicTwoHolder.titlePic0 = (ImageView) convertView
							.findViewById(R.id.item_news_home_topic_two_image0);
					mTopicTwoHolder.titlePic1 = (ImageView) convertView
							.findViewById(R.id.item_news_home_topic_two_image1);
					mTopicTwoHolder.titlePic0.getLayoutParams().height = (int) ((mActivity.mScreenWidth - PixelUtil
							.dp2px(12.5f * 3)) / 2 / 3);
					mTopicTwoHolder.titlePic1.getLayoutParams().height = (int) ((mActivity.mScreenWidth - PixelUtil
							.dp2px(12.5f * 3)) / 2 / 3);
					convertView.setTag(mTopicTwoHolder);
				} else if (itemType == 6) {
					mOutLinkHolder = new OutLinkHolder();
					convertView = inflate.inflate(mActivity,
							R.layout.item_news_home_outlink, null);
					mOutLinkHolder.title = (TextView) convertView
							.findViewById(R.id.item_news_home_outlink_title);
					mOutLinkHolder.keyboardIconContent = (LinearLayout) convertView
							.findViewById(R.id.item_news_home_outlink_keyboard_content);
					mOutLinkHolder.titlePic = (ImageView) convertView
							.findViewById(R.id.item_news_home_outlink_image);
					mOutLinkHolder.titlePic.getLayoutParams().height = (int) ((double) (mActivity.mScreenWidth - PixelUtil
							.dp2px(12.5f * 2)) / 600 * 285);
					convertView.setTag(mOutLinkHolder);
				} else if (itemType == 7) {
					mVoteHolder = new VoteHolder();
					convertView = inflate.inflate(mActivity,
							R.layout.item_news_home_vote, null);
					mVoteHolder.title = (TextView) convertView
							.findViewById(R.id.item_news_home_vote_title);
					mVoteHolder.change = convertView
							.findViewById(R.id.item_news_home_vote_change);
					mVoteHolder.changeIcon = convertView
							.findViewById(R.id.item_news_home_vote_change_icon);
					mVoteHolder.quetions = (TextView) convertView
							.findViewById(R.id.item_news_home_vote_question);
					mVoteHolder.rootView = (LinearLayout) convertView
							.findViewById(R.id.item_news_home_vote_options_root_view);
					mVoteHolder.icon = (ImageView) convertView
							.findViewById(R.id.item_news_home_vote_icon);
					convertView.setTag(mVoteHolder);
				}
			} else {
				if (itemType == 0) {
					mSwiperHeadHolder = (SwiperHeadHolder) convertView.getTag();
				} else if (itemType == 1) {
					mMatrixHolder = (MatrixHolder) convertView.getTag();
				} else if (itemType == 2) {
					mMatrixListHolder = (MatrixListHolder) convertView.getTag();
				} else if (itemType == 3) {
					mGalleryHolder = (GalleryHolder) convertView.getTag();
				} else if (itemType == 4) {
					mTopicOneHolder = (TopicOneHolder) convertView.getTag();
				} else if (itemType == 5) {
					mTopicTwoHolder = (TopicTwoHolder) convertView.getTag();
				} else if (itemType == 6) {
					mOutLinkHolder = (OutLinkHolder) convertView.getTag();
				} else if (itemType == 7) {
					mVoteHolder = (VoteHolder) convertView.getTag();
				}
			}
			if (itemType == 0) {
				// TODO
				final NewsHomeModuleItem moduleItem = module.getList().get(
						mSwiperHeadHolder.imgViewPager.getCurrentItem()
								% module.getList().size());
				mSwiperHeadHolder.title.setText(moduleItem.getTitle());
				mSwiperHeadHolder.title.setTag(module.getList());
			} else if (itemType == 1) {
				if (module.getChange() == 1) {
					mMatrixHolder.change.setVisibility(View.VISIBLE);
					mMatrixHolder.change.setTag(mMatrixHolder.changeIcon);
					mMatrixHolder.change
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									final ImageView changeIcon = (ImageView) v
											.getTag();
									if (CommonUtils
											.isNetworkAvailable(mActivity)) {
										int num = 1;
										String time = "";
										if (mRandomNumMap.get(module
												.getAppclassid()) == null) {
											mRandomNumMap.put(
													module.getAppclassid(), num);
										} else {
											num = mRandomNumMap.get(module
													.getAppclassid());
										}
										Animation operatingAnim = AnimationUtils
												.loadAnimation(mActivity,
														R.anim.rotate_self);
										operatingAnim.setDuration(500);
										LinearInterpolator lin = new LinearInterpolator();
										operatingAnim.setInterpolator(lin);
										changeIcon
												.startAnimation(operatingAnim);
										// if (num > 10) {
										// num = 0;
										// } else {
										time = module.getList().get(3)
												.getNewstime();
										// }
										mRandomNumMap.put(
												module.getAppclassid(), num + 1);
										netUtils.getNewHomeChange(
												module.getAppclassid(), time,
												num,
												new Listener<JSONObject>() {
													@Override
													public void onResponse(
															JSONObject jsonObject) {
														changeIcon
																.clearAnimation();
														if (jsonObject != null
																&& !jsonObject
																		.toString()
																		.trim()
																		.equals("")) {
															NewsHomeModule newModule = JsonUtils.toObject(
																	jsonObject
																			.toString(),
																	NewsHomeModule.class);
															if (newModule
																	.getList()
																	.size() != 4)
																return;
															mNewsHome
																	.getModule_list()
																	.set(position - 1,
																			newModule);
															mNewsHomeListAdapter
																	.notifyDataSetChanged();
														}
													}
												}, new ErrorListener() {
													@Override
													public void onErrorResponse(
															VolleyError error) {
														changeIcon
																.clearAnimation();
														ToastUtils.Errortoast(
																mActivity,
																"请求失败,请重试");
													}
												});
									}
								}
							});
				} else {
					mMatrixHolder.change.setVisibility(View.GONE);
				}
				mMatrixHolder.title.setText(module.getTitle());
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(module.getIcon()),
						mMatrixHolder.icon, ImgUtils.homeImageOptions);
				for (int i = 0; i < 4; i++) {
					ImageView image = (ImageView) mMatrixHolder.rootView[i]
							.findViewById(R.id.item_news_home_matrix_item_image);
					ImageView icon = (ImageView) mMatrixHolder.rootView[i]
							.findViewById(R.id.item_news_home_matrix_item_icon);
					TextView title = (TextView) mMatrixHolder.rootView[i]
							.findViewById(R.id.item_news_home_matrix_item_title);
					ImgUtils.imageLoader.displayImage(
							CommonUtils.doWebpUrl(module.getList().get(i)
									.getTitlepic()), image,
							ImgUtils.homeImageOptions);
					title.setText(module.getList().get(i).getTitle());
					if ("video".equals(module.getList().get(i).getType())) {
						icon.setVisibility(View.VISIBLE);
					} else {
						icon.setVisibility(View.GONE);
					}
					mMatrixHolder.rootView[i].setTag(module.getList().get(i));
					mMatrixHolder.rootView[i]
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									openNews((NewsHomeModuleItem) v.getTag());
								}
							});
				}
				mMatrixHolder.more.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// NewsHomeFragment.this.startAnimActivityByAppClassId(
						// NewsListActivity.class, module.getAppclassid());
						NewsHomeFragment.this
								.startAnimActivityByNewsHomeModule(
										NewsListActivity.class, module);
					}
				});
			} else if (itemType == 2) {
				if (module.getList().get(0).getType().trim().equals("video")) {
					mMatrixListHolder.icon0.setVisibility(View.VISIBLE);
				} else {
					mMatrixListHolder.icon0.setVisibility(View.GONE);
				}
				if (module.getList().get(1).getType().trim().equals("video")) {
					mMatrixListHolder.icon1.setVisibility(View.VISIBLE);
				} else {
					mMatrixListHolder.icon1.setVisibility(View.GONE);
				}
				if (module.getList().get(2).getType().trim().equals("video")) {
					mMatrixListHolder.icon2.setVisibility(View.VISIBLE);
				} else {
					mMatrixListHolder.icon2.setVisibility(View.GONE);
				}
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(module.getIcon()),
						mMatrixListHolder.icon, ImgUtils.homeImageOptions);
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(module.getList().get(0)
								.getTitlepic()), mMatrixListHolder.img0,
						ImgUtils.homeImageOptions);
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(module.getList().get(1)
								.getTitlepic()), mMatrixListHolder.img1,
						ImgUtils.homeImageOptions);
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(module.getList().get(2)
								.getTitlepic()), mMatrixListHolder.img2,
						ImgUtils.homeImageOptions);
				mMatrixListHolder.title.setText(module.getTitle());
				if (!module.getList().get(0).getIntro().trim().equals("")) {
					mMatrixListHolder.intro0.setVisibility(View.VISIBLE);
					mMatrixListHolder.intro0.setText(module.getList().get(0)
							.getIntro());
				} else {
					mMatrixListHolder.intro0.setVisibility(View.GONE);
				}
				mMatrixListHolder.title0.setText(module.getList().get(0)
						.getTitle());
				mMatrixListHolder.title1.setText(module.getList().get(1)
						.getTitle());
				mMatrixListHolder.title2.setText(module.getList().get(2)
						.getTitle());
				FontUtils.setTextViewFontSizeDIP(NewsHomeFragment.this,
						mMatrixListHolder.title0, R.string.home_news_text_size,
						1);
				FontUtils.setTextViewFontSizeDIP(NewsHomeFragment.this,
						mMatrixListHolder.intro0,
						R.string.home_news_intro_size, 1);
				FontUtils.setTextViewFontSizeDIP(NewsHomeFragment.this,
						mMatrixListHolder.title1, R.string.home_news_text_size,
						1);
				FontUtils.setTextViewFontSizeDIP(NewsHomeFragment.this,
						mMatrixListHolder.title2, R.string.home_news_text_size,
						1);
				mMatrixListHolder.click1.setText(module.getList().get(1)
						.getOnclick()
						+ "");
				mMatrixListHolder.click2.setText(module.getList().get(2)
						.getOnclick()
						+ "");
				mMatrixListHolder.rootView0
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								openNews(module.getList().get(0));
							}
						});
				mMatrixListHolder.rootView1
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								openNews(module.getList().get(1));
							}
						});
				mMatrixListHolder.rootView2
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								openNews(module.getList().get(2));
							}
						});
				mMatrixListHolder.more
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// NewsHomeFragment.this
								// .startAnimActivityByAppClassId(
								// NewsListActivity.class,
								// module.getAppclassid());

								NewsHomeFragment.this
										.startAnimActivityByNewsHomeModule(
												NewsListActivity.class, module);
							}
						});
			} else if (itemType == 3) {
				mGalleryHolder.title.setText("#" + module.getTitle() + "#");
				if (module != (NewsHomeModule) mGalleryHolder.rootView.getTag()) {
					mGalleryHolder.rootView.removeAllViews();
					for (int i = 0; i < module.getList().size(); i++) {
						View itemView = inflate.inflate(mActivity,
								R.layout.item_news_home_image_title_item, null);
						View imageRootView = itemView
								.findViewById(R.id.image_root_view);
						if (i == 0)
							((LinearLayout.LayoutParams) imageRootView
									.getLayoutParams()).leftMargin = PixelUtil
									.dp2px(12.5f);
						((LinearLayout.LayoutParams) imageRootView
								.getLayoutParams()).rightMargin = PixelUtil
								.dp2px(12.5f);
						final NewsHomeModuleItem moduleItem = module.getList()
								.get(i);
						moduleItem.setAppclassid(module.getAppclassid());
						ImageView image = (ImageView) itemView
								.findViewById(R.id.image_item);
						int width = (mActivity.mScreenWidth - PixelUtil
								.dp2px(12.5f * 3)) / 7 * 3;
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
								width, (int) (width * 0.75));
						image.setLayoutParams(layoutParams);
						ImgUtils.imageLoader
								.displayImage(CommonUtils.doWebpUrl(moduleItem
										.getTitlepic()), image,
										ImgUtils.homeImageOptions);
						LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						if (i == 0)
							textLayoutParams.leftMargin = PixelUtil
									.dp2px(12.5f);
						textLayoutParams.rightMargin = PixelUtil.dp2px(12.5f);
						MyTextView title = (MyTextView) itemView
								.findViewById(R.id.title_item);
						title.setText(moduleItem.getTitle());
						title.setLayoutParams(textLayoutParams);
						title.setLineSpacing(PixelUtil.dp2px(3), 1);
						itemView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// openNews(moduleItem);
								NewsHomeFragment.this
										.startAnimActivityByNewsHomeModuleItem(
												NewsVideoPackageActivity.class,
												moduleItem);
							}
						});
						mGalleryHolder.rootView.addView(itemView);
					}
				}
				mGalleryHolder.rootView.setTag(module);
			} else if (itemType == 4) {
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(module.getTitlepic()),
						mTopicOneHolder.titlePic, ImgUtils.homeImageOptions);
				FontUtils.setTextViewFontSizeDIP(NewsHomeFragment.this,
						mTopicOneHolder.title, R.string.home_news_text_size, 1);
				FontUtils
						.setTextViewFontSizeDIP(NewsHomeFragment.this,
								mTopicOneHolder.intro,
								R.string.home_news_intro_size, 1);
				mTopicOneHolder.title.setText(module.getTitle());
				mTopicOneHolder.intro.setText(module.getIntro());
				final NewsHomeModuleItem tmp = new NewsHomeModuleItem();
				tmp.setAppclassid(module.getAppclassid());
				tmp.setTitle(module.getTitle());
				tmp.setTitlepic(module.getTitlepic());
				tmp.setIntro(module.getIntro());
				tmp.setNum(module.getNum());
				tmp.setType(module.getType());
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						openNews(tmp);
					}
				});
			} else if (itemType == 5) {
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(module.getList().get(0)
								.getTitlepic()), mTopicTwoHolder.titlePic0,
						ImgUtils.homeImageOptions);
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(module.getList().get(1)
								.getTitlepic()), mTopicTwoHolder.titlePic1,
						ImgUtils.homeImageOptions);
				mTopicTwoHolder.titlePic0
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (module.getList().get(0).getNum() > 0)
									NewsHomeFragment.this
											.startAnimActivityByNewsHomeModuleItem(
													NewsTopicActivity.class,
													module.getList().get(0));
								else
									NewsHomeFragment.this
											.startAnimActivityByNewsHomeModuleItem(
													NewsTopicListActivity.class,
													module.getList().get(0));
							}
						});
				mTopicTwoHolder.titlePic1
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (module.getList().get(1).getNum() > 0)
									NewsHomeFragment.this
											.startAnimActivityByNewsHomeModuleItem(
													NewsTopicActivity.class,
													module.getList().get(1));
								else
									NewsHomeFragment.this
											.startAnimActivityByNewsHomeModuleItem(
													NewsTopicListActivity.class,
													module.getList().get(1));
							}
						});
			} else if (itemType == 6) {
				mOutLinkHolder.title
						.setText(module.getList().get(0).getTitle());
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(module.getList().get(0)
								.getTitlepic()), mOutLinkHolder.titlePic,
						ImgUtils.homeImageOptions);
				mOutLinkHolder.keyboardIconContent.setVisibility(View.VISIBLE);
				Keyboard mKeyboard = module.getList().get(0).getKeyboard();
				mKeyboard.setColor("#ffab40");
				mKeyboard.setText("推广");
				if (mKeyboard != null
						&& !mKeyboard.getColor().trim().equals("")
						&& !mKeyboard.getText().trim().equals("")) {
					mOutLinkHolder.keyboardIconContent.removeAllViews();
					TextView view = new BorderTextView(mActivity,
							mKeyboard.getColor());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					view.setLayoutParams(params);
					view.setGravity(Gravity.CENTER);
					int px3 = PixelUtil.dp2px(3);
					view.setPadding(px3, px3, px3, px3);
					view.setText(mKeyboard.getText());
					FontUtils.setTextViewFontSizeDIP(NewsHomeFragment.this,
							view, R.string.live_border_text_view_text_size, 1f);
					view.setTextColor(Color.parseColor(mKeyboard.getColor()));
					mOutLinkHolder.keyboardIconContent.addView(view);
				}
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						openNews(module.getList().get(0));
					}
				});
			} else if (itemType == 7) {
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(module.getIcon()),
						mVoteHolder.icon, ImgUtils.homeImageOptions);
				mVoteHolder.title.setText(module.getTitle());
				mVoteHolder.quetions.setText(module.getVote());
				initVoteView(module);
				mVoteHolder.change.setTag(mVoteHolder.changeIcon);
				mVoteHolder.change.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final ImageView changeIcon = (ImageView) v.getTag();
						if (CommonUtils.isNetworkAvailable(mActivity)) {
							Animation operatingAnim = AnimationUtils
									.loadAnimation(mActivity,
											R.anim.rotate_self);
							operatingAnim.setDuration(500);
							LinearInterpolator lin = new LinearInterpolator();
							operatingAnim.setInterpolator(lin);
							changeIcon.startAnimation(operatingAnim);
							netUtils.getNewHomeVoteChange(
									module.getAppclassid(), module.getId(),
									new Listener<JSONObject>() {
										@Override
										public void onResponse(
												JSONObject jsonObject) {
											changeIcon.clearAnimation();
											if (jsonObject != null
													&& !jsonObject.toString()
															.trim().equals("")) {
												NewsHomeModule newModule = JsonUtils.toObject(
														jsonObject.toString(),
														NewsHomeModule.class);
												mNewsHome
														.getModule_list()
														.get(position - 1)
														.setList(
																newModule
																		.getList());
												mNewsHome
														.getModule_list()
														.get(position - 1)
														.setId(newModule
																.getId());
												mNewsHome
														.getModule_list()
														.get(position - 1)
														.setVote(
																newModule
																		.getVote());
												mNewsHomeListAdapter
														.notifyDataSetChanged();
											}
										}
									}, new ErrorListener() {
										@Override
										public void onErrorResponse(
												VolleyError error) {
											changeIcon.clearAnimation();
											ToastUtils.Errortoast(mActivity,
													"请求失败,请重试");
										}
									});
						}
					}
				});
			}
			return convertView;
		}
	}

	private void initVoteView(NewsHomeModule module) {
		// TODO
		mVoteHolder.rootView.removeAllViews();
		if (spUtil.judgeVoteId(module.getId())) {
			initVoteHasVote(module);
		} else {
			initVoteNoVote(module);
		}
	}

	public void initVoteHasVote(final NewsHomeModule module) {
		int voteSumNum = 0;
		BigDecimal tmpVoteSumNum = new BigDecimal(0);
		int _flag = 0;
		int maxLength = (int) (mActivity.mScreenWidth * 0.7);
		for (int i = module.getList().size() - 1; i >= 0; i--) {
			int num = module.getList().get(i).getNum();
			voteSumNum += num;
			if (num != 0 && _flag == 0)
				_flag = i;
		}
		for (int i = 0; i < module.getList().size(); i++) {
			if (i >= VOTE_ANSWER_PREFIX.length)
				break;
			final NewsHomeModuleItem item = module.getList().get(i);
			View itemView = View.inflate(mActivity,
					R.layout.item_news_home_vote_item, null);
			TextView answer = (TextView) itemView
					.findViewById(R.id.vote_answer);
			answer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			answer.setText(VOTE_ANSWER_PREFIX[i] + item.getOption());
			View answerLoading = itemView
					.findViewById(R.id.vote_answer_loading);
			double percent = (double) item.getNum() / voteSumNum;
			if (maxLength * percent > PixelUtil.dp2px(10))
				answerLoading.getLayoutParams().width = (int) (maxLength * percent);
			else
				answerLoading.getLayoutParams().width = PixelUtil.dp2px(10);
			((GradientDrawable) answerLoading.getBackground()).setColor(Color
					.parseColor(VOTE_ANSWER_COLOR[i]));
			TextView answerPercent = (TextView) itemView
					.findViewById(R.id.vote_answer_percent);
			BigDecimal tmpPercent = new BigDecimal(
					(double) Math.round(percent * 10000) / 100 + "");
			tmpVoteSumNum = tmpVoteSumNum.add(tmpPercent);
			if (i != _flag) {
				answerPercent.setText(mVoteNumDF.format(tmpPercent).toString()
						+ "%");
			} else {
				if (!tmpVoteSumNum.toString().equals("100.00")
						|| !tmpVoteSumNum.toString().equals("100.0")
						|| !tmpVoteSumNum.toString().equals("100")) {
					answerPercent.setText(tmpPercent.add(
							new BigDecimal(new BigDecimal("100").subtract(
									new BigDecimal(tmpVoteSumNum.toString()))
									.toString())).toString()
							+ "%");
				} else
					answerPercent.setText(mVoteNumDF.format(tmpPercent) + "%");
			}
			mVoteHolder.rootView.addView(itemView);
		}
	}

	public void initVoteNoVote(final NewsHomeModule module) {
		for (int i = 0; i < module.getList().size(); i++) {
			if (i >= VOTE_ANSWER_PREFIX.length)
				break;
			final NewsHomeModuleItem item = module.getList().get(i);
			String option = item.getOption();
			TextView optionView = new MyTextView(mActivity);
			LinearLayout.LayoutParams optionParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			optionParams.leftMargin = PixelUtil.dp2px(12.5f);
			optionParams.rightMargin = PixelUtil.dp2px(12.5f);
			optionParams.topMargin = PixelUtil.dp2px(5f);
			optionParams.bottomMargin = PixelUtil.dp2px(5f);
			optionView.setText(VOTE_ANSWER_PREFIX[i] + option);
			optionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			optionView.setLayoutParams(optionParams);
			optionView
					.setBackgroundResource(R.drawable.bg_item_news_home_vote_answer);
			optionView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					netUtils.putVoteAnswer(module.getAppclassid(),
							module.getId(), item.getId(),
							new Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject jsonObject) {
									Map voteMap = JsonUtils.toMap(jsonObject
											.toString());
									spUtil.addVoteId(module.getId());
									item.setNum(Integer.parseInt(voteMap.get(
											"num").toString()));
									mNewsHomeListAdapter.notifyDataSetChanged();
								}
							}, new ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError arg0) {
									ToastUtils
											.Errortoast(mActivity, "请求失败,请重试");
								}
							});
				}
			});
			mVoteHolder.rootView.addView(optionView);
		}
	}

	public class NewsHomeSwiperHeadAdapter extends RecyclingPagerAdapter {
		private List<NewsHomeModuleItem> itemList;

		public void setItemList(List<NewsHomeModuleItem> itemList) {
			this.itemList = itemList;
		}

		private int mChildCount = 0;

		public NewsHomeSwiperHeadAdapter(List<NewsHomeModuleItem> itemList) {
			super();
			this.itemList = itemList;
		}

		@Override
		public void notifyDataSetChanged() {
			mChildCount = getCount();
			super.notifyDataSetChanged();
		}

		@Override
		public int getItemPosition(Object object) {
			if (mChildCount > 0) {
				// mChildCount--;
				return POSITION_NONE;
			}
			return super.getItemPosition(object);
		}

		@Override
		public int getCount() {
			// return itemList.size();
			return itemList.size() == 1 ? 1 : Integer.MAX_VALUE;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup container) {
			final int index = position % itemList.size();
			if (convertView == null) {
				ImageView imageView = new ImageView(
						NewsHomeFragment.this.mActivity);
				imageView.setScaleType(ScaleType.FIT_XY);
				convertView = imageView;
			}
			ImgUtils.imageLoader.displayImage(
					CommonUtils.doWebpUrl(itemList.get(index).getTitlepic()),
					(ImageView) convertView, ImgUtils.homeImageOptions);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					openNews(itemList.get(index));
				}
			});
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
		// TODO
		if (mSwiperHeadHolder != null) {
			List<NewsHomeModuleItem> itemList = (List<NewsHomeModuleItem>) mSwiperHeadHolder.title
					.getTag();
			int index = arg0 % itemList.size();
			mSwiperHeadHolder.title.setText(itemList.get(index).getTitle());
			List<View> points = (List<View>) mSwiperHeadHolder.pointRootView
					.getTag();
			for (View v : points) {
				v.setBackgroundResource(R.drawable.point_white);
			}
			points.get(index).setBackgroundResource(R.drawable.point_red);
		}
	}

	private void openNews(NewsHomeModuleItem moduleItem) {
		if (moduleItem.getType().equals("video")
				|| moduleItem.getType().equals("text")) {
			this.startAnimActivityByNewsHomeModuleItem(
					NewsContentActivity.class, moduleItem);
		} else if (moduleItem.getType().equals("outlink")) {
			moduleItem.setOutLinkType("module");
			this.startAnimActivityByNewsHomeModuleItem(
					NewsOutLinkActivity.class, moduleItem);
		} else if (moduleItem.getType().equals("album")) {
			this.startAnimActivityByNewsHomeModuleItem(NewsAlbumActivity.class,
					moduleItem);
		} else if (moduleItem.getType().equals("stream")) {
			mActivity.touchTab(mActivity.tabLive);
		} else if (moduleItem.getType().equals("topic")) {
			if (moduleItem.getNum() > 0)
				this.startAnimActivityByNewsHomeModuleItem(
						NewsTopicActivity.class, moduleItem);
			else
				NewsHomeFragment.this.startAnimActivityByNewsHomeModuleItem(
						NewsTopicListActivity.class, moduleItem);
		}
	}

	private boolean judgeObject() {
		if (mNewsHome.getModule_list().get(0).getList().size() < 1)
			return false;
		if (mNewsHome.getModule_list().size() > 1) {
			for (int i = 1; i < mNewsHome.getModule_list().size(); i++) {
				NewsHomeModule module = mNewsHome.getModule_list().get(i);
				if (module.getType().equals("matrix")) {
					if (module.getNum() != 4 && module.getNum() != 3)
						return false;
				}
				if (module.getType().equals("swiper-video")) {
					if (module.getList().size() < 1)
						return false;
				}
				if (module.getType().equals("topicpackage")) {
					if (module.getList().size() < 2)
						return false;
				}
			}
		}
		return true;
	}
}
