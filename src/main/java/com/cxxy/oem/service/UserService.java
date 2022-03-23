package com.cxxy.oem.service;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.cxxy.oem.common.Md5Util;
import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.model.User;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 用户相关业务类
 *
 * @author LZH
 */
public class UserService {

    public static final UserService me = new UserService();

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_STUDENT = "student";

    private UserService() {

    }

    /**
     * 获得当前登录用户的基本信息
     */
    public Record getCurrentUser() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        return new DbRecord(DbConfig.T_USER).whereEqualTo("username", username).queryFirst();
    }

    /**
     * 获得当前登录用户的详细信息
     */
    public Record getCurrentUserInfo() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (SecurityUtils.getSubject().hasRole(ROLE_STUDENT)) {
            return new DbRecord(DbConfig.V_STUDENT_INFO).whereEqualTo("username", username).queryFirst();
        } else {
            return new DbRecord(DbConfig.V_TEACHER_INFO).whereEqualTo("username", username).queryFirst();
        }
    }

    /**
     * 用户登录
     */
    public boolean login(String username, String password) {
        password = Md5Util.Md5(password, username);  //加密认证
        //System.out.println(password);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password); //安全令牌 封装即将验证的用户名和密码
        Subject subject = SecurityUtils.getSubject();  //认证对象 审核令牌 获取subject对象，即即将登陆的用户创建subject实例
        try {
            subject.login(token);  //得到login对象 认证失败报异常 交给当前封装好的方法
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 用户退出登录
     */
    public boolean logout() {
        try {
            SecurityUtils.getSubject().logout();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 重置用户密码
     */
    public boolean resetPassword(Integer[] ids) {
        final List<Record> records = new ArrayList<Record>(ids.length);
        for (int i = 0; i < ids.length; i++) {
            Record record = new DbRecord(DbConfig.T_USER).whereEqualTo("id", ids[i]).queryFirst();
            record.set("password", Md5Util.Md5(record.getStr("username"), record.getStr("username"))); //加密重置
            records.add(record);
        }
        return Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                Db.batchUpdate(DbConfig.T_USER, "id", records, records.size());
                return true;
            }
        });
    }

    /**
     * 设置用户角色
     */
    public boolean setRole(Integer[] ids, Integer roleId) {
        final List<Record> records = new ArrayList<Record>(ids.length);
        for (int i = 0; i < ids.length; i++) {
            Record record = new DbRecord(DbConfig.T_USER_ROLE).whereEqualTo("userId", ids[i]).queryFirst();
            record.set("roleId", roleId);
            records.add(record);
        }
        return Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                Db.batchUpdate(DbConfig.T_USER_ROLE, "id", records, records.size());
                return true;
            }
        });
    }

    /**
     * 添加管理连接
     */
    public boolean addLink(Integer[] classIds, String[] teacherIds, Integer[] oldroleIds) {
        final List<Record> records = new ArrayList<Record>();
        if (oldroleIds[0] == WebConfig.ROLE_TEACHER_ID && classIds != null) {// 如果原来身份是教师且设置了管理班级，减少冗余数据的增加
            List<Record> ids = new DbRecord(DbConfig.T_TEACHER_LINK_STUDENT)
                    .whereEqualTo("teacherId", teacherIds[0]).query();
            for (int i = 0; i < classIds.length; i++) {
                if (ids.size() > 0) {
                    for (int j = 0; j < ids.size(); j++) {
                        if (classIds[i].intValue() == ids.get(j).getInt("classId")) // 如果该教师已经管理该班级，就不需要重复添加
                            break;
                        else {
                            if (j == ids.size() - 1) { // 如果辅导员没有管理该班级，就将其加入记录中的等待更新
                                Record record = new Record();
                                record.set("classId", classIds[i]).set("teacherId", teacherIds[0]);
                                records.add(record);
                            } else {
                                continue;
                            }
                        }
                    }
                } else { //如果身份为教师但是没有管理任何班级，就将其加入记录中的等待更新
                    Record record = new Record();
                    record.set("classId", classIds[i]).set("teacherId", teacherIds[0]);
                    records.add(record);
                }
            }
        } else { // 添加系统管理员用的
            for (int i = 0; i < classIds.length; i++) {
                Record record = new Record();
                record.set("classId", classIds[i]).set("teacherId", teacherIds[0]);
                records.add(record);
            }
        }

        return Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                Db.batchSave(DbConfig.T_TEACHER_LINK_STUDENT, records, records.size());
                return true;
            }
        });
    }

    /**
     * 删除管理连接
     */
    public boolean delLink(Integer[] oldroleIds, String[] teacherIds) {
        final List<Record> records = new ArrayList<Record>(oldroleIds.length);
        for (int i = 0; i < oldroleIds.length; i++) {  //如果原来身份是教师 保存该记录 等待删除
            if (oldroleIds[i] == WebConfig.ROLE_TEACHER_ID) {
                Record record = new Record().set("teacherId", teacherIds[i]);
                records.add(record);
            }
        }
        return Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                boolean success = true;
                for (int i = 0; i < records.size(); i++) {
                    List<Record> ids = new DbRecord(DbConfig.T_TEACHER_LINK_STUDENT).whereEqualTo("teacherId", records.get(i).getStr("teacherId")).query();
                    for (int j = 0; j < ids.size(); j++) {
                        if (!Db.deleteById(DbConfig.T_TEACHER_LINK_STUDENT, ids.get(j).getInt("id"))) {
                            success = false;
                        }
                    }
                }
                return success;
            }
        });
    }

    /**
     * 单个解除辅导员与班级的管理连接
     */
    public boolean delLinkClass(Integer[] ids) {
        final List<Record> records = new ArrayList<Record>();
        for (int i = 0; i < ids.length; i++) {
            Record record = new Record();
            record.set("id", ids[i]);
            records.add(record);
        }
        return Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                boolean success = true;
                for (int i = 0; i < records.size(); i++) {
                    List<Record> ids = new DbRecord(DbConfig.T_TEACHER_LINK_STUDENT)
                            .whereEqualTo("id", records.get(i).getStr("id")).query();
                    for (int j = 0; j < ids.size(); j++) {
                        if (!Db.deleteById(DbConfig.T_TEACHER_LINK_STUDENT, ids.get(j).getInt("id"))) {
                            success = false;
                        }
                    }
                }
                return success;
            }
        });
    }
    
    /**
     * 删除课程文件
     */
    public boolean deleteVideoFile(String name,String salt) {
        String path = "";
        String fileName = name + "_" + salt + "_";
        boolean success = true;
        path = PathKit.getWebRootPath() + File.separator + "upload" + File.separator + "video";
        final File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(fileName)) {
                    if (!file.delete()) {
                        success = false;
                    }
                }
            }
        } else {
            success = false;
        }
        return success;
    }

    /**
     * 删除课程信息
     */
    public boolean deleteSubject(String name) {
        return Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                boolean success = true;
                Record subjectRecord = new DbRecord(DbConfig.T_SUBJECT).whereEqualTo("SubjectName", name).queryFirst();
                if (!Db.delete(DbConfig.T_SUBJECT, "id", subjectRecord)) {
                    success = false;
                }
                return success;
            }
        });
    }

    /**
     * 删除章节信息
     */
    public boolean deleteVideo(String name) {
        return Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                boolean success = true;
                Record subjectRecord = new DbRecord(DbConfig.T_VIDEO).whereEqualTo("VideoName", name).queryFirst();
                if (!Db.delete(DbConfig.T_VIDEO, "id", subjectRecord)) {
                    success = false;
                }
                return success;
            }
        });
    }

    /**
     * 删除资源信息
     */
    public boolean deleteSource(Integer id) {
        return Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                boolean success = true;
                Record subjectRecord = new DbRecord(DbConfig.T_SOURCE).whereEqualTo("id", id).queryFirst();
                if (!Db.delete(DbConfig.T_SOURCE, "id", subjectRecord)) {
                    success = false;
                }
                return success;
            }
        });
    }

    /**
     * 删除资源文件
     */
    public boolean deleteSourceFile(Integer id) {
        String path = "";
        boolean success = true;
        path = PathKit.getWebRootPath() + File.separator + "upload" + File.separator + "video";
        String name = new DbRecord(DbConfig.T_SOURCE).whereEqualTo("id", id).queryFirst().getStr("SourceName");
        final File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(name)) {
                    if (!file.delete()) {
                        success = false;
                    }
                }
            }
        } else {
            success = false;
        }
        return success;
    }


    /**
     * 删除作业图片
     */
    public boolean deleteFiles(Integer[] ids, String identity) {
        String path = "";
        boolean success = true;
        if (identity.equals(ROLE_STUDENT)) {  //如果用户身份是学生
            path = PathKit.getWebRootPath() + File.separator + "upload" + File.separator + "student" + File.separator;
            for (int i = 0; i < ids.length; i++) {
                Record record = new DbRecord(DbConfig.V_STUDENT_INFO).whereEqualTo("stuNo", ids[i]).queryFirst(); //找到该学生对应的班级
                File root = new File(path + record.get("className"));//从该学生的班级文件下开始事务
                File[] files = root.listFiles();  //找到对应文件夹下所有的作业图片
                if (files != null && files.length != 0) {
                    for (File file : files) {
                        for (int j = 0; j < ids.length; j++) {
                            if (file.getName().startsWith(ids[j].toString()) && !file.delete()) { //如果该图片属于该学生的作业荣誉，那么删除
                                success = false;
                            }
                        }
                    }
                }
            }
            return success;
        } else { //如果用户身份不是学生
            path = PathKit.getWebRootPath() + File.separator + "upload" + File.separator + "teacher";
            final File[] files = new File(path).listFiles();
            for (File file : files) {
                for (int i = 0; i < ids.length; i++) {
                    if (file.getName().startsWith(ids[i].toString())) {
                        if (!file.delete()) {
                            success = false;
                        }
                    }
                }
            }
            return success;
        }
    }

    /**
     * 删除用户信息
     */
    public boolean deleteUsers(Integer[] ids, String identity) {
        if (identity.equals(ROLE_STUDENT)) {  //如果用户身份是学生
            return Db.tx(new IAtom() {
                public boolean run() throws SQLException {
                    boolean success = true;
                    for (int i = 0; i < ids.length; i++) {//删除该学生在学生表中的信息
                        Record studentRecord = new DbRecord(DbConfig.T_STUDENT).whereEqualTo("stuNo", ids[i]).queryFirst();
                        if (!Db.delete(DbConfig.T_STUDENT, "id", studentRecord)) {
                            success = false;
                        }
                    }
                    for (int i = 0; i < ids.length; i++) {//删除该学生在用户表中的信息
                        Record userRecord = new DbRecord(DbConfig.T_USER).whereEqualTo("username", ids[i]).queryFirst();
                        if (!Db.delete(DbConfig.T_USER, "id", userRecord)) {
                            success = false;
                        }
                    }
                    for (int i = 0; i < ids.length; i++) { //删除该学生在优秀作业展示里的信息
                        Record pictureRecord = new DbRecord(DbConfig.T_PICTURE).whereContains("imagePath", ids[i]).queryFirst();
                        if (pictureRecord != null && !Db.delete(DbConfig.T_PICTURE, "id", pictureRecord)) {
                            success = false;
                        }
                    }
                    return success;
                }
            });
        } else {
            return Db.tx(new IAtom() {
                public boolean run() throws SQLException {
                    boolean success = true;
                    for (int i = 0; i < ids.length; i++) { //删除该教师在教师表中的信息
                        Record teacherRecord = new DbRecord(DbConfig.T_TEACHER).whereEqualTo("teaNo", ids[i]).queryFirst();
                        if (!Db.delete(DbConfig.T_TEACHER, "id", teacherRecord)) {
                            success = false;
                        }
                    }
                    for (int i = 0; i < ids.length; i++) { //删除该教师在用户表中的信息
                        Record userRecord = new DbRecord(DbConfig.T_USER).whereEqualTo("username", ids[i]).queryFirst();
                        if (!Db.delete(DbConfig.T_USER, "id", userRecord)) {
                            success = false;
                        }
                    }
                    return success;
                }
            });
        }

    }

    /**
     * 修改密码
     */
    public boolean updatePassword(Integer userId, String newPass) {
        newPass = Md5Util.Md5(newPass, new User().findById(userId).getUsername());  //加密修改
        return new User().findById(userId).setPassword(newPass).update(); //也可以dao.user.findById 因为已经创建了一个新的对象
    }

}
