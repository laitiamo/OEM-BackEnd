package com.cxxy.oem.validator;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

import java.util.Date;

public class UploadValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		Date now = new Date(System.currentTimeMillis());
		if (c.getFile("file") == null) {
			addError("msg", "请上传图片");
		}
		if(now.getTime() > (c.getParaToDate("DeadLine").getTime())){
			addError("msg", "当前时间已超出作业截止时间，请与任课老师联系");
		}
		validateRequired("WorkId", "msg", "请选择作业名称");
		validateRequiredString("WorkTime", "msg", "请选择作业完成日期");
		validateRequiredString("WorkPlace", "msg", "请填写作业名次");

	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
