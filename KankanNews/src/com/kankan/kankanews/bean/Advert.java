package com.kankan.kankanews.bean;

import org.json.JSONObject;

import android.util.Log;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class Advert extends BaseBean<Advert> {
	private String id;
	private String value;
	private int width;
	private int height;
	private String type;
	private String event;
	private String url;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Advert parseJSON(JSONObject jsonObj) {
		// TODO Auto-generated method stub
		try {
			JSONObject content = ((JSONObject) jsonObj.getJSONArray("ad")
					.get(0));
			this.id = content.optString("aid");
			JSONObject mediafiles = ((JSONObject) ((JSONObject) content
					.optJSONArray("creative").get(0))
					.getJSONArray("mediafiles").get(0));
			this.width = mediafiles.optInt("w");
			this.height = mediafiles.optInt("h");
			this.type = mediafiles.optString("type");
			this.value = mediafiles.optString("value");
			this.event = mediafiles.optString("event");
			this.url = mediafiles.optString("url");
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("Advert", e.getLocalizedMessage());
		}
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
