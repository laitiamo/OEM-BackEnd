package com.cxxy.oem.controller;

import com.alibaba.fastjson.JSON;
import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.cxxy.oem.model.Source;
import com.cxxy.oem.model.Video;
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
import java.util.*;

public class SubjectController extends Controller {

    public void index() {
        Map<String, Object> attrMap = new HashMap<String, Object>();
        setAttr("Subject", new DbRecord(DbConfig.T_SUBJECT).query());
        //向前端发送全部attribute
        Enumeration<String> names = getAttrNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            attrMap.put(name, getAttr(name));
        }
        renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, JSON.toJSONString(attrMap)));
    }

    @Before(VideoValidator.class)
    public void add() {
        Record user = UserService.me.getCurrentUserInfo();

        // 获取上传视频原始文件(暂定最多1个)
        List<UploadFile> allVideos = getFiles("video");

        if (allVideos.size() > 1) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "文件数量超出上限，上传失败"));
        } else {
            // 重命名元素: 用户名
            String username = (String) SecurityUtils.getSubject().getPrincipal();
            // 重命名元素: 上传时间
            int salt = new Random().nextInt(1000000);

            // 重命名元素: 序号

            // 目标文件路径列表
            ArrayList<String> webPaths = new ArrayList<String>();
            // 源路径
            String originPath = "";
            // 每个文件名
            String webPathString;
            username += user.getStr("name");

            originPath = File.separator + "upload" +
                    File.separator + "video" +
                    File.separator + getPara("VideoName") + "_" + salt + "_" + ".mp4";
            webPaths.add(originPath);
            //2020-10-20之前的单图片模式
            // File targetFile = new File(PathKit.getWebRootPath() + webPath);

            ArrayList<File> targetFiles = new ArrayList<File>();
            for (int i = 0; i < allVideos.size(); i++) {
                File oneFile = new File(PathKit.getWebRootPath() + webPaths.get(i));
                targetFiles.add(oneFile);
            }

            try {
                // 保存文件信息至数据库
                Video video = new Video();
                video.setSubjectId(getParaToInt("SubjectId"));
                video.setVideoName(getPara("VideoName"));
                video.setSalt(String.valueOf(salt));
                video.setUploadTime(new Date(System.currentTimeMillis()));

                // 数据库最终保存的路径，如果多图则在结尾加 "*"符号 跟上图片数量
                String finalPath = originPath;
                if (video.save()) {
                    for (int i = 0; i < allVideos.size(); i++) {
                        if (!targetFiles.get(i).getParentFile().exists()) {
                            targetFiles.get(i).getParentFile().mkdirs(); // 递归创建父类文件夹
                        }
                        allVideos.get(i).getFile().renameTo(targetFiles.get(i));
                    }
                    renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "上传成功"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "上传失败"));
            } finally {
                for (int i = 0; i < allVideos.size(); i++) {
                    allVideos.get(i).getFile().delete();
                }
            }
        }
    }

    @Before(SourceValidator.class)
    public void addSource() {
        Record user = UserService.me.getCurrentUserInfo();

        // 获取上传视频原始文件(暂定最多5个)
        List<UploadFile> allSources = getFiles("source");

        if (allSources.size() > 5) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "文件数量超出上限，上传失败"));
        } else {
            // 重命名元素: 用户名
            String username = (String) SecurityUtils.getSubject().getPrincipal();
            // 重命名元素: 上传时间

            // 重命名元素: 序号

            // 目标文件路径列表
            ArrayList<String> webPaths = new ArrayList<String>();
            // 源路径
            String originPath = "";
            // 每个文件名
            String webPathString;
            username += user.getStr("name");

            originPath = File.separator + "upload" +
                    File.separator + "video" +
                    File.separator + username + getPara("SourceName");
            if (allSources.size() == 1) {
                webPathString = originPath + ".pdf";
                webPaths.add(webPathString);
            } else {
                for (int i = 0; i < allSources.size(); i++) {
                    webPathString = originPath + "_" + i + ".pdf";
                    webPaths.add(webPathString);
                }
            }
            //2020-10-20之前的单图片模式
            // File targetFile = new File(PathKit.getWebRootPath() + webPath);

            ArrayList<File> targetFiles = new ArrayList<File>();
            for (int i = 0; i < allSources.size(); i++) {
                File oneFile = new File(PathKit.getWebRootPath() + webPaths.get(i));
                targetFiles.add(oneFile);
            }

            try {
                // 保存文件信息至数据库
                Source source = new Source();
                source.setVideoId(getParaToInt("VideoId"));
                source.setSourceName(getPara("SourceName"));
                source.setUploadTime(new Date(System.currentTimeMillis()));

                // 数据库最终保存的路径，如果多图则在结尾加 "*"符号 跟上图片数量
                String finalPath = "";
                if (allSources.size() == 1) {
                    finalPath = originPath + ".pdf";
                } else {
                    finalPath = originPath + ".pdf" + "*" + allSources.size();
                }
                source.setFilePath(finalPath);
                if (source.save()) {
                    for (int i = 0; i < allSources.size(); i++) {
                        if (!targetFiles.get(i).getParentFile().exists()) {
                            targetFiles.get(i).getParentFile().mkdirs(); // 递归创建父类文件夹
                        }
                        allSources.get(i).getFile().renameTo(targetFiles.get(i));
                    }
                    renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "上传成功"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "上传失败"));
            } finally {
                for (int i = 0; i < allSources.size(); i++) {
                    allSources.get(i).getFile().delete();
                }
            }
        }
    }

    public void listSubject() {
        int page = getParaToInt("page");
        int limit = getParaToInt("limit");
        String key = getPara("key");
        String order = getPara("order");
        String field = getPara("field");
        String defaultField = "SubjectName";
        Page<Record> p = null;
        p = new DbRecord(DbConfig.T_SUBJECT)
                .whereContains("SubjectName", key)
                .orderBySelect(field, order, defaultField)
                .page(page, limit);

        renderJson(new LayUITableResult<Record>(AjaxResult.CODE_SUCCESS, "", p.getTotalRow(), p.getList()));
    }

    public void deleteSubject() {
        final String subjectName = getPara("SubjectName");
        boolean recordsDeletionSuccess = true;

        recordsDeletionSuccess = UserService.me.deleteSubject(subjectName);

        if (recordsDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "课程信息删除成功"));
        } else if (!recordsDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "课程信息删除失败"));
        }
    }

    public void deleteVideo() {
        final String VideoName = getPara("VideoName");
        final String salt = getPara("salt");
        boolean recordsDeletionSuccess = true;
        boolean filesDeletionSuccess = true;

        filesDeletionSuccess = UserService.me.deleteVideoFile(VideoName, salt);
        recordsDeletionSuccess = UserService.me.deleteVideo(VideoName);

        if (recordsDeletionSuccess && filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "视频信息删除成功，已上传文件清理成功"));
        } else if (!recordsDeletionSuccess && !filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "视频信息删除失败，已上传文件清理失败"));
        } else if (recordsDeletionSuccess && !filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "视频信息删除成功，已上传文件清理失败"));
        } else if (!recordsDeletionSuccess && filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "视频信息删除失败，已上传文件清理成功"));
        }
    }

    public void deleteFile() {
        final Integer[] studentNos = getParaValuesToInt("studentNos[]");
        boolean filesDeletionSuccess = true;

        filesDeletionSuccess = UserService.me.deleteFiles(studentNos, WebConfig.ROLE_STUDENT);

        if (filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "已上传文件清理成功"));
        } else if (!filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "已上传文件清理失败"));
        }
    }

    public void deleteSource() {
        final Integer SourceId = getParaToInt("id");
        boolean recordsDeletionSuccess = true;
        boolean filesDeletionSuccess = true;

        filesDeletionSuccess = UserService.me.deleteSourceFile(SourceId);
        recordsDeletionSuccess = UserService.me.deleteSource(SourceId);

        if (recordsDeletionSuccess && filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_SUCCESS, "资源信息删除成功，已上传文件清理成功"));
        } else if (!recordsDeletionSuccess && !filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "资源信息删除失败，已上传文件清理失败"));
        } else if (recordsDeletionSuccess && !filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "资源信息删除成功，已上传文件清理失败"));
        } else if (!recordsDeletionSuccess && filesDeletionSuccess) {
            renderJson(new AjaxResult(AjaxResult.CODE_ERROR, "资源信息删除失败，已上传文件清理成功"));
        }
    }

    public void exportPDF() {
        Integer id = getParaToInt("id");
        List<Record> records = new DbRecord(DbConfig.V_SOURCE_INFO)
                .whereEqualTo("id", id)
                .query();
        try {
            File downloadFile = ExportService.me.exportTeacherSubjectPDF(records);
            if (downloadFile != null) {
                renderFile(downloadFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
