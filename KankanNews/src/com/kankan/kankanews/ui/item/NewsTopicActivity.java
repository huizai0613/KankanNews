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
import com.iss.view.pulltorefresh.PullToRefreshPinnedSectionListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.bean.Keyboard;
import com.kankan.kankanews.bean.NewsAlbum;
import com.kankan.kankanews.bean.NewsHome;
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

public class NewsTopicActivity extends BaseActivity implements
		AdapterView.OnItemClickListener,
		StickyListHeadersListView.OnHeaderClickListener,
		StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
		StickyListHeadersListView.OnStickyHeaderChangedListener,
		OnClickListener {

	// 分享类
	private ShareUtil shareUtil;

	private TextView mTitleText;
	private boolean fadeHeader = true;

	private NewsHomeModuleItem mHomeModuleItem;

	private NewsHomeModule mTopicModule;

	private JSONObject mTopicModuleJson;

	private StickyListHeadersListView stickyList;

	private View headerView;

	private TopicAdapter mTopicAdapter;

	// 加载
	private RelativeLayout mLoadingView;
	// 重试
	private LinearLayout mRetryView;

	private View nightView;

	private String[] categorys;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic);
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

	@Override
	protected void initView() {

		mLoadingView = (RelativeLayout) findViewById(R.id.content_loading);
		mRetryView = (LinearLayout) findViewById(R.id.main_bg);

		nightView = findViewById(R.id.night_view);

		// title = intent.getStringExtra("title");
		// titlepic = intent.getStringExtra("titlepic");
		// sharedPic = intent.getStringExtra("sharedPic");
		// titleurl = intent.getStringExtra("titleurl");
		// intro = intent.getStringExtra("intro");
		//
		// new_news = new New_News();
		//
		// new_news.setTitlelist(title);
		// if (sharedPic == null || sharedPic.trim().equals(""))
		// new_news.setSharedPic(titlepic);
		// else
		// new_news.setSharedPic(sharedPic);
		// new_news.setTitleurl(titleurl);
		// new_news.setIntro(intro);
		//
		// NetUtils.getInstance(mContext).getAnalyse(this, "topic",
		// new_news.getTitlelist(), new_news.getTitleurl());

		initTitleBarIcon(R.drawable.ic_share, R.drawable.new_ic_back,
				R.drawable.ic_close_white, R.drawable.ic_font,
				R.drawable.ic_refresh);

		// com_title_bar_right_bt.setVisibility(View.GONE);

		// mTopicAdapter = new TopicAdapter(this);

		headerView = getLayoutInflater().inflate(R.layout.item_topic_header,
				null);

		stickyList = (StickyListHeadersListView) findViewById(R.id.list);
		// stickyList.setVerticalScrollBarEnabled(false);
		stickyList.setOnItemClickListener(this);
		// stickyList.setOnHeaderClickListener(this);
		// stickyList.setOnStickyHeaderChangedListener(this);
		// stickyList.setOnStickyHeaderOffsetChangedListener(this);
		stickyList.addHeaderView(headerView);
		// stickyList.setDrawingListUnderStickyHeader(false);
		// stickyList.setAreHeadersSticky(true);
		// stickyList.setAdapter(mTopicAdapter);

		mTitleText = (MyTextView) headerView.findViewById(R.id.intro);
		FontUtils.setTextViewFontSize(this, mTitleText,
				R.string.news_content_text_size, spUtil.getFontSizeRadix());

	}

	protected boolean initLocalData() {
		try {
			DebugLog.e(mHomeModuleItem.getAppclassid());
			SerializableObj object = (SerializableObj) this.dbUtils
					.findFirst(Selector.from(SerializableObj.class).where(
							"classType", "=",
							"NewsTopic" + mHomeModuleItem.getAppclassid()));
			if (object != null) {
				if (TimeUtil.isListSaveTimeOK(object.getSaveTime())) {
					mTopicModuleJson = new JSONObject(object.getJsonStr());
					mTopicModule = JsonUtils.toObject(
							mTopicModuleJson.toString(), NewsHomeModule.class);
					return true;
				} else {
					this.dbUtils.delete(
							SerializableObj.class,
							WhereBuilder.b("classType", "=", "NewsTopic"
									+ mHomeModuleItem.getAppclassid()));
					return false;
				}
			} else {
				return false;
			}
		} catch (DbException e) {
			DebugLog.e(e.getLocalizedMessage());
		} catch (JSONException e) {
			DebugLog.e(e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	protected void saveLocalDate() {
		try {
			SerializableObj obj = new SerializableObj(UUID.randomUUID()
					.toString(), mTopicModuleJson.toString(), "NewsTopic"
					+ mTopicModule.getAppclassid(), new Date().getTime());
			this.dbUtils.delete(
					SerializableObj.class,
					WhereBuilder.b("classType", "=",
							"NewsTopic" + mTopicModule.getAppclassid()));
			this.dbUtils.save(obj);
		} catch (DbException e) {
			DebugLog.e(e.getLocalizedMessage());
		}
	}

	private void initNetDate() {
		netUtils.getTopicData(mHomeModuleItem.getAppclassid(), mListener,
				mErrorListener);
	}

	@Override
	protected void initData() {
		Intent intent = getIntent();
		mHomeModuleItem = (NewsHomeModuleItem) intent
				.getSerializableExtra("_NEWS_HOME_MODEULE_ITEM_");
		this.mLoadingView.setVisibility(View.VISIBLE);
		boolean _flag = initLocalData();
		if (_flag) {
			showData();
			shareUtil = new ShareUtil(mTopicModule, mContext);
		} else {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				initNetDate();
			} else {
				if (!_flag) {
					this.mRetryView.setVisibility(View.VISIBLE);
				}
			}
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
		mTopicModuleJson = jsonObject;
		mTopicModule = JsonUtils.toObject(mTopicModuleJson.toString(),
				NewsHomeModule.class);
		if (!mTopicModule.getAppclassid().trim().equals("")) {
			// 初始化shareutil类
			shareUtil = new ShareUtil(mTopicModule, mContext);
			this.saveLocalDate();
			showData();
		} else {
			if (mTopicModule == null)
				this.mRetryView.setVisibility(View.VISIBLE);
			else
				shareUtil = new ShareUtil(mTopicModule, mContext);
		}
	}

	private void showData() {
		try {
			categorys = mTopicModule.getCategory().split(",");
			mTopicModule.setList(new ArrayList<NewsHomeModuleItem>());
			for (int i = 0; i < categorys.length; i++) {
				List<NewsHomeModuleItem> list;
				list = JsonUtils.toObjectByType(
						mTopicModuleJson.get(categorys[i]).toString(),
						new TypeToken<List<NewsHomeModuleItem>>() {
						}.getType());
				for (NewsHomeModuleItem newsHomeModuleItem : list) {
					newsHomeModuleItem.setCategory(i + "");
				}
				mTopicModule.getList().addAll(list);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ImageView photoView = (ImageView) headerView
				.findViewById(R.id.titlepic);
		photoView.getLayoutParams().height = (int) (mScreenWidth / 3.2);
		ImgUtils.imageLoader.displayImage(
				CommonUtils.doWebpUrl(mTopicModule.getTitlepic()), photoView,
				ImgUtils.homeImageOptions);
		mTitleText.setText("         " + mTopicModule.getIntro());
		if (mTopicAdapter == null) {
			mTopicAdapter = new TopicAdapter(this);
			stickyList.setAdapter(mTopicAdapter);
		} else {
			mTopicAdapter.notifyDataSetChanged();
		}
		mLoadingView.setVisibility(View.GONE);
		mRetryView.setVisibility(View.GONE);
	}

	@Override
	protected void onFailure(VolleyError error) {
		ToastUtils.Errortoast(mContext, "请求失败,请重试");
		if (mTopicAdapter == null) {
			mRetryView.setVisibility(View.VISIBLE);
		}
		mLoadingView.setVisibility(View.GONE);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	View.OnClickListener buttonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public void onHeaderClick(StickyListHeadersListView l, View header,
			int itemPosition, long headerId, boolean currentlySticky) {
		// Toast.makeText(this, "Header " + headerId + " currentlySticky ? " +
		// currentlySticky, Toast.LENGTH_SHORT).show();
	}

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onStickyHeaderOffsetChanged(StickyListHeadersListView l,
			View header, int offset) {
		if (fadeHeader
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			header.setAlpha(1 - (offset / (float) header.getMeasuredHeight()));
		}
	}

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onStickyHeaderChanged(StickyListHeadersListView l, View header,
			int itemPosition, long headerId) {
		header.setAlpha(1);
	}

	// 自定义适配器
	private class TopicAdapter extends BaseAdapter implements
			StickyListHeadersAdapter, SectionIndexer {

		private LayoutInflater mInflater;

		public TopicAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mTopicModule.getList().size();
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
			String type = mTopicModule.getList().get(position).getType();
			if (type.equals("album")) {
				return 3;
			} else {
				return 1;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 4;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NewContentHolder newHolder = null;
			NewAlbumsHolder albumsHolder = null;

			int itemViewType = getItemViewType(position);

			if (convertView == null) {
				if (itemViewType == 1) {
					convertView = mInflater.inflate(
							R.layout.item_news_list_content, parent, false);
					newHolder = new NewContentHolder();
					newHolder.titlepic = (ImageView) convertView
							.findViewById(R.id.home_news_titlepic);

					newHolder.title = (MyTextView) convertView
							.findViewById(R.id.home_news_title);
					FontUtils.setTextViewFontSize(NewsTopicActivity.this,
							newHolder.title, R.string.home_news_text_size,
							spUtil.getFontSizeRadix());
					newHolder.newstime = (MyTextView) convertView
							.findViewById(R.id.home_news_newstime);
					newHolder.newsTimeIcon = (ImageView) convertView
							.findViewById(R.id.home_news_newstime_sign);
					newHolder.news_type = (ImageView) convertView
							.findViewById(R.id.home_news_newstype);
					newHolder.home_news_play = (ImageView) convertView
							.findViewById(R.id.home_news_play);
					newHolder.keyboardIconContent = (LinearLayout) convertView
							.findViewById(R.id.news_list_keyboard_content);
					convertView.setTag(newHolder);
				} else if (itemViewType == 3) {
					convertView = mInflater.inflate(
							R.layout.new_home_news_albums_item, parent, false);
					albumsHolder = new NewAlbumsHolder();
					albumsHolder.title = (MyTextView) convertView
							.findViewById(R.id.home_albums_title);
					FontUtils.setTextViewFontSize(NewsTopicActivity.this,
							albumsHolder.title, R.string.home_news_text_size,
							spUtil.getFontSizeRadix());
					albumsHolder.home_albums_imgs_layout = (LinearLayout) convertView
							.findViewById(R.id.home_albums_imgs_layout);
					albumsHolder.albums_image_1 = (ImageView) convertView
							.findViewById(R.id.home_albums_img_1);
					albumsHolder.albums_image_2 = (ImageView) convertView
							.findViewById(R.id.home_albums_img_2);
					albumsHolder.albums_image_3 = (ImageView) convertView
							.findViewById(R.id.home_albums_img_3);
					albumsHolder.home_albums_imgs_layout
							.setLayoutParams(new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.MATCH_PARENT,
									(int) ((mScreenWidth - PixelUtil
											.dp2px(10 * 4)) / 3 * 0.7)));

					convertView.setTag(albumsHolder);
				}
			} else {
				if (itemViewType == 1) {
					newHolder = (NewContentHolder) convertView.getTag();
				} else if (itemViewType == 3) {
					albumsHolder = (NewAlbumsHolder) convertView.getTag();
				}
			}

			final NewsHomeModuleItem item = mTopicModule.getList()
					.get(position);
			if (itemViewType == 1) {
				newHolder.keyboardIconContent.setVisibility(View.VISIBLE);
				Keyboard mKeyboard = item.getKeyboard();
				if (mKeyboard != null
						&& !mKeyboard.getColor().trim().equals("")
						&& !mKeyboard.getText().trim().equals("")) {
					newHolder.keyboardIconContent.removeAllViews();
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
					newHolder.keyboardIconContent.addView(view);
				}
				item.setTitlepic(CommonUtils.doWebpUrl(item.getTitlepic()));
				if (NewsBrowseUtils.isBrowed(item.getId())) {
					newHolder.title.setTextColor(Color.parseColor("#B0B0B0"));
				} else {
					newHolder.title.setTextColor(Color.parseColor("#000000"));
				}
				newHolder.titlepic.setTag(R.string.viewwidth,
						PixelUtil.dp2px(80));
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(item.getTitlepic()),
						newHolder.titlepic, ImgUtils.homeImageOptions);
				newHolder.title.setText(item.getTitle());
				if (item.getType().equals("outlink")) {
					newHolder.newsTimeIcon.setVisibility(View.GONE);
					newHolder.newstime.setVisibility(View.GONE);
				} else {
					newHolder.newsTimeIcon.setVisibility(View.VISIBLE);
					newHolder.newstime.setVisibility(View.VISIBLE);
					newHolder.newstime.setText(item.getOnclick() + "");
				}
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (ClickUtils.isFastDoubleClick()) {
							return;
						}
						MyTextView textView = (MyTextView) v
								.findViewById(R.id.home_news_title);
						textView.setTextColor(Color.parseColor("#B0B0B0"));
						NewsBrowseUtils.hasBrowedNews(item.getId());
						if (item.getType().equals("video")
								|| item.getType().equals("text")) {
							NewsTopicActivity.this
									.startAnimActivityByNewsHomeModuleItem(
											NewsContentActivity.class, item);
						} else if (item.getType().equals("outlink")) {
							NewsTopicActivity.this
									.startAnimActivityByNewsHomeModuleItem(
											NewsOutLinkActivity.class, item);
						}
					}
				});
			} else if (itemViewType == 3) {
				if (NewsBrowseUtils.isBrowed(item.getId())) {
					albumsHolder.title
							.setTextColor(Color.parseColor("#B0B0B0"));
				} else {
					albumsHolder.title
							.setTextColor(Color.parseColor("#000000"));
				}

				albumsHolder.title.setText(item.getTitle());

				int width = (mScreenWidth - PixelUtil.dp2px(20) / 3);
				albumsHolder.albums_image_1.setTag(R.string.viewwidth, width);
				albumsHolder.albums_image_2.setTag(R.string.viewwidth, width);
				albumsHolder.albums_image_3.setTag(R.string.viewwidth, width);
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(item.getAlbum_1()),
						albumsHolder.albums_image_1, ImgUtils.homeImageOptions);
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(item.getAlbum_2()),
						albumsHolder.albums_image_2, ImgUtils.homeImageOptions);
				ImgUtils.imageLoader.displayImage(
						CommonUtils.doWebpUrl(item.getAlbum_3()),
						albumsHolder.albums_image_3, ImgUtils.homeImageOptions);

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
						NewsTopicActivity.this
								.startAnimActivityByNewsHomeModuleItem(
										NewsAlbumActivity.class, item);
					}
				});
			}
			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			HeaderViewHolder headerHolder = null;
			if (convertView == null) {
				headerHolder = new HeaderViewHolder();
				convertView = mInflater.inflate(R.layout.subject_section,
						parent, false);
				headerHolder.title = (MyTextView) convertView
						.findViewById(R.id.text1);
				convertView.setTag(headerHolder);
			} else {
				headerHolder = (HeaderViewHolder) convertView.getTag();
			}

			// int key = subjectList.getHeaderids()[position];
			// String title = subjectList.getKeys()[key];
			int index = Integer.parseInt(mTopicModule.getList().get(position)
					.getCategory());
			headerHolder.title.setText(categorys[index]);

			return convertView;
		}

		/**
		 * Remember that these have to be static, postion=1 should always return
		 * the same Id that is.
		 */
		@Override
		public long getHeaderId(int position) {
			// return subjectList.getHeaderids()[position];
			return Integer.parseInt(mTopicModule.getList().get(position)
					.getCategory());
		}

		@Override
		public int getPositionForSection(int section) {
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			return null;
		}

		public void clear() {
			notifyDataSetChanged();
		}

		public void restore() {
			notifyDataSetChanged();
		}

		private class TagViewHolder {
			LinearLayout tagview;
		}

		private class HeaderViewHolder {
			MyTextView title;
		}

		private class ViewHolder {
			MyTextView title;
			PhotoView titlePic;
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		case R.id.title_bar_content_img:
			// 一键分享
			if (shareUtil == null)
				return;
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
				initNetDate();
			}
			break;
		}
	}

	@Override
	public void refresh() {
		mLoadingView.setVisibility(View.VISIBLE);
		if (CommonUtils.isNetworkAvailable(mContext)) {
			initNetDate();
		} else {
			if (mTopicModule != null)
				mLoadingView.setVisibility(View.GONE);
		}
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
		// TODO
		ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		// clip.setText(mTopicModule.get);
		ToastUtils.Infotoast(this, "已将链接复制进黏贴板");
	}

	@Override
	public void changeFontSize() {
		FontUtils.setTextViewFontSize(this, mTitleText,
				R.string.news_content_text_size, spUtil.getFontSizeRadix());
		FontUtils.chagneFontSizeGlobal();

		int first = stickyList.getFirstVisiblePosition();
		stickyList.setAdapter(mTopicAdapter);
		stickyList.setSelection(first);
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
}
