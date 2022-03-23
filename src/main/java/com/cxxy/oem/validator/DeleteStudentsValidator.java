package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class DeleteStudentsValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		Integer[] studentNos = c.getParaValuesToInt("studentNos[]");
		if (studentNos == null || studentNos.length == 0) {
			addError("msg", "至少选择一个用户进行操作");
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}
	
}
