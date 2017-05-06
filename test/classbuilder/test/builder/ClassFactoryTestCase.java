package classbuilder.test.builder;

import static classbuilder.IClass.PUBLIC;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import classbuilder.BuilderCompilerException;
import classbuilder.BuilderModifierException;
import classbuilder.BuilderNameException;
import classbuilder.BuilderTypeException;
import classbuilder.ClassFactory;
import classbuilder.DynamicClassLoader;
import classbuilder.IClass;
import classbuilder.util.DefaultDynamicClassLoader;

public class ClassFactoryTestCase {
	
	private ClassFactory classFactory = new ClassFactory();
	
	@Test
	public void createClassTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderCompilerException {
		ClassFactory factory = new ClassFactory();
		IClass cls = factory.createClass(IClass.PUBLIC, "test", "CreateClassTest", Object.class);
		cls.build();
	}
	
	@Test
	public void addClass_package_BuilderNameException() throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		try {
			classFactory.createClass(PUBLIC, null, "Class", Object.class);
			fail("<null>");
		} catch (BuilderNameException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "", "Class", Object.class);
			fail("<empty>");
		} catch (BuilderNameException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "*", "Class", Object.class);
			fail("*");
		} catch (BuilderNameException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "1", "Class", Object.class);
			fail("1");
		} catch (BuilderNameException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, ".foo", "Class", Object.class);
			fail(".foo");
		} catch (BuilderNameException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "bar.", "Class", Object.class);
			fail("bar.");
		} catch (BuilderNameException e) {
			
		}
	}
	
	@Test
	public void addClass_name_BuilderNameException() throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		try {
			classFactory.createClass(PUBLIC, "package", null, Object.class);
			fail("<null>");
		} catch (BuilderNameException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "package", "", Object.class);
			fail("<empty>");
		} catch (BuilderNameException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "package", "*", Object.class);
			fail("*");
		} catch (BuilderNameException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "package", "1", Object.class);
			fail("1");
		} catch (BuilderNameException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "package", "a.b", Object.class);
			fail("a.b");
		} catch (BuilderNameException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "package", "foo", Object.class);
		} catch (BuilderNameException e) {
			fail("foo");
		}
	}
	
	@Test
	public void addClass_BuilderModifierException() throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		classFactory.createClass(PUBLIC, "package", "Class", Object.class);
		classFactory.createClass(PUBLIC | IClass.ABSTRACT, "package", "Class", Object.class);
		classFactory.createClass(PUBLIC | IClass.INTERFACE, "package", "Class", null);
		
		for (int i = 0x40000000; i > 1; i >>= 1) {
			if (i == PUBLIC) continue;
			if (i == IClass.ABSTRACT) continue;
			if (i == IClass.INTERFACE) continue;
			if (i == IClass.FINAL) continue;
			if (i == IClass.SUPER) continue;
			if (i == IClass.ENUM) continue;
			if (i == 0x2000) continue;
			try {
				classFactory.createClass(i, "package", "Class", Object.class);
				fail(Integer.toHexString(i));
			} catch (BuilderModifierException e) {
				
			}
		}
		
		try {
			classFactory.createClass(IClass.FINAL | IClass.ABSTRACT, "package", "Class", Object.class);
			fail("final abstract");
		} catch (BuilderModifierException e) {
			
		}
	}
	
	@Test
	public void addClass_1_BuilderTypeException() throws BuilderModifierException, BuilderNameException {
		try {
			classFactory.createClass(PUBLIC, "package", "Class", null);
			fail("<null>");
		} catch (BuilderTypeException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "package", "Class", int.class);
			fail("primitive");
		} catch (BuilderTypeException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "package", "Class", Integer.class);
			fail("final");
		} catch (BuilderTypeException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "package", "Class", Iterable.class);
			fail("interface");
		} catch (BuilderTypeException e) {
			
		}
	}
	
	@Test
	public void addClass_2_BuilderTypeException() throws BuilderModifierException, BuilderNameException {
		try {
			classFactory.createClass(PUBLIC, "package", "Class", Object.class, (Class<?>)null);
			fail("<null>");
		} catch (BuilderTypeException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "package", "Class", Object.class, int.class);
			fail("primitive");
		} catch (BuilderTypeException e) {
			
		}
		try {
			classFactory.createClass(PUBLIC, "package", "Class", Object.class, Integer.class);
			fail("class");
		} catch (BuilderTypeException e) {
			
		}
	}
	
	public static class TestClassLoader extends DefaultDynamicClassLoader {
		
	}
	
	@Test
	public void setClassLoaderTest() throws BuilderCompilerException, BuilderModifierException, BuilderNameException, BuilderTypeException {
		ClassFactory factory = new ClassFactory();
		DynamicClassLoader cl = new TestClassLoader();
		factory.setClassLoader(cl);
		
		IClass cls = factory.createClass(IClass.PUBLIC, "test", "SetClassLoaderTest", Object.class);
		Class<?> c = cls.build();
		Assert.assertEquals(cl, factory.getClassLoader());
		Assert.assertEquals(cl, c.getClassLoader());
	}
	
	@Test
	public void setClassPathTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderCompilerException {
		ClassFactory factory = new ClassFactory();
		factory.setClassPath("gen/bin");
		
		IClass cls = factory.createClass(IClass.PUBLIC, "test", "SetClassPathTest", Object.class);
		cls.build();
		Assert.assertEquals("gen/bin", factory.getClassPath());
		Assert.assertTrue(new File("gen/bin/test/SetClassPathTest.class").exists());
	}
	
	@Test
	public void setSourcePathTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderCompilerException {
		ClassFactory factory = new ClassFactory();
		factory.setSourcePath("gen/src");
		
		IClass cls = factory.createClass(IClass.PUBLIC, "test", "SetSourcePathTest", Object.class);
		cls.build();
		Assert.assertEquals("gen/src", factory.getSourcePath());
		Assert.assertTrue(new File("gen/src/test/SetSourcePathTest.java").exists());
	}
}
