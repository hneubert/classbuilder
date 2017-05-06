package classbuilder.test.builder;

import static classbuilder.IClass.PUBLIC;

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
import classbuilder.IMethod;
import classbuilder.Variable;

public class BasicTestCase {
	private ClassFactory classFactory = new ClassFactory();
	
	public interface Foo {
		public void foo();
	}
	
	public interface Bar extends Foo {
		
	}
	
	public static class FooBar implements Bar {
		@Override
		public void foo() {
			System.out.println("foo");
		}
	}
	
	public interface SimpleInterface {
		public void foo();
	}
	
	public interface ITest {
		public Integer test(Integer i);
	}
	
	@Test
	public void test() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				m.New(FooBar.class).cast(Bar.class).invoke("foo");
			m.End();
			
			IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
			annotation.setValue("value", "blubber");
			annotation.setValue("num", 5);
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		test.foo();
	}
	
	@Test
	public void annotationTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				m.$(System.class).get("out").invoke("println", "hallo");
			m.End();
			
			IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
			annotation.setValue("value", "blubber");
			annotation.setValue("num", 5);
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		test.foo();
		System.out.println(test.getClass().getAnnotation(TestAnnotation.class).num());
	}
	
	@Test
	public void simpleTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				m.$(System.class).get("out").invoke("println", "hallo");
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		test.foo();
	}
	
	@Test
	public void varTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				Variable i = m.addVar(long.class);
				Variable j = m.addVar(int.class);
				
				i.set(2);
				j.set(1);
				
				i.set(i.add(j));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		test.foo();
	}
	
	@Test
	public void ifElseTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				m.If(m.$(true));
					m.$(System.class).get("out").invoke("println", "ok");
				m.ElseIf(m.$(true));
					m.$(System.class).get("out").invoke("println", "error");
				m.Else();
					m.$(System.class).get("out").invoke("println", "error");
				m.End();
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		test.foo();
	}
	
	@Test
	public void whileTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				Variable i = m.addVar(int.class);
				
				i.set(0);
				m.While(i.notEqual(3));
					m.$(System.class).get("out").invoke("println", "hallo");
					i.set(i.add(1));
				m.End();
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		test.foo();
	}
	
	@Test
	public void forEachTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				Variable array = m.addVar(String[].class);
				
				array.set(m.New(String[].class, 2));
				array.get(0).set("1");
				array.get(1).set("2");
				
				Variable e = m.ForEach(array);
					m.$(System.class).get("out").invoke("println", e);
				m.End();
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		test.foo();
	}
	
	@Test
	public void tryCatchTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				m.Try();
					m.Throw(m.New(Exception.class, "Error"));
					Variable e = m.Catch(Exception.class);
					m.$(System.class).get("out").invoke("println", e.invoke("getMessage"));
				m.End();
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		test.foo();
	}
	
	@Test
	public void arrayTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				Variable array = m.addVar(String[].class);
				
				array.set(m.New(String[].class, 5));
				
				array.get(2).set("42");
				
				m.$(System.class).get("out").invoke("println", array.get(2));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		test.foo();
	}
	
	@Test
	public void arrayByteTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				Variable array = m.addVar(byte[].class);
				
				array.set(m.New(byte[].class, 5));
				
				array.get(2).set(m.$(3).cast(byte.class));
				
				m.$(System.class).get("out").invoke("println", array.get(2));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		test.foo();
	}
	
	@Test
	public void arrayMultiTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, "foo");
				Variable array = m.addVar(String[][].class);
				
				array.set(m.New(String[][].class, 5));
				
				array.get(2).set(m.New(String[].class, 5));
				array.get(2).get(2).set("42");
				
				m.$(System.class).get("out").invoke("println", array.get(2).get(2));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		test.foo();
	}
	
	@Test
	public void interfaceTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderCompilerException {
		IClass cls = classFactory.createClass(IClass.INTERFACE, "basicTest", "InterfaceTest", null);
			cls.addMethod(PUBLIC, int.class, "foo", int.class);
		cls.build();
	}
	
	@Test
	public void enumTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderCompilerException, InstantiationException, IllegalAccessException, BuilderSyntaxException, BuilderAccessException {
		IClass cls = classFactory.createClass(PUBLIC | IClass.ENUM, "basicTest", "EnumTest", null);
			cls.addEnumConstant("A");
			cls.addEnumConstant("B");
		Class<?> c = cls.build();
		for (Object o : c.getEnumConstants()) {
			Enum<?> e = (Enum<?>)o;
			System.out.println(e.ordinal() + " " + e.name());
		}
	}
	
	private static int counter = 0;
	private IClass addClass(Class<?> ...intf) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		counter++;
		return classFactory.createClass(PUBLIC, "generated", "BasicTest" + counter, Object.class, intf);
	}
	
	private Object getInstance(Class<?> cls) throws InstantiationException, IllegalAccessException {
		try {
			return cls.getConstructor().newInstance();
		} catch (Exception e) {
			throw new InstantiationException(e.getMessage());
		}
	}
}
