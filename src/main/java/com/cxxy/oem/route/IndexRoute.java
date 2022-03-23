package com.cxxy.oem.route;

import com.cxxy.oem.controller.LoginController;
import com.cxxy.oem.interceptor.CORSInterceptor;
import com.jfinal.config.Routes;

public class IndexRoute extends Routes {

	@Override
	public void config() {
		setBaseViewPath("/view");
		addInterceptor(new CORSInterceptor()); //设置允许跨域
		add("/", LoginController.class, "/");
	}

}
