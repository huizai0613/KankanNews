package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.base.Error;
import com.kankan.kankanews.exception.NetRequestException;

public class News extends BaseBean<News> {

	private String id;
	private String title;
	private String titlepic;
	private String sharepic;
	private String onclick;
	private String newstime;

	private String uid;
	private String name;
	private String motto;
	private String intro;
	private String profile_image_url;
	private String type;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public News parseJSON(JSONObject jsonObj) throws NetRequestException {
		if (jsonObj != null) {
			id = jsonObj.optString("id");
			title = jsonObj.optString("title");
			titlepic = jsonObj.optString("titlepic");
			onclick = jsonObj.optString("onclick");
			sharepic = jsonObj.optString("sharepic");
			newstime = jsonObj.optString("newstime");

			JSONObject optJSONObject = jsonObj.optJSONObject("reporter");

			uid = optJSONObject.optString("uid");
			name = optJSONObject.optString("name");
			motto = optJSONObject.optString("motto");
			intro = optJSONObject.optString("intro");
			profile_image_url = optJSONObject.optString("profile_image_url");
			type = optJSONObject.optString("type");

		} else {
			throw new NetRequestException(new Error());
		}
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSharepic() {
		return sharepic;
	}

	public void setSharepic(String sharepic) {
		this.sharepic = sharepic;
	}

	public String getNewstime() {
		return newstime;
	}

	public void setNewstime(String newstime) {
		this.newstime = newstime;
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

	public String getSmallTitlepic() {

		return titlepic.substring(0, titlepic.lastIndexOf('.'))
				+ "_480x360.jpg";
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
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

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
