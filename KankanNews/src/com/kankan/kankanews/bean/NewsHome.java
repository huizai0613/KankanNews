package com.kankan.kankanews.bean;

import java.io.Serializable;
import java.util.List;

public class NewsHome implements Serializable{
	private String update;
	private String module_num;
	private List<NewsHomeModule> module_list;

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getModule_num() {
		return module_num;
	}

	public void setModule_num(String module_num) {
		this.module_num = module_num;
	}

	public List<NewsHomeModule> getModule_list() {
		return module_list;
	}

	public void setModule_list(List<NewsHomeModule> module_list) {
		this.module_list = module_list;
	}

}
