package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.base.Error;
import com.kankan.kankanews.exception.NetRequestException;

public class Reporter extends BaseBean<Reporter> {

	private String id;
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
	public Reporter parseJSON(JSONObject jsonObj) throws NetRequestException {

		if (jsonObj != null) {
			id = jsonObj.optString("uid");
			name = jsonObj.optString("name");
			motto = jsonObj.optString("motto");
			intro = jsonObj.optString("intro");
			profile_image_url = jsonObj.optString("profile_image_url");
			type = jsonObj.optString("type");
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
