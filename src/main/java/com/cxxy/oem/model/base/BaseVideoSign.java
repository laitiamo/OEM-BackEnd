package com.cxxy.oem.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVideoSign<M extends BaseVideoSign<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public M setVideoId(java.lang.Integer VideoId) {
		set("VideoId", VideoId);
		return (M)this;
	}
	
	public java.lang.Integer getVideoId() {
		return getInt("VideoId");
	}

	public M setUserId(java.lang.Integer userId) {
		set("userId", userId);
		return (M)this;
	}
	
	public java.lang.Integer getUserId() {
		return getInt("userId");
	}

	public M setSignTime(java.util.Date signTime) {
		set("signTime", signTime);
		return (M)this;
	}
	
	public java.util.Date getSignTime() {
		return get("signTime");
	}

}
