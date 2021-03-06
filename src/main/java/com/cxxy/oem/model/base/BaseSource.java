package com.cxxy.oem.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseSource<M extends BaseSource<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public M setSourceName(java.lang.String SourceName) {
		set("SourceName", SourceName);
		return (M)this;
	}
	
	public java.lang.String getSourceName() {
		return getStr("SourceName");
	}

	public M setVideoId(java.lang.Integer VideoId) {
		set("VideoId", VideoId);
		return (M)this;
	}
	
	public java.lang.Integer getVideoId() {
		return getInt("VideoId");
	}

	public M setFilePath(java.lang.String filePath) {
		set("filePath", filePath);
		return (M)this;
	}
	
	public java.lang.String getFilePath() {
		return getStr("filePath");
	}

	public M setUploadTime(java.util.Date UploadTime) {
		set("UploadTime", UploadTime);
		return (M)this;
	}
	
	public java.util.Date getUploadTime() {
		return get("UploadTime");
	}

}
