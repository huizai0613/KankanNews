package com.kankan.kankanews.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.kankanews.kankanxinwen.R;
public class Loading_Dialog extends Dialog {

	public Loading_Dialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	protected Loading_Dialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public Loading_Dialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
	}

}
