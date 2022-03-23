package com.cxxy.oem.controller;

import java.sql.SQLException;

import org.apache.shiro.SecurityUtils;

import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.service.UserService;
import com.cxxy.oem.vo.AjaxResult;
import com.cxxy.oem.vo.LayUITableResult;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class MineController extends Controller {

	public void index() {
		renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "1"));
	}

	public void list() {
		int page = getParaToInt("page");
		int limit = getParaToInt("limit");
		String order=getPara("order");
		String field=getPara("field");
		String defaultField="createAt";
		Page<Record> p = new DbRecord(DbConfig.V_STUDENT_WORK)
								.whereEqualTo("userId", UserService.me.getCurrentUser().getInt("id"))
								.orderBySelect(field,order,defaultField)
								.page(page, limit);
		renderJson(new LayUITableResult<Record>(AjaxResult.CODE_SUCCESS, "", p.getTotalRow(), p.getList()));
	}

	public void detail() {
		int id = getParaToInt("id");
			redirect("/oem/detail-stu?id=" + id);
	}
	
	public void delete() {
		final Integer id = getParaToInt("id");
		
		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				boolean success = true;
				Record r = new DbRecord(DbConfig.V_STUDENT_WORK).whereEqualTo("id", id).whereEqualTo("reviewId", WebConfig.REVIEW_NOT_PASS).queryFirst();
				if (!Db.delete(DbConfig.T_USER_WORK, "id", r)) {
					success = false;
				}
				if (success) {
					renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "删除成功"));
				} else {
					renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "删除失败"));
				}
				return success;
			}
		});
	}

}
