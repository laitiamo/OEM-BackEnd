package com.cxxy.oem.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import org.apache.shiro.SecurityUtils;
import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.model.UserWork;
import com.cxxy.oem.service.UserService;
import com.cxxy.oem.validator.UploadValidator;
import com.cxxy.oem.vo.AjaxResult;
import com.cxxy.oem.vo.LayUITableResult;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

import net.coobird.thumbnailator.Thumbnails;

public class UploadController extends Controller {

    public void index() {
    }

    @Before(UploadValidator.class)
    public void upload() {
        Record user = UserService.me.getCurrentUserInfo();

        // 获取上传原始文件(暂定最多20个)
        List<UploadFile> allFiles = getFiles("file");

        if (allFiles.size() > 20) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "图片数量超出上限，上传失败"));
        } else {
            // 重命名元素: 用户名
            String username = (String) SecurityUtils.getSubject().getPrincipal();
            // 重命名元素: 上传时间
            Date now = new Date(System.currentTimeMillis());
            String uploadDateTime = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(now);

            // 重命名元素: 序号

            // 目标文件路径列表
            ArrayList<String> webPaths = new ArrayList<String>();
            // 源路径
            String originPath = "";
            // 每个文件名
            String webPathString;
            username += user.getStr("name");
            // 重命名元素：班级ID
            String className = user.getStr("className");
            originPath = File.separator + "upload" +
                    File.separator + "student" +
                    File.separator + className +
                    File.separator + className + File.separator + username + File.separator + getParaToInt("WorkId") + "_" + getPara("WorkName") + File.separator + getPara("WorkPlace") + "_" + uploadDateTime;
            if (allFiles.size() == 1) {
                webPaths.add(originPath);
            } else {
                for (int i = 0; i < allFiles.size(); i++) {
                    webPathString = originPath + "_" + i;
                    webPaths.add(webPathString);
                }
            }
            //2020-10-20之前的单图片模式
            // File targetFile = new File(PathKit.getWebRootPath() + webPath);

            ArrayList<File> targetFiles = new ArrayList<File>();
            for (int i = 0; i < allFiles.size(); i++) {
                File oneFile = new File(PathKit.getWebRootPath() + webPaths.get(i));
                targetFiles.add(oneFile);
            }


            try {
                // 保存文件信息至数据库
                UserWork userWork = new UserWork();
                userWork.setWorkId(getParaToInt("WorkId"));
                userWork.setWorkPlace(getPara("WorkPlace"));
                userWork.setWorkTime(new SimpleDateFormat("yyyy-MM-dd").parse(getPara("WorkTime")));// parse方法可解析一个日期时间字符串

                // 数据库最终保存的路径，如果多图则在结尾加 "*"符号 跟上图片数量
                String finalPath = "";
                if (allFiles.size() == 1) {
                    finalPath = originPath + ".jpeg";
                } else {
                    finalPath = originPath + ".jpeg*" + allFiles.size();
                }
                userWork.setImagePath(finalPath);
                userWork.setUserId(UserService.me.getCurrentUser().getInt("id"));
                userWork.setReviewId(WebConfig.REVIEW_UNREAD);//未审核
                userWork.setScoreType(WebConfig.SCORE_NOT_REVIEW);//未评分
                if (userWork.save()) {
                    for (int i = 0; i < allFiles.size(); i++) {
                        if (!targetFiles.get(i).getParentFile().exists()) {
                            targetFiles.get(i).getParentFile().mkdirs(); // 递归创建父类文件夹
                        }
                        // 压缩图片
                        Thumbnails.of(allFiles.get(i).getFile())
                                .size(1280, 720)        //转换图片大小
                                .keepAspectRatio(true)  //不按横纵比压缩图片
                                .outputFormat("jpeg")   //转化图片形式
                                .toFile(targetFiles.get(i));
                    }
                }
                if (SecurityUtils.getSubject().hasRole(WebConfig.ROLE_STUDENT)) {
                    renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "上传成功，等待审核..."));
                } else {
                    renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "上传成功"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "上传失败"));
            } finally {
                for (int i = 0; i < allFiles.size(); i++) {
                    allFiles.get(i).getFile().delete();
                }
            }
        }
    }

    public void listWork() {
        int page = getParaToInt("page");
        int limit = getParaToInt("limit");
        String keyWorkName = getPara("keyWorkName");
        String keySubjectName = getPara("keySubjectName");
        String order = getPara("order");
        String field = getPara("field");
        String defaultField = "WorkName";
        Page<Record> p = new DbRecord(DbConfig.V_WORK_INFO)
                    .whereContains("WorkName", keyWorkName)
                    .whereContains("SubjectName", keySubjectName)
                    .orderBySelect(field, order, defaultField)
                    .page(page, limit);
        renderJson(new LayUITableResult<Record>(AjaxResult.CODE_SUCCESS, "", p.getTotalRow(), p.getList()));
    }

}
