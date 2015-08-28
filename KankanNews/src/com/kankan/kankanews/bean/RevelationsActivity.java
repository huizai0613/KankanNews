package com.kankan.kankanews.bean;

import java.io.Serializable;

public class RevelationsActivity implements Serializable {
	private String id;
	private String title;
	private String newstime;
	private String titlepic;
	private String intro;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNewstime() {
		return newstime;
	}

	public void setNewstime(String newstime) {
		this.newstime = newstime;
	}

	public String getTitlepic() {
		return titlepic;
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

}
