package com.kankan.kankanews.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import com.kankanews.kankanxinwen.R;
import com.kankan.kankanews.ui.view.MyTextView;

public class TishiMsgHint extends Dialog {

	private MyTextView dialog_info_content;
	private Button dialog_info_cancle;

	public TishiMsgHint(Context context) {
		super(context);
		init();
	}

	public TishiMsgHint(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}

	public TishiMsgHint(Context context, int theme) {
		super(context, theme);
		init();
	}

	public void init() {
		setContentView(R.layout.dialog_tishimsghint);
		initView();
		initData();
		setListener();
	}

	protected void initView() {
		dialog_info_content = (MyTextView) findViewById(R.id.dialog_info_content);
		dialog_info_cancle = (Button) findViewById(R.id.dialog_info_cancle);
	}

	protected void initData() {

	}

	protected void setListener() {

	}

	public void setContent(String content_title, String cancleString) {
		dialog_info_content.setText(content_title);
		dialog_info_cancle.setText(cancleString);

	}

	public void setOKListener(android.view.View.OnClickListener clickListener) {
	}

	public void setCancleListener(
			android.view.View.OnClickListener clickListener) {
		dialog_info_cancle.setOnClickListener(clickListener);
	}

}
