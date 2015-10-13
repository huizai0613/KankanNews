package com.kankan.kankanews.ui;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankanews.kankanxinwen.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sso.UMSsoHandler;

public class SharedJumpActivity extends BaseActivity {
	private CanSharedObject mShareObj;
	private String mType;
	ShareUtil shareUtil;
	private boolean isFirst = true;

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
		DebugLog.e("卧槽onResume");
		super.onResume();
		if (!isFirst)
			this.AnimFinsh();
		isFirst = false;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		DebugLog.e("卧槽onPause");
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {
		mShareObj = (CanSharedObject) this.getIntent().getSerializableExtra(
				"_SHARED_OBJ_");
		mType = this.getIntent().getStringExtra("_SHARED_TYPE_");
		shareUtil = new ShareUtil(this.mShareObj, this);
		if ("WEIXIN".equals(mType))
			shareUtil.directShare(SHARE_MEDIA.WEIXIN);
		if ("WEIXIN_CIRCLE".equals(mType))
			shareUtil.directShare(SHARE_MEDIA.WEIXIN_CIRCLE);
		if ("QQ".equals(mType))
			shareUtil.directShare(SHARE_MEDIA.QQ);
		if ("SINA".equals(mType))
			shareUtil.directShare(SHARE_MEDIA.SINA);
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
		if (!this.mType.equals("QQ"))
			isFirst = true;
		if (this.shareUtil != null) {
			UMSsoHandler ssoHandler = this.shareUtil.getmController()
					.getConfig().getSsoHandler(requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}
		DebugLog.e("卧槽");
	}

	public void goReturn(View v) {
		this.AnimFinsh();
	}

}
