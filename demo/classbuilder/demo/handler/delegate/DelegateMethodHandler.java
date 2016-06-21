package classbuilder.demo.handler.delegate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import classbuilder.BuilderException;
import classbuilder.handler.AbstractMethodHandler;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;
import classbuilder.handler.MethodFilter;
import classbuilder.handler.MethodId;
import classbuilder.handler.MethodSelector;

/**
 * Implements delegate methods for a field or getter method.
 */
public class DelegateMethodHandler extends AbstractMethodHandler implements MethodSelector {
	
	// implements getter methods
	@Override
	public void handle(HandlerContext context) throws BuilderException {
		if (context.getAnnotatedElement() instanceof Field) {
			// @Delegate at field
			
			Field field = (Field)context.getAnnotatedElement();
			if (getReturnType() != null) {
				// implements a method with return value
				// return delegate.anyMethod(param1, param2);
				Return(get(field).invoke(getName(), (Object[])getParameters()));
			} else {
				// implements a method without return value
				// delegate.anyMethod(param1, param2);
				get(field).invoke(getName(), (Object[])getParameters());
			}
		} else if (context.getAnnotatedElement() instanceof Method) {
			// @Delegate at getter
			
			Method method = (Method)context.getAnnotatedElement();
			if (getReturnType() != null) {
				// implements a method with return value
				// return getDelegate().anyMethod(param1, param2);
				Return(invoke(method).invoke(getName(), (Object[])getParameters()));
			} else {
				// implements a method without return value
				// getDelegate().anyMethod(param1, param2);
				invoke(method).invoke(getName(), (Object[])getParameters());
			}
		}
	}
	
	// Erstellung der Liste der zu delegierenden Methoden
	@Override
	public Collection<MethodId> getMethods(HandlerContext handlerContext) throws HandlerException {
		// @Delegate must be present
		if (!(handlerContext.getAnnotation() instanceof Delegate)) {
			throw new HandlerException("@Delegate annotation not present");
		}
		
		// get @Delegate annotation
		Delegate delegate = (Delegate)handlerContext.getAnnotation();
		Class<?>[] interfaces = delegate.value();
		
		// get annotated member
		Class<?> type;
		if (handlerContext.getAnnotatedElement() instanceof Field) {
			// annotated field
			type = ((Field)handlerContext.getAnnotatedElement()).getType();
		} else if (handlerContext.getAnnotatedElement() instanceof Method) {
			// annotated getter
			Method method = (Method)handlerContext.getAnnotatedElement();
			type = method.getReturnType();
			
			// there are no arguments allowed
			if (method.getParameterTypes().length != 0) {
				new HandlerException("the delegate-getter must not have parameters");
			}
		} else {
			// invalid member
			throw new HandlerException("invalid delgate target: " + handlerContext.getAnnotatedElement().getClass().getName());
		}
		
		// check delegate type
		if (type == null || type.isPrimitive() || type.isArray()) {
			new HandlerException("invalid delegate type: " + type);
		}
		
		// specified interfaces limits delegate methods
		if (interfaces.length == 0) {
			interfaces = new Class<?>[] {type};
		}
		
		final Set<MethodId> set = new HashSet<MethodId>();
		for (Class<?> intf : interfaces) {
			for (Method method : intf.getMethods()) {
				set.add(MethodId.getMethodId(method));
			}
		}
		
		// create the list of delegate methods
		return MethodId.getMethods(handlerContext, delegate.override(), new MethodFilter() {
			@Override
			public boolean checkMethod(MethodId method) {
				return set.contains(method);
			}
		});
	}
}
