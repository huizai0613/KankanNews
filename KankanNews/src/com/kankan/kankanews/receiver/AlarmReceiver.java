package com.kankan.kankanews.receiver;

import java.io.Serializable;

import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.bean.New_LivePlay;
import com.kankan.kankanews.ui.MainActivity;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * 
 * @ClassName: AlarmReceiver
 * @Description: 闹铃时间到了会进入这个广播，这个时候可以做一些该做的业务。
 * @author HuHood
 * @date 2013-11-25 下午4:44:30
 *
 */
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		New_LivePlay serializableExtra = (New_LivePlay) intent
				.getSerializableExtra("LIVE");

		serializableExtra.setOrder(false);

		try {
			((CrashApplication) CrashApplication.getInstance()).getDbUtils()
					.saveOrUpdate(serializableExtra);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Notification noti = new Notification(R.drawable.icon_launch, context
				.getResources().getString(R.string.app_name),
				System.currentTimeMillis());

		noti.icon = R.drawable.icon_launch;

		// noti.tickerText = "您预约的节目:" + serializableExtra.getTitle();
		noti.setLatestEventInfo(context,
				"您预约的节目:" + serializableExtra.getTitle(), "已经开始播放", null);

		// 上面第一个参数，我们的通知发送出去显示在状态栏上的提醒图标（这个图标可不是跟我们Notification栏上的一样啊）
		// 上面第二个参数，我们的通知发送出去显示在状态栏上的提醒文字（这个图标可不是跟我们Notification栏上的一样啊）
		// 上面第三个参数，flag，用于标识这个Notification
		noti.defaults = Notification.DEFAULT_ALL;
		// noti.contentView = new RemoteViews(context.getPackageName(),
		// R.layout.notifacation_live);
		// 上面第一个参数，提取我们应用的包名
		// 上面第二个参数，应用r.layout.notification_back这个View当做我们自定义的Notification
		noti.contentView.setImageViewResource(R.id.image,
				R.drawable.icon_launch);
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		// noti.contentView.setTextViewText(R.id.text, "您预约的节目:"
		// + serializableExtra.getTitle());
		// noti.contentView.setTextViewText(R.id.text1, "已经开始播放");

		Intent intent2 = new Intent(context, MainActivity.class);

		intent2.putExtra("LIVE", serializableExtra);

		// 以上就是为Notification设置参数
		noti.contentIntent = PendingIntent
				.getActivity(context, 0, intent2, 100);
		// 以上的代码是为这个Notification设置一个即将跳的意图
		NotificationManager nManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// 实例化NotificationManager
		nManager.notify(1111, noti);

	}
}
