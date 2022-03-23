package com.cxxy.oem.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.interceptor.DetailStuInterceptor;
import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

public class DetailStuController extends Controller {

	@Before(DetailStuInterceptor.class)
	public void index() {
		Integer id = getParaToInt("id");
		Record r = new DbRecord(DbConfig.V_STUDENT_WORK).whereEqualTo("id", id).queryFirst();
		Map<String, Object> attrMap = new HashMap<String, Object>();
		//多图优化，字符分割 2020-10-20

		String imagePath=r.getStr("imagePath");
		ArrayList<String> images = new ArrayList<String>();
		if(imagePath.indexOf("*")!=-1) {
			String[] parts = imagePath.split("\\*");
			//如果存在"*"则分割，取出图片数量
			Integer picNum = Integer.parseInt(parts[1]);
			String[] pathParts = parts[0].split("\\.");
			String pathPart = pathParts[0];
			for (int i = 0; i < picNum; i++){
				//图片url补全 2021-2-25
	            images.add(getAttr("basePath").toString()+pathPart+"_"+i+".jpeg");
	        }
		}else {
			images.add(getAttr("basePath").toString()+imagePath);
		}

		attrMap.put("imagePaths", images);
		attrMap.put("username", r.getStr("username"));
		attrMap.put("name", r.getStr("name"));
		attrMap.put("gender", r.getStr("genderName"));
		attrMap.put("class", r.getStr("className"));
		attrMap.put("subject", r.getStr("SubjectName"));
		attrMap.put("work", r.getStr("WorkName"));
		attrMap.put("score", r.getStr("score") == null? "未批阅" : r.getStr("score"));
		attrMap.put("scoreName", r.getStr("scoreName"));
		attrMap.put("place", r.getStr("WorkPlace"));
		attrMap.put("time", new SimpleDateFormat("yyyy/MM/dd").format(r.getDate("WorkTime")));
		attrMap.put("createAt", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(r.getDate("createAt")));
		attrMap.put("review", r.getStr("reviewName"));
		attrMap.put("reviewAt", r.getDate("reviewAt") == null ? 
			"" : new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(r.getDate("reviewAt")));
		setAttrs(attrMap);

		//向前端发送全部attribute

		renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, JSON.toJSONString(attrMap)));
	}

}
