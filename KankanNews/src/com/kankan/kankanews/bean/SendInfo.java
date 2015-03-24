package com.kankan.kankanews.bean;

public class SendInfo {
	
	private String mid;
	private String type;
	private String titleurl;
	private String newstime;
	private String titlepiclist;//列表页的图片，用于作为分享的图片
	
	public SendInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitleurl() {
		return titleurl;
	}

	public void setTitleurl(String titleurl) {
		this.titleurl = titleurl;
	}

	public String getNewstime() {
		return newstime;
	}

	public void setNewstime(String newstime) {
		this.newstime = newstime;
	}

	public String getTitlepiclist() {
		return titlepiclist;
	}

	public void setTitlepiclist(String titlepiclist) {
		this.titlepiclist = titlepiclist;
	}

}
