package classbuilder.test.builder;

import static classbuilder.IClass.*;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import classbuilder.BuilderAccessException;
import classbuilder.BuilderCompilerException;
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

public class IClassTestCase {
	private ClassFactory classFactory = new ClassFactory();
	
	@Test
	public void build_test() throws BuilderCompilerException, BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		Class<?> c = cls.build();
		if (c == null) {
			fail("class is null");
		}
	}
	
	@Ignore
	public void write_test() throws BuilderCompilerException, IOException, BuilderSyntaxException, BuilderAccessException, BuilderTypeException, BuilderModifierException, BuilderNameException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "WriteSourceTest", Object.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				m.$(System.class).get("out").invoke("println", "hallo");
			m.End();
		cls.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		cls.writeSource(out);
//		System.out.print(new String(out.toByteArray()));
	}
	
	@Test
	public void getName_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		Assert.assertEquals("package.Class", cls.getName());
	}
	
	@Test
	public void getSimpleName() throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		Assert.assertEquals("Class", cls.getSimpleName());
	}
	
	@Test
	public void getModifiers_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		Assert.assertEquals(SUPER | PUBLIC, cls.getModifiers());
	}
	
	@Test
	public void getField_test() throws NoSuchFieldException, BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		cls.addField(PUBLIC, int.class, "field");
		IField field = cls.getField("field");
		Assert.assertEquals(PUBLIC, field.getModifiers());
		Assert.assertEquals(int.class, field.getType());
		Assert.assertEquals("field", field.getName());
		Assert.assertEquals(cls, field.getDeclaringClass());
	}
	
	@Test
	public void getFields() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		cls.addField(PUBLIC, int.class, "field");
		Collection<IField> fields = cls.getFields();
		Assert.assertEquals(1, fields.size());
		for (IField field : fields) {
			Assert.assertEquals(PUBLIC, field.getModifiers());
			Assert.assertEquals(int.class, field.getType());
			Assert.assertEquals("field", field.getName());
			Assert.assertEquals(cls, field.getDeclaringClass());
		}
	}
	
	@Test
	public void getMethod_test() throws NoSuchMethodException, BuilderModifierException, BuilderNameException, BuilderTypeException, NoSuchFieldException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		cls.addMethod(PUBLIC, int.class, "method", int.class);
		IMethod method = cls.getMethod("method", new Class<?>[] {int.class});
		Assert.assertEquals(PUBLIC, method.getModifiers());
		Assert.assertEquals(int.class, method.getReturnType());
		Assert.assertEquals("method", method.getName());
		Assert.assertEquals(cls, method.getDeclaringClass());
	}
	
	@Test
	public void getMethods_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		cls.addMethod(PUBLIC, int.class, "field", int.class);
		Collection<IMethod> methods = cls.getMethods();
		Assert.assertEquals(1, methods.size());
		for (IMethod method : methods) {
			Assert.assertEquals(PUBLIC, method.getModifiers());
			Assert.assertEquals(int.class, method.getReturnType());
			Assert.assertEquals("field", method.getName());
			Assert.assertEquals(cls, method.getDeclaringClass());
		}
	}
	
	@Test
	public void getConstructor_test() throws NoSuchMethodException, BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		cls.addConstructor(PUBLIC, int.class);
		IConstructor method = cls.getConstructor(new Class<?>[] {int.class});
		Assert.assertEquals(PUBLIC, method.getModifiers());
		Assert.assertEquals(cls, method.getDeclaringClass());
	}
	
	@Test
	public void getConstructors_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		cls.addConstructor(PUBLIC, int.class);
		Collection<IConstructor> methods = cls.getConstructors();
		Assert.assertEquals(1, methods.size());
		for (IConstructor method : methods) {
			Assert.assertEquals(PUBLIC, method.getModifiers());
			Assert.assertEquals(cls, method.getDeclaringClass());
		}
	}
	
	@Test
	public void writeSource_test() throws IOException, BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderAccessException, BuilderCompilerException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "WriteSourceTest", Object.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				m.$(System.class).get("out").invoke("println", "hallo");
			m.End();
		cls.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		cls.write(out);
