package classbuilder.test.builder;

import static classbuilder.IClass.*;

import java.io.PrintStream;

import org.junit.Assert;
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

public class OperatorTestCase {
	private ClassFactory classFactory = new ClassFactory();
	
	public interface SimpleInterface {
		public int foo();
	}
	
	public interface BoolInterface {
		public boolean foo();
	}
	
	@Test
	public void add() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(1).add(2));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void sub() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(4).sub(1));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void mul() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(1).mul(3));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void div() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(9).div(3));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void mod() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(3).mod(4));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void and() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(3).and(15));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void or() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(1).or(2));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void xor() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(3).xor(0));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void shl() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(3).shl(1));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(6, test.foo());
	}
	
	@Test
	public void ushr() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(6).ushr(1));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void shr() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(-6).shr(1));
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(-3, test.foo());
	}
	
	@Test
	public void neg() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(-3).neg());
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void not() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Return(m.$(~3).not());
			m.End();
		SimpleInterface test = (SimpleInterface)getInstance(cls.build());
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void instanceof_() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(BoolInterface.class);
			IMethod m = cls.addMethod(PUBLIC, boolean.class, "foo");
				try {
					m.$(1).instanceOf(BoolInterface.class);
					Assert.fail("instanceof primitive");
				} catch (BuilderTypeException e) {}
