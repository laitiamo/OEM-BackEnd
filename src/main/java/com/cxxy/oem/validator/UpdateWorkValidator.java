package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class UpdateWorkValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequiredString("newContent", "msg", "作业内容不能为空");
		validateRequiredString("newDeadLine", "msg", "截止日期不能为空");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
