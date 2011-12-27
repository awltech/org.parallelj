package org.parallelj.tracknrestart.test.quartz.pjj.parsers;

import java.util.Arrays;
import java.util.List;

import org.parallelj.launching.parser.Parser;
import org.parallelj.tracknrestart.test.quartz.pjj.flow.runnable.People;


public class ListPeopleParser implements Parser {

	@Override
	public Object parse(String value) {
		String[] arrayOfString = value.split(",");
		People[] array = new People[arrayOfString.length%2];
		for (int i = 0; i < arrayOfString.length; i=i+2) {
			array[i].setForname(arrayOfString[i]);
			array[i].setLastname(arrayOfString[i+1]);
		}
		List<People> result = Arrays.asList(array);
		return (List<People>)result;
	}
}
