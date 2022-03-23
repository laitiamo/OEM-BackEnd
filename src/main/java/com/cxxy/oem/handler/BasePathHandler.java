package com.cxxy.oem.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
import com.jfinal.kit.PropKit;

public class BasePathHandler extends Handler {
	//设置接口地址
	String api_path  = PropKit.get("API_Path");

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		request.setAttribute("basePath", api_path);
		next.handle(target, request, response, isHandled);
	}

}
