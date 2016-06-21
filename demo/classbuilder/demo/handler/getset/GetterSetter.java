package classbuilder.demo.handler.getset;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import classbuilder.handler.Handler;

/**
 * Implements abstract getter and setter methods and defclare field, if required.
 */
@Target(value=ElementType.TYPE)
@Retention(value=RetentionPolicy.RUNTIME)
@Handler(GetterSetterHandler.class)
public @interface GetterSetter {
	
}
