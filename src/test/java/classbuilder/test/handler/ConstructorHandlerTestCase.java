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
import classbuilder.handler.AbstractConstructorHandler;
import classbuilder.handler.Handler;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;
import classbuilder.handler.Ignore;
import classbuilder.handler.MethodId;
import classbuilder.handler.MethodSelector;
import classbuilder.handler.ObjectFactory;

public class ConstructorHandlerTestCase {
	
	public static class BaseClass {
		protected int a;
		protected int b;
		
		public BaseClass() {
			a = 5;
		}
		
		protected BaseClass(int b) {
			this.b = b;
		}
		
		@SuppressWarnings("unused")
		private BaseClass(long c) {
			
		}
		
		public int getA() {
			return a;
		}
		
		public int getB() {
			return b;
		}
	}
	
	public static class TestHandler extends AbstractConstructorHandler {
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			if (this.getParameterTypes().length == 0) {
				get("a").set(10);
			} else {
				get("b").set(getParameter(0).add(1));
			}
		}
	}
	
	public static class TestHandler2 extends AbstractConstructorHandler {
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			get("a").set(15);
		}
	}
	
	@Handler(TestHandler.class)
	@Target({ElementType.TYPE, ElementType.CONSTRUCTOR})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyConstructorHandler {
		
	};
	
	public static class DefaultConstructorHandlerTest extends BaseClass {
		
	}
	
	@Test
	public void defaultConstructorHandlerTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		DefaultConstructorHandlerTest test = factory.create(DefaultConstructorHandlerTest.class);
		
		Assert.assertEquals(5, test.getA());
	}
	
	@MyConstructorHandler
	public static class ConstructorHandlerTest extends BaseClass {
		public ConstructorHandlerTest() {
			a = 1;
		}
		
		protected ConstructorHandlerTest(int b) {
			this.b = 2;
		}
	}
	
	@Test
	public void constructorHandlerTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ConstructorHandlerTest test = factory.create(ConstructorHandlerTest.class);
		Assert.assertEquals(10, test.getA());
		
		test = factory.create(ConstructorHandlerTest.class, 15);
		Assert.assertEquals(15 + 1, test.getB());
	}
	
	public static class ConstructorHandlerTest2 extends BaseClass {
		@MyConstructorHandler
		public ConstructorHandlerTest2() {
			a = 15;
		}
	}
	
	@Test
	public void constructorHandlerTest2() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ConstructorHandlerTest2 test = factory.create(ConstructorHandlerTest2.class);
		Assert.assertEquals(10, test.getA());
	}
	
	@Ignore
	public static class ConstructorHandlerIgnoreAllTest extends ConstructorHandlerTest {
		public ConstructorHandlerIgnoreAllTest() {
			a = 1;
		}
		
		protected ConstructorHandlerIgnoreAllTest(int b) {
			this.b = 2;
		}
	}
	
	@Test
	public void constructorHandlerIgnoreAllTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ConstructorHandlerIgnoreAllTest test = factory.create(ConstructorHandlerIgnoreAllTest.class);
		
		Assert.assertEquals(1, test.getA());
	}
	
	@Ignore(TestHandler.class)
	public static class ConstructorHandlerIgnoreSingleTest extends ConstructorHandlerTest {
		public ConstructorHandlerIgnoreSingleTest() {
			a = 1;
		}
		
		protected ConstructorHandlerIgnoreSingleTest(int b) {
			this.b = 2;
		}
	}
	
	@Test
	public void constructorHandlerIgnoreSingleTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ConstructorHandlerIgnoreSingleTest test = factory.create(ConstructorHandlerIgnoreSingleTest.class);
		
		Assert.assertEquals(1, test.getA());
	}
	
	public static class ConstructorHandlerIgnoreAllCtorTest extends ConstructorHandlerTest {
		public ConstructorHandlerIgnoreAllCtorTest() {
			a = 1;
		}
		@Ignore
		protected ConstructorHandlerIgnoreAllCtorTest(int b) {
			this.b = 2;
		}
	}
	
	@Test
	public void constructorHandlerIgnoreAllCtorTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ConstructorHandlerIgnoreAllCtorTest test = factory.create(ConstructorHandlerIgnoreAllCtorTest.class);
		
		Assert.assertEquals(10, test.getA());
		Assert.assertEquals(1, test.getClass().getDeclaredConstructors().length);
	}
	
	public static class ConstructorHandlerIgnoreSingleCtorTest extends ConstructorHandlerTest {
		public ConstructorHandlerIgnoreSingleCtorTest() {
			a = 1;
		}
		@Ignore(TestHandler.class)
		protected ConstructorHandlerIgnoreSingleCtorTest(int b) {
			this.b = 2;
		}
	}
	
	@Test
	public void constructorHandlerIgnoreSingleCtorTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ConstructorHandlerIgnoreSingleCtorTest test = factory.create(ConstructorHandlerIgnoreSingleCtorTest.class);
		
		Assert.assertEquals(10, test.getA());
		Assert.assertEquals(1, test.getClass().getDeclaredConstructors().length);
	}
	
	@Handler(value=TestHandler2.class, priority=-1)
	public static class PriorityTest1 extends ConstructorHandlerTest {
		public PriorityTest1() {
			a = 1;
		}
	}
	
	@Test
	public void priorityTest1() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		PriorityTest1 test = factory.create(PriorityTest1.class);
		
		Assert.assertEquals(10, test.getA());
	}
	
	public static class PriorityTest2 extends ConstructorHandlerTest {
		@Handler(value=TestHandler2.class, priority=1)
		public PriorityTest2() {
			a = 1;
		}
	}
	
	@Test
	public void priorityTest2() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		PriorityTest2 test = factory.create(PriorityTest2.class);
		
		Assert.assertEquals(15, test.getA());
	}
	
	public static class MethodSelectorHandler extends AbstractConstructorHandler implements MethodSelector {
		@Override
		public Collection<MethodId> getMethods(HandlerContext handlerContext) throws HandlerException {
			return Arrays.asList(MethodId.getMethodId(null, new Class<?>[] {String.class}, null));
		}
		
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			Assert.assertEquals(String.class, getParameterTypes()[0]);
		}
	}
	
	@Handler(MethodSelectorHandler.class)
	public static class MethodSelectorTest {
		
	}
	
	@Test
	public void methodSelectorTest() throws BuilderException, HandlerException, NoSuchMethodException, SecurityException {
		ObjectFactory factory = new ObjectFactory();
		MethodSelectorTest test = factory.create(MethodSelectorTest.class, "");
		Assert.assertNotNull(test.getClass().getDeclaredConstructor(String.class));
	}
}
