package classbuilder.test.builder;

public class TestObject {
	public int publicField;
	
	protected int protectedField;
	
	@SuppressWarnings("unused")
	private int privateField;
	
	public static int staticField;
	
	public final int finalField = 1;
	
	public TestObject() {
		
	}
	
	@SuppressWarnings("unused")
	private TestObject(int a) {
		// private constructor
	}
	
	protected TestObject(int a, int b) {
		// private constructor
	}
	
	public TestObject(int a, int b, int c) {
		// public constructor
	}
	
	@SuppressWarnings("unused")
	private void privateMethod() {
		// private method
	}
	
	protected void protectedMethod() {
		// private method
	}
	
	public void publicMethod() {
		// public method
	}
	
	public int publicMethod(int i) {
		return i;
	}
	
	public static void staticMethod() {
		// public method
	}
	
	public static int staticMethod(int i) {
		return i;
	}
	
	public int foo() {
		return 0;
	}
	
	public int bar(int i) {
		return 0;
	}
}
