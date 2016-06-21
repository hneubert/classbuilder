package classbuilder.demo;

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
import classbuilder.IClass;
import classbuilder.IMethod;
import classbuilder.Variable;
import classbuilder.util.MethodDefinition;

/**
 * simple examples
 */
public class BasicDemo {
	private ClassFactory classFactory = new ClassFactory();
	
	/**
	 * an interface
	 */
	public interface SimpleInterface {
		public void foo();
	}
	
	/**
	 * create a class with a method, which prints a sys-out
	 */
	@Test
	public void simpleTest() throws BuilderException, InstantiationException, IllegalAccessException {
		// public class generated.SimpleTest implements SimpleInterface {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "SimpleTest", Object.class, SimpleInterface.class);
			// public void foo() {
			IMethod m = cls.addMethod(PUBLIC, "foo");
				// System.out.println("hallo");
				m.$(System.class).get("out").invoke("println", "hello world");
			// }
			m.End();
		// }
		Class<?> newClass = cls.build();
		
		SimpleInterface instance = (SimpleInterface)newClass.newInstance();
		instance.foo();
	}
	
	/**
	 * usage of variables
	 */
	@Test
	public void varTest() throws BuilderException, InstantiationException, IllegalAccessException {
		// public class generated.VarTest implements SimpleInterface {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "VarTest", Object.class, SimpleInterface.class);
			// public void foo() {
			IMethod m = cls.addMethod(PUBLIC, "foo");
				// long i;
				Variable i = m.addVar(long.class);
				// int j;
				Variable j = m.addVar(int.class);
				
				// i = 2;
				i.set(2);
				// j = 1;
				j.set(1);
				
				// i = i + j;
				i.set(i.add(j));
			// }
			m.End();
		// }
		Class<?> newClass = cls.build();
		
		SimpleInterface instance = (SimpleInterface)newClass.newInstance();
		instance.foo();
	}
	
	/**
	 * usage of if-else
	 */
	@Test
	public void ifElseTest() throws BuilderException, InstantiationException, IllegalAccessException {
		// public class generated.IfElseTest implements SimpleInterface {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "IfElseTest", Object.class, SimpleInterface.class);
			// public void foo() {
			IMethod m = cls.addMethod(PUBLIC, "foo");
				// if (true) {
				m.If(m.$(true));
					// System.out.println("ok");
					m.$(System.class).get("out").invoke("println", "ok");
				// } else if (true) {
				m.ElseIf(m.$(true));
					// System.out.println("error");
					m.$(System.class).get("out").invoke("println", "error");
				// } else {
				m.Else();
					// System.out.println(""error);
					m.$(System.class).get("out").invoke("println", "error");
				// }
				m.End();
			// }
			m.End();
		// }
		Class<?> newClass = cls.build();
		
		SimpleInterface instance = (SimpleInterface)newClass.newInstance();
		instance.foo();
	}
	
	/**
	 * usage of while-loops
	 */
	@Test
	public void whileTest() throws BuilderException, InstantiationException, IllegalAccessException {
		// public class generated.WhileTest implements SimpleInterface {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "WhileTest", Object.class, SimpleInterface.class);
			// public void foo() {
			IMethod m = cls.addMethod(PUBLIC, "foo");
				// int i;
				Variable i = m.addVar(int.class);
				
				// i = 0;
				i.set(0);
				// while (i != 3) {
				m.While(i.notEqual(3));
					// System.out.println("hallo");
					m.$(System.class).get("out").invoke("println", "hello");
					// i = i + 1;
					i.set(i.add(1));
				// }
				m.End();
			// }
			m.End();
		// }
		Class<?> newClass = cls.build();
		
		SimpleInterface instance = (SimpleInterface)newClass.newInstance();
		instance.foo();
	}
	
	/**
	 * uasage of for-each-loops
	 */
	@Test
	public void forEachTest() throws BuilderException, InstantiationException, IllegalAccessException {
		// public class generated.ForEachTest implements SimpleInterface {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "ForEachTest", Object.class, SimpleInterface.class);
			// public void foo() {
			IMethod m = cls.addMethod(PUBLIC, "foo");
				// String[] array;
				Variable array = m.addVar(String[].class);
				
				// array = new String[2];
				array.set(m.New(String[].class, 2));
				
				// array[0] = "1";
				array.get(0).set("1");
				
				// array[0] = "2";
				array.get(1).set("2");
				
				// for (String e : array) {
				Variable e = m.ForEach(array);
					// System.out.println(e);
					m.$(System.class).get("out").invoke("println", e);
				// }
				m.End();
			// }
			m.End();
		// }
		Class<?> newClass = cls.build();
		
		SimpleInterface instance = (SimpleInterface)newClass.newInstance();
		instance.foo();
	}
	
