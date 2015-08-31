package com.kankan.kankanews.ui.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
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
import com.kankan.kankanews.bean.SerializableObj;
import com.kankan.kankanews.ui.view.BorderTextView;
import com.kankan.kankanews.ui.view.MyTextView;
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

	private RevelationsListNewsHolder newsHolder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		inflate = inflater.inflate(R.layout.new_fragment_revelations, null);

		initview();
		initLister();
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

	private void initLister() {
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
		public Object instantiateItem(View container, int position) {
			if (topHolder != null & topHolder.activityImageViews == null)
				topHolder.activityImageViews = new ArrayList<ImageView>();
			if (topHolder.activityImageViews.size() <= position + 1) {
				ImageView imageView = new ImageView(
						New_RevelationsFragment.this.mActivity);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setLayoutParams(new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						(int) (mActivity.mScreenWidth * 111 / 310)));
				ImgUtils.imageLoader.displayImage(revelationsHomeList
						.getActivity().get(position).getTitlepic(), imageView,
						ImgUtils.homeImageOptions);
				((ViewPager) container).addView(imageView);
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

	private class RevelationsListTopHolder {
		android.support.v4.view.ViewPager activityViewPager;
		LinearLayout activityPointContent;
		MyTextView activityTitle;
		List<ImageView> activityImageViews;
		List<View> activityPointViews;
	}

	private class RevelationsListNewsHolder {
		RelativeLayout breaknewsMoreContent;
		LinearLayout keyboardIconContent;
	}

	private class RevelationsListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (revelationsHomeList.getActivity() != null
					&& revelationsHomeList.getActivity().size() > 0)
				return revelationsHomeList.getBreaknews().size() + 1;
			return revelationsHomeList.getBreaknews().size();
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 && revelationsHomeList.getActivity() != null
					&& revelationsHomeList.getActivity().size() != 0) {
				return 0;
			} else
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
					newsHolder = new RevelationsListNewsHolder();
					newsHolder.breaknewsMoreContent = (RelativeLayout) convertView
							.findViewById(R.id.revelations_breaknews_more_content);
					newsHolder.keyboardIconContent = (LinearLayout) convertView
							.findViewById(R.id.revelations_breaknews_keyboard_icon_content);
					convertView.setTag(newsHolder);
				}
			} else {
				if (itemViewType == 0) {
					topHolder = (RevelationsListTopHolder) convertView.getTag();
				} else if (itemViewType == 1) {
					newsHolder = (RevelationsListNewsHolder) convertView
							.getTag();
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
				// if ((position == 0 &&
				// revelationsHomeList.getActivity().size() == 0)
				// || (position == 1 && revelationsHomeList.getActivity()
				// .size() > 0))
				if (breakLocation == 0)
					newsHolder.breaknewsMoreContent.setVisibility(View.VISIBLE);
				else
					newsHolder.breaknewsMoreContent.setVisibility(View.GONE);
				RevelationsBreaknews news = revelationsHomeList.getBreaknews()
						.get(breakLocation);
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
					view.setPadding(0, px3, 0, px3);
					view.setText(keyboard.getText());
					view.setTextSize(PixelUtil.dp2px(6));
					view.setTextColor(Color.parseColor(keyboard.getColor()));
					newsHolder.keyboardIconContent.addView(view);
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

}
