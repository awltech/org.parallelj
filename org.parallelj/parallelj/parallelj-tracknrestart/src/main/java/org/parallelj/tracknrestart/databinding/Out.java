package org.parallelj.tracknrestart.databinding;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.parallelj.launching.parser.NopParser;
import org.parallelj.launching.parser.Parser;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Out {
	Class<? extends Parser> parser() default NopParser.class;}
