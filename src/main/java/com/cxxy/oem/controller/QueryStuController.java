package com.cxxy.oem.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.model.UserWork;
import com.cxxy.oem.service.ExportService;
import com.cxxy.oem.service.UserService;
import com.cxxy.oem.validator.StudentWorkDeleteValidator;
import com.cxxy.oem.validator.UpdateWorkImageValidator;
import com.cxxy.oem.vo.AjaxResult;
import com.cxxy.oem.vo.LayUITableResult;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.shiro.SecurityUtils;

public class QueryStuController extends Controller {

    public void index() {
        Map<String, Object> attrMap = new HashMap<String, Object>();
        attrMap.put("grade", new DbRecord(DbConfig.T_GRADE).query());
        attrMap.put("major", new DbRecord(DbConfig.T_MAJOR).query());
        attrMap.put("teacher", new DbRecord(DbConfig.V_TEACHER_INFO).query());
        attrMap.put("score",new DbRecord(DbConfig.T_SCORE).query());
        renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, JSON.toJSONString(attrMap)));
    }

    public void list() {
        Integer page = getParaToInt("page");
        Integer limit = getParaToInt("limit");
        Integer gradeId = getParaToInt("gradeId");
        Integer majorId = getParaToInt("majorId");
        Integer classId = getParaToInt("classId");
        Integer subjectId = getParaToInt("SubjectId");
        Integer scoreType = getParaToInt("scoreType");
        String keyUsername = getPara("keyUsername");
        String keyName = getPara("keyName");
        String keyWorkName = getPara("keyWorkName");
        String keyWorkPlace = getPara("keyWorkPlace");
        String order = getPara("order");
        String field = getPara("field");
        String defaultField = "id";
        Page<Record> p = null;
        Record info = UserService.me.getCurrentUserInfo();
        if (info.getStr("roleNameEn").equals(WebConfig.ROLE_ADMIN)) {
            p = new DbRecord(DbConfig.V_STUDENT_WORK)
                    .whereEqualTo("gradeId", gradeId)
                    .whereEqualTo("majorId", majorId)
                    .whereEqualTo("classId", classId)
                    .whereEqualTo("SubjectId", subjectId)
                    .whereEqualTo("scoreType", scoreType)
                    .whereEqualTo("username", keyUsername)
                    .whereContains("WorkName", keyWorkName)
                    .whereContains("WorkPlace", keyWorkPlace)
                    .whereContains("name", keyName)
                    .orderBySelect(field, order, defaultField)
                    .page(page, limit);
        } else {
            p = new DbRecord(DbConfig.V_STUDENT_WORK_TEACHER)
                    .whereEqualTo("teacherId", info.getStr("teaNo"))
                    .whereEqualTo("gradeId", gradeId)
                    .whereEqualTo("majorId", majorId)
                    .whereEqualTo("classId", classId)
                    .whereEqualTo("SubjectId", subjectId)
                    .whereEqualTo("scoreType", scoreType)
                    .whereEqualTo("username", keyUsername)
                    .whereContains("WorkName", keyWorkName)
                    .whereContains("WorkPlace", keyWorkPlace)
                    .whereContains("name", keyName)
                    .orderBySelect(field, order, defaultField)
                    .page(page, limit);
        }
        renderJson(new LayUITableResult<Record>(AjaxResult.CODE_SUCCESS, "", p.getTotalRow(), p.getList()));
    }

    @Before(StudentWorkDeleteValidator.class)
    public void del() {
        final Integer id = getParaToInt("id");

        Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                boolean success = true;
                Record r = new DbRecord(DbConfig.T_USER_WORK).whereEqualTo("id", id).queryFirst();
                if (!Db.delete(DbConfig.T_USER_WORK, "id", r)) {
                    success = false;
                }
                if (success) {
                    renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "删除成功"));
                } else {
                    renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "删除失败"));
                }
                return success;
            }
        });
    }

    //更新学生作业图片
    @Before(UpdateWorkImageValidator.class)
    public void updateImg() {

        // 获取上传原始文件(暂定最多5个)
        List<UploadFile> allFiles = getFiles("file");
        Integer newImageNum = allFiles.size(); //新图片数
        System.out.println("新图片数：" + newImageNum.toString());
        if (newImageNum > 5) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "图片数量超出上限，更新失败"));
        } else {
            Integer id = getParaToInt("id");
            Record record = new DbRecord(DbConfig.T_USER_WORK).whereEqualTo("id", id).queryFirst();
            String imagePath = record.getStr("imagePath"); //旧图片全路径 xxxxxx.jpeg*n
            String pathPart = "";//不带序号后缀的图片路径
            Integer oldImageNum = 0;//旧图片数量 （n）
            //如果存在多图
            if (imagePath.indexOf("*") != -1) {
                String[] parts = imagePath.split("\\*");
                //如果存在"*"则分割，取出图片数量和图片路径
                oldImageNum = Integer.parseInt(parts[1]);
                String[] pathParts = parts[0].split("\\.");
                pathPart = pathParts[0];
            } else { //如果旧图为单张
                oldImageNum = 1;
                String[] pathParts = imagePath.split("\\.");
                pathPart = pathParts[0]; //不带序号后缀的图片路径
            }
            //清理旧图
            if (oldImageNum < 2) {
                File deleteFile = new File(PathKit.getWebRootPath() + pathPart + ".jpeg");
                if (deleteFile.exists()) {
                    deleteFile.delete();
                }
            } else {
                for (int i = 0; i < oldImageNum; i++) {
                    File deleteFile = new File(PathKit.getWebRootPath() + pathPart + "_" + i + ".jpeg");
                    if (deleteFile.exists()) {
                        deleteFile.delete();
                    }
                }
            }
            //添加新图
            // 目标文件路径列表
            ArrayList<String> webPaths = new ArrayList<String>();
            String webPathString = "";
            if (newImageNum < 2) {
                webPaths.add(pathPart);
            } else {
                for (int i = 0; i < newImageNum; i++) {
                    webPathString = pathPart + "_" + i;
                    webPaths.add(webPathString);
                }
            }
            ArrayList<File> targetFiles = new ArrayList<File>();
            for (int i = 0; i < newImageNum; i++) {
                File oneFile = new File(PathKit.getWebRootPath() + webPaths.get(i));
                targetFiles.add(oneFile);
            }
            try {
                //更新数据库
                String newImagePath = "";
                if (newImageNum < 2) {
                    newImagePath = pathPart + ".jpeg";
                } else {
                    newImagePath = pathPart + ".jpeg*" + newImageNum.toString();
                }

                boolean success = new UserWork().setId(id).setImagePath(newImagePath).update();
                if (success) {
                    for (int i = 0; i < newImageNum; i++) {
                        // 保存目标文件
                        if (!targetFiles.get(i).getParentFile().exists()) {
                            targetFiles.get(i).getParentFile().mkdirs(); // 递归创建父类文件夹
                        }
                        Thumbnails.of(allFiles.get(i).getFile())
                                .size(1280, 720)        //转换图片大小
                                .keepAspectRatio(true)  //不按横纵比压缩图片
                                .outputQuality(0.5F)    //压缩图片质量
                                .outputFormat("jpeg")   //转化图片形式
                                .toFile(targetFiles.get(i));
                    }
                    renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "更新成功"));

                } else {
                    renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "数据库写入发生错误，更新失败"));

                }


            } catch (Exception e) {
                e.printStackTrace();
                renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "更新失败"));
            } finally {
                for (int i = 0; i < newImageNum; i++) {
                    allFiles.get(i).getFile().delete();
                }
            }
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

    public void exportXLS() {
        Integer gradeId = getParaToInt("gradeId");
        Integer majorId = getParaToInt("majorId");
        Integer classId = getParaToInt("classId");
        Integer subjectId = getParaToInt("SubjectId");
        String keyUsername = getPara("keyUsername");
        String keyName = getPara("keyName");
        String keyWorkName = getPara("keyWorkName");
        String keyWorkPlace = getPara("keyWorkPlace");
        List<Record> records = new DbRecord(DbConfig.V_STUDENT_WORK)
                .whereEqualTo("gradeId", gradeId)
                .whereEqualTo("majorId", majorId)
                .whereEqualTo("classId", classId)
                .whereEqualTo("SubjectId", subjectId)
                .whereEqualTo("reviewId", WebConfig.REVIEW_PASS)
                .whereEqualTo("username", keyUsername)
                .whereContains("WorkName", keyWorkName)
                .whereContains("WorkPlace", keyWorkPlace)
                .whereContains("name", keyName)
                .query();
        try {
            File downloadFile = ExportService.me.exportStudentWork(records);
            renderFile(downloadFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportZIP() {
        Integer gradeId = getParaToInt("gradeId");
        Integer majorId = getParaToInt("majorId");
        Integer classId = getParaToInt("classId");
        Integer subjectId = getParaToInt("SubjectId");
        String keyUsername = getPara("keyUsername");
        String keyName = getPara("keyName");
        String keyWorkName = getPara("keyWorkName");
        String keyWorkPlace = getPara("keyWorkPlace");
        List<Record> records = new DbRecord(DbConfig.V_STUDENT_WORK)
                .whereEqualTo("gradeId", gradeId)
                .whereEqualTo("majorId", majorId)
                .whereEqualTo("classId", classId)
                .whereEqualTo("SubjectId", subjectId)
                .whereEqualTo("username", keyUsername)
                .whereContains("WorkName", keyWorkName)
                .whereContains("WorkPlace", keyWorkPlace)
                .whereContains("name", keyName)
                .query();
        try {
            File downloadFile = ExportService.me.exportStudentZIP(records);
            if (downloadFile != null) {
                renderFile(downloadFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
