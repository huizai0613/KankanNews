package com.kankan.kankanews.ui;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import android.widget.GridView;
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
import com.kankan.kankanews.ui.fragment.New_RevelationsFragment;
import com.kankan.kankanews.ui.item.New_Activity_Content_PicSet;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.item.New_Activity_Content_Web;
import com.kankan.kankanews.ui.item.New_Avtivity_Subject;
import com.kankan.kankanews.ui.view.BorderTextView;
import com.kankan.kankanews.ui.view.EllipsizingTextView;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.EllipsizingTextView.EllipsizeListener;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.StringUtils;
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

	private Set<Integer> isShowSetTextView = new HashSet<Integer>();

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
		initTitleLeftBar("活动详情", R.drawable.new_ic_back);
		inflate = LayoutInflater.from(this);
		loadingView = this.findViewById(R.id.activity_loading_view);
		retryView = this.findViewById(R.id.activity_retry_view);
		activityListView = (PullToRefreshListView) this
				.findViewById(R.id.activity_list_view);
		goRevelationsBut = (LinearLayout) this
				.findViewById(R.id.go_revelations_but);
		nightView = findViewById(R.id.night_view);
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
		EllipsizingTextView newsText;
		MyTextView allNewsTextBut;
		GridView newsImageGridView;
		ListView aboutReportListView;
		ImageView aboutReportIcon;
		ImageView oneNewsImageView;
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			int itemViewType = getItemViewType(position);

			// if (convertView == null) {
			if (itemViewType == 0) {
				convertView = inflate.inflate(
						R.layout.item_revelations_activity_list_activity, null);
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
				newsHolder.newsText = (EllipsizingTextView) convertView
						.findViewById(R.id.revelations_breaknews_newstext);
				newsHolder.allNewsTextBut = (MyTextView) convertView
						.findViewById(R.id.revelations_breaknews_alltext_but);
				newsHolder.newsImageGridView = (GridView) convertView
						.findViewById(R.id.revelations_breaknews_image_grid);
				newsHolder.oneNewsImageView = (ImageView) convertView
						.findViewById(R.id.revelations_breaknews_image_one_view);
				newsHolder.aboutReportListView = (ListView) convertView
						.findViewById(R.id.revelations_breaknews_about_report_news_list);
				newsHolder.aboutReportIcon = (ImageView) convertView
						.findViewById(R.id.revelations_breaknews_about_report_icon);
				newsHolder.aboutReportContent = (LinearLayout) convertView
						.findViewById(R.id.revelations_breaknews_about_report_content);
				newsHolder.allNewsTextBut.setVisibility(View.GONE);
				convertView.setTag(newsHolder);
			} else if (itemViewType == 2) {
				convertView = inflate.inflate(R.layout.item_list_foot_text,
						null);
				finishHolder = new LoadedFinishHolder();
				finishHolder.loadedTextView = (MyTextView) convertView
						.findViewById(R.id.list_has_loaded_item_textview);
				convertView.setTag(finishHolder);
			}
			// } else {
			// if (itemViewType == 0) {
			// topHolder = (ActivityListTopHolder) convertView.getTag();
			// } else if (itemViewType == 1) {
			// newsHolder = (RevelationsBreaksListNewsHolder) convertView
			// .getTag();
			// } else if (itemViewType == 2) {
			// finishHolder = (LoadedFinishHolder) convertView.getTag();
			// }
			// }

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
				newsHolder.allNewsTextBut.setVisibility(View.GONE);
				if (isShowSetTextView.contains(position)) {
					newsHolder.newsText.setMaxLines(100);
					newsHolder.allNewsTextBut.setVisibility(View.VISIBLE);
					newsHolder.allNewsTextBut.setText("收起");
				} else
					newsHolder.newsText.setMaxLines(3);
				newsHolder.newsText.setText(StringUtils.deleteLastNewLine(news
						.getNewstext()));
				FontUtils.setTextViewFontSize(
						RevelationsActivityDetailActivity.this,
						newsHolder.newsText,
						R.string.home_news_title_text_size,
						spUtil.getFontSizeRadix());
				newsHolder.allNewsTextBut.setTag(newsHolder.newsText);
				newsHolder.newsText.setTag(newsHolder.allNewsTextBut);
				newsHolder.newsText
						.addEllipsizeListener(new EllipsizeListener() {

							@Override
							public void ellipsizeStateChanged(
									boolean ellipsized,
									EllipsizingTextView textView) {
								LinearLayout parent = (LinearLayout) (textView
										.getParent());
								MyTextView allBut = (MyTextView) parent
										.findViewById(R.id.revelations_breaknews_alltext_but);
								if (!ellipsized && textView.getMaxLines() == 3)
									allBut.setVisibility(View.GONE);
								else
									allBut.setVisibility(View.VISIBLE);
							}
						});
				newsHolder.allNewsTextBut
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								LinearLayout parent = (LinearLayout) (v
										.getParent());
								EllipsizingTextView textView = (EllipsizingTextView) parent
										.findViewById(R.id.revelations_breaknews_newstext);
								if (textView.getMaxLines() == 3) {
									textView.setMaxLines(100);
									((MyTextView) v).setText("收起");
									Log.e("textView",
											((MyTextView) v).getText() + "");
									((MyTextView) v).postInvalidate();
									isShowSetTextView.add(position);
								} else {
									textView.setMaxLines(3);
									((MyTextView) v).setText("全文");
									Log.e("textView",
											((MyTextView) v).getText() + "");
									((MyTextView) v).postInvalidate();
									isShowSetTextView.remove(position);
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
					FontUtils.setTextViewFontSize(
							RevelationsActivityDetailActivity.this, view,
							R.string.border_text_view_text_size, 1);
					view.setTextColor(Color.parseColor(keyboard.getColor()));
					newsHolder.keyboardIconContent.addView(view);
				}
				newsHolder.oneNewsImageView.setVisibility(View.GONE);
				if (news.getImagegroup() == null
						|| news.getImagegroup().trim().equals(""))
					newsHolder.newsImageGridView.setVisibility(View.GONE);
				else {
					newsHolder.newsImageGridView.setVisibility(View.VISIBLE);
					final String[] imageGroup = news.getImagegroup().split(
							"\\|");
					if (imageGroup.length == 1) {
						newsHolder.oneNewsImageView.setVisibility(View.VISIBLE);
						newsHolder.newsImageGridView.setVisibility(View.GONE);
						ImgUtils.imageLoader.displayImage(
								CommonUtils.doWebpUrl(imageGroup[0]),
								newsHolder.oneNewsImageView,
								ImgUtils.homeImageOptions);
						newsHolder.oneNewsImageView
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										Intent intent = new Intent(
												RevelationsActivityDetailActivity.this,
												PhotoViewActivity.class);
										intent.putExtra("_IMAGE_GROUP_",
												imageGroup);
										intent.putExtra("_PHOTO_CUR_NUM_", 0);
										startActivity(intent);
									}
								});

					} else {
						int width = RevelationsActivityDetailActivity.this.mScreenWidth
								- PixelUtil.dp2px(60);
						ViewGroup.LayoutParams params = newsHolder.newsImageGridView
								.getLayoutParams();
						int num = (int) Math
								.ceil(((float) (imageGroup.length)) / 3);
						params.height = (int) (width / 3 * 0.75 * num);
						newsHolder.newsImageGridView.setLayoutParams(params);
						ImageGroupGridAdapter gridAdapter = new ImageGroupGridAdapter();
						gridAdapter.setImageGroup(imageGroup);
						newsHolder.newsImageGridView
								.setSelector(new ColorDrawable(
										Color.TRANSPARENT));
						newsHolder.newsImageGridView.setAdapter(gridAdapter);
					}
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = null;
			// if (convertView == null) {
			convertView = inflate.inflate(
					R.layout.item_revelations_breaksnews_image_grid_item, null);
			imageView = (ImageView) convertView
					.findViewById(R.id.breaknews_image_item);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView
					.getLayoutParams();
			params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
			params.height = (int) (parent.getWidth() / 3 * 0.75);
			imageView.setLayoutParams(params);
			convertView.setTag(imageView);
			// } else {
			// imageView = (ImageView) convertView.getTag();
			// }

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
								RevelationsActivityDetailActivity.this,
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
								RevelationsActivityDetailActivity.this,
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
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
			ImgUtils.imageLoader.displayImage(CommonUtils
					.doWebpUrl(revelationsNew.get(position).getTitlepic()),
					aboutReportHolder.newsTitilePic, ImgUtils.homeImageOptions);
			aboutReportHolder.newsTitile.setText(revelationsNew.get(position)
					.getTitle());

			FontUtils.setTextViewFontSize(
					RevelationsActivityDetailActivity.this,
					aboutReportHolder.newsTitile,
					R.string.revelations_aboutreport_news_text_size,
					spUtil.getFontSizeRadix());
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openNews(revelationsNew.get(position));
				}
			});
			return convertView;
		}
	}

	private void openNews(RevelationsNew news) {
		//
		final int news_type = Integer.valueOf(news.getType());
		if (news_type % 10 == 1) {
			this.startAnimActivityByParameter(New_Activity_Content_Video.class,
					news.getMid(), news.getType(), news.getTitleurl(),
					news.getNewstime(), news.getTitle(), news.getTitlepic(),
					news.getTitlepic(), news.getIntro());
		} else if (news_type % 10 == 2) {
			final String[] pics = news.getTitlepic().split("::::::");
			this.startAnimActivityByParameter(
					New_Activity_Content_PicSet.class, news.getMid(),
					news.getType(), news.getTitleurl(), news.getNewstime(),
					news.getTitle(), news.getTitlepic(), pics[1],
					news.getIntro());
		} else if (news_type % 10 == 5) {
			// 专题
			this.startSubjectActivityByParameter(New_Avtivity_Subject.class,
					news.getZtid(), news.getTitle(), news.getTitlepic(),
					news.getTitleurl(), news.getTitlepic(), news.getTitlepic(),
					news.getIntro());
		}
		// else if (news.getZtype().equals("1")) {
		// this.startSubjectActivityByParameter(New_Avtivity_Subject.class,
		// news.getZtid(), news.getTitle(), news.getTitlepic(),
		// news.getTitleurl(), news.getTitlepic(), news.getTitlepic());
		// }
		else {
			this.startAnimActivityByParameter(New_Activity_Content_Web.class,
					news.getMid(), news.getType(), news.getTitleurl(),
					news.getNewstime(), news.getTitle(), news.getTitlepic(),
					news.getTitlepic(), news.getIntro());
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (FontUtils.isRevelationsActivityFontSizeHasChanged()) {
			changeFontSize();
			FontUtils.setRevelationsActivityFontSizeHasChanged(false);
		}
		if (!spUtil.getIsDayMode())
			chage2Night();
		else
			chage2Day();
	}

	@Override
	public void changeFontSize() {
		// TODO Auto-generated method stub
		int first = activityListView.getFirstVisiblePosition();
		activityListView.setAdapter(activityListAdapter);
		activityListView.setSelection(first);
	}
}
