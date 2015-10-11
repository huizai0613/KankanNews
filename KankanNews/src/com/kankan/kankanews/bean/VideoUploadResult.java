package com.kankan.kankanews.bean;

import java.io.Serializable;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.base.Error;
import com.kankan.kankanews.exception.NetRequestException;

public class VideoUploadResult implements Serializable {

	private String message;
	private String token;
	private boolean succeess;
	private long start;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isSucceess() {
		return succeess;
	}

	public void setSucceess(boolean succeess) {
		this.succeess = succeess;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

}
