package com.kankan.kankanews.bean;

import java.io.Serializable;
import java.util.List;

public class NewsHomeModuleItem implements Serializable {
	private String id;
	private String o_cmsid;
	private String o_classid;
	private String title;
	private String titlepic;
	private String newstime;
	private String type;
	private String intro;
	private Keyboard keyboard;
	private String appclassid;
	private int num;
	private String category;
	private int onclick;
	private String option;
	private String videourl;
	private String titleurl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getO_cmsid() {
		return o_cmsid;
	}

	public void setO_cmsid(String o_cmsid) {
		this.o_cmsid = o_cmsid;
	}

	public String getO_classid() {
		return o_classid;
	}

	public void setO_classid(String o_classid) {
		this.o_classid = o_classid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitlepic() {
		return titlepic;
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public String getNewstime() {
		return newstime;
	}

	public void setNewstime(String newstime) {
		this.newstime = newstime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public Keyboard getKeyboard() {
		return keyboard;
	}

	public void setKeyboard(Keyboard keyboard) {
		this.keyboard = keyboard;
	}

	public String getAppclassid() {
		return appclassid;
	}

	public void setAppclassid(String appclassid) {
		this.appclassid = appclassid;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getOnclick() {
		return onclick;
	}

	public void setOnclick(int onclick) {
		this.onclick = onclick;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getVideourl() {
		return videourl;
	}

	public void setVideourl(String videourl) {
		this.videourl = videourl;
	}

	public String getTitleurl() {
		return titleurl;
	}

	public void setTitleurl(String titleurl) {
		this.titleurl = titleurl;
	}

}
