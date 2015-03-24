package com.kankan.kankanews.ui.item;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.Content_News;
import com.kankan.kankanews.bean.Reporter_News;
import com.kankan.kankanews.bean.Reporter_NewsList;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class Activity_Reporter extends BaseActivity {

	public int newsW;
	private int[] arrayid;

	private Content_News content_News;
	private LinkedList<Reporter_News> reporter_Newss;
	private Reporter_NewsList reporter_NewsList;
	private Reporter_News no_more_news;
	private String reporter_id;

	private ItnetUtils instance;
	private MyAdapter myAdapter;

	// 没有内容 点击重试
	private LinearLayout main_bg;

	private View default_bg;

	// 是否有本地数据
	private boolean initLocalDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		newsW = (mScreenWidth - PixelUtil.dp2px(15)) / 2;
		setContentView(R.layout.activity_reporter);
	}

	@Override
	protected void initView() {

		default_bg = findViewById(R.id.default_bg);
		main_bg = (LinearLayout) findViewById(R.id.main_bg);

		initTitle_Left_bar(R.drawable.icon_black_big,
				R.drawable.icon_title_logo);
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});

		listview = (PullToRefreshListView) findViewById(R.id.listview);
		initListView();
		listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				refreshNetDate();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				loadMoreNetDate();
			}
		});

	}

	@Override
	protected void initData() {
		instance = ItnetUtils.getInstance(this);
		// 初始化上个页面传递过来的对象
		content_News = (Content_News) getIntent().getSerializableExtra(
				"content_News");
		String reporterId = getIntent().getStringExtra("NUM");
		if (content_News != null) {
			reporter_id = content_News.getUid();
			initLocalDate();
			// 是否显示点击重新加载页面
			if (CommonUtils.isNetworkAvailable(mContext)) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (listview != null) {
							listview.setRefreshing(false);
						}
					}
				}, 500);
			} else {
				if (!initLocalDate) {
					main_bg.setVisibility(View.VISIBLE);
				}
			}

		} else if (!TextUtils.isEmpty(reporterId)) {
			initLocalDate();

			if (CommonUtils.isNetworkAvailable(mContext)) {
				reporter_id = reporterId;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (listview != null) {
							listview.setRefreshing(false);
						}
					}
				}, 500);
			} else {
				if (!initLocalDate) {
					main_bg.setVisibility(View.VISIBLE);
				}
			}

		}
	}

	// 初始化id数组
	private void init_arrayid() {
		if (reporter_Newss != null) {
			arrayid = new int[reporter_Newss.size()];
			for (int i = 0; i < reporter_Newss.size(); i++) {
				arrayid[i] = Integer.parseInt(reporter_Newss.get(i).getId());
			}
		} else {
			arrayid = null;
		}
	}

	// 初始化本地数据
	protected void initLocalDate() {
		try {
			List<Reporter_News> mReporter_News = dbUtils.findAll(Selector.from(
					Reporter_News.class).where("uid", "=", reporter_id));
			if (mReporter_News != null && mReporter_News.size() > 0) {
				reporter_Newss = new LinkedList<Reporter_News>(mReporter_News);
				myAdapter = new MyAdapter();
				listview.setAdapter(myAdapter);
				// 初始化id数组
				init_arrayid();
				initLocalDate = true;
				default_bg.setVisibility(View.GONE);
			} else {
				reporter_Newss = new LinkedList<Reporter_News>();
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void setListener() {
		main_bg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshNetDate();
			}
		});
	}

	protected void refreshNetDate() {
		isLoadMore = false;
		no_more_news = null;
		instance.getReporter(reporter_id, "", mListener, mErrorListener);
	}

	protected void loadMoreNetDate() {
		isLoadMore = true;
		instance.getReporter(reporter_id, reporter_Newss.getLast()
				.getNewstime(), mListener, mErrorListener);

	}

	private void addData() {
		if (!isLoadMore) {
			reporter_Newss = reporter_NewsList.getReporter_News_List();
			saveLocalDate();
			main_bg.setVisibility(View.GONE);
			default_bg.setVisibility(View.GONE);
			adapter = new MyAdapter();
			listview.setAdapter(adapter);
		} else {
			reporter_Newss.addAll(reporter_NewsList.getReporter_News_List());
		}

	}

	private void saveLocalDate() {
		new Thread() {
			@Override
			public synchronized void run() {
				if (reporter_Newss != null) {
					try {
						dbUtils.saveOrUpdateAll(reporter_Newss);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		try {
			if (reporter_NewsList == null) {
				reporter_NewsList = new Reporter_NewsList();
			}
			reporter_NewsList = reporter_NewsList.parseJSON(jsonObject);
			if (reporter_NewsList != null) {
				addData();
			} else {
				if (isLoadMore) {
					// TODO
					if (no_more_news == null) {
						no_more_news = new Reporter_News();
						no_more_news.setType(0);
					}
				} else {
					// TODO d
				}
			}
			// 初始化arrayid
			init_arrayid();
			adapter.notifyDataSetChanged();
		} catch (NetRequestException e) {
			e.printStackTrace();
		}
		listview.onRefreshComplete();
	}

	@Override
	protected void onFailure(VolleyError error) {
		listview.onRefreshComplete();
		if (initLocalDate) {
			main_bg.setVisibility(View.GONE);
		} else {
			main_bg.setVisibility(View.VISIBLE);
		}
		ToastUtils.ErrorToastNoNet(mContext);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		back();
	}

	private void back() {
		AnimFinsh();
	}

	ReproterTopHolder reproterTopHolder = null;
	ViewHolderInfo holderInfo = null;
	NewHolder newHolder = null;
	private MyAdapter adapter;

	private class MyAdapter extends BaseAdapter {

		Reporter_News reporter_News;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (no_more_news != null) {
				return reporter_Newss != null ? (reporter_Newss.size() + 1) / 2 + 1 + 1
						: 2;
			} else {
				return reporter_Newss != null ? (reporter_Newss.size() + 1) / 2 + 1
						: 1;
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub

			if (position == 0) {
				return 0;
			} else if (no_more_news != null
					&& (reporter_Newss.size() + 1) / 2 + 1 <= position) {
				return 2;
			} else {
				return 1;
			}

			// return position == 0 ? 0 : 1;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			int itemViewType = getItemViewType(position);
			final int newPosition = (position - 1) * 2;

			if (reporter_Newss != null && reporter_Newss.size() > 0) {
				if (convertView == null) {
					if (itemViewType == 0) {
						convertView = LayoutInflater.from(mContext).inflate(
								R.layout.new_reproter_top, null);
						reproterTopHolder = new ReproterTopHolder();

						reproterTopHolder.reporter_top_pic = (ImageView) convertView
								.findViewById(R.id.reporter_top_pic);
						reproterTopHolder.report_top_name = (MyTextView) convertView
								.findViewById(R.id.report_top_name);
						reproterTopHolder.report_top_motto = (MyTextView) convertView
								.findViewById(R.id.report_top_motto);
						reproterTopHolder.reporter_top_intro = (MyTextView) convertView
								.findViewById(R.id.reporter_top_intro);
						reproterTopHolder.reporter_top_attention = (MyTextView) convertView
								.findViewById(R.id.reporter_top_attention);

						convertView.setTag(reproterTopHolder);
					} else if (itemViewType == 1) {
						convertView = LayoutInflater.from(mContext).inflate(
								R.layout.reporter_item_new, null);

						newHolder = new NewHolder();
						newHolder.parserView(1,
								convertView.findViewById(R.id.item1));
						newHolder.parserView(2,
								convertView.findViewById(R.id.item2));
						convertView.setTag(newHolder);
					} else if (itemViewType == 2) {
						holderInfo = new ViewHolderInfo();
						convertView = LayoutInflater.from(mContext).inflate(
								R.layout.comment_nomore, null);
						holderInfo.info = (MyTextView) convertView
								.findViewById(R.id.comment_no_more);
						convertView.setTag(holderInfo);
					}
				} else {
					if (itemViewType == 0) {
						reproterTopHolder = (ReproterTopHolder) convertView
								.getTag();
					} else if (itemViewType == 1) {
						newHolder = (NewHolder) convertView.getTag();
					} else if (itemViewType == 2) {
						holderInfo = (ViewHolderInfo) convertView.getTag();
					}
				}

				// 更新数据
				if (itemViewType == 0 && position == 0) {
					Reporter_News reporter = reporter_Newss.get(0);
					if (reporter != null) {
						imageLoader.displayImage(
								reporter.getProfile_image_url(),
								reproterTopHolder.reporter_top_pic);
						reproterTopHolder.report_top_name.setText(reporter
								.getName());
						reproterTopHolder.report_top_motto.setText(reporter
								.getReporter_intro());
						reproterTopHolder.reporter_top_intro.setText(reporter
								.getMotto());
						reproterTopHolder.reporter_top_attention
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										mApplication.checkLogin();
										if (mApplication.isLogin) {
											// instance.addAttention(mApplication
											// .getUser().getUser_id(),
											// reporter_id,
											// AddAttentionListener,
											// AddAttentionErrorListener);
										} else {
											no_loading();
										}
									}
								});
					}
				} else if (itemViewType == 1) {
					if (reporter_Newss.size() > newPosition) {
						newHolder.itemView1.v.setVisibility(View.VISIBLE);
						reporter_News = reporter_Newss.get(newPosition);
						imageLoader.displayImage(reporter_News.getTitlepic(),
								newHolder.itemView1.titlepic,
								Options.getSmallImageOptions(false));
						newHolder.itemView1.title.setText(reporter_News
								.getTitle());
						newHolder.itemView1.v
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										startAnimActivityById(
												New_Activity_Content_Video.class,
												(position - 1) * 2, "arrayid",
												arrayid);
									}
								});
					} else {
						newHolder.itemView1.v.setVisibility(View.INVISIBLE);
					}

					if (reporter_Newss.size() > newPosition + 1) {
						newHolder.itemView2.v.setVisibility(View.VISIBLE);
						// newHolder.itemView2.title.setVisibility(View.VISIBLE);
						reporter_News = reporter_Newss.get(newPosition + 1);
						imageLoader.displayImage(reporter_News.getTitlepic(),
								newHolder.itemView2.titlepic,
								Options.getSmallImageOptions(false));
						newHolder.itemView2.title.setText(reporter_News
								.getTitle());
						newHolder.itemView2.v
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										startAnimActivityById(
												New_Activity_Content_Video.class,
												(position - 1) * 2 + 1,
												"arrayid", arrayid);
									}
								});
					} else {
						newHolder.itemView2.v.setVisibility(View.INVISIBLE);
						// newHolder.itemView2.title.setVisibility(View.GONE);
					}

				} else if (itemViewType == 2) {
					int padding_in_dp = 10; // 6 dps
					final float scale = getResources().getDisplayMetrics().density;
					int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
					holderInfo.info.setPadding(0, padding_in_px, 0,
							padding_in_px);
				}

			}

			return convertView;
		}
	}

	// 顶部
	private class ReproterTopHolder {
		ImageView reporter_top_pic;
		MyTextView report_top_name;
		MyTextView report_top_motto;
		MyTextView reporter_top_intro;
		MyTextView reporter_top_attention;
	}

	// 底部
	class ViewHolderInfo {
		MyTextView info;
	}

	// 两种item类型
	private class NewHolder {
		NewContentHolder itemView1;
		NewContentHolder itemView2;

		public void parserView(int position, View v) {
			if (position == 1) {
				itemView1 = new NewContentHolder();
				itemView1.v = v;
				itemView1.titlepic = (ImageView) v
						.findViewById(R.id.new_imageview);
				itemView1.titlepic
						.setLayoutParams(new RelativeLayout.LayoutParams(
								LayoutParams.MATCH_PARENT, (int) (newsW / 1.5)));
				itemView1.title = (MyTextView) v
						.findViewById(R.id.item_tv_content);
			} else {
				itemView2 = new NewContentHolder();
				itemView2.v = v;
				itemView2.titlepic = (ImageView) v
						.findViewById(R.id.new_imageview);
				itemView2.titlepic
						.setLayoutParams(new RelativeLayout.LayoutParams(
								LayoutParams.MATCH_PARENT, (int) (newsW / 1.5)));
				itemView2.title = (MyTextView) v
						.findViewById(R.id.item_tv_content);
			}
		}
	}

	private class NewContentHolder {
		View v;
		ImageView titlepic;
		MyTextView title;
	}

	/**
	 * 未登录的弹出对话框提示登录
	 */
	private void no_loading() {
		final InfoMsgHint dialog = new InfoMsgHint(mContext, R.style.MyDialog1);

		dialog.setContent("您尚未登录，需登录后才能关注", "是否登录", "是", "否");

		dialog.setCancleListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setOKListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startAnimActivity(Activity_Login.class);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/*
	 * 点击关注
	 */
	// 处理网络出错
	protected ErrorListener AddAttentionErrorListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			error.printStackTrace();
			ToastUtils.Infotoast(mContext, "关注失败，请重试");
		}
	};
	// 处理网络成功
	protected Listener<JSONObject> AddAttentionListener = new Listener<JSONObject>() {
		@Override
		public void onResponse(JSONObject jsonObject) {
			ToastUtils.Infotoast(mContext, "关注成功");
		}
	};

}
