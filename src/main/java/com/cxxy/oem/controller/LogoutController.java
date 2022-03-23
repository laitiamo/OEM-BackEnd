package com.cxxy.oem.controller;

import com.cxxy.oem.service.UserService;
import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;

public class LogoutController extends Controller {
	public void index() {
		if (UserService.me.logout()) {
			renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "已退出登陆"));
		}
	}

}
