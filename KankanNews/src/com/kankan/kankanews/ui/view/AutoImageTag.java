package com.kankan.kankanews.ui.view;

public class AutoImageTag {
	private String urlPath;
	private boolean isBig;
	private String id;

	public AutoImageTag(String urlPath, boolean isBig) {
		super();
		this.urlPath = urlPath;
	}

	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public boolean isBig() {
		return isBig;
	}


	public void setBig(boolean isBig) {
		this.isBig = isBig;
	}



	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

}
