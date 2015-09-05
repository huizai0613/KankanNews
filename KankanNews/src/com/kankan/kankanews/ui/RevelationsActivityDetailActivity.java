package com.kankan.kankanews.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.Keyboard;
import com.kankan.kankanews.bean.RevelationsBreaknews;
import com.kankan.kankanews.bean.RevelationsHomeList;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.NestingGridView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;

public class RevelationsActivityDetailActivity extends BaseActivity implements
		OnClickListener {
	private String aid;
	private LayoutInflater inflate;
	private View retryView;
	private View loadingView;

	private PullToRefreshListView activityListView;

	private RevelationsHomeList revelationsActivityList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_revelations_activity_detail);

	}

	protected void refreshNetDate() {
		// TODO Auto-generated method stub
		if (CommonUtils.isNetworkAvailable(this)) {
			this.netUtils.getRevelationsActivityList(this.aid, "",
					this.mListener, mErrorListener);
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					activityListView.onRefreshComplete();
				}
			}, 500);
		}
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		initTitleBar("活动详情");
		inflate = LayoutInflater.from(this);
		loadingView = this.findViewById(R.id.activity_loading_view);
		retryView = this.findViewById(R.id.activity_retry_view);
		activityListView = (PullToRefreshListView) this
				.findViewById(R.id.activity_list_view);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		this.aid = this.getIntent().getStringExtra("_AID_");
		if (this.aid == null)
			this.finish();
		Log.e("AID", aid);
		refreshNetDate();
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

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
	protected void onSuccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		boolean needRefresh = (revelationsActivityList == null);
		// ToastUtils.Infotoast(getActivity(), jsonObject.toString());
		revelationsActivityList = JsonUtils.toObject(jsonObject.toString(),
				RevelationsHomeList.class);
		if (revelationsActivityList != null) {
			loadingView.setVisibility(View.GONE);
			showData(needRefresh);
		}

	}

	private void showData(boolean needRefresh) {
		activityListView.onRefreshComplete();
		if (needRefresh) {
			activityListAdapter = new ActivityListAdapter();
			activityListView.setAdapter(revelationsListAdapter);
		} else {
			revelationsListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		ToastUtils.Errortoast(this, "获取活动详情失败");
		loadingView.setVisibility(View.GONE);
		if (revelationsActivityList == null)
			retryView.setVisibility(View.VISIBLE);
		else
			activityListView.onRefreshComplete();
	}

	private class ActivityListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return revelationsActivityList.getBreaknews().size() + 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 && revelationsActivityList.getActivity() != null
					&& revelationsActivityList.getActivity().size() != 0) {
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
					convertView = inflate.inflate(
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
				}
			} else {
				if (itemViewType == 0) {
					topHolder = (RevelationsListTopHolder) convertView.getTag();
				} else if (itemViewType == 1) {
					newsHolder = (RevelationsBreaksListNewsHolder) convertView
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
				if (breakLocation == 0)
					newsHolder.moreContent.setVisibility(View.VISIBLE);
				else
					newsHolder.moreContent.setVisibility(View.GONE);
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
				// for (Keyboard keyboard : keyboardList) {
				// TextView view = new BorderTextView(
				// New_RevelationsFragment.this.mActivity,
				// keyboard.getColor());
				// LinearLayout.LayoutParams params = new
				// LinearLayout.LayoutParams(
				// LayoutParams.MATCH_PARENT,
				// LayoutParams.WRAP_CONTENT);
				// int px = PixelUtil.dp2px(5);
				// params.setMargins(0, px, 0, px);
				// view.setLayoutParams(params);
				// view.setGravity(Gravity.CENTER);测试1
				// int px3 = PixelUtil.dp2px(3);
				// view.setPadding(px3, px3, px3, px3);
				// view.setText(keyboard.getText());
				// view.setTextSize(PixelUtil.dp2px(6));
				// view.setTextColor(Color.parseColor(keyboard.getColor()));
				// newsHolder.keyboardIconContent.addView(view);
				// }
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
}
