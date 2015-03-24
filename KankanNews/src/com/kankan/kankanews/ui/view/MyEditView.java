package com.kankan.kankanews.ui.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class MyEditView extends EditText{

	public MyEditView(Context context) {
		super(context);
		initFontStyle();
		// TODO Auto-generated constructor stub
	}

	public MyEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFontStyle();
		// TODO Auto-generated constructor stub
	}

	public MyEditView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initFontStyle();
		// TODO Auto-generated constructor stub
	}
	
	private void initFontStyle() {
		AssetManager mgr = getContext().getAssets();// 得到AssetManager
		Typeface tf = Typeface.createFromAsset(mgr, "nomal.TTF");// 根据路径得到Typeface
		setTypeface(tf);// 设置字体
	}

}
