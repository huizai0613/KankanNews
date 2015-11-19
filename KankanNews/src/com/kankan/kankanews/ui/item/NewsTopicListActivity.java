package com.kankan.kankanews.ui.item;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.iss.view.pulltorefresh.PullToRefreshPinnedSectionListView;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.bean.NewsHomeModule;
import com.kankan.kankanews.bean.NewsHomeModuleItem;
import com.kankan.kankanews.bean.Subject_Item;
import com.kankan.kankanews.photoview.PhotoView;
import com.kankan.kankanews.ui.MainActivity;
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
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.sso.UMSsoHandler;

public class NewsTopicListActivity extends BaseActivity implements
		OnClickListener {

	// 分享类
	private ShareUtil shareUtil;

	private NewsHomeModuleItem mHomeModuleItem;

	private NewsHomeModule mTopicListModule;

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
	private String mLastTime = "";
	private boolean mIsLoadEnd = false;

	@Override
	public void initNightView(boolean isFullScreen) {
		if (!spUtil.getIsDayMode())
			chage2Night();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_list);
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

		mTopicListListView = (PullToRefreshListView) findViewById(R.id.topiclist_list_view);
		initListView();
		//
		// NetUtils.getInstance(mContext).getAnalyse(this, "topic",
		// new_news.getTitlelist(), new_news.getTitleurl());

		// 初始化shareutil类
		// shareUtil = new ShareUtil(mHomeModuleItem, mContext);

		initTitleBarIcon(R.drawable.ic_share, R.drawable.new_ic_back,
				R.drawable.ic_close_white, R.drawable.ic_font,
				R.drawable.ic_refresh);

	}

	protected void initListView() {
		// TODO Auto-generated method stub
		mTopicListListView.setMode(Mode.PULL_UP_TO_REFRESH);
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
						// String time = TimeUtil.getTime(new Date());
						// refreshView.getLoadingLayoutProxy()
						// .setLastUpdatedLabel("最后更新:" + time);
						refreshNetDate();
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//						loadMoreNetDate();
					}
				});
	}

	private boolean initLocalData() {

		return false;
	}

	private void refreshNetDate() {
		mLoadingView.setVisibility(View.VISIBLE);
		netUtils.getNewsList(mHomeModuleItem.getAppclassid(), mLastTime,
				mListener, mErrorListener);
	}

