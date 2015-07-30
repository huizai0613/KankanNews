package com.kankan.kankanews.ui;

import com.kankan.kankanews.base.view.SildingFinishLayout;
import com.kankan.kankanews.ui.item.New_Activity_Content_Web;
import com.kankanews.kankanxinwen.R;

import android.app.Activity;
import android.os.Bundle;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_normal);
		SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout);
		mSildingFinishLayout
				.setOnSildingFinishListener(new SildingFinishLayout.OnSildingFinishListener() {

					@Override
					public void onSildingFinish() {
						TestActivity.this.finish();

						TestActivity.this.overridePendingTransition(
								R.anim.in_from_right, R.anim.alpha_out);
					}
				});

		mSildingFinishLayout.setTouchView(mSildingFinishLayout);
	}
}
