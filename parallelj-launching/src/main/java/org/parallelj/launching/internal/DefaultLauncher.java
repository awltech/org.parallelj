package org.parallelj.launching.internal;

import org.parallelj.internal.reflect.ProcessHelperImpl;
import org.parallelj.launching.errors.ProceduresOnError;
import org.parallelj.launching.errors.ProceduresOnErrorManagement;
import org.parallelj.launching.quartz.Launch;
import org.parallelj.launching.quartz.LaunchException;
import org.parallelj.launching.quartz.Launcher;
import org.parallelj.launching.quartz.QuartzUtils;
import org.quartz.JobDataMap;

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
			Launch launch =  null;
			try {
				launcher = Launcher.getLauncher();
				launch = launcher.newLaunch(programClass);
				for (ArgEntry entry : arguments) {
					launch.addParameter(entry.argumentName, entry.stringValue);
				}
				launch.synchLaunch();
				
				// Get the result of the launch and print Program status
				JobDataMap jobDataMap = launch.getLaunchResult();
				System.out.println("Program status ["+jobDataMap.get(QuartzUtils.RETURN_CODE)+"] Return code ["+jobDataMap.get(QuartzUtils.USER_RETURN_CODE)+"]");
				
				// Get and sho Procedures on error if exists.
				ProcessHelperImpl<?> processhelper = (ProcessHelperImpl<?>)jobDataMap.get(QuartzUtils.CONTEXT);
				ProceduresOnError procOnError = ProceduresOnErrorManagement.getProceduresInErrors(processhelper.getProcess());
				if (procOnError != null && procOnError.getNumberOfProceduresInError()>0) {
					System.err.println("Program terminated with errors: "+procOnError);
				}
			} catch (LaunchException e) {
				e.printStackTrace();
			} finally {
				if (launcher != null) {
					launcher.complete();
				}
			}
		}

	}

}
