package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;
import com.lidroid.xutils.db.annotation.Id;

public class New_Colums_Info extends BaseBean<New_Colums_Info> {
	@Id
	private int id;
	private String mid;
	private String title;
	private String titlepic;
	private String titleurl;
	private String videoscale;
	private String type;
	private String newstime;
	private String date;
	
	private String myType;//用来区分是什么栏目上的新闻

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public New_Colums_Info parseJSON(JSONObject jsonObj) throws NetRequestException {
		
		mid = jsonObj.optString("id");
		title = jsonObj.optString("title");
		titlepic = jsonObj.optString("titlepic");
		titleurl = jsonObj.optString("titleurl");
		videoscale = jsonObj.optString("videoscale");
		type = jsonObj.optString("type");
		newstime = jsonObj.optString("newstime");
		date = jsonObj.optString("date");
		
		return this;
	}


	public String getId() {
		return mid;
	}

	public void setId(String id) {
		this.mid = id;
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

	public String getTitleurl() {
		return titleurl;
	}

	public void setTitleurl(String titleurl) {
		this.titleurl = titleurl;
	}

	public String getVideoscale() {
		return videoscale;
	}

	public void setVideoscale(String videoscale) {
		this.videoscale = videoscale;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNewstime() {
		return newstime;
	}

	public void setNewstime(String newstime) {
		this.newstime = newstime;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMyType() {
		return myType;
	}

	public void setMyType(String myType) {
		this.myType = myType;
	}

}