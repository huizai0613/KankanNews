package com.kankan.kankanews.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class New_NewsPic extends BaseBean<New_NewsPic> {

	private String id; // mid
	private String title;
	private String titlepic;
	private String keyboard;
	private String picsay;
	private String imagegroup;

	public String getid() {
		return id;
	}

	public void setid(String id) {
		this.id = id;
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

	public String getKeyboard() {
		return keyboard;
	}

	public void setKeyboard(String keyboard) {
		this.keyboard = keyboard;
	}

	public String getPicsay() {
		return picsay;
	}

	public void setPicsay(String picsay) {
		this.picsay = picsay;
	}

	public String getImagegroup() {
		return imagegroup;
	}

	public void setImagegroup(String imagegroup) {
		this.imagegroup = imagegroup;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[][] parseImagegroup() {
		String[][] images = null;
		try {
			JSONArray jsonArray = new JSONArray(imagegroup);

			if (jsonArray != null && jsonArray.length() > 0) {
				int length = jsonArray.length();
				images = new String[length][2];
				for (int i = 0; i < length; i++) {
					JSONObject optJSONObject = jsonArray.optJSONObject(i);
					String optString = optJSONObject.optString("title");
					images[i][0] = optString == null ? "" : optString;
					images[i][1] = optJSONObject.optString("src");
				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return images;
	}

	@Override
	public New_NewsPic parseJSON(JSONObject jsonObj) throws NetRequestException {

		// checkJson(jsonObj);

		title = jsonObj.optString("title");
		titlepic = jsonObj.optString("titlepic");
		keyboard = jsonObj.optString("keyboard");
		picsay = jsonObj.optString("picsay");
		JSONArray optJSONArray = jsonObj.optJSONArray("imagegroup");
		if (optJSONArray != null) {
			imagegroup = optJSONArray.toString();
		}

		return this;
	}

}
