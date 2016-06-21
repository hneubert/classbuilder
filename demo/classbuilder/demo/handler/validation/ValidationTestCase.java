package classbuilder.demo.handler.validation;

import org.junit.Assert;
import org.junit.Test;

import classbuilder.BuilderException;
import classbuilder.handler.HandlerException;
import classbuilder.handler.ObjectFactory;

public class ValidationTestCase {
	
	@Test
	public void validationTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		TestBean test = (TestBean)factory.create(TestBean.class);
		
		test.setNumber(0);
		try {
			test.setNumber(-1);
			Assert.fail();
		} catch (Exception e) {
			
		}
		try {
			test.setNumber(2);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		test.setText("123");
		try {
			test.setText("x");
			Assert.fail();
		} catch (Exception e) {
			
		}
		try {
			test.setText(null);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		Assert.assertEquals(0, test.getNumber());
		Assert.assertEquals("123", test.getText());
	}
	
}
