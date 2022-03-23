package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class LoginValidator extends Validator {
	//登陆验证
	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequiredString("username", "msg", "用户名不能为空");
		validateRequiredString("password", "msg", "密码不能为空");
		validateRequiredString("captcha", "msg", "验证码不能为空");
		validateCaptcha("captcha", "msg", "验证码不正确");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
