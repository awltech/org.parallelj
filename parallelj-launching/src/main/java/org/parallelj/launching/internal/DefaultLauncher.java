package org.parallelj.launching.internal;

import org.parallelj.Programs;

public class DefaultLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("********************");
		for (String string : args) {
			System.out.println(string);
			
		}
		System.out.println("********************");

		String className = args[0];
		try {
			Class<?> programClass = Class.forName(className);
			Object programInstance = programClass.newInstance();
			
			Programs.as(programInstance).execute().join();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	
	}
}
