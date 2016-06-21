package classbuilder.demo.handler.orm;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import classbuilder.BuilderException;
import classbuilder.IMethod;
import classbuilder.Variable;
import classbuilder.handler.AbstractClassHandler;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;

// implements orm helper methods to each bean
public class OrmHandler extends AbstractClassHandler {
	
	public interface OrmSupport {
		public String __insertSql();
		public String __updateSql();
		public String __selectSql();
		public String __getSql();
		public String __deleteSql();
		
		public void toStatement(PreparedStatement statement);
		public void toStatementKey(PreparedStatement statement);
		public void fromResultSet(ResultSet resultSet);
	}
	
	// constant type map: java type -> Statement/ResultSet type name
	private static Map<Class<?>, String> typeMap;
	
	static {
		typeMap = new HashMap<Class<?>, String>();
		typeMap.put(byte.class, "Byte");
		typeMap.put(Byte.class, "Byte");
		typeMap.put(short.class, "Short");
		typeMap.put(Short.class, "Short");
		typeMap.put(int.class, "Int");
		typeMap.put(Integer.class, "Int");
		typeMap.put(long.class, "Long");
		typeMap.put(Long.class, "Long");
		typeMap.put(float.class, "Float");
		typeMap.put(Float.class, "Float");
		typeMap.put(double.class, "Double");
		typeMap.put(Double.class, "Double");
		typeMap.put(String.class, "String");
		typeMap.put(Date.class, "Date");
		typeMap.put(byte[].class, "Bytes");
		typeMap.put(InputStream.class, "BinaryStream");
	}
	
	@Override
	public void handle(HandlerContext context) throws BuilderException, HandlerException {
		String fieldList = null;
		String paramList = null;
		String updateList = null;
		String whereList = null;
		
		getInterfaces().add(OrmSupport.class);
		
		ArrayList<Field> fields = new ArrayList<Field>();
		ArrayList<Field> pkFields = new ArrayList<Field>();
		Class<?> cls = getSuperclass();
		while (cls != null && cls != Object.class) {
			Field[] list = cls.getDeclaredFields();
			for (Field field : list) {
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					if (column.pk()) {
						pkFields.add(field);
					} else {
						fields.add(field);
					}
				}
			}
			cls = cls.getSuperclass();
		}
		fields.addAll(pkFields);
		
		if (pkFields.size() == 0) {
			throw new HandlerException("no primary key found");
		}
		
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			if (column != null) {
					fieldList = concat(fieldList, column.name());
					paramList = concat(paramList, "?");
					if (!column.pk()) updateList = concat(updateList, column.name() + " = ?");
					if (column.pk()) whereList = concat(whereList, column.name() + " = ?");
			}
		}
		
		Table table = getSuperclass().getAnnotation(Table.class);
		String tableName = table.value();
		
		String insert = "INSERT INTO " + tableName + " (" + fieldList + ") VALUES (" + paramList + ")";
		String update = "UPDATE " + tableName + " SET " + updateList + " WHERE " + whereList;
		String select = "SELECT " + fieldList + " FROM " + tableName;
		String get    = "SELECT " + fieldList + " FROM " + tableName + " WHERE " + whereList;
		String delete = "DELETE FROM " + tableName + " WHERE " + whereList;
		
		IMethod getInsertSql = addMethod(PUBLIC, String.class, "__insertSql");
			getInsertSql.Return(insert);
		getInsertSql.End();
		
		IMethod getUpdateSql = addMethod(PUBLIC, String.class, "__updateSql");
			getUpdateSql.Return(update);
		getUpdateSql.End();
		
		IMethod getSelectSql = addMethod(PUBLIC, String.class, "__selectSql");
			getSelectSql.Return(select);
		getSelectSql.End();
		
		IMethod getGetSql = addMethod(PUBLIC, String.class, "__getSql");
			getGetSql.Return(get);
		getGetSql.End();
		
		IMethod getDeleteSql = addMethod(PUBLIC, String.class, "__deleteSql");
			getDeleteSql.Return(delete);
		getDeleteSql.End();
		
		IMethod toStatement = addMethod(PUBLIC, "toStatement", PreparedStatement.class);
			Variable s = toStatement.getParameter(0);
			int index = 1;
			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					String typeName = typeMap.get(field.getType());
					if (typeName != null) {
						s.invoke("set" + typeName, index++, toStatement.get(field));
					}
				}
			}
		toStatement.End();
		
		IMethod toStatementKey = addMethod(PUBLIC, "toStatementKey", PreparedStatement.class);
			s = toStatementKey.getParameter(0);
			index = 1;
			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);
				if (column != null && column.pk()) {
					String typeName = typeMap.get(field.getType());
					if (typeName != null) {
						s.invoke("set" + typeName, index++, toStatementKey.get(field));
					}
				}
			}
		toStatementKey.End();
		
		IMethod fromResultSet = addMethod(PUBLIC, "fromResultSet", ResultSet.class);
			Variable rs = fromResultSet.getParameter(0);
			index = 1;
			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					String typeName = typeMap.get(field.getType());
					if (typeName != null) {
						fromResultSet.get(field).set(rs.invoke("get" + typeName, index++));
					}
				}
			}
		fromResultSet.End();
	}
	
	private String concat(String base, String sufix) {
		if (base == null) return sufix;
		return base + ", " + sufix;
	}
}
