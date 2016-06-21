package classbuilder.test.builder;

import static classbuilder.IClass.*;

import java.util.Collection;

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
import classbuilder.IField;
import classbuilder.IMethod;
import classbuilder.Variable;

public class IFieldTestCase {
	private ClassFactory classFactory = new ClassFactory();
	private static int counter = 1;
	
	public interface SimpleInterface {
		public int foo();
	}
	
	public interface SimpleObjectInterface {
		public Object foo();
	}
	
	public static class SimpleClass {
		public int field;
		public Object obj;
		public static int staticField;
		public final int finalField = 42;
	}
	
	@Before
	public void before() {
		counter++;
	}
	
	@Test
	public void staticField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IField field = cls.addField(PUBLIC | STATIC, int.class, "foo");
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.get(field).set(1);
				m.Return(m.get(field));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test
	public void initStaticField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IField field = cls.addField(PUBLIC | STATIC, "foo", 1);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.get(field));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test
	public void finalStaticField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IField field = cls.addField(PUBLIC | STATIC | FINAL, "foo", 1);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.get(field));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test
	public void field() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IField field = cls.addField(PUBLIC, int.class, "foo");
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.get(field).set(1);
				m.Return(m.get(field));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test
	public void initField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IField field = cls.addField(PUBLIC, "foo", 1);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.get(field));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test
	public void finalField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IField field = cls.addField(PUBLIC | FINAL, "foo", 1);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.get(field));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test
	public void superField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "FieldTest" + counter++, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Super().get("field").set(42);	
				m.Return(m.Super().get("field"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void superStaticField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "FieldTest" + counter++, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.$(cls).get("staticField").set(42);	
				m.Return(m.$(cls).get("staticField"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void superFinalField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "FieldTest" + counter++, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.Super().get("finalField"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test(expected=BuilderAccessException.class)
	public void superFinalField_write() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "FieldTest" + counter++, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Super().get("finalField").set(42);	
				m.Return(m.Super().get("finalField"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void thisSuperField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "FieldTest" + counter++, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.This().get("field").set(42);	
				m.Return(m.This().get("field"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void thisSuperStaticField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "FieldTest" + counter++, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.$(cls).get("staticField").set(42);	
				m.Return(m.$(cls).get("staticField"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void thisSuperFinalField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "FieldTest" + counter++, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.This().get("finalField"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test(expected=BuilderAccessException.class)
	public void thisSuperFinalField_write() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "generated", "FieldTest" + counter++, SimpleClass.class, SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.This().get("finalField").set(42);	
				m.Return(m.This().get("finalField"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void thisStaticField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			cls.addField(PUBLIC | STATIC, int.class, "field");
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.$(cls).get("field").set(1);
				m.Return(m.$(cls).get("field"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test
	public void thisField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			cls.addField(PUBLIC, int.class, "field");
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.This().get("field").set(1);
				m.Return(m.This().get("field"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test
	public void thisFinalField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			cls.addField(PUBLIC | FINAL, "field", 1);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(cls).get("field"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test(expected=BuilderAccessException.class)
	public void instanceStaticField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable obj = m.addVar(SimpleClass.class);
				obj.set(m.New(SimpleClass.class));
				obj.get("staticField").set(1);
				m.Return(obj.get("staticField"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test
	public void instanceField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable obj = m.addVar(SimpleClass.class);
				obj.set(m.New(SimpleClass.class));
				obj.get("field").set(1);
				m.Return(obj.get("field"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test
	public void instanceFinalField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable obj = m.addVar(SimpleClass.class);
				obj.set(m.New(SimpleClass.class));
				m.Return(obj.get("finalField"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(42, test.foo());
	}
	
	@Test
	public void staticStaticField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.$(SimpleClass.class).get("staticField").set(1);
				m.Return(m.$(SimpleClass.class).get("staticField"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	@Test(expected=BuilderAccessException.class)
	public void staticInstanceField() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.$(SimpleClass.class).get("field").set(1);
				m.Return(m.$(SimpleClass.class).get("field"));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(1, test.foo());
	}
	
	private IClass addClass(Class<?> ...intf) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		return classFactory.createClass(PUBLIC, "generated", "FieldTest" + counter, Object.class, intf);
	}
	
	@Test
	public void getName_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IField field = cls.addField(PUBLIC, int.class, "field");
		Assert.assertEquals("field", field.getName());
	}
	
	@Test
	public void getType_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IField field = cls.addField(PUBLIC, int.class, "field");
		Assert.assertEquals(int.class, field.getType());
	}
	
	@Test
	public void getDeclaringClass_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IField field = cls.addField(PUBLIC, int.class, "field");
		Assert.assertEquals(cls, field.getDeclaringClass());
	}
	
	@Test
	public void getModifiers_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IField field = cls.addField(PUBLIC, int.class, "field");
		Assert.assertEquals(PUBLIC, field.getModifiers());
	}
	
	@Test
	public void addAnnotation_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IField field = cls.addField(PUBLIC, int.class, "field");
		IAnnotation annotation = field.addAnnotation(TestAnnotation.class);
		Assert.assertEquals(TestAnnotation.class, annotation.getType());
	}
	
	@Test
	public void getAnnotations_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = classFactory.createClass(PUBLIC, "pkg", "Class", Object.class);
		IField field = cls.addField(PUBLIC, int.class, "field");
		field.addAnnotation(TestAnnotation.class);
		Collection<IAnnotation> annotations = field.getAnnotations();
		Assert.assertEquals(1, annotations.size());
		for (IAnnotation annotation : annotations) {
			Assert.assertEquals(TestAnnotation.class, annotation.getType());
		}
	}
	
	@Test
	public void getSetThisNull() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls = addClass(SimpleObjectInterface.class);
			IField field = cls.addField(PUBLIC, Object.class, "field");
			IMethod m = cls.addMethod(PUBLIC, Object.class, "foo");
				m.get(field).set(null);
				m.Return(m.get(field));
			m.End();
		SimpleObjectInterface test = (SimpleObjectInterface)cls.build().newInstance();
		Assert.assertEquals(null, test.foo());
	}
}
