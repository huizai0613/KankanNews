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

public class NewsAlbum implements Serializable, CanSharedObject {
	private String id;
	private String type;
	private String url;
	private String title;
	private String titlepic;
	private String newstime;
	private String newsdate;
	private String intro;
	private String keywords;
	private String share_title;
	private String share_titlepic;
	private String share_intro;
	private List<NewsAlbumImage> album;

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

	public String getTitlepic() {
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

	public String getShare_title() {
		return share_title;
	}

	public void setShare_title(String share_title) {
		this.share_title = share_title;
	}

	public String getShare_titlepic() {
		return share_titlepic;
	}

	public void setShare_titlepic(String share_titlepic) {
		this.share_titlepic = share_titlepic;
	}

	public String getShare_intro() {
		return share_intro;
	}

	public void setShare_intro(String share_intro) {
		this.share_intro = share_intro;
	}

	public List<NewsAlbumImage> getAlbum() {
		return album;
	}

	public void setAlbum(List<NewsAlbumImage> album) {
		this.album = album;
	}

	@Override
	public String getShareTitle() {
		return this.getShare_title();
	}

	@Override
	public String getTitleurl() {
		return this.getUrl();
	}

	@Override
	public String getSharedPic() {
		return this.getShare_titlepic();
	}

	@Override
	public void setSharedPic(String sharepic) {
		this.setShare_titlepic(sharepic);
	}

	@Override
	public String getShareIntro() {
		return this.getShare_intro();
	}

}
