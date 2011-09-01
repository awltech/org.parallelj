package org.parallelj;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To be placed on fields that will be part of the event log trace.
 * 
 * @see <a href="http://www.parallelj.org/confluence/display/core/Event+Log+Management">Event Log Management</a>
 * 
 * @author Laurent Legrand
 * @since 0.4.0
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute {

}
