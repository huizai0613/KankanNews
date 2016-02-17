package com.kankan.kankanews.receiver;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.bean.LiveLiveObj;
import com.kankan.kankanews.bean.New_LivePlay;
import com.kankan.kankanews.ui.MainActivity;
import com.kankan.kankanews.ui.SplashActivity;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

public class MessagePushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			if (bundle != null) {
				String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
				if (extra != null && !extra.equals("")) {
					try {
						JSONObject jsonObject = new JSONObject(extra);
						String newsId = jsonObject.getString("PUSH_NEWS_ID");
						if (newsId != null && !newsId.equals("")) {
							// 打开自定义的Activity
							Intent i = new Intent(context, SplashActivity.class);
							bundle.putString("PUSH_NEWS_ID", newsId);
							i.putExtras(bundle);

							// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TOP);
							context.startActivity(i);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
		}

	}
}
