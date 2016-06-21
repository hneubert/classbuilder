package classbuilder.demo.handler.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import classbuilder.handler.Handler;

/**
 * Maps a database table to a class.
 */
@Handler(OrmHandler.class)
@Target(value=ElementType.TYPE)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Table {
	
	/**
	 * Returns the table name.
	 * @return table name
	 */
	public String value();
}
