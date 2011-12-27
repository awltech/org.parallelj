package org.parallelj.tracknrestart.test.quartz.pjj.parsers;

import java.util.Arrays;
import java.util.List;

import org.parallelj.launching.parser.Parser;


public class ListStringParser implements Parser {

	@Override
	public Object parse(String value) {
		String[] array = value.split(",");
		List<String> result = Arrays.asList(array);
		return (List<String>)result;
	}

}
