package classbuilder.demo.handler.getset;

import org.junit.Assert;
import org.junit.Test;

import classbuilder.BuilderException;
import classbuilder.handler.HandlerException;
import classbuilder.handler.ObjectFactory;

public class GetterSetterTestCase {
	
	// use generated getters and setters
	@Test
	public void getterSetterTest() throws BuilderException, HandlerException {
		// create an object factory
		ObjectFactory factory = new ObjectFactory();
		
		// create an instance with implemented getters and setters
		TestBean test = (TestBean)factory.create(TestBean.class);
		
		// use getters and setters
		test.setNumber(1);
		test.setText("x");
		Assert.assertEquals(1, test.getNumber());
		Assert.assertEquals("x", test.getText());
	}
	
}
