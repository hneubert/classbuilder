package classbuilder.demo.report.impl;

import java.io.OutputStream;

public class WriterFactory {
	
	public static final String PDF = "pdf";
	
	public static final LayoutParams A4 = new LayoutParams(21, 29.7f, 3, 3, 3, 3, Unit.CM);
	
	public enum Unit {
		CM,
		MM,
		INCH
	}
	
	public static class LayoutParams {
		private float width;
		private float height;
		private float borderLeft;
		private float borderRight;
		private float borderTop;
		private float borderBottom;
		private Unit unit;
		
		public LayoutParams(float width, float height, float borderLeft, float borderRight, float borderTop, float borderBottom, Unit unit) {
			this.width = width;
			this.height = height;
			this.borderLeft = borderLeft;
			this.borderRight = borderRight;
			this.borderTop = borderTop;
			this.borderBottom = borderBottom;
			this.unit = unit;
		}
		
		public float getWidth() {
			return width;
		}
		public void setWidth(float width) {
			this.width = width;
		}
		public float getHeight() {
			return height;
		}
		public void setHeight(float height) {
			this.height = height;
		}
		public float getBorderLeft() {
			return borderLeft;
		}
		public void setBorderLeft(float borderLeft) {
			this.borderLeft = borderLeft;
		}
		public float getBorderRight() {
			return borderRight;
		}
		public void setBorderRight(float borderRight) {
			this.borderRight = borderRight;
		}
		public float getBorderTop() {
			return borderTop;
		}
		public void setBorderTop(float borderTop) {
			this.borderTop = borderTop;
		}
		public float getBorderBottom() {
			return borderBottom;
		}
		public void setBorderBottom(float borderBottom) {
			this.borderBottom = borderBottom;
		}
		public Unit getUnit() {
			return unit;
		}
		public void setUnit(Unit unit) {
			this.unit = unit;
		}
	}
	
	public DocumentWriter newWriter(OutputStream out, String format, LayoutParams layoutParams) {
		if (PDF.equals(format)) {
			return new PdfDocumentWriter(out, layoutParams);
		} else {
			return null;
		}
	}
}
