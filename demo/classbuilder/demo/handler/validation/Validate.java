package classbuilder.demo.handler.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import classbuilder.handler.Handler;

/**
 * Validates method parameters.
 */
@Target(value={ElementType.METHOD, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@Handler(ValidationHandler.class)
public @interface Validate {
	
	/**
	 * Returns the minimal allowed value.
	 * @return minimal allowed value
	 */
	public double min() default Double.NEGATIVE_INFINITY;
	
	/**
	 * Returns the maximal allowed value.
	 * @return maximal allowed value
	 */
	public double max() default Double.POSITIVE_INFINITY;
	
	/**
	 * Returns the allowd regex pattern.
	 * @return allowed regex pattern
	 */
	public String pattern() default ".*";
	
	/**
	 * Returns true, if null is allowed.
	 * @return true, if null is allowed
	 */
	public boolean nullable() default true;
}
