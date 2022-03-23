package com.cxxy.oem.service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.cxxy.oem.common.Md5Util;
import com.cxxy.oem.common.WebConfig;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class ImportService {

	// 设计模式：单例模式
	public static final ImportService me = new ImportService();

	private ImportService() {

	}


	public boolean importStu(File xls, Integer classId) throws Exception {
		FileInputStream fis = new FileInputStream(xls); //文件输入流 方便读取文件 与c++类似
		HSSFWorkbook workbook = new HSSFWorkbook(fis);  //创建工作簿对象
		HSSFSheet sheet = workbook.getSheetAt(0);   //得到工作表
		
		final List<Record> studentRecords = new ArrayList<Record>(sheet.getLastRowNum());  //传入list容器大小，确定大小 性能损耗
		final List<Record> userRecords = new ArrayList<Record>(sheet.getLastRowNum());
		
		for (int i = 6; i <= sheet.getLastRowNum(); i++) {//模板限定要求 模板读取数据 excel从行号开始读取数据
			HSSFRow row = sheet.getRow(i);//得到第i列的数据
			String studentNo = row.getCell(0).getStringCellValue();
			String studentName = row.getCell(1).getStringCellValue();
			Integer genderId = (int) row.getCell(2).getNumericCellValue();

			if (StrKit.isBlank(studentNo) || StrKit.isBlank(studentName)) {//判断是否空值传入
				/*
				 * 测试中发现经常会读到最后一行空行，这种情况本不该抛出异常。
				 * 为了解决这种问题，目前的策略改为：假如某行出现空studentNo或studentName，直接跳过该行。
				 */
				// workbook.close();
				// throw new RuntimeException("用户导入的表中，出现空学号或学生姓名");
				continue;
			}
			if (!studentNo.matches("[0-9]{8}")) { //正则判断输入格式是否合法
				workbook.close();
				throw new RuntimeException("用户导入的表中，学号格式不正确");
			}
			
			// 如果记录在User表和Student表都不存在的话，才导入该行数据
			List<Record> sRecords = new DbRecord(DbConfig.T_STUDENT)
								.whereEqualTo("stuNo", studentNo)
								.whereEqualTo("stuName", studentName)
								.whereEqualTo("genderId", genderId)
								.whereEqualTo("classId", classId)
								.query();
			List<Record> uRecords = new DbRecord(DbConfig.T_USER)
									.whereEqualTo("username", studentNo)
									.query();
			
			if (sRecords.isEmpty() && uRecords.isEmpty()) {
				Record studentRecord = new Record()
									  .set("stuNo", studentNo)
									  .set("stuName", studentName)
									  .set("genderId", genderId)
									  .set("classId", classId);
				studentRecords.add(studentRecord);
				//对导入数据进行md5盐值加密 盐值取用户名
				Record userRecord = new Record().set("username", studentNo).set("password", Md5Util.Md5(studentNo, studentNo));
				userRecords.add(userRecord);
			}
		}
		workbook.close(); //关闭文件
		
		boolean success1 = Db.tx(new IAtom() {//事务处理，有错一个地方回滚全部数据 原子操作
			public boolean run() throws SQLException {//（匿名类）只能在这边用一次
				Db.batchSave(DbConfig.T_STUDENT, studentRecords, studentRecords.size());//批量插入（底层用for循环做的）
				Db.batchSave(DbConfig.T_USER, userRecords, userRecords.size());
				return true;
			}
		});
		if (!success1) return success1;
		
		boolean success2 =  Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				for (Record r : userRecords) {
					Integer userId = new DbRecord(DbConfig.T_USER)
									.whereEqualTo("username", r.getStr("username"))
									.queryFirst()
									.getInt("id");
					Record userRoleRecord = new Record();
					userRoleRecord.set("userId", userId);
					userRoleRecord.set("roleId", WebConfig.ROLE_STUDENT_ID);//t_user_role表里添加数据
					Db.save(DbConfig.T_USER_ROLE, "id", userRoleRecord);
				}
				return true;
			}
		});
		return success2;
	}

	public boolean importTea(File xls) throws Exception {
		FileInputStream fis = new FileInputStream(xls);
		HSSFWorkbook workbook = new HSSFWorkbook(fis);
		HSSFSheet sheet = workbook.getSheetAt(0);
		
		final List<Record> teacheRecords = new ArrayList<Record>(sheet.getLastRowNum());
		final List<Record> userRecords = new ArrayList<Record>(sheet.getLastRowNum());
		
		for (int i = 6; i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			String teacherNo = row.getCell(0).getStringCellValue();
			String teacherName = row.getCell(1).getStringCellValue();
			Integer genderId = (int) row.getCell(2).getNumericCellValue();

			if (StrKit.isBlank(teacherNo) || StrKit.isBlank(teacherName)) {
				/*
				 * 测试中发现经常会读到最后一行空行，这种情况本不该抛出异常。
				 * 为了解决这种问题，目前的策略改为：假如某行出现空teacheNo或teacherName，直接跳过该行。
				 */
				// workbook.close();
				// throw new RuntimeException("用户导入的表中，出现空教工号或空教工名");
				continue;
			}
			if (!teacherNo.matches("[0-9]{9}")) {
				workbook.close();
				throw new RuntimeException("用户导入的表中，教工号格式不正确");
			}
			
			// 如果记录在User表和Student表都不存在的话，才导入该行数据
			List<Record> tRecords = new DbRecord(DbConfig.T_TEACHER)
									.whereEqualTo("teaNo", teacherNo)
									.whereEqualTo("teaName", teacherName)
									.whereEqualTo("genderId", genderId)
									.query();
			List<Record> uRecords = new DbRecord(DbConfig.T_USER)
									.whereEqualTo("username", teacherNo)
									.query();
			if (tRecords.isEmpty() && uRecords.isEmpty()) {
				Record teacherRecord = new Record()
									  .set("teaNo", teacherNo)
									  .set("teaName", teacherName)
									  .set("genderId", genderId);
				teacheRecords.add(teacherRecord);
			
				Record userRecord = new Record().set("username", teacherNo).set("password", Md5Util.Md5(teacherNo, teacherNo));
				userRecords.add(userRecord);
			}
		}
		workbook.close();

		boolean success1 = Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				Db.batchSave(DbConfig.T_TEACHER, teacheRecords, teacheRecords.size());
				Db.batchSave(DbConfig.T_USER, userRecords, userRecords.size());
				return true;
			}
		});
		if (!success1) return success1;

		boolean success2 =  Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				for (Record r : userRecords) {
					Integer userId = new DbRecord(DbConfig.T_USER)
									.whereEqualTo("username", r.getStr("username"))
									.queryFirst()
									.getInt("id");
					Record urr = new Record().set("userId", userId).set("roleId", WebConfig.ROLE_TEACHER_ID);
					Db.save(DbConfig.T_USER_ROLE, "id", urr);
				}
				return true;
			}
		});
		return success2;
	}
}
