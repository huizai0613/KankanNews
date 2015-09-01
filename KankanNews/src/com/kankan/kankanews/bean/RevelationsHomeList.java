package com.kankan.kankanews.bean;

import java.io.Serializable;
import java.util.List;

public class RevelationsHomeList implements Serializable {
	private String id;
	private List<RevelationsActivity> activity;
	private List<RevelationsBreaknews> breaknews;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<RevelationsActivity> getActivity() {
		return activity;
	}

	public void setActivity(List<RevelationsActivity> activity) {
		this.activity = activity;
	}

	public List<RevelationsBreaknews> getBreaknews() {
		return breaknews;
	}

	public void setBreaknews(List<RevelationsBreaknews> breaknews) {
		this.breaknews = breaknews;
	}

}
