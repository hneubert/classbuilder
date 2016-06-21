package classbuilder.test.builder;

import static classbuilder.IClass.*;

import org.junit.Test;

import classbuilder.BuilderException;
import classbuilder.BuilderSyntaxException;
import classbuilder.ClassFactory;
import classbuilder.IClass;
import classbuilder.IMethod;

public class ExceptionTest {
	
	private ClassFactory classFactory = new ClassFactory();
	
	@Test(expected=BuilderException.class)
	public void noIterable() throws BuilderException {
		IClass cls = classFactory.createClass(PUBLIC, "test", "Test", Object.class);
			IMethod m = cls.addMethod(PUBLIC, "foo", int.class);
				m.ForEach(m.$("foo"));
	}
	
	@Test(expected=BuilderSyntaxException.class)
	public void unusedExpression() throws BuilderException {
		IClass cls = classFactory.createClass(PUBLIC, "test", "Test", Object.class);
			IMethod m = cls.addMethod(PUBLIC, "foo", int.class);
				m.$(System.class).get("out");
			m.End();
		cls.build();
	}
}
