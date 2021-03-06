package com.cxxy.oem.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {
	
	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("sys_url", "id", SysUrl.class);
		arp.addMapping("sys_url_role", "id", SysUrlRole.class);
		arp.addMapping("t_class", "id", Class.class);
		arp.addMapping("t_gender", "id", Gender.class);
		arp.addMapping("t_grade", "id", Grade.class);
		arp.addMapping("t_inform", "id", Inform.class);
		arp.addMapping("t_major", "id", Major.class);
		arp.addMapping("t_picture", "id", Picture.class);
		arp.addMapping("t_review", "id", Review.class);
		arp.addMapping("t_role", "id", Role.class);
		arp.addMapping("t_score", "id", Score.class);
		arp.addMapping("t_source", "id", Source.class);
		arp.addMapping("t_student", "id", Student.class);
		arp.addMapping("t_subject", "id", Subject.class);
		arp.addMapping("t_teacher", "id", Teacher.class);
		arp.addMapping("t_teacher_link_student", "id", TeacherLinkStudent.class);
		arp.addMapping("t_user", "id", User.class);
		arp.addMapping("t_user_role", "id", UserRole.class);
		arp.addMapping("t_user_work", "id", UserWork.class);
		arp.addMapping("t_video", "id", Video.class);
		arp.addMapping("t_video_sign", "id", VideoSign.class);
		arp.addMapping("t_work", "id", Work.class);
	}
}

