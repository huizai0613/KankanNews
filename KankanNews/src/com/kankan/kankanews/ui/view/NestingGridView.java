package com.kankan.kankanews.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class NestingGridView extends GridView {
	public NestingGridView(Context context) {
		// TODO Auto-generated method stub
		super(context);
	}

	public NestingGridView(Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		super(context, attrs);
	}

	public NestingGridView(Context context, AttributeSet attrs, int defStyle) {
		// TODO Auto-generated method stub
		super(context, attrs, defStyle);
	}

	/**
	 * 设置不滚动
	 */
//	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//				MeasureSpec.AT_MOST);
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//	}

}