package classbuilder.demo.handler.logging;

import org.junit.Assert;
import org.junit.Test;

import classbuilder.BuilderException;
import classbuilder.handler.HandlerException;
import classbuilder.handler.ObjectFactory;

public class LoggingTestCase {
	
	@Test
	public void loggingTest() throws BuilderException, HandlerException {
		// create an object factory
		ObjectFactory factory = new ObjectFactory();
		
		// create an instance with additional log outputs
		TestObject test = (TestObject)factory.create(TestObject.class);
		
		// invoke the methods
		test.foo();
		Assert.assertEquals(42, test.bar(42));
	}
	
}
