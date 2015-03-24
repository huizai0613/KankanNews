package com.kankan.kankanews.bean;

import com.lidroid.xutils.db.annotation.Transient;

public class User_Collect_Offline {

	@Transient
	public static final int DOWNLOADING = 0;
	@Transient
	public static final int DOWNLOADED = 1;
	@Transient
	public static final int DOWNLOADPAUSE = 2;
	@Transient
	public static final int DOWNLOADSTOP = 3;
	@Transient
	public static final int DOWNLOADBAD = 4;

	private String id; // 新闻id
	private boolean isCollect;
	private boolean isOffline;
	private long OfflineTime;
	private long CollectTime;
	private double statusPercent;
	private double statusM;
	private double totalM;
	private int type;
	private int progress;

	public double getStatusPercent() {
		return statusPercent;
	}

	public void setStatusPercent(double statusPercent) {
		this.statusPercent = statusPercent;
	}

	public double getStatusM() {
		return statusM;
	}

	public void setStatusM(double statusM) {
		this.statusM = statusM;
	}

	public double getTotalM() {
		return totalM;
	}

	public void setTotalM(double totalM) {
		this.totalM = totalM;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getOfflineTime() {
		return OfflineTime;
	}

	public void setOfflineTime(long offlineTime) {
		OfflineTime = offlineTime;
	}

	public long getCollectTime() {
		return CollectTime;
	}

	public void setCollectTime(long collectTime) {
		CollectTime = collectTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isCollect() {
		return isCollect;
	}

	public void setCollect(boolean isCollect) {
		this.isCollect = isCollect;
	}

	public boolean isOffline() {
		return isOffline;
	}

	public void setOffline(boolean isOffline) {
		this.isOffline = isOffline;
	}

	public User_Collect_Offline(String id, boolean isCollect,
			boolean isOffline, long OfflineTime, long CollectTime) {
		super();
		this.id = id;
		this.isCollect = isCollect;
		this.isOffline = isOffline;
		this.OfflineTime = OfflineTime;
		this.CollectTime = CollectTime;
	}

	public User_Collect_Offline() {
		super();
	}

}
