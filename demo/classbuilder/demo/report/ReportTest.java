package classbuilder.demo.report;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import classbuilder.BuilderException;
import classbuilder.demo.report.impl.DocumentWriter;
import classbuilder.demo.report.impl.PdfDocumentWriter;
import classbuilder.demo.report.impl.ReportException;
import classbuilder.demo.report.impl.ReportFactory;
import classbuilder.demo.report.impl.WriterFactory;

public class ReportTest {
	
	// this demo shows a more complex example
	// see impl/ReportFactory.java for implementation
	// 1. a xml report will be compiled to a class
	// 2. the generated class writes a data bean to a report
	@Test
	public void reportTest() throws IOException, XMLStreamException, BuilderException, ReportException {
		// create a report fectory
		ReportFactory reportFactory = new ReportFactory();
		
		// register the report format and a bean type
		reportFactory.registerReport("report", new FileInputStream("demo/classbuilder/demo/report/example.xml"), TestBean.class);
		
		// open output file
		DocumentWriter out = new PdfDocumentWriter(new FileOutputStream("reportTest.pdf"), WriterFactory.A4);
		
		// create test data
		TestBean testBean = new TestBean();
		
		// write the report
		reportFactory.execute("report", out, testBean);
		
		// close the output file
		out.close();
	}
	
	// writes a simple pdf
	@Test
	public void writerTest() throws IOException, XMLStreamException, BuilderException {
		// creaze a document writer
		DocumentWriter out = new PdfDocumentWriter(new FileOutputStream("writerTest.pdf"), WriterFactory.A4);
		
		// load an image
		BufferedImage image = ImageIO.read(ReportTest.class.getResourceAsStream("test.png"));
		
		// write some text
		out.writeLine("Line 1");
		out.write("Line 2 ");
		out.write("Line 2");
		out.writeLine();
		
		// write more text
		out.write("Line 3");
		out.moveTo(25, 0);
		out.write("Line 3");
		out.moveTo(25, 0);
		out.write("Line 3");
		
		// draw an image
		out.writeLine();
		out.drawImage(image);
		
		// close the document
		out.close();
	}
	
}
