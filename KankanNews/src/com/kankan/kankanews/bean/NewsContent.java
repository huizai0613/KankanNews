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
	private String journalist_id;
	private String journalist_name;
	private String journalist_pic;
	private String journalist_sign;
	private String journalist_intro;
	private String share_title;
	private String share_titlepic;
	private String share_intro;

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

	public String getJournalist_id() {
		return journalist_id;
	}

	public void setJournalist_id(String journalist_id) {
		this.journalist_id = journalist_id;
	}

	public String getJournalist_name() {
		return journalist_name;
	}

	public void setJournalist_name(String journalist_name) {
		this.journalist_name = journalist_name;
	}

	public String getJournalist_pic() {
		return journalist_pic;
	}

	public void setJournalist_pic(String journalist_pic) {
		this.journalist_pic = journalist_pic;
	}

	public String getJournalist_sign() {
		return journalist_sign;
	}

	public void setJournalist_sign(String journalist_sign) {
		this.journalist_sign = journalist_sign;
	}

	public String getJournalist_intro() {
		return journalist_intro;
	}

	public void setJournalist_intro(String journalist_intro) {
		this.journalist_intro = journalist_intro;
	}

	public String getShare_title() {
		if (this.share_title == null)
			return title;
		return this.share_title;
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

	@Override
	public String getShareTitle() {
		return getShare_title();
	}

	@Override
	public void setSharedTitle(String shareTitle) {
		// TODO Auto-generated method stub
		this.share_title = shareTitle;
	}

	@Override
	public String getTitleurl() {
		return url;
	}

	@Override
	public String getTitlepic() {
		return getShare_titlepic();
	}

	@Override
	public String getSharedPic() {
		return getShare_titlepic();
	}

	@Override
	public void setSharedPic(String sharedPic) {
		this.share_titlepic = sharedPic;
	}

	@Override
	public String getShareIntro() {
		return this.getShare_intro();
	}

}
