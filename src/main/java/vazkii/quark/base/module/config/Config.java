package vazkii.quark.base.module.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

	String name() default "";
	String description() default "";
	String flag() default "";

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Restriction {
		String[] value();
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Min {
		double value();
		boolean exclusive() default false;
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Max {
		double value();
		boolean exclusive() default false;
	}
	
}
