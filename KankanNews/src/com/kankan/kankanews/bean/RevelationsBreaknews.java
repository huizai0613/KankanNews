package com.kankan.kankanews.bean;

import java.io.Serializable;
import java.util.List;

public class RevelationsBreaknews implements Serializable {
	private String id;
	private String phonenum;
	private String titlepic;
	private List<Keyboard> keyboard;
	private String newstime;
	private String imagegroup;
	private String relatedids;
	private String newstext;
	private List<RevelationsNew> relatednews;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}

	public String getTitlepic() {
		return titlepic;
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public List<Keyboard> getKeyboard() {
		return keyboard;
	}

	public void setKeyboard(List<Keyboard> keyboard) {
		this.keyboard = keyboard;
	}

	public String getNewstime() {
		return newstime;
	}

	public void setNewstime(String newstime) {
		this.newstime = newstime;
	}

	public String getImagegroup() {
		return imagegroup;
	}

	public void setImagegroup(String imagegroup) {
		this.imagegroup = imagegroup;
	}

	public String getRelatedids() {
		return relatedids;
	}

	public void setRelatedids(String relatedids) {
		this.relatedids = relatedids;
	}

	public String getNewstext() {
		return newstext;
	}

	public void setNewstext(String newstext) {
		this.newstext = newstext;
	}

	public List<RevelationsNew> getRelatednews() {
		return relatednews;
	}

	public void setRelatednews(List<RevelationsNew> relatednews) {
		this.relatednews = relatednews;
	}

}
