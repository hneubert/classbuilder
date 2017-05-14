package classbuilder.demo.handler.orm;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import classbuilder.BuilderException;
import classbuilder.handler.HandlerException;
import classbuilder.handler.ObjectFactory;

public class OrmTestCase {
	
	private Connection connection;
	
	@Before
	public void before() throws Exception {
		// load derby
		URL url = new File(System.getenv().get("JAVA_HOME").replace('\\', '/') + "/db/lib/derby.jar").toURI().toURL();
		URLClassLoader cl = new URLClassLoader(new URL[] {url}, Thread.currentThread().getContextClassLoader());
		Thread.currentThread().setContextClassLoader(cl);
		Driver driver = (Driver)Class.forName("org.apache.derby.jdbc.AutoloadedDriver", true, cl).getConstructor().newInstance();
		connection = driver.connect("jdbc:derby:memory:JcbTestDB;create=true", new Properties());
		
		// create a table
		PreparedStatement ps = connection.prepareStatement("create table t_test (c_int integer, c_float float, c_text varchar(15))");
		ps.execute();
		ps.close();
	}
	
	// simple orm mapping
	@Test
	public void ormTest() throws SQLException, BuilderException, HandlerException {
		// create a object factory
		ObjectFactory factory = new ObjectFactory();
		
		// create and fill a test bean
		TestBean b = factory.create(TestBean.class);
		b.setFloatNumber(1.23f);
		b.setNumber(42);
		b.setText("hallo world");
		
		// create a orm-mapper with a connection and an object factory
		OrmMapper mapper = new OrmMapper(connection, factory);
		
		// insert data into database
		mapper.insert(b);
		
		// read data from database
		mapper.select(b);
		
		// update data
		b.setText("foo");
		mapper.update(b);
		
		// read and print all
		Collection<TestBean> list = mapper.select(TestBean.class, null);
		for (TestBean bean : list) {
			System.out.println(bean.getFloatNumber() + " " + bean.getNumber() + " " + bean.getText());
		}
		
		connection.close();
	}
	
}
