package classbuilder.demo.report.pdf;

import java.io.OutputStream;

public class PDFCatalog extends PDFObject {
	private PDFPages pages;
	
	public void setPages(PDFPages pages) {
		this.pages = pages;
	}
	
	@Override
	public int getLength() {
		String s;
		
		s = "obj << /Type /Catalog /Version /1.4 /Pages " + pages.getIndex() + " 0 R >> endobj";
		
		return s.length();
	}

	@Override
	public void write(OutputStream out) {
		String s;
		
		s = "obj << /Type /Catalog /Version /1.4 /Pages " + pages.getIndex() + " 0 R >> endobj";
		
		try {
			out.write(s.getBytes());
		} catch (Exception e) {
			
		}
	}
	
	public boolean equal(PDFObject obj) {
		if (obj instanceof PDFCatalog) {
			return true;
		} else {
			return false;
		}
	}
}
