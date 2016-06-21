package classbuilder.demo.report.pdf;

import java.io.OutputStream;

public abstract class PDFObject {
	private int index;
	protected int length;
	protected String id;
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getLength() {
		return length;
	}
	
	public String getId() {
		return id;
	}
	
	public abstract void write(OutputStream out);
	public abstract boolean equal(PDFObject obj);
}
