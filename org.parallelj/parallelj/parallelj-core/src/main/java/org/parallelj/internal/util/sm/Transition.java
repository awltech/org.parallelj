package org.parallelj.internal.util.sm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Transition {

	TransitionKind kind() default TransitionKind.EXTERNAL;

	String source();

	String target();

	String[] triggers();

	String guard() default "";

}