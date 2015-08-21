package com.kankan.kankanews.receiver;

import com.kankan.kankanews.base.BaseVideoActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

public class NetChangeReceiver extends BroadcastReceiver {
	private BaseVideoActivity activity;

	public NetChangeReceiver(BaseVideoActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			this.activity.netChanged();
		}
	}

}
