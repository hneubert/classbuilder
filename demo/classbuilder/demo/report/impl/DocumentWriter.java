package classbuilder.demo.report.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface DocumentWriter {
	
	public static final float CM_PER_INCH = 2.54f;
	
	public void setupFont(String name, int size);
	
	public void moveTo(float x, float y);
	
	public void write(Object value);
	public void writeLine();
	public void writeLine(Object value);
	
	public void drawImage(BufferedImage image);
	
	public void close() throws IOException;
	
}
