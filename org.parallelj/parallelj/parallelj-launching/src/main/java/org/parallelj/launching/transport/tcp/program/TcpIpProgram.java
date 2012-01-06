package org.parallelj.launching.transport.tcp.program;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.In;
import org.parallelj.launching.parser.Parser;


public class TcpIpProgram {
	private Class<? extends Adapter> adapterClass;
	
	private List<ArgEntry> argEntries = new ArrayList<ArgEntry>();
	
	public TcpIpProgram(Class<? extends Adapter> adapterClass) {
		this.adapterClass = adapterClass;

		// Search for annotation @In on attributes of
		// class clazz
		for (Field field : this.adapterClass.getDeclaredFields()) {
			// Search for an annotation @In
			for (Annotation annotation : field.getAnnotations()) {
				if (annotation.annotationType().equals(In.class)) {
					// Add the attribute where is the @In annotation
					// and
					// the Parser class
					Class<? extends Parser> parserClass = ((In) annotation)
							.parser();
					this.argEntries.add(new ArgEntry(field.getName(),
							field.getType(), parserClass));
				}
			}
		}

	}

	public List<ArgEntry> getArgEntries() {
		return argEntries;
	}

	public Class<? extends Adapter> getAdapterClass() {
		return adapterClass;
	}
	
	
}
