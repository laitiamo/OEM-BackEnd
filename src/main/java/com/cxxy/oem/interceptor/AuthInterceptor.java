package com.cxxy.oem.interceptor;

import java.util.List;

import com.cxxy.oem.vo.AjaxResult;
import com.jfinal.core.Controller;
import org.apache.shiro.SecurityUtils;

import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.plugin.activerecord.Record;


/*
  * 权限拦截器类：
  * 功能：权限验证拦截的，即不同身份的人只能看到与自己身份相对应的界面，否则会显示拒绝访问
  * 安全方式属于编程式：通过if/else授权代码完成
 * List<Record> records = new DbRecord(DbConfig.SYS_URL_ROLE).include("urlId").include("roleId")
 *.whereEqualTo("url", "'" + inv.getControllerKey() + "'").query();
  * 现将查询出来的什么身份的人应能访问的资源符地址相关记录找出来放入list里，然再依次遍历
 * Record r : records java中特有的写法
 * SecurityUtils.getSubject().hasRole(r.getStr("roleNameEn"))判断查询的记录角色身份与当前登录的角色身份是否匹配
  * 如果匹配，就让其访问该资源，否则返回"无法访问的相应界面"
 * */
public class AuthInterceptor implements Interceptor {

	public void intercept(Invocation inv) {
		boolean success = false;
		List<Record> records = new DbRecord(DbConfig.SYS_URL_ROLE).include("urlId").include("roleId")
				.whereEqualTo("url", inv.getControllerKey()).query();
		for (Record r : records) {
			if (SecurityUtils.getSubject().hasRole(r.getStr("roleNameEn"))) {
				success = true;
				break;
			}
		}
		if (success) {
			inv.invoke();
		} else {
			Controller c = inv.getController();
			c.renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "无权限访问该页面"));
		}
	}

}
