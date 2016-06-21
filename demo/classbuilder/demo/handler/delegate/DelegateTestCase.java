package classbuilder.demo.handler.delegate;

import org.junit.Assert;
import org.junit.Test;

import classbuilder.BuilderException;
import classbuilder.handler.HandlerException;
import classbuilder.handler.ObjectFactory;

public class DelegateTestCase {
	
	// interface
	public interface DelegateInterface {
		public void foo();
		public int bar(int i);
	}
	
	// implementation
	public static class DelegateClass implements DelegateInterface {
		public void foo() {
			System.out.println("DelegateClass.foo()");
		}
		
		public int bar(int i) {
			return i;
		}
	}
	
	// the method 'getDelegate' will be delegated
	public static abstract class GetterTest implements DelegateInterface {
		private DelegateClass delegate;
		
		public GetterTest() {
			delegate = new DelegateClass();
		}
		
		@Delegate
		public DelegateInterface getDelegate() {
			return delegate;
		}
		
		public int bar(int i) {
			return i + 1;
		}
	}
	
	// the field 'delegate' will be delegated
	public static abstract class FieldTest implements DelegateInterface {
		@Delegate()
		protected DelegateClass delegate;
		
		public FieldTest() {
			delegate = new DelegateClass();
		}
		
		public int bar(int i) {
			return i + 1;
		}
	}
	
	// override test 
	public static abstract class OverrideTest implements DelegateInterface {
		@Delegate(value=DelegateInterface.class, override=true)
		protected DelegateClass delegate;
		
		public OverrideTest() {
			delegate = new DelegateClass();
		}
		
		public int bar(int i) {
			return i + 1;
		}
	}
	
	public interface DelegateInterface2 {
		public int bar(int i);
	}
	
	// interface test 
	public static abstract class InterfaceTest implements DelegateInterface {
		@Delegate(DelegateInterface2.class)
		protected DelegateClass delegate;
		
		public InterfaceTest() {
			delegate = new DelegateClass();
		}
		
		public void foo() {
			
		}
	}
	
	@Test
	public void delegateFieldTest() throws BuilderException, HandlerException {
		// create an object factory
		ObjectFactory factory = new ObjectFactory();
		
		// create an instance with generated delegate methods
		FieldTest test = (FieldTest)factory.create(FieldTest.class);
		
		// test delegate methods
		test.foo();
		int result = test.bar(42);
		Assert.assertEquals(43, result);
	}
	
	@Test
	public void delegateGetterTest() throws BuilderException, HandlerException {
		// create an object factory
		ObjectFactory factory = new ObjectFactory();
		
		// create an instance with generated delegate methods
		GetterTest test = (GetterTest)factory.create(GetterTest.class);
		
		// test delegate methods
		test.foo();
		int result = test.bar(42);
		Assert.assertEquals(43, result);
	}
	
	@Test
	public void delegateOverrideTest() throws BuilderException, HandlerException {
		// create an object factory
		ObjectFactory factory = new ObjectFactory();
		
		// create an instance with generated delegate methods
		OverrideTest test = (OverrideTest)factory.create(OverrideTest.class);
		
		// test delegate methods
		test.foo();
		int result = test.bar(42);
		Assert.assertEquals(42, result);
	}
	
	@Test
	public void delegateInterfaceTest() throws BuilderException, HandlerException {
		// create an object factory
		ObjectFactory factory = new ObjectFactory();
		
		// create an instance with generated delegate methods
		InterfaceTest test = (InterfaceTest)factory.create(InterfaceTest.class);
		
		int result = test.bar(42);
		Assert.assertEquals(42, result);
	}
}
