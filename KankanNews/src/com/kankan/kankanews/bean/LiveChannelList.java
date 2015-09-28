package com.kankan.kankanews.bean;

import java.util.List;

public class LiveChannelList {
	private List<LiveChannelObj> tv;
	private List<LiveChannelObj> fm;

	public List<LiveChannelObj> getTv() {
		return tv;
	}

	public void setTv(List<LiveChannelObj> tv) {
		this.tv = tv;
	}

	public List<LiveChannelObj> getFm() {
		return fm;
	}

	public void setFm(List<LiveChannelObj> fm) {
		this.fm = fm;
	}

}
