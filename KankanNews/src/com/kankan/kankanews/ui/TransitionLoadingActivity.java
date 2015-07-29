package com.kankan.kankanews.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.base.BaseVideoActivity;
import com.kankan.kankanews.base.download.MyRequestCallBack;
import com.kankan.kankanews.bean.MyCollect;
import com.kankan.kankanews.bean.New_LivePlay;
import com.kankan.kankanews.bean.New_News_Home;
import com.kankan.kankanews.bean.User_Collect_Offline;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.ui.fragment.New_ColumsFragment;
import com.kankan.kankanews.ui.fragment.New_HomeFragment;
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.fragment.New_MyFragment;
import com.kankan.kankanews.ui.fragment.New_RevelationsFragment;
import com.kankan.kankanews.ui.item.New_Activity_Content_PicSet;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.item.New_Activity_Content_Web;
import com.kankan.kankanews.ui.item.New_Avtivity_Subject;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.http.HttpHandler;
import com.networkbench.agent.impl.NBSAppAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.update.UmengUpdateAgent;

public class TransitionLoadingActivity extends BaseActivity {

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_transition_loading);

		Intent intent = getIntent();
		Bundle bun = intent.getExtras();

		String news_id = bun.getString("PUSH_NEWS_ID");
		netUtils.getNewsContentDataPush(news_id, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				New_News_Home news = new New_News_Home();
				try {
					news.parseJSON(jsonObject);
					openNews(news);
				} catch (NetRequestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// ToastUtils.ErrorToastNoNet(getActivity());
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {
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
	public void onBackPressed() {
		this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	private void openNews(New_News_Home news) {
		//
		final int news_type = Integer.valueOf(news.getType());
		if (news_type % 10 == 1) {
			this.startAnimActivityByParameterAlpha(
					New_Activity_Content_Video.class, news.getMid(),
					news.getType(), news.getTitleurl(), news.getNewstime(),
					news.getTitle(), news.getTitlepic(), news.getSharedPic());
		} else if (news_type % 10 == 2) {
			final String[] pics = news.getTitlepic().split("::::::");
			this.startAnimActivityByParameterAlpha(
					New_Activity_Content_PicSet.class, news.getMid(),
					news.getType(), news.getTitleurl(), news.getNewstime(),
					news.getTitle(), news.getTitlepic(), pics[1]);
		} else if (news_type % 10 == 5) {
			// 专题
			this.startSubjectActivityByParameterAlpha(
					New_Avtivity_Subject.class, news.getZtid(),
					news.getTitle(), news.getTitlepic(), news.getTitleurl(),
					news.getTitlepic(), news.getSharedPic());
		} else if (news_type % 10 == 6) {
			// 直播
			// New_LivePlayFragment fragment = (New_LivePlayFragment)
			// this.fragments
			// .get(1);
			// fragment.setSelectPlay(true);
			// fragment.setSelectPlayID(Integer.parseInt(news.getZtid()));
			// mActivity.touchTab(mActivity.tab_two);
		} else if (news.getZtype().equals("1")) {
			this.startSubjectActivityByParameterAlpha(
					New_Avtivity_Subject.class, news.getZtid(),
					news.getTitle(), news.getTitlepic(), news.getTitleurl(),
					news.getTitlepic(), news.getSharedPic());
		} else {
			this.startAnimActivityByParameterAlpha(
					New_Activity_Content_Web.class, news.getMid(),
					news.getType(), news.getTitleurl(), news.getNewstime(),
					news.getTitle(), news.getTitlepic(), news.getSharedPic());
		}
		this.finish();
	}

}
