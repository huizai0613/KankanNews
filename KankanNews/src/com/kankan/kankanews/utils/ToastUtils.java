package com.kankan.kankanews.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kankanews.kankanxinwen.R;

public class ToastUtils {

	private static long lastToastTime;

	public static void ErrorToastNoNet(Context mContext) {
		Errortoast(mContext, "当前无可用网络");
	}

	public static void Errortoast(Context mContext, String msg) {
		long curTime = System.currentTimeMillis();
		if (curTime - lastToastTime > 1000) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(R.layout.userdefinedtoast, null);
			// TextView txtView_Title = (TextView)
			// view.findViewById(R.id.txt_Title);
			TextView txtView_Context = (TextView) view
					.findViewById(R.id.txt_context);
			// ImageView imageView = (ImageView)
			// view.findViewById(R.id.image_toast);
			txtView_Context.setText(msg);
			Toast toast = new Toast(mContext);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(view);
			toast.show();
		}
		lastToastTime = curTime;
	}

	public static void Infotoast(Context mContext, String msg) {
		long curTime = System.currentTimeMillis();
		if (curTime - lastToastTime > 1000) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(R.layout.userdefinedtoast, null);
			// TextView txtView_Title = (TextView)
			// view.findViewById(R.id.txt_Title);
			TextView txtView_Context = (TextView) view
					.findViewById(R.id.txt_context);
			// ImageView imageView = (ImageView)
			// view.findViewById(R.id.image_toast);
			txtView_Context.setText(msg);
			// view.setBackgroundColor(Color.parseColor("#56c52f"));
			// Toast toast = new Toast(mContext);
			// toast.setGravity(Gravity.BOTTOM, 0, PixelUtil.dp2px(100));

			// TODO临时
			// view.setBackgroundColor(Color.parseColor("#33000000"));
			Toast toast = new Toast(mContext);
			toast.setGravity(Gravity.CENTER, 0, 0);

			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(view);
			toast.show();
		}
		lastToastTime = curTime;
	}

}
