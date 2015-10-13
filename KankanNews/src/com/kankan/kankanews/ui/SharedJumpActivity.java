package com.kankan.kankanews.ui;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.New_News_Home;
import com.kankan.kankanews.bean.interfaz.CanSharedObject;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.ui.item.New_Activity_Content_PicSet;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.item.New_Activity_Content_Web;
import com.kankan.kankanews.ui.item.New_Avtivity_Subject;
import com.kankan.kankanews.ui.view.TasksCompletedView;
import com.kankanews.kankanxinwen.R;

public class SharedJumpActivity extends BaseActivity {
	private CanSharedObject mShareObj;
	private String mType;

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
		mShareObj = (CanSharedObject) this.getIntent().getSerializableExtra(
				"_SHARED_OBJ_");
		mType = this.getIntent().getStringExtra("_SHARED_TYPE_");

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

}