//		System.out.print(new String(out.toByteArray()));
	}
	
	@Test
	public void getSuperclass_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		Assert.assertEquals(Object.class, cls.getSuperclass());
	}
	
	@Test
	public void getPackage_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		Assert.assertEquals("package", cls.getPackage());
	}
	
	@Test
	public void staticTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderCompilerException, BuilderSyntaxException, BuilderAccessException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "StaticTest", Object.class);
			IField f = cls.addField(PUBLIC | STATIC, int.class, "test");
			IMethod s = cls.Static();
				s.get(f).set(1);
			s.End();
		Class<?> c = cls.build();
		getInstance(c);
		
		Field field = c.getField("test");
		Assert.assertEquals(1, field.get(null));
	}
	
	@Test
	public void addAnnotationTest() throws BuilderTypeException, BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderAccessException, BuilderCompilerException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		try {
			cls.addAnnotation(null);
			fail("<null>");
		} catch (BuilderTypeException e) {
			
		}
		cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		try {
			cls.addAnnotation(Object.class);
			fail("Object");
		} catch (BuilderTypeException e) {
			
		}
		cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		try {
			cls.addAnnotation(Override.class);
			fail("Override");
		} catch (BuilderTypeException e) {
			
		}
		
		cls = classFactory.createClass(PUBLIC, "test.iclass", "AddAnnotationTest", Object.class);
		IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
		annotation.setValue("value", "test");
		Class<?> c = cls.build();
		TestAnnotation a = c.getAnnotation(TestAnnotation.class);
		Assert.assertEquals("test", a.value());
	}
	
	@Test
	public void addConstructor_BuilderModifierException() throws BuilderSyntaxException, BuilderNameException, BuilderTypeException, BuilderModifierException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		for (int i = 0x40000000; i > 1; i >>= 1) {
			if (i == PUBLIC) continue;
			if (i == PROTECTED) continue;
			if (i == PRIVATE) continue;
			try {
				cls.addConstructor(PUBLIC | PRIVATE);
				fail(Integer.toHexString(i));
			} catch (BuilderModifierException e) {
				
			}
		}
		try {
			cls.addConstructor(PUBLIC | PROTECTED);
			fail("PUBLIC | PROTECTED");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addConstructor(PUBLIC | PRIVATE);
			fail("PUBLIC | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addConstructor(PROTECTED | PRIVATE);
			fail("PROTECTED | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
	}
	
	@Test
	public void addConstructor_BuilderNameException() throws BuilderSyntaxException, BuilderModifierException, BuilderTypeException, BuilderNameException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		try {
			cls.addConstructor(PUBLIC, int.class);
		} catch (BuilderNameException e) {
			fail("<init>");
		}
		try {
			cls.addConstructor(PUBLIC, int.class);
			fail("<init> 2");
		} catch (BuilderNameException e) {
			
		}
	}
	
	public static class ConstructorTest {
		public ConstructorTest(int i) {
			
		}
	}
	
	@Test
	public void addConstructorTestCase() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException, BuilderCompilerException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		try {
			IClass cls = classFactory.createClass(PUBLIC, "package", "Class", ConstructorTest.class);
				IConstructor ctor = cls.addConstructor(PUBLIC);
				ctor.End();
			getInstance(cls.build());
			Assert.fail();
		} catch (BuilderSyntaxException e) {
			
		}
		
		IClass cls = classFactory.createClass(PUBLIC, "package", "AddConstructorTestCase1", ConstructorTest.class);
			IConstructor ctor = cls.addConstructor(PUBLIC, int.class);
				ctor.invokeSuper(ctor.getParameter(0));
			ctor.End();
			
			try {
				cls.addConstructor(PUBLIC, int.class);
				Assert.fail("duplicate name");
			} catch (BuilderNameException e) {
				
			}
		cls.build().getConstructor(int.class).newInstance(1);
		
		cls = classFactory.createClass(PUBLIC, "package", "AddConstructorTestCase2", Object.class);
			ctor = cls.addConstructor(PUBLIC);
			ctor.End();
		getInstance(cls.build());
	}
	
	@Test
	public void addField_1_BuilderModifierException() throws BuilderSyntaxException, BuilderNameException, BuilderTypeException, BuilderModifierException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		for (int i = 0x40000000; i > 1; i >>= 1) {
			try {
				if (i == PUBLIC) continue;
				if (i == PROTECTED) continue;
				if (i == PRIVATE) continue;
				if (i == TRANSIENT) continue;
				if (i == STATIC) continue;
				cls.addField(i, Object.class, "foo");
				fail(Integer.toHexString(i));
			} catch (BuilderModifierException e) {
				
			}
		}
		try {
			cls.addField(PUBLIC | PROTECTED, int.class, "foo");
			fail("PUBLIC | PROTECTED");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addField(PUBLIC | PRIVATE, int.class, "foo");
			fail("PUBLIC | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addField(PROTECTED | PRIVATE, int.class, "foo");
			fail("PROTECTED | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addField(PROTECTED | PRIVATE, int.class, "foo");
			fail("STATIC | ABSTRACT");
		} catch (BuilderModifierException e) {
			
		}
	}
	
	@Test
	public void addField_1_BuilderNameException() throws BuilderSyntaxException, BuilderModifierException, BuilderTypeException, BuilderNameException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		try {
			cls.addField(PUBLIC, Object.class, null);
			fail("<null>");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addField(PUBLIC, Object.class, "");
			fail("<empty>");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addField(PUBLIC, Object.class, "*");
			fail("*");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addField(PUBLIC, Object.class, "1");
			fail("1");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addField(PUBLIC, Object.class, "a.b");
			fail("a.b");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addField(PUBLIC, Object.class, "foo");
		} catch (BuilderNameException e) {
			fail("foo");
		}
		try {
			cls.addField(PUBLIC, Object.class, "foo");
			fail("foo");
		} catch (BuilderNameException e) {
			
		}
	}
	
	@Test
	public void addField_1_BuilderTypeException() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		try {
			cls.addField(PUBLIC, (Class<?>)null, "field");
			fail("<null>");
		} catch (BuilderTypeException e) {
			
		}
		try {
			cls.addField(PUBLIC, void.class, "field");
			fail("void");
		} catch (BuilderTypeException e) {
			
		}
		try {
			cls.addField(PUBLIC, Void.class, "field");
			fail("Void");
		} catch (BuilderTypeException e) {
			
		}
	}
	
	@Test
	public void addField_1_Test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderCompilerException, NoSuchFieldException, SecurityException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "AddField_1_Test", Object.class);
			cls.addField(PUBLIC, int.class, "test");
			
			try {
				cls.addField(PUBLIC, int.class, "test");
				Assert.fail("duplicate name");
			} catch (BuilderNameException e) {
				
			}
		Class<?> c = cls.build();
		
		Field f = c.getField("test");
		Assert.assertEquals(int.class, f.getType());
	}
	
	@Test
	public void addField_2_BuilderModifierException() throws BuilderSyntaxException, BuilderNameException, BuilderTypeException, BuilderModifierException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		for (int i = 0x40000000; i > 1; i >>= 1) {
			try {
				if (i == PUBLIC) continue;
				if (i == PROTECTED) continue;
				if (i == PRIVATE) continue;
				if (i == TRANSIENT) continue;
				if (i == STATIC) continue;
				if (i == FINAL) continue;
				cls.addField(i, "foo", 1);
				fail(Integer.toHexString(i));
			} catch (BuilderModifierException e) {
				
			}
		}
		try {
			cls.addField(PUBLIC | PROTECTED, "foo", 1);
			fail("PUBLIC | PROTECTED");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addField(PUBLIC | PRIVATE, "foo", 1);
			fail("PUBLIC | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addField(PROTECTED | PRIVATE, "foo", 1);
			fail("PROTECTED | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addField(PROTECTED | PRIVATE, int.class, "foo");
			fail("STATIC | ABSTRACT");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addField(PROTECTED | PRIVATE, int.class, "foo");
			fail("FINAL | ABSTRACT");
		} catch (BuilderModifierException e) {
			
		}
	}
	
	@Test
	public void addField_2_BuilderNameException() throws BuilderSyntaxException, BuilderModifierException, BuilderTypeException, BuilderNameException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		try {
			cls.addField(PUBLIC, null, 1);
			fail("<null>");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addField(PUBLIC, "", 1);
			fail("<empty>");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addField(PUBLIC, "*", 1);
			fail("*");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addField(PUBLIC, "1", 1);
			fail("1");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addField(PUBLIC, "a.b", 1);
			fail("a.b");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addField(PUBLIC, "foo", 1);
		} catch (BuilderNameException e) {
			fail("foo");
		}
		try {
			cls.addField(PUBLIC, "foo", 1);
			fail("foo");
		} catch (BuilderNameException e) {
			
		}
	}
	
	@Test
	public void addField_2_BuilderTypeException() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		try {
			cls.addField(PUBLIC, "field", null);
			fail("<null>");
		} catch (BuilderTypeException e) {
			
		}
		try {
			cls.addField(PUBLIC, "field", new Object());
			fail("Object");
		} catch (BuilderTypeException e) {
			
		}
	}
	
	@Test
	public void addField_2_Test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderCompilerException, NoSuchFieldException, SecurityException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "AddField_2_Test", Object.class);
			cls.addField(PUBLIC | FINAL, "test", 1);
			
			try {
				cls.addField(PUBLIC | FINAL, "test", 1);
				Assert.fail("duplicate name");
			} catch (BuilderNameException e) {
				
			}
		Class<?> c = cls.build();
		
		Field f = c.getField("test");
		Assert.assertEquals(int.class, f.getType());
	}
	
	@Test
	public void addMethod_1_BuilderModifierException() throws BuilderSyntaxException, BuilderNameException, BuilderTypeException, BuilderModifierException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		for (int i = 0x40000000; i > 1; i >>= 1) {
			if (i == PUBLIC) continue;
			if (i == PROTECTED) continue;
			if (i == PRIVATE) continue;
			if (i == SYNCHRONIZED) continue;
			if (i == STRICT) continue;
			if (i == FINAL) continue;
			if (i == STATIC) continue;
			if (i == NATIVE) continue;
			if (i == ABSTRACT) continue;
			try {
				cls.addMethod(i, "foo");
				fail(Integer.toHexString(i));
			} catch (BuilderModifierException e) {
				
			}
		}
		try {
			cls.addMethod(PUBLIC | PROTECTED, "foo");
			fail("PUBLIC | PROTECTED");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addMethod(PUBLIC | PRIVATE, "foo");
			fail("PUBLIC | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addMethod(PROTECTED | PRIVATE, "foo");
			fail("PROTECTED | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addMethod(ABSTRACT | STATIC, "foo");
			fail("PROTECTED | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addMethod(ABSTRACT | FINAL, "foo");
			fail("PROTECTED | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
	}
	
	@Test
	public void addMethod_2_BuilderModifierException() throws BuilderSyntaxException, BuilderNameException, BuilderTypeException, BuilderModifierException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		for (int i = 0x40000000; i > 1; i >>= 1) {
			try {
				if (i == PUBLIC) continue;
				if (i == PROTECTED) continue;
				if (i == PRIVATE) continue;
				if (i == SYNCHRONIZED) continue;
				if (i == STRICT) continue;
				if (i == FINAL) continue;
				if (i == STATIC) continue;
				if (i == NATIVE) continue;
				if (i == ABSTRACT) continue;
				cls.addMethod(i, int.class, "foo");
				fail(Integer.toHexString(i));
			} catch (BuilderModifierException e) {
				
			}
		}
		try {
			cls.addMethod(PUBLIC | PROTECTED, int.class, "foo");
			fail("PUBLIC | PROTECTED");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addMethod(PUBLIC | PRIVATE, int.class, "foo");
			fail("PUBLIC | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addMethod(PROTECTED | PRIVATE, int.class, "foo");
			fail("PROTECTED | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addMethod(ABSTRACT | STATIC, int.class, "foo");
			fail("PROTECTED | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
		try {
			cls.addMethod(ABSTRACT | FINAL, int.class, "foo");
			fail("PROTECTED | PRIVATE");
		} catch (BuilderModifierException e) {
			
		}
	}
	
	@Test
	public void addMethod_1_BuilderNameException() throws BuilderSyntaxException, BuilderModifierException, BuilderTypeException, BuilderNameException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		try {
			cls.addMethod(PUBLIC, null);
			fail("<null>");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, "");
			fail("<empty>");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, "*");
			fail("*");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, "1");
			fail("1");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, "a.b");
			fail("a.b");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, "foo");
		} catch (BuilderNameException e) {
			fail("foo");
		}
		try {
			cls.addMethod(PUBLIC, "foo");
			fail("foo 2");
		} catch (BuilderNameException e) {
			
		}
	}
	
	@Test
	public void addMethod_2_BuilderNameException() throws BuilderSyntaxException, BuilderModifierException, BuilderTypeException, BuilderNameException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		try {
			cls.addMethod(PUBLIC, int.class, null);
			fail("<null>");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, int.class, "");
			fail("<empty>");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, int.class, "*");
			fail("*");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, int.class, "1");
			fail("1");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, int.class, "a.b");
			fail("a.b");
		} catch (BuilderNameException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, int.class, "foo");
		} catch (BuilderNameException e) {
			fail("foo");
		}
		try {
			cls.addMethod(PUBLIC, int.class, "foo");
			fail("foo 2");
		} catch (BuilderNameException e) {
			
		}
	}
	
	@Test
	public void addMethod_1_BuilderTypeException() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		try {
			cls.addMethod(PUBLIC, "method", (Class<?>)null);
			fail("<null>");
		} catch (BuilderTypeException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, "method", void.class);
			fail("void");
		} catch (BuilderTypeException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, "method", Void.class);
			fail("Void");
		} catch (BuilderTypeException e) {
			
		}
	}
	
	@Test
	public void addMethod_2_BuilderTypeException() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		try {
			cls.addMethod(PUBLIC, int.class, "method", (Class<?>)null);
			fail("<null>");
		} catch (BuilderTypeException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, int.class, "method", void.class);
			fail("void");
		} catch (BuilderTypeException e) {
			
		}
		try {
			cls.addMethod(PUBLIC, int.class, "method", Void.class);
			fail("Void");
		} catch (BuilderTypeException e) {
			
		}
	}
	
	@Test
	public void addMethod_1_Test() throws BuilderSyntaxException, BuilderModifierException, BuilderTypeException, BuilderNameException, BuilderAccessException, NoSuchMethodException, SecurityException, BuilderCompilerException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "AddMethod_1_Test", Object.class);
		
		try {
			IMethod m = cls.addMethod(PUBLIC | ABSTRACT, "test", int.class);
			m.$(System.class).get("out").invoke("println", "hello");
			fail("abstract");
		} catch (BuilderSyntaxException e) {
			
		}
		
		IMethod m = cls.addMethod(PUBLIC, "test2", int.class);
			m.$(System.class).get("out").invoke("println", "hello");
		m.End();
		try {
			cls.addMethod(PUBLIC, "test2", int.class);
			Assert.fail("duplicate name");
		} catch (BuilderNameException e) {
			
		}
		Class<?> c = cls.build();
		Method method = c.getMethod("test2", int.class);
		Assert.assertEquals(int.class, method.getParameterTypes()[0]);
	}
	
	@Test
	public void addMethod_2_Test() throws BuilderSyntaxException, BuilderModifierException, BuilderTypeException, BuilderNameException, BuilderAccessException, NoSuchMethodException, SecurityException, BuilderCompilerException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "AddMethod_2_Test", Object.class);
		
		try {
			IMethod m = cls.addMethod(PUBLIC | ABSTRACT, int.class, "test", int.class);
			m.$(System.class).get("out").invoke("println", "hello");
			fail("abstract");
		} catch (BuilderSyntaxException e) {
			
		}
		
		IMethod m = cls.addMethod(PUBLIC, int.class, "test2", int.class);
			m.Return(m.getParameter(0));
		m.End();
		try {
			cls.addMethod(PUBLIC, int.class, "test2", int.class);
			Assert.fail("duplicate name");
		} catch (BuilderNameException e) {
			
		}
		Class<?> c = cls.build();
		Method method = c.getMethod("test2", int.class);
		Assert.assertEquals(int.class, method.getParameterTypes()[0]);
		Assert.assertEquals(int.class, method.getReturnType());
		}
	
	@Test
	public void getAnnotations_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		cls.addAnnotation(TestAnnotation.class);
		Collection<IAnnotation> annotations = cls.getAnnotations();
		Assert.assertEquals(1, annotations.size());
		for (IAnnotation annotation : annotations) {
			Assert.assertEquals(TestAnnotation.class, annotation.getType());
		}
	}
	
	@Test
	public void addInterfaceTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "AddInterfaceTest", Object.class);
		try {
			cls.addInterface(null);
			Assert.fail("<null>");
		} catch (BuilderTypeException e) {
			
		}
		try {
			cls.addInterface(int.class);
			Assert.fail("primitive type");
		} catch (BuilderTypeException e) {
			
		}
		try {
			cls.addInterface(Integer.class);
			Assert.fail("class type");
		} catch (BuilderTypeException e) {
			
		}
		cls.addInterface(Serializable.class);
		Class<?> c = cls.build();
		Assert.assertTrue(getInstance(c) instanceof Serializable);
	}
	
	@Test
	public void getInterfaces_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class, Serializable.class);
		Assert.assertEquals(1, cls.getInterfaces().size());
		for (Class<?> intf : cls.getInterfaces()) {
			Assert.assertEquals(Serializable.class, intf);
		}
	}
	
	public void getClassFactoryTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		IClass cls = classFactory.createClass(PUBLIC, "package", "Class", Object.class, Serializable.class);
		Assert.assertEquals(classFactory, cls.getClassFactory());
	}
	
	public interface IEnum {
		public String getValue();
	}
	
	@Test
	public void addEnumFieldTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderCompilerException, BuilderAccessException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
