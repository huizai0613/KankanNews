package com.kankan.kankanews.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class subject_List extends BaseBean<subject_List> {

	private LinkedList<Subject_Item> list = new LinkedList<Subject_Item>();
	private int[] sections;
	private String[] keys;
	private String titlePic = "";
	private String intro = "";
	
	private  int[] headerids;


	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public subject_List parseJSON(JSONObject jsonObj)
			throws NetRequestException {

//		checkJson(jsonObj);


		JSONObject ztData = jsonObj.optJSONObject("zt");
		titlePic = ztData.optString("titlepic");
		intro = ztData.optString("intro");

		//标题位置
		ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
		//标题内容
		LinkedList<String> keysList = new LinkedList<String>();
//		ArrayList<Integer> headers = new ArrayList<Integer>();
		
		ArrayList<Integer> headers = new ArrayList<Integer>();
		
		list = new LinkedList<Subject_Item>();

		JSONObject listData = jsonObj.optJSONObject("data");
		 
		
		JSONArray labelArray = null;
		try {
			labelArray = jsonObj.getJSONArray("label");

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (labelArray != null && labelArray.length() > 0) {
			String keyName = null;
			for (int i = 0; i < labelArray.length(); i++) {
				keyName = labelArray.optString(i);
	        	keysList.add(keyName);
	        	
//	        	Subject_Item sectionItem = new Subject_Item();
//	        	sectionItem.setTitle(keyName);
//	        	sectionItem.setDataType(1);
//				list.add(sectionItem);
				
				JSONArray jsonArray = null; 
	        	try {
					jsonArray = listData.getJSONArray(keyName);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 

				if (jsonArray != null && jsonArray.length() > 0) {
					JSONObject jsonObject;
					for (int j = 0; j < jsonArray.length(); j++) {
						jsonObject = jsonArray.optJSONObject(j);
						Subject_Item item = new Subject_Item();
						item.parseJSON(jsonObject);
						list.add(item);
						//headerid
			        	headers.add(i);
					}
				}
	        	
			}
		}

		
/*
		Iterator<?> it = listData.keys();  
		JSONArray jsonArray = null;  
        String keyName = null;  
        while(it.hasNext()){//遍历JSONObject  
        	keyName = (String) it.next().toString(); 
        	keysList.add(keyName);
        	
        	Subject_Item sectionItem = new Subject_Item();
        	sectionItem.setTitle(keyName);
        	sectionItem.setDataType(1);
			list.add(sectionItem);
        	try {
				jsonArray = listData.getJSONArray(keyName);

				if (jsonArray != null && jsonArray.length() > 0) {
					JSONObject jsonObject;
					for (int i = 0; i < jsonArray.length(); i++) {
						jsonObject = jsonArray.optJSONObject(i);
						Subject_Item item = new Subject_Item();
						item.parseJSON(jsonObject);
						list.add(item);
//						headers.add(index);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        }
        
        */

	    for (int i = 0; i < list.size(); i++) {
	    	Subject_Item aa = list.get(i);
	    	if (aa.getDataType() == 1) {
	        	sectionIndices.add(i);
			}
	    }

        sections = new int[sectionIndices.size()];
	    for (int i = 0; i < sectionIndices.size(); i++) {
	        sections[i] = sectionIndices.get(i);
	    }
	    
	    headerids = new int[headers.size()];
	    for (int i = 0; i < headers.size(); i++) {
	    	headerids[i] = headers.get(i);
		}
	    
//	    headerids = new long[headers.size()];
//	    for (int i = 0; i < headers.size(); i++) {
//	    	headerids[i] = headers.get(i);
//	    }
	    
        keys = new String[keysList.size()];  
        keys = keysList.toArray(keys); 
		return this;
        
//		JSONArray jsonArray = jsonObj.optJSONArray("return_result");
//		if (jsonArray != null && jsonArray.length() > 0) {
//			list = new LinkedList<Subject_Item>();
//			item = null;
//			JSONObject jsonObject;
//			for (int i = 0; i < jsonArray.length(); i++) {
//				jsonObject = jsonArray.optJSONObject(i);
//				item = new Subject_Item();
//				item.parseJSON(jsonObject);
//				list.add(item);
//			}
//			return this;
//		}
//		return null;
	}

	public int[] getHeaderids() {
		return headerids;
	}

	public void setHeaderids(int[] headerids) {
		this.headerids = headerids;
	}

	public LinkedList<Subject_Item> getList() {
		return list;
	}

	public void setList(LinkedList<Subject_Item> list) {
		this.list = list;
	}

	public int[] getSections() {
		return sections;
	}

	public void setSections(int[] sections) {
		this.sections = sections;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}


	public String getTitlePic() {
		if(titlePic == null) return "";
		return titlePic;
	}

	public void setTitlePic(String titlePic) {
		this.titlePic = titlePic;
	}

	public String getIntro() {
		if(intro == null) return "";
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}


}
