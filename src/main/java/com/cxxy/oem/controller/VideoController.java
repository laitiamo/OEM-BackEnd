package com.cxxy.oem.controller;

import com.alibaba.fastjson.JSON;
import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.model.Source;
import com.cxxy.oem.model.Video;
import com.cxxy.oem.model.VideoSign;
import com.cxxy.oem.service.ExportService;
import com.cxxy.oem.service.UserService;
import com.cxxy.oem.validator.SourceValidator;
import com.cxxy.oem.validator.VideoValidator;
import com.cxxy.oem.vo.AjaxResult;
import com.cxxy.oem.vo.LayUITableResult;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import org.apache.shiro.SecurityUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class VideoController extends Controller {

    public void index() {

    }

    public void listVideo() {
        int page = getParaToInt("page");
        int limit = getParaToInt("limit");
        String keyVideoName = getPara("keyVideoName");
        String keySubjectName = getPara("keySubjectName");
        String order = getPara("order");
        String field = getPara("field");
        String defaultField = "VideoName";
        Page<Record> p = new DbRecord(DbConfig.V_VIDEO_INFO)
                .whereContains("VideoName", keyVideoName)
                .whereContains("SubjectName", keySubjectName)
                .orderBySelect(field, order, defaultField)
                .page(page, limit);
        renderJson(new LayUITableResult<Record>(AjaxResult.CODE_SUCCESS, "", p.getTotalRow(), p.getList()));
    }

    public void listSign() {
        Integer page = getParaToInt("page");
        Integer limit = getParaToInt("limit");
        Integer gradeId = getParaToInt("gradeId");
        Integer majorId = getParaToInt("majorId");
        Integer classId = getParaToInt("classId");
        Integer subjectId = getParaToInt("SubjectId");
        String keyUsername = getPara("keyUsername");
        String keyName = getPara("keyName");
        String keyVideoName = getPara("keyVideoName");
        String order = getPara("order");
        String field = getPara("field");
        String defaultField = "VideoName";
        Page<Record> p = new DbRecord(DbConfig.V_SIGN_INFO)
                .whereEqualTo("gradeId", gradeId)
                .whereEqualTo("majorId", majorId)
                .whereEqualTo("classId", classId)
                .whereEqualTo("SubjectId", subjectId)
                .whereContains("VideoName", keyVideoName)
                .whereContains("username", keyUsername)
                .whereContains("name", keyName)
                .orderBySelect(field, order, defaultField)
                .page(page, limit);
        renderJson(new LayUITableResult<Record>(AjaxResult.CODE_SUCCESS, "", p.getTotalRow(), p.getList()));
    }

    public void sign(){
        Integer VideoId = getParaToInt("VideoId");
        Record result = new DbRecord(DbConfig.T_VIDEO_SIGN).whereEqualTo("VideoId", VideoId).whereEqualTo("userId",UserService.me.getCurrentUser().getInt("id")).queryFirst();

        if(result != null) {    //已经签到
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "您本章节已经签到，请勿重复签到"));
        }else {
            boolean success = new VideoSign().setVideoId(VideoId).setUserId(UserService.me.getCurrentUser().getInt("id")).setSignTime(new Date(System.currentTimeMillis())).save();

            if (success) {
                renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "签到成功"));
            } else {
                renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "签到失败"));
            }
        }
    }

    public void getVideoList() {
        Integer SubjectId = getParaToInt("SubjectId");
        List<Record> p = new DbRecord(DbConfig.V_VIDEO_INFO)
                .whereEqualTo("SubjectId", SubjectId).query();
        renderJson(p);
    }

    public void getSourceList() {
        Integer VideoId = getParaToInt("VideoId");
        List<Record> p = new DbRecord(DbConfig.V_SOURCE_INFO)
                .whereEqualTo("VideoId", VideoId).query();
        renderJson(p);
    }

    public void video() {
        Integer id = getParaToInt("id");
        List<Record> result = new DbRecord(DbConfig.T_VIDEO)
                .whereEqualTo("id", id)
                .query();
        renderJson(result);
    }

    public void exportXLS() {
        Integer gradeId = getParaToInt("gradeId");
        Integer majorId = getParaToInt("majorId");
        Integer classId = getParaToInt("classId");
        Integer subjectId = getParaToInt("SubjectId");
        String keyUserName = getPara("keyUserName");
        String keyName = getPara("keyName");
        String keyVideoName = getPara("keyVideoName");
        List<Record> records = new DbRecord(DbConfig.V_SIGN_INFO)
                .whereEqualTo("gradeId", gradeId)
                .whereEqualTo("majorId", majorId)
                .whereEqualTo("classId", classId)
                .whereEqualTo("SubjectId", subjectId)
                .whereEqualTo("username", keyUserName)
                .whereContains("VideoName", keyVideoName)
                .whereContains("name", keyName)
                .query();
        try {
            File downloadFile = ExportService.me.exportStudentSign(records);
            renderFile(downloadFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
