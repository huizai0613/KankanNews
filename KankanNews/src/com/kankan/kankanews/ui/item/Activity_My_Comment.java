package com.kankan.kankanews.ui.item;

import java.util.LinkedList;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.Comment;
import com.kankan.kankanews.bean.Comment_List;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.ui.MainActivity;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;

public class Activity_My_Comment extends BaseActivity {

	private ItnetUtils instance;
	private MyAdapter myAdapter;

	private Comment_List comment_list;
	private LinkedList<Comment> comments = new LinkedList<Comment>();

	private boolean isaddcomment = false;
	private String user_id;
	private LinearLayout no_comment_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_comment);
		
		//统计
		initAnalytics(AndroidConfig.my_comment_page);
		
		initTitle_Left_bar("我的评论", "", "#000000", R.drawable.icon_black_big);

		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});
	}

	@Override
	protected void initView() {
		//添加效果用的
		Comment comment = new Comment();
		comment.setType(0);
		comment.setUid("1000000");
		comments.add(comment);
		
		listview = (PullToRefreshListView) findViewById(R.id.listview);
		myAdapter = new MyAdapter();
		listview.setAdapter(myAdapter);
		no_comment_layout = (LinearLayout) findViewById(R.id.no_comment_layout);
		initListView();
	}

	@Override
	protected void initData() {
		user_id = mApplication.getUser().getUser_id();
		instance = ItnetUtils.getInstance(this);
	}

	@Override
	protected void setListener() {
		listview.setMode(Mode.BOTH);
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

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (listview != null) {
					listview.setRefreshing(false);
				}
			}
		}, 500);

	}

	protected void refreshNetDate() {
		isLoadMore = false;
		isaddcomment = false;
		instance.getMyComment(user_id, "", mListener, mErrorListener);
	}

	protected void loadMoreNetDate() {
		isLoadMore = true;
		if (comments != null && comments.size() > 0) {
			if (comments.getLast().getId() != null) {
				instance.getMyComment(user_id, comments.getLast().getId(),
						mListener, mErrorListener);
			} else {
				instance.getMyComment(user_id, comments
						.get(comments.size() - 2).getId(), mListener,
						mErrorListener);
			}
		} else {
			refreshNetDate();
		}
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		try {
			comment_list = new Comment_List();
			comment_list = comment_list.parseJSON(jsonObject);
			if (comment_list != null) {

				if (!isLoadMore) {
					comments.clear();
					comments = comment_list.getComment_list();
				} else {
					if (isaddcomment) {
						comments.removeLast();
						isaddcomment = false;
					}
					comments.addAll(comment_list.getComment_list());
				}
				myAdapter.notifyDataSetChanged();
				no_comment_layout.setVisibility(View.GONE);
			} else {
				if (isLoadMore) {
					// TODO
					if (!isaddcomment) {
						Comment no_comment = new Comment();
						no_comment.setType(0);
//						no_comment.setUid("1000000");
						comments.addLast(no_comment);
						isaddcomment = true;
						myAdapter.notifyDataSetChanged();
					}
				} else {
					no_comment_layout.setVisibility(View.VISIBLE);
				}
			}

		} catch (NetRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listview.onRefreshComplete();

	}

	@Override
	protected void onFailure(VolleyError error) {
		listview.onRefreshComplete();
		ToastUtils.Errortoast(mContext, "网络不可用");
	}

	@Override
	public void onBackPressed() {
		back();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	protected void back() {
		startAnimActivityBack(MainActivity.class);
	}

	// 自定义适配器
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return comments.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return comments.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return comments.get(position).getType();
		}

		class ViewHolderCom {
			ImageView comment_userpic;
			MyTextView comment_name;
			MyTextView comment_time;
			MyTextView comment_intro;
			MyTextView comment_title;
		}

		class ViewHolderInfo {
			MyTextView info;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			int itemViewType = getItemViewType(position);
			final Comment c = comments.get(position);
			ViewHolderCom holderCom = null;
			ViewHolderInfo holderInfo = null;
			if (convertView == null) {
				switch (itemViewType) {
				case 1:// 评论
					holderCom = new ViewHolderCom();
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.my_comment_item, null);
					holderCom.comment_userpic = (ImageView) convertView
							.findViewById(R.id.comment_userpic);
					holderCom.comment_name = (MyTextView) convertView
							.findViewById(R.id.comment_name);
					holderCom.comment_time = (MyTextView) convertView
							.findViewById(R.id.comment_time);
					holderCom.comment_intro = (MyTextView) convertView
							.findViewById(R.id.comment_intro);
					holderCom.comment_title = (MyTextView) convertView
							.findViewById(R.id.comment_title);
					convertView.setTag(holderCom);
					break;
				case 0:// 提示信息
					holderInfo = new ViewHolderInfo();
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.comment_nomore, null);
					holderInfo.info = (MyTextView) convertView
							.findViewById(R.id.comment_no_more);
					convertView.setTag(holderInfo);
					break;
				}
			} else {
				switch (itemViewType) {
				case 1:// 评论
					holderCom = (ViewHolderCom) convertView.getTag();
					break;
				case 0:// 提示信息
					holderInfo = (ViewHolderInfo) convertView.getTag();
					break;

				}
			}
			switch (itemViewType) {
			case 1:// 评论
				imageLoader.displayImage(c.getUserpic(),
						holderCom.comment_userpic,
						Options.getSmallImageOptions(false));
				holderCom.comment_name.setText(c.getName());
				holderCom.comment_time.setText(TimeUtil
						.getMyTime(Long.valueOf(c.getcTime())));
				holderCom.comment_intro.setText(c.getMemo());
				holderCom.comment_title.setText("原文：" + c.getNewtitle());
				if (position == 0) {
					int padding_in_dp = 10; // 6 dps
					final float scale = getResources().getDisplayMetrics().density;
					int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
					convertView.setPadding(0, padding_in_px, 0, 0);
				} else {
					convertView.setPadding(0, 0, 0, 0);
				}
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// ToastUtils.Errortoast(mContext, c.getMid());
						int[] arrayid = { Integer.parseInt(c.getMid()) };
						startAnimActivityById(New_Activity_Content_Video.class, 0,
								"arrayid", arrayid);
					}
				});
				break;
			case 0:// 提示信息
				if ("1000000".equals(c.getUid())) {
					holderInfo.info.setText("");
				}else{
					holderInfo.info.setText("已加载全部数据");
				}

				break;
			}
			return convertView;
		}
	}

}
