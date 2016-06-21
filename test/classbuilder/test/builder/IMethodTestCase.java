package classbuilder.test.builder;

import static classbuilder.IClass.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import classbuilder.BuilderAccessException;
import classbuilder.BuilderCompilerException;
import classbuilder.BuilderException;
import classbuilder.BuilderModifierException;
import classbuilder.BuilderNameException;
import classbuilder.BuilderSyntaxException;
import classbuilder.BuilderTypeException;
import classbuilder.ClassFactory;
import classbuilder.IAnnotation;
import classbuilder.IClass;
import classbuilder.IConstructor;
import classbuilder.IField;
import classbuilder.IMethod;
import classbuilder.Variable;

public class IMethodTestCase {
	private ClassFactory classFactory = new ClassFactory();
	
	public interface SimpleInterface {
		public int foo();
	}
	
	public static class SimpleClass {
		public int method(int param) {
			return param;
		}
		
		public static int staticMethod(int param) {
			return param;
		}
		
		public final int finalMethod(int param) {
			return param;
		}
	}
	
	@Before
	public void before() {
		counter++;
	}
	
	@Test
	public void $_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "DollarTest", Object.class);
		IField ifield = cls.addField(PUBLIC, int.class, "test");
		IMethod method = cls.addMethod(PUBLIC, "foo");
		Variable var = method.addVar(int.class);
		method.$(null);
		method.$(true);
		method.$((byte)1);
		method.$((short)2);
		method.$(3);
		method.$(4L);
		method.$(5.0f);
		method.$(6.0);
		method.$((char)7);
		method.$("text");
		method.$(Object.class);
		method.$(method.$(42));
		method.$(var);
		
		try {
			method.$(ifield);
			Assert.fail("$ field");
		} catch (BuilderTypeException e) {
			
		}
		
		try {
			method.$(new Object());
			Assert.fail("$ <Object>");
		} catch (BuilderTypeException e) {
			
		}
	}
	
	@Test
	public void addAnnotationTest() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderAccessException, BuilderCompilerException, NoSuchMethodException, SecurityException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "MethodAddAnnotationTest", Object.class);
			IMethod method = cls.addMethod(PUBLIC, "foo");
				IAnnotation annotation = method.addAnnotation(TestAnnotation.class);
				annotation.setValue("value", "test");
			method.End();
		Class<?> c = cls.build();
		TestAnnotation a = c.getMethod("foo").getAnnotation(TestAnnotation.class);
		Assert.assertNotNull(a);
		Assert.assertEquals("test", a.value());
	}
	
	@Test
	public void addVarTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, int.class, "foo");
		try {
			method.addVar(null);
			Assert.fail("<null>");
		} catch (BuilderTypeException e) {
			
		}
		try {
			method.addVar(void.class);
			Assert.fail("void");
		} catch (BuilderTypeException e) {
			
		}
		try {
			method.addVar(Void.class);
			Assert.fail("Void");
		} catch (BuilderTypeException e) {
			
		}
		try {
			method.addVar(int.class);
			method.addVar(Object.class);
		} catch (BuilderTypeException e) {
			Assert.fail("int/Object");
		}
	}
	
	@Test
	public void EndTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, "foo");
		method.End();
		method = cls.addMethod(PUBLIC, int.class, "bar");
		try {
			method.End();
			Assert.fail("End <no return value>");
		} catch (BuilderSyntaxException e) {
			
		}
		method.Return(1);
		method.End();
		try {
			method.End();
			Assert.fail("allready closed");
		} catch (BuilderSyntaxException e) {
			
		}
	}
	
	@Test
	public void getTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, NoSuchFieldException, SecurityException, BuilderSyntaxException, BuilderAccessException, BuilderCompilerException, InstantiationException, IllegalAccessException {
		IClass cls2 = classFactory.createClass(PUBLIC, "pkg", "Class", TestObject.class);
		IField ifield2 = cls2.addField(PUBLIC, int.class, "test");
		Field filed2 = System.class.getField("out");
		
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", TestObject.class);
		IField ifield = cls.addField(PUBLIC, int.class, "test");
		IField staticIfield = cls.addField(PUBLIC | STATIC, int.class, "staticTest");
		Field field = TestObject.class.getField("publicField");
		Field staticField = TestObject.class.getField("staticField");
		
		IMethod method = cls.addMethod(PUBLIC, "method");
			method.get("test");
			method.get("publicField");
			method.get("staticField");
			method.get(field);
			method.get(staticField);
			method.get(ifield);
			method.get(staticIfield);
			try {
				method.get("foo");
				Assert.fail("getField(String)");
			} catch (BuilderAccessException e) {
				
			}
			try {
				method.get(filed2);
				Assert.fail("getField(Field)");
			} catch (BuilderAccessException e) {
				
			}
			try {
				method.get(ifield2);
				Assert.fail("getField(IField)");
			} catch (BuilderAccessException e) {
				
			}
		
		method = cls.addMethod(PUBLIC | STATIC, "staticMethod2");
			try {
				method.get("test");
				Assert.fail("getField(String)");
			} catch (BuilderAccessException e) {
				
			}
			try {
				method.get("publicField");
				Assert.fail("getField(String)");
			} catch (BuilderAccessException e) {
				
			}
			method.get("staticField");
			try {
				method.get(field);
				Assert.fail("getField(Field)");
			} catch (BuilderAccessException e) {
				
			}
			method.get(staticField);
			try {
				method.get(ifield);
				Assert.fail("getField(IField)");
			} catch (BuilderAccessException e) {
				
			}
			method.get(staticIfield);
			
		cls = classFactory.createClass(PUBLIC, "methodTest", "GetTest1", TestObject.class);
		ifield = cls.addField(PUBLIC, int.class, "test");
		staticIfield = cls.addField(PUBLIC | STATIC, int.class, "staticTest");
		field = TestObject.class.getField("publicField");
		staticField = TestObject.class.getField("staticField");
		method = cls.addMethod(PUBLIC, int.class, "foo");
			Variable i = method.addVar(int.class);
			method.get("test").set(1);
			i.set(method.get("test"));
			method.get("publicField").set(1);
			i.set(method.get("publicField"));
			method.get("staticField").set(1);
			i.set(method.get("staticField"));
			method.get(field).set(1);
			i.set(method.get(field));
			method.get(staticField).set(1);
			i.set(method.get(staticField));
			method.get(ifield).set(1);
			i.set(method.get(ifield));
			method.get(staticIfield).set(1);
			i.set(method.get(staticIfield));
			method.Return(1);
		method.End();
		Class<?> c = cls.build();
		TestObject obj = (TestObject)c.newInstance();
		obj.foo();
		
		cls = classFactory.createClass(PUBLIC, "methodTest", "GetTest2", TestObject.class);
		ifield = cls.addField(PUBLIC, int.class, "test");
		staticIfield = cls.addField(PUBLIC | STATIC, int.class, "staticTest");
		field = TestObject.class.getField("publicField");
		staticField = TestObject.class.getField("staticField");
		method = cls.addMethod(PUBLIC | STATIC, int.class, "foo");
			i = method.addVar(int.class);
			method.get("staticField").set(1);
			i.set(method.get("staticField"));
			method.get(staticField).set(1);
			i.set(method.get(staticField));
			method.get(staticIfield).set(1);
			i.set(method.get(staticIfield));
			method.Return(1);
		method.End();
		c = cls.build();
		obj = (TestObject)c.newInstance();
		obj.foo();
	}
	
	@Test
	public void invokeTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, NoSuchFieldException, SecurityException, BuilderSyntaxException, BuilderAccessException, NoSuchMethodException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls2 = classFactory.createClass(PUBLIC, "pkg", "Class", TestObject.class);
		IMethod imethod2 = cls2.addMethod(PUBLIC, int.class, "test");
		Method method2 = System.class.getMethod("console");
		
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", TestObject.class);
		IMethod imethod = cls.addMethod(PUBLIC, int.class, "test");
		IMethod staticImethod = cls.addMethod(PUBLIC | STATIC, int.class, "staticTest");
		Method method = TestObject.class.getMethod("publicMethod");
		Method staticMethod = TestObject.class.getMethod("staticMethod");
		
		IMethod m = cls.addMethod(PUBLIC, "method");
			m.invoke("test");
			m.invoke("publicMethod");
			m.invoke("staticMethod");
			m.invoke(method);
			m.invoke(staticMethod);
			m.invoke(imethod);
			m.invoke(staticImethod);
			try {
				m.invoke("bar");
				Assert.fail("getField(String)");
			} catch (BuilderAccessException e) {
				
			}
			try {
				m.invoke(method2);
				Assert.fail("getField(Field)");
			} catch (BuilderAccessException e) {
				
			}
			try {
				m.invoke(imethod2);
				Assert.fail("getField(IField)");
			} catch (BuilderAccessException e) {
				
			}
		
		m = cls.addMethod(PUBLIC | STATIC, "staticMethod2");
			try {
				m.invoke("test");
				Assert.fail("getField(String)");
			} catch (BuilderAccessException e) {
				
			}
			try {
				m.invoke("publicMethod");
				Assert.fail("getField(String)");
			} catch (BuilderAccessException e) {
				
			}
			m.invoke("staticMethod");
			try {
				m.invoke(method);
				Assert.fail("getField(Field)");
			} catch (BuilderAccessException e) {
				
			}
			m.invoke(staticMethod);
			try {
				m.invoke(imethod);
				Assert.fail("getField(IField)");
			} catch (BuilderAccessException e) {
				
			}
			m.invoke(staticImethod);
			
		cls = classFactory.createClass(PUBLIC, "methodTest", "InvokeTest1", TestObject.class);
		imethod = cls.addMethod(PUBLIC, int.class, "test", int.class);
			imethod.Return(1);
		imethod.End();
		staticImethod = cls.addMethod(PUBLIC | STATIC, int.class, "staticTest", int.class);
			staticImethod.Return(1);
		staticImethod.End();
		method = TestObject.class.getMethod("publicMethod", int.class);
		staticMethod = TestObject.class.getMethod("staticMethod", int.class);
		m = cls.addMethod(PUBLIC, int.class, "foo");
			Variable i = m.addVar(int.class);
			i.set(m.invoke("test", 1));
			i.set(m.invoke("publicMethod", 1));
			i.set(m.invoke("staticMethod", 1));
			i.set(m.invoke(method, 1));
			i.set(m.invoke(staticMethod, 1));
			i.set(m.invoke(imethod, 1));
			i.set(m.invoke(staticImethod, 1));
			m.Return(1);
		m.End();
		Class<?> c = cls.build();
		TestObject obj = (TestObject)c.newInstance();
		obj.foo();
		
		cls = classFactory.createClass(PUBLIC, "methodTest", "InvokeTest2", TestObject.class);
		imethod = cls.addMethod(PUBLIC, int.class, "test", int.class);
			imethod.Return(1);
		imethod.End();
		staticImethod = cls.addMethod(PUBLIC | STATIC, int.class, "staticTest", int.class);
			staticImethod.Return(1);
		staticImethod.End();
		method = TestObject.class.getMethod("publicMethod", int.class);
		staticMethod = TestObject.class.getMethod("staticMethod", int.class);
		m = cls.addMethod(PUBLIC | STATIC, int.class, "foo");
			i = m.addVar(int.class);
			i.set(m.invoke("staticMethod", 1));
			i.set(m.invoke(staticMethod, 1));
			i.set(m.invoke(staticImethod, 1));
			m.Return(1);
		m.End();
		c = cls.build();
		obj = (TestObject)c.newInstance();
		obj.foo();
	}
	
	@Test
	public void method() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(1);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test
	public void staticMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		IClass cls = addClass();
			IMethod m = cls.addMethod(PUBLIC | STATIC, int.class, "foo");
				m.Return(1);
			m.End();
		Object test = cls.build().newInstance();
		Assert.assertEquals(1, test.getClass().getMethod("foo").invoke(test));
	}
	
	@Test
	public void finalMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC | FINAL, int.class, "foo");
				m.Return(1);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test
	public void abstractMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls = classFactory.createClass(PUBLIC | ABSTRACT, "generated", "MethodTest" + counter, Object.class, SimpleInterface.class);
			cls.addMethod(PUBLIC | ABSTRACT, int.class, "foo");
		cls.build();
	}
	
	@Test
	public void invokeMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, BuilderAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod method = cls.addMethod(PUBLIC, int.class, "bar", int.class);
				method.Return(method.getParameter(0));
			method.End();
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.invoke(method, 42));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void invokeStaticMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, BuilderAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod method = cls.addMethod(PUBLIC, int.class, "bar", int.class);
				method.Return(method.getParameter(0));
			method.End();
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.invoke(method, 42));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void invokeFinalMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, BuilderAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod method = cls.addMethod(PUBLIC, int.class, "bar", int.class);
				method.Return(method.getParameter(0));
			method.End();
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.invoke(method, 42));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void invokeAbstractMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, BuilderAccessException {
		IClass cls = classFactory.createClass(PUBLIC | ABSTRACT, "generated", "MethodTest" + counter, Object.class, SimpleInterface.class);
			IMethod method = cls.addMethod(PUBLIC | ABSTRACT, int.class, "bar", int.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.invoke(method, 42));
			m.End();
		cls.build();
	}
	
	
	
	
	
	@Test
	public void thisMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, BuilderAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "MethodTest" + counter, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.This().invoke("method", 42));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test(expected=BuilderAccessException.class)
	public void thisStaticMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, BuilderAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "MethodTest" + counter, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.This().invoke("staticMethod", 42));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void thisFinalMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, BuilderAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "MethodTest" + counter, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.This().invoke("finalMethod", 42));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
