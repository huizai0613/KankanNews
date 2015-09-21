package com.kankan.kankanews.bean;

import java.util.List;

public class LiveLiveList {
	private List<LiveLiveObj> live;
	private List<LiveLiveObj> trailer;

	public List<LiveLiveObj> getLive() {
		return live;
	}

	public void setLive(List<LiveLiveObj> live) {
		this.live = live;
	}

	public List<LiveLiveObj> getTrailer() {
		return trailer;
	}

	public void setTrailer(List<LiveLiveObj> trailer) {
		this.trailer = trailer;
	}

}
