package com.kankan.kankanews.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.bean.interfaz.CanSharedObject;
import com.kankan.kankanews.exception.NetRequestException;

public class NewsContentConponent implements Serializable {
	private Map<String, NewsContentVideo> video;
	private Map<String, NewsContentImage> image;

	public Map<String, NewsContentVideo> getVideo() {
		return video;
	}

	public void setVideo(Map<String, NewsContentVideo> video) {
		this.video = video;
	}

	public Map<String, NewsContentImage> getImage() {
		return image;
	}

	public void setImage(Map<String, NewsContentImage> image) {
		this.image = image;
	}

}