//				try {
//					Variable i = m.addVar(Object.class);
//					i.set(Object.class);
//					i.instanceOf(int.class);
//					Assert.fail("instanceof primitive");
//				} catch (BuilderTypeException e) {}
		
		cls = addClass(BoolInterface.class);
			m = cls.addMethod(PUBLIC, boolean.class, "foo");
				m.Return(m.$(System.class).get("out").instanceOf(PrintStream.class));
			m.End();
		BoolInterface test = (BoolInterface)getInstance(cls.build());
		Assert.assertEquals(true, test.foo());
	}
	
	@Test
	public void equal() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(BoolInterface.class);
			IMethod m = cls.addMethod(PUBLIC, boolean.class, "foo");
				m.Return(m.$(1).equal(1));
			m.End();
		BoolInterface test = (BoolInterface)getInstance(cls.build());
		Assert.assertEquals(true, test.foo());
	}
	
	@Test
	public void unequal() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(BoolInterface.class);
			IMethod m = cls.addMethod(PUBLIC, boolean.class, "foo");
				m.Return(m.$(1).notEqual(2));
			m.End();
		BoolInterface test = (BoolInterface)getInstance(cls.build());
		Assert.assertEquals(true, test.foo());
	}
	
	@Test
	public void lower() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(BoolInterface.class);
			IMethod m = cls.addMethod(PUBLIC, boolean.class, "foo");
				m.Return(m.$(1).less(2));
			m.End();
		BoolInterface test = (BoolInterface)getInstance(cls.build());
		Assert.assertEquals(true, test.foo());
	}
	
	@Test
	public void greater() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(BoolInterface.class);
			IMethod m = cls.addMethod(PUBLIC, boolean.class, "foo");
				m.Return(m.$(2).greater(1));
			m.End();
		BoolInterface test = (BoolInterface)getInstance(cls.build());
		Assert.assertEquals(true, test.foo());
	}
	
	@Test
	public void lowerEqual() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(BoolInterface.class);
			IMethod m = cls.addMethod(PUBLIC, boolean.class, "foo");
				m.Return(m.$(1).lessEqual(2));
			m.End();
		BoolInterface test = (BoolInterface)getInstance(cls.build());
		Assert.assertEquals(true, test.foo());
	}
	
	@Test
	public void greaterEqual() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(BoolInterface.class);
			IMethod m = cls.addMethod(PUBLIC, boolean.class, "foo");
				m.Return(m.$(2).greaterEqual(1));
			m.End();
		BoolInterface test = (BoolInterface)getInstance(cls.build());
		Assert.assertEquals(true, test.foo());
	}
	
	@Test
	public void isNull() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(BoolInterface.class);
			IMethod m = cls.addMethod(PUBLIC, boolean.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				try {
					i.isNull();
					Assert.fail("isNull <null>");
				} catch (BuilderTypeException e) {
					
				}
		
		cls = addClass(BoolInterface.class);
			m = cls.addMethod(PUBLIC, boolean.class, "foo");
				m.Return(m.$(System.class).get("out").isNull());
			m.End();
		BoolInterface test = (BoolInterface)getInstance(cls.build());
		Assert.assertEquals(false, test.foo());
		
		cls = addClass(BoolInterface.class);
			m = cls.addMethod(PUBLIC, boolean.class, "foo");
				i = m.addVar(Object.class);
				i.set(null);
				m.Return(i.isNull());
			m.End();
		test = (BoolInterface)getInstance(cls.build());
		Assert.assertEquals(true, test.foo());
	}
	
	@Test
	public void isNotNull() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(BoolInterface.class);
			IMethod m = cls.addMethod(PUBLIC, boolean.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				try {
					i.isNotNull();
					Assert.fail("isNotNull <null>");
				} catch (BuilderTypeException e) {
					
				}
		
		cls = addClass(BoolInterface.class);
			m = cls.addMethod(PUBLIC, boolean.class, "foo");
				m.Return(m.$(System.class).get("out").isNotNull());
			m.End();
		BoolInterface test = (BoolInterface)getInstance(cls.build());
		Assert.assertEquals(true, test.foo());
		
		cls = addClass(BoolInterface.class);
			m = cls.addMethod(PUBLIC, boolean.class, "foo");
				i = m.addVar(Object.class);
				i.set(null);
				m.Return(i.isNotNull());
			m.End();
		test = (BoolInterface)getInstance(cls.build());
		Assert.assertEquals(false, test.foo());
	}
	
	@Test
	public void nullTest() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException, BuilderModifierException, BuilderNameException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls = addClass(BoolInterface.class);
			IMethod m = cls.addMethod(PUBLIC, boolean.class, "foo");
				try {
					m.$(null).length();
					Assert.fail("<null>");
				} catch (BuilderTypeException e) {}
				try {
					m.$(null).get("");
					Assert.fail("<null>");
				} catch (BuilderAccessException e) {}
				try {
					m.$(null).get(1);
					Assert.fail("<null>");
				} catch (BuilderTypeException e) {}
				try {
					m.$(null).isNull();
					Assert.fail("<null>");
				} catch (BuilderTypeException e) {}
				m.$(null).cast(String.class);
				try {
					m.$(null).invoke("");
					Assert.fail("<null>");
				} catch (BuilderTypeException e) {}
				try {
					m.$(1).isNull();
					Assert.fail("<null>");
				} catch (BuilderTypeException e) {}
	}
	
	private static int counter = 0;
	private IClass addClass(Class<?> ...intf) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		counter++;
		return classFactory.createClass(PUBLIC, "generated", "OperatorTest" + counter, Object.class, intf);
	}
	
	public interface MatrixTest {
		public Object foo();
	}
	
	private static final int AND = 1;
	private static final int OR  = 2;
	private static final int XOR = 3;
	private static final int ADD = 4;
	private static final int SUB = 5;
	private static final int MUL = 6;
	private static final int DIV = 7;
	private static final int MOD = 8;
	private static final int SHR = 9;
	private static final int USHR = 10;
	private static final int SHL = 11;
	private static final int NOT = 12;
	private static final int NEG = 13;
	private static final int EQUAL = 14;
	private static final int UNEQUAL = 15;
	private static final int LOWER = 16;
	private static final int GREATER = 17;
	private static final int LOWER_EQUAL = 18;
	private static final int GREATER_EQUAL = 19;
	
	// Komplexität:
	// op * types * types = tests
	// 19 * 8     * 8     = 1216
	
	private int[] operators = {AND, OR, XOR, ADD, SUB, MUL, DIV, MOD, SHR, USHR, SHL, NOT, NEG, EQUAL, UNEQUAL, LOWER, GREATER, LOWER_EQUAL, GREATER_EQUAL};
	private Object[] a = {true,  (byte)0x5A, (short)0x5A5A, (char)0x5A5A, 0x5A5A5A5A, 0x5A5A5A5A5A5A5A5AL, 1.25f, -5.0};
	private Object[] b = {false, (byte)0x0F, (short)0x0F0F, (char)0x0F0F, 0x0F0F0F0F, 0x0F0F0F0F0F0F0F0FL, 5.0f,  -1.25};
	
	@Test
	public void matrixTest() throws InstantiationException, IllegalAccessException, BuilderException {
		for (int op : operators) {
			for (Object a : this.a) {
				if (op == NOT) {
					Object result = getResultType(a, 1, calc(op, a, a));
					//System.out.println(op + ": " + a.getClass().getSimpleName());
					subtest(op, a, a, result);
				} else if (op == NEG) {
					Object result = getResultType(a, 1, calc(op, a, a));
					//System.out.println(op + ": " + a.getClass().getSimpleName());
					subtest(op, a, a, result);
				} else {
					for (Object b : this.b) {
						try  {
							Object result = getResultType(a, b, calc(op, a, b));
							//System.out.println(op + ": " + a.getClass().getSimpleName() + " + " + b.getClass().getSimpleName());
							subtest(op, a, b, result);
						} catch (Exception e) {
							e.printStackTrace();
							Assert.fail(op + ": " + a.getClass().getSimpleName() + " + " + b.getClass().getSimpleName() + " -> " + e.getMessage());
						}
					}
				}
			}
		}
	}
	
	public void subtest(int op, Object a, Object b, Object result) throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(MatrixTest.class);
			IMethod m = cls.addMethod(PUBLIC, Object.class, "foo");
				try {
					switch (op) {
					case AND :
						m.Return(m.$(a).and(b));
						break;
					case OR  :
						m.Return(m.$(a).or(b));
						break;
					case XOR :
						m.Return(m.$(a).xor(b));
						break;
					case ADD :
						m.Return(m.$(a).add(b));
						break;
					case SUB :
						m.Return(m.$(a).sub(b));
						break;
					case MUL :
						m.Return(m.$(a).mul(b));
						break;
					case DIV :
						m.Return(m.$(a).div(b));
						break;
					case MOD :
						m.Return(m.$(a).mod(b));
						break;
					case SHR :
						m.Return(m.$(a).shr(b));
						break;
					case USHR :
						m.Return(m.$(a).ushr(b));
						break;
					case SHL :
						m.Return(m.$(a).shl(b));
						break;
					case NOT :
						m.Return(m.$(a).not());
						break;
					case NEG :
						m.Return(m.$(a).neg());
						break;
					case EQUAL :
						m.Return(m.$(a).equal(b));
						break;
					case UNEQUAL :
						m.Return(m.$(a).notEqual(b));
						break;
					case LOWER :
						m.Return(m.$(a).less(b));
						break;
					case GREATER :
						m.Return(m.$(a).greater(b));
						break;
					case LOWER_EQUAL :
						m.Return(m.$(a).lessEqual(b));
						break;
					case GREATER_EQUAL :
						m.Return(m.$(a).greaterEqual(b));
						break;
					default :
						m.Return(null);
						break;
					}
					if (result == null) {
						Assert.fail();
					}
				} catch (BuilderTypeException e) {
					if (result != null) {
						throw e;
					} else {
						m.Return(null);
					}
				}
			m.End();
		MatrixTest test = (MatrixTest)getInstance(cls.build());
		Assert.assertEquals(result , test.foo());
	}
	
	private Object calc(int op, Object a, Object b) {
		if (a instanceof Character) a = (int)((Character)a).charValue();
		if (b instanceof Character) b = (int)((Character)b).charValue();
		if (a instanceof Boolean || b instanceof Boolean) {
			if (!(a instanceof Boolean) || !(b instanceof Boolean)) return null;
			boolean x = (Boolean)a;
			boolean y = (Boolean)b;
			switch (op) {
			case AND :
				return x & y;
			case OR :
				return x | y;
			case XOR :
				return x ^ y;
			case NOT :
				return !x;
			case EQUAL :
				return x == y;
			case UNEQUAL :
				return x != y;
			}
		} else if (a instanceof Double || b instanceof Double) {
			double x = ((Number)a).doubleValue();
			double y = ((Number)b).doubleValue();
			switch (op) {
			case ADD :
				return x + y;
			case SUB :
				return x - y;
			case MUL :
				return x * y;
			case DIV :
				return x / y;
			case MOD :
				return x % y;
			case NEG :
				return -x;
			case EQUAL :
				return x == y;
			case UNEQUAL :
				return x != y;
			case LOWER :
				return x < y;
			case GREATER :
				return x > y;
			case LOWER_EQUAL :
				return x <= y;
			case GREATER_EQUAL :
				return x >= y;
			}
		} else if (a instanceof Float || b instanceof Float) {
			double x = ((Number)a).floatValue();
			double y = ((Number)b).floatValue();
			switch (op) {
			case ADD :
				return x + y;
			case SUB :
				return x - y;
			case MUL :
				return x * y;
			case DIV :
				return x / y;
			case MOD :
				return x % y;
			case NEG :
				return -x;
			case EQUAL :
				return x == y;
			case UNEQUAL :
				return x != y;
			case LOWER :
				return x < y;
			case GREATER :
				return x > y;
			case LOWER_EQUAL :
				return x <= y;
			case GREATER_EQUAL :
				return x >= y;
			}
		} else {
			long x = ((Number)a).longValue();
			long y = ((Number)b).longValue();
			switch (op) {
			case AND :
				return x & y;
			case OR :
				return x | y;
			case XOR :
				return x ^ y;
			case ADD :
				return x + y;
			case SUB :
				return x - y;
			case MUL :
				return x * y;
			case DIV :
				return x / y;
			case MOD :
				return x % y;
			case SHR :
				if (b instanceof Long) return null;
				return x >> y;
			case USHR :
				if (b instanceof Long) return null;
				return x >>> y;
			case SHL :
				if (b instanceof Long) return null;
				return x << y;
			case NOT :
				return ~x;
			case NEG :
				return -x;
			case EQUAL :
				return x == y;
			case UNEQUAL :
				return x != y;
			case LOWER :
				return x < y;
			case GREATER :
				return x > y;
			case LOWER_EQUAL :
				return x <= y;
			case GREATER_EQUAL :
				return x >= y;
			}
		}
		return null;
	}
	
	public Object getResultType(Object a, Object b, Object n) {
		if (n == null) return null;
		if (n instanceof Boolean) return n;
		if (a instanceof Character) a = (int)((Character)a).charValue();
		if (b instanceof Character) b = (int)((Character)b).charValue();
		if (a instanceof Boolean || b instanceof Boolean) return (Boolean)n;
		if (a instanceof Double || b instanceof Double) return n;
		if (a instanceof Float || b instanceof Float) return ((Number)n).floatValue();
		if (a instanceof Long || b instanceof Long) return n;
		if (a instanceof Integer || b instanceof Integer) return ((Number)n).intValue();
		if (a instanceof Character || b instanceof Character) return ((Number)n).intValue();
		if (a instanceof Short || b instanceof Short) return ((Number)n).intValue();
		if (a instanceof Byte || b instanceof Byte) return ((Number)n).intValue();
		return n;
	}
	
	private Object getInstance(Class<?> cls) throws InstantiationException, IllegalAccessException {
		try {
			return cls.getConstructor().newInstance();
		} catch (Exception e) {
			throw new InstantiationException(e.getMessage());
		}
	}
}
