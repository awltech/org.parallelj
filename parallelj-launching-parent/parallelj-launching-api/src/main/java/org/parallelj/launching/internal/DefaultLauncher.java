/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010, 2011, 2012 Atos Worldline or third-party contributors as
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
package org.parallelj.launching.internal;

import org.parallelj.internal.reflect.ProcessHelperImpl;
import org.parallelj.launching.Launch;
import org.parallelj.launching.LaunchException;
import org.parallelj.launching.LaunchResult;
import org.parallelj.launching.Launcher;
import org.parallelj.launching.errors.IProceduresOnError;
import org.parallelj.launching.errors.ProceduresOnError;

public class DefaultLauncher {

	Class<?> program;

	private static class ArgEntry {
		public String argumentName;
		public String stringValue;

		public ArgEntry(String arg) {
			int valIndex = arg.indexOf('=');
			this.argumentName = arg.substring(0, valIndex);
			this.stringValue = arg.substring(valIndex + 1);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String className = args[0];
		Class<?> programClass = null;
		Object programInstance = null;
		try {
			programClass = Class.forName(className);
			programInstance = programClass.newInstance();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		ArgEntry[] arguments = new ArgEntry[args.length - 1];
		try {
			for (int cpt = 1; cpt < args.length; cpt++) {
				arguments[cpt - 1] = new ArgEntry(args[cpt]);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		if (programInstance != null) {
			// Run the Program
			Launcher launcher = null;
			Launch<?> launch = null;
			try {
				launcher = Launcher.getLauncher();
				launch = launcher.newLaunch(programClass);
				for (ArgEntry entry : arguments) {
					launch.addParameter(entry.argumentName, entry.stringValue);
				}
				launch.synchLaunch();

				// Get the result of the launch and print Program status
				LaunchResult result = launch.getLaunchResult();
				System.out.println("Program status [" + result.getStatusCode()
						+ "] Return code [" + result.getReturnCode() + "]");

				// Get and sho Procedures on error if exists.
				ProcessHelperImpl<?> processhelper = (ProcessHelperImpl<?>) launch
						.getProcessHelper();
				ProceduresOnError procOnError = getProceduresInErrors(processhelper.getProcess());
				if (procOnError != null
						&& procOnError.getNumberOfProceduresInError() > 0) {
					System.err.println("Program terminated with errors: "
							+ procOnError);
				}
			} catch (LaunchException e) {
				e.printStackTrace();
			} finally {
			}
		}

	}

	public static ProceduresOnError getProceduresInErrors(
			org.parallelj.mirror.Process process) {
		return null;
	}

}
