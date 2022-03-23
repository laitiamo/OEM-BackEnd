package com.cxxy.oem.controller;

import java.io.File;

import com.cxxy.oem.service.ImportService;
import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;

public class ImportTeaController extends Controller {

	public void index() {
		renderTemplate("import-tea.html");
	}

	public void download() {
		File templateFile = new File(PathKit.getWebRootPath() + File.separator + 
									"download" + File.separator + 
									"template" + File.separator + 
									"template_tea.xls");
		renderFile(templateFile);
	}
	
	public void upload() {
		File xls = getFile().getFile();

		try {
			if (ImportService.me.importTea(xls)) {
				renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "数据导入成功"));
			} else {
				renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "数据导入失败"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "数据导入失败"));
		} finally {
			xls.delete();
		}
	}

}
