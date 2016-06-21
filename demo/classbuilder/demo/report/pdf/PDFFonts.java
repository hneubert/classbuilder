package classbuilder.demo.report.pdf;

public enum PDFFonts {
	Times_Roman("Times-Roman"),
	Helvetica("Helvetica"),
	Courier("Courier"),
	Symbol("Symbol"),
	Times_Bold("Times-Bold"),
	Helvetica_Bold("Helvetica-Bold"),
	Courier_Bold("Courier-Bold"),
	ZapfDingbats("ZapfDingbats"),
	Times_Italic("Times-Italic"),
	Helvetica_Oblique("Helvetica-Oblique"),
	Courier_Oblique("Courier-Oblique"),
	Times_BoldItalic("Times-BoldItalic"),
	Helvetica_BoldOblique("Helvetica-BoldOblique"),
	Courier_BoldOblique("Courier-BoldOblique");
	
	private String name;
	
	PDFFonts(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
