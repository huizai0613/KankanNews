package com.kankan.kankanews.bean;

import java.io.Serializable;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.base.Error;
import com.kankan.kankanews.exception.NetRequestException;

public class Video implements Serializable {

	private String id;
	private String name;
	private String path;
	private String mimeType;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}
