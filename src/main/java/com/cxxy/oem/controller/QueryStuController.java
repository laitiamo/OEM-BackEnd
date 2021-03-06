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
                    renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "????????????"));
                } else {
                    renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "????????????"));
                }
                return success;
            }
        });
    }

    //????????????????????????
    @Before(UpdateWorkImageValidator.class)
    public void updateImg() {

        // ????????????????????????(????????????5???)
        List<UploadFile> allFiles = getFiles("file");
        Integer newImageNum = allFiles.size(); //????????????
        System.out.println("???????????????" + newImageNum.toString());
        if (newImageNum > 5) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "???????????????????????????????????????"));
        } else {
            Integer id = getParaToInt("id");
            Record record = new DbRecord(DbConfig.T_USER_WORK).whereEqualTo("id", id).queryFirst();
            String imagePath = record.getStr("imagePath"); //?????????????????? xxxxxx.jpeg*n
            String pathPart = "";//?????????????????????????????????
            Integer oldImageNum = 0;//??????????????? ???n???
            //??????????????????
            if (imagePath.indexOf("*") != -1) {
                String[] parts = imagePath.split("\\*");
                //????????????"*"?????????????????????????????????????????????
                oldImageNum = Integer.parseInt(parts[1]);
                String[] pathParts = parts[0].split("\\.");
                pathPart = pathParts[0];
            } else { //?????????????????????
                oldImageNum = 1;
                String[] pathParts = imagePath.split("\\.");
                pathPart = pathParts[0]; //?????????????????????????????????
            }
            //????????????
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
            //????????????
            // ????????????????????????
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
                //???????????????
                String newImagePath = "";
                if (newImageNum < 2) {
                    newImagePath = pathPart + ".jpeg";
                } else {
                    newImagePath = pathPart + ".jpeg*" + newImageNum.toString();
                }

                boolean success = new UserWork().setId(id).setImagePath(newImagePath).update();
                if (success) {
                    for (int i = 0; i < newImageNum; i++) {
                        // ??????????????????
                        if (!targetFiles.get(i).getParentFile().exists()) {
                            targetFiles.get(i).getParentFile().mkdirs(); // ???????????????????????????
                        }
                        Thumbnails.of(allFiles.get(i).getFile())
                                .size(1280, 720)        //??????????????????
                                .keepAspectRatio(true)  //???????????????????????????
                                .outputQuality(0.5F)    //??????????????????
                                .outputFormat("jpeg")   //??????????????????
                                .toFile(targetFiles.get(i));
                    }
                    renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "????????????"));

                } else {
                    renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "??????????????????????????????????????????"));

                }


            } catch (Exception e) {
                e.printStackTrace();
                renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "????????????"));
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
