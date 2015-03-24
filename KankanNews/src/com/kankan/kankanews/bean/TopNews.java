package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.base.Error;
import com.kankan.kankanews.exception.NetRequestException;

public class TopNews extends BaseBean<TopNews> {

	private String id;
	private String ititled;
	private String titlepic;
	private String labels;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopNews parseJSON(JSONObject jsonObj) throws NetRequestException {

		if (jsonObj != null) {
			id = jsonObj.optString("id");
			ititled = jsonObj.optString("title");
			titlepic = jsonObj.optString("titlepic");
			labels = jsonObj.optString("labels");
		} else {
			throw new NetRequestException(new Error());
		}
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItitled() {
		return ititled;
	}

	public void setItitled(String ititled) {
		this.ititled = ititled;
	}

	public String getTitlepic() {
		return titlepic;
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

}
