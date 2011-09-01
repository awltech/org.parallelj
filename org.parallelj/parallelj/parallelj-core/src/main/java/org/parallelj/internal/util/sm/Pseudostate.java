package org.parallelj.internal.util.sm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Pseudostate {

	PseudostateKind kind();

}
