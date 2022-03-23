package com.cxxy.oem.vo;

import java.util.List;

public class ElementTableResult<T> extends AjaxResult {

	private int count;
	private List<T> data;

	public ElementTableResult(int code, String msg, int count, List<T> data) {
		super(code, msg);
		this.count = count;
		this.data = data;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

}
