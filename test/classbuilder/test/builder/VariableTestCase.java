package classbuilder.test.builder;

import org.junit.Assert;
import org.junit.Test;

import classbuilder.BuilderAccessException;
import classbuilder.BuilderModifierException;
import classbuilder.BuilderNameException;
import classbuilder.BuilderSyntaxException;
import classbuilder.BuilderTypeException;
import classbuilder.ClassFactory;
import classbuilder.IClass;
import classbuilder.IMethod;
import classbuilder.Variable;

public class VariableTestCase {
	
	private ClassFactory classFactory = new ClassFactory();
	
	@Test
	public void getNameTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(IClass.PUBLIC, "test", "GetNameTest", Object.class);
		IMethod m = cls.addMethod(IClass.PUBLIC, "test");
			Variable v = m.addVar(int.class);
			Assert.assertEquals("$1", v.getName());
	}
	
	@Test
	public void getTypeTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(IClass.PUBLIC, "test", "GetTypeTest", Object.class);
		IMethod m = cls.addMethod(IClass.PUBLIC, "test");
			Variable v = m.addVar(int.class);
			Assert.assertEquals(int.class, v.getType());
	}
	
	@Test
	public void isInitializedTest() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException {
		IClass cls = classFactory.createClass(IClass.PUBLIC, "test", "IsInitializedTest", Object.class);
		IMethod m = cls.addMethod(IClass.PUBLIC, "test");
			Variable v = m.addVar(int.class);
			Assert.assertFalse(v.isInitialized());
			v.set(1);
			Assert.assertTrue(v.isInitialized());
	}
	
}