//	@Test
//	public void thisAbstractMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, BuilderAccessException {
//		IClass cls = addClass(PUBLIC, null, null, SimpleClass.class, SimpleInterface.class);
//			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
//				Return(This().invoke("abstractMethod", 42));
//			End();
//		End();
//		
//		cls.build();
//	}
	
	
	
	
	@Test
	public void superMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, BuilderAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "MethodTest" + counter, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.Super().invoke("method", 42));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void superStaticMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, BuilderAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "MethodTest" + counter, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.Super().invoke("staticMethod", 42));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void superFinalMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, BuilderAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "MethodTest" + counter, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.Super().invoke("finalMethod", 42));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
//	@Test
//	public void superAbstractMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, BuilderAccessException {
//		IClass cls = addClass(PUBLIC, null, null, SimpleClass.class, SimpleInterface.class);
//			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
//				Return(Super().invoke("abstractMethod", 42));
//			End();
//		End();
//		
//		cls.build();
//	}
	
	
	
	
	@Test
	public void instanceMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, BuilderAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "MethodTest" + counter, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable var = m.addVar(SimpleClass.class);
				var.set(m.New(SimpleClass.class));
				m.Return(var.invoke("method", 42));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test(expected=BuilderAccessException.class)
	public void instanceStaticMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, BuilderAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "MethodTest" + counter, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable var = m.addVar(SimpleClass.class);
				var.set(m.New(SimpleClass.class));
				m.Return(var.invoke("staticMethod", 42));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void instanceFinalMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, BuilderAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "MethodTest" + counter, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable var = m.addVar(SimpleClass.class);
				var.set(m.New(SimpleClass.class));
				m.Return(var.invoke("finalMethod", 42));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
