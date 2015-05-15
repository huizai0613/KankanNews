package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class Subject_Item extends BaseBean<Subject_Item> {

	private String id;
	private String title;
	private String titleurl;
	private String titlepic;
	private String sharedPic;
	private String type;
	private String sourceid;
	private String newstime;
	private String spclassid;
	private String ztid;
	private String mid;
	
	private int dataType;//类型1、标题  2、内容
	
	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Subject_Item parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optString("id");
		title = jsonObj.optString("title");
		titleurl = jsonObj.optString("titleurl");
		titlepic = jsonObj.optString("titlepic");
		sharedPic = jsonObj.optString("titlepic");
		type = jsonObj.optString("type");
		sourceid = jsonObj.optString("sourceid");
		newstime = jsonObj.optString("newstime");
		spclassid = jsonObj.optString("spclassid");
		ztid = jsonObj.optString("ztid");
		mid = jsonObj.optString("mid");
		
		dataType = 2;
		
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
	public String getTitleurl() {
		return titleurl;
	}
	public void setTitleurl(String titleurl) {
		this.titleurl = titleurl;
	}
	public String getTitlepic() {
		return titlepic;
	}
	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSourceid() {
		return sourceid;
	}
	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}
	public String getNewstime() {
		return newstime;
	}
	public void setNewstime(String newstime) {
		this.newstime = newstime;
	}
	public String getSpclassid() {
		return spclassid;
	}
	public void setSpclassid(String spclassid) {
		this.spclassid = spclassid;
	}
	public String getZtid() {
		return ztid;
	}
	public void setZtid(String ztid) {
		this.ztid = ztid;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public String getSharedPic() {
		return sharedPic;
	}
	public void setSharedPic(String sharedPic) {
		this.sharedPic = sharedPic;
	}

}
