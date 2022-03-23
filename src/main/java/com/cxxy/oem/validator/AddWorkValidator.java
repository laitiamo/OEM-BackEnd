package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class AddWorkValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequiredString("SubjectId", "msg", "课程不能为空");
		validateRequiredString("name", "msg", "作业名称不能为空");
		validateRequiredString("DeadLine", "msg", "请选择作业截止日期");
		validateRequiredString("content", "msg", "作业内容不能为空");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
