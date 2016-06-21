package classbuilder.demo.report.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import classbuilder.BuilderException;
import classbuilder.ClassFactory;
import classbuilder.IClass;
import classbuilder.IMethod;
import classbuilder.Variable;

public class ReportFactory {
	
	public interface Report {
		public void execute(ReportFactory factory, DocumentWriter out, Object bean);
	}
	
	private XMLInputFactory factory;
	private ClassFactory classFactory;
	private HashMap<String, Report> reportMap;
	private int counter;
	
	public ReportFactory() {
		factory = XMLInputFactory.newFactory();
		classFactory = new ClassFactory();
		reportMap = new HashMap<String, Report>();
		counter = 0;
	}
	
	public void execute(String report, DocumentWriter out, Object bean) throws ReportException {
		Report r = reportMap.get(report);
		if (r == null) {
			throw new ReportException("unkonwn report: " + report);
		}
		reportMap.get(report).execute(this, out, bean);
	}
	
	public void registerReport(String name, InputStream in, Class<?> type) throws IOException, XMLStreamException, BuilderException, ReportException {
		XMLStreamReader reader = factory.createXMLStreamReader(in);
		registerReport(name, reader, type);
	}
	
	private void registerReport(String name, XMLStreamReader in, Class<?> type) throws IOException, XMLStreamException, BuilderException, ReportException {
		IClass cls = classFactory.createClass(IClass.PUBLIC, "report", "Report_" + counter++, Object.class, Report.class);
			IMethod method = cls.addMethod(IClass.PUBLIC, "execute", ReportFactory.class, DocumentWriter.class, Object.class);
				Variable factory = method.getParameter(0);
				Variable doc = method.getParameter(1);
				Variable bean = method.addVar(type);
				bean.set(method.getParameter(2).cast(type));
				
				while (in.getEventType() != XMLStreamReader.END_DOCUMENT && in.getEventType() != XMLStreamReader.END_ELEMENT) {
					switch (in.getEventType()) {
					case XMLStreamReader.START_ELEMENT :
						if ("text".equals(in.getName().getLocalPart())) {
							float left = getFloat(in, "left", 0);
							if (left != 0) {
								doc.invoke("moveTo", left, 0);
							}
							String text = "";
							in.next();
							while (in.getEventType() != XMLStreamReader.END_DOCUMENT && in.getEventType() != XMLStreamReader.END_ELEMENT) {
								text += in.getText();
								in.next();
							}
							doc.invoke("write", text);
						} else if ("value".equals(in.getName().getLocalPart())) {
							float left = getFloat(in, "left", 0);
							if (left != 0) {
								doc.invoke("moveTo", left, 0);
							}
							doc.invoke("write", bean.invoke(getPropertyName(in)));
						} else if ("br".equals(in.getName().getLocalPart())) {
							doc.invoke("writeLine");
						} else if ("image".equals(in.getName().getLocalPart())) {
							Variable image = method.addVar(BufferedImage.class);
							String url = in.getAttributeValue(null, "url");
							if (url == null) {
								throw new ReportException("image-tag requires an url");
							} else if (url.startsWith("classpath://")) {
								url = ReportFactory.class.getResource(url.replace("classpath:/", "")).toString();
							}
							image.set(method.$(ImageIO.class).invoke("read", method.New(URL.class, url).invoke("openStream")));
							doc.invoke("drawImage", image);
						} else if ("subreport".equals(in.getName().getLocalPart())) {
							String id = UUID.randomUUID().toString();
							Method getter = getPropertyType(in, type);
							String property = getPropertyName(in);
							if (Collection.class.isAssignableFrom(getter.getReturnType())) {
								in.next();
								registerReport(id, in, (Class<?>)((ParameterizedType)getter.getGenericReturnType()).getActualTypeArguments()[0]);
								Variable entry = method.ForEach(bean.invoke(property));
									factory.invoke("execute", id, doc, entry);
								method.End();
							} else {
								registerReport(id, in, getter.getReturnType());
								factory.invoke("execute", id, doc, bean.invoke(getPropertyName(in)));
							}
						} else if ("report".equals(in.getName().getLocalPart())) {
							break;
						}
						while (in.getEventType() != XMLStreamReader.END_DOCUMENT && in.getEventType() != XMLStreamReader.END_ELEMENT) {
							in.next();
						}
						break;
					}
					in.next();
				}
			method.End();
		try {
			reportMap.put(name, (Report)cls.build().newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private float getFloat(XMLStreamReader in, String id, float defaultValue) {
		String left = in.getAttributeValue(null, id);
		if (left != null) {
			return Float.parseFloat(left);
		} else {
			return defaultValue;
		}
	}
	
	private String getPropertyName(XMLStreamReader in) {
		String name = in.getAttributeValue(null, "name");
		return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	private Method getPropertyType(XMLStreamReader in, Class<?> type) {
		String name = in.getAttributeValue(null, "name");
		Method method;
		try {
			method = type.getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
			return method;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
}
