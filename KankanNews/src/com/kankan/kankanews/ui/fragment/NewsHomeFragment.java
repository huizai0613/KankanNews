package com.kankan.kankanews.ui.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
import com.kankan.kankanews.bean.NewsHome;
import com.kankan.kankanews.bean.NewsHomeModule;
import com.kankan.kankanews.bean.NewsHomeModuleItem;
import com.kankan.kankanews.bean.SerializableObj;
import com.kankan.kankanews.ui.ColumsActivity;
import com.kankan.kankanews.ui.MeSetActivity;
import com.kankan.kankanews.ui.NewsListActivity;
import com.kankan.kankanews.ui.SearchMainActivity;
import com.kankan.kankanews.ui.item.NewsContentActivity;
import com.kankan.kankanews.ui.item.NewsOutLinkActivity;
import com.kankan.kankanews.ui.item.NewsVideoPackageActivity;
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
	private View mLoadingView;
	private View mRetryView;
	private View mGoColumsBut;
	private View mGoMeSetBut;
	private PullToRefreshListView mNewsHomeListView;

	private String mNewsHomeListJson;
	private NewsHome mNewsHome;

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
		if (CommonUtils.isNetworkAvailable(this.mActivity)) {
			refreshNetDate();
		} else {
			if (hasLocal) {

			} else {
				this.mLoadingView.setVisibility(View.GONE);
				this.mRetryView.setVisibility(View.VISIBLE);
			}
		}
		netUtils.getNewsHomeList(this.mListenerObject, this.mErrorListener);
	}

	private void initLinsenter() {
		mRetryView.setOnClickListener(this);
		mGoColumsBut.setOnClickListener(this);
		mGoMeSetBut.setOnClickListener(this);
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
			refreshNetDate();
			break;
		}
	}

	private void showData() {
		this.mLoadingView.setVisibility(View.GONE);
		if (mNewsHomeListAdapter == null) {
			mNewsHomeListAdapter = new NewsHomeListAdapter();
			mNewsHomeListView.setAdapter(mNewsHomeListAdapter);
		} else {
			mNewsHomeListAdapter.notifyDataSetChanged();
		}
		mNewsHomeListView.setSelection(2);
	}

	@Override
	public void refresh() {
		super.refresh();
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
					.toString(), mNewsHomeListJson, "NewsHome");
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
			this.mNewsHomeListJson = jsonObject.toString();
			mNewsHome = JsonUtils.toObject(this.mNewsHomeListJson,
					NewsHome.class);
			saveLocalDate();
			showData();
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
		TextView title0;
		TextView intro0;
		View rootView1;
		ImageView img1;
		TextView title1;
		TextView click1;
		View rootView2;
		ImageView img2;
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
	}

	private class VoteHolder {
		ImageView icon;
		TextView title;
		View change;
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
					FontUtils.setTextViewFontSize(NewsHomeFragment.this,
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
					mSwiperHeadHolder.imgViewPager
							.setAdapter(new NewsHomeSwiperHeadAdapter(module
									.getList()));
					mSwiperHeadHolder.imgViewPager.setCurrentItem(0);
					mSwiperHeadHolder.pointRootView = (LinearLayout) convertView
							.findViewById(R.id.news_home_swiper_head_point_root_view);
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
					points.get(0).setBackgroundResource(R.drawable.point_red);
					mSwiperHeadHolder.pointRootView.setTag(points);
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
						FontUtils.setTextViewFontSize(NewsHomeFragment.this,
								title, R.string.home_news_text_size, 1);
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
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							(int) ((mActivity.mScreenWidth - PixelUtil
									.dp2px(12.5f * 2)) * 0.5));
					mMatrixListHolder.img0.setLayoutParams(layoutParams);
					mMatrixListHolder.title0 = (MyTextView) convertView
							.findViewById(R.id.item_news_home_matrix_list_title0);
					mMatrixListHolder.intro0 = (MyTextView) convertView
							.findViewById(R.id.item_news_home_matrix_list_intro0);
					mMatrixListHolder.rootView1 = convertView
							.findViewById(R.id.item_news_home_matrix_list_1);
					mMatrixListHolder.img1 = (ImageView) convertView
							.findViewById(R.id.item_news_home_matrix_list_image1);
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
					mOutLinkHolder.titlePic = (ImageView) convertView
							.findViewById(R.id.item_news_home_outlink_image);
					mOutLinkHolder.titlePic.getLayoutParams().height = (int) ((mActivity.mScreenWidth - PixelUtil
							.dp2px(12.5f * 2)) / 2.4);
					convertView.setTag(mOutLinkHolder);
				} else if (itemType == 7) {
					mVoteHolder = new VoteHolder();
					convertView = inflate.inflate(mActivity,
							R.layout.item_news_home_vote, null);
					mVoteHolder.title = (TextView) convertView
							.findViewById(R.id.item_news_home_vote_title);
					mVoteHolder.change = convertView
							.findViewById(R.id.item_news_home_vote_change);
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
						mSwiperHeadHolder.imgViewPager.getCurrentItem());
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
										Animation operatingAnim = AnimationUtils
												.loadAnimation(mActivity,
														R.anim.rotate_self);
										operatingAnim.setDuration(500);
										LinearInterpolator lin = new LinearInterpolator();
										operatingAnim.setInterpolator(lin);
										changeIcon
												.startAnimation(operatingAnim);
										netUtils.getNewHomeChange(
												module.getAppclassid(),
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
				ImgUtils.imageLoader.displayImage(module.getIcon(),
						mMatrixHolder.icon, ImgUtils.homeImageOptions);
				for (int i = 0; i < 4; i++) {
					ImageView image = (ImageView) mMatrixHolder.rootView[i]
							.findViewById(R.id.item_news_home_matrix_item_image);
					ImageView icon = (ImageView) mMatrixHolder.rootView[i]
							.findViewById(R.id.item_news_home_matrix_item_icon);
					TextView title = (TextView) mMatrixHolder.rootView[i]
							.findViewById(R.id.item_news_home_matrix_item_title);
					ImgUtils.imageLoader.displayImage(module.getList().get(i)
							.getTitlepic(), image, ImgUtils.homeImageOptions);
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
						NewsHomeFragment.this.startAnimActivityByAppClassId(
								NewsListActivity.class, module.getAppclassid());
					}
				});
			} else if (itemType == 2) {
				ImgUtils.imageLoader.displayImage(module.getIcon(),
						mMatrixListHolder.icon, ImgUtils.homeImageOptions);
				ImgUtils.imageLoader.displayImage(module.getList().get(0)
						.getTitlepic(), mMatrixListHolder.img0,
						ImgUtils.homeImageOptions);
				ImgUtils.imageLoader.displayImage(module.getList().get(1)
						.getTitlepic(), mMatrixListHolder.img1,
						ImgUtils.homeImageOptions);
				ImgUtils.imageLoader.displayImage(module.getList().get(2)
						.getTitlepic(), mMatrixListHolder.img2,
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
				FontUtils.setTextViewFontSize(NewsHomeFragment.this,
						mMatrixListHolder.title0, R.string.home_news_text_size,
						1);
				FontUtils.setTextViewFontSize(NewsHomeFragment.this,
						mMatrixListHolder.intro0,
						R.string.home_news_intro_size, 1);
				FontUtils.setTextViewFontSize(NewsHomeFragment.this,
						mMatrixListHolder.title1, R.string.home_news_text_size,
						1);
				FontUtils.setTextViewFontSize(NewsHomeFragment.this,
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
								NewsHomeFragment.this
										.startAnimActivityByAppClassId(
												NewsListActivity.class,
												module.getAppclassid());
							}
						});
			} else if (itemType == 3) {
				mGalleryHolder.title.setText("#" + module.getTitle() + "#");
				if (module != (NewsHomeModule) mGalleryHolder.rootView.getTag()) {
					mGalleryHolder.rootView.removeAllViews();
					for (int i = 0; i < module.getList().size(); i++) {
						View itemView = inflate.inflate(mActivity,
								R.layout.item_news_home_image_title_item, null);
						final NewsHomeModuleItem moduleItem = module.getList()
								.get(i);
						ImageView image = (ImageView) itemView
								.findViewById(R.id.image_item);
						int width = (mActivity.mScreenWidth - PixelUtil
								.dp2px(12.5f * 3)) / 7 * 3;
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
								width, (int) (width * 0.75));
						if (i == 0)
							layoutParams.leftMargin = PixelUtil.dp2px(12.5f);
						layoutParams.rightMargin = PixelUtil.dp2px(12.5f);
						image.setLayoutParams(layoutParams);
						ImgUtils.imageLoader.displayImage(
								moduleItem.getTitlepic(), image,
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
										.startAnimActivityByAppClassId(
												NewsVideoPackageActivity.class,
												module.getAppclassid());
							}
						});
						mGalleryHolder.rootView.addView(itemView);
					}
				}
				mGalleryHolder.rootView.setTag(module);
			} else if (itemType == 4) {
				ImgUtils.imageLoader.displayImage(module.getTitlepic(),
						mTopicOneHolder.titlePic, ImgUtils.homeImageOptions);
				FontUtils.setTextViewFontSize(NewsHomeFragment.this,
						mTopicOneHolder.title, R.string.home_news_text_size, 1);
				FontUtils
						.setTextViewFontSize(NewsHomeFragment.this,
								mTopicOneHolder.intro,
								R.string.home_news_intro_size, 1);
				mTopicOneHolder.title.setText(module.getTitle());
				mTopicOneHolder.intro.setText(module.getIntro());
			} else if (itemType == 5) {
				ImgUtils.imageLoader.displayImage(module.getList().get(0)
						.getTitlepic(), mTopicTwoHolder.titlePic0,
						ImgUtils.homeImageOptions);
				ImgUtils.imageLoader.displayImage(module.getList().get(1)
						.getTitlepic(), mTopicTwoHolder.titlePic1,
						ImgUtils.homeImageOptions);
			} else if (itemType == 6) {
				mOutLinkHolder.title
						.setText(module.getList().get(0).getTitle());
				ImgUtils.imageLoader.displayImage(module.getList().get(0)
						.getTitlepic(), mOutLinkHolder.titlePic,
						ImgUtils.homeImageOptions);
				mOutLinkHolder.titlePic
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								openNews(module.getList().get(0));
							}
						});
			} else if (itemType == 7) {
				ImgUtils.imageLoader.displayImage(module.getIcon(),
						mVoteHolder.icon, ImgUtils.homeImageOptions);
				mVoteHolder.title.setText(module.getTitle());
				mVoteHolder.quetions.setText(module.getVote());
				initVoteView(module);
			}
			return convertView;
		}
	}

	private void initVoteView(NewsHomeModule module) {
		mVoteHolder.rootView.removeAllViews();
		for (int i = 0; i < module.getList().size(); i++) {
			NewsHomeModuleItem item = module.getList().get(i);
			String option = item.getOption();
			TextView optionView = new MyTextView(mActivity);
			LinearLayout.LayoutParams optionParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			optionParams.leftMargin = PixelUtil.dp2px(12.5f);
			optionParams.rightMargin = PixelUtil.dp2px(12.5f);
			optionView.setText(option);
			optionView.setLayoutParams(optionParams);
			mVoteHolder.rootView.addView(optionView);
		}
	}

	public class NewsHomeSwiperHeadAdapter extends RecyclingPagerAdapter {
		private List<NewsHomeModuleItem> itemList;

		public NewsHomeSwiperHeadAdapter(List<NewsHomeModuleItem> itemList) {
			super();
			this.itemList = itemList;
		}

		@Override
		public int getCount() {
			return itemList.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup container) {
			if (convertView == null) {
				ImageView imageView = new ImageView(
						NewsHomeFragment.this.mActivity);
				imageView.setScaleType(ScaleType.FIT_XY);
				convertView = imageView;
			}
			ImgUtils.imageLoader.displayImage(itemList.get(position)
					.getTitlepic(), (ImageView) convertView,
					ImgUtils.homeImageOptions);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					openNews(itemList.get(position));
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
		if (mSwiperHeadHolder != null) {
			List<NewsHomeModuleItem> itemList = (List<NewsHomeModuleItem>) mSwiperHeadHolder.title
					.getTag();
			mSwiperHeadHolder.title.setText(itemList.get(arg0).getTitle());
			List<View> points = (List<View>) mSwiperHeadHolder.pointRootView
					.getTag();
			for (View v : points) {
				v.setBackgroundResource(R.drawable.point_white);
			}
			points.get(arg0 % itemList.size()).setBackgroundResource(
					R.drawable.point_red);
		}
	}

	private void openNews(NewsHomeModuleItem moduleItem) {
		if (moduleItem.getType().equals("video")
				|| moduleItem.getType().equals("text")) {
			this.startAnimActivityByNewsHomeModuleItem(
					NewsContentActivity.class, moduleItem);
		} else if (moduleItem.getType().equals("outlink")) {
			this.startAnimActivityByNewsHomeModuleItem(
					NewsOutLinkActivity.class, moduleItem);
		} else if (moduleItem.getType().equals("album")) {
			// TODO
		} else if (moduleItem.getType().equals("stream")) {
			mActivity.touchTab(mActivity.tabLive);
		} else if (moduleItem.getType().equals("topic")) {
			// TODO
		}
	}
}
