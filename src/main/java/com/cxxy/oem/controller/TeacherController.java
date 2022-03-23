package com.cxxy.oem.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.model.Teacher;
import com.cxxy.oem.service.UserService;
import com.cxxy.oem.validator.DeleteTeachersValidator;
import com.cxxy.oem.validator.ResetValidator;
import com.cxxy.oem.validator.SetRoleValidator;
import com.cxxy.oem.validator.TeacherInfoUpdateValidator;
import com.cxxy.oem.vo.AjaxResult;
import com.cxxy.oem.vo.LayUITableResult;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class TeacherController extends Controller {

    public void index() {
//		setAttr("grade", new DbRecord(DbConfig.T_GRADE).orderByASC("id").query());
//		setAttr("major", new DbRecord(DbConfig.T_MAJOR).orderByASC("id").query());
//		setAttr("role", new DbRecord(DbConfig.T_ROLE).whereNotEqualTo("id", WebConfig.ROLE_STUDENT_ID).query());
//		renderTemplate("teacher-management.html");

        Map<String, Object> attrMap = new HashMap<String, Object>();
        attrMap.put("grade", new DbRecord(DbConfig.T_GRADE).orderByASC("id").query());
        attrMap.put("major", new DbRecord(DbConfig.T_MAJOR).orderByASC("id").query());
        attrMap.put("role", new DbRecord(DbConfig.T_ROLE).whereNotEqualTo("id", WebConfig.ROLE_STUDENT_ID).query());
        renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, JSON.toJSONString(attrMap)));
    }

    public void list() {
        Integer roleId = getParaToInt("roleId");
        String keyUsername = getPara("keyUsername");
        String keyName = getPara("keyName");
        Integer page = getParaToInt("page");
        Integer limit = getParaToInt("limit");
        String order = getPara("order");
        String field = getPara("field");
        String defaultField = "username";

        Page<Record> p = new DbRecord(DbConfig.V_TEACHER_INFO)
                .whereEqualTo("username", keyUsername)
                .whereContains("name", keyName)
                .whereEqualTo("roleId", roleId)
                .orderBySelect(field, order, defaultField)
                .page(page, limit);
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

    @Before(SetRoleValidator.class)
    public void setRole() {
        Integer[] ids = getParaValuesToInt("ids[]");
        Integer roleId = getParaToInt("roleId");
        Integer[] oldroleIds = getParaValuesToInt("oldroleIds[]");
        Integer[] classIds = getParaValuesToInt("classIds[]");
        String[] teacherIds = getParaValues("usernames[]");

        if (UserService.me.setRole(ids, roleId)) {
            if (UserService.me.addLink(classIds, teacherIds, oldroleIds)) {
                renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "角色设置成功"));
            } else {
                renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "角色设置失败"));
            }
        } else if (UserService.me.delLink(oldroleIds, teacherIds)) {
            renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "角色设置成功"));
        } else {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "角色设置失败"));
        }
    }


    @Before(TeacherInfoUpdateValidator.class)
    public void update() {
        String username = getPara("username");
        String newName = getPara("newName");

        Record id = new DbRecord(DbConfig.T_TEACHER).whereEqualTo("teaNo", username).queryFirst();
        Integer ID = id.getInt("id");

        boolean success = new Teacher().setId(ID).setTeaName(newName).update();

        if (success) {
            renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "更新成功"));
        } else {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "更新失败"));
        }
    }

    // TODO: 如果是系统管理员，则不能删除该用户，请联系系统管理员
    @Before(DeleteTeachersValidator.class)
    public void delete() {
        final Integer[] teacherNos = getParaValuesToInt("teacherNos[]");
        boolean recordsDeletionSuccess = true;
        boolean filesDeletionSuccess = true;

        filesDeletionSuccess = UserService.me.deleteFiles(teacherNos, WebConfig.ROLE_TEACHER);
        recordsDeletionSuccess = UserService.me.deleteUsers(teacherNos, WebConfig.ROLE_TEACHER);

        if (recordsDeletionSuccess && filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "教师信息删除成功，已上传文件清理成功"));
        } else if (!recordsDeletionSuccess && !filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "教师信息删除失败，已上传文件清理失败"));
        } else if (recordsDeletionSuccess && !filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "教师信息删除成功，已上传文件清理失败"));
        } else {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "教师信息删除失败，已上传文件清理成功"));
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
}
