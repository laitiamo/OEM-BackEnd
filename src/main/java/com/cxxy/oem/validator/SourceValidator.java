package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class SourceValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		if (c.getFile("source") == null) {
			addError("msg", "请上传资源文件");
		}
		validateRequired("SubjectId", "msg", "请选择课程");
		validateRequired("VideoId", "msg", "请选择章节");
		validateRequiredString("SourceName", "msg", "请输入文件名称");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
