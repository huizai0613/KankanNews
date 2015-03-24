package com.kankan.kankanews.bean;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class Comment_List extends BaseBean<Comment_List> {

	private Comment comment;
	private LinkedList<Comment> comment_list;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comment_List parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		checkJson(jsonObj);

		JSONArray jsonArray = jsonObj.optJSONArray("return_result");
		if (jsonArray != null && jsonArray.length() > 0) {
			comment_list = new LinkedList<Comment>();
			comment = null;
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.optJSONObject(i);
				comment = new Comment();
				comment.parseJSON(jsonObject);
				comment_list.add(comment);
			}
			return this;
		}
		return null;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public LinkedList<Comment> getComment_list() {
		return comment_list;
	}

	public void setComment_list(LinkedList<Comment> comment_list) {
		this.comment_list = comment_list;
	}

}
