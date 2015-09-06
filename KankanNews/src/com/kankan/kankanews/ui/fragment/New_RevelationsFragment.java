package com.kankan.kankanews.ui.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.Keyboard;
import com.kankan.kankanews.bean.RevelationsBreaknews;
import com.kankan.kankanews.bean.RevelationsHomeList;
import com.kankan.kankanews.bean.RevelationsNew;
import com.kankan.kankanews.bean.SerializableObj;
import com.kankan.kankanews.ui.MainActivity;
import com.kankan.kankanews.ui.PhotoViewActivity;
import com.kankan.kankanews.ui.RevelationsActivityDetailActivity;
import com.kankan.kankanews.ui.RevelationsBreakNewsMoreActivity;
import com.kankan.kankanews.ui.item.New_Activity_My_FanKui;
import com.kankan.kankanews.ui.view.BorderTextView;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.NestingGridView;
import com.kankan.kankanews.utils.CommonUtils;
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
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.fragment.FeedbackFragment;

public class New_RevelationsFragment extends BaseFragment implements
		OnClickListener {

	private View inflate;
	private View retryView;
	private View loadingView;

	private PullToRefreshListView revelationsListView;
	private RevelationsHomeList revelationsHomeList;
	private String revelationsHomeListJson;

	private RevelationsListAdapter revelationsListAdapter;

	private RevelationsListTopHolder topHolder;

	private RevelationsBreaksListNewsHolder newsHolder;

	private BreaknewsAboutReportHolder aboutReportHolder;

	private LoadedFinishHolder finishHolder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		inflate = inflater.inflate(R.layout.new_fragment_revelations, null);

		initview();
		initListener();
		initData();
		return inflate;

	}

	public void initview() {
		initTitleBar(inflate, "报料大厅");
		revelationsListView = (PullToRefreshListView) inflate
				.findViewById(R.id.revelations_list_view);
		retryView = inflate.findViewById(R.id.revelations_retry_view);
		loadingView = inflate.findViewById(R.id.revelations_loading_view);

		initListView();
	}

	protected void initListView() {
		// TODO Auto-generated method stub
		revelationsListView.setMode(Mode.PULL_FROM_START);
		revelationsListView.getLoadingLayoutProxy(true, false).setPullLabel(
				"下拉可以刷新");
		revelationsListView.getLoadingLayoutProxy(true, false).setReleaseLabel(
				"释放后刷新");
		revelationsListView
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
						loadMoreNetDate();
					}
				});
	}

	private void initListener() {
		retryView.setOnClickListener(this);
	}

	private void initData() {
		boolean flag = this.initLocalDate();
		if (flag) {
			showData(true);
			loadingView.setVisibility(View.GONE);
			revelationsListView.showHeadLoadingView();
		}
		refreshNetDate();
	}

	private void showData(boolean needRefresh) {
		revelationsListView.onRefreshComplete();
		if (needRefresh) {
			revelationsListAdapter = new RevelationsListAdapter();
			revelationsListView.setAdapter(revelationsListAdapter);
		} else {
			revelationsListAdapter.notifyDataSetChanged();
		}
	}

	private class ActivityPageChangeListener implements
			ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			if (topHolder != null) {
				topHolder.activityTitle.setText(revelationsHomeList
						.getActivity().get(arg0).getTitle());
				for (View v : topHolder.activityPointViews) {
					v.setBackgroundResource(R.drawable.point_gray);
				}
				topHolder.activityPointViews.get(
						arg0 % topHolder.activityPointViews.size())
						.setBackgroundResource(R.drawable.point_red);
			}
		}
	}

	private class ActivityViewPageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return revelationsHomeList.getActivity().size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(View container, final int position) {
			if (topHolder != null & topHolder.activityImageViews == null)
				topHolder.activityImageViews = new ArrayList<ImageView>();
			if (topHolder.activityImageViews.size() <= position + 1) {
				ImageView imageView = new ImageView(
						New_RevelationsFragment.this.mActivity);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setLayoutParams(new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						(int) (mActivity.mScreenWidth * 111 / 310)));
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(revelationsHomeList.getActivity()
								.get(position).getTitlepic()), imageView,
						ImgUtils.homeImageOptions);
				((ViewPager) container).addView(imageView);
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String aid = revelationsHomeList.getActivity()
								.get(position).getId();
						Intent intent = new Intent(
								New_RevelationsFragment.this.mActivity,
								RevelationsActivityDetailActivity.class);
						intent.putExtra("_AID_", aid);
						startActivity(intent);
					}
				});
				return imageView;
			}
			return topHolder.activityImageViews.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			// super.destroyItem(container, position, object);
		}
	}

	private class LoadedFinishHolder {
		MyTextView loadedTextView;
		RelativeLayout loadMoreItem;
	}

	private class BreaknewsAboutReportHolder {
		ImageView newsTitilePic;
		MyTextView newsTitile;
	}

	private class RevelationsListTopHolder {
		android.support.v4.view.ViewPager activityViewPager;
		LinearLayout activityPointContent;
		MyTextView activityTitle;
		List<ImageView> activityImageViews;
		List<View> activityPointViews;
	}

	private class RevelationsBreaksListNewsHolder {
		RelativeLayout moreContent;
		LinearLayout keyboardIconContent;
		LinearLayout aboutReportContent;
		MyTextView phoneNumText;
		MyTextView newsText;
		MyTextView allNewsTextBut;
		NestingGridView newsImageGridView;
		ListView aboutReportListView;
		ImageView aboutReportIcon;
	}

	private class RevelationsListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (revelationsHomeList.getActivity() != null
					&& revelationsHomeList.getActivity().size() > 0)
				return revelationsHomeList.getBreaknews().size() + 1 + 1;
			return revelationsHomeList.getBreaknews().size() + 1;
		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 && revelationsHomeList.getActivity() != null
					&& revelationsHomeList.getActivity().size() != 0) {
				return 0;
			} else if (position == (revelationsHomeList.getBreaknews().size() + (revelationsHomeList
					.getActivity().size() > 0 ? 1 : 0)))
				return 2;
			else
				return 1;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			int itemViewType = getItemViewType(position);

			if (convertView == null) {
				if (itemViewType == 0) {
					convertView = inflate.inflate(mActivity,
							R.layout.item_revelations_list_activity, null);
					topHolder = new RevelationsListTopHolder();
					topHolder.activityViewPager = (android.support.v4.view.ViewPager) convertView
							.findViewById(R.id.revelations_activity_viewpager);
					topHolder.activityPointContent = (LinearLayout) convertView
							.findViewById(R.id.revelations_activity_point_content);
					topHolder.activityTitle = (MyTextView) convertView
							.findViewById(R.id.revelations_activity_title);
					topHolder.activityViewPager
							.setOnPageChangeListener(new ActivityPageChangeListener());
					topHolder.activityViewPager
							.setLayoutParams(new RelativeLayout.LayoutParams(
									RelativeLayout.LayoutParams.MATCH_PARENT,
									(int) (mActivity.mScreenWidth * 111 / 310)));
					topHolder.activityViewPager
							.setAdapter(new ActivityViewPageAdapter());
					if (topHolder.activityPointViews == null)
						topHolder.activityPointViews = new ArrayList<View>();

					android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
							PixelUtil.dp2px(6), PixelUtil.dp2px(6));
					for (int i = 0; i < revelationsHomeList.getActivity()
							.size(); i++) {
						View point = new View(mActivity);
						layoutParams.rightMargin = PixelUtil.dp2px(2);
						point.setLayoutParams(layoutParams);
						point.setBackgroundResource(R.drawable.point_gray);
						topHolder.activityPointViews.add(point);
					}
					int size = topHolder.activityPointViews.size();
					if (size > 1) {
						for (View v : topHolder.activityPointViews) {
							if (v.getParent() != null)
								((LinearLayout) v.getParent()).removeView(v);
							topHolder.activityPointContent.addView(v);
							v.setBackgroundResource(R.drawable.point_gray);
						}
						topHolder.activityPointViews.get(0)
								.setBackgroundResource(R.drawable.point_red);
						topHolder.activityViewPager.setCurrentItem(0);
						topHolder.activityPointContent
								.setVisibility(View.VISIBLE);
					} else {
						topHolder.activityPointContent.setVisibility(View.GONE);
					}
					convertView.setTag(topHolder);
				} else if (itemViewType == 1) {
					convertView = inflate.inflate(mActivity,
							R.layout.item_revelations_list_break, null);
					newsHolder = new RevelationsBreaksListNewsHolder();
					newsHolder.moreContent = (RelativeLayout) convertView
							.findViewById(R.id.revelations_breaknews_more_content);
					newsHolder.keyboardIconContent = (LinearLayout) convertView
							.findViewById(R.id.revelations_breaknews_keyboard_icon_content);
					newsHolder.phoneNumText = (MyTextView) convertView
							.findViewById(R.id.revelations_breaknews_phonenum);
					newsHolder.newsText = (MyTextView) convertView
							.findViewById(R.id.revelations_breaknews_newstext);
					newsHolder.allNewsTextBut = (MyTextView) convertView
							.findViewById(R.id.revelations_breaknews_alltext_but);
					newsHolder.newsImageGridView = (NestingGridView) convertView
							.findViewById(R.id.revelations_breaknews_image_grid);
					newsHolder.aboutReportListView = (ListView) convertView
							.findViewById(R.id.revelations_breaknews_about_report_news_list);
					newsHolder.aboutReportIcon = (ImageView) convertView
							.findViewById(R.id.revelations_breaknews_about_report_icon);
					newsHolder.aboutReportContent = (LinearLayout) convertView
							.findViewById(R.id.revelations_breaknews_about_report_content);
					convertView.setTag(newsHolder);
				} else if (itemViewType == 2) {
					convertView = inflate.inflate(mActivity,
							R.layout.item_list_foot_text, null);
					finishHolder = new LoadedFinishHolder();
					finishHolder.loadedTextView = (MyTextView) convertView
							.findViewById(R.id.list_has_loaded_item_textview);
					finishHolder.loadedTextView.setVisibility(View.GONE);
					finishHolder.loadMoreItem = (RelativeLayout) convertView
							.findViewById(R.id.list_load_more_item);
					finishHolder.loadMoreItem.setVisibility(View.VISIBLE);
					finishHolder.loadMoreItem
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											New_RevelationsFragment.this.mActivity,
											RevelationsBreakNewsMoreActivity.class);
									startActivity(intent);
								}
							});
					convertView.setTag(finishHolder);
				}
			} else {
				if (itemViewType == 0) {
					topHolder = (RevelationsListTopHolder) convertView.getTag();
				} else if (itemViewType == 1) {
					newsHolder = (RevelationsBreaksListNewsHolder) convertView
							.getTag();
				} else if (itemViewType == 2) {
					finishHolder = (LoadedFinishHolder) convertView.getTag();
				}
			}

			if (itemViewType == 0) {
				topHolder.activityTitle.setText(revelationsHomeList
						.getActivity().get(position).getTitle());
				FontUtils.setTextViewFontSize(New_RevelationsFragment.this,
						topHolder.activityTitle,
						R.string.home_news_title_text_size,
						spUtil.getFontSizeRadix());
			} else if (itemViewType == 1) {
				int breakLocation = position
						- (revelationsHomeList.getActivity().size() > 0 ? 1 : 0);
				if (breakLocation == 0) {
					newsHolder.moreContent.setVisibility(View.VISIBLE);
					newsHolder.moreContent
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											New_RevelationsFragment.this.mActivity,
											RevelationsBreakNewsMoreActivity.class);
									startActivity(intent);
								}
							});
				} else {
					newsHolder.moreContent.setVisibility(View.GONE);
					newsHolder.moreContent
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {

								}
							});
				}
				final RevelationsBreaknews news = revelationsHomeList
						.getBreaknews().get(breakLocation);
				newsHolder.phoneNumText.setText("网友 "
						+ news.getPhonenum()
						+ " "
						+ TimeUtil.timeStrToString(news.getNewstime(),
								"yyyy-MM-dd"));
				newsHolder.newsText.setText(news.getNewstext());
				newsHolder.allNewsTextBut.setTag(newsHolder.newsText);
				newsHolder.newsText.setTag(newsHolder.allNewsTextBut);
				newsHolder.newsText
						.addOnLayoutChangeListener(new OnLayoutChangeListener() {

							@Override
							public void onLayoutChange(View v, int left,
									int top, int right, int bottom,
									int oldLeft, int oldTop, int oldRight,
									int oldBottom) {
								// TODO Auto-generated method stub
								MyTextView textVi = (MyTextView) v;
								// LinearLayout parent =
								// (LinearLayout)(v.getParent());
								// MyTextView allBut = (MyTextView)
								// parent.findViewById(R.id.revelations_breaknews_alltext_but);
								// Log.e("v",
								// textVi.getLayout() + " "
								// + textVi.getText());
								textVi.removeOnLayoutChangeListener(this);
								boolean isOver = isOverFlowed(textVi);
								MyTextView allBut = (MyTextView) textVi
										.getTag();
								if (isOver || textVi.getLineCount() > 3) {
									allBut.setVisibility(View.VISIBLE);
									if (textVi.getLineCount() > 3)
										allBut.setText("收起");
									else
										allBut.setText("全文");
								} else
									allBut.setVisibility(View.GONE);
							}
						});
				newsHolder.allNewsTextBut
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								boolean isOver = isOverFlowed((MyTextView) v
										.getTag());
								if (isOver) {
									((MyTextView) v).setText("收起");
									((MyTextView) v.getTag()).setMaxLines(100);
									v.postInvalidate();
								} else {
									((MyTextView) v).setText("全文");
									((MyTextView) v.getTag()).setMaxLines(3);
									v.postInvalidate();
								}
								revelationsListAdapter.notifyDataSetChanged();
							}
						});
				List<Keyboard> keyboardList = news.getKeyboard();
				newsHolder.keyboardIconContent.removeAllViews();
				for (Keyboard keyboard : keyboardList) {
					TextView view = new BorderTextView(
							New_RevelationsFragment.this.mActivity,
							keyboard.getColor());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					int px = PixelUtil.dp2px(5);
					params.setMargins(0, px, 0, px);
					view.setLayoutParams(params);
					view.setGravity(Gravity.CENTER);
					int px3 = PixelUtil.dp2px(3);
					view.setPadding(px3, px3, px3, px3);
					view.setText(keyboard.getText());
					view.setTextSize(PixelUtil.dp2px(6));
					view.setTextColor(Color.parseColor(keyboard.getColor()));
					newsHolder.keyboardIconContent.addView(view);
				}
				if (news.getImagegroup() == null
						|| news.getImagegroup().trim().equals(""))
					newsHolder.newsImageGridView.setVisibility(View.GONE);
				else {
					newsHolder.newsImageGridView.setVisibility(View.VISIBLE);
					String[] imagegroup = news.getImagegroup().split("\\|");
					ImageGroupGridAdapter gridAdapter = new ImageGroupGridAdapter();
					gridAdapter.setImageGroup(imagegroup);
					newsHolder.newsImageGridView.setSelector(new ColorDrawable(
							Color.TRANSPARENT));
					newsHolder.newsImageGridView.setAdapter(gridAdapter);
				}
				if (news.getRelatednews().size() == 0) {
					newsHolder.aboutReportContent.setVisibility(View.GONE);
					newsHolder.aboutReportIcon.setVisibility(View.GONE);
				} else {
					newsHolder.aboutReportIcon.setVisibility(View.VISIBLE);
					newsHolder.aboutReportContent.setVisibility(View.VISIBLE);
					newsHolder.aboutReportListView
							.setAdapter(new AboutReportNewsListAdapter(news
									.getRelatednews()));
				}
			}
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.revelations_retry_view:
			refreshNetDate();
		}
	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {
		revelationsHomeListJson = jsonObject.toString();
		boolean needRefresh = (revelationsHomeList == null);
		// ToastUtils.Infotoast(getActivity(), jsonObject.toString());
		revelationsHomeList = JsonUtils.toObject(revelationsHomeListJson,
				RevelationsHomeList.class);
		if (revelationsHomeList != null) {
			loadingView.setVisibility(View.GONE);
			saveLocalDate();
			showData(needRefresh);
		}
	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		ToastUtils.Errortoast(getActivity(), "获取报料列表失败");
		loadingView.setVisibility(View.GONE);
		if (revelationsHomeList == null)
			retryView.setVisibility(View.VISIBLE);
		else
			revelationsListView.onRefreshComplete();
	}

	@Override
	protected boolean initLocalDate() {
		// TODO Auto-generated method stub
		try {
			SerializableObj object = (SerializableObj) mActivity.dbUtils
					.findFirst(Selector.from(SerializableObj.class).where(
							"classType", "=", "RevelationsHomeList"));
			if (object != null) {
				revelationsHomeListJson = object.getJsonStr();
				revelationsHomeList = JsonUtils.toObject(
						revelationsHomeListJson, RevelationsHomeList.class);
				return true;
			} else {
				return false;
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void saveLocalDate() {
		// TODO Auto-generated method stub
		try {
			SerializableObj obj = new SerializableObj("0",
					revelationsHomeListJson, "RevelationsHomeList");
			mActivity.dbUtils.delete(SerializableObj.class,
					WhereBuilder.b("classType", "=", "RevelationsHomeList"));
			mActivity.dbUtils.save(obj);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void refreshNetDate() {
		// TODO Auto-generated method stub
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			this.netUtils.getRevelationsHomeList(mListenerObject,
					mErrorListener);
		} else {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					revelationsListView.onRefreshComplete();
				}
			}, 500);
		}
	}

	@Override
	protected void loadMoreNetDate() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		super.refresh();
		revelationsListView.setSelection(0);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				revelationsListView.setmCurrentMode(Mode.PULL_FROM_START);
				revelationsListView.setRefreshing(false);
			}
		}, 100);
	}

	public boolean isOverFlowed(TextView view) {
		return view.getLayout().getEllipsisCount(view.getLineCount() - 1) > 0;
	}

	private class ImageGroupGridAdapter extends BaseAdapter {
		private String[] imageGroup;

		public void setImageGroup(String[] imageGroup) {
			this.imageGroup = imageGroup;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (imageGroup.length == 4)
				return 5;
			return imageGroup.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (imageGroup.length == 4)
				return null;
			return imageGroup[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = null;
			if (convertView == null) {
				convertView = inflate.inflate(mActivity,
						R.layout.item_revelations_breaksnews_image_grid_item,
						null);
				imageView = (ImageView) convertView
						.findViewById(R.id.breaknews_image_item);
				imageView.setLayoutParams(new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, (int) (parent
								.getWidth() / 3 * 0.75)));
				convertView.setTag(imageView);
			} else {
				imageView = (ImageView) convertView.getTag();
			}
			imageView.setVisibility(View.VISIBLE);
			if (imageGroup.length == 4 && position == 2) {
				// imageView.setVisibility(View.GONE);
				imageView.setBackground(null);
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				});
			} else if (imageGroup.length == 4 && position > 2) {
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(imageGroup[position - 1]),
						imageView, ImgUtils.homeImageOptions);
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								New_RevelationsFragment.this.mActivity,
								PhotoViewActivity.class);
						intent.putExtra("_IMAGE_GROUP_", imageGroup);
						intent.putExtra("_PHOTO_CUR_NUM_", position - 1);
						startActivity(intent);
					}
				});
			} else {
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(imageGroup[position]), imageView,
						ImgUtils.homeImageOptions);
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								New_RevelationsFragment.this.mActivity,
								PhotoViewActivity.class);
						intent.putExtra("_IMAGE_GROUP_", imageGroup);
						intent.putExtra("_PHOTO_CUR_NUM_", position);
						startActivity(intent);
					}
				});
			}
			return convertView;
		}
	}

	private class AboutReportNewsListAdapter extends BaseAdapter {
		private List<RevelationsNew> revelationsNew;

		public AboutReportNewsListAdapter(List<RevelationsNew> revelationsNew) {
			this.revelationsNew = revelationsNew;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return revelationsNew.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return revelationsNew.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflate.inflate(mActivity,
						R.layout.item_revelations_breaknews_about_report, null);
				aboutReportHolder = new BreaknewsAboutReportHolder();
				aboutReportHolder.newsTitilePic = (ImageView) convertView
						.findViewById(R.id.about_report_news_titlepic);
				aboutReportHolder.newsTitile = (MyTextView) convertView
						.findViewById(R.id.about_report_news_title);
				convertView.setTag(aboutReportHolder);
			} else {
				aboutReportHolder = (BreaknewsAboutReportHolder) convertView
						.getTag();
			}
			ImgUtils.imageLoader.displayImage(CommonUtils
					.doWebpUrl(revelationsNew.get(position).getTitlepic()),
					aboutReportHolder.newsTitilePic, ImgUtils.homeImageOptions);
			aboutReportHolder.newsTitile.setText(revelationsNew.get(position)
					.getTitle());

			return convertView;
		}
	}

}
