package com.cxxy.oem.controller;

import java.io.File;
import java.util.List;

import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.service.ImportService;
import com.cxxy.oem.validator.ImportStuValidator;
import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Record;

public class ImportStuController extends Controller {

	public void index() {
		setAttr("grade", new DbRecord(DbConfig.T_GRADE).orderByASC("id").query());
		setAttr("major", new DbRecord(DbConfig.T_MAJOR).orderByASC("id").query());
		renderTemplate("import-stu.html");
	}

	public void download() {
		File templateFile = new File(PathKit.getWebRootPath() + File.separator + 
									"download" + File.separator + 
									"template" + File.separator + 
									"template_stu.xls");
		renderFile(templateFile);
	}

	@Before(ImportStuValidator.class)
	public void upload() {
		File xls = getFile().getFile();	
		Integer classId = getParaToInt("class");
		try {
			if (ImportService.me.importStu(xls, classId)) {
				renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "数据导入成功"));
			} else {
				renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "数据导入失败"));
			}
		} catch (Exception e) {
			renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "数据导入失败，上传表格不符合规则"));
			e.printStackTrace();
		} finally {
			xls.delete();
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
