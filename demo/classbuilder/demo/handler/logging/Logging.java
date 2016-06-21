package classbuilder.demo.handler.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import classbuilder.handler.Handler;

/**
 * Adds log messages before and after ever method call.
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Handler(LoggingHandler.class)
public @interface Logging {

}
