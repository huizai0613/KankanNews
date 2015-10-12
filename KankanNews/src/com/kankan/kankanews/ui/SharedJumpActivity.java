package com.kankan.kankanews.ui;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.New_News_Home;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.ui.item.New_Activity_Content_PicSet;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.item.New_Activity_Content_Web;
import com.kankan.kankanews.ui.item.New_Avtivity_Subject;
import com.kankan.kankanews.ui.view.TasksCompletedView;
import com.kankanews.kankanxinwen.R;

public class SharedJumpActivity extends BaseActivity {
	private TasksCompletedView post_video_progress_bar;

	private int mTotalProgress = 100;
	private int mCurrentProgress = 0;

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
		this.setContentView(R.layout.activity_shared_jump);
		post_video_progress_bar = (TasksCompletedView) this
				.findViewById(R.id.post_video_progress_bar);
//		post_video_progress_bar.setProgress(80);
		new Thread(new ProgressRunable()).start();
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

	class ProgressRunable implements Runnable {

		@Override
		public void run() {
			while (mCurrentProgress < mTotalProgress) {
				mCurrentProgress += 1;
				post_video_progress_bar.setProgress(mCurrentProgress);
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}
