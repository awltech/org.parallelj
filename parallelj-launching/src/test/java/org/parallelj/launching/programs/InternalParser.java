package org.parallelj.launching.programs;

import org.parallelj.launching.parser.Parser;

public class InternalParser implements Parser {

	@Override
	public Object parse(String value) {
		InternalClass ic = new InternalClass();
		ic.value = "in_"+value;
		return ic;
	}
}

