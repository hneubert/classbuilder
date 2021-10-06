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
import classbuilder.handler.AbstractProxyHandler;
import classbuilder.handler.Handler;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;
import classbuilder.handler.Ignore;
import classbuilder.handler.MethodId;
import classbuilder.handler.MethodSelector;
import classbuilder.handler.ObjectFactory;

/**
 * 4x Ignore
 * 2x Priority
 * 
 */
public class ProxyHandlerTestCase {
	
	public static class ProxyHandler1 extends AbstractProxyHandler {
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			Return(invoke(getParameter(0).add(1)));
		}
	}
	
	public static class ProxyHandler2 extends AbstractProxyHandler {
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			Return(invoke(getParameter(0).mul(2)));
		}
	}
	
	public static class MethodHandler extends AbstractMethodHandler {
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			Return(getParameter(0));
		}
	}
	
	@Handler(ProxyHandler1.class)
	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyHandler {
		
	}
	
	@MyHandler
	public static abstract class ProxyTest {
		@Handler(ProxyHandler2.class)
		public int foo(int i) {
			return i;
		}
		@MyHandler
		@Handler(MethodHandler.class)
		public abstract long bar(long l);
	}
	
	@Test
	public void proxyHandlerTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ProxyTest test = factory.create(ProxyTest.class);
		
		Assert.assertEquals(42 * 2 + 1, test.foo(42));
		Assert.assertEquals(42 + 1, test.bar(42));
	}
	
	@Ignore
	public abstract static class IgnoreTest1 extends ProxyTest {
		public int foo(int i) {
			return i;
		}
		public long bar(long l) {
			return l;
		}
	}
	
	@Test
	public void ignoreProxyTest1() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		IgnoreTest1 test = factory.create(IgnoreTest1.class);
		
		Assert.assertEquals(42, test.foo(42));
		Assert.assertEquals(42, test.bar(42));
	}
	
	@Ignore(ProxyHandler1.class)
	public abstract static class IgnoreTest2 extends ProxyTest {
		public int foo(int i) {
			return i;
		}
	}
	
	@Test
	public void ignoreProxyTest2() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		IgnoreTest2 test = factory.create(IgnoreTest2.class);
		
		Assert.assertEquals(42 * 2, test.foo(42));
		Assert.assertEquals(42, test.bar(42));
	}
	
	public abstract static class IgnoreTest3 extends ProxyTest {
		@Ignore
		public int foo(int i) {
			return i;
		}
	}
	
	@Test
	public void ignoreProxyTest3() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		IgnoreTest3 test = factory.create(IgnoreTest3.class);
		
		Assert.assertEquals(42, test.foo(42));
		Assert.assertEquals(42 + 1, test.bar(42));
	}
	
	public abstract static class IgnoreTest4 extends ProxyTest {
		@Ignore(ProxyHandler1.class)
		public int foo(int i) {
			return i;
		}
	}
	
	@Test
	public void ignoreProxyTest4() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		IgnoreTest4 test = factory.create(IgnoreTest4.class);
		
		Assert.assertEquals(42 * 2, test.foo(42));
		Assert.assertEquals(42 + 1, test.bar(42));
	}
	
	
	
	
	public abstract static class ProxyPriorityTest1 extends ProxyTest {
		@Handler(value=ProxyHandler2.class, priority=-1)
		public abstract long bar(long l);
	}
	
	@Test
	public void proxyPriorityTest1() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ProxyPriorityTest1 test = factory.create(ProxyPriorityTest1.class);
		
		Assert.assertEquals(42 * 2 + 1, test.foo(42));
		Assert.assertEquals((42 + 1) * 2, test.bar(42));
	}
	
	public abstract static class ProxyPriorityTest2 extends ProxyTest {
		@Handler(value=ProxyHandler2.class, priority=1)
		public abstract long bar(long l);
	}
	
	@Test
	public void proxyPriorityTest2() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ProxyPriorityTest2 test = factory.create(ProxyPriorityTest2.class);
		
		Assert.assertEquals(42 * 2 + 1, test.foo(42));
		Assert.assertEquals(42 * 2 + 1, test.bar(42));
	}
	
	public static class MethodSelectorHandler extends AbstractProxyHandler implements MethodSelector {
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
		public int foo(String s) {
			return 0;
		}
	}
	
	@Test
	public void methodSelectorTest() throws BuilderException, HandlerException, NoSuchMethodException, SecurityException {
		ObjectFactory factory = new ObjectFactory();
		MethodSelectorTest test = factory.create(MethodSelectorTest.class, "");
		Assert.assertNotNull(test.getClass().getDeclaredMethod("foo", String.class));
	}
}
