package classbuilder.test.builder;

import static classbuilder.IClass.PUBLIC;
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import classbuilder.BuilderException;
import classbuilder.BuilderTypeException;
import classbuilder.ClassFactory;
import classbuilder.IClass;
import classbuilder.IMethod;
import classbuilder.Variable;

public class TypeCastTestCase {
	
	private ClassFactory classFactory = new ClassFactory();
	
	private static Class<?>[] types =        {boolean.class, byte.class, short.class, char.class     , int.class    , long.class , float.class, double.class};
	private static Class<?>[] wrapperTypes = {Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class , Float.class, Double.class};
	private static Object[]   values =       {true         , (byte)0x5 , (short)0x5 , (char)0x5      , 0x5          , 0x5L       , 5f      , 5.0};
	private static int counter = 1;
	
	public interface ITest {
		public Object test(Object value);
	}
	
	private static final boolean[][] implicit = { // source -> dest
		//BOOL  BYTE   SHORT  CHAR   INT    LONG   FLOAT  DOUBLE
		{true,  false, false, false, false, false, false, false}, // BOOLEAN
		{false, true,  true,  false, true,  true,  true,  true }, // BYTE
		{false, false, true,  false, true,  true,  true,  true }, // SHORT
		{false, false, false, true,  true,  true,  true,  true }, // CHAR
		{false, false, false, false, true,  true,  true,  true }, // INT
		{false, false, false, false, false, true,  true,  true }, // LONG
		{false, false, false, false, false, false, true,  true }, // FLOAT
		{false, false, false, false, false, false, false, true }  // DOUBLE
	};
	
	private static final boolean[][] explicit = { // source -> dest
		//BOOL  BYTE   SHORT  CHAR   INT    LONG   FLOAT  DOUBLE
		{true,  false, false, false, false, false, false, false}, // BOOLEAN
		{false, true , true , true , true , true,  true,  true }, // BYTE
		{false, true , true , true , true , true,  true,  true }, // SHORT
		{false, true , true , true , true , true,  true,  true }, // CHAR
		{false, true , true , true , true , true,  true,  true }, // INT
		{false, true , true , true , true , true,  true,  true }, // LONG
		{false, true , true , true , true , true,  true,  true }, // FLOAT
		{false, true , true , true , true , true,  true,  true }  // DOUBLE
	};
	
	private static final boolean[][] wrapper = { // source -> dest
		//BOOL  BYTE   SHORT  CHAR   INT    LONG   FLOAT  DOUBLE
		{true,  false, false, false, false, false, false, false}, // BOOLEAN
		{false, true,  false, false, false, false, false, false}, // BYTE
		{false, false, true,  false, false, false, false, false}, // SHORT
		{false, false, false, true,  false, false, false, false}, // CHAR
		{false, false, false, false, true,  false, false, false}, // INT
		{false, false, false, false, false, true,  false, false}, // LONG
		{false, false, false, false, false, false, true,  false}, // FLOAT
		{false, false, false, false, false, false, false, true }  // DOUBLE
	};
	
	@Test
	public void castTest() {
		castTest0(types, types, false, implicit);
		castTest0(types, types, true, explicit);
		castTest0(types, wrapperTypes, false, wrapper);
		castTest0(types, wrapperTypes, true, wrapper);
		castTest0(wrapperTypes, types, false, wrapper);
		castTest0(wrapperTypes, types, true, wrapper);
		castTest0(wrapperTypes, wrapperTypes, false, wrapper);
		castTest0(wrapperTypes, wrapperTypes, true, wrapper);
		

		// 			implicit	explicit
		// w -> o	always		always
		// o -> w	never		always
		// p -> o	always		always
		// o -> p	never		always
		castTest1(Integer.class, Object.class,  false, true);
		castTest1(Integer.class, Object.class,  true,  true);
		castTest1(Object.class,  Integer.class, false, false);
		castTest1(Object.class,  Integer.class, true,  true);
		castTest1(int.class,     Object.class,  false, true);
		castTest1(int.class,     Object.class,  true,  true);
		castTest1(Object.class,  int.class,     false, false);
		castTest1(Object.class,  int.class,     true,  true);
	}
	
	public void castTest0(Class<?>[] from, Class<?>[] to, boolean explicit, boolean[][] matrix) {
		for (int i = 0; i < from.length; i++) {
			for (int j = 0; j < to.length; j++) {
				try {
					IClass cls = classFactory.createClass(PUBLIC, "pkg", "Cast_" + counter++, Object.class, ITest.class);
						IMethod m = cls.addMethod(PUBLIC, Object.class, "test", Object.class);
							Variable v = m.addVar(to[j]);
							if (explicit) {
								v.set(m.getParameter(0).cast(from[i]).cast(to[j]));
							} else {
								v.set(m.getParameter(0).cast(from[i]));
							}
							m.Return(v);
						m.End();
					Class<?> c = cls.build();
					
					ITest test = (ITest)c.newInstance();
					
					Assert.assertEquals(values[j], test.test(values[i]));
					assertTrue(from[i] + " -> " + to[j], matrix[i][j]);
					//if (!cast[i][j]) System.out.println("1 " + types[i] + " -> " + types[j]);
				} catch (InstantiationException e) {
					fail(from[i] + " -> " + to[j] + " " + e.getMessage());
				} catch (IllegalAccessException e) {
					fail(from[i] + " -> " + to[j] + " " + e.getMessage());
				} catch (BuilderTypeException e) {
					assertFalse(from[i] + " -> " + to[j], matrix[i][j]);
				} catch (BuilderException e) {
					fail(from[i] + " -> " + to[j] + " " + e.getMessage());
				}
			}
		}
	}
	
	public void castTest1(Class<?> from, Class<?> to, boolean explicit, boolean allowed) {
		try {
			IClass cls = classFactory.createClass(PUBLIC, "pkg", "Cast_" + counter++, Object.class, ITest.class);
				IMethod m = cls.addMethod(PUBLIC, Object.class, "test", Object.class);
					Variable v = m.addVar(to);
					if (explicit) {
						v.set(m.getParameter(0).cast(from).cast(to));
					} else {
						v.set(m.getParameter(0).cast(from));
					}
					m.Return(v);
				m.End();
			Class<?> c = cls.build();
			
			ITest test = (ITest)c.newInstance();
			
			Assert.assertEquals(5, test.test(5));
			assertTrue(from + " -> " + to, allowed);
		} catch (InstantiationException e) {
			fail(from + " -> " + to + " " + e.getMessage());
		} catch (IllegalAccessException e) {
			fail(from + " -> " + to + " " + e.getMessage());
		} catch (BuilderTypeException e) {
			assertFalse(from + " -> " + to, allowed);
		} catch (BuilderException e) {
			fail(from + " -> " + to + " " + e.getMessage());
		}
	}
}
