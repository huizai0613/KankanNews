package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class Content_News extends BaseBean<Content_News> {

	private String id;
	private String title;
	private String newstime;
	private String titlepic;
	private String filelength;
	private String intro;
	private String omsid;
	private String keyboard;
	private String m_url;
	private String sharepic;
	private String mid;
	private String dateline;

	private String sourceid;
	private String mp4url;
	private String iosurl;

	private String uid;
	private String name;
	private String motto;
	private String profile_image_url;
	private int type;
	private String reporter_intro;

	// 看新闻时的本地时间
	private String looktime = "0";

	public String getIosurl() {
		return iosurl;
	}

	public void setIosurl(String iosurl) {
		this.iosurl = iosurl;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public Content_News parseJSONAll(JSONObject jsonObj)
			throws NetRequestException {
		id = "";
		title = "";
		newstime = "";
		keyboard = "";
		uid = "";
		name = "";
		profile_image_url = "";
		motto = "";
		reporter_intro = "";
		mp4url = "";
		id = "";
		title = "";
		newstime = "";
		iosurl = "";
		titlepic = "";
		filelength = "0";
		newstime = "";
		intro = "";
		sharepic = "";
		m_url = "";
		id = jsonObj.optString("id");
		title = jsonObj.optString("title");
		dateline = jsonObj.optString("dateline");
		mid = jsonObj.optString("mid");
		titlepic = jsonObj.optString("titlepic");

		return this;
	}

	@Override
	public Content_News parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		checkJson(jsonObj);

		JSONObject jsonObject = jsonObj.optJSONObject("return_result");

		JSONObject reporter = jsonObject.optJSONObject("reporter");
		JSONObject url = jsonObject.optJSONObject("url");

		id = jsonObject.optString("id");
		title = jsonObject.optString("title");
		newstime = jsonObject.optString("newstime");
		keyboard = jsonObject.optString("keyboard");

		uid = reporter.optString("uid");
		name = reporter.optString("name");
		profile_image_url = reporter.optString("profile_image_url");
		motto = reporter.optString("motto");
		reporter_intro = reporter.optString("intro");
		type = reporter.optInt("type");

		mp4url = url.optString("mp4url");

		id = jsonObject.optString("id");
		title = jsonObject.optString("title");
		newstime = jsonObject.optString("newstime");
		iosurl = url.optString("iosurl");

		titlepic = jsonObject.optString("titlepic");
		filelength = jsonObject.optString("filelength")==null?"0": jsonObject.optString("filelength");
		newstime = jsonObject.optString("newstime");
		intro = jsonObject.optString("intro");
		sharepic = jsonObject.optString("sharepic");
		m_url = jsonObject.optString("m_url");
		mid = jsonObject.optString("mid");
		return this;
	}

	// 对面是否相等，避免两次重复的加载(暂时只比较了少部分，需要再加)
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else {
			if (this.getClass() == o.getClass()) {
				Content_News c = (Content_News) o;
				if (this.getMid().equals(c.getMid())
						&& this.getMp4url().equals(c.getMp4url())
						&& this.getTitle().equals(c.getTitle())
						&& this.getTitlepic().equals(c.getTitlepic())
						&& this.getUid().equals(c.getUid())
						&& this.getName().equals(c.getName())
						&& this.getProfile_image_url().equals(
								c.getProfile_image_url())) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
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

	public String getNewstime() {
		return newstime;
	}

	public void setNewstime(String newstime) {
		this.newstime = newstime;
	}

	public String getTitlepic() {
		return titlepic;
	}

//	public String getSmallTitlepic() {
//
//		return titlepic.substring(0, titlepic.lastIndexOf('.'))
//				+ "_480x360.jpg";
//	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public String getFilelength() {
		return filelength;
	}

	public void setFilelength(String filelength) {
		this.filelength = filelength;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getOmsid() {
		return omsid;
	}

	public void setOmsid(String omsid) {
		this.omsid = omsid;
	}

	public String getKeyboard() {
		return keyboard;
	}

	public void setKeyboard(String keyboard) {
		this.keyboard = keyboard;
	}

	public String getM_url() {
		return m_url;
	}

	public void setM_url(String m_url) {
		this.m_url = m_url;
	}

	public String getSharepic() {
		return sharepic;
	}

	public void setSharepic(String sharepic) {
		this.sharepic = sharepic;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getSourceid() {
		return sourceid;
	}

	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}

	public String getMp4url() {
		return mp4url;
	}

	public void setMp4url(String mp4url) {
		this.mp4url = mp4url;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

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

	public String getReporter_intro() {
		return reporter_intro;
	}

	public void setReporter_intro(String reporter_intro) {
		this.reporter_intro = reporter_intro;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getLooktime() {
		return looktime;
	}

	public void setLooktime(String looktime) {
		this.looktime = looktime;
	}

}
