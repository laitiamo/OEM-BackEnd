package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class ImportStuValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		if (c.getFile() == null) {
			addError("msg", "请选择上传文件");
		}
		validateInteger("class", "msg", "请选择班级");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
