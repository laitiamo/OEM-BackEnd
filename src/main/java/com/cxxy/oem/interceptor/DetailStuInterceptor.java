package com.cxxy.oem.interceptor;

import java.util.List;

import org.apache.shiro.SecurityUtils;

import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.service.UserService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.plugin.activerecord.Record;

public class DetailStuInterceptor implements Interceptor {

	public void intercept(Invocation inv) {
		boolean success = false;
		Record info = UserService.me.getCurrentUserInfo();
		if (info.getInt("roleId") != WebConfig.ROLE_STUDENT_ID) {
			success = true;
		} else {
			List<Record> records = new DbRecord(DbConfig.V_STUDENT_WORK)
									.whereEqualTo("username", SecurityUtils.getSubject().getPrincipal())
									.query();
			for (Record r : records) {
				if ((int)r.getInt("id") == inv.getController().getParaToInt("id")) {
					success = true;
					break;
				}
			}
		}
		if (success) {
			inv.invoke();
		} else {
			inv.getController().renderTemplate("refuse.html");
		}
	}

}
