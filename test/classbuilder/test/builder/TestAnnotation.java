package classbuilder.test.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestAnnotation {
	
	public String value();
	public int num() default 42;
	
	public int[] array() default {};
	public ElementType enumValue() default ElementType.TYPE;
	public Class<?> classValue() default Class.class;
	
	@Target({ElementType.ANNOTATION_TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface SubAnnotation {
		public String value() default "";
	}
	
	public SubAnnotation sub() default @SubAnnotation;
	public SubAnnotation[] subArray() default {};
}