//		classFactory.setClassPath("gen/bin");
		IClass cls = classFactory.createClass(PUBLIC | IClass.ENUM, "package", "AddEnumFieldTest", Enum.class, IEnum.class);
			IField field = cls.addField(PRIVATE, String.class, "value");
			IConstructor ctor = cls.addConstructor(PUBLIC, String.class);
				ctor.get(field).set(ctor.getParameter(0));
			ctor.End();
			
			IMethod m = cls.addMethod(PUBLIC, String.class, "getValue");
				m.Return(m.get(field));
			m.End();
			
			try {
				cls.addEnumConstant(null);
				Assert.fail("<null>");
			} catch (BuilderNameException e) {
				
			}
			try {
				cls.addEnumConstant("*");
				Assert.fail("imvalid character");
			} catch (BuilderNameException e) {
				
			}
			try {
				cls.addEnumConstant("A", 1);
				Assert.fail("imvalid type");
			} catch (BuilderAccessException e) {
				
			}
			cls.addEnumConstant("A");
			cls.addEnumConstant("B");
			cls.addEnumConstant("C", "c");
			cls.addEnumConstant("D", "d");
		Class<?> c = cls.build();
		Object[] fields = c.getEnumConstants();
		Enum<?> e = (Enum<?>)fields[0];
		Assert.assertEquals(0, e.ordinal());
		Assert.assertEquals("A", e.name());
		e = (Enum<?>)fields[1];
		Assert.assertEquals(1, e.ordinal());
		Assert.assertEquals("B", e.name());
		
		e = (Enum<?>)fields[2];
		Assert.assertEquals(2, e.ordinal());
		Assert.assertEquals("C", e.name());
		Assert.assertEquals("c", ((IEnum)e).getValue());
		
		e = (Enum<?>)fields[3];
		Assert.assertEquals(3, e.ordinal());
		Assert.assertEquals("D", e.name());
		Assert.assertEquals("d", ((IEnum)e).getValue());
	}
	
	public interface ICurrentClassTest {
		public Object getInstance();
	}
	
	@Test
	public void currentClassTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderCompilerException, BuilderSyntaxException, BuilderAccessException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
