package com.cxxy.oem.controller;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.model.Work;
import com.cxxy.oem.validator.DeleteWorkValidator;
import com.cxxy.oem.validator.UpdateWorkValidator;
import com.cxxy.oem.vo.AjaxResult;
import com.cxxy.oem.vo.LayUITableResult;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class    WorkController extends Controller {

    public void index() {
        renderTemplate("work.html");
    }

    public void list() {
        Integer page = getParaToInt("page");
        Integer limit = getParaToInt("limit");
        String key = getPara("key");
        String order = getPara("order");
        String field = getPara("field");
        String defaultField = "id";
        Page<Record> p = null;
        p = new DbRecord(DbConfig.V_WORK_INFO)
                .whereContains("WorkName", key)
                .orderBySelect(field, order, defaultField)
                .page(page, limit);
        renderJson(new LayUITableResult<Record>(AjaxResult.CODE_SUCCESS, "", p.getTotalRow(), p.getList()));
    }

    @Before(UpdateWorkValidator.class)
    public void update() throws ParseException {
        Integer id = getParaToInt("id");
        String newContent = getPara("newContent");
        String newDeadLine = getPara("newDeadLine");

        boolean success = new Work().setId(id).setWorkContent(newContent).setDeadLine(new SimpleDateFormat("yyyy-MM-dd").parse(newDeadLine)).update();

        if (success) {
            renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "更新成功"));
        } else {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "更新失败"));
        }
    }

    @Before(DeleteWorkValidator.class)
    public void delete() {
        final Integer[] ids = getParaValuesToInt("ids[]");

        Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                boolean success = true;
                for (int i = 0; i < ids.length; i++) {
                    Record r = new DbRecord(DbConfig.T_WORK).whereEqualTo("id", ids[i]).queryFirst();
                    if (!Db.delete(DbConfig.T_WORK, "id", r)) {
                        success = false;
                    }
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
