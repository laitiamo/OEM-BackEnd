package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class TeacherWorkDeleteValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		// TODO Auto-generated method stub
		setShortCircuit(true);
		Integer id = c.getParaToInt("id");
		if (id == null) {
			addError("msg", "该信息不存在");
		}
	}

	@Override
	protected void handleError(Controller c) {
		// TODO Auto-generated method stub
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
