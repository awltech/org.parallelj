package org.parallelj.internal.util.sm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface StateMachine {

	/**
	 * @return the enum that contains the list of possible states.
	 */
	Class<? extends Enum<?>> states();

	/**
	 * @return the transitions which have no effect.
	 */
	Transition[] transitions() default {};

}
