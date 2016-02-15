package com.kankan.kankanews.ui.item;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import com.kankan.kankanews.bean.New_News_Home;
import com.kankan.kankanews.bean.NewsHomeModule;
import com.kankan.kankanews.bean.NewsHomeModuleItem;
import com.kankan.kankanews.bean.RevelationsActicityObjBreakNewsList;
import com.kankan.kankanews.bean.RevelationsBreaknews;
import com.kankan.kankanews.bean.RevelationsHomeList;
import com.kankan.kankanews.bean.RevelationsNew;
import com.kankan.kankanews.bean.SerializableObj;
import com.kankan.kankanews.ui.fragment.LiveLiveListFragment;
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.fragment.New_RevelationsFragment;
import com.kankan.kankanews.ui.fragment.NewsHomeFragment;
import com.kankan.kankanews.ui.fragment.item.New_HomeItemFragment;
import com.kankan.kankanews.ui.view.BorderTextView;
import com.kankan.kankanews.ui.view.EllipsizingTextView;
import com.kankan.kankanews.ui.view.EllipsizingTextView.EllipsizeListener;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.ClickUtils;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankan.kankanews.utils.NetUtils;
import com.kankan.kankanews.utils.NewsBrowseUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.StringUtils;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class NewsListActivity extends BaseActivity implements OnClickListener {
	private LayoutInflater inflate;
	private View mRetryView;
	private View mLoadingView;

	private PullToRefreshListView mNewsListView;

	private NewsHomeModule mNewsHomeModule;
	private String mNewsHomeModuleJson;
	private NewsListAdapter mNewsListAdapter;

	private NewsListHolder mNewsListHolder;
	private NewAlbumsHolder mNewAlbumsHolder;
	private LoadedFinishHolder mFinishHolder;
	private NewTopPicHolder mNewTopPicHolder;
	private boolean mIsLoadEnd = false;
	private String mLastTime = "";

	private String mAppClassId = "";

	// private NewsHomeModule mHomeModule;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_news_list);
	}

	@Override
	protected void initView() {
		inflate = LayoutInflater.from(this);
		mLoadingView = this.findViewById(R.id.newslist_loading_view);
		mRetryView = this.findViewById(R.id.newslist_retry_view);
		mNewsListView = (PullToRefreshListView) this
				.findViewById(R.id.newslist_list_view);
		nightView = findViewById(R.id.night_view);
		initTitleLeftBar("", R.drawable.new_ic_back);
		initListView();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		// mAppClassId = this.getIntent().getStringExtra(
		// "_NEWS_HOME_APP_CLASS_ID_");
		NewsHomeModule mHomeModule = (NewsHomeModule) this.getIntent()
				.getSerializableExtra("_NEWS_HOME_MODEULE_");
		mAppClassId = mHomeModule.getAppclassid();

		boolean flag = this.initLocalData();
		if (flag) {
			showData();
			NetUtils.getInstance(mContext).getAnalyse(this, "module",
					mNewsHomeModule.getTitle(), mNewsHomeModule.getTitleurl());
		} else {
			mNewsListView.showHeadLoadingView();
			refreshNetDate();
		}
	}

	protected void initListView() {
		mNewsListView.setMode(Mode.BOTH);
		mNewsListView.getLoadingLayoutProxy(true, false).setPullLabel("下拉可以刷新");
		mNewsListView.getLoadingLayoutProxy(true, false).setReleaseLabel(
				"释放后刷新");
		mNewsListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
		mNewsListView.getLoadingLayoutProxy(false, true).setRefreshingLabel(
				"刷新中…");
		mNewsListView.getLoadingLayoutProxy(false, true).setReleaseLabel(
				"松开立即加载");
		mNewsListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						refreshNetDate();
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						loadMoreNetDate();
					}
				});
	}

	protected void refreshNetDate() {
		mIsLoadEnd = false;
		if (CommonUtils.isNetworkAvailable(this)) {
			this.netUtils.getNewsList(mAppClassId, "", this.mListener,
					mErrorListener);
		} else {
			this.mLoadingView.setVisibility(View.GONE);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mNewsListView.onRefreshComplete();
				}
			}, 500);
			if (mNewsHomeModule == null) {
				this.mRetryView.setVisibility(View.VISIBLE);
			}
		}
	}

	protected void loadMoreNetDate() {
		// TODO Auto-generated method stub
		if (mIsLoadEnd || !CommonUtils.isNetworkAvailable(this)
				|| mNewsHomeModule == null) {
			mNewsListView.postDelayed(new Runnable() {
				@Override
				public void run() {
					mNewsListView.onRefreshComplete();
				}
			}, 300);
			return;
		}
		this.netUtils.getNewsList(
				mAppClassId,
				mNewsHomeModule.getList()
						.get(mNewsHomeModule.getList().size() - 1)
						.getNewstime(), new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject jsonObject) {
						mNewsListView.onRefreshComplete();
						NewsHomeModule more = JsonUtils.toObject(
								jsonObject.toString(), NewsHomeModule.class);
						if (more.getList().size() == 0) {
							mIsLoadEnd = true;
						} else {
							mIsLoadEnd = false;
							mNewsHomeModule.getList().addAll(more.getList());
						}
						mNewsListAdapter.notifyDataSetChanged();
					}
				}, mErrorListener);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		mRetryView.setOnClickListener(this);
		setOnLeftClickLinester(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.newslist_retry_view:
			refreshNetDate();
			break;
		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		}
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		mNewsHomeModuleJson = jsonObject.toString();
		mNewsHomeModule = JsonUtils.toObject(mNewsHomeModuleJson,
				NewsHomeModule.class);
		NetUtils.getInstance(mContext).getAnalyse(this, "module",
				mNewsHomeModule.getTitle(), mNewsHomeModule.getTitleurl());
		if (mNewsHomeModule != null) {
			if (mNewsHomeModule.getList().size() == 0)
				mIsLoadEnd = true;
			saveLocalDate();
			showData();
		}
	}

	private void showData() {
		this.setContentTextView(mNewsHomeModule.getTitle());
		mNewsListView.onRefreshComplete();
		if (mNewsListAdapter == null) {
			mNewsListAdapter = new NewsListAdapter();
			mNewsListView.setAdapter(mNewsListAdapter);
		} else {
			mNewsListAdapter.notifyDataSetChanged();
		}
		mLoadingView.setVisibility(View.GONE);
		mRetryView.setVisibility(View.GONE);
	}

	@Override
	protected void onFailure(VolleyError error) {
		ToastUtils.Errortoast(this, "获取新闻列表失败");
		mLoadingView.setVisibility(View.GONE);
		if (mNewsHomeModule == null)
			mRetryView.setVisibility(View.VISIBLE);
		else
			mNewsListView.onRefreshComplete();
	}

	private class NewsListHolder {
		ImageView titlepic;
		MyTextView title;
		ImageView newsTimeIcon;
		MyTextView newsClick;
		ImageView newsType;
		LinearLayout keyboardIconContent;
	}

	private class NewAlbumsHolder {
		MyTextView title;
		LinearLayout home_albums_imgs_layout;
		ImageView albums_image_1;
		ImageView albums_image_2;
		ImageView albums_image_3;
	}

	private class NewTopPicHolder {
		MyTextView title;
		ImageView image;
	}

	private class LoadedFinishHolder {
		MyTextView loadedTextView;
	}

	private class NewsListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			boolean hasTopPic = mNewsHomeModule.getHeadline() != null
					&& mNewsHomeModule.getHeadline().size() > 0;
			if (hasTopPic) {
				if (mIsLoadEnd)
					return mNewsHomeModule.getList().size() + 2;
				return mNewsHomeModule.getList().size() + 1;
			} else {
				if (mIsLoadEnd)
					return mNewsHomeModule.getList().size() + 1;
				return mNewsHomeModule.getList().size();
			}
		}

		@Override
		public int getViewTypeCount() {
			return 4;
		}

		@Override
		public int getItemViewType(int position) {
			boolean hasTopPic = mNewsHomeModule.getHeadline() != null
					&& mNewsHomeModule.getHeadline().size() > 0;
			if (hasTopPic) {
				if (position == 0)
					return 3;
				if (position == mNewsHomeModule.getList().size() + 1) {
					return 1;
				} else {
					NewsHomeModuleItem news = mNewsHomeModule.getList().get(
							position - 1);
					if (news.getType().equals("album")) {
						return 2;
					} else {
						return 0;
					}
				}
			} else {
				if (position == mNewsHomeModule.getList().size()) {
					return 1;
				} else {
					NewsHomeModuleItem news = mNewsHomeModule.getList().get(
							position);
					if (news.getType().equals("album")) {
						return 2;
					} else {
						return 0;
					}
				}
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			final boolean hasTopPic = mNewsHomeModule.getHeadline() != null
					&& mNewsHomeModule.getHeadline().size() > 0;
			int itemViewType = getItemViewType(position);
			if (convertView == null) {
				if (itemViewType == 0) {
					convertView = inflate.inflate(
							R.layout.item_news_list_content, null);
					mNewsListHolder = new NewsListHolder();
					mNewsListHolder.titlepic = (ImageView) convertView
							.findViewById(R.id.home_news_titlepic);
					mNewsListHolder.title = (MyTextView) convertView
							.findViewById(R.id.home_news_title);
					FontUtils.setTextViewFontSize(NewsListActivity.this,
							mNewsListHolder.title,
							R.string.home_news_text_size,
							spUtil.getFontSizeRadix());
					mNewsListHolder.newsTimeIcon = (ImageView) convertView
							.findViewById(R.id.home_news_newstime_sign);
					mNewsListHolder.newsClick = (MyTextView) convertView
							.findViewById(R.id.home_news_newstime);
					mNewsListHolder.newsType = (ImageView) convertView
							.findViewById(R.id.home_news_newstype);
					mNewsListHolder.keyboardIconContent = (LinearLayout) convertView
							.findViewById(R.id.news_list_keyboard_content);
					convertView.setTag(mNewsListHolder);
				} else if (itemViewType == 1) {
					convertView = inflate.inflate(R.layout.item_list_foot_text,
							null);
					mFinishHolder = new LoadedFinishHolder();
					mFinishHolder.loadedTextView = (MyTextView) convertView
							.findViewById(R.id.list_has_loaded_item_textview);
					convertView.setTag(mFinishHolder);
					return convertView;
				} else if (itemViewType == 2) {
					convertView = inflate.inflate(
							R.layout.new_home_news_albums_item, null);
					mNewAlbumsHolder = new NewAlbumsHolder();
					mNewAlbumsHolder.title = (MyTextView) convertView
							.findViewById(R.id.home_albums_title);
					FontUtils.setTextViewFontSize(NewsListActivity.this,
							mNewAlbumsHolder.title,
							R.string.home_news_text_size,
							spUtil.getFontSizeRadix());
					mNewAlbumsHolder.home_albums_imgs_layout = (LinearLayout) convertView
							.findViewById(R.id.home_albums_imgs_layout);
					mNewAlbumsHolder.albums_image_1 = (ImageView) convertView
							.findViewById(R.id.home_albums_img_1);
					mNewAlbumsHolder.albums_image_2 = (ImageView) convertView
							.findViewById(R.id.home_albums_img_2);
					mNewAlbumsHolder.albums_image_3 = (ImageView) convertView
							.findViewById(R.id.home_albums_img_3);
					mNewAlbumsHolder.home_albums_imgs_layout
							.setLayoutParams(new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.MATCH_PARENT,
									(int) ((mScreenWidth - PixelUtil
											.dp2px(10 * 4)) / 3 * 0.7)));
					convertView.setTag(mNewAlbumsHolder);
				} else if (itemViewType == 3) {
					convertView = inflate.inflate(
							R.layout.item_news_list_header, null);
					mNewTopPicHolder = new NewTopPicHolder();
					mNewTopPicHolder.title = (MyTextView) convertView
							.findViewById(R.id.top_title);
					FontUtils.setTextViewFontSizeDIP(NewsListActivity.this,
							mNewTopPicHolder.title,
							R.string.home_news_text_size, 1);
					mNewTopPicHolder.image = (ImageView) convertView
							.findViewById(R.id.top_pic);
					mNewTopPicHolder.image
							.setLayoutParams(new RelativeLayout.LayoutParams(
									RelativeLayout.LayoutParams.MATCH_PARENT,
									(int) (mContext.mScreenWidth / 6.4 * 3)));
					convertView.setTag(mNewTopPicHolder);
				}
			} else {
				if (itemViewType == 0) {
					mNewsListHolder = (NewsListHolder) convertView.getTag();
				} else if (itemViewType == 1) {
					mFinishHolder = (LoadedFinishHolder) convertView.getTag();
					return convertView;
				} else if (itemViewType == 2) {
					mNewAlbumsHolder = (NewAlbumsHolder) convertView.getTag();
				} else if (itemViewType == 2) {
					mNewTopPicHolder = (NewTopPicHolder) convertView.getTag();
				}
			}
			if (itemViewType == 3) {
				mNewTopPicHolder.title.setText(mNewsHomeModule.getHeadline()
						.get(0).getTitle());
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(mNewsHomeModule.getHeadline()
								.get(0).getTitlepic()), mNewTopPicHolder.image,
						ImgUtils.homeImageOptions);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						openNews(mNewsHomeModule.getHeadline().get(0));
					}
				});
			} else {
				final NewsHomeModuleItem news = mNewsHomeModule.getList().get(
						hasTopPic ? position - 1 : position);
				if (itemViewType == 0) {
					if (NewsBrowseUtils.isBrowed(news.getId())) {
						mNewsListHolder.title.setTextColor(Color
								.parseColor("#B0B0B0"));
					} else {
						mNewsListHolder.title.setTextColor(Color
								.parseColor("#000000"));
					}
					news.setTitlepic(CommonUtils.doWebpUrl(news.getTitlepic()));
					String clicktime = news.getOnclick() + "";
					clicktime = TextUtils.isEmpty(clicktime) ? "0" : clicktime;
					mNewsListHolder.titlepic.setTag(R.string.viewwidth,
							PixelUtil.dp2px(80));
					ImgUtils.imageLoader
							.displayImage(
									CommonUtils.doWebpUrl(news.getTitlepic()),
									mNewsListHolder.titlepic,
									ImgUtils.homeImageOptions);
					mNewsListHolder.title.setText(news.getTitle());
					if (news.getType().equals("outlink")) {
						mNewsListHolder.newsTimeIcon.setVisibility(View.GONE);
						mNewsListHolder.newsClick.setVisibility(View.GONE);
					} else {
						mNewsListHolder.newsTimeIcon
								.setVisibility(View.VISIBLE);
						mNewsListHolder.newsClick.setVisibility(View.VISIBLE);
						mNewsListHolder.newsClick.setText(clicktime);
					}
					mNewsListHolder.keyboardIconContent
							.setVisibility(View.VISIBLE);
					Keyboard mKeyboard = news.getKeyboard();
					if (mKeyboard != null
							&& !mKeyboard.getColor().trim().equals("")
							&& !mKeyboard.getText().trim().equals("")) {
						mNewsListHolder.keyboardIconContent.removeAllViews();
						TextView view = new BorderTextView(mContext,
								mKeyboard.getColor());
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						view.setLayoutParams(params);
						view.setGravity(Gravity.CENTER);
						int px3 = PixelUtil.dp2px(3);
						view.setPadding(px3, px3, px3, px3);
						view.setText(mKeyboard.getText());
						FontUtils.setTextViewFontSize(mContext, view,
								R.string.live_border_text_view_text_size, 1);
						view.setTextColor(Color.parseColor(mKeyboard.getColor()));
						mNewsListHolder.keyboardIconContent.addView(view);
					}
					convertView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (ClickUtils.isFastDoubleClick()) {
								return;
							}
							MyTextView textView = (MyTextView) v
									.findViewById(R.id.home_news_title);
							textView.setTextColor(Color.parseColor("#B0B0B0"));
							NewsBrowseUtils.hasBrowedNews(news.getId());
							if (news.getType().equals("video")
									|| news.getType().equals("text")) {
								NewsListActivity.this
										.startAnimActivityByNewsHomeModuleItem(
												NewsContentActivity.class, news);
							} else if (news.getType().equals("outlink")) {
								news.setOutLinkType("outlink");
								NewsListActivity.this
										.startAnimActivityByNewsHomeModuleItem(
												NewsOutLinkActivity.class, news);
							}
						}
					});
				} else if (itemViewType == 2) {
					if (NewsBrowseUtils.isBrowed(news.getId())) {
						mNewAlbumsHolder.title.setTextColor(Color
								.parseColor("#B0B0B0"));
					} else {
						mNewAlbumsHolder.title.setTextColor(Color
								.parseColor("#000000"));
					}
					mNewAlbumsHolder.title.setText(news.getTitle());
					int width = (mScreenWidth - PixelUtil.dp2px(20) / 3);
					mNewAlbumsHolder.albums_image_1.setTag(R.string.viewwidth,
							width);
					mNewAlbumsHolder.albums_image_2.setTag(R.string.viewwidth,
							width);
					mNewAlbumsHolder.albums_image_3.setTag(R.string.viewwidth,
							width);
					ImgUtils.imageLoader.displayImage(
							CommonUtils.doWebpUrl(news.getAlbum_1()),
							mNewAlbumsHolder.albums_image_1,
							ImgUtils.homeImageOptions);
					ImgUtils.imageLoader.displayImage(
							CommonUtils.doWebpUrl(news.getAlbum_2()),
							mNewAlbumsHolder.albums_image_2,
							ImgUtils.homeImageOptions);
					ImgUtils.imageLoader.displayImage(
							CommonUtils.doWebpUrl(news.getAlbum_3()),
							mNewAlbumsHolder.albums_image_3,
							ImgUtils.homeImageOptions);
					convertView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if (ClickUtils.isFastDoubleClick()) {
								return;
							}
							MyTextView textView = (MyTextView) arg0
									.findViewById(R.id.home_albums_title);
							NewsBrowseUtils.hasBrowedNews(news.getId());
							textView.setTextColor(Color.parseColor("#B0B0B0"));
							NewsBrowseUtils.hasBrowedNews(news.getId());
							NewsListActivity.this
									.startAnimActivityByNewsHomeModuleItem(
											NewsAlbumActivity.class, news);
						}
					});
				}
			}
			return convertView;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (FontUtils.isRevelationsBreaknewsFontSizeHasChanged()) {
			changeFontSize();
			FontUtils.setRevelationsBreaknewsFontSizeHasChanged(false);
		}
		if (!spUtil.getIsDayMode())
			chage2Night();
		else
			chage2Day();
	}

	@Override
	public void changeFontSize() {
		int first = mNewsListView.getFirstVisiblePosition();
		mNewsListView.setAdapter(mNewsListAdapter);
		mNewsListView.setSelection(first);
	}

	@Override
	protected boolean initLocalData() {
		try {
			SerializableObj object = (SerializableObj) this.dbUtils
					.findFirst(Selector.from(SerializableObj.class).where(
							"classType", "=", "NewsListView" + mAppClassId));
			if (object != null) {
				if (TimeUtil.isListSaveTimeOK(object.getSaveTime())) {
					mNewsHomeModuleJson = object.getJsonStr();
					mNewsHomeModule = JsonUtils.toObject(mNewsHomeModuleJson,
							NewsHomeModule.class);
					return true;
				} else {
					this.dbUtils.delete(
							SerializableObj.class,
							WhereBuilder.b("classType", "=", "NewsListView"
									+ mAppClassId));
					return false;
				}
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
					.toString(), mNewsHomeModuleJson, "NewsListView"
					+ mAppClassId, new Date().getTime());
			this.dbUtils.delete(
					SerializableObj.class,
					WhereBuilder.b("classType", "=", "NewsListView"
							+ mAppClassId));
			this.dbUtils.save(obj);
		} catch (DbException e) {
			DebugLog.e(e.getLocalizedMessage());
		}

	}

	private void openNews(NewsHomeModuleItem moduleItem) {
		if (moduleItem.getType().equals("video")
				|| moduleItem.getType().equals("text")) {
			this.startAnimActivityByNewsHomeModuleItem(
					NewsContentActivity.class, moduleItem);
		} else if (moduleItem.getType().equals("outlink")) {
			moduleItem.setOutLinkType("outlink");
			this.startAnimActivityByNewsHomeModuleItem(
					NewsOutLinkActivity.class, moduleItem);
		} else if (moduleItem.getType().equals("album")) {
			this.startAnimActivityByNewsHomeModuleItem(NewsAlbumActivity.class,
					moduleItem);
		} else if (moduleItem.getType().equals("topic")) {
			if (moduleItem.getAppclassid() == null
					|| moduleItem.getAppclassid().trim().equals(""))
				moduleItem.setAppclassid(moduleItem.getLabels());
			if (moduleItem.getNum() > 0)
				this.startAnimActivityByNewsHomeModuleItem(
						NewsTopicActivity.class, moduleItem);
			else
				this.startAnimActivityByNewsHomeModuleItem(
						NewsTopicListActivity.class, moduleItem);
		}
	}
}
