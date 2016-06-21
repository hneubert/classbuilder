package classbuilder.demo.handler.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import classbuilder.BuilderException;
import classbuilder.demo.handler.orm.OrmHandler.OrmSupport;
import classbuilder.handler.HandlerException;
import classbuilder.handler.ObjectFactory;

public class OrmMapper {
	private ObjectFactory factory;
	private Connection connection;
	
	public OrmMapper(Connection connection, ObjectFactory factory) {
		this.connection = connection;
		this.factory = factory;
	}
	
	public <T> Collection<T> select(Class<T> type, String where) throws SQLException {
		String sql = null;
		OrmSupport ormSupport;
		try {
			Object object = (OrmSupport)factory.create(type);
			ormSupport = cast(object);
			sql = ormSupport.__selectSql();
		} catch (BuilderException e) {
			e.printStackTrace();
		} catch (HandlerException e) {
			e.printStackTrace();
		}
		if (where != null) sql = sql + " where " + where;
		PreparedStatement ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		ArrayList<T> list = new ArrayList<T>();
		while (rs.next()) {
			try {
				T bean = factory.create(type);
				((OrmSupport)bean).fromResultSet(rs);
				list.add(bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		rs.close();
		ps.close();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T select(T object) throws SQLException {
		OrmSupport bean = cast(object);
		PreparedStatement ps = connection.prepareStatement(bean.__getSql());
		bean.toStatementKey(ps);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			bean.fromResultSet(rs);
		} else {
			throw new SQLException("no data found");
		}
		rs.close();
		ps.close();
		return (T)bean;
	}
	
	public void insert(Object object) throws SQLException {
		OrmSupport bean = cast(object);
		PreparedStatement ps = connection.prepareStatement(bean.__insertSql());
		bean.toStatement(ps);
		ps.execute();
		ps.close();
	}
	
	public void insert(Collection<Object> beans) throws SQLException {
		PreparedStatement ps = null;
		boolean first = true;
		for (Object object : beans) {
			OrmSupport bean = cast(object);
			if (first) {
				ps = connection.prepareStatement(bean.__insertSql());
				first = false;
			}
			bean.toStatement(ps);
			ps.executeQuery();
		}
		if (ps != null) {
			ps.executeBatch();
			ps.close();
		}
	}
	
	public void update(Object object) throws SQLException {
		OrmSupport bean = cast(object);
		PreparedStatement ps = connection.prepareStatement(bean.__updateSql());
		bean.toStatement(ps);
		ps.execute();
		ps.close();
	}
	
	public void update(Collection<Object> beans) throws SQLException {
		PreparedStatement ps = null;
		boolean first = true;
		for (Object object : beans) {
			OrmSupport bean = cast(object);
			if (first) {
				ps = connection.prepareStatement(bean.__insertSql());
			}
			bean.toStatement(ps);
			ps.addBatch();
		}
		if (ps != null) {
			ps.executeBatch();
			ps.close();
		}
	}
	
	public void delete(Object object) throws SQLException {
		OrmSupport bean = cast(object);
		PreparedStatement ps = connection.prepareStatement(bean.__deleteSql());
		bean.toStatementKey(ps);
		ps.execute();
		ps.close();
	}
	
	public void delete(Collection<Object> beans) throws SQLException {
		PreparedStatement ps = null;
		boolean first = true;
		for (Object object : beans) {
			OrmSupport bean = cast(object);
			if (first) {
				ps = connection.prepareStatement(bean.__deleteSql());
			}
			bean.toStatementKey(ps);
			ps.addBatch();
		}
		if (ps != null) {
			ps.executeBatch();
			ps.close();
		}
	}
	
	private OrmSupport cast(Object object) throws SQLException {
		if (object instanceof OrmSupport) {
			return (OrmSupport)object;
		} else if (object == null) {
			throw new SQLException("<null>");
		} else {
			throw new SQLException("invalid type " + object.getClass().getName());
		}
	}
}
