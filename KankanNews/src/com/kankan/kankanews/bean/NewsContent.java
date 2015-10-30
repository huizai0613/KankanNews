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

public class NewsContent implements Serializable, CanSharedObject {
	private String id;
	private String type;
	private String url;
	private String title;
	private String titlepic;
	private String newstime;
	private String newsdate;
	private String intro;
	private String keywords;
	private String contents;
	private NewsContentConponent conponents;
	private List<NewsContentRecommend> recommend;
	private String sharedPic;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitlePic() {
		return titlepic;
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public String getNewstime() {
		return newstime;
	}

	public void setNewstime(String newstime) {
		this.newstime = newstime;
	}

	public String getNewsdate() {
		return newsdate;
	}

	public void setNewsdate(String newsdate) {
		this.newsdate = newsdate;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public NewsContentConponent getConponents() {
		return conponents;
	}

	public void setConponents(NewsContentConponent conponents) {
		this.conponents = conponents;
	}

	public List<NewsContentRecommend> getRecommend() {
		return recommend;
	}

	public void setRecommend(List<NewsContentRecommend> recommend) {
		this.recommend = recommend;
	}

	@Override
	public String getTitlelist() {
		return title;
	}

	@Override
	public String getTitleurl() {
		return url;
	}

	@Override
	public String getTitlepic() {
		return titlepic;
	}

	@Override
	public String getSharedPic() {
		return this.sharedPic;
	}

	@Override
	public void setSharedPic(String sharedPic) {
		this.sharedPic = sharedPic;
	}

}
