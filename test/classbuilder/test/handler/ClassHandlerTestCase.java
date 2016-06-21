package classbuilder.test.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import classbuilder.BuilderException;
import classbuilder.IClass;
import classbuilder.IMethod;
import classbuilder.handler.AbstractClassHandler;
import classbuilder.handler.Handler;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;
import classbuilder.handler.Ignore;
import classbuilder.handler.ObjectFactory;

public class ClassHandlerTestCase {
	
	public static List<String> executionOrder = new ArrayList<String>();
	
	public static class TestHandler extends AbstractClassHandler {
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			executionOrder.add("TestHandler");
			IMethod bar = addMethod(IClass.PUBLIC, int.class, "bar", int.class);
				bar.Return(bar.getParameter(0));
			bar.End();
		}
	}
	
	public static class TestHandler2 extends AbstractClassHandler {
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			executionOrder.add("TestHandler2");
			IMethod bar = addMethod(IClass.PUBLIC, int.class, "foo", int.class);
				bar.Return(bar.getParameter(0).add(1));
			bar.End();
		}
	}
	
	@Handler(TestHandler.class)
	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyClassHandler {
		
	};
	
	@MyClassHandler
	public interface ClassHandlerTest {
		public int bar(int a);
	}
	
	@Test
	public void classHandlerTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ClassHandlerTest test = factory.create(ClassHandlerTest.class);
		
		int result = test.bar(42);
		Assert.assertEquals(42, result);
	}
	
	@Ignore
	public static abstract class ClassHandlerIgnoreAllTest implements ClassHandlerTest {
		public int bar(int a) {
			return a * 2;
		}
	}
	
	@Test
	public void classHandlerIgnoreAllTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ClassHandlerIgnoreAllTest test = factory.create(ClassHandlerIgnoreAllTest.class);
		
		int result = test.bar(42);
		Assert.assertEquals(42 * 2, result);
	}
	
	@Ignore(TestHandler.class)
	@Handler(value=TestHandler2.class)
	public static abstract class ClassHandlerIgnoreSingleTest implements ClassHandlerTest {
		public int bar(int a) {
			return a * 2;
		}
		public abstract int foo(int a);
	}
	
	@Test
	public void classHandlerIgnoreSingleTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ClassHandlerIgnoreSingleTest test = factory.create(ClassHandlerIgnoreSingleTest.class);
		
		int result = test.bar(42);
		Assert.assertEquals(42 * 2, result);
		
		result = test.foo(42);
		Assert.assertEquals(42 + 1, result);
	}
	
	@Handler(value=TestHandler2.class, priority=-1)
	public static abstract class ClassHandlerPriorityLowerTest implements ClassHandlerTest {
		public abstract int foo(int a);
	}
	
	@Test
	public void classHandlerPriorityLowerTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		executionOrder.clear();
		
		ClassHandlerPriorityLowerTest test = factory.create(ClassHandlerPriorityLowerTest.class);
		
		int result = test.bar(42);
		Assert.assertEquals(42, result);
		
		result = test.foo(42);
		Assert.assertEquals(42 + 1, result);
		
		Assert.assertEquals(executionOrder.size(), 2);
		Assert.assertEquals(executionOrder.get(0), "TestHandler2");
		Assert.assertEquals(executionOrder.get(1), "TestHandler");
	}
	
	@Handler(value=TestHandler2.class, priority=1)
	public static abstract class ClassHandlerPriorityHigherTest implements ClassHandlerTest {
		public abstract int foo(int a);
	}
	
	@Test
	public void classHandlerPriorityHigherTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		executionOrder.clear();
		
		ClassHandlerPriorityHigherTest test = factory.create(ClassHandlerPriorityHigherTest.class);
		
		int result = test.bar(42);
		Assert.assertEquals(42, result);
		
		result = test.foo(42);
		Assert.assertEquals(42 + 1, result);
		
		Assert.assertEquals(executionOrder.size(), 2);
		Assert.assertEquals(executionOrder.get(0), "TestHandler");
		Assert.assertEquals(executionOrder.get(1), "TestHandler2");
	}
	
	@Handler(value=TestHandler.class)
	public interface ClassHandlerMultibleTest extends ClassHandlerTest {
		
	}
	
	@Test
	public void classHandlerMultibleTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		executionOrder.clear();
		
		ClassHandlerTest test = factory.create(ClassHandlerMultibleTest.class);
		
		int result = test.bar(42);
		Assert.assertEquals(42, result);
		
		Assert.assertEquals(executionOrder.size(), 1);
		Assert.assertEquals(executionOrder.get(0), "TestHandler");
	}
}
