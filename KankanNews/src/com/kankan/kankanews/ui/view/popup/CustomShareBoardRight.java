/**
 * 
 */

package com.kankan.kankanews.ui.view.popup;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.interfaz.CanBeShared;
import com.kankan.kankanews.bean.interfaz.CanSharedObject;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.filesel.PicSelectedMainActivity;
import com.kankan.kankanews.ui.RevelationsActivity;
import com.kankan.kankanews.ui.SharedJumpActivity;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankanews.kankanxinwen.R;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 
 */
public class CustomShareBoardRight extends PopupWindow implements
		OnClickListener {

	private BaseActivity mActivity;
	private CanSharedObject shareObj;

	public CustomShareBoardRight(BaseActivity activity, CanSharedObject shareObj) {
		super(activity);
		this.mActivity = activity;
		this.shareObj = shareObj;
		initView(activity);
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.custom_board_right, null);
		rootView.findViewById(R.id.wechat_box).setOnClickListener(this);
		rootView.findViewById(R.id.wechat_circle_box).setOnClickListener(this);
		rootView.findViewById(R.id.qq_box).setOnClickListener(this);
		rootView.findViewById(R.id.sina_box).setOnClickListener(this);
		rootView.findViewById(R.id.share_back_view).setOnClickListener(this);
		setContentView(rootView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchable(true);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (v.getId() == R.id.share_back_view) {
			dismiss();
			return;
		}
		Intent intent = new Intent(this.mActivity, SharedJumpActivity.class);
		intent.putExtra("_SHARED_OBJ_", (Serializable) shareObj);
		switch (id) {
		case R.id.wechat_box:
			intent.putExtra("_SHARED_TYPE_", "WEIXIN");
			break;
		case R.id.wechat_circle_box:
			intent.putExtra("_SHARED_TYPE_", "WEIXIN_CIRCLE");
			break;
		case R.id.qq_box:
			intent.putExtra("_SHARED_TYPE_", "QQ");
			break;
		case R.id.sina_box:
			intent.putExtra("_SHARED_TYPE_", "SINA");
			break;
		}
		this.mActivity.startActivity(intent);
		dismiss();
	}
}
