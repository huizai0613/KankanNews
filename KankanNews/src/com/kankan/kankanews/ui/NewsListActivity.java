package com.kankan.kankanews.ui;

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
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.fragment.New_RevelationsFragment;
import com.kankan.kankanews.ui.fragment.item.New_HomeItemFragment;
import com.kankan.kankanews.ui.item.New_Activity_Content_PicSet;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.item.New_Activity_Content_Web;
import com.kankan.kankanews.ui.item.New_Avtivity_Subject;
import com.kankan.kankanews.ui.view.BorderTextView;
import com.kankan.kankanews.ui.view.EllipsizingTextView;
import com.kankan.kankanews.ui.view.EllipsizingTextView.EllipsizeListener;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
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
	private LoadedFinishHolder mFinishHolder;
	private boolean mIsLoadEnd = false;
	private String mLastTime = "";

	private String mAppClassId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_news_list);
	}

	@Override
	protected void initView() {
		initTitleLeftBar("热点新闻", R.drawable.new_ic_back);
		inflate = LayoutInflater.from(this);
		mLoadingView = this.findViewById(R.id.newslist_loading_view);
		mRetryView = this.findViewById(R.id.newslist_retry_view);
		mNewsListView = (PullToRefreshListView) this
				.findViewById(R.id.newslist_list_view);
		nightView = findViewById(R.id.night_view);
		initListView();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		mAppClassId = this.getIntent().getStringExtra(
				"_NEWS_HOME_APP_CLASS_ID_");
		boolean flag = this.initLocalDate();
		if (flag) {
			showData(true);
			mLoadingView.setVisibility(View.GONE);
			mNewsListView.showHeadLoadingView();
		}
		refreshNetDate();
	}

	protected void initListView() {
		// TODO Auto-generated method stub
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

	protected void refreshNetDate() {
		// TODO Auto-generated method stub
		mIsLoadEnd = false;
		if (CommonUtils.isNetworkAvailable(this)) {
			this.netUtils.getNewsList(mAppClassId, "", this.mListener,
					mErrorListener);
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mNewsListView.onRefreshComplete();
				}
			}, 500);
		}
	}

	protected void loadMoreNetDate() {
		// TODO Auto-generated method stub
		if (mIsLoadEnd || !CommonUtils.isNetworkAvailable(this)) {
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
		// TODO Auto-generated method stub
		mNewsHomeModuleJson = jsonObject.toString();
		boolean needRefresh = (mNewsHomeModuleJson == null);
		// ToastUtils.Infotoast(getActivity(), jsonObject.toString());
		mNewsHomeModule = JsonUtils.toObject(mNewsHomeModuleJson,
				NewsHomeModule.class);
		if (mNewsHomeModule != null) {
			mLoadingView.setVisibility(View.GONE);
			if (mNewsHomeModule.getList().size() == 0)
				mIsLoadEnd = true;
			saveLocalDate();
			showData(needRefresh);
		}
	}

	private void showData(boolean needRefresh) {
		mNewsListView.onRefreshComplete();
		if (mNewsListAdapter == null) {
			mNewsListAdapter = new NewsListAdapter();
			mNewsListView.setAdapter(mNewsListAdapter);
		} else {
			mNewsListAdapter.notifyDataSetChanged();
		}
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
	}

	private class LoadedFinishHolder {
		MyTextView loadedTextView;
	}

	private class NewsListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mIsLoadEnd)
				return mNewsHomeModule.getList().size() + 1;
			return mNewsHomeModule.getList().size();
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == mNewsHomeModule.getList().size()) {
				return 1;
			} else
				return 0;
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

			if (convertView == null) {
				if (itemViewType == 0) {
					convertView = inflate.inflate(R.layout.new_home_news_item,
							null);
					mNewsListHolder = new NewsListHolder();
					mNewsListHolder.titlepic = (ImageView) convertView
							.findViewById(R.id.home_news_titlepic);
					mNewsListHolder.title = (MyTextView) convertView
							.findViewById(R.id.home_news_title);
					FontUtils.setTextViewFontSize(NewsListActivity.this,
							mNewsListHolder.title,
							R.string.home_news_title_text_size,
							spUtil.getFontSizeRadix());
					mNewsListHolder.newsTimeIcon = (ImageView) convertView
							.findViewById(R.id.home_news_newstime_sign);
					mNewsListHolder.newsClick = (MyTextView) convertView
							.findViewById(R.id.home_news_newstime);
					mNewsListHolder.newsType = (ImageView) convertView
							.findViewById(R.id.home_news_newstype);
					convertView.setTag(mNewsListHolder);
				} else if (itemViewType == 1) {
					convertView = inflate.inflate(R.layout.item_list_foot_text,
							null);
					mFinishHolder = new LoadedFinishHolder();
					mFinishHolder.loadedTextView = (MyTextView) convertView
							.findViewById(R.id.list_has_loaded_item_textview);
					convertView.setTag(mFinishHolder);
				}
			} else {
				if (itemViewType == 0) {
					mNewsListHolder = (NewsListHolder) convertView.getTag();
				} else if (itemViewType == 1) {
					mFinishHolder = (LoadedFinishHolder) convertView.getTag();
				}
			}
			if (itemViewType == 0) {
				final NewsHomeModuleItem news = mNewsHomeModule.getList().get(
						position);
				news.setTitlepic(CommonUtils.doWebpUrl(news.getTitlepic()));
				String clicktime = news.getOnclick() + "";
				clicktime = TextUtils.isEmpty(clicktime) ? "0" : clicktime;
//				final int news_type = Integer.valueOf(news.getType());
				mNewsListHolder.titlepic.setTag(R.string.viewwidth,
						PixelUtil.dp2px(80));

				ImgUtils.imageLoader.displayImage(news.getTitlepic(),
						mNewsListHolder.titlepic, ImgUtils.homeImageOptions);

				mNewsListHolder.title.setText(news.getTitle());
				mNewsListHolder.newsClick.setText(clicktime);
			}
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
		// TODO Auto-generated method stub
		int first = mNewsListView.getFirstVisiblePosition();
		mNewsListView.setAdapter(mNewsListAdapter);
		mNewsListView.setSelection(first);
	}

	@Override
	protected boolean initLocalDate() {
		try {
			SerializableObj object = (SerializableObj) this.dbUtils
					.findFirst(Selector.from(SerializableObj.class).where(
							"classType", "=", "NewsListView" + mAppClassId));
			if (object != null) {
				mNewsHomeModuleJson = object.getJsonStr();
				mNewsHomeModule = JsonUtils.toObject(mNewsHomeModuleJson,
						NewsHomeModule.class);
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
					.toString(), mNewsHomeModuleJson, "NewsListView"
					+ mAppClassId);
			this.dbUtils.delete(
					SerializableObj.class,
					WhereBuilder.b("classType", "=", "NewsListView"
							+ mAppClassId));
			this.dbUtils.save(obj);
		} catch (DbException e) {
			DebugLog.e(e.getLocalizedMessage());
		}

	}
}
