package classbuilder.demo.report.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import classbuilder.demo.report.impl.WriterFactory.LayoutParams;
import classbuilder.demo.report.pdf.PDFDocument;
import classbuilder.demo.report.pdf.PDFFonts;

public class PdfDocumentWriter implements DocumentWriter  {
	private OutputStream out;
	private PDFDocument doc;
	private StringBuilder builder;
	
	private float factor;
	
	private float left;
	private float top;
	
	private int size;
	
	private int width;
	private int height;
	
	private int borderLeft;
//	private int borderRight;
	private int borderTop;
	private int borderBottom;
	
	public PdfDocumentWriter(OutputStream out, LayoutParams layoutParams) {
		this.out = out;
		doc = new PDFDocument();
		
		switch (layoutParams.getUnit()) {
		case MM :
			factor = 7.1f / CM_PER_INCH;
			break;
		case CM :
			factor = 71 / CM_PER_INCH;
			break;
		case INCH :
			factor = 71;
			break;
		}
		
		size = 14;
		doc.setFontSize(size);
		
		width = (int)(layoutParams.getWidth() * factor);
		height = (int)(layoutParams.getHeight() * factor);
		doc.addPage(width, height);
		
		borderLeft = (int)(layoutParams.getBorderLeft() * factor);
//		borderRight = 60;
		borderTop = (int)(layoutParams.getBorderTop() * factor);
		borderBottom = (int)(layoutParams.getBorderBottom() * factor);
		
		left = borderLeft;
		top = height - borderTop - size;
		
		builder = new StringBuilder();
		doc.setFont(PDFFonts.Helvetica);
	}
	
	@Override
	public void write(Object value) {
		builder.append(value.toString());
	}
	
	@Override
	public void writeLine() {
		if (builder.length() > 0) {
			doc.write((int)left, (int)top, builder.toString());
			builder = new StringBuilder();
		}
		top -= size;
		left = borderLeft;
	}
	
	@Override
	public void writeLine(Object value) {
		write(value.toString());
		writeLine();
	}
	
	@Override
	public void drawImage(BufferedImage image) {
		doc.write((int)left, (int)(top + size - image.getHeight()), image.getWidth(), image.getHeight(), image);
		top -= image.getHeight();
	}
	
	@Override
	public void setupFont(String name, int size) {
		this.size = size;
	}
	
	@Override
	public void moveTo(float x, float y) {
		if (builder.length() > 0) {
			doc.write((int)left, (int)top, builder.toString());
			builder = new StringBuilder();
		}
		
		left += x * factor;
		top -= y * factor;
		
		if (top < borderBottom) {
			doc.addPage(width, height);
			top = height - borderTop - size;
		}
	}
	
	public void close() throws IOException {
		if (builder.length() > 0) {
			doc.write((int)left, (int)top, builder.toString());
		}
		doc.write(out);
		out.close();
	}
}
