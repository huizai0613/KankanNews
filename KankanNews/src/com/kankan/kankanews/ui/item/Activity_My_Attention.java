package com.kankan.kankanews.ui.item;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.Content_News;
import com.kankan.kankanews.bean.User;
import com.kankan.kankanews.bean.User_Collect_Offline;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class Activity_My_Attention extends BaseActivity implements OnClickListener{
	
	private MyAdapter myAdapter;
	private ArrayList< String> test = new ArrayList<String>();
	
	//查询出数据库里的数据
	private ArrayList<User_Collect_Offline> user_collect_offlines;
	private int my_collect_count = 0;
	private int my_offline_count = 0;
	private int my_foot_count = 0;
	private int my_attention_count = 0;
	private List<Content_News> content_newss;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_attention);

		initTitle_Left_bar("关注", "", "#000000", R.drawable.icon_black_big);
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

	protected void loadMoreNetDate() {
		// TODO Auto-generated method stub

	}

	protected void refreshNetDate() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initData() {
		//初始化本地数据
		initLocaldata();
		
		myAdapter = new MyAdapter();
		
		test.add("ffff");
		listview.setAdapter(myAdapter);
	}
	
	private void initLocaldata(){
		try {
			user_collect_offlines = (ArrayList<User_Collect_Offline>) dbUtils.findAll(User_Collect_Offline.class);
			content_newss = dbUtils.findAll(Selector.from(Content_News.class).where("looktime", ">", "0"));
			
		} catch (DbException e) {
			e.printStackTrace();
		}
		
		//初始化各种数据
		if(user_collect_offlines!=null&&user_collect_offlines.size()>0){
			for (User_Collect_Offline item : user_collect_offlines) {
				if(item.isCollect()){
					my_collect_count ++;
				}
				if(item.isOffline()){
					my_offline_count ++;
				}
			}
		}
		
		if(content_newss!=null&&content_newss.size()>0){
			my_foot_count = content_newss.size();
		}
		
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.attention_user_foot_layout:
//			startAnimActivity(Activity_My_Foot.class);
//			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		back();
	}

	private void back() {
		AnimFinsh();
	}
	
	
	private AttentionTopHolder attentionTopHolder;
	private AttentionHolder attentionHolder;
	private NomoreHolder nomoreHolder;

	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return test.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}
		
		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			if(position == 0){
				return 0;
			}else{
				return 1;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			int itemViewType = getItemViewType(position);
			final int newPosition = position - 1;
			
			if(convertView == null){
				if(itemViewType == 0){
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.attention_top, null);
					attentionTopHolder = new AttentionTopHolder();
					attentionTopHolder.user_poster = (ImageView) convertView.findViewById(R.id.attention_user_poster);
					attentionTopHolder.user_name = (TextView) convertView.findViewById(R.id.attention_user_name);
					attentionTopHolder.user_attention = (TextView) convertView.findViewById(R.id.attention_user_attention);
					attentionTopHolder.user_collect = (TextView) convertView.findViewById(R.id.attention_user_collect);
					attentionTopHolder.user_offline = (TextView) convertView.findViewById(R.id.attention_user_offline);
					attentionTopHolder.user_foot = (TextView) convertView.findViewById(R.id.attention_user_foot);
					
					attentionTopHolder.user_attention_layout = (LinearLayout) convertView.findViewById(R.id.attention_user_attention_layout);
					attentionTopHolder.user_collect_layout = (LinearLayout) convertView.findViewById(R.id.attention_user_collect_layout);
					attentionTopHolder.user_offline_layout = (LinearLayout) convertView.findViewById(R.id.attention_user_offline_layout);
					attentionTopHolder.user_foot_layout = (LinearLayout) convertView.findViewById(R.id.attention_user_foot_layout);
					convertView.setTag(attentionTopHolder);
				}
			}else{
				if(itemViewType == 0){
					attentionTopHolder = (AttentionTopHolder) convertView.getTag();
				}
				
			}
			
			//设值
			if(itemViewType == 0){
				User user = mApplication.getUser();
				if(user!=null){
					ImgUtils.imageLoader.displayImage(
						user.getUser_poster(),
						attentionTopHolder.user_poster);
				attentionTopHolder.user_name.setText(user.getUser_name());
				attentionTopHolder.user_attention.setText(my_attention_count+"");
				attentionTopHolder.user_collect.setText(my_collect_count+"");
				attentionTopHolder.user_offline.setText(my_offline_count+"");
				attentionTopHolder.user_foot.setText(my_foot_count+"");
				
				setTopListen();
				}
			}else{
				
			}
			
			return convertView;
		}
	}
	
	private class AttentionTopHolder{
		ImageView user_poster;
		TextView user_name;
		TextView user_attention;
		TextView user_collect;
		TextView user_offline;
		TextView user_foot;
		
		LinearLayout user_attention_layout;
		LinearLayout user_collect_layout;
		LinearLayout user_offline_layout;
		LinearLayout user_foot_layout;
	}
	
	private class AttentionHolder{
		
	}
	
	private class NomoreHolder{
		
	}

	public void setTopListen() {
		attentionTopHolder.user_attention_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
		
		attentionTopHolder.user_collect_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startAnimActivity(Activity_My_Collect.class);
			}
		});
		
		attentionTopHolder.user_offline_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startAnimActivity(Activity_OffLine.class);
			}
		});
		
		attentionTopHolder.user_foot_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startAnimActivity(Activity_My_Foot.class);
			}
		});
	}

}
