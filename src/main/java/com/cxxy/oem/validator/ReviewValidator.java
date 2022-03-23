package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

import java.util.Date;

public class ReviewValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequired("score", "msg", "成绩不能为空");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
