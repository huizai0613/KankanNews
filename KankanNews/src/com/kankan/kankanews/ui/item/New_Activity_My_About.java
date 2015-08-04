package com.kankan.kankanews.ui.item;

import org.json.JSONObject;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankanews.kankanxinwen.R;

public class New_Activity_My_About extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_set_about);

		// tongji
		initAnalytics(AndroidConfig.set_about_page);

		initTitleLeftBar("关于", R.drawable.new_ic_back);

		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AnimFinsh();
			}
		});

		MyTextView findViewById = (MyTextView) findViewById(R.id.version);
		try {
			String versionName = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName;

			findViewById.setText("看看新闻 V " + versionName);

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		nightView = findViewById(R.id.night_view);
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
		// TODO Auto-generated method stub
		AnimFinsh();
	}

}
