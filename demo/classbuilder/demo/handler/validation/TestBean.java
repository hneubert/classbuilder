package classbuilder.demo.handler.validation;

import classbuilder.demo.handler.getset.GetterSetter;

// a simple test bean
@GetterSetter
public abstract class TestBean {
	protected int number;
	protected String text;
	
	// validates number: 0 <= number && number <= 1
	@Validate(min=0, max=1) // <- handles implicit the first parameter
	public abstract void setNumber(int number);
	public abstract int getNumber();
	
	// validates text: text.matches("[0-9]*")
	@Validate // <- specific validations at method parameters
	public abstract void setText(@Validate(pattern="[0-9]*", nullable=false) String text);
	public abstract String getText();
}
