package classbuilder.demo.report.pdf;

import java.io.OutputStream;

public class PDFContent extends PDFObject {
	private String value;
	
	public PDFContent() {
		value = "";
	}

	@Override
	public int getLength() {
		String s;
		
		s = "obj << /Length " + value.length() + " >> stream\n" + value + "\nendstream endobj";
		
		return s.length();
	}

	@Override
	public void write(OutputStream out) {
		String s;
		
		s = "obj << /Length " + value.length() + " >> stream\n" + value + "\nendstream endobj";
		
		try {
			out.write(s.getBytes());
		} catch (Exception e) {
			
		}
	}

	@Override
	public boolean equal(PDFObject obj) {
		return false;
	}
	
	public void add(String s) {
		value += s + " ";
	}
}
