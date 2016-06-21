package classbuilder.demo.handler.getset;

// a simple test bean
@GetterSetter
public abstract class TestBean {
	protected int number;
	protected String text;
	
	public abstract int getNumber();
	public abstract void setNumber(int number);
	public abstract String getText();
	public abstract void setText(String text);
}
