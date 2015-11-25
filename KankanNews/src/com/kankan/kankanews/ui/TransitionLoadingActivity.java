package com.kankan.kankanews.ui;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.New_News_Home;
import com.kankan.kankanews.bean.NewsHomeModuleItem;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.ui.item.New_Activity_Content_PicSet;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.item.New_Activity_Content_Web;
import com.kankan.kankanews.ui.item.New_Avtivity_Subject;
import com.kankan.kankanews.ui.item.NewsAlbumActivity;
import com.kankan.kankanews.ui.item.NewsContentActivity;
import com.kankan.kankanews.ui.item.NewsOutLinkActivity;
import com.kankan.kankanews.ui.item.NewsTopicActivity;
import com.kankanews.kankanxinwen.R;

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
				if (jsonObject == null
						|| jsonObject.toString().trim().equals("")) {
					goHome();
				}
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
		if (news.getTitle() == null || news.getTitle().trim().equals("")) {
			goHome();
		}

		final int news_type = Integer.valueOf(news.getType());
		NewsHomeModuleItem moduleItem = new NewsHomeModuleItem();
		moduleItem.setId(news.getMid());
		moduleItem.setO_cmsid(news.getMid());
		moduleItem.setAppclassid(news.getZtid());
		moduleItem.setTitle(news.getTitle());
		moduleItem.setTitlepic(news.getTitlepic());
		moduleItem.setTitleurl(news.getTitleurl());
		moduleItem.setSharedPic(news.getSharedPic());
		if (news_type % 10 == 1) {
			moduleItem.setType("video");
			this.startAnimActivityByNewsHomeModuleItem(
					NewsContentActivity.class, moduleItem);
			// this.startAnimActivityByParameterAlpha(
			// New_Activity_Content_Video.class, news.getMid(),
			// news.getType(), news.getTitleurl(), news.getNewstime(),
			// news.getTitle(), news.getTitlepic(), news.getSharedPic());
		} else if (news_type % 10 == 2) {
			moduleItem.setType("album");
			this.startAnimActivityByNewsHomeModuleItem(NewsAlbumActivity.class,
					moduleItem);
			// final String[] pics = news.getTitlepic().split("::::::");
			// this.startAnimActivityByParameterAlpha(
			// New_Activity_Content_PicSet.class, news.getMid(),
			// news.getType(), news.getTitleurl(), news.getNewstime(),
			// news.getTitle(), news.getTitlepic(), pics[1]);
		} else if (news_type % 10 == 5) {
			// 专题
			moduleItem.setType("topic");

			// this.startAnimActivityByNewsHomeModuleItem(NewsTopicActivity.class,
			// moduleItem);
			goHome();

			// this.startSubjectActivityByParameterAlpha(
			// New_Avtivity_Subject.class, news.getZtid(),
			// news.getTitle(), news.getTitlepic(), news.getTitleurl(),
			// news.getTitlepic(), news.getSharedPic());
		} else if (news_type % 10 == 6) {
			Intent intent = getIntent();
			intent.setClass(this, MainActivity.class);
			intent.putExtra("LIVE", news.getZtid());
			this.startActivity(intent);
			// 直播
			// New_LivePlayFragment fragment = (New_LivePlayFragment)
			// this.fragments
			// .get(1);
			// fragment.setSelectPlay(true);
			// fragment.setSelectPlayID(Integer.parseInt(news.getZtid()));
			// mActivity.touchTab(mActivity.tab_two);
		} else if (news.getZtype().equals("1")) {
			moduleItem.setType("topic");
			// this.startAnimActivityByNewsHomeModuleItem(NewsTopicActivity.class,
			// moduleItem);
			goHome();
			// this.startSubjectActivityByParameterAlpha(
			// New_Avtivity_Subject.class, news.getZtid(),
			// news.getTitle(), news.getTitlepic(), news.getTitleurl(),
			// news.getTitlepic(), news.getSharedPic());
		} else {
			moduleItem.setType("outlink");
			// this.startAnimActivityByNewsHomeModuleItem(
			// NewsContentActivity.class, moduleItem);
			this.startAnimActivityByNewsHomeModuleItem(
					NewsOutLinkActivity.class, moduleItem);
			// this.startAnimActivityByParameterAlpha(
			// New_Activity_Content_Web.class, news.getMid(),
			// news.getType(), news.getTitleurl(), news.getNewstime(),
			// news.getTitle(), news.getTitlepic(), news.getSharedPic());
		}
		this.finish();
	}

	private void goHome() {
		Intent intent = getIntent();
		intent.setClass(this, MainActivity.class);
		this.startActivity(intent);
		overridePendingTransition(R.anim.alpha_in, R.anim.out_to_right);
	}

}
