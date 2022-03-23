package com.cxxy.oem.validator;

import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class DeleteTeachersValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		Integer[] teacherNos = c.getParaValuesToInt("teacherNos[]");
		if (teacherNos == null || teacherNos.length == 0) {
			addError("msg", "至少选择一个用户进行操作");
		}
		for (int i = 0; i < teacherNos.length; i++) {
			String roleName = new DbRecord(DbConfig.V_TEACHER_INFO).whereEqualTo("teaNo", teacherNos[i]).queryFirst().get("roleNameEn");
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