	/**
	 * usage of try-catch
	 */
	@Test
	public void tryCatchTest() throws BuilderException, InstantiationException, IllegalAccessException {
		// public class generated.TryCatchTest implements SimpleInterface {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "TryCatchTest", Object.class, SimpleInterface.class);
			// public void foo() {
			IMethod m = cls.addMethod(PUBLIC, "foo");
				// try {
				m.Try();
					// throw new Exception("Error");
					m.Throw(m.New(Exception.class, "Error"));
				// } catch (Exception e) {
				Variable e = m.Catch(Exception.class);
					// System.out.println(e.getMessage());
					m.$(System.class).get("out").invoke("println", e.invoke("getMessage"));
				// }
				m.End();
			// }
			m.End();
		// }
		Class<?> newClass = cls.build();
		
		SimpleInterface instance = (SimpleInterface)newClass.newInstance();
		instance.foo();
	}
	
	/**
	 * usage of arrays
	 */
	@Test
	public void arrayTest() throws BuilderException, InstantiationException, IllegalAccessException {
		// public class generated.ArrayTest implements SimpleInterface {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "ArrayTest", Object.class, SimpleInterface.class);
			// public void foo() {
			IMethod m = cls.addMethod(PUBLIC, "foo");
				// String[] array;
				Variable array = m.addVar(String[].class);
				
				// array = new String[5];
				array.set(m.New(String[].class, 5));
				
				// array[2] = "42";
				array.get(2).set("42");
				
				// System.out.pringtln(array[2]);
				m.$(System.class).get("out").invoke("println", array.get(2));
			// }
			m.End();
		// }
		Class<?> newClass = cls.build();
		
		SimpleInterface instance = (SimpleInterface)newClass.newInstance();
		instance.foo();
	}
	
	/**
	 * usage of MethodDefinition class for a simplified programming
	 */
	@Test
	public void methodDefinitionTest() throws BuilderException, InstantiationException, IllegalAccessException {
		// public class generated.MethodDefinitionTest implements SimpleInterface {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "MethodDefinitionTest", Object.class, SimpleInterface.class);
		new MethodDefinition(cls, PUBLIC, "foo") {
			// public void foo() {
			@Override
			protected void implement() throws BuilderException {
				// int i;
				Variable i = addVar(int.class);
				
				// i = 0;
				i.set(0);
				// while (i != 3) {
				While(i.notEqual(3));
					// System.out.println("hallo");
					$(System.class).get("out").invoke("println", "hello");
					// i = i + 1;
					i.set(i.add(1));
				// }
				End();
			}
			// }
		};
		// }
		Class<?> newClass = cls.build();
		
		SimpleInterface instance = (SimpleInterface)newClass.newInstance();
		instance.foo();
	}
	
	/**
	 * debugging of generated classes (source path must be set)
	 * Eclipse:
	 * 1. run debugDemo
	 * 2. "Run" -> "Debug-Configuartions..." -> "Source" -> "Add..." -> "File System Directory" -> insert <workspace_location>/ClassBuilder/gen/src
	 * 3. add break point at "instance.foo()"
	 * 4. debug debugDemo
	 * 5. step into (F5)
	 */
	@Test
	public void debugDemo() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException, BuilderCompilerException, InstantiationException, IllegalAccessException {
		// new ClassFactory
		ClassFactory classFactory = new ClassFactory();
		// source path for generated java-files
		classFactory.setSourcePath("gen/src");
		
		// public class generated.WhileTest implements SimpleInterface {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "DebugTest", Object.class, SimpleInterface.class);
			// public void foo() {
			IMethod m = cls.addMethod(PUBLIC, "foo");
				// int i;
				Variable i = m.addVar(int.class);
				
				// i = 0;
				i.set(0);
				// while (i != 3) {
				m.While(i.notEqual(3));
					// System.out.println("hallo");
					m.$(System.class).get("out").invoke("println", "hello");
					// i = i + 1;
					i.set(i.add(1));
				// }
				m.End();
			// }
			m.End();
		// }
		Class<?> newClass = cls.build();
		
		SimpleInterface instance = (SimpleInterface)newClass.newInstance();
		
		// set a breakpoint in the next line and step-into (F5)
		instance.foo();
	}
}
