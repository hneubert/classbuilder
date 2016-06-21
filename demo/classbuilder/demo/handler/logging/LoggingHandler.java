package classbuilder.demo.handler.logging;

import java.util.logging.Logger;

import classbuilder.BuilderException;
import classbuilder.IClass;
import classbuilder.IField;
import classbuilder.Variable;
import classbuilder.handler.AbstractProxyHandler;
import classbuilder.handler.ClassHandler;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;

// adds log messages before and after every method
public class LoggingHandler extends AbstractProxyHandler implements ClassHandler {
	// implements log messages
	@Override
	public void handle(HandlerContext context) throws BuilderException, HandlerException {
		// logger.info("enter <method name>");
		get("__logger").invoke("info", "enter " + getName());
		
		// Object result = super.<method>(<parameters>);
		Variable result = invoke((Object[])getParameters());
		
		// logger.info("leave <method name>");
		get("__logger").invoke("info", "leave " + getName());
		
		// return result;
		Return(result);
	}
	
	// adds the logger
	@Override
	public void handle(HandlerContext context, IClass cls) throws BuilderException, HandlerException {
		super.cls = cls;
		
		// private static Logger __logger;
		IField logger = addField(PRIVATE | STATIC, Logger.class, "__logger");
		
		// Logger initialisieren
		// static { __logger = Logger.getLogger("<class name>"); }
		method = cls.Static();
			get(logger).set($(Logger.class).invoke("getLogger", getSuperclass().getName()));
		End();
	}
}
