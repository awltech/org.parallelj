/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010 Atos Worldline or third-party contributors as
 *     indicated by the @author tags or express copyright attribution
 *     statements applied by the authors.
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.parallelj.launching.transport.tcp.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.parallelj.internal.conf.CBeans.Bean;
import org.parallelj.internal.conf.ParalleljConfiguration;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.In;
import org.parallelj.launching.parser.Parser;
import org.parallelj.launching.transport.ArgEntry;

/**
 * ListProgram TcpCommand
 *
 */
public class ListPrograms extends AbstractTcpCommand {

	private static final int PRIORITY=90;

	/**
	 * Represents an available program to be print to the client
	 *
	 */
	static final class ListEntry {
		private ListEntry(String program, List<ArgEntry> args) {
			this.program = program;
			this.args = args;
		}

		private String program;
		private List<ArgEntry> args;
		
		public String getProgram() {
			return program;
		}
		public List<ArgEntry> getArgs() {
			return args;
		}
		
	}

	private List<ListEntry> listEntries = new ArrayList<ListPrograms.ListEntry>();

	/**
	 * Find and list all available Program for remote launching
	 */
	public ListPrograms() {
		// Search for all available Program and print it's name and parameters
		// Available Programs are defined in parallej.xml as MBeans
		ParalleljConfiguration configuration = ParalleljConfigurationManager
				.getConfiguration();
		if (configuration.getServers().getBeans() != null
				&& configuration.getServers().getBeans().getBean() != null) {
			for (Bean bean : configuration.getServers().getBeans().getBean()) {
				//
				/*
				 * List of types annotated with @In and its Parser class:
				 * adapterArgs[0] : the attribute name adapterArgs[1] : the
				 * canonical name of the corresponding parser class
				 */
				List<ArgEntry> adapterArgs = new ArrayList<ArgEntry>();
				try {
					@SuppressWarnings("unchecked")
					Class<? extends Adapter> clazz = (Class<? extends Adapter>) Class
							.forName(bean.getClazz());
					// Search for annotation @In on attributes of
					// class clazz
					for (Field field : clazz.getDeclaredFields()) {
						// Search for an annotation @In
						for (Annotation annotation : field.getAnnotations()) {
							if (annotation.annotationType().equals(In.class)) {
								// Add the attribute where is the @In annotation
								// and
								// the Parser class
								Class<? extends Parser> parserClass = ((In) annotation)
										.parser();
								adapterArgs.add(new ArgEntry(field.getName(),
										field.getType(), parserClass));
							}
						}
					}

					//
					this.listEntries.add(new ListEntry(
							clazz.getCanonicalName(), adapterArgs));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#process(org.apache.mina.core.session.IoSession, java.lang.String[])
	 */
	@Override
	public final String process(IoSession session, String... args) {
		StringBuffer stb = new StringBuffer();
		for (int index = 0; index < this.listEntries.size(); index++) {
			ListEntry listEntry = this.listEntries.get(index);
			stb.append("id:").append(index).append(" ").append(listEntry.program)
					.append(" args:[");
			for (ArgEntry argEntry : listEntry.args) {
				stb.append(argEntry.getName()).append(":")
						.append(argEntry.getType().getCanonicalName())
						.append(" ");
			}
			stb.append("]\n\r");
		}
		session.write(stb.toString());
		return null;
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getType()
	 */
	public String getType() {
		return RemoteCommand.list.name();
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getUsage()
	 */
	@Override
	public String getUsage() {
		return "                            list : Lists available programs and their associated IDs.";
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getPriorityUsage()
	 */
	@Override
	public int getPriorityUsage() {
		return PRIORITY;
	}

}
