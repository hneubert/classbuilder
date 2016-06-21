package classbuilder.test.builder;

import static classbuilder.IClass.*;

import java.util.ArrayList;

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

public class StructureTestCase {
	private ClassFactory classFactory = new ClassFactory();
	
	public interface SimpleInterface {
		public int foo();
	}
	
	@Test
	public void if_1() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				
				m.If(m.$(true));
					i.set(3);
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void if_2() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				
				m.If(m.$(true));
					i.set(3);
				m.Else();
					i.set(2);
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void else_() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				
				m.If(m.$(false));
					i.set(2);
				m.Else();
					i.set(3);
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void ifIf() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				
				m.If(m.$(true));
					m.If(m.$(true));
						i.set(3);
					m.Else();
						i.set(2);
					m.End();
				m.Else();
					i.set(4);
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void ifElse() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				
				m.If(m.$(true));
					m.If(m.$(false));
						i.set(2);
					m.Else();
						i.set(3);
					m.End();
				m.Else();
					i.set(4);
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void elseIf() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				
				m.If(m.$(false));
					i.set(4);
				m.Else();
					m.If(m.$(true));
						i.set(3);
					m.Else();
						i.set(2);
					m.End();
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void elseElse() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				
				m.If(m.$(false));
					i.set(4);
				m.Else();
					m.If(m.$(false));
						i.set(2);
					m.Else();
						i.set(3);
					m.End();
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void beforeIf() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				
				i.set(3);
				
				m.If(m.$(false));
					i.set(4);
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void afterIf() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				
				m.If(m.$(false));
					i.set(2);
				m.End();
				
				i.set(3);
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void afterElse() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(1);
				
				m.If(m.$(false));
					i.set(2);
				m.Else();
					i.set(4);
				m.End();
				
				i.set(3);
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void while_() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				
				i.set(0);
				m.While(i.notEqual(3));
					i.set(i.add(1));
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void whileWhile() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				Variable j = m.addVar(int.class);
				
				i.set(0);
				j.set(0);
				m.While(i.notEqual(3));
					j.set(0);
					m.While(j.notEqual(3));
						j.set(j.add(1));
					m.End();
					i.set(i.add(1));
				m.End();
				
				m.Return(j);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	public static ArrayList<Integer> checkpoints = new ArrayList<Integer>();
	
	public static void clear() {
		checkpoints.clear();
	}
	
	public static void checkpoint(int i) {
		checkpoints.add(i);
	}
	
	public static void check(int ...cps) {
		Assert.assertEquals(cps.length, checkpoints.size());
		for (int i = 0; i < cps.length; i++) {
			Assert.assertEquals(cps[i], (int)checkpoints.get(i));
		}
	}
	
	@Test
	public void breakWhileTest() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderAccessException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				i.set(0);
				
				m.$(StructureTestCase.class).invoke("checkpoint", 0);
				m.While(i.notEqual(3));
					m.$(StructureTestCase.class).invoke("checkpoint", 1);
					m.While(i.notEqual(3));
						m.$(StructureTestCase.class).invoke("checkpoint", 2);
						m.If(m.$(true));
							m.Break();
						m.Else();
						
						m.End();
						m.$(StructureTestCase.class).invoke("checkpoint", 3);
					m.End();
					m.$(StructureTestCase.class).invoke("checkpoint", 4);
					m.Break();
				m.End();
				m.$(StructureTestCase.class).invoke("checkpoint", 5);
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		clear();
		test.foo();
		check(new int[] {0, 1, 2, 4, 5});
	}
	
	@Test
	public void breakContinueTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				Variable j = m.addVar(int.class);
				i.set(0);
				j.set(0);
				m.While(m.$(true));
					j.set(0);
					m.While(m.$(true));
						j.set(j.add(1));
						m.If(j.equal(3));
							m.Break();
						m.Else();
							m.Continue();
						m.End();
					m.End();
					i.set(i.add(1));
					m.If(i.notEqual(3));
						m.Continue();
					m.Else();
						m.Break();
					m.End();
				m.End();
				m.Return(i.mul(j));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(9, test.foo());
		
		cls = addClass(SimpleInterface.class);
			m = cls.addMethod(PUBLIC, int.class, "foo");
				i = m.addVar(int.class);
				j = m.addVar(int.class);
				i.set(0);
				j.set(0);
				Variable list = m.addVar(ArrayList.class);
				list.set(m.New(ArrayList.class));
				list.invoke("add", 1);
				list.invoke("add", 2);
				list.invoke("add", 3);
				list.invoke("add", 4);
				m.ForEach(list);
					j.set(0);
					m.ForEach(list);
						j.set(j.add(1));
						m.If(j.equal(3));
							m.Break();
						m.Else();
							m.Continue();
						m.End();
					m.End();
					i.set(i.add(1));
					m.If(i.notEqual(3));
						m.Continue();
					m.Else();
						m.Break();
					m.End();
				m.End();
				m.Return(i.mul(j));
			m.End();
		test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(9, test.foo());
	}
	
	@Test
	public void breakForEachTest() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderAccessException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable list = m.addVar(ArrayList.class);
				Variable i = m.addVar(int.class);
				
				list.set(m.New(ArrayList.class));
				list.invoke("add", "1");
				list.invoke("add", "2");
				list.invoke("add", "3");
				
				i.set(0);
				m.ForEach(list);
					i.set(5);
					m.Break();
