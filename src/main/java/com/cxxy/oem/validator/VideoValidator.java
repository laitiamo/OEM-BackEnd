package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class VideoValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		if (c.getFile("video") == null) {
			addError("msg", "请上传视频文件");
		}
		validateRequired("SubjectId", "msg", "请选择课程");
		validateRequiredString("VideoName", "msg", "请输入章节名称");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