//	@Test
//	public void instanceAbstractMethod() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException, BuilderAccessException {
//		IClass cls = addClass(PUBLIC, null, null, SimpleClass.class, SimpleInterface.class);
//			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
//				Variable var = addVar(SimpleClass.class);
//				var.set(New(SimpleClass.class));
//				Return(var.invoke("abstractMethod", 42));
//			End();
//		End();
//		
//		cls.build();
//	}
	
	private static int counter = 0;
	private IClass addClass(Class<?> ...intf) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		counter++;
		return classFactory.createClass(PUBLIC, "generated", "MethodTest" + counter, Object.class, intf);
	}
	
	@Test
	public void getNameTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, int.class, "foo");
		Assert.assertEquals("foo", method.getName());
	}
	
	@Test
	public void getModifiersTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, int.class, "foo");
		Assert.assertEquals(PUBLIC, method.getModifiers());
	}
	
	@Test
	public void getDeclaringClassTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, int.class, "foo");
		Assert.assertEquals(cls, method.getDeclaringClass());
	}
	
	@Test
	public void getReturnTypeTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, int.class, "foo");
		Assert.assertEquals(int.class, method.getReturnType());
	}
	
	@Test
	public void getParametersTest() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, int.class, "foo", int.class);
		Assert.assertEquals(1, method.getParameters().length);
		Assert.assertEquals(int.class, method.getParameters()[0].getType());
	}
	
	@Test
	public void getParameterTypes_test() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, int.class, "foo", int.class);
		Assert.assertEquals(1, method.getParameterTypes().length);
		Assert.assertEquals(int.class, method.getParameterTypes()[0]);
	}
	
	@Test
	public void getParameterTest() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, int.class, "foo", int.class);
		Assert.assertEquals(int.class, method.getParameter(0).getType());
	}
	
	@Test
	public void getLocalsTest() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, int.class, "foo");
		method.addVar(int.class);
		Assert.assertEquals(1, method.getLocals().size());
		Assert.assertEquals(int.class, method.getLocals().iterator().next().getType());
	}
	
	@Test
	public void getAnnotationsTest() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, int.class, "foo");
		method.addAnnotation(TestAnnotation.class);
		Assert.assertEquals(1, method.getAnnotations().size());
		Assert.assertEquals(TestAnnotation.class, method.getAnnotations().iterator().next().getType());
	}
	
	@Test
	public void isClosedTest() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, "foo");
		Assert.assertEquals(false, method.isClosed());
		method.End();
		Assert.assertEquals(true, method.isClosed());
	}
	
	@Test
	public void isConstructorTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IMethod method = cls.addMethod(PUBLIC, "foo");
		Assert.assertEquals(false, method.isConstructor());
		method.End();
		method = cls.addConstructor(PUBLIC, int.class);
		Assert.assertEquals(true, method.isConstructor());
	}
	
	public interface NewTest {
		public Object foo();
	}
	
	@Test
	public void newTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException, BuilderCompilerException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "methodTest", "NewTest", Object.class);
			IMethod method = cls.addMethod(PUBLIC, "foo");
				try {
					method.New(null);
					Assert.fail("new <null>");
				} catch (BuilderTypeException e) {
					
				}
				try {
					method.New(void.class);
					Assert.fail("new void");
				} catch (BuilderTypeException e) {
					
				}
				try {
					method.New(Void.class);
					Assert.fail("new Void");
				} catch (BuilderTypeException e) {
					
				}
				try {
					method.New(int.class);
					Assert.fail("new <primitive>");
				} catch (BuilderTypeException e) {
					
				}
				try {
					method.New(Serializable.class);
					Assert.fail("new <interface>");
				} catch (BuilderTypeException e) {
					
				}
				try {
					method.New(List.class);
					Assert.fail("new <abstract class>");
				} catch (BuilderTypeException e) {
					
				}
				try {
					method.New(int[].class);
					Assert.fail("new <array without length>");
				} catch (BuilderTypeException e) {
					
				}
