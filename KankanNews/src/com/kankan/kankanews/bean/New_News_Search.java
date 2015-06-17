package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class New_News_Search extends BaseBean<New_News_Search> {

	private String id;// 用mid 作为主键
	private String title;
	private String titleUrl;
	private String titlePic;
	private String sharedPic;
	private String type;
	private String newsTime;
	private String mId;
	private int clickNum;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public New_News_Search parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		id = jsonObj.optString("mid");
		title = jsonObj.optString("title");
		titleUrl = jsonObj.optString("titleurl");
		titlePic = jsonObj.optString("titlepic");
		sharedPic = jsonObj.optString("titlepic");
		type = jsonObj.optString("type");
		newsTime = jsonObj.optString("newstime");
		mId = jsonObj.optString("mid");
		String num = jsonObj.optString("onclick");
		clickNum = num == null || num.equals("") ? 0 : Integer.parseInt(num);

		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleUrl() {
		return titleUrl;
	}

	public void setTitleUrl(String titleUrl) {
		this.titleUrl = titleUrl;
	}

	public String getTitlePic() {
		return titlePic;
	}

	public void setTitlePic(String titlePic) {
		this.titlePic = titlePic;
	}

	public String getSharedPic() {
		return sharedPic;
	}

	public void setSharedPic(String sharedPic) {
		this.sharedPic = sharedPic;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNewsTime() {
		return newsTime;
	}

	public void setNewsTime(String newsTime) {
		this.newsTime = newsTime;
	}

	public String getMId() {
		return mId;
	}

	public void setMId(String mId) {
		this.mId = mId;
	}

	public int getClickNum() {
		return clickNum;
	}

	public void setClickNum(int clickNum) {
		this.clickNum = clickNum;
	}

}
