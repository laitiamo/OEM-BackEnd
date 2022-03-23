package com.cxxy.oem.validator;

import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class SetRoleValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		Integer[] ids = c.getParaValuesToInt("ids[]");
		if (ids == null || ids.length == 0) {
			addError("msg", "至少选择一个用户进行操作");
		}
		validateInteger("roleId", "msg", "请选择角色");
		Integer roleId = c.getParaToInt("roleId");
		Integer[] classIds = c.getParaValuesToInt("classIds[]");
		if ((roleId == WebConfig.ROLE_TEACHER_ID && classIds == null) || 
			(roleId == WebConfig.ROLE_TEACHER_ID && classIds.length == 0)) {
			addError("msg", "至少选择一个管理的班级");
		} else if (roleId == WebConfig.ROLE_TEACHER_ID && ids.length != 1) {
			addError("msg", "设置该角色不能批量操作");
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
