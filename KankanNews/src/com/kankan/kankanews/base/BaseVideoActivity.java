package com.kankan.kankanews.base;

import org.json.JSONObject;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.utils.CommonUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

public abstract class BaseVideoActivity extends BaseActivity {

	// 是否可以触摸调整声音
	public boolean canScrool = true;

	public boolean isFullScrenn;

	public LinearLayout video_pb;
	public LinearLayout small_video_pb;

	// 从全屏到小屏
	public void fullScrenntoSamll() {

		CommonUtils.clickevent(mContext, "action", "缩小",
				AndroidConfig.video_fullscreen_event);

		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
				}
			}, 3000);
		}
	}

	// 从小屏到全屏
	public void samllScrenntoFull() {

		CommonUtils.clickevent(mContext, "action", "放大",
				AndroidConfig.video_fullscreen_event);

		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
				}
			}, 3000);
		}
	}

	public void setCanScrool(boolean canScrool) {
		this.canScrool = canScrool;
	}

	public boolean isFullScrenn() {
		return isFullScrenn;
	}

	public void setFullScrenn(boolean isFullScrenn) {
		this.isFullScrenn = isFullScrenn;
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

}
