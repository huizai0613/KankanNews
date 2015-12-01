package com.kankan.kankanews.ui.item;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.iss.view.pulltorefresh.PullToRefreshPinnedSectionListView;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.bean.Keyboard;
import com.kankan.kankanews.bean.NewsHomeModule;
import com.kankan.kankanews.bean.NewsHomeModuleItem;
import com.kankan.kankanews.bean.SerializableObj;
import com.kankan.kankanews.bean.Subject_Item;
import com.kankan.kankanews.photoview.PhotoView;
import com.kankan.kankanews.ui.MainActivity;
import com.kankan.kankanews.ui.view.BorderTextView;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.popup.CustomShareBoard;
import com.kankan.kankanews.ui.view.popup.FontColumsBoard;
import com.kankan.kankanews.utils.ClickUtils;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankan.kankanews.utils.NetUtils;
import com.kankan.kankanews.utils.NewsBrowseUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.sso.UMSsoHandler;

public class NewsTopicListActivity extends BaseActivity implements
		OnClickListener {

	// 分享类
	private ShareUtil shareUtil;

	private NewsHomeModuleItem mHomeModuleItem;

	private NewsHomeModule mTopicListModule;
	private String mTopicListModuleJson;

	private PullToRefreshListView mTopicListListView;

	private TopicListAdapter mTopicListAdapter;

	// 加载
	private RelativeLayout mLoadingView;
	// 重试
	private LinearLayout mRetryView;

	private View nightView;

	private TopicHeaderHolder mTopicHeaderHolder;

	private NewContentHolder mNewContentHolder;

	private NewAlbumsHolder mNewAlbumsHolder;
	private LoadedFinishHolder mLoadedFinishHolder;
	private String mLastTime = "";
	private boolean mIsLoadEnd = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_list);
	}

	@Override
	protected void initView() {
		mLoadingView = (RelativeLayout) findViewById(R.id.content_loading);
		mRetryView = (LinearLayout) findViewById(R.id.main_bg);

		nightView = findViewById(R.id.night_view);

		mTopicListListView = (PullToRefreshListView) findViewById(R.id.topiclist_list_view);
		initListView();
		// TODO

		initTitleBarIcon(R.drawable.ic_share, R.drawable.new_ic_back,
				R.drawable.ic_close_white, R.drawable.ic_font,
				R.drawable.ic_refresh);

	}

	protected void initListView() {
		mTopicListListView.setMode(Mode.BOTH);
		mTopicListListView.getLoadingLayoutProxy(true, false).setPullLabel(
				"下拉可以刷新");
		mTopicListListView.getLoadingLayoutProxy(true, false).setReleaseLabel(
				"释放后刷新");
		mTopicListListView.getLoadingLayoutProxy(false, true).setPullLabel(
				"上拉加载更多");
		mTopicListListView.getLoadingLayoutProxy(false, true)
				.setRefreshingLabel("刷新中…");
		mTopicListListView.getLoadingLayoutProxy(false, true).setReleaseLabel(
				"松开立即加载");
		mTopicListListView
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

	private void refreshNetDate() {
		mIsLoadEnd = false;
		if (CommonUtils.isNetworkAvailable(this)) {
			netUtils.getNewsList(mHomeModuleItem.getAppclassid(), mLastTime,
					mListener, mErrorListener);
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mTopicListListView.onRefreshComplete();
				}
			}, 500);
		}
	}

	protected void loadMoreNetDate() {
		// TODO Auto-generated method stub
		if (mIsLoadEnd || !CommonUtils.isNetworkAvailable(this)) {
			mTopicListListView.postDelayed(new Runnable() {
				@Override
				public void run() {
					mTopicListListView.onRefreshComplete();
				}
			}, 300);
			return;
		}
		this.netUtils.getNewsList(
				mHomeModuleItem.getAppclassid(),
				mTopicListModule.getList()
						.get(mTopicListModule.getList().size() - 1)
						.getNewstime(), new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject jsonObject) {
						mTopicListListView.onRefreshComplete();
						NewsHomeModule more = JsonUtils.toObject(
								jsonObject.toString(), NewsHomeModule.class);
						if (more.getList().size() == 0) {
							mIsLoadEnd = true;
						} else {
							mIsLoadEnd = false;
							mTopicListModule.getList().addAll(more.getList());
						}
						mTopicListAdapter.notifyDataSetChanged();
					}
				}, mErrorListener);
	}

	@Override
	protected void initData() {
		// TODO
		mHomeModuleItem = (NewsHomeModuleItem) this.getIntent()
				.getSerializableExtra("_NEWS_HOME_MODEULE_ITEM_");

		NetUtils.getInstance(mContext).getAnalyse(this, "module",
				mHomeModuleItem.getTitle(), mHomeModuleItem.getTitleurl());
		boolean _flag = this.initLocalData();
		if (_flag) {
			showData();
		} else {
			refreshNetDate();
		}
	}

	@Override
	protected void setListener() {
		mRetryView.setOnClickListener(this);
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);
		setOnContentClickLinester(this);
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		mTopicListModuleJson = jsonObject.toString();
		mTopicListModule = JsonUtils.toObject(mTopicListModuleJson,
				NewsHomeModule.class);
		if (mTopicListModule != null) {
			if (mTopicListModule.getList().size() == 0)
				mIsLoadEnd = true;
			saveLocalDate();
			showData();
		}
	}

	@Override
	protected void saveLocalDate() {
		try {
			SerializableObj obj = new SerializableObj(UUID.randomUUID()
					.toString(), mTopicListModuleJson, "NewsTopicList"
					+ mHomeModuleItem.getAppclassid(), new Date().getTime());
			this.dbUtils.delete(
					SerializableObj.class,
					WhereBuilder.b("classType", "=", "NewsTopicList"
							+ mHomeModuleItem.getAppclassid()));
			this.dbUtils.save(obj);
		} catch (DbException e) {
			DebugLog.e(e.getLocalizedMessage());
		}
	}

	@Override
	protected boolean initLocalData() {
		try {
			SerializableObj object = (SerializableObj) this.dbUtils
					.findFirst(Selector.from(SerializableObj.class).where(
							"classType", "=",
							"NewsTopicList" + mHomeModuleItem.getAppclassid()));
			if (object != null) {
				if (TimeUtil.isListSaveTimeOK(object.getSaveTime())) {
					mTopicListModuleJson = object.getJsonStr();
					mTopicListModule = JsonUtils.toObject(mTopicListModuleJson,
							NewsHomeModule.class);
					return true;
				} else {
					this.dbUtils.delete(
							SerializableObj.class,
							WhereBuilder.b("classType", "=", "NewsTopicList"
									+ mHomeModuleItem.getAppclassid()));
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

	private void showData() {
		mTopicListListView.onRefreshComplete();
		if (mTopicListAdapter == null) {
			mTopicListAdapter = new TopicListAdapter(this);
			mTopicListListView.setAdapter(mTopicListAdapter);
		} else {
			mTopicListAdapter.notifyDataSetChanged();
		}
		mLoadingView.setVisibility(View.GONE);
		mRetryView.setVisibility(View.GONE);
	}

	@Override
	protected void onFailure(VolleyError error) {
		ToastUtils.Errortoast(mContext, "请求失败,请重试");
		if (mTopicListModule == null) {
			mRetryView.setVisibility(View.VISIBLE);
		}
		mLoadingView.setVisibility(View.GONE);
	}

	private class TopicHeaderHolder {
		ImageView titlePic;
		TextView intro;
	}

	private class NewContentHolder {
		ImageView titlepic;
		MyTextView title;
		ImageView newstime_sign;
		ImageView home_news_play;
		ImageView newsTimeIcon;
		MyTextView newstime;
		ImageView news_type;
		LinearLayout keyboardIconContent;
	}

	private class NewAlbumsHolder {
		MyTextView title;
		LinearLayout home_albums_imgs_layout;
		ImageView albums_image_1;
		ImageView albums_image_2;
		ImageView albums_image_3;
	}

	private class LoadedFinishHolder {
		MyTextView loadedTextView;
	}

	// 自定义适配器
	private class TopicListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public TopicListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			DebugLog.e(mIsLoadEnd + "");
			if (mIsLoadEnd)
				return mTopicListModule.getList().size() + 2;
			return mTopicListModule.getList().size() + 1;
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
		public int getItemViewType(int position) {
			if (position == mTopicListModule.getList().size() + 1) {
				return 3;
			}
			if (position != 0) {
				String type = mTopicListModule.getList().get(position - 1)
						.getType();
				if (type.equals("album")) {
					return 2;
				} else {
					return 1;
				}
			}
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 4;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int itemViewType = getItemViewType(position);

			if (convertView == null) {
				if (itemViewType == 0) {
					convertView = mInflater.inflate(R.layout.item_topic_header,
							parent, false);
					mTopicHeaderHolder = new TopicHeaderHolder();
					mTopicHeaderHolder.titlePic = (ImageView) convertView
							.findViewById(R.id.titlepic);
					mTopicHeaderHolder.intro = (TextView) convertView
							.findViewById(R.id.intro);

					mTopicHeaderHolder.titlePic.getLayoutParams().height = (int) (mScreenWidth / 3.2);
					convertView.setTag(mTopicHeaderHolder);
				} else if (itemViewType == 1) {
					convertView = mInflater.inflate(
							R.layout.item_news_list_content, parent, false);
					mNewContentHolder = new NewContentHolder();
					mNewContentHolder.titlepic = (ImageView) convertView
							.findViewById(R.id.home_news_titlepic);
					mNewContentHolder.title = (MyTextView) convertView
							.findViewById(R.id.home_news_title);
					FontUtils.setTextViewFontSize(NewsTopicListActivity.this,
							mNewContentHolder.title,
							R.string.home_news_text_size,
							spUtil.getFontSizeRadix());
					mNewContentHolder.newstime = (MyTextView) convertView
							.findViewById(R.id.home_news_newstime);
					mNewContentHolder.newsTimeIcon = (ImageView) convertView
							.findViewById(R.id.home_news_newstime_sign);
					mNewContentHolder.news_type = (ImageView) convertView
							.findViewById(R.id.home_news_newstype);
					mNewContentHolder.home_news_play = (ImageView) convertView
							.findViewById(R.id.home_news_play);
					mNewContentHolder.keyboardIconContent = (LinearLayout) convertView
							.findViewById(R.id.news_list_keyboard_content);
					convertView.setTag(mNewContentHolder);
				} else if (itemViewType == 2) {
					convertView = mInflater.inflate(
							R.layout.new_home_news_albums_item, parent, false);
					mNewAlbumsHolder = new NewAlbumsHolder();
					mNewAlbumsHolder.title = (MyTextView) convertView
							.findViewById(R.id.home_albums_title);
					FontUtils.setTextViewFontSize(NewsTopicListActivity.this,
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
					convertView = mInflater.inflate(
							R.layout.item_list_foot_text, null);
					mLoadedFinishHolder = new LoadedFinishHolder();
					mLoadedFinishHolder.loadedTextView = (MyTextView) convertView
							.findViewById(R.id.list_has_loaded_item_textview);
					convertView.setTag(mLoadedFinishHolder);
					return convertView;
				}
			} else {
				if (itemViewType == 1) {
					mNewContentHolder = (NewContentHolder) convertView.getTag();
				} else if (itemViewType == 2) {
					mNewAlbumsHolder = (NewAlbumsHolder) convertView.getTag();
				} else if (itemViewType == 3) {
					mLoadedFinishHolder = (LoadedFinishHolder) convertView
							.getTag();
				}
			}

			if (itemViewType == 0) {
				mTopicHeaderHolder.intro.setText("         "
						+ mTopicListModule.getIntro());
				FontUtils.setTextViewFontSize(NewsTopicListActivity.this,
						mTopicHeaderHolder.intro,
						R.string.news_content_text_size,
						spUtil.getFontSizeRadix());
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(mTopicListModule.getTitlepic()),
						mTopicHeaderHolder.titlePic, ImgUtils.homeImageOptions);
			} else if (itemViewType == 1) {
				final NewsHomeModuleItem item = mTopicListModule.getList().get(
						position - 1);
				item.setTitlepic(CommonUtils.doWebpUrl(item.getTitlepic()));
				if (NewsBrowseUtils.isBrowed(item.getId())) {
					mNewContentHolder.title.setTextColor(Color
							.parseColor("#B0B0B0"));
				} else {
					mNewContentHolder.title.setTextColor(Color
							.parseColor("#000000"));
				}
				mNewContentHolder.titlepic.setTag(R.string.viewwidth,
						PixelUtil.dp2px(80));
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(item.getTitlepic()),
						mNewContentHolder.titlepic, ImgUtils.homeImageOptions);
				mNewContentHolder.title.setText(item.getTitle());
				mNewContentHolder.keyboardIconContent
						.setVisibility(View.VISIBLE);
				if (item.getType().equals("outlink")) {
					mNewContentHolder.newsTimeIcon.setVisibility(View.GONE);
					mNewContentHolder.newstime.setVisibility(View.GONE);
				} else {
					mNewContentHolder.newsTimeIcon.setVisibility(View.VISIBLE);
					mNewContentHolder.newstime.setVisibility(View.VISIBLE);
					mNewContentHolder.newstime.setText(item.getOnclick() + "");
				}
				Keyboard mKeyboard = item.getKeyboard();
				if (mKeyboard != null
						&& !mKeyboard.getColor().trim().equals("")
						&& !mKeyboard.getText().trim().equals("")) {
					mNewContentHolder.keyboardIconContent.removeAllViews();
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
					mNewContentHolder.keyboardIconContent.addView(view);
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
						NewsBrowseUtils.hasBrowedNews(item.getId());
						if (item.getType().equals("video")
								|| item.getType().equals("text")) {
							NewsTopicListActivity.this
									.startAnimActivityByNewsHomeModuleItem(
											NewsContentActivity.class, item);
						} else if (item.getType().equals("outlink")) {
							item.setOutLinkType("outlink");
							NewsTopicListActivity.this
									.startAnimActivityByNewsHomeModuleItem(
											NewsOutLinkActivity.class, item);
						}
					}
				});
			} else if (itemViewType == 2) {
				final NewsHomeModuleItem item = mTopicListModule.getList().get(
						position - 1);
				if (NewsBrowseUtils.isBrowed(item.getId())) {
					mNewAlbumsHolder.title.setTextColor(Color
							.parseColor("#B0B0B0"));
				} else {
					mNewAlbumsHolder.title.setTextColor(Color
							.parseColor("#000000"));
				}
				mNewAlbumsHolder.title.setText(item.getTitle());
				// final String[] pics = item.getTitlepic().split("::::::");
				// ArrayList<ImageView> image_view_list = new
				// ArrayList<ImageView>();

				int width = (mScreenWidth - PixelUtil.dp2px(20) / 3);
				mNewAlbumsHolder.albums_image_1.setTag(R.string.viewwidth,
						width);
				mNewAlbumsHolder.albums_image_2.setTag(R.string.viewwidth,
						width);
				mNewAlbumsHolder.albums_image_3.setTag(R.string.viewwidth,
						width);
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(item.getAlbum_1()),
						mNewAlbumsHolder.albums_image_1,
						ImgUtils.homeImageOptions);
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(item.getAlbum_2()),
						mNewAlbumsHolder.albums_image_2,
						ImgUtils.homeImageOptions);
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(item.getAlbum_3()),
						mNewAlbumsHolder.albums_image_3,
						ImgUtils.homeImageOptions);
				// image_view_list.add(mNewAlbumsHolder.albums_image_1);
				// image_view_list.add(mNewAlbumsHolder.albums_image_2);
				// image_view_list.add(mNewAlbumsHolder.albums_image_3);
				// for (int i = 0; i < (pics.length > 3 ? 3 : pics.length); i++)
				// {
				// ImgUtils.imageLoader.displayImage(
				// CommonUtils.doWebpUrl(pics[i + 1]),
				// image_view_list.get(i), ImgUtils.homeImageOptions);
				// }

				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (ClickUtils.isFastDoubleClick()) {
							return;
						}
						MyTextView textView = (MyTextView) arg0
								.findViewById(R.id.home_albums_title);
						NewsBrowseUtils.hasBrowedNews(item.getId());
						textView.setTextColor(Color.parseColor("#B0B0B0"));
						NewsBrowseUtils.hasBrowedNews(item.getId());
						NewsTopicListActivity.this
								.startAnimActivityByNewsHomeModuleItem(
										NewsAlbumActivity.class, item);
					}
				});
			}
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		case R.id.title_bar_content_img:
			shareUtil = new ShareUtil(mTopicListModule, mContext);
			CustomShareBoard shareBoard = new CustomShareBoard(this, shareUtil,
					this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.title_bar_right_second_img:
			this.refresh();
			break;
		case R.id.title_bar_right_img:
			FontColumsBoard fontBoard = new FontColumsBoard(this);
			fontBoard.setAnimationStyle(R.style.popwin_anim_style);
			fontBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.main_bg:
			if (CommonUtils.isNetworkAvailable(mContext)) {
				refreshNetDate();
			}
			break;
		}
	}

	@Override
	public void refresh() {
		// mLoadingView.setVisibility(View.VISIBLE);
		// if (CommonUtils.isNetworkAvailable(mContext)) {
		// refreshNetDate();
		// } else {
		// if (mTopicListModule != null)
		// mLoadingView.setVisibility(View.GONE);
		// }
		this.mTopicListListView.setSelection(0);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mTopicListListView.setmCurrentMode(Mode.PULL_FROM_START);
				mTopicListListView.setRefreshing(false);
			}
		}, 100);
	}

	@Override
	public void finish() {
		if (this.mApplication.getMainActivity() == null) {
			Intent intent = getIntent();
			intent.setClass(this, MainActivity.class);
			this.startActivity(intent);
			overridePendingTransition(R.anim.alpha_in, R.anim.out_to_right);
		}
		super.finish();
	}

	@Override
	public void chage2Day() {
		nightView.setVisibility(View.GONE);
		((CrashApplication) this.getApplication()).changeMainActivityDayMode();
	}

	@Override
	public void chage2Night() {
		nightView.setVisibility(View.VISIBLE);
		((CrashApplication) this.getApplication()).changeMainActivityDayMode();
	}

	@Override
	public void copy2Clip() {
		// TODO Auto-generated method stub
		ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		// clip.setText(mTopicModule.get);
		ToastUtils.Infotoast(this, "已将链接复制进黏贴板");
	}

	@Override
	public void changeFontSize() {
		int first = mTopicListListView.getFirstVisiblePosition();
		mTopicListListView.setAdapter(mTopicListAdapter);
		mTopicListListView.setSelection(first);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (FontUtils.isSubjectFontSizeHasChanged()) {
			changeFontSize();
			FontUtils.setSubjectFontSizeHasChanged(false);
		}
		if (!spUtil.getIsDayMode())
			chage2Night();
		else
			chage2Day();
	}

	@Override
	public void initNightView(boolean isFullScreen) {
		if (!spUtil.getIsDayMode())
			chage2Night();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */

		UMSsoHandler ssoHandler = shareUtil.getmController().getConfig()
				.getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
}
