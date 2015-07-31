package com.kankan.kankanews.ui.item;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankanews.kankanxinwen.R;
import com.umeng.fb.fragment.FeedbackFragment;

/**
 * Demo Activity to use {@link com.umeng.fb.fragment.FeedbackFragment}
 */
public class New_Activity_My_FanKui extends BaseActivity {

	private FeedbackFragment mFeedbackFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_fankui);

		// tongji
		initAnalytics(AndroidConfig.set_fankui_page);

		initTitleBarContent("意见反馈", "", "", 0, R.drawable.new_ic_back);
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AnimFinsh();
			}
		});

		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			String conversation_id = getIntent().getStringExtra(
					FeedbackFragment.BUNDLE_KEY_CONVERSATION_ID);
			mFeedbackFragment = FeedbackFragment.newInstance(conversation_id);

			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, mFeedbackFragment).commit();
		}
	}

	@Override
	protected void onNewIntent(android.content.Intent intent) {
		mFeedbackFragment.addPushDevReply();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		AnimFinsh();
	}

}
