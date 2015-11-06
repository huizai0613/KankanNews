package com.kankan.kankanews.bean;

import java.util.List;

public class NewsHomeModule {
	private String appclassid;
	private String title;
	private String titlepic;
	private String sharepic;
	private String icon;
	private String intro;
	private String type;
	private int num;
	private int change;
	private List<NewsHomeModuleItem> list;

	public String getAppclassid() {
		return appclassid;
	}

	public void setAppclassid(String appclassid) {
		this.appclassid = appclassid;
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

	public String getSharepic() {
		return sharepic;
	}

	public void setSharepic(String sharepic) {
		this.sharepic = sharepic;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getChange() {
		return change;
	}

	public void setChange(int change) {
		this.change = change;
	}

	public List<NewsHomeModuleItem> getList() {
		return list;
	}

	public void setList(List<NewsHomeModuleItem> list) {
		this.list = list;
	}
	
}
