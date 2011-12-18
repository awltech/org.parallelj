package org.parallelj.tracknrestart.databinding;

import java.lang.reflect.Field;

import org.parallelj.Program;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class ProgramFieldsBinder {

	public static void setProgramInputFields(Object prog, JobExecutionContext context)
			throws NoSuchFieldException, IllegalAccessException {
		Class<? extends Object> clazz = prog.getClass();
		if (clazz.isAnnotationPresent(Program.class)) {
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].isAnnotationPresent(In.class)) {
					boolean wasAccessible = fields[i].isAccessible();
					if (!wasAccessible) {
						fields[i].setAccessible(true);
					}
					fields[i].set(prog, context.getJobDetail().getJobDataMap().get(fields[i].getName()));
					if (!wasAccessible) {
						fields[i].setAccessible(false);
					}
				}
			}
		}
	}

	public static void getProgramOutputFields(Object prog, JobExecutionContext context)
			throws NoSuchFieldException, IllegalAccessException {
		Class<? extends Object> clazz = prog.getClass();
		if (clazz.isAnnotationPresent(Program.class)) {
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].isAnnotationPresent(Out.class)) {
					boolean wasAccessible = fields[i].isAccessible();
					if (!wasAccessible) {
						fields[i].setAccessible(true);
					}
					Object oResult = context.getResult();
					if (oResult instanceof JobDataMap){
						JobDataMap result = (JobDataMap) oResult;
						result.put(fields[i].getName(),fields[i].get(prog));
					}
					if (!wasAccessible) {
						fields[i].setAccessible(false);
					}
				}
			}
		}
	}


}
