package classbuilder.test.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import classbuilder.BuilderException;
import classbuilder.handler.AbstractMethodHandler;
import classbuilder.handler.Handler;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;
import classbuilder.handler.Ignore;
import classbuilder.handler.MethodId;
import classbuilder.handler.MethodSelector;
import classbuilder.handler.ObjectFactory;

public class MethodHandlerTestCase {
	
	public static class TestHandler extends AbstractMethodHandler implements MethodSelector {
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			Return(getParameter(0).add(1));
		}
		
		@Override
		public Collection<MethodId> getMethods(HandlerContext handlerContext) throws HandlerException {
			return MethodId.getMethods(handlerContext, true);
		}
	}
	
	public static class TestHandler2 extends AbstractMethodHandler implements MethodSelector {
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			Return(getParameter(0).add(2));
		}
		
		@Override
		public Collection<MethodId> getMethods(HandlerContext handlerContext) throws HandlerException {
			return MethodId.getMethods(handlerContext, true);
		}
	}
	
	@Handler(TestHandler.class)
	@Target({ElementType.TYPE, ElementType.CONSTRUCTOR})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyHandler {
		
	}
	
	@MyHandler
	public static abstract class MethodHandlerTest {
		public int foo(int i) {
			return i;
		}
		@Handler(TestHandler2.class)
		public long bar(long l) {
			return l;
		}
	}
	
	@Test
	public void methodHandlerTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		MethodHandlerTest test = factory.create(MethodHandlerTest.class);
		
		Assert.assertEquals(42 + 1, test.foo(42));
		Assert.assertEquals(42 + 2, test.bar(42));
	}
	
	@Ignore
	public static abstract class MethodHandlerIgnoreTest1 extends MethodHandlerTest {
		
	}
	
	@Test
	public void methodHandlerIgnoreTest1() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		MethodHandlerIgnoreTest1 test = factory.create(MethodHandlerIgnoreTest1.class);
		
		Assert.assertEquals(42, test.foo(42));
		Assert.assertEquals(42, test.bar(42));
	}
	
	@Ignore(TestHandler.class)
	public static abstract class MethodHandlerIgnoreTest2 extends MethodHandlerTest {
		
	}
	
	@Test
	public void methodHandlerIgnoreTest2() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		MethodHandlerIgnoreTest2 test = factory.create(MethodHandlerIgnoreTest2.class);
		
		Assert.assertEquals(42, test.foo(42));
		Assert.assertEquals(42 + 2, test.bar(42));
	}
	
	public static abstract class MethodHandlerIgnoreTest3 extends MethodHandlerTest {
		@Ignore
		public int foo(int i) {
			return i;
		}
	}
	
	@Test
	public void methodHandlerIgnoreTest3() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		MethodHandlerIgnoreTest3 test = factory.create(MethodHandlerIgnoreTest3.class);
		
		Assert.assertEquals(42, test.foo(42));
		Assert.assertEquals(42 + 2, test.bar(42));
	}
	
	public static abstract class MethodHandlerIgnoreTest4 extends MethodHandlerTest {
		@Ignore(TestHandler.class)
		public int foo(int i) {
			return i;
		}
	}
	
	@Test
	public void methodHandlerIgnoreTest4() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		MethodHandlerIgnoreTest4 test = factory.create(MethodHandlerIgnoreTest4.class);
		
		Assert.assertEquals(42, test.foo(42));
		Assert.assertEquals(42 + 2, test.bar(42));
	}
	
	public static abstract class MethodHandlerPriorityTest1 extends MethodHandlerTest {
		@Handler(value=TestHandler.class, priority=-1)
		public long bar(long i) {
			return i;
		}
	}
	
	@Test
	public void methodHandlerPriorityTest1() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		MethodHandlerPriorityTest1 test = factory.create(MethodHandlerPriorityTest1.class);
		
		Assert.assertEquals(42 + 1, test.foo(42));
		Assert.assertEquals(42 + 2, test.bar(42));
	}
	
	public static abstract class MethodHandlerPriorityTest2 extends MethodHandlerTest {
		@Handler(value=TestHandler.class, priority=1)
		public long bar(long i) {
			return i;
		}
	}
	
	@Test
	public void methodHandlerPriorityTest2() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		MethodHandlerPriorityTest2 test = factory.create(MethodHandlerPriorityTest2.class);
		
		Assert.assertEquals(42 + 1, test.foo(42));
		Assert.assertEquals(42 + 1, test.bar(42));
	}
	
	public static class MethodSelectorHandler extends AbstractMethodHandler implements MethodSelector {
		@Override
		public Collection<MethodId> getMethods(HandlerContext handlerContext) throws HandlerException {
			return Arrays.asList(MethodId.getMethodId("foo", new Class<?>[] {String.class}, int.class));
		}
		
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			Assert.assertEquals(String.class, getParameterTypes()[0]);
			Assert.assertEquals("foo", getName());
			Assert.assertEquals(int.class, getReturnType());
			Return(0);
		}
	}
	
	@Handler(MethodSelectorHandler.class)
	public static class MethodSelectorTest {
		
	}
	
	@Test
	public void methodSelectorTest() throws BuilderException, HandlerException, NoSuchMethodException, SecurityException {
		ObjectFactory factory = new ObjectFactory();
		MethodSelectorTest test = factory.create(MethodSelectorTest.class, "");
		Assert.assertNotNull(test.getClass().getDeclaredMethod("foo", String.class));
	}
}
