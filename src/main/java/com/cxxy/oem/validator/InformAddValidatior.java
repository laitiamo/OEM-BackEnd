package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class InformAddValidatior extends Validator {

	@Override
	protected void validate(Controller c) {

		setShortCircuit(true);
		validateRequiredString("title", "msg", "消息标题不能为空");
		validateRequiredString("content", "msg", "消息内容不能为空");
	}

	@Override
	protected void handleError(Controller c) {

		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
