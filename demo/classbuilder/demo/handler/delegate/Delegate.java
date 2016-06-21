package classbuilder.demo.handler.delegate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import classbuilder.handler.Handler;

/**
 * The Delegate annotation triggers the generation of delegate methods for the annotated member.<br>
 * <br>
 * Example:<br>
 * in this example were delegate methods for each method in IBar generated.
 * public class Foo implements IBar {
 *     {@literal @}Delegate
 *     protected IBar bar;
 * }
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Handler(DelegateMethodHandler.class)
public @interface Delegate {
	/**
	 * Interfaces which will be implementd.
	 * @return interfaces
	 */
	public Class<?>[] value() default {};
	
	/**
	 * True, if existing mothod should be overriden.
	 * @return true, if existing mothod should be overriden
	 */
	public boolean override() default false;
}
