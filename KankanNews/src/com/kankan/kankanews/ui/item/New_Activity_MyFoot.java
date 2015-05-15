package com.kankan.kankanews.ui.item;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.New_News;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ViewHolderUtil;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class New_Activity_MyFoot extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	private ListView myListView;
	// private View scroll_view;
	private LinearLayout no_foot_layout;

	private List<New_News> mNew_News;
	private MyAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_myfoot);
	}

	@Override
	protected void initView() {
		myListView = (ListView) findViewById(R.id.myfoot_listview);
		no_foot_layout = (LinearLayout) findViewById(R.id.no_foot_layout);
		// scroll_view = findViewById(R.id.scroll_view);
		// scroll_view.setLayoutParams(new LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.MATCH_PARENT, mContext.mScreenHeight
		// - PixelUtil.dp2px(51)));

		// 初始化头部
		initTitle_Right_Left_bar("浏览记录", "", "", "#ffffff",
				R.drawable.new_icon_delete, R.drawable.new_ic_back, "#000000",
				"#000000");
		// 头部的左右点击事件
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);
	}

	@Override
	protected void initData() {
		try {
			mNew_News = dbUtils.findAll(Selector.from(New_News.class)
					.where("looktime", ">", "0").orderBy("looktime", true));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mNew_News != null && mNew_News.size() > 0) {
			myAdapter = new MyAdapter();
			myListView.setAdapter(myAdapter);
		} else {
			no_foot_layout.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		myListView.setOnItemClickListener(this);
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
		case R.id.com_title_bar_left_bt:
			onBackPressed();
			break;
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			if (mNew_News != null && mNew_News.size() > 0) {
				delete();
			}
			break;
		}

	}

	private void delete() {
		final InfoMsgHint dialog = new InfoMsgHint(mContext, R.style.MyDialog1);
		dialog.setContent("清空浏览记录", "亲，想掩盖些什么呢？", "清空", "取消");
		dialog.setCancleListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setOKListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final ArrayList<New_News> rest_uco = new ArrayList<New_News>();
				for (New_News cn : mNew_News) {
					cn.setLooktime("0");
					rest_uco.add(cn);
				}
				new Thread() {
					@Override
					public void run() {
						try {
							dbUtils.saveOrUpdateAll(rest_uco);
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
				// initData();
				mNew_News.clear();
				myAdapter.notifyDataSetChanged();
				dialog.dismiss();
				no_foot_layout.setVisibility(View.VISIBLE);
			}
		});
		dialog.show();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mNew_News.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.new_item_myfoot, null);
			}

			ImageView titlepic = ViewHolderUtil.get(convertView,
					R.id.home_news_titlepic);
			TextView title = ViewHolderUtil.get(convertView,
					R.id.home_news_title);
			TextView looktime = ViewHolderUtil.get(convertView,
					R.id.home_news_newstime);

			final New_News new_news = mNew_News.get(position);
			titlepic.setTag(R.string.viewwidth, PixelUtil.dp2px(80));
			if (new_news.getTitlepic() != null) {
//				CommonUtils.zoomImage(imageLoader, new_news.getTitlepiclist()
//						.split("::::::")[0], titlepic, mContext, imageCache);
				ImgUtils.imageLoader.displayImage(new_news.getTitlepic()
						.split("::::::")[0], titlepic, ImgUtils.homeImageOptions);
			}else{
//				CommonUtils.zoomImage(imageLoader, new_news.getTitlepic()
//						.split("::::::")[0], titlepic, mContext, imageCache);
				ImgUtils.imageLoader.displayImage(new_news.getSharedPic()
						.split("::::::")[0], titlepic, ImgUtils.homeImageOptions);
			}
			if (new_news.getTitlelist() != null) {
			title.setText(new_news.getTitlelist());
			}else{
				title.setText(new_news.getTitle());
			}
			looktime.setText(new_news.getLooktime());
			looktime.setVisibility(View.GONE);

			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		New_News new_news = mNew_News.get(arg2);
		int type = Integer.valueOf(new_news.getType());
		switch (type % 10) {
		case 1:// 视频
			startAnimActivityByParameter(New_Activity_Content_Video.class,
					new_news.getId(), new_news.getType(),
					new_news.getTitleurl(), new_news.getNewstime(),
					new_news.getTitle(), new_news.getTitlepic(), new_news.getSharedPic());
			break;
		case 2:// 图集
			startAnimActivityByParameter(New_Activity_Content_PicSet.class,
					new_news.getId(), new_news.getType(),
					new_news.getTitleurl(), new_news.getNewstime(),
					new_news.getTitle(), new_news.getTitlepic(), new_news.getSharedPic());
			break;
		case 5:// 专题
			startSubjectActivityByParameter(New_Avtivity_Subject.class,
					new_news.getZtid(), new_news.getTitle(),
					new_news.getTitlepic(), new_news.getTitleurl(), new_news.getTitlepic(), new_news.getSharedPic());
			break;
		default:// 图文
			startAnimActivityByParameter(New_Activity_Content_Web.class,
					new_news.getId(), new_news.getType(),
					new_news.getTitleurl(), new_news.getNewstime(), new_news.getTitlelist(), new_news.getTitlepic(), new_news.getSharedPic());
			break;
		}
	}

}
