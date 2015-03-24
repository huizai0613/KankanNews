package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class User extends BaseBean<User>{
	
	private String user_id;
	private String user_name;
	private String user_poster;
	
	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User parseJSON(JSONObject jsonObj) throws NetRequestException {
//		checkJson(jsonObj);
		user_id = jsonObj.optString("kk_id");
		user_name = jsonObj.optString("kk_nickName");
		user_poster = jsonObj.optString("kk_posterURL");
		return this;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_poster() {
		return user_poster;
	}

	public void setUser_poster(String user_poster) {
		this.user_poster = user_poster;
	}
	
}
