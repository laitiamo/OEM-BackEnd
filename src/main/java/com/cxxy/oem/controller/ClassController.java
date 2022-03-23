package com.cxxy.oem.controller;

import com.alibaba.fastjson.JSON;
import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.service.UserService;
import com.cxxy.oem.vo.AjaxResult;
import com.cxxy.oem.vo.LayUITableResult;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassController extends Controller {

	public void index() {
//		setAttr("rank", new DbRecord(DbConfig.T_RANK).query());
//		renderTemplate("query-tea.html");
		Map<String, Object> attrMap = new HashMap<String, Object>();
		attrMap.put("grade", new DbRecord(DbConfig.T_GRADE).orderByASC("id").query());
		attrMap.put("major", new DbRecord(DbConfig.T_MAJOR).orderByASC("id").query());
		renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, JSON.toJSONString(attrMap)));
	}

	public void list() {
		Integer page = getParaToInt("page");
		Integer limit = getParaToInt("limit");
		Integer gradeId = getParaToInt("gradeId");
		Integer majorId = getParaToInt("majorId");
		Integer classId = getParaToInt("classId");
		Integer classNo = getParaToInt("classNo");
		String keyName = getPara("keyName");
		String keyUserName = getPara("keyUserName");
		String order=getPara("order");
		String field=getPara("field");
		String defaultField="id";
		
		Page<Record> p = new DbRecord(DbConfig.V_TEACHER_CLASS_INFO)
						.whereEqualTo("gradeId", gradeId)
						.whereEqualTo("majorId", majorId)
						.whereEqualTo("classId", classId)
						.whereEqualTo("classNo", classNo)
						.whereContains("teaName", keyName)
						.whereContains("teacherId", keyUserName)
						.orderBySelect(field,order,defaultField)
						.page(page, limit);
		renderJson(new LayUITableResult<Record>(AjaxResult.CODE_SUCCESS, "", p.getTotalRow(), p.getList()));
	}

	public void listClass() {
		Integer gradeId = getParaToInt("gradeId");
		Integer majorId = getParaToInt("majorId");
		List<Record> result = new DbRecord(DbConfig.T_CLASS)
							.whereEqualTo("gradeId", gradeId)
							.whereEqualTo("majorId", majorId)
							.query();
		renderJson(result);
	}

	public void delete() {
		final Integer[] ids = getParaValuesToInt("ids[]");
		boolean recordsDeletionSuccess = true;
		recordsDeletionSuccess = UserService.me.delLinkClass(ids);

		if (recordsDeletionSuccess) {
			renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "教师与班级关系解绑成功"));
		} else if (!recordsDeletionSuccess) {
			renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "教师与班级关系解绑失败"));
		}
	}
}
