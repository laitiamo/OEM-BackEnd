package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class TeacherInfoUpdateValidator extends Validator {
	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequiredString("newName", "msg", "姓名不能为空");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}
}
