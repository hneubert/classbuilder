package classbuilder.demo.report.pdf;

import java.io.OutputStream;
import java.util.Vector;

public class PDFPage extends PDFObject {
	private int width;
	private int height;
	private PDFPages pages;
	private PDFContent content;
	private Vector<PDFObject> resources;
	
	public PDFPage() {
		resources = new Vector<PDFObject>();
		
		width = 595;
		height = 842;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setPages(PDFPages pages) {
		this.pages = pages;
	}
	
	public void setContent(PDFContent content) {
		this.content = content;
	}
	
	public String addResource(PDFObject obj) {
		int images = 0;
		int fonts = 0;
		int i;
		
		for (i = 0; i < resources.size(); i++) {
			if (obj.equal(resources.elementAt(i))) {
				return resources.elementAt(i).getId();
			}
			if (resources.elementAt(i) instanceof PDFFont) fonts++;
			if (resources.elementAt(i) instanceof PDFImage) images++;
		}
		
		resources.add(obj);
		
		if (obj instanceof PDFFont) obj.id = "/F" + (fonts + 1);
		if (obj instanceof PDFImage) obj.id = "/Im" + (images + 1);
		
		
		return obj.id;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public void write(OutputStream out) {
		String s, f, x;
		PDFObject obj;
		int i;
		
		f = "/Font << ";
		x = "/XObject << ";
		
		for (i = 0; i < resources.size(); i++) {
			obj = resources.elementAt(i);
			if (obj instanceof PDFFont) {
				f += obj.getId() + " " + obj.getIndex() + " 0 R ";
			} else if (obj instanceof PDFImage) {
				x += obj.getId() + " " + obj.getIndex() + " 0 R ";
			}
		}
		
		f += ">>";
		x += ">>";
		
		s = "obj << /Type /Page /MediaBox [0 0 " + width + " " + height + "] /Parent " + pages.getIndex() + " 0 R /Resources << " + f + " " + x + " >> /Contents " + content.getIndex() + " 0 R >> endobj";
		
		length = s.length();
		
		try {
			out.write(s.getBytes());
		} catch (Exception e) {
			
		}
	}
	
	public boolean equal(PDFObject obj) {
		return false;
	}
}