//				try {
//					method.New(int[].class, -1);
//					Assert.fail("new <array with negative length>");
//				} catch (BuilderTypeException e) {
//					
//				}
				try {
					method.New(int[].class, (Object)null);
					Assert.fail("new <array with <null> length>");
				} catch (BuilderTypeException e) {
					
				}
				try {
					method.New(Object.class, 123);
					Assert.fail("new <no constructor>");
				} catch (BuilderAccessException e) {
					
				}
		
		cls = classFactory.createClass(PUBLIC, "methodTest", "NewTest2", Object.class, NewTest.class);
			method = cls.addMethod(PUBLIC, Object.class, "foo");
				method.Return(method.New(Object.class));
			method.End();
		Class<?> c = cls.build();
		NewTest test = (NewTest)c.newInstance();
		Assert.assertEquals(Object.class, test.foo().getClass());
		
		cls = classFactory.createClass(PUBLIC, "methodTest", "NewTest3", Object.class, NewTest.class);
			method = cls.addMethod(PUBLIC, Object.class, "foo");
				method.Return(method.New(int[].class, 1));
			method.End();
		c = cls.build();
		test = (NewTest)c.newInstance();
		Assert.assertEquals(int[].class, test.foo().getClass());
		
		
		cls = classFactory.createClass(PUBLIC, "methodTest", "NewTest4", Object.class, NewTest.class);
			method = cls.addMethod(PUBLIC, Object.class, "foo");
				method.Return(method.New(Integer.class, 42));
			method.End();
		c = cls.build();
		test = (NewTest)c.newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	public interface ReturnTest {
		public void foo();
		public int bar();
		public void foobar();
	}
	
	@Test
	public void returnTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderCompilerException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "methodTest", "ReturnTest", Object.class, ReturnTest.class);
			IMethod method = cls.addMethod(PUBLIC, "foo");
				try {
					method.Return(1);
					Assert.fail("no return type");
				} catch (BuilderTypeException e) {
					
				}
				method.Return();
				try {
					method.Return();
					Assert.fail("return <no value, dead code>");
				} catch (BuilderSyntaxException e) {
					
				}
			method.End();
			
			method = cls.addMethod(PUBLIC, "foobar");
				// implicit return
			method.End();
			
			method = cls.addMethod(PUBLIC, int.class, "bar");
				try {
					method.Return();
					Assert.fail("return <no data>");
				} catch (BuilderTypeException e) {
					
				}
				try {
					method.Return(1L);
					Assert.fail("return <invalid type>");
				} catch (BuilderTypeException e) {
					
				}
				method.Return(1);
				try {
					method.Return(1);
					Assert.fail("return <dead code>");
				} catch (BuilderSyntaxException e) {
					
				}
			method.End();
		Class<?> c = cls.build();
		
		ReturnTest test = (ReturnTest)c.newInstance();
		test.foo();
		Assert.assertEquals(1, test.bar());
		test.foobar();
	}
	
	@Test
	public void throwTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "methodTest", "ThrowTest", Object.class, SimpleInterface.class);
			IMethod method = cls.addMethod(PUBLIC, int.class, "foo");
				try {
					method.Throw(method.$(1));
					Assert.fail("throw <no exception type>");
				} catch (BuilderTypeException e) {
					
				}
		
		cls = classFactory.createClass(PUBLIC, "methodTest", "ThrowTest", Object.class, SimpleInterface.class);
			method = cls.addMethod(PUBLIC, int.class, "foo");
				method.Throw(method.New(Exception.class, "test"));
				try {
					method.Return(1);
					Assert.fail("throw <dead code>");
				} catch (BuilderSyntaxException e) {
					
				}
			method.End();
		Class<?> c = cls.build();
		
		SimpleInterface test = (SimpleInterface)c.newInstance();
		
		try {
			test.foo();
		} catch (Exception e) {
			Assert.assertEquals("test", e.getMessage());
		}
	}
	
	public static abstract class ThisTest {
		public abstract Object foo();
		public int bar() {
			return 1;
		}
	}
	
	// implicit tested by IMethod.get/invoke
	@Test
	public void thisTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException, BuilderCompilerException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "methodTest", "ThisTest", ThisTest.class);
			IMethod method = cls.addMethod(PUBLIC | STATIC, "bar");
				try {
					method.This();
					Assert.fail("This <static method>");
				} catch (BuilderAccessException e) {
					
				}
			
		cls = classFactory.createClass(PUBLIC, "methodTest", "ThisTest", ThisTest.class);
			method = cls.addMethod(PUBLIC, Object.class, "foo");
				method.Return(method.This());
			method.End();
		Class<?> c = cls.build();
		
		ThisTest test = (ThisTest)c.newInstance();
		Assert.assertEquals(test, test.foo());
	}
	
	@Test
	public void superTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderAccessException, BuilderSyntaxException, BuilderCompilerException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "methodTest", "SuperTest", ThisTest.class);
			IMethod method = cls.addMethod(PUBLIC | STATIC, "bar");
				try {
					method.Super();
					Assert.fail("This <static method>");
				} catch (BuilderAccessException e) {
					
				}
			
		cls = classFactory.createClass(PUBLIC, "methodTest", "SuperTest", ThisTest.class);
			method = cls.addMethod(PUBLIC, Object.class, "foo");
				method.Return(method.Super());
			method.End();
			method = cls.addMethod(PUBLIC, int.class, "bar");
				method.Return(method.Super().invoke("bar").add(1));
			method.End();
		Class<?> c = cls.build();
		
		ThisTest test = (ThisTest)c.newInstance();
		Assert.assertEquals(test, test.foo());
		Assert.assertEquals(2, test.bar());
	}
	
	public static class InvokeSuperTest {
		public InvokeSuperTest(int i) {}
	}
	
	@Test
	public void invokeSuperTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "methodTest", "InvokeSuperTest1", Object.class);
			IConstructor ctor = cls.addConstructor(PUBLIC);
				Variable i = ctor.addVar(int.class);
				i.set(1);
			ctor.End();
		Class<?> c = cls.build();
		c.newInstance();
		
		cls = classFactory.createClass(PUBLIC, "methodTest", "InvokeSuperTest2", Object.class);
			ctor = cls.addConstructor(PUBLIC);
				ctor.invokeSuper();
				i = ctor.addVar(int.class);
				i.set(1);
			ctor.End();
		c = cls.build();
		c.newInstance();
		
		cls = classFactory.createClass(PUBLIC, "methodTest", "InvokeSuperTest3", InvokeSuperTest.class);
			ctor = cls.addConstructor(PUBLIC);
			try {
				ctor.End();
				Assert.fail("invokeSuper <no super()>");
			} catch (BuilderSyntaxException e) {
				
			}
		
		cls = classFactory.createClass(PUBLIC, "methodTest", "InvokeSuperTest4", InvokeSuperTest.class);
			ctor = cls.addConstructor(PUBLIC);
				ctor.invokeSuper(1);
				i = ctor.addVar(int.class);
				i.set(1);
			ctor.End();
		c = cls.build();
		c.newInstance();
	}
}
