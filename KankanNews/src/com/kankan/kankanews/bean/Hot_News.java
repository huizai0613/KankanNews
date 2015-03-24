package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;
import com.lidroid.xutils.db.annotation.Id;

public class Hot_News extends BaseBean<Hot_News> {

	@Id
	private String id;
	private String title;
	private String titlepic;
	private String onclick;
	private String sharepic;
	private String m_url;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hot_News parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optString("id");
		title = jsonObj.optString("title");
		onclick = jsonObj.optString("onclick");
		titlepic = jsonObj.optString("titlepic");
		sharepic = jsonObj.optString("sharepic");
		m_url = jsonObj.optString("m_url");

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

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getTitlepic() {
		return titlepic;
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public String getSharepic() {
		return sharepic;
	}

	public void setSharepic(String sharepic) {
		this.sharepic = sharepic;
	}

	public String getM_url() {
		return m_url;
	}

	public void setM_url(String m_url) {
		this.m_url = m_url;
	}

}
