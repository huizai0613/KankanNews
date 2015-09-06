package com.kankan.kankanews.ui;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.Keyboard;
import com.kankan.kankanews.bean.RevelationsActicityList;
import com.kankan.kankanews.bean.RevelationsBreaknews;
import com.kankan.kankanews.bean.RevelationsNew;
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

public class RevelationsActivityDetailActivity extends BaseActivity implements
		OnClickListener {
	private String aid;
	private LayoutInflater inflate;
	private View retryView;
	private View loadingView;

	private PullToRefreshListView activityListView;

	private RevelationsActicityList revelationsActivityList;
	private ActivityListAdapter activityListAdapter;

	private ActivityListTopHolder topHolder;
	private BreaknewsAboutReportHolder aboutReportHolder;

	private RevelationsBreaksListNewsHolder newsHolder;
	private LoadedFinishHolder finishHolder;
	private LinearLayout goRevelationsBut;
	private boolean isLoadEnd = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_revelations_activity_detail);

	}

	protected void refreshNetDate() {
		// TODO Auto-generated method stub
		isLoadEnd = false;
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
		goRevelationsBut = (LinearLayout) this
				.findViewById(R.id.go_revelations_but);
		initListView();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		this.aid = this.getIntent().getStringExtra("_AID_");
		if (this.aid == null)
			this.finish();
		refreshNetDate();
	}

	protected void initListView() {
		// TODO Auto-generated method stub
		activityListView.setMode(Mode.BOTH);
		activityListView.getLoadingLayoutProxy(true, false).setPullLabel(
				"下拉可以刷新");
		activityListView.getLoadingLayoutProxy(true, false).setReleaseLabel(
				"释放后刷新");
		activityListView.getLoadingLayoutProxy(false, true).setPullLabel(
				"上拉加载更多");
		activityListView.getLoadingLayoutProxy(false, true).setRefreshingLabel(
				"刷新中…");
		activityListView.getLoadingLayoutProxy(false, true).setReleaseLabel(
				"松开立即加载");
		activityListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						// String time = TimeUtil.getTime(new Date());
						// refreshView.getLoadingLayoutProxy()
						// .setLastUpdatedLabel("最后更新:" + time);
						refreshNetDate();
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						loadMoreNetDate();
					}
				});
	}

	protected void loadMoreNetDate() {
		// TODO Auto-generated method stub
		if (isLoadEnd || !CommonUtils.isNetworkAvailable(this)) {
			activityListView.postDelayed(new Runnable() {
				@Override
				public void run() {
					activityListView.onRefreshComplete();
				}
			}, 300);
			return;
		}
		List<RevelationsBreaknews> breaknews = this.revelationsActivityList
				.getBreaknews();
		this.netUtils.getRevelationsActivityList(this.aid,
				breaknews.get(breaknews.size() - 1).getNewstime(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject jsonObject) {
						activityListView.onRefreshComplete();
						RevelationsActicityList moreList = JsonUtils.toObject(
								jsonObject.toString(),
								RevelationsActicityList.class);
						if (moreList.getBreaknews().size() == 0) {
							isLoadEnd = true;
						} else {
							isLoadEnd = false;
							revelationsActivityList.getBreaknews().addAll(
									moreList.getBreaknews());
						}
						activityListAdapter.notifyDataSetChanged();
					}
				}, mErrorListener);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		retryView.setOnClickListener(this);
		goRevelationsBut.setOnClickListener(this);
		setOnLeftClickLinester(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.revelations_retry_view:
			refreshNetDate();
			break;
		case R.id.go_revelations_but:
			goRevelations();
			break;
		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		}
	}

	private void goRevelations() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, RevelationsActivity.class);
		intent.putExtra("_AID_", this.aid);
		this.startActivity(intent);
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		boolean needRefresh = (revelationsActivityList == null);
		// ToastUtils.Infotoast(getActivity(), jsonObject.toString());
		revelationsActivityList = JsonUtils.toObject(jsonObject.toString(),
				RevelationsActicityList.class);
		if (revelationsActivityList != null) {
			loadingView.setVisibility(View.GONE);
			if (revelationsActivityList.getBreaknews().size() == 0)
				isLoadEnd = true;
			showData(needRefresh);
		}
	}

	private void showData(boolean needRefresh) {
		if (needRefresh) {
			activityListAdapter = new ActivityListAdapter();
			activityListView.setAdapter(activityListAdapter);
		} else {
			activityListAdapter.notifyDataSetChanged();
		}
		activityListView.onRefreshComplete();
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

	private class ActivityListTopHolder {
		ImageView activityImageView;
		MyTextView activityTitle;
		MyTextView activityIntro;
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

	private class LoadedFinishHolder {
		MyTextView loadedTextView;
	}

	private class BreaknewsAboutReportHolder {
		ImageView newsTitilePic;
		MyTextView newsTitile;
	}

	private class ActivityListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (isLoadEnd)
				return revelationsActivityList.getBreaknews().size() + 1 + 1;
			return revelationsActivityList.getBreaknews().size() + 1;
		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 && revelationsActivityList.getActivity() != null) {
				return 0;
			} else if (position == (revelationsActivityList.getBreaknews()
					.size() + 1)) {
				return 2;
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
							R.layout.item_revelations_activity_list_activity,
							null);
					topHolder = new ActivityListTopHolder();
					topHolder.activityImageView = (ImageView) convertView
							.findViewById(R.id.activity_list_imageview);
					topHolder.activityTitle = (MyTextView) convertView
							.findViewById(R.id.activity_list_title);
					topHolder.activityIntro = (MyTextView) convertView
							.findViewById(R.id.activity_list_intro);
					topHolder.activityImageView
							.setLayoutParams(new RelativeLayout.LayoutParams(
									RelativeLayout.LayoutParams.MATCH_PARENT,
									(int) (RevelationsActivityDetailActivity.this.mScreenWidth * 111 / 310)));
					convertView.setTag(topHolder);
				} else if (itemViewType == 1) {
					convertView = inflate.inflate(
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
					convertView = inflate.inflate(R.layout.item_list_foot_text,
							null);
					finishHolder = new LoadedFinishHolder();
					finishHolder.loadedTextView = (MyTextView) convertView
							.findViewById(R.id.list_has_loaded_item_textview);
					convertView.setTag(finishHolder);
				}
			} else {
				if (itemViewType == 0) {
					topHolder = (ActivityListTopHolder) convertView.getTag();
				} else if (itemViewType == 1) {
					newsHolder = (RevelationsBreaksListNewsHolder) convertView
							.getTag();
				} else if (itemViewType == 2) {
					finishHolder = (LoadedFinishHolder) convertView.getTag();
				}
			}

			if (itemViewType == 0) {
				com.kankan.kankanews.bean.RevelationsActivity activity = revelationsActivityList
						.getActivity();
				ImgUtils.imageLoader.displayImage(activity.getTitlepic(),
						topHolder.activityImageView, ImgUtils.homeImageOptions);
				topHolder.activityTitle.setText(revelationsActivityList
						.getActivity().getTitle());
				topHolder.activityIntro.setText(revelationsActivityList
						.getActivity().getIntro());
				FontUtils.setTextViewFontSize(
						RevelationsActivityDetailActivity.this,
						topHolder.activityTitle,
						R.string.home_news_title_text_size,
						spUtil.getFontSizeRadix());
				FontUtils.setTextViewFontSize(
						RevelationsActivityDetailActivity.this,
						topHolder.activityIntro,
						R.string.home_news_title_text_size,
						spUtil.getFontSizeRadix());
			} else if (itemViewType == 1) {
				int breakLocation = position
						- (revelationsActivityList.getActivity() != null ? 1
								: 0);
				newsHolder.moreContent.setVisibility(View.GONE);
				final RevelationsBreaknews news = revelationsActivityList
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
								activityListAdapter.notifyDataSetChanged();
							}
						});
				List<Keyboard> keyboardList = news.getKeyboard();
				newsHolder.keyboardIconContent.removeAllViews();
				for (Keyboard keyboard : keyboardList) {
					TextView view = new BorderTextView(
							RevelationsActivityDetailActivity.this,
							keyboard.getColor());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = null;
			if (convertView == null) {
				convertView = inflate.inflate(
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
			} else if (imageGroup.length == 4 && position > 2) {
				ImgUtils.imageLoader.displayImage(imageGroup[position - 1],
						imageView, ImgUtils.homeImageOptions);
			} else {
				ImgUtils.imageLoader.displayImage(imageGroup[position],
						imageView, ImgUtils.homeImageOptions);
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
				convertView = inflate.inflate(
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
			ImgUtils.imageLoader.displayImage(revelationsNew.get(position)
					.getTitlepic(), aboutReportHolder.newsTitilePic,
					ImgUtils.homeImageOptions);
			aboutReportHolder.newsTitile.setText(revelationsNew.get(position)
					.getTitle());

			return convertView;
		}
	}

	public boolean isOverFlowed(TextView view) {
		return view.getLayout().getEllipsisCount(view.getLineCount() - 1) > 0;
	}
}
