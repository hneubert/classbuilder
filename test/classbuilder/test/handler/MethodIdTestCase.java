package classbuilder.test.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import classbuilder.BuilderModifierException;
import classbuilder.BuilderNameException;
import classbuilder.BuilderTypeException;
import classbuilder.ClassFactory;
import classbuilder.IClass;
import classbuilder.handler.ClassHandler;
import classbuilder.handler.ConstructorHandler;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;
import classbuilder.handler.MethodFilter;
import classbuilder.handler.MethodId;
import classbuilder.handler.ProxyHandler;
import classbuilder.handler.impl.DefaultHandlerContext;

public class MethodIdTestCase {
	
	@Test
	public void methodTest() throws NoSuchMethodException {
		Method method = String.class.getMethod("substring", int.class);
		MethodId m = MethodId.getMethodId(method);
		Assert.assertEquals("substring", m.getName());
		Assert.assertArrayEquals(new Class<?>[] {int.class}, m.getTypes());
		Assert.assertEquals(String.class, m.getReturnType());
		Assert.assertEquals(method, m.getDeclaration());
	}
	
	@Test
	public void constructorTest() throws NoSuchMethodException {
		Constructor<?> constructor = String.class.getConstructor(String.class);
		MethodId m = MethodId.getMethodId(constructor);
		Assert.assertEquals("<init>", m.getName());
		Assert.assertArrayEquals(new Class<?>[] {String.class}, m.getTypes());
		Assert.assertNull(m.getReturnType());
		Assert.assertEquals(constructor, m.getDeclaration());
	}
	
	@Test
	public void genericTest() throws NoSuchMethodException {
		MethodId m = MethodId.getMethodId("substring", new Class<?>[] {int.class}, String.class);
		Assert.assertEquals("substring", m.getName());
		Assert.assertArrayEquals(new Class<?>[] {int.class}, m.getTypes());
		Assert.assertEquals(String.class, m.getReturnType());
		Assert.assertNull(m.getDeclaration());
	}
	
	public abstract static class SuperClass implements Comparable<String> {
		protected void foo() {
			
		}
	}
	
	public abstract static class TestClass extends SuperClass {
		public abstract void bar();
	}
	
	@Test
	public void getMethods() throws BuilderModifierException, BuilderNameException, BuilderTypeException, HandlerException, NoSuchMethodException, SecurityException {
		ClassFactory factory = new ClassFactory();
		
		IClass cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", TestClass.class);
		HandlerContext handlerContext = new DefaultHandlerContext(null, cls, null, ClassHandler.class, 0, null, null);
		Collection<MethodId> methods = MethodId.getMethods(handlerContext);
		Assert.assertNull(methods);
		
		cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", TestClass.class);
		handlerContext = new DefaultHandlerContext(null, cls, null, MethodHandlerTestCase.class, 0, TestClass.class, null);
		methods = MethodId.getMethods(handlerContext);
		test(methods, Arrays.asList("bar", "compareTo"));
		
		cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", TestClass.class);
		handlerContext = new DefaultHandlerContext(null, cls, null, MethodHandlerTestCase.class, 0, TestClass.class.getMethod("bar"), null);
		methods = MethodId.getMethods(handlerContext);
		test(methods, Arrays.asList("bar"));
		
		cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", TestClass.class);
		handlerContext = new DefaultHandlerContext(null, cls, null, MethodHandlerTestCase.class, 0, SuperClass.class.getDeclaredMethod("foo"), null);
		methods = MethodId.getMethods(handlerContext);
		test(methods, Arrays.asList("foo"));
		
		cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", TestClass.class);
		handlerContext = new DefaultHandlerContext(null, cls, null, ConstructorHandler.class, 0, TestClass.class, null);
		methods = MethodId.getMethods(handlerContext);
		test(methods, Arrays.asList("<init>"));
		
		cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", TestClass.class);
		handlerContext = new DefaultHandlerContext(null, cls, null, ConstructorHandler.class, 0, TestClass.class.getConstructor(), null);
		methods = MethodId.getMethods(handlerContext);
		test(methods, Arrays.asList("<init>"));
		
		cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", TestClass.class);
		handlerContext = new DefaultHandlerContext(null, cls, null, ProxyHandler.class, 0, TestClass.class, null);
		methods = MethodId.getMethods(handlerContext);
		test(methods, Arrays.asList("foo", "bar", "compareTo"));
		
		cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", TestClass.class);
		handlerContext = new DefaultHandlerContext(null, cls, null, ProxyHandler.class, 0, TestClass.class.getMethod("bar"), null);
		methods = MethodId.getMethods(handlerContext);
		test(methods, Arrays.asList("bar"));
		
		cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", TestClass.class);
		handlerContext = new DefaultHandlerContext(null, cls, null, ProxyHandler.class, 0, SuperClass.class.getDeclaredMethod("foo"), null);
		methods = MethodId.getMethods(handlerContext);
		test(methods, Arrays.asList("foo"));
	}
	
	private void test(Collection<MethodId> methods, List<String> expected) {
		Assert.assertEquals(expected.size(), methods.size());
		for (MethodId m : methods) {
			Assert.assertTrue(expected.contains(m.getName()));
		}
	}
	
	public static class SuperTest {
		public void foo() {
			
		}
	}
	
	public static abstract class AbstractTest {
		public void foo() {
			
		}
		public abstract void bar();
	}
	
	@Test
	public void allMethodsTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		ClassFactory factory = new ClassFactory();
		
		IClass cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", Object.class, Comparable.class);
		HandlerContext handlerContext = new DefaultHandlerContext(null, cls, null, null, 0, null, null);
		Collection<MethodId> methods = MethodId.getAllMethods(handlerContext, false);
		Assert.assertEquals(1, methods.size());
		Assert.assertEquals("compareTo", methods.iterator().next().getName());
		
		cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", SuperTest.class);
		handlerContext = new DefaultHandlerContext(null, cls, null, null, 0, null, null);
		methods = MethodId.getAllMethods(handlerContext, true);
		Assert.assertEquals(1, methods.size());
		Assert.assertEquals("foo", methods.iterator().next().getName());
		
		cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", AbstractTest.class);
		handlerContext = new DefaultHandlerContext(null, cls, null, null, 0, null, null);
		methods = MethodId.getAllMethods(handlerContext, false);
		Assert.assertEquals(1, methods.size());
		Assert.assertEquals("bar", methods.iterator().next().getName());
	}
	
	@Test
	public void filterTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		ClassFactory factory = new ClassFactory();
		
		IClass cls = factory.createClass(IClass.PUBLIC, "pkg", "Cls", AbstractTest.class, Comparable.class);
		HandlerContext handlerContext = new DefaultHandlerContext(null, cls, null, null, 0, null, null);
		
		Collection<MethodId> methods = MethodId.getMethods(handlerContext, false, new MethodFilter() {
			@Override
			public boolean checkMethod(MethodId method) {
				return method.getName().equals("compareTo");
			}
		});
		Assert.assertEquals(1, methods.size());
		Assert.assertEquals("compareTo", methods.iterator().next().getName());
	}
}
