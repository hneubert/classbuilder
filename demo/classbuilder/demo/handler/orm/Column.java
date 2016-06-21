package classbuilder.demo.handler.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps a column to a field.
 */
@Target(value=ElementType.FIELD)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Column {
	
	/**
	 * Returns the column name.
	 * @return column name
	 */
	public String name();
	
	/**
	 * Returns true, if the column is part of the primary key.
	 * @return true, if is primary key
	 */
	public boolean pk() default false;
}
