package classbuilder.demo.handler.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import classbuilder.BuilderException;
import classbuilder.Variable;
import classbuilder.handler.AbstractProxyHandler;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;

// implements validation code
public class ValidationHandler extends AbstractProxyHandler {
	
	// implements validation code
	@Override
	public void handle(HandlerContext context) throws BuilderException, HandlerException {
		// get annotate method
		Method method = (Method)context.getAnnotatedElement();
		
		// get all parameter annotations
		Annotation[][] annotations = method.getParameterAnnotations();
		
		// test all parameters
		for (int i = 0; i < annotations.length; i++) {
			Validate validate = null;
			// find Validate annotation at parameter
			for (Annotation annotation : annotations[i]) {
				if (annotation.annotationType() == Validate.class) {
					validate = (Validate)annotation;
				}
			}
			
			// optinally use the method annotation for the first parameter
			if (validate == null && i == 0) {
				validate = (Validate)context.getAnnotation();
			}
			
			if (validate != null) {
				// get the parameter
				Variable param = getParameters()[0];
				
				if (!validate.nullable() && !param.getVarType().isPrimitive()) {
					// null test
					// if (param == null) {
					If(param.isNull());
						// throw new Exception();
						Throw(New(Exception.class));
					// }
					End();
				}
				
				// test parameter type
				if (isNumeric(param.getVarType())) {
					// numeric test
					// if (param < min || param > max) {
					If(param.less(validate.min()).or(param.greater(validate.max())));
						// throw new Exception();
						Throw(New(Exception.class));
					// }
					End();
				} else if (param.getVarType() == String.class && !".*".equals(validate.pattern())) {
					// string test
					// if (param != null && !param.matches("<pattern>")) {
					If(param.isNotNull().and(param.invoke("matches", validate.pattern()).not()));
						// throw new Exception();
						Throw(New(Exception.class));
					// }
					End();
				}
			}
		}
		
		Return(invoke((Object[])getParameters()));
	}
	
	// returns true, if type is numeric
	private boolean isNumeric(Class<?> type) {
		if (type == byte.class || 
				type == char.class || 
				type == short.class || 
				type == int.class || 
				type == long.class || 
				type == float.class || 
				type == double.class || 
				type == Byte.class || 
				type == Short.class || 
				type == Character.class || 
				type == Integer.class || 
				type == Long.class || 
				type == Float.class || 
				type == Double.class) {
			return true;
		} else {
			return false;
		}
	}
	
}