//					i.set(i.add(1));
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(5, test.foo());
	}
	
	@Test
	public void continueWhileTest() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderAccessException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				Variable j = m.addVar(int.class);
				
				i.set(0);
				j.set(5);
				m.While(i.notEqual(3));
					i.set(i.add(1));
					m.Continue();
//					j.set(j.add(1));
				m.End();
				
				m.Return(j.add(i));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(8, test.foo());
	}
	
	@Test
	public void continueForEachTest() throws BuilderSyntaxException, BuilderModifierException, BuilderNameException, BuilderTypeException, BuilderAccessException, InstantiationException, IllegalAccessException, BuilderCompilerException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable list = m.addVar(ArrayList.class);
				Variable i = m.addVar(int.class);
				Variable j = m.addVar(int.class);
				
				list.set(m.New(ArrayList.class));
				list.invoke("add", "1");
				list.invoke("add", "2");
				list.invoke("add", "3");
				
				i.set(0);
				j.set(5);
				m.ForEach(list);
					i.set(i.add(1));
					m.Continue();
//					j.set(j.add(1));
				m.End();
				
				m.Return(j.add(i));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(8, test.foo());
	}
	
	@Test
	public void beforeWhile() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				Variable j = m.addVar(int.class);
				
				i.set(0);
				j.set(3);
				m.While(i.notEqual(3));
					i.set(i.add(1));
				m.End();
				
				m.Return(j);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void afterWhile() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				
				i.set(0);
				m.While(i.notEqual(5));
					i.set(i.add(1));
				m.End();
				
				i.set(3);
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void for_() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable list = m.addVar(ArrayList.class);
				Variable i = m.addVar(int.class);
				
				list.set(m.New(ArrayList.class));
				list.invoke("add", "1");
				list.invoke("add", "2");
				list.invoke("add", "3");
				
				i.set(0);
				m.ForEach(list);
					i.set(i.add(1));
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void for2Test() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable list = m.addVar(ArrayList.class);
				Variable i = m.addVar(int.class);
				
				list.set(m.New(ArrayList.class));
				list.invoke("add", 1);
				list.invoke("add", 2);
				list.invoke("add", 3);
				
				i.set(0);
				Variable e = m.ForEach(list, Integer.class);
					i.set(i.add(e));
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(6, test.foo());
	}
	
	@Test
	public void forFor() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable list = m.addVar(ArrayList.class);
				Variable list2 = m.addVar(ArrayList.class);
				Variable i = m.addVar(int.class);
				
				list.set(m.New(ArrayList.class));
				list.invoke("add", "1");
				list.invoke("add", "2");
				list.invoke("add", "3");
				
				list2.set(m.New(ArrayList.class));
				list2.invoke("add", "1");
				list2.invoke("add", "2");
				list2.invoke("add", "3");
				
				i.set(0);
				m.ForEach(list);
					m.ForEach(list2);
						i.set(i.add(1));
					m.End();
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(9, test.foo());
	}
	
	@Test
	public void element() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable list = m.addVar(ArrayList.class);
				Variable i = m.addVar(int.class);
				
				list.set(m.New(ArrayList.class));
				list.invoke("add", "1");
				list.invoke("add", "2");
				list.invoke("add", "3");
				
				i.set(0);
				Variable e = m.ForEach(list);
					i.set(m.$(Integer.class).invoke("parseInt", e.cast(String.class)));
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void beforeFor() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable list = m.addVar(ArrayList.class);
				Variable i = m.addVar(int.class);
				
				list.set(m.New(ArrayList.class));
				list.invoke("add", "1");
				list.invoke("add", "2");
				list.invoke("add", "3");
				
				i.set(3);
				m.ForEach(list);
					//i.set(m.$(i, NodeType.ADD, 1));
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void afterFor() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable list = m.addVar(ArrayList.class);
				Variable i = m.addVar(int.class);
				
				list.set(m.New(ArrayList.class));
				list.invoke("add", "1");
				list.invoke("add", "2");
				list.invoke("add", "3");
				
				i.set(0);
				m.ForEach(list);
					i.set(i.add(1));
				m.End();
				i.set(3);
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test(expected=Exception.class)
	public void throw_() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				m.Throw(m.New(Exception.class));
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void try_() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				
				i.set(1);
				m.Try();
					i.set(3);
				m.Catch(Exception.class);
					i.set(2);
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void catch_() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				
				i.set(1);
				m.Try();
					m.Throw(m.New(Exception.class));