//	protected void loadMoreNetDate() {
//		// TODO Auto-generated method stub
//		if (mIsLoadEnd || !CommonUtils.isNetworkAvailable(this)) {
//			mTopicListListView.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					mNewsListView.onRefreshComplete();
//				}
//			}, 300);
//			return;
//		}
//		this.netUtils.getNewsList(
//				mAppClassId,
//				mNewsHomeModule.getList()
//						.get(mNewsHomeModule.getList().size() - 1)
//						.getNewstime(), new Listener<JSONObject>() {
//					@Override
//					public void onResponse(JSONObject jsonObject) {
//						mNewsListView.onRefreshComplete();
//						NewsHomeModule more = JsonUtils.toObject(
//								jsonObject.toString(), NewsHomeModule.class);
//						if (more.getList().size() == 0) {
//							mIsLoadEnd = true;
//						} else {
//							mIsLoadEnd = false;
//							mNewsHomeModule.getList().addAll(more.getList());
//						}
//						mNewsListAdapter.notifyDataSetChanged();
//					}
//				}, mErrorListener);
//	}

	@Override
	protected void initData() {
		Intent intent = getIntent();
		mHomeModuleItem = (NewsHomeModuleItem) intent
				.getSerializableExtra("_NEWS_HOME_MODEULE_ITEM_");
		if (CommonUtils.isNetworkAvailable(mContext)) {
			// ToastUtils.Errortoast(mContext, "参数错误");
			refreshNetDate();
		} else {
			ToastUtils.ErrorToastNoNet(mContext);
			initLocalData();
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
		mTopicListModule = JsonUtils.toObject(jsonObject.toString(),
				NewsHomeModule.class);
		showData();
	}

	private void showData() {
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
		ToastUtils.Errortoast(mContext, "网络不可用");
		// if () {
		// mRetryView.setVisibility(View.VISIBLE);
		// }
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
		MyTextView newstime;
		ImageView news_type;
	}

	private class NewAlbumsHolder {
		MyTextView title;
		LinearLayout home_albums_imgs_layout;
		ImageView albums_image_1;
		ImageView albums_image_2;
		ImageView albums_image_3;
	}

	// 自定义适配器
	private class TopicListAdapter extends BaseAdapter {

		private final Context mContext;
		private LayoutInflater mInflater;

		public TopicListAdapter(Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
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
			if (position != 0) {
				String type = mTopicListModule.getList().get(position - 1)
						.getType();
				if (type.equals("album")) {
					return 3;
				} else {
					return 1;
				}
			}
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 3;
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

					mTopicHeaderHolder.titlePic.getLayoutParams().height = mScreenWidth * 65 / 320;
					convertView.setTag(mTopicHeaderHolder);
				} else if (itemViewType == 1) {
					convertView = mInflater.inflate(
							R.layout.new_home_news_item, parent, false);
					mNewContentHolder = new NewContentHolder();
					mNewContentHolder.titlepic = (ImageView) convertView
							.findViewById(R.id.home_news_titlepic);
					mNewContentHolder.title = (MyTextView) convertView
							.findViewById(R.id.home_news_title);
					FontUtils.setTextViewFontSize(NewsTopicListActivity.this,
							mNewContentHolder.title,
							R.string.home_news_text_size,
							spUtil.getFontSizeRadix());
					mNewContentHolder.newstime_sign = (ImageView) convertView
							.findViewById(R.id.home_news_newstime_sign);
					mNewContentHolder.newstime = (MyTextView) convertView
							.findViewById(R.id.home_news_newstime);
					mNewContentHolder.news_type = (ImageView) convertView
							.findViewById(R.id.home_news_newstype);
					mNewContentHolder.home_news_play = (ImageView) convertView
							.findViewById(R.id.home_news_play);
					convertView.setTag(mNewContentHolder);
				} else if (itemViewType == 3) {
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
				}
			} else {
				if (itemViewType == 1) {
					mNewContentHolder = (NewContentHolder) convertView.getTag();
				} else if (itemViewType == 3) {
					mNewAlbumsHolder = (NewAlbumsHolder) convertView.getTag();
				}
			}

			if (itemViewType == 0) {
				mTopicHeaderHolder.intro.setText(mTopicListModule.getTitle());
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
				ImgUtils.imageLoader.displayImage(item.getTitlepic(),
						mNewContentHolder.titlepic, ImgUtils.homeImageOptions);
				mNewContentHolder.title.setText(item.getTitle());
				// switch (news_type / 10) {
				// case 1:
				// newHolder.news_type
				// .setImageResource(R.drawable.new_icon_sign_unique);
				// break;
				// case 2:
				// newHolder.news_type
				// .setImageResource(R.drawable.new_icon_sign_tui);
				// break;
				// case 5:
				// newHolder.news_type
				// .setImageResource(R.drawable.new_icon_sign_subject);
				// break;
				//
				// default:
				// newHolder.news_type.setImageBitmap(null);
				// }
				//
				// switch (news_type % 10) {
				// case 5:
				// if (clicktime.equalsIgnoreCase("false")) {
				// newHolder.newstime
				// .setVisibility(View.INVISIBLE);
				// newHolder.newstime_sign
				// .setVisibility(View.INVISIBLE);
				// } else {
				// newHolder.newstime.setVisibility(View.VISIBLE);
				// newHolder.newstime_sign
				// .setVisibility(View.VISIBLE);
				// }
				// newHolder.news_type
				// .setImageResource(R.drawable.new_icon_sign_subject);
				// break;
				// case 6:
				// if (clicktime.equalsIgnoreCase("false")) {
				// newHolder.newstime
				// .setVisibility(View.INVISIBLE);
				// newHolder.newstime_sign
				// .setVisibility(View.INVISIBLE);
				// } else {
				// newHolder.newstime.setVisibility(View.VISIBLE);
				// newHolder.newstime_sign
				// .setVisibility(View.VISIBLE);
				// }
				// newHolder.news_type
				// .setImageResource(R.drawable.new_icon_sign_live);
				// break;
				// default:
				// if (clicktime.equalsIgnoreCase("false")) {
				// newHolder.newstime
				// .setVisibility(View.INVISIBLE);
				// newHolder.newstime_sign
				// .setVisibility(View.INVISIBLE);
				// } else {
				// newHolder.newstime.setVisibility(View.VISIBLE);
				// newHolder.newstime_sign
				// .setVisibility(View.VISIBLE);
				// }
				// break;
				// }

				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// if (ClickUtils.isFastDoubleClick()) {
						// return;
						// }
						// if (news_type % 10 == 1) {
						//
						// MyTextView textView = (MyTextView) v
						// .findViewById(R.id.home_news_title);
						// NewsBrowseUtils.hasBrowedNews(item.getId());
						// textView.setTextColor(Color
						// .parseColor("#B0B0B0"));
						//
						// startAnimActivityByParameter(
						// New_Activity_Content_Video.class,
						// item.getMid(), item.getType(),
						// item.getTitleurl(),
						// item.getNewstime(),
						// item.getTitle(),
						// item.getTitlepic(),
						// item.getSharedPic(),
						// item.getIntro());
						// } else if (news_type % 10 == 5) {
						// // 专题
						// MyTextView textView = (MyTextView) v
						// .findViewById(R.id.home_news_title);
						// NewsBrowseUtils.hasBrowedNews(item.getId());
						// textView.setTextColor(Color
						// .parseColor("#B0B0B0"));
						//
						// startSubjectActivityByParameter(
						// NewsTopicActivity.class,
						// item.getZtid(), item.getTitle(),
						// item.getTitlepic(),
						// item.getTitleurl(),
						// item.getTitlepic(),
						// item.getSharedPic(),
						// item.getIntro());
						// } else if (news_type % 10 == 6) {// 直播
						//
						// } else {
						// MyTextView textView = (MyTextView) v
						// .findViewById(R.id.home_news_title);
						// NewsBrowseUtils.hasBrowedNews(item.getId());
						// textView.setTextColor(Color
						// .parseColor("#B0B0B0"));
						//
						// startAnimActivityByParameter(
						// New_Activity_Content_Web.class,
						// item.getMid(), item.getType(),
						// item.getTitleurl(),
						// item.getNewstime(),
						// item.getTitle(),
						// item.getTitlepic(),
						// item.getSharedPic(),
						// item.getIntro());
						// }
					}
				});
			} else if (itemViewType == 3) {
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
				final String[] pics = item.getTitlepic().split("::::::");
				ArrayList<ImageView> image_view_list = new ArrayList<ImageView>();

				int width = (mScreenWidth - PixelUtil.dp2px(20) / 3);
				mNewAlbumsHolder.albums_image_1.setTag(R.string.viewwidth,
						width);
				mNewAlbumsHolder.albums_image_2.setTag(R.string.viewwidth,
						width);
				mNewAlbumsHolder.albums_image_3.setTag(R.string.viewwidth,
						width);

				image_view_list.add(mNewAlbumsHolder.albums_image_1);
				image_view_list.add(mNewAlbumsHolder.albums_image_2);
				image_view_list.add(mNewAlbumsHolder.albums_image_3);
				for (int i = 0; i < (pics.length > 3 ? 3 : pics.length); i++) {
					// CommonUtils.zoomImage(imageLoader,
					// CommonUtils.doWebpUrl(pics[i + 1]),
					// image_view_list.get(i), mContext,
					// imageCache);
					ImgUtils.imageLoader.displayImage(
							CommonUtils.doWebpUrl(pics[i + 1]),
							image_view_list.get(i), ImgUtils.homeImageOptions);
				}

				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (ClickUtils.isFastDoubleClick()) {
							return;
						}
						// MyTextView textView = (MyTextView) arg0
						// .findViewById(R.id.home_albums_title);
						// NewsBrowseUtils.hasBrowedNews(item.getId());
						// textView.setTextColor(Color
						// .parseColor("#B0B0B0"));
						//
						// startAnimActivityByParameter(
						// New_Activity_Content_PicSet.class,
						// item.getMid(), item.getType(),
						// item.getTitleurl(), item.getNewstime(),
						// item.getTitle(), item.getTitlepic(),
						// pics[1], item.getIntro());
					}
				});

			}

			// viewHolder.title.setText(item.getTitle());
			// imageLoader.displayImage(item.getTitlepic(),
			// viewHolder.titlePic,
			// Options.getSmallImageOptions(false));

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
			// 一键分享
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
				// if (ztid != null && Integer.valueOf(ztid) > 0) {
				refreshNetDate();
				// }
			}
			break;

		}
	}

	// /*
	// * 获取新闻点击量
	// */
	// // 处理网络出错
	// protected ErrorListener getClickTimeErrorListener = new ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// error.printStackTrace();
	// mLoadingView.setVisibility(View.GONE);
	// }
	// };

	// 处理网络成功
	// protected Listener<JSONArray> getClickTimeListener = new
	// Listener<JSONArray>() {
	// @Override
	// public void onResponse(JSONArray jsonObject) {
	//
	// try {
	// // JSONArray jsonArray = new JSONArray(jsonObject.toString());
	// JSONArray jsonArray = jsonObject;
	// if (jsonArray != null && jsonArray.length() > 0) {
	// for (int i = 0; i < jsonArray.length(); i++) {
	// JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
	// String clickTime = jsonObject1
	// .optString(new_news_clicks.get(i).getId()
	// + "_"
	// + (Integer.valueOf(new_news_clicks.get(
	// i).getType()) % 10));
	// new_news_clicks.get(i).setClickTime(clickTime);
	// new_news_clicks.get(i).setZtid(ztid);
	// }
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// // 存储数据
	// try {
	// dbUtils.saveOrUpdateAll(new_news_clicks);
	// } catch (DbException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// initContentData();
	// // new_news_clicks
	// }
	// };

	// 初始化数据
	// private void initContentData() {
	// // 点击量
	// for (int i = 0; new_news_clicks != null && i < new_news_clicks.size();
	// i++) {
	// mClicks.put(new_news_clicks.get(i).getId(), new_news_clicks.get(i)
	// .getClickTime());
	// }
	// //
	// ImageView photoView = (ImageView) headerView
	// .findViewById(R.id.titlepic);
	// imageLoader.displayImage(
	// CommonUtils.doWebpUrl(subjectList.getTitlePic()), photoView,
	// Options.getSmallImageOptions(false));
	//
	// titleText.setText("         " + subjectList.getIntro());
	//
	// mAdapter.notifyDataSetChanged();
	// main_bg.setVisibility(View.GONE);
	// content_loading.setVisibility(View.GONE);
	// }

	/**
	 * 获取当前新闻的缩略图对应的 Bitmap。
	 */
	// private Bitmap getThumbBitmap() {
	// BitmapFactory.Options options = new BitmapFactory.Options();
	// options.inJustDecodeBounds = true;
	// options.inPreferredConfig = Bitmap.Config.RGB_565;
	// // Bitmap decodeFile = BitmapFactory.decodeFile(CommonUtils
	// // .getImageCachePath(mContext)
	// // + "/"
	// // + CommonUtils.generate(titlepic));
	// Bitmap decodeFile = BitmapFactory.decodeFile(CommonUtils
	// .getImageCachePath(mContext)
	// + "/"
	// + CommonUtils.doWebpUrl(CommonUtils.generate(titlepic)));
	//
	// if (decodeFile == null) {
	// decodeFile = BitmapFactory.decodeFile(CommonUtils
	// .getImageCachePath(mContext)
	// + "/"
	// + "big_"
	// + CommonUtils.doWebpUrl(CommonUtils.generate(titlepic)));
	// }
	// if (decodeFile == null) {
	// decodeFile = ImgUtils.getNetImage(titlepic);
	// if (decodeFile == null) {
	// BitmapDrawable draw = (BitmapDrawable) getResources()
	// .getDrawable(R.drawable.ic_logo);
	// decodeFile = draw.getBitmap();
	// }
	// }
	// int byteCount = decodeFile.getRowBytes();
	// int height2 = decodeFile.getHeight();
	// long mem = height2 * byteCount;
	// ByteArrayOutputStream bao = new ByteArrayOutputStream();
	//
	// if (mem > 100 * 1024 * 8) {
	// decodeFile.compress(CompressFormat.JPEG, 80, bao);
	// } else if (mem < 100 * 1024 * 8 && mem > 80 * 1024 * 8) {
	// decodeFile.compress(CompressFormat.JPEG, 90, bao);
	// } else {
	// decodeFile.compress(CompressFormat.JPEG, 100, bao);
	// }
	// if (decodeFile != null && !decodeFile.isRecycled()) {
	// decodeFile.recycle();
	// }
	// byte[] byteArray = bao.toByteArray();
	// Bitmap decodeByteArray = BitmapFactory.decodeByteArray(byteArray, 0,
	// byteArray.length);
	// return decodeByteArray;
	// }

	@Override
	public void refresh() {
		mLoadingView.setVisibility(View.VISIBLE);
		if (CommonUtils.isNetworkAvailable(mContext)) {
			// if (ztid != null && Integer.valueOf(ztid) > 0) {
			refreshNetDate();
			// } else {
			// ToastUtils.Errortoast(mContext, "参数错误");
			// }
		} else {
			ToastUtils.ErrorToastNoNet(mContext);
			initLocalData();
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		// FontUtils.setTextViewFontSize(this, titleText,
		// R.string.news_content_text_size, spUtil.getFontSizeRadix());
		// FontUtils.chagneFontSizeGlobal();
		//
		// int first = stickyList.getFirstVisiblePosition();
		// stickyList.setAdapter(mAdapter);
		// stickyList.setSelection(first);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
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
}
