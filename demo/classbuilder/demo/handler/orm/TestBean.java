package classbuilder.demo.handler.orm;

import classbuilder.demo.handler.getset.GetterSetter;

// a test bean
// mapped table: create table t_test (c_int integer, c_float float, c_text varchar(15))
@GetterSetter
@Table("t_test")
public abstract class TestBean {
	
	@Column(name="c_int", pk=true)
	protected int number;
	
	@Column(name="c_float")
	protected float floatNumber;
	
	@Column(name="c_text")
	protected String text;
	
	public TestBean() {
		
	}
	
	public abstract int getNumber();
	public abstract void setNumber(int number);
	
	public abstract float getFloatNumber();
	public abstract void setFloatNumber(float floatNumber);
	
	public abstract String getText();
	public abstract void setText(String text);
	
}
