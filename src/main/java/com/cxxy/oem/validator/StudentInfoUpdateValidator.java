package com.cxxy.oem.validator;

import java.util.List;

import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.validate.Validator;

public class StudentInfoUpdateValidator extends Validator {
	
	@Override
	protected void validate(Controller c) {

		setShortCircuit(true);
		
		//空字段验证
		validateRequiredString("newName", "msg", "姓名不能为空");
		validateRequiredString("newGradeId", "msg", "年级不能为空");
		validateRequiredString("newMajorName", "msg", "专业名不能为空");
		validateRequiredString("newClassNo", "msg", "班号不能为空");
		
		
		//字段格式验证
		validateRegex("newGradeId", "20[0-9][0-9]", "msg", "年级格式错误，正确格式示例：2015");
		
		List<Record> records = new DbRecord(DbConfig.T_MAJOR).query();
		boolean majorNameExist = false;
		for (Record r : records) {
			String majorName = r.getStr("majorName");
			if (c.getPara("newMajorName").equals(majorName)) {
				majorNameExist = true;
				break;
			}
		}
		if (!majorNameExist) {
			addError("msg", "专业名不在已有专业列表中，请检查是否输入错误。<br/>若为新增专业，请联系管理员在数据库添加。");
		}
		
		validateRegex("newClassNo", "[1-9]", "msg", "班号格式错误");
	}

	@Override
	protected void handleError(Controller c) {

		c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, c.getAttrForStr("msg")));
	}

}
