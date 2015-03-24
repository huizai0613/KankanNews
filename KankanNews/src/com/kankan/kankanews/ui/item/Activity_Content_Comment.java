package com.kankan.kankanews.ui.item;

import java.util.LinkedList;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.Comment;
import com.kankan.kankanews.bean.Comment_List;
import com.kankan.kankanews.bean.Content_News;
import com.kankan.kankanews.bean.SuccessMsg;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.ui.view.MyEditView;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;

public class Activity_Content_Comment extends BaseActivity implements
		OnClickListener {

	private ItnetUtils instance;
	private MyAdapter myAdapter;

	private String newsid;
	private Comment_List comment_list;
	private LinkedList<Comment> comments = new LinkedList<Comment>();

	private LinearLayout no_comment_layout;
	private Button comment_created_btn;
	private MyEditView comment_memo;

	private Content_News content_News;

	private boolean isaddcomment = false;

	TextWatcher mTextWatcher = new TextWatcher() {

		private CharSequence temp;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (temp.length() > 0) {
				comment_created_btn
						.setBackgroundResource(R.drawable.send_btn_2);
				comment_created_btn.setClickable(true);
			} else {
				comment_created_btn
						.setBackgroundResource(R.drawable.send_btn_1);
				comment_created_btn.setClickable(false);
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_comment);
		initTitle_Left_bar("评论", "", "#000000", R.drawable.icon_black_big);

		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});
	}

	@Override
	protected void initView() {
		listview = (PullToRefreshListView) findViewById(R.id.listview);
		myAdapter = new MyAdapter();
		listview.setAdapter(myAdapter);

		no_comment_layout = (LinearLayout) findViewById(R.id.no_comment_layout);
		comment_created_btn = (Button) findViewById(R.id.comment_created_btn);
		comment_memo = (MyEditView) findViewById(R.id.comment_memo);
	}

	@Override
	protected void initData() {
		Intent intent = getIntent();
		newsid = intent.getStringExtra("NUM");
		content_News = (Content_News) intent
				.getSerializableExtra("content_News");
		// comment_list = new Comment_List();
		comment_list = (Comment_List) intent
				.getSerializableExtra("comment_list");
		instance = ItnetUtils.getInstance(this);
		// if (comment_list != null) {
		// comments = comment_list.getComment_list();
		// myAdapter.notifyDataSetChanged();
		// no_comment_layout.setVisibility(View.GONE);
		// } else {
		refreshNetDate();
		// }
	}

	@Override
	protected void setListener() {
		comment_memo.addTextChangedListener(mTextWatcher);
		comment_created_btn.setOnClickListener(this);
		comment_created_btn.setClickable(false);
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

		comment_memo.requestFocus();
		comment_memo.setOnClickListener(this);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (listview != null) {
					listview.setRefreshing(false);
				}
			}
		}, 500);

	}

	protected void loadMoreNetDate() {
		isLoadMore = true;
		if (comments != null && comments.size() > 0) {
			if (comments.getLast().getId() != null) {
				instance.getNewsContentCommentData(newsid, comments.getLast()
						.getId(), mListener, mErrorListener);
			} else {
				instance.getNewsContentCommentData(newsid,
						comments.get(comments.size() - 2).getId(), mListener,
						mErrorListener);
			}
		} else {
			refreshNetDate();
		}
	}

	protected void refreshNetDate() {
		isLoadMore = false;
		isaddcomment = false;
		instance.getNewsContentCommentData(newsid, "", mListener,
				mErrorListener);
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
		// TODO Auto-generated method stub
		back();
	}

	private void back() {
		Intent data = new Intent();
		data.putExtra("comment_count", true);
		setResult(AndroidConfig.Comment_resultCode, data);
		AnimFinsh();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.comment_created_btn:
			if (mApplication.isLogin) {

				CommonUtils.clickevent(mContext, "title",
						content_News.getTitle(),
						AndroidConfig.video_comment_event);

				String name = mApplication.getUser().getUser_name();
				String uid = mApplication.getUser().getUser_id();
				String userpic = mApplication.getUser().getUser_poster();
				String memo = comment_memo.getText().toString();

				instance.CommitComment(newsid, memo, name, uid, userpic,
						CCListener, CCErrorListener);
			}
			break;

		case R.id.comment_memo:
			mApplication.checkLogin();
			if (!mApplication.isLogin) {
				// 强制隐藏输入法
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(comment_memo.getWindowToken(), 0);
				no_loading();
			}
			break;

		default:
			break;
		}

	}

	private void no_loading() {
		final InfoMsgHint dialog = new InfoMsgHint(mContext, R.style.MyDialog1);

		dialog.setContent("您尚未登录，需登录后才能评论", "是否登录", "是", "否");

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
	 * 提交评论
	 */
	// 处理网络出错
	protected ErrorListener CCErrorListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
		}
	};
	// 处理网络成功
	protected Listener<JSONObject> CCListener = new Listener<JSONObject>() {
		@Override
		public void onResponse(JSONObject jsonObject) {
			try {
				SuccessMsg successMsg = new SuccessMsg();
				successMsg.parseJSON(jsonObject);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				comment_memo.setText("");
				ToastUtils.Infotoast(mContext, "发送成功");
				comment_created_btn
						.setBackgroundResource(R.drawable.send_btn_1);
				comment_created_btn.setClickable(false);

				// Comment comment = new Comment();
				// comment.setcTime(System.currentTimeMillis() / 1000 + "");
				// comment.setMemo(memo);
				// comment.setUid(uid);
				// comment.setMid(newsid);
				// comment.setName(name);
				// comment.setUserpic(userpic);
				//
				// comment.setType(1);
				// try {
				// dbUtils.save(comment);
				// } catch (DbException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				refreshNetDate();
			} catch (NetRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ToastUtils.Errortoast(mContext, e.toString());
			}
		}
	};
	private String memo;
	private String uid;
	private String userpic;
	private String name;

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
		}

		class ViewHolderInfo {
			MyTextView info;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int itemViewType = getItemViewType(position);
			Comment c = comments.get(position);
			ViewHolderCom holderCom = null;
			ViewHolderInfo holderInfo = null;
			if (convertView == null) {
				switch (itemViewType) {
				case 1:// 评论
					holderCom = new ViewHolderCom();
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.comment_item, null);
					holderCom.comment_userpic = (ImageView) convertView
							.findViewById(R.id.comment_userpic);
					holderCom.comment_name = (MyTextView) convertView
							.findViewById(R.id.comment_name);
					holderCom.comment_time = (MyTextView) convertView
							.findViewById(R.id.comment_time);
					holderCom.comment_intro = (MyTextView) convertView
							.findViewById(R.id.comment_intro);
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
				holderCom.comment_time.setText(TimeUtil.getMyTime(Long
						.valueOf(c.getcTime())));
				holderCom.comment_intro.setText(c.getMemo());
				if (position == 0) {
					int padding_in_dp = 10; // 6 dps
					final float scale = getResources().getDisplayMetrics().density;
					int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
					convertView.setPadding(0, padding_in_px, 0, 0);
				} else {
					convertView.setPadding(0, 0, 0, 0);
				}

				break;
			case 0:// 提示信息
				break;
			}
			return convertView;
		}
	}
}
