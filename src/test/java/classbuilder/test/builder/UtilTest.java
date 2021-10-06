package classbuilder.test.builder;

import static classbuilder.IClass.PUBLIC;

import org.junit.Before;
import org.junit.Test;

import classbuilder.BuilderAccessException;
import classbuilder.BuilderException;
import classbuilder.BuilderModifierException;
import classbuilder.BuilderNameException;
import classbuilder.BuilderSyntaxException;
import classbuilder.BuilderTypeException;
import classbuilder.ClassFactory;
import classbuilder.IClass;
import classbuilder.test.builder.BasicTestCase.SimpleInterface;
import classbuilder.util.MethodDefinition;

public class UtilTest {
	private ClassFactory classFactory = new ClassFactory();
	
	public interface Foo {
		public void foo();
	}
	
	@Before
	public void before() throws BuilderException {
		//classFactory.setSourcePath("D:\\Projekte\\baroque2\\JavaClassBuilder\\debug");
	}
	
	@Test
	public void methodDefinitionTest() throws InstantiationException, IllegalAccessException, BuilderException {
		IClass cls = addClass(SimpleInterface.class);
		new MethodDefinition(cls, PUBLIC, "foo") {
			@Override
			public void implement() throws BuilderSyntaxException, BuilderAccessException, BuilderTypeException {
				$(System.class).get("out").invoke("println", "hello");
			}
		};
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		test.foo();
	}
	
	private static int counter = 0;
	private IClass addClass(Class<?> ...intf) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		counter++;
		return classFactory.createClass(PUBLIC, "generated", "UtilTest" + counter, Object.class, intf);
	}
	
	private Object getInstance(Class<?> cls) throws InstantiationException, IllegalAccessException {
		try {
			return cls.getConstructor().newInstance();
		} catch (Exception e) {
			throw new InstantiationException(e.getMessage());
		}
	}
}
