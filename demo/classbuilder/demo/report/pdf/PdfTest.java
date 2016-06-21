package classbuilder.demo.report.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class PdfTest {
	
	@Test
	public void test() throws IOException {
		FileOutputStream out;
		PDFDocument doc;
		
		doc = new PDFDocument();
		
		doc.addPage(595, 842);
		
		new File("test.pdf").delete();
		out = new FileOutputStream("test.pdf");
		
		doc.setFont(PDFFonts.Helvetica);
		doc.setFontSize(16);
		doc.write(10, 10, "Hallo");
		
		BufferedImage image = ImageIO.read(new File("demo/classbuilder/demo/report/test.png"));
		doc.write(10, 50, image.getWidth(), image.getHeight(), image);
		
		doc.write(out);
	}
	
}
