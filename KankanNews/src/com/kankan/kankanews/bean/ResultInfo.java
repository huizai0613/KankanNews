package com.kankan.kankanews.bean;

public class ResultInfo {
	private int resultCode;
	private int errCode;
	private String msg;

	public ResultInfo(int resultCode, int errCode, String msg) {
		super();
		this.resultCode = resultCode;
		this.errCode = errCode;
		this.msg = msg;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
