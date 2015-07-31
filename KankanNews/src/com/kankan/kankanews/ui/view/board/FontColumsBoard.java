/**
 * 
 */

package com.kankan.kankanews.ui.view.board;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.New_Colums;
import com.kankan.kankanews.bean.New_Colums_Second;
import com.kankan.kankanews.ui.item.New_Activity_Colums_Info;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankanews.kankanxinwen.R;

/**
 * 
 */
public class FontColumsBoard extends PopupWindow implements OnClickListener {
	private LayoutInflater inflater;
	private BaseActivity activity;
	private View cancelBut;

	public class ColumItem {
		ImageView logo;
		TextView title;
	}

	ColumItem item;

	public FontColumsBoard(BaseActivity activity) {
		super(activity);
		this.activity = activity;
		initView(activity);
		initData();
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		inflater = LayoutInflater.from(context);
		View rootView = inflater.inflate(R.layout.font_colum_board, null);
		cancelBut = rootView.findViewById(R.id.font_colums_cancel_but);
		DebugLog.e(cancelBut.toString());
		cancelBut.setOnClickListener(this);
		setContentView(rootView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchable(true);
	}

	public void initData() {

	}

	@Override
	public void onClick(View v) {
		dismiss();
	}

}
