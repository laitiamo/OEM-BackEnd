package com.cxxy.oem.controller;

import org.apache.shiro.SecurityUtils;

import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.service.UserService;
import com.cxxy.oem.validator.UpdatePasswordValidator;
import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

public class PasswordController extends Controller {

	public void index() {
		renderTemplate("password.html");
	}

	@Before(UpdatePasswordValidator.class)
	public void update() {
		String username = (String) SecurityUtils.getSubject().getPrincipal();
		Integer id = new DbRecord(DbConfig.T_USER)
						.whereEqualTo("username", username)
						.queryFirst()
						.getInt("id");
		String newPass = getPara("new-pass");
		if (UserService.me.updatePassword(id, newPass)) {
			renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "密码修改成功"));
		} else {
			renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "密码修改失败"));
		}
	}

}
