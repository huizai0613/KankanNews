package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.bean.interfaz.CanSharedObject;
import com.kankan.kankanews.exception.NetRequestException;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Transient;

public class New_LivePlay extends BaseBean<New_LivePlay> implements CanSharedObject {

	// "title": "36集连续剧：幸福在哪里（13）",
	// "titlepic":
	// "http://static.statickksmg.com/image/2014/11/03/2d2d602595784a98947074ce090974e3.jpg",
	// "intro": "新闻综合",
	// "type": "正在直播",
	// "streamurl": "http://live-cdn.kksmg.com/channels/tvie/xwzh/m3u8:sd",
	// "catename": "新闻综合",
	// "time": "14:48-15:33",
	// "datetime": "14:48:::15:33"
	@Id
	private String zid;
	private String title;
	private String titlepic;
	private String intro;
	private String type;
	private String streamurl;
	private String catename;
	private String time;
	private String datetime;
	private String titleurl;
	private boolean isOrder;

	public boolean isOrder() {
		return isOrder;
	}

	public void setOrder(boolean isOrder) {
		this.isOrder = isOrder;
	}

	public String getZid() {
		return zid;
	}

	public void setZid(String zid) {
		this.zid = zid;
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

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStreamurl() {
		return streamurl;
	}

	public void setStreamurl(String streamurl) {
		this.streamurl = streamurl;
	}

	public String getCatename() {
		return catename;
	}

	public void setCatename(String catename) {
		this.catename = catename;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	
	public String getTitleurl() {
		return titleurl;
	}

	public void setTitleurl(String titleurl) {
		this.titleurl = titleurl;
	}

	@Override
	public String getTitlelist() {
		// TODO Auto-generated method stub
		return this.title;
	}

	@Override
	public String getTitlepiclist() {
		// TODO Auto-generated method stub
		return this.titlepic;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public New_LivePlay parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		zid = jsonObj.optString("id");
		title = jsonObj.optString("title");
		titlepic = jsonObj.optString("titlepic");
		intro = jsonObj.optString("intro");
		type = jsonObj.optString("type");
		streamurl = jsonObj.optString("streamurl");
		catename = jsonObj.optString("catename");
		time = jsonObj.optString("time");
		datetime = jsonObj.optString("datetime");
		titleurl = jsonObj.optString("titleurl");
		return this;
	}

}
