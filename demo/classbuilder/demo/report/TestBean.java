package classbuilder.demo.report;

import java.util.Arrays;
import java.util.Collection;

// simple test data
public class TestBean {
	private String value1;
	private Collection<TestData> data;
	
	public TestBean() {
		value1 = "test";
		data = Arrays.asList(new TestData(), new TestData());
	}
	
	public String getValue1() {
		return value1;
	}
	
	public Collection<TestData> getData() {
		return data;
	}
}
