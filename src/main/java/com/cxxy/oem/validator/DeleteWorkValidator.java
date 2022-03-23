package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class DeleteWorkValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		Integer[] ids = c.getParaValuesToInt("ids[]");
		if (ids == null || ids.length == 0) {
			addError("msg", "至少选择一个作业进行操作");
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