//		classFactory.setClassPath("gen/bin");
		IClass cls = classFactory.createClass(PUBLIC, "package", "CurrentClassTest", Object.class, ICurrentClassTest.class);
			IField f = cls.addField(PUBLIC, int.class, "num");
			f = cls.addField(PRIVATE | STATIC, IClass.CURRENT_CLASS_TYPE, "instance");
			IMethod m = cls.addConstructor(PUBLIC);
				
			m.End();
			m = cls.addMethod(PUBLIC, "init");
				m.Return();
			m.End();
			m = cls.addMethod(PUBLIC | STATIC, IClass.CURRENT_CLASS_TYPE, "getInstance");
				m.If(m.get(f).isNull());
					Variable v = m.addVar(IClass.CURRENT_CLASS_TYPE);
					v.set(m.New(IClass.CURRENT_CLASS_TYPE));
//					Variable v = m.addVar(ICurrentClassTest.class);
//					v.set(m.New(IClass.CURRENT_CLASS_TYPE));
					v.invoke("init");
					v.get("num").set(1);
					m.get(f).set(v.cast(IClass.CURRENT_CLASS_TYPE));
				m.End();
				m.Return(m.get(f));
			m.End();
		Object obj = cls.build().getMethod("getInstance").invoke(null);
		System.out.println(obj.getClass());
	}
	
	private Object getInstance(Class<?> cls) throws InstantiationException, IllegalAccessException {
		try {
			return cls.getConstructor().newInstance();
		} catch (Exception e) {
			throw new InstantiationException(e.getMessage());
		}
	}
}
