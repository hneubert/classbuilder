package classbuilder.demo.report.pdf;

import java.io.OutputStream;

public class PDFFont extends PDFObject {
	private PDFFonts font;
	
	private String format() {
		String value;
		
		value = "obj << /Type /Font /Subtype /Type1 /BaseFont ";
		value = font.getName();
		value += " >> endobj";
		
		return value;
	}
	
	public PDFFont() {

	}
	
	public void setFont(PDFFonts font) {
		this.font = font;
	}
	
	public PDFFonts getFont() {
		return font;
	}
	
	@Override
	public int getLength() {
		String s;
		
		s = format();
		
		return s.length();
	}

	@Override
	public void write(OutputStream out) {
		String s;
		
		s = format();
		
		try {
			out.write(s.getBytes());
		} catch (Exception e) {
			
		}
	}
	
	public boolean equal(PDFObject obj) {
		if (obj instanceof PDFFont) {
			if (((PDFFont)obj).font == font) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
