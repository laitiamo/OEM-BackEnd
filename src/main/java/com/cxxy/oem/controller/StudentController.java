package com.cxxy.oem.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.model.Student;
import com.cxxy.oem.service.UserService;
import com.cxxy.oem.validator.DeleteStudentsValidator;
import com.cxxy.oem.validator.ResetValidator;
import com.cxxy.oem.validator.StudentInfoUpdateValidator;
import com.cxxy.oem.vo.AjaxResult;
import com.cxxy.oem.vo.LayUITableResult;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class StudentController extends Controller {

	public void index() {
//		setAttr("grade", new DbRecord(DbConfig.T_GRADE).orderByASC("id").query());
//		setAttr("major", new DbRecord(DbConfig.T_MAJOR).orderByASC("id").query());
		Map<String, Object> attrMap = new HashMap<String, Object>();
		attrMap.put("grade", new DbRecord(DbConfig.T_GRADE).orderByASC("id").query());
		attrMap.put("major", new DbRecord(DbConfig.T_MAJOR).orderByASC("id").query());
		renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, JSON.toJSONString(attrMap)));
//		renderTemplate("student-management.html");
	}

	public void list() {
		Integer gradeId = getParaToInt("gradeId");
		Integer majorId = getParaToInt("majorId");
		Integer classId = getParaToInt("classId");
		String keyUsername = getPara("keyUsername");
		String keyName = getPara("keyName");
		Integer page = getParaToInt("page");
		Integer limit = getParaToInt("limit");
		String order=getPara("order");
		String field=getPara("field");
		String defaultField="username";
		Page<Record> p = null;
		Record info = UserService.me.getCurrentUserInfo();
		if (info.getStr("roleNameEn").equals(WebConfig.ROLE_ADMIN)) {
			p = new DbRecord(DbConfig.V_STUDENT_INFO)
					.whereEqualTo("gradeId", gradeId)
					.whereEqualTo("majorId", majorId)
					.whereEqualTo("classId", classId)
					.whereEqualTo("username", keyUsername)
					.whereContains("name", keyName)
					.orderBySelect(field, order, defaultField)
					.page(page, limit);
		}else{
			p = new DbRecord(DbConfig.V_STUDENT_INFO_TEACHER)
					.whereEqualTo("teacherId",info.getStr("teaNo"))
					.whereEqualTo("gradeId", gradeId)
					.whereEqualTo("majorId", majorId)
					.whereEqualTo("classId", classId)
					.whereEqualTo("username", keyUsername)
					.whereContains("name", keyName)
					.orderBySelect(field, order, defaultField)
					.page(page, limit);
		}
		renderJson(new LayUITableResult<Record>(AjaxResult.CODE_SUCCESS, "", p.getTotalRow(), p.getList()));
	}

	@Before(ResetValidator.class)
	public void reset() {
		Integer[] ids = getParaValuesToInt("ids[]");

		if (UserService.me.resetPassword(ids)) {
			renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "密码重置成功"));
		} else {
			renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "密码重置失败"));
		}
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
	
	@Before(StudentInfoUpdateValidator.class)
	public void update() {
		String stuNo = getPara("stuNo");
		String modifiedField = getPara("modifiedField");
		String newName = getPara("newName");
		Integer newGradeId = getParaToInt("newGradeId");
		String newMajorName = getPara("newMajorName");
		Integer newClassNo = getParaToInt("newClassNo");

		Record record = new DbRecord(DbConfig.T_STUDENT).whereEqualTo("stuNo", stuNo).queryFirst();
		Integer id = record.getInt("id");

		boolean success = true;
		if (modifiedField.equals("name")) {
			success = new Student().setId(id).setStuName(newName).update();
		} else {
			Integer newMajorId = new DbRecord(DbConfig.T_MAJOR)
								.whereEqualTo("majorName", newMajorName)
								.queryFirst()
								.getInt("id");
			Record classRecord = new DbRecord(DbConfig.T_CLASS)
								.whereEqualTo("classNo", newClassNo)
								.whereEqualTo("majorId", newMajorId)
								.whereEqualTo("gradeId", newGradeId)
								.queryFirst();
		
			if (classRecord==null) {
				success = false;
			}else {
				Integer newClassId = classRecord.getInt("id");
				success = new Student().setId(id).setClassId(newClassId).update();
			}
		}

		if (success) {
			renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "更新成功"));
		} else {
			renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "更新失败"));
		}
	}

	public void delete() {
		final Integer[] studentNos = getParaValuesToInt("studentNos[]");
		boolean recordsDeletionSuccess = true;
		boolean filesDeletionSuccess = true;

		filesDeletionSuccess = UserService.me.deleteFiles(studentNos, WebConfig.ROLE_STUDENT);
		recordsDeletionSuccess = UserService.me.deleteUsers(studentNos, WebConfig.ROLE_STUDENT);

		if (recordsDeletionSuccess && filesDeletionSuccess) {
			renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "学生信息删除成功，已上传文件清理成功"));
		} else if (!recordsDeletionSuccess && !filesDeletionSuccess){
			renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "学生信息删除失败，已上传文件清理失败"));
		} else if (recordsDeletionSuccess && !filesDeletionSuccess) {
			renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "学生信息删除成功，已上传文件清理失败"));
		} else if (!recordsDeletionSuccess && filesDeletionSuccess) {
			renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "学生信息删除失败，已上传文件清理成功"));
		}
	}

}
