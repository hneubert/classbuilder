package classbuilder.test.builder;

import org.junit.Assert;
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
import classbuilder.test.builder.TestAnnotation.SubAnnotation;

import static classbuilder.IClass.PUBLIC;

import java.lang.annotation.ElementType;

public class IAnnotationTestCase {
	
	private ClassFactory cf = new ClassFactory();
	
	@Test
	public void getType_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException {
		IClass cls = cf.createClass(PUBLIC, "package", "Class", Object.class);
		IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
		Assert.assertEquals(TestAnnotation.class, annotation.getType());
	}
	
	@Test
	public void setValue_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException {
		IClass cls = cf.createClass(PUBLIC, "package", "Class", Object.class);
		IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
		annotation.setValue("value", "foo");
		annotation.setValue("num", 55);
	}
	
	@Test(expected=BuilderTypeException.class)
	public void setValue_invalidValue_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException {
		IClass cls = cf.createClass(PUBLIC, "package", "Class", Object.class);
		IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
		annotation.setValue("value", 55);
	}
	
	@Test(expected=BuilderTypeException.class)
	public void setValue_invalidValue2_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException {
		IClass cls = cf.createClass(PUBLIC, "package", "Class", Object.class);
		IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
		annotation.setValue("value", null);
	}
	
	@Test(expected=BuilderAccessException.class)
	public void setValue_invalidName_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException {
		IClass cls = cf.createClass(PUBLIC, "package", "Class", Object.class);
		IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
		annotation.setValue("bar", 55);
	}
	
	@Test
	public void getValue_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException {
		IClass cls = cf.createClass(PUBLIC, "package", "Class", Object.class);
		IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
		annotation.setValue("value", "foo");
		annotation.setValue("num", 55);
		Assert.assertEquals("foo", annotation.getValue("value"));
		Assert.assertEquals(55, annotation.getValue("num"));
	}
	
	@Test
	public void getNames_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException {
		IClass cls = cf.createClass(PUBLIC, "package", "Class", Object.class);
		IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
		annotation.setValue("value", "foo");
		Assert.assertEquals(1, annotation.getNames().size());
		Assert.assertEquals("value", annotation.getNames().iterator().next());
	}
	
	@Test
	public void countValues_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException {
		IClass cls = cf.createClass(PUBLIC, "package", "Class", Object.class);
		IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
		annotation.setValue("value", "foo");
		annotation.setValue("num", 55);
		Assert.assertEquals(2, annotation.countValues());
	}
	
	@Test
	public void createAnnotation_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException {
		IClass cls = cf.createClass(PUBLIC, "package", "Class", Object.class);
		IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
		annotation.setValue("value", "foo");
		IAnnotation sub = annotation.createAnnotaton(SubAnnotation.class);
		annotation.setValue("sub", sub);
		Assert.assertEquals(sub, annotation.getValue("sub"));
	}
	
	@Test(expected=BuilderTypeException.class)
	public void createAnnotation_invalidType_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException {
		IClass cls = cf.createClass(PUBLIC, "package", "Class", Object.class);
		IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
		annotation.setValue("value", "foo");
		IAnnotation sub = annotation.createAnnotaton(TestAnnotation.class);
		annotation.setValue("sub", sub);
		Assert.assertEquals(sub, annotation.getValue("sub"));
	}
	
	@Test(expected=BuilderCompilerException.class)
	public void noValue_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException, BuilderCompilerException {
		IClass cls = cf.createClass(PUBLIC, "package", "Class", Object.class);
		cls.addAnnotation(TestAnnotation.class);
		cls.build();
	}
	
	@Test
	public void compile_test() throws BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderSyntaxException, BuilderAccessException, BuilderCompilerException {
		IClass cls = cf.createClass(PUBLIC, "package", "CompileTest", Object.class);
		IAnnotation annotation = cls.addAnnotation(TestAnnotation.class);
		annotation.setValue("value", "foo");
		annotation.setValue("num", 55);
		annotation.setValue("array", new int[] {1, 2});
		annotation.setValue("classValue", Integer.class);
		annotation.setValue("enumValue", ElementType.FIELD);
		IAnnotation sub = annotation.createAnnotaton(SubAnnotation.class);
		sub.setValue("value", "bar");
		annotation.setValue("sub", sub);
		annotation.setValue("subArray", new IAnnotation[] {sub});
		Class<?> c = cls.build();
		
		TestAnnotation a = c.getAnnotation(TestAnnotation.class);
		Assert.assertEquals("foo", a.value());
		Assert.assertEquals(55, a.num());
		Assert.assertArrayEquals(new int[] {1, 2}, a.array());
		Assert.assertEquals("bar", a.sub().value());
		Assert.assertEquals("bar", a.subArray()[0].value());
		Assert.assertEquals(Integer.class, a.classValue());
		Assert.assertEquals(ElementType.FIELD, a.enumValue());
	}
}
