package classbuilder.test.builder;

import static classbuilder.IClass.*;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import classbuilder.BuilderAccessException;
import classbuilder.BuilderCompilerException;
import classbuilder.BuilderException;
import classbuilder.BuilderModifierException;
import classbuilder.BuilderNameException;
import classbuilder.BuilderSyntaxException;
import classbuilder.BuilderTypeException;
import classbuilder.ClassFactory;
import classbuilder.IClass;
import classbuilder.IField;
import classbuilder.IMethod;
import classbuilder.Variable;

public class ValueTestCase {
	private ClassFactory classFactory = new ClassFactory();
	
	public static int staticField = 3;
	public int field = 3;
	
	public interface SimpleInterface {
		public int foo();
	}
	
	@Test
	public void int_const() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(3);
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	//@Ignore // int -> short nicht implizit möglich
	public void int_short() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				Variable j = m.addVar(short.class);
				j.set(m.$(3).cast(short.class));
				i.set(j);
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void int_int() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				Variable j = m.addVar(int.class);
				j.set(3);
				i.set(j);
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	//@Ignore // long -> int nicht implizit möglich
	public void int_long() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				Variable j = m.addVar(long.class);
				j.set(3);
				i.set(j.cast(int.class));
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void int_Integer() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				Variable j = m.addVar(Integer.class);
				j.set(m.New(Integer.class, 3));
				i.set(j);
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void integer_const() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(Integer.class);
				i.set(m.New(Integer.class, 0));
				i.set(3);
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	//@Ignore // ok
	public void integer_short() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(Integer.class);
				Variable j = m.addVar(short.class);
				i.set(m.New(Integer.class, 0));
				j.set(m.$(3).cast(short.class));
				i.set(j.cast(int.class));
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void integer_int() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(Integer.class);
				Variable j = m.addVar(int.class);
				i.set(m.New(Integer.class, 0));
				j.set(3);
				i.set(j);
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	//@Ignore // ok
	public void integer_long() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(Integer.class);
				Variable j = m.addVar(long.class);
				i.set(m.New(Integer.class, 0));
				j.set(3);
				i.set(j.cast(int.class));
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void integer_Integer() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(Integer.class);
				Variable j = m.addVar(Integer.class);
				i.set(m.New(Integer.class, 0));
				j.set(m.New(Integer.class, 3));
				i.set(j);
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void newPrimitiveArray() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable var = m.addVar(int[].class);
				var.set(m.New(int[].class, 5));
				m.Return(3);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void newObject() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable var = m.addVar(String.class);
				var.set(m.New(String.class));
				m.Return(3);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void newObjectConstructor() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable var = m.addVar(Integer.class);
				var.set(m.New(Integer.class, 1));
				m.Return(3);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void newObjectArray() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable var = m.addVar(String[].class);
				var.set(m.New(String[].class, 3));
				m.Return(3);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void invokeStatic() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(Math.class).invoke("abs", 3));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void invokeConstructor() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.New(Integer.class, 3).invoke("intValue"));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void invokePrivate() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PRIVATE, int.class, "test");
				m.Return(3);
			m.End();
			
			IMethod n = cls.addMethod(PUBLIC, int.class, "foo");
				n.Return(n.This().invoke("test"));
			n.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void invokePublic() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.New(Integer.class, 3).invoke("intValue"));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void invokeThis() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "test");
				m.Return(3);
			m.End();
			
			IMethod n = cls.addMethod(PUBLIC, int.class, "foo");
				n.Return(n.This().invoke("test"));
			n.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void invokeNull() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.$(System.class).get("out").invoke("println", (Object)null);
				m.Return(3);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void invokeSuper() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Super().invoke("toString");
				m.Return(3);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}

	@Test
	public void setVar() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(3);
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void setStatic() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.$(ValueTestCase.class).get("staticField").set(3);
				m.Return(m.$(ValueTestCase.class).get("staticField"));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	public static class FieldTest {
		public int field;
	}
	
	@Test
	public void setProperty() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable field = m.addVar(FieldTest.class);
				field.set(m.New(FieldTest.class));
				
				field.get("field").set(3);
				m.Return(field.get("field"));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void setArray() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable array = m.addVar(int[].class);
				array.set(m.New(int[].class, 3));
				array.get(1).set(3);
				m.Return(array.get(1));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void setThis() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IField field = cls.addField(PUBLIC, int.class, "bar");
			
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.get(field).set(3);
				m.Return(m.get(field));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Ignore
	public void setSuper() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}

	@Test
	public void getVar() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(3);
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void getStatic() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.$(ValueTestCase.class).get("staticField").set(3);
				m.Return(m.$(ValueTestCase.class).get("staticField"));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void getProperty() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable field = m.addVar(FieldTest.class);
				field.set(m.New(FieldTest.class));
				
				field.get("field").set(3);
				m.Return(field.get("field"));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void getThis() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IField field = cls.addField(PUBLIC, int.class, "bar");
			
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.get(field).set(3);
				m.Return(m.get(field));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Ignore
	public void getSuper() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void stackCleanup() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.New(Integer.class, 3).invoke("intValue"));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void invokeCast() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.New(Integer.class, 3).cast(Integer.class).invoke("intValue"));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void getArrayTest() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException, BuilderModifierException, BuilderNameException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				try {
					i.get(1);
					Assert.fail("length primitive");
				} catch (BuilderTypeException e) {}
		
		cls = addClass(SimpleInterface.class);
			m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable array = m.addVar(int[].class);
				array.set(m.New(int[].class, 3));
				array.get(1).set(42);
				m.Return(array.get(1));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void lengthTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				try {
					i.length();
					Assert.fail("length primitive");
				} catch (BuilderTypeException e) {}
		
		cls = addClass(SimpleInterface.class);
			m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable array = m.addVar(int[].class);
				array.set(m.New(int[].class, 3));
				m.Return(array.length());
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void getVarTypeTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
		Assert.assertEquals(int.class, m.$(5).getVarType());
	}
	
	private static int counter = 0;
	private IClass addClass(Class<?> ...intf) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		counter++;
		return classFactory.createClass(PUBLIC, "generated", "VarTest" + counter, Object.class, intf);
	}
	
	private Object getInstance(Class<?> cls) throws InstantiationException, IllegalAccessException {
		try {
			return cls.getConstructor().newInstance();
		} catch (Exception e) {
			throw new InstantiationException(e.getMessage());
		}
	}
}
