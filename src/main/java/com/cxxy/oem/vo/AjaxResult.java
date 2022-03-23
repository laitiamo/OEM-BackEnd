package com.cxxy.oem.vo;

public class AjaxResult {

	public static final int CODE_SUCCESS = 0;
	public static final int CODE_ERROR = -1;

	private int code;
	private String msg;

	public AjaxResult(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
