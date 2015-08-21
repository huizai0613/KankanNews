/**
 * 
 */

package com.kankan.kankanews.ui.view.popup;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.BaseVideoActivity;
import com.kankan.kankanews.bean.interfaz.CanBeShared;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankanews.kankanxinwen.R;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 
 */
public class CustomShareBoard extends PopupWindow implements OnClickListener {

	private BaseActivity mActivity;
	private ShareUtil shareUtil;
	private CanBeShared shareObj;
	private View copyBox;

	public CustomShareBoard(BaseActivity activity, ShareUtil shareUtil,
			CanBeShared shareObj) {
		super(activity);
		this.mActivity = activity;
		this.shareUtil = shareUtil;
		this.shareObj = shareObj;
		initView(activity);
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.custom_board, null);
		rootView.findViewById(R.id.wechat_box).setOnClickListener(this);
		rootView.findViewById(R.id.wechat_circle_box).setOnClickListener(this);
		rootView.findViewById(R.id.qq_box).setOnClickListener(this);
		rootView.findViewById(R.id.sina_box).setOnClickListener(this);
		rootView.findViewById(R.id.email_box).setOnClickListener(this);
		rootView.findViewById(R.id.colums_cancel_but).setOnClickListener(this);
		rootView.findViewById(R.id.colums_back_view).setOnClickListener(this);
		copyBox = rootView.findViewById(R.id.copy_box);
		copyBox.setOnClickListener(this);
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
		switch (id) {
		case R.id.wechat_box:
			shareUtil.directShare(SHARE_MEDIA.WEIXIN);
			break;
		case R.id.wechat_circle_box:
			shareUtil.directShare(SHARE_MEDIA.WEIXIN_CIRCLE);
			break;
		case R.id.qq_box:
			shareUtil.directShare(SHARE_MEDIA.QQ);
			break;
		case R.id.email_box:
			shareUtil.directShare(SHARE_MEDIA.EMAIL);
			break;
		case R.id.sina_box:
			shareUtil.directShare(SHARE_MEDIA.SINA);
			// shareObj.sendSingleMessage();
			break;
		case R.id.copy_box:
			shareObj.copy2Clip();
			break;
		case R.id.colums_back_view:
		default:
			mActivity.shareReBack();
			break;
		}
		dismiss();
	}
}
