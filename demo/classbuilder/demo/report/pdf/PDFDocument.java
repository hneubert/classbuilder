package classbuilder.demo.report.pdf;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Vector;

public class PDFDocument {
	private Vector<PDFObject> vo;
	
	private PDFCatalog catalog;
	private PDFPages pages;
	private PDFPage page;
	private PDFContent content;
	
	private PDFFonts font;
	private int size;
	
	public PDFDocument() {
		vo = new Vector<PDFObject>();
		
		catalog = new PDFCatalog();
		catalog = (PDFCatalog)addObject(catalog);
		
		pages = new PDFPages();
		pages = (PDFPages)addObject(pages);
		catalog.setPages(pages);
	}
	
	public PDFPage addPage(int width, int height) {
		page = new PDFPage();
		page = (PDFPage)addObject(page);
		pages.addPage(page);
		
		content = new PDFContent();
		content = (PDFContent)addObject(content);
		page.setPages(pages);
		page.setContent(content);
		page.setWidth(width);
		page.setHeight(height);
		
		return page;
	}
	
	public PDFObject addObject(PDFObject obj) {
		int i;
		
		for (i = 0; i < vo.size(); i++) {
			if (obj.equal(vo.elementAt(i))) {
				return vo.elementAt(i);
			}
		}
		
		vo.add(obj);
		obj.setIndex(vo.size());
		
		return obj;
	}
	
	public void write(OutputStream out) {
		int i, j, offset;
		String s;
		
		try {
			offset = 9;
			out.write("%PDF-1.4 \n".getBytes());
			
			for (i = 0; i < vo.size(); i++) {
				s = " " + (i + 1) + " 0 ";
				out.write(s.getBytes());
				vo.elementAt(i).write(out);
			}
			
			s = " xref 0 " + (vo.size() + 1) + " ";
			out.write(s.getBytes());
			out.write("0000000000 65535 f\r\n".getBytes());
			for (i = 0; i < vo.size(); i++) {
				s = " " + (i-1) + " 0 ";
				offset += s.length();
				s = Integer.toString(offset-4);
				for (j = s.length(); j < 10; j++) {
					s = "0" + s;
				}
				s += " 00000 n\r\n";
				out.write(s.getBytes());
				offset += vo.elementAt(i).getLength();
			}
			
			s = "trailer << /Size 8 /Root 1 0 R >> startxref " + offset + " %%EOF";
			out.write(s.getBytes());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setFont(PDFFonts font) {
		this.font = font;
	}
	
	public void setFontSize(int size) {
		this.size = size;
	}
	
	public void write(int x, int y, String text) {
		PDFFont font;
		String s, res;
		
		font = new PDFFont();
		font.setFont(this.font);
		font = (PDFFont)addObject(font);
		
		res = page.addResource(font);
		
		s = "BT " + res + " " + size + " Tf " + x + " " + y + " Td " + size + " Tr " /*+ r + " " + g + " " + b + " RG*/ + " (" + text + ") Tj ET";
		
		content.add(s);
	}
	
	public void write(int x, int y, int w, int h, BufferedImage image) {
		PDFImage img;
		String s, res;
		
		img = new PDFImage();
		img.setImage(image);
		img = (PDFImage)addObject(img);
		
		res = page.addResource(img);
		
		s = "q " + w + " 0 0 " + h + " " + x + " " + y + " cm " + res + " Do Q";
		
		content.add(s);
	}
}
