package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class Reporter_News extends BaseBean<Reporter_News> {

	private String id; // 新闻id
	private String title;
	private String titlepic;
	private String uid;// 记者id
	private String newstime;

	private String name;
	private String motto;
	private String profile_image_url;
	private int typeR;
	private String reporter_intro;

	private int type;// 1为获取的 0为自己添加的

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMotto() {
		return motto;
	}

	public void setMotto(String motto) {
		this.motto = motto;
	}

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}

	public int getTypeR() {
		return typeR;
	}

	public void setTypeR(int typeR) {
		this.typeR = typeR;
	}

	public String getReporter_intro() {
		return reporter_intro;
	}

	public void setReporter_intro(String reporter_intro) {
		this.reporter_intro = reporter_intro;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reporter_News parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		id = jsonObj.optString("id");
		title = jsonObj.optString("title");
		titlepic = jsonObj.optString("titlepic");
		newstime = jsonObj.optString("newstime");

		type = 1;

		JSONObject reporter = jsonObj.optJSONObject("reporter");
		uid = reporter.optString("uid");
		name = reporter.optString("name");
		profile_image_url = reporter.optString("profile_image_url");
		motto = reporter.optString("motto");
		reporter_intro = reporter.optString("intro");
		typeR = reporter.optInt("type");
		
		return this;
	}

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

	public String getTitlepic() {
		return titlepic;
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getNewstime() {
		return newstime;
	}

	public void setNewstime(String newstime) {
		this.newstime = newstime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
