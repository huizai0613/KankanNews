package com.kankan.kankanews.ui.item;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.annotation.TargetApi;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
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

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.BaseVideoActivity;
import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.bean.New_News;
import com.kankan.kankanews.bean.New_News_Click;
import com.kankan.kankanews.bean.New_Subject_Json;
import com.kankan.kankanews.bean.Subject_Item;
import com.kankan.kankanews.bean.subject_List;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.photoview.PhotoView;
import com.kankan.kankanews.ui.MainActivity;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.popup.CustomShareBoard;
import com.kankan.kankanews.ui.view.popup.FontColumsBoard;
import com.kankan.kankanews.utils.ClickUtils;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.NetUtils;
import com.kankan.kankanews.utils.NewsBrowseUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.sso.UMSsoHandler;

public class NewsSubjectActivity extends BaseActivity implements
		AdapterView.OnItemClickListener,
		StickyListHeadersListView.OnHeaderClickListener,
		StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
		StickyListHeadersListView.OnStickyHeaderChangedListener,
		OnClickListener {

	// 分享类
	private ShareUtil shareUtil;

	private String ztid;
	private String title;
	private String titlepic;
	private String sharedPic;
	private String titleurl;
	private String intro;
	private MyTextView titleText;

	private LinkedList<Subject_Item> subjectData = new LinkedList<Subject_Item>();
	private SubjectAdapter mAdapter;
	private boolean fadeHeader = true;

	private subject_List subjectList = new subject_List();

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private StickyListHeadersListView stickyList;

	private View headerView;

	private NetUtils instance;

	// 新闻点击量
	private ArrayList<New_News_Click> new_news_clicks;
	private HashMap<String, String> mClicks = new HashMap<String, String>();

	// 加载
	private RelativeLayout content_loading;
	// 重试
	private LinearLayout main_bg;
	boolean LoaclData = false;

	private View nightView;

	@Override
	public void initNightView(boolean isFullScreen) {
		if (!spUtil.getIsDayMode())
			chage2Night();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subject);
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

		content_loading = (RelativeLayout) findViewById(R.id.content_loading);
		main_bg = (LinearLayout) findViewById(R.id.main_bg);

		nightView = findViewById(R.id.night_view);

		instance = NetUtils.getInstance(this);
		// 获取上个页面传来的数据
		Intent intent = getIntent();
		ztid = intent.getStringExtra("ztid");
		title = intent.getStringExtra("title");
		titlepic = intent.getStringExtra("titlepic");
		sharedPic = intent.getStringExtra("sharedPic");
		titleurl = intent.getStringExtra("titleurl");
		intro = intent.getStringExtra("intro");

		new_news = new New_News();

		new_news.setTitlelist(title);
		if (sharedPic == null || sharedPic.trim().equals(""))
			new_news.setSharedPic(titlepic);
		else
			new_news.setSharedPic(sharedPic);
		new_news.setTitleurl(titleurl);
		new_news.setIntro(intro);

		NetUtils.getInstance(mContext).getAnalyse(this, "topic",
				new_news.getTitlelist(), new_news.getTitleurl());

		// 初始化shareutil类
		shareUtil = new ShareUtil(new_news, mContext);

		initTitleBarIcon(R.drawable.ic_share, R.drawable.new_ic_back,
				R.drawable.ic_close_white, R.drawable.ic_font,
				R.drawable.ic_refresh);

		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);
		setOnContentClickLinester(this);

		// com_title_bar_right_bt.setVisibility(View.GONE);

		mAdapter = new SubjectAdapter(this);

		headerView = getLayoutInflater().inflate(R.layout.subject_header, null);

		stickyList = (StickyListHeadersListView) findViewById(R.id.list);
		stickyList.setVerticalScrollBarEnabled(false);
		stickyList.setOnItemClickListener(this);
		stickyList.setOnHeaderClickListener(this);
		stickyList.setOnStickyHeaderChangedListener(this);
		stickyList.setOnStickyHeaderOffsetChangedListener(this);
		stickyList.addHeaderView(headerView);
		stickyList.setDrawingListUnderStickyHeader(false);
		stickyList.setAreHeadersSticky(true);
		stickyList.setAdapter(mAdapter);

		titleText = (MyTextView) headerView.findViewById(R.id.intro);
		FontUtils.setTextViewFontSize(this, titleText,
				R.string.news_content_text_size, spUtil.getFontSizeRadix());

		main_bg.setOnClickListener(this);

		if (CommonUtils.isNetworkAvailable(mContext)) {
			if (ztid != null && Integer.valueOf(ztid) > 0) {
				initNetDate(ztid);
			} else {
				ToastUtils.Errortoast(mContext, "参数错误");
			}
		} else {
			ToastUtils.ErrorToastNoNet(mContext);
			initLocalData(ztid);
		}
	}

	private boolean initLocalData(String ztid) {

		List<New_News_Click> mNewsClick;
		try {
			mNewsClick = dbUtils.findAll(Selector.from(New_News_Click.class)
					.where("ztid", "=", ztid));

			New_Subject_Json mSubjectJson;
			mSubjectJson = dbUtils.findById(New_Subject_Json.class, ztid);

			if (mNewsClick != null) {
				new_news_clicks = new ArrayList<New_News_Click>(mNewsClick);
			}

			if (mSubjectJson != null) {
				try {
					JSONObject jsonObject;
					jsonObject = new JSONObject(mSubjectJson.getJson());
					subjectList.parseJSON(jsonObject);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (mNewsClick != null && mSubjectJson != null) {
				initContentData();
				LoaclData = true;
				return true;
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		content_loading.setVisibility(View.GONE);
		main_bg.setVisibility(View.VISIBLE);
		LoaclData = false;
		return false;
	}

	private void initNetDate(String ztid) {
		content_loading.setVisibility(View.VISIBLE);
		instance.getSubjectData(ztid, mListener, mErrorListener);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		try {
			subjectList.parseJSON(jsonObject);
			// 存储json
			try {
				if (jsonObject != null) {
					New_Subject_Json mNew_Subject_Json = new New_Subject_Json();
					mNew_Subject_Json.setId(ztid);
					mNew_Subject_Json.setJson(jsonObject.toString());
					dbUtils.saveOrUpdate(mNew_Subject_Json);
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (subjectList.getList().size() > 0) {
				String midtype = "";
				new_news_clicks = new ArrayList<New_News_Click>();
				for (int i = 0; i < subjectList.getList().size(); i++) {
					Subject_Item item = subjectList.getList().get(i);
					if (Integer.parseInt(item.getType()) % 10 != 5
							&& Integer.parseInt(item.getType()) % 10 != 6) {
						New_News_Click new_news_click = new New_News_Click();
						new_news_click.setId(item.getMid());
						new_news_click.setType(item.getType());
						new_news_clicks.add(new_news_click);
						midtype = midtype + item.getMid() + ":"
								+ Integer.valueOf(item.getType()) % 10 + "_";
					}
				}
				midtype = midtype.substring(0, midtype.length() - 1);
				instance.getNewNewsClickData(midtype, getClickTimeListener,
						getClickTimeErrorListener);
			} else {
				new_news_clicks = new ArrayList<New_News_Click>();
			}

		} catch (NetRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onFailure(VolleyError error) {
		ToastUtils.Errortoast(mContext, "网络不可用");
		if (!LoaclData) {
			main_bg.setVisibility(View.VISIBLE);
		}
		content_loading.setVisibility(View.GONE);
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
	private class SubjectAdapter extends BaseAdapter implements
			StickyListHeadersAdapter, SectionIndexer {

		private final Context mContext;
		private LayoutInflater mInflater;

		public SubjectAdapter(Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return subjectList.getList().size();
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
			if (getItemDataType(position) == 2) {
				int type = Integer.valueOf(subjectList.getList().get(position)
						.getType());
				if (type % 10 == 2) {
					return 3;
				} else {
					return 1;
				}
			}
			return 0;
		}

		public int getItemDataType(int position) {
			if (subjectList.getList().size() > position) {
				return subjectList.getList().get(position).getDataType();
			} else {
				return 0;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 4;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			HeaderViewHolder headerHolder = null;

			NewContentHolder newHolder = null;
			NewAlbumsHolder albumsHolder = null;

			int dataType = getItemDataType(position);
			int itemViewType = getItemViewType(position);

			if (dataType > 0) {
				if (convertView == null) {
					if (dataType == 1) {// section标题
						headerHolder = new HeaderViewHolder();
						convertView = mInflater.inflate(
								R.layout.subject_section, parent, false);
						headerHolder.title = (MyTextView) convertView
								.findViewById(R.id.text1);
						FontUtils.setTextViewFontSize(
								NewsSubjectActivity.this, headerHolder.title,
								R.string.home_news_text_size,
								spUtil.getFontSizeRadix());
						convertView.setTag(headerHolder);
					} else {
						if (itemViewType == 1) {
							convertView = mInflater.inflate(
									R.layout.new_home_news_item, parent, false);
							newHolder = new NewContentHolder();
							newHolder.titlepic = (ImageView) convertView
									.findViewById(R.id.home_news_titlepic);

							newHolder.title = (MyTextView) convertView
									.findViewById(R.id.home_news_title);
							FontUtils.setTextViewFontSize(
									NewsSubjectActivity.this, newHolder.title,
									R.string.home_news_text_size,
									spUtil.getFontSizeRadix());
							newHolder.newstime_sign = (ImageView) convertView
									.findViewById(R.id.home_news_newstime_sign);
							newHolder.newstime = (MyTextView) convertView
									.findViewById(R.id.home_news_newstime);
							newHolder.news_type = (ImageView) convertView
									.findViewById(R.id.home_news_newstype);
							newHolder.home_news_play = (ImageView) convertView
									.findViewById(R.id.home_news_play);

							convertView.setTag(newHolder);
						} else if (itemViewType == 3) {
							convertView = mInflater.inflate(
									R.layout.new_home_news_albums_item, parent,
									false);
							albumsHolder = new NewAlbumsHolder();
							albumsHolder.title = (MyTextView) convertView
									.findViewById(R.id.home_albums_title);
							FontUtils.setTextViewFontSize(
									NewsSubjectActivity.this,
									albumsHolder.title,
									R.string.home_news_text_size,
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
					}
				} else {
					if (dataType == 1) {
						headerHolder = (HeaderViewHolder) convertView.getTag();
					} else {
						if (itemViewType == 1) {
							newHolder = (NewContentHolder) convertView.getTag();
						} else if (itemViewType == 3) {
							albumsHolder = (NewAlbumsHolder) convertView
									.getTag();
						}
					}
				}

				final Subject_Item item = subjectList.getList().get(position);
				item.setTitlepic(CommonUtils.doWebpUrl(item.getTitlepic()));
				if (dataType == 1) {
					headerHolder.title.setText(item.getTitle());
				} else {
					if (itemViewType == 1) {
						if (NewsBrowseUtils.isBrowed(item.getId())) {
							newHolder.title.setTextColor(Color
									.parseColor("#B0B0B0"));
						} else {
							newHolder.title.setTextColor(Color
									.parseColor("#000000"));
						}

						String clicktime = mClicks.get(item.getMid());
						clicktime = TextUtils.isEmpty(clicktime) ? "0"
								: clicktime;
						// String clicktime = "0次";
						newHolder.newstime.setText(clicktime);
						final int news_type = Integer.valueOf(item.getType());
						newHolder.titlepic.setTag(R.string.viewwidth,
								PixelUtil.dp2px(80));
						imageLoader.displayImage(item.getTitlepic(),
								newHolder.titlepic, ImgUtils.homeImageOptions);
						newHolder.title.setText(item.getTitle());
						switch (news_type / 10) {
						case 1:
							newHolder.news_type
									.setImageResource(R.drawable.new_icon_sign_unique);
							break;
						case 2:
							newHolder.news_type
									.setImageResource(R.drawable.new_icon_sign_tui);
							break;
						case 5:
							newHolder.news_type
									.setImageResource(R.drawable.new_icon_sign_subject);
							break;

						default:
							newHolder.news_type.setImageBitmap(null);
						}

						switch (news_type % 10) {
						case 5:
							if (clicktime.equalsIgnoreCase("false")) {
								newHolder.newstime
										.setVisibility(View.INVISIBLE);
								newHolder.newstime_sign
										.setVisibility(View.INVISIBLE);
							} else {
								newHolder.newstime.setVisibility(View.VISIBLE);
								newHolder.newstime_sign
										.setVisibility(View.VISIBLE);
							}
							newHolder.news_type
									.setImageResource(R.drawable.new_icon_sign_subject);
							break;
						case 6:
							if (clicktime.equalsIgnoreCase("false")) {
								newHolder.newstime
										.setVisibility(View.INVISIBLE);
								newHolder.newstime_sign
										.setVisibility(View.INVISIBLE);
							} else {
								newHolder.newstime.setVisibility(View.VISIBLE);
								newHolder.newstime_sign
										.setVisibility(View.VISIBLE);
							}
							newHolder.news_type
									.setImageResource(R.drawable.new_icon_sign_live);
							break;
						default:
							if (clicktime.equalsIgnoreCase("false")) {
								newHolder.newstime
										.setVisibility(View.INVISIBLE);
								newHolder.newstime_sign
										.setVisibility(View.INVISIBLE);
							} else {
								newHolder.newstime.setVisibility(View.VISIBLE);
								newHolder.newstime_sign
										.setVisibility(View.VISIBLE);
							}
							break;
						}

						convertView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if (ClickUtils.isFastDoubleClick()) {
									return;
								}
								if (news_type % 10 == 1) {

									MyTextView textView = (MyTextView) v
											.findViewById(R.id.home_news_title);
									NewsBrowseUtils.hasBrowedNews(item.getId());
									textView.setTextColor(Color
											.parseColor("#B0B0B0"));

									startAnimActivityByParameter(
											New_Activity_Content_Video.class,
											item.getMid(), item.getType(),
											item.getTitleurl(),
											item.getNewstime(),
											item.getTitle(),
											item.getTitlepic(),
											item.getSharedPic(),
											item.getIntro());
								} else if (news_type % 10 == 5) {
									// 专题
									MyTextView textView = (MyTextView) v
											.findViewById(R.id.home_news_title);
									NewsBrowseUtils.hasBrowedNews(item.getId());
									textView.setTextColor(Color
											.parseColor("#B0B0B0"));

									startSubjectActivityByParameter(
											NewsSubjectActivity.class,
											item.getZtid(), item.getTitle(),
											item.getTitlepic(),
											item.getTitleurl(),
											item.getTitlepic(),
											item.getSharedPic(),
											item.getIntro());
								} else if (news_type % 10 == 6) {// 直播

								} else {
									MyTextView textView = (MyTextView) v
											.findViewById(R.id.home_news_title);
									NewsBrowseUtils.hasBrowedNews(item.getId());
									textView.setTextColor(Color
											.parseColor("#B0B0B0"));

									startAnimActivityByParameter(
											New_Activity_Content_Web.class,
											item.getMid(), item.getType(),
											item.getTitleurl(),
											item.getNewstime(),
											item.getTitle(),
											item.getTitlepic(),
											item.getSharedPic(),
											item.getIntro());
								}
							}
						});
					} else if (itemViewType == 3) {
						if (NewsBrowseUtils.isBrowed(item.getId())) {
							albumsHolder.title.setTextColor(Color
									.parseColor("#B0B0B0"));
						} else {
							albumsHolder.title.setTextColor(Color
									.parseColor("#000000"));
						}

						albumsHolder.title.setText(item.getTitle());
						final String[] pics = item.getTitlepic()
								.split("::::::");
						ArrayList<ImageView> image_view_list = new ArrayList<ImageView>();

						int width = (mScreenWidth - PixelUtil.dp2px(20) / 3);
						albumsHolder.albums_image_1.setTag(R.string.viewwidth,
								width);
						albumsHolder.albums_image_2.setTag(R.string.viewwidth,
								width);
						albumsHolder.albums_image_3.setTag(R.string.viewwidth,
								width);

						image_view_list.add(albumsHolder.albums_image_1);
						image_view_list.add(albumsHolder.albums_image_2);
						image_view_list.add(albumsHolder.albums_image_3);
						for (int i = 0; i < (pics.length > 3 ? 3 : pics.length); i++) {
							// CommonUtils.zoomImage(imageLoader,
							// CommonUtils.doWebpUrl(pics[i + 1]),
							// image_view_list.get(i), mContext,
							// imageCache);
							imageLoader.displayImage(
									CommonUtils.doWebpUrl(pics[i + 1]),
									image_view_list.get(i),
									ImgUtils.homeImageOptions);
						}

						convertView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (ClickUtils.isFastDoubleClick()) {
									return;
								}
								MyTextView textView = (MyTextView) arg0
										.findViewById(R.id.home_albums_title);
								NewsBrowseUtils.hasBrowedNews(item.getId());
								textView.setTextColor(Color
										.parseColor("#B0B0B0"));

								startAnimActivityByParameter(
										New_Activity_Content_PicSet.class,
										item.getMid(), item.getType(),
										item.getTitleurl(), item.getNewstime(),
										item.getTitle(), item.getTitlepic(),
										pics[1], item.getIntro());
							}
						});

					}

					// viewHolder.title.setText(item.getTitle());
					// imageLoader.displayImage(item.getTitlepic(),
					// viewHolder.titlePic,
					// Options.getSmallImageOptions(false));
				}

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

			int key = subjectList.getHeaderids()[position];
			String title = subjectList.getKeys()[key];
			headerHolder.title.setText(title);

			return convertView;
		}

		/**
		 * Remember that these have to be static, postion=1 should always return
		 * the same Id that is.
		 */
		@Override
		public long getHeaderId(int position) {
			return subjectList.getHeaderids()[position];
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
				if (ztid != null && Integer.valueOf(ztid) > 0) {
					initNetDate(ztid);
				}
			}
			break;

		}
	}

	/*
	 * 获取新闻点击量
	 */
	// 处理网络出错
	protected ErrorListener getClickTimeErrorListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			error.printStackTrace();
			content_loading.setVisibility(View.GONE);
		}
	};
	// 处理网络成功
	protected Listener<JSONArray> getClickTimeListener = new Listener<JSONArray>() {
		@Override
		public void onResponse(JSONArray jsonObject) {

			try {
				// JSONArray jsonArray = new JSONArray(jsonObject.toString());
				JSONArray jsonArray = jsonObject;
				if (jsonArray != null && jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
						String clickTime = jsonObject1
								.optString(new_news_clicks.get(i).getId()
										+ "_"
										+ (Integer.valueOf(new_news_clicks.get(
												i).getType()) % 10));
						new_news_clicks.get(i).setClickTime(clickTime);
						new_news_clicks.get(i).setZtid(ztid);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 存储数据
			try {
				dbUtils.saveOrUpdateAll(new_news_clicks);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			initContentData();
			// new_news_clicks
		}
	};
	private New_News new_news;

	// 初始化数据
	private void initContentData() {
		// 点击量
		for (int i = 0; new_news_clicks != null && i < new_news_clicks.size(); i++) {
			mClicks.put(new_news_clicks.get(i).getId(), new_news_clicks.get(i)
					.getClickTime());
		}
		//
		ImageView photoView = (ImageView) headerView
				.findViewById(R.id.titlepic);
		imageLoader.displayImage(
				CommonUtils.doWebpUrl(subjectList.getTitlePic()), photoView,
				Options.getSmallImageOptions(false));

		titleText.setText("         " + subjectList.getIntro());

		mAdapter.notifyDataSetChanged();
		main_bg.setVisibility(View.GONE);
		content_loading.setVisibility(View.GONE);
	}

	/**
	 * 获取当前新闻的缩略图对应的 Bitmap。
	 */
	private Bitmap getThumbBitmap() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		// Bitmap decodeFile = BitmapFactory.decodeFile(CommonUtils
		// .getImageCachePath(mContext)
		// + "/"
		// + CommonUtils.generate(titlepic));
		Bitmap decodeFile = BitmapFactory.decodeFile(CommonUtils
				.getImageCachePath(mContext)
				+ "/"
				+ CommonUtils.doWebpUrl(CommonUtils.generate(titlepic)));

		if (decodeFile == null) {
			decodeFile = BitmapFactory.decodeFile(CommonUtils
					.getImageCachePath(mContext)
					+ "/"
					+ "big_"
					+ CommonUtils.doWebpUrl(CommonUtils.generate(titlepic)));
		}
		if (decodeFile == null) {
			decodeFile = ImgUtils.getNetImage(titlepic);
			if (decodeFile == null) {
				BitmapDrawable draw = (BitmapDrawable) getResources()
						.getDrawable(R.drawable.ic_logo);
				decodeFile = draw.getBitmap();
			}
		}
		int byteCount = decodeFile.getRowBytes();
		int height2 = decodeFile.getHeight();
		long mem = height2 * byteCount;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		if (mem > 100 * 1024 * 8) {
			decodeFile.compress(CompressFormat.JPEG, 80, bao);
		} else if (mem < 100 * 1024 * 8 && mem > 80 * 1024 * 8) {
			decodeFile.compress(CompressFormat.JPEG, 90, bao);
		} else {
			decodeFile.compress(CompressFormat.JPEG, 100, bao);
		}
		if (decodeFile != null && !decodeFile.isRecycled()) {
			decodeFile.recycle();
		}
		byte[] byteArray = bao.toByteArray();
		Bitmap decodeByteArray = BitmapFactory.decodeByteArray(byteArray, 0,
				byteArray.length);
		return decodeByteArray;
	}

	@Override
	public void refresh() {
		content_loading.setVisibility(View.VISIBLE);
		if (CommonUtils.isNetworkAvailable(mContext)) {
			if (ztid != null && Integer.valueOf(ztid) > 0) {
				initNetDate(ztid);
			} else {
				ToastUtils.Errortoast(mContext, "参数错误");
			}
		} else {
			ToastUtils.ErrorToastNoNet(mContext);
			initLocalData(ztid);
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
		// TODO Auto-generated method stub
		nightView.setVisibility(View.VISIBLE);
		((CrashApplication) this.getApplication()).changeMainActivityDayMode();
	}

	@Override
	public void copy2Clip() {
		// TODO Auto-generated method stub
		ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		clip.setText(titleurl);
		ToastUtils.Infotoast(this, "已将链接复制进黏贴板");
	}

	@Override
	public void changeFontSize() {
		// TODO Auto-generated method stub
		FontUtils.setTextViewFontSize(this, titleText,
				R.string.news_content_text_size, spUtil.getFontSizeRadix());
		FontUtils.chagneFontSizeGlobal();

		int first = stickyList.getFirstVisiblePosition();
		stickyList.setAdapter(mAdapter);
		stickyList.setSelection(first);
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