//					i.set(3);
				m.Catch(Exception.class);
					i.set(3);
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void trysCatch() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				
				i.set(1);
				m.Try();
					m.Try();
						i.set(3);
					m.Catch(Exception.class);
						i.set(2);
					m.End();
				m.Catch(Exception.class);
					i.set(4);
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void tryTry() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				
				i.set(1);
				m.Try();
					m.Try();
						m.Throw(m.New(Exception.class));
//						i.set(2);
					m.Catch(Exception.class);
						i.set(3);
					m.End();
				m.Catch(Exception.class);
					i.set(4);
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void catchTry() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				
				i.set(1);
				m.Try();
					m.Throw(m.New(Exception.class));
				m.Catch(Exception.class);
					m.Try();
						i.set(3);
					m.Catch(Exception.class);
						i.set(2);
					m.End();
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void catchCatch() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				
				i.set(1);
				m.Try();
					m.Throw(m.New(Exception.class));
				m.Catch(Exception.class);
					m.Try();
						m.Throw(m.New(Exception.class));
//						i.set(2);
					m.Catch(Exception.class);
						i.set(3);
					m.End();
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void exception() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				
				i.set(1);
				m.Try();
					m.Throw(m.New(Exception.class, "3"));
//					i.set(2);
				Variable e = m.Catch(Exception.class);
					i.set(m.$(Integer.class).invoke("parseInt", e.invoke("getMessage")));
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void beforeTry() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				
				i.set(3);
				m.Try();
					m.Throw(m.New(Exception.class, "3"));
				m.Catch(Exception.class);
					
				m.End();
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test
	public void afterCatch() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = addClass(SimpleInterface.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo");
				Variable i = m.addVar(int.class);
				
				i.set(1);
				m.Try();
					i.set(2);
					m.Throw(m.New(Exception.class, "3"));
				m.Catch(Exception.class);
					i.set(4);
				m.End();
				i.set(3);
				
				m.Return(i);
			m.End();
		SimpleInterface test = (SimpleInterface)cls.build().newInstance();
		Assert.assertEquals(3, test.foo());
	}
	
	@Test(expected=BuilderException.class)
	public void elseNotAllowed() throws BuilderException {
		IClass cls = classFactory.createClass(PUBLIC, "test", "Test", Object.class);
			IMethod m = cls.addMethod(PUBLIC, "foo", int.class);
				m.Else();
	}
	
	@Test(expected=BuilderException.class)
	public void catchNotAllowed() throws BuilderException {
		IClass cls = classFactory.createClass(PUBLIC, "test", "Test", Object.class);
			IMethod m = cls.addMethod(PUBLIC, "foo", int.class);
				m.Catch(Exception.class);
	}
	
	@Test(expected=BuilderException.class)
	public void noConditional_If() throws BuilderException {
		IClass cls = classFactory.createClass(PUBLIC, "test", "Test", Object.class);
			IMethod m = cls.addMethod(PUBLIC, "foo", int.class);
				m.If(m.$(1));
	}

	@Test(expected=BuilderException.class)
	public void noConditional_While() throws BuilderException {
		IClass cls = classFactory.createClass(PUBLIC, "test", "Test", Object.class);
			IMethod m = cls.addMethod(PUBLIC, "foo", int.class);
				m.While(m.$(1));
	}
	
	@Test(expected=BuilderException.class)
	public void noException_Catch() throws BuilderException {
		IClass cls = classFactory.createClass(PUBLIC, "test", "Test", Object.class);
			IMethod m = cls.addMethod(PUBLIC, "foo", int.class);
				m.Catch(Object.class);
	}
	
	@Test
	public void deadCodeVarInitTest() throws BuilderException {
		IClass cls = classFactory.createClass(PUBLIC, "test", "Test", Object.class);
			IMethod m = cls.addMethod(PUBLIC, "foo", int.class);
				Variable i = m.addVar(int.class);
				Assert.assertFalse(i.isInitialized());
				
				Variable e = m.ForEach(m.New(ArrayList.class));
					Assert.assertTrue(e.isInitialized());
					i.set(1);
					Assert.assertTrue(i.isInitialized());
				m.End();
				Assert.assertFalse(i.isInitialized());
				
				m.While(m.$(true));
					i.set(1);
					Assert.assertTrue(i.isInitialized());
				m.End();
				Assert.assertFalse(i.isInitialized());
				
				m.If(m.$(true));
					i.set(1);
					Assert.assertTrue(i.isInitialized());
				m.End();
				Assert.assertFalse(i.isInitialized());
				
				m.If(m.$(true));
					i.set(1);
					Assert.assertTrue(i.isInitialized());
				m.ElseIf(m.$(true));
					i.set(1);
					Assert.assertTrue(i.isInitialized());
				m.End();
				Assert.assertFalse(i.isInitialized());
				
				m.If(m.$(true));
					i.set(1);
					Assert.assertTrue(i.isInitialized());
				m.ElseIf(m.$(true));
					Assert.assertFalse(i.isInitialized());
				m.Else();
					Assert.assertFalse(i.isInitialized());
					i.set(1);
					Assert.assertTrue(i.isInitialized());
				m.End();
				Assert.assertFalse(i.isInitialized());
				
				m.If(m.$(true));
					m.If(m.$(true));
						i.set(1);
						Assert.assertTrue(i.isInitialized());
					m.ElseIf(m.$(true));
						Assert.assertFalse(i.isInitialized());
						i.set(1);
						Assert.assertTrue(i.isInitialized());
					m.Else();
						Assert.assertFalse(i.isInitialized());
						i.set(1);
						Assert.assertTrue(i.isInitialized());
					m.End();
					Assert.assertTrue(i.isInitialized());
				m.Else();
					Assert.assertFalse(i.isInitialized());
				m.End();
				Assert.assertFalse(i.isInitialized());
				
				m.Try();
					i.set(1);
					Assert.assertTrue(i.isInitialized());
				m.Catch(Exception.class);
					Assert.assertFalse(i.isInitialized());
					i.set(1);
					Assert.assertTrue(i.isInitialized());
				m.End();
				Assert.assertTrue(i.isInitialized());
			m.End();
		cls.build();
	}
	
	public interface IfElseTest {
		public int foo(int i);
	}
	
	@Test
	public void ifElseTest() throws BuilderException, InstantiationException, IllegalAccessException {
		IClass cls = classFactory.createClass(PUBLIC, "test", "IfElseTest", Object.class, IfElseTest.class);
			IMethod m = cls.addMethod(PUBLIC, int.class, "foo", int.class);
				Variable i = m.addVar(int.class);
				m.If(m.getParameter(0).equal(1));
					i.set(1);
				m.ElseIf(m.getParameter(0).equal(2));
					i.set(2);
				m.Else();
					i.set(3);
				m.End();
				m.Return(i);
			m.End();
		Class<?> c = cls.build();
		
		IfElseTest test = (IfElseTest)c.newInstance();
		Assert.assertEquals(1, test.foo(1));
		Assert.assertEquals(2, test.foo(2));
		Assert.assertEquals(3, test.foo(3));
	}
	
	private static int counter = 0;
	private IClass addClass(Class<?> ...intf) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		counter++;
		return classFactory.createClass(PUBLIC, "generated", "StructureTest" + counter, Object.class, intf);
	}
	
}