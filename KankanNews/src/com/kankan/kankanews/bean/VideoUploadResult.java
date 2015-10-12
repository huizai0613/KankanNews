package com.kankan.kankanews.bean;

import java.io.Serializable;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.base.Error;
import com.kankan.kankanews.exception.NetRequestException;

public class VideoUploadResult implements Serializable {

	private String message;
	private String token;
	private boolean success;
	private long start;
	private String releaseName;

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

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public String getReleaseName() {
		return releaseName;
	}

	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}

}
