package classbuilder.demo.report.pdf;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

public class PDFImage extends PDFObject {
	private int width;
	private int height;
	private BufferedImage image;
	
	@Override
	public int getLength() {
		return length;
	}

	@Override
	public void write(OutputStream out) {
		String s, c;
		int i, j, rgb;
		
		s = "obj << /Type /XObject /Subtype /Image /Width " + width + " /Height " + height + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Length " + (width * height * 6) + " /Filter /ASCIIHexDecode >> stream\n";
		for (i = 0; i < height; i++) {
			for (j = 0; j < width; j++) {
				rgb = image.getRGB(j, i);
				c = Integer.toHexString(rgb);
				if (c.length() > 6) {
					c = c.substring(2);
				}
				while (c.length() < 6) {
					c = "0" + c;
				}
				s += c;
			}
		}
		//s += "\n>endstream endobj";
		s += ">\nendstream endobj";
		
		length = s.length();
		
		try {
			out.write(s.getBytes());
		} catch (Exception e) {
			
		}
	}

	@Override
	public boolean equal(PDFObject obj) {
		if (obj instanceof PDFImage) {
			if (((PDFImage)obj).image == image) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void setImage(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();
		
		this.image = image;
	}
}
