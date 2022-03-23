package com.cxxy.oem.controller;

import com.cxxy.oem.model.Subject;
import com.cxxy.oem.validator.AddSubjectValidator;
import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

public class SubjectAddController extends Controller {

	public void index() {

	}

	@Before(AddSubjectValidator.class)
	public void add() {
		String subjectName = getPara("name");
		boolean success = new Subject().setSubjectName(subjectName).save();
		if (success) {
			renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "课程添加成功"));
		} else {
			renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "课程添加失败"));
		}
	}

}
