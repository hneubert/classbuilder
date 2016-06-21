package classbuilder.demo.handler.getset;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

import classbuilder.BuilderException;
import classbuilder.handler.AbstractMethodHandler;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;
import classbuilder.handler.MethodId;
import classbuilder.handler.MethodSelector;

// implements getter/setter methods
public class GetterSetterHandler extends AbstractMethodHandler implements MethodSelector {
	
	// implements getter/setter methods
	@Override
	public void handle(HandlerContext context) throws BuilderException, HandlerException {
		String field = Introspector.decapitalize(getName().substring(3));
		boolean exists = true;
		
		// exists a protected or public field?
		Class<?> cls = getSuperclass();
		exists = false;
		while (cls != null) {
			try {
				Field f = cls.getDeclaredField(field);
				if ((f.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0) {
					exists = true;
				}
				break;
			} catch (NoSuchFieldException e1) {
				
			} catch (SecurityException e1) {
				
			}
			cls = cls.getSuperclass();
		}
		
		if (getName().startsWith("get")) {
			// create field
			if (!exists) {
				addField(PROTECTED, getReturnType(), field);
			}
			
			// implement getter:
			// return field;
			Return(get(field));
		} else if (getName().startsWith("set")) {
			// create field
			if (!exists) {
				addField(PROTECTED, getParameterTypes()[0], field);
			}
			
			// implemt setter:
			// field = value;
			get(field).set(getParameter(0));
		}
	}
	
	// filter getter and setter methods
	@Override
	public Collection<MethodId> getMethods(HandlerContext handlerContext) throws HandlerException {
		// get all abstract methods
		Collection<MethodId> methods = MethodId.getMethods(handlerContext, false);
		
		// find getter and setter methods
		Collection<MethodId> result = new ArrayList<MethodId>();
		for (MethodId methodId : methods) {
			if (methodId.getName().startsWith("get") && methodId.getTypes().length == 0 && 
					methodId.getReturnType() != null && methodId.getReturnType() != void.class) {
				// method signature: <type> get<name>();
				result.add(methodId);
			} else if (methodId.getName().startsWith("set") && methodId.getTypes().length == 1 && 
					(methodId.getReturnType() == null || methodId.getReturnType() == void.class)) {
				// method signature: void get<name>(<type> value);
				result.add(methodId);
			}
		}
		
		// methods
		return result;
	}
}
