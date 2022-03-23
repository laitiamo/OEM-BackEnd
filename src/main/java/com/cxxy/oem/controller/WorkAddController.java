package com.cxxy.oem.controller;

import com.cxxy.oem.model.Work;
import com.cxxy.oem.service.UserService;
import com.cxxy.oem.validator.AddWorkValidator;
import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class WorkAddController extends Controller {

	public void index() {

	}

	@Before(AddWorkValidator.class)
	public void add() throws ParseException {
		String name = getPara("name");
		String deadline = getPara("DeadLine");
		String content = getPara("content");
		boolean success = new Work().setWorkName(name).setWorkContent(content).setSubjectId(getParaToInt("SubjectId")).setDeadLine(new SimpleDateFormat("yyyy-MM-dd").parse(deadline)).save();//parse方法可解析一个日期时间字符串
		if (success) {
			renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "作业布置成功"));
		} else {
			renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "作业布置失败"));
		}
	}

}
