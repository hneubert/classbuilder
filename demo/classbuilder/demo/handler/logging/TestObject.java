package classbuilder.demo.handler.logging;

// a simple test object
@Logging
public class TestObject {
	
	public void foo() {
		System.out.println("foo");
	}
	
	public int bar(int value) {
		return value;
	}
	
}
