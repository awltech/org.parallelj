package org.parallelj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To be placed on classes that contains fields annotated by {@link Attribute}.
 * 
 * Optional for classes already annotated by {@link Program}.
 * 
 * @author Laurent Legrand
 * @since 0.4.0
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Executable {

}
