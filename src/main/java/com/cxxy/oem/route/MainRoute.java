package com.cxxy.oem.route;

import com.cxxy.oem.controller.*;
import com.cxxy.oem.interceptor.AuthInterceptor;
import com.cxxy.oem.interceptor.CORSInterceptor;
import com.cxxy.oem.interceptor.MainLayoutInterceptor;
import com.cxxy.oem.model.Subject;
import com.jfinal.config.Routes;

public class MainRoute extends Routes {

	@Override
	public void config() {
		setBaseViewPath("/view");
		addInterceptor(new CORSInterceptor()); //设置允许跨域
		addInterceptor(new MainLayoutInterceptor());	//主要弹出层拦截类（公用模板那边用来显示登录用户的个人详细用的）
		addInterceptor(new AuthInterceptor());   //设置权限拦截器类

		add("/oem/", HomeController.class, "/");
		add("/oem/home", HomeController.class, "/");
		add("/oem/upload", UploadController.class, "/");
		add("/oem/mine", MineController.class, "/");
		add("/oem/review", ReviewController.class, "/");
		add("/oem/query-stu", QueryStuController.class, "/");
		add("/oem/work", WorkController.class, "/");
		add("/oem/work-add", WorkAddController.class, "/");
		add("/oem/subject", SubjectController.class, "/");
		add("/oem/video", VideoController.class, "/");
		add("/oem/subject-add", SubjectAddController.class, "/");
		add("/oem/student-management", StudentController.class, "/");
		add("/oem/teacher-management", TeacherController.class, "/");
		add("/oem/class-management", ClassController.class, "/");
		add("/oem/import-stu", ImportStuController.class, "/");
		add("/oem/import-tea", ImportTeaController.class, "/");
		add("/oem/password", PasswordController.class, "/");
		add("/oem/logout", LogoutController.class, "/");
		add("/oem/detail-stu", DetailStuController.class, "/");
		add("/oem/notice",NoticeController.class, "/");
	}
}
