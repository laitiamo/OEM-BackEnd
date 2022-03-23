package com.cxxy.oem.db;

import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

public class DbRecord extends Record {
	// 序列化时为了保持版本的兼容性，即在版本升级时反序列化仍保持对象的唯一性 eclipse自动生成
	private static final long serialVersionUID = -205027140960773323L;

	private StringBuilder sqlBuilder;

	private String tableName;

	public DbRecord(String tableName) {
		this.tableName = tableName;
		sqlBuilder = new StringBuilder(Db.getSql(tableName));
	}

	public String getSql() {
		return sqlBuilder.toString();
	}

	public DbRecord whereEqualTo(String column, Object value) {
		if (value == null || StrKit.isBlank(value.toString()))
			return this;

		if (!sqlBuilder.toString().toLowerCase().contains("where")) {
			sqlBuilder.append(" WHERE ");
		} else {
			sqlBuilder.append(" AND ");
		}
		if (value.getClass() == String.class) {
			sqlBuilder.append(column + " = '" + value + "'");
		} else {
			sqlBuilder.append(column + " = " + value);
		}
		return this;
	}

	public DbRecord whereNotEqualTo(String column, Object value) {
		if (value == null || StrKit.isBlank(value.toString()))
			return this;

		if (!sqlBuilder.toString().toLowerCase().contains("where")) {
			sqlBuilder.append(" WHERE ");
		} else {
			sqlBuilder.append(" AND ");
		}
		if (value.getClass() == String.class) {
			sqlBuilder.append(column + " <> '" + value + "'");
		} else {
			sqlBuilder.append(column + " <> " + value);
		}
		return this;
	}

	public DbRecord whereContains(String column, Object key) {
		if (key == null || StrKit.isBlank(key.toString()))
			return this;

		if (!sqlBuilder.toString().toLowerCase().contains("where")) {
			sqlBuilder.append(" WHERE ");
		} else {
			sqlBuilder.append(" AND ");
		}
		sqlBuilder.append(column + " LIKE '%" + key + "%'");
		return this;
	}

	public DbRecord include(String foreignKey) {
		Record record = Db.use("system").findFirst(Db.getSql("KEY_COLUMN_USAGE"), tableName, foreignKey);
		String targetSchema = record.getStr("targetSchema");
		String targetTable = record.getStr("targetTable");
		String targetColumn = record.getStr("targetColumn");
		sqlBuilder.insert(sqlBuilder.toString().toLowerCase().indexOf("from") + "from".length(),
				" " + targetSchema + "." + targetTable + ",");
		if (!sqlBuilder.toString().toLowerCase().contains("where")) {
			sqlBuilder.append(" WHERE ");
		} else {
			sqlBuilder.append(" AND ");
		}
		sqlBuilder.append(tableName + "." + foreignKey + " = " + targetTable + "." + targetColumn);
		return this;
	}

	public DbRecord orderByASC(String column) {
		sqlBuilder.append(" ORDER BY " + column + " ASC");
		return this;
	}

	public DbRecord orderByDESC(String column) {
		sqlBuilder.append(" ORDER BY " + column + " DESC");
		return this;
	}
	
	public DbRecord orderBySelect(String column,String order,String defaultColumn) {
		if (column == null || StrKit.isBlank(column) || column.equals("null")) {
			if (order == null || StrKit.isBlank(order) || order.equals("null")) {
				sqlBuilder.append(" ORDER BY "+defaultColumn+" DESC");
			}else {
				sqlBuilder.append(" ORDER BY " +defaultColumn+ " "+order);
			}
		}else {
			if (order == null || StrKit.isBlank(order) || order.equals("null")) {
				sqlBuilder.append(" ORDER BY " + column + " DESC");
			}else {
				sqlBuilder.append(" ORDER BY " + column + " "+order);
			}
		}
		return this;
	} 

	public Page<Record> page(Integer pageNumber, Integer pageSize) {
		return Db.paginate(pageNumber, pageSize, new SqlPara().setSql(sqlBuilder.toString()));
	}

	public List<Record> query() {
		return Db.find(sqlBuilder.toString());
	}

	public Record queryFirst() {
		return Db.findFirst(sqlBuilder.toString());
	}

}
