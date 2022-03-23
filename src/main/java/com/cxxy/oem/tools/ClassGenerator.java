package com.cxxy.oem.tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.model._MappingKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * Class表是根据专业、年级、班号生成的，此类是生成Class表的工具。
 * 
 * 
 * Major表、Grade表和Class表都需要管理员手工维护。
 * 
 * 目前（2019/04/09）Major表的有以下几个专业（日后可能需要增加专业）：
 * 电子信息工程、电子科学与技术、自动化、计算机科学与技术、软件工程
 * 
 * 目前（2019/04/09）Grade表预留的范围是[2014, 2030]（基本没有变动的可能性）
 * 
 * 目前预留的班号范围是[1, 6]
 *
 */
public final class ClassGenerator {
	public static void generate() {
		PropKit.use("config.properties");
		DruidPlugin dp = new DruidPlugin(
				PropKit.get("jdbcUrl").trim(), 
				PropKit.get("user").trim(),
				PropKit.get("password").trim());
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
		arp.setShowSql(true);
		arp.setDialect(new MysqlDialect());
		_MappingKit.mapping(arp);
		dp.start();
		arp.start();
		
		
		List<Record> majorRecords = Db.find("select * from t_major");
		List<Record> gradeRecords = Db.find("select * from t_grade");
		int[] classNos = {1, 2, 3, 4, 5, 6, 7, 8};
		
		final List<Record> classRecords = new ArrayList<>();
		int id = 1;
		for (Record gradeRecord : gradeRecords) {
			for (Record majorRecord : majorRecords) {
				for (int classNo : classNos) {
					int gradeId = gradeRecord.getInt("id");
					int majorId = majorRecord.getInt("id");
					StringBuilder builder = new StringBuilder()
											.append(Integer.toString(gradeId - 2000))
											.append(majorRecord.getStr("majorName"))
											.append(Integer.toString(classNo))
											.append("班");
					String className = builder.toString();
					//System.out.println(className);
					Record classRecord = new Record()
										.set("id", id)
										.set("className", className)
										.set("classNo", classNo)
										.set("majorId", majorId)
										.set("gradeId", gradeId);
					classRecords.add(classRecord);
					id++;
				}
			}
		}
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				Db.batchSave(DbConfig.T_CLASS, classRecords, classRecords.size());
				return true;
			}
		});
	}
	public static void main(String[] args) {
		ClassGenerator.generate();
	}
}
