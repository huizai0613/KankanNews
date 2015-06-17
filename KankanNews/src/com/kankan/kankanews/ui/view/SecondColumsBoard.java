/**
 * 
 */

package com.kankan.kankanews.ui.view;

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
import com.kankan.kankanews.utils.ImgUtils;
import com.kankanews.kankanxinwen.R;

/**
 * 
 */
public class SecondColumsBoard extends PopupWindow implements OnClickListener {
	private LinearLayout secondColumsList;
	private New_Colums colum;
	private LayoutInflater inflater;
	private BaseActivity activity;
	private View cancelBut;

	public class ColumItem {
		ImageView logo;
		TextView title;
	}

	ColumItem item;

	public SecondColumsBoard(BaseActivity activity, New_Colums colum) {
		super(activity);
		this.colum = colum;
		this.activity = activity;
		initView(activity);
		initData();
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		inflater = LayoutInflater.from(context);
		View rootView = inflater.inflate(R.layout.second_colum_board, null);
		secondColumsList = (LinearLayout) rootView
				.findViewById(R.id.second_colums_list);
		cancelBut =  rootView
				.findViewById(R.id.second_colums_cancel_but);
		cancelBut.setOnClickListener(this);
		setContentView(rootView);
		setFocusable(true);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchable(true);
	}

	public void initData() {
		int length = colum.getSecondNum();
		for (int i = 0; i < length; i++) {
			final View convertView = inflater.inflate(R.layout.second_colum_item,
					null);
			ImageView logo = (ImageView) convertView
					.findViewById(R.id.colum_tv_logo);
			TextView title = (TextView) convertView
					.findViewById(R.id.colum_titls);
			New_Colums_Second secondColum = SecondColumsBoard.this.colum
					.getSecondList().get(i);
			ImgUtils.imageLoader.displayImage(secondColum.getTvLogo(), logo,
					ImgUtils.homeImageOptions);
			title.setText(secondColum.getName());
			convertView.setTag(secondColum);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					activity.startAnimActivity2Obj(New_Activity_Colums_Info.class,
							"secondColum", (New_Colums_Second)(convertView.getTag()));
				}
			});
			secondColumsList.addView(convertView);
		}
	}

	@Override
	public void onClick(View v) {
		dismiss();
	}

	private class ColumsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return colum.getSecondNum();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return SecondColumsBoard.this.colum.getSecondList().get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				item = new ColumItem();
				convertView = inflater
						.inflate(R.layout.second_colum_item, null);
				item.logo = (ImageView) convertView
						.findViewById(R.id.colum_tv_logo);
				item.title = (TextView) convertView
						.findViewById(R.id.colum_titls);
				convertView.setTag(item);
			} else {
				item = (ColumItem) convertView.getTag();
			}
			New_Colums_Second secondColum = SecondColumsBoard.this.colum
					.getSecondList().get(position);
			ImgUtils.imageLoader.displayImage(secondColum.getTvLogo(),
					item.logo, ImgUtils.homeImageOptions);
			item.title.setText(secondColum.getName());
			return convertView;
		}
	}

}
