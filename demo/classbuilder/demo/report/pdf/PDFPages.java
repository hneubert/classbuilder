package classbuilder.demo.report.pdf;

import java.io.OutputStream;
import java.util.Vector;

public class PDFPages extends PDFObject {
	private Vector<PDFPage> pages;
	
	public PDFPages() {
		pages = new Vector<PDFPage>();
	}
	
	public void addPage(PDFPage page) {
		pages.add(page);
	}
	
	@Override
	public int getLength() {
		String s, kids = "";
		int i;
		
		for (i = 0; i < pages.size(); i++) {
			kids += pages.elementAt(i).getIndex() + " 0 R ";
		}
		
		s = "obj << /Type /Pages /Kids [" + kids + "] /Count " + pages.size() + " >> endobj";
		
		return s.length();
	}

	@Override
	public void write(OutputStream out) {
		String s, kids = "";
		int i;
		
		for (i = 0; i < pages.size(); i++) {
			kids += pages.elementAt(i).getIndex() + " 0 R ";
		}
		
		s = "obj << /Type /Pages /Kids [" + kids + "] /Count " + pages.size() + " >> endobj";
		
		try {
			out.write(s.getBytes());
		} catch (Exception e) {
			
		}
	}

	@Override
	public boolean equal(PDFObject obj) {
		if (obj instanceof PDFPages) {
			return true;
		} else {
			return false;
		}
	}
}
