package com.kankan.kankanews.ui.item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
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
import com.kankan.kankanews.bean.NewsBrowseRecord;
import com.kankan.kankanews.bean.NewsHomeModuleItem;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ViewHolderUtil;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class NewsBrowseRecordActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	private ListView myListView;
	// private View scroll_view;
	private LinearLayout no_foot_layout;

	private List<NewsBrowseRecord> mNewsBrowseRecordList;
	private MyAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_record);
	}

	@Override
	protected void initView() {
		myListView = (ListView) findViewById(R.id.myfoot_listview);
		no_foot_layout = (LinearLayout) findViewById(R.id.no_foot_layout);
		nightView = findViewById(R.id.night_view);
		// scroll_view = findViewById(R.id.scroll_view);
		// scroll_view.setLayoutParams(new LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.MATCH_PARENT, mContext.mScreenHeight
		// - PixelUtil.dp2px(51)));

		// 初始化头部
		initTitleBarContent("浏览记录", 0, "", R.drawable.new_icon_delete,
				R.drawable.new_ic_back);
		// 头部的左右点击事件
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);
	}

	@Override
	protected void initData() {
		try {
			mNewsBrowseRecordList = dbUtils.findAll(Selector
					.from(NewsBrowseRecord.class).where("browseTime", ">", "0")
					.orderBy("browseTime", true));
		} catch (DbException e) {
			e.printStackTrace();
		}

		if (mNewsBrowseRecordList != null && mNewsBrowseRecordList.size() > 0) {
			myAdapter = new MyAdapter();
			myListView.setAdapter(myAdapter);
		} else {
			no_foot_layout.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!spUtil.getIsDayMode())
			chage2Night();
		else
			chage2Day();
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
		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		case R.id.title_bar_right_img:
			if (mNewsBrowseRecordList != null
					&& mNewsBrowseRecordList.size() > 0) {
				delete();
			}
			break;
		}

	}

	private void delete() {
		final InfoMsgHint dialog = new InfoMsgHint(mContext, R.style.MyDialog1);
		dialog.setContent("清空浏览记录", "亲，确定想清空记录吗？", "清空", "取消");
		dialog.setCancleListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setOKListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// final ArrayList<New_News> rest_uco = new
				// ArrayList<New_News>();
				// for (NewsBrowseRecord browse : mNewsBrowseRecordList) {
				// cn.setLooktime("0");
				// rest_uco.add(cn);
				// }
				// new Thread() {
				// @Override
				// public void run() {
				// try {
				// dbUtils.saveOrUpdateAll(rest_uco);
				// } catch (DbException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }
				// }.start();
				// initData();
				mNewsBrowseRecordList.clear();
				new Thread() {
					@Override
					public void run() {
						try {
							dbUtils.deleteAll(NewsBrowseRecord.class);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
				}.start();
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
			return mNewsBrowseRecordList.size();
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

			final NewsBrowseRecord new_news = mNewsBrowseRecordList
					.get(position);
			titlepic.setTag(R.string.viewwidth, PixelUtil.dp2px(80));
			// if (new_news.getTitlepic() != null) {
			// CommonUtils.zoomImage(imageLoader, new_news.getTitlepiclist()
			// .split("::::::")[0], titlepic, mContext, imageCache);
			ImgUtils.imageLoader.displayImage(
					new_news.getTitlepic().split("::::::")[0], titlepic,
					ImgUtils.homeImageOptions);
			// } else {
			// // CommonUtils.zoomImage(imageLoader, new_news.getTitlepic()
			// // .split("::::::")[0], titlepic, mContext, imageCache);
			// ImgUtils.imageLoader.displayImage(new_news.getSharedPic()
			// .split("::::::")[0], titlepic,
			// ImgUtils.homeImageOptions);
			// }
			// if (new_news.getShareTitle() != null) {
			// title.setText(new_news.getShareTitle());
			// } else {
			title.setText(new_news.getTitle());
			// }
			looktime.setText(TimeUtil.formatBrowseTime(new Date(new_news
					.getBrowseTime())));
			// looktime.setVisibility(View.GONE);

			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		NewsBrowseRecord mNewsBrowseRecord = mNewsBrowseRecordList.get(arg2);
		NewsHomeModuleItem moduleItem = new NewsHomeModuleItem();
		moduleItem.setId(mNewsBrowseRecord.getId());
		moduleItem.setO_cmsid(mNewsBrowseRecord.getId());
		moduleItem.setType(mNewsBrowseRecord.getType());
		moduleItem.setTitle(mNewsBrowseRecord.getTitle());
		moduleItem.setTitlepic(mNewsBrowseRecord.getTitlepic());
		moduleItem.setTitleurl(mNewsBrowseRecord.getTitleurl());
		if (mNewsBrowseRecord.getType().equals("video"))
			this.startAnimActivityByNewsHomeModuleItem(
					NewsContentActivity.class, moduleItem);
		if (mNewsBrowseRecord.getType().equals("text"))
			this.startAnimActivityByNewsHomeModuleItem(
					NewsContentActivity.class, moduleItem);
		if (mNewsBrowseRecord.getType().equals("album"))
			this.startAnimActivityByNewsHomeModuleItem(NewsAlbumActivity.class,
					moduleItem);
	}

}
