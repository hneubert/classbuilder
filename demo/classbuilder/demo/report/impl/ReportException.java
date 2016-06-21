package classbuilder.demo.report.impl;

public class ReportException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ReportException(String message) {
		super(message);
	}
	
	public ReportException(String message, Throwable exception) {
		super(message, exception);
	}
	
}
