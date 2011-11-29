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

package org.parallelj.launching.transport.jmx;

import static org.quartz.JobBuilder.newJob;

import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;

import org.apache.commons.lang3.ArrayUtils;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.parser.NopParser;
import org.parallelj.launching.quartz.AdapterJobsRunner;
import org.parallelj.launching.quartz.ParalleljScheduler;
import org.parallelj.launching.quartz.ParalleljSchedulerFactory;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

public class DynamicLegacyProgram implements DynamicMBean {

	private Class<? extends Adapter> adapterClass;
	private List<ArgEntry> adapterArgs;

	public DynamicLegacyProgram(Class<? extends Adapter> adapterClass,
			List<ArgEntry> adapterArgs) {
		this.adapterClass = adapterClass;
		this.adapterArgs = adapterArgs;
	}

	private void addAdapterArgumentsToJobDataMap(JobDetail job, Object[] params, String[] signature) throws MBeanException {
		/*
		 * if no restartId:
		 * 	   this.adapterArgs.size() == params[].length == signature[].length
		 * if a restartId:
		 * 	   this.adapterArgs.size()+1 == params[].length == signature[].length
		 * 	   In this case, params[0] is the restartId
		 * If it is not the case, there is an error in
		 * initializing JMX description methods for the Adpater MBean.
		 */
		try {
			// Is there a restartId?
			int ind=0;
			if (params.length == this.adapterArgs.size()+1) {
				job.getJobDataMap().put("restartId", params[ind++]);
			}
			
			for (ArgEntry arg:this.adapterArgs) {
				// Do we have to use a Parser?
				Object obj = null;
				if (!arg.parser.equals(NopParser.class)) {
					obj = arg.parser.newInstance().parse(String.valueOf(params[ind++]));
				} else {
					obj = params[ind++];
				}
				job.getJobDataMap().put(arg.name, obj);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new MBeanException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new MBeanException(e);
		} finally {
		}
	}
	
	@Override
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		boolean isSync = actionName.startsWith("sync");
		Object result = null;
		try {
			// First we must get a reference to a scheduler
			ParalleljScheduler scheduler = (new ParalleljSchedulerFactory())
					.getScheduler();

			// define the job and tie it to our HelloJob class
			@SuppressWarnings("unchecked")
			JobBuilder jobBuilder = newJob((Class<Job>) adapterClass);
			JobDetail job = jobBuilder.withIdentity(
					String.valueOf(jobBuilder),
					String.valueOf(jobBuilder)).build();

			// initialize arguments for Quartz
			addAdapterArgumentsToJobDataMap(job, params, signature);
		
			if (isSync) {
				result = AdapterJobsRunner.syncLaunch(scheduler, job);
			} else {
				AdapterJobsRunner.ayncLaunch(scheduler, job);
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		} finally {
		}
		
		return result;
	}

	private MBeanOperationInfo[] createMBeanOperationInfo() {
		MBeanOperationInfo[] mbeansInfos = new MBeanOperationInfo[] {
				new MBeanOperationInfo("syncLaunch",
						"the method for a syncLaunch",
						ArrayUtils.addAll(createMBeanParameterInfos()),
						"java.lang.String", MBeanOperationInfo.INFO),
				new MBeanOperationInfo(
						"syncLaunch",
						"the method for a syncLaunch",
						ArrayUtils
								.addAll(new MBeanParameterInfo[] { new MBeanParameterInfo(
										"restartId", "java.lang.String",
										"the restarting Id") },
										createMBeanParameterInfos()),
						"java.lang.String", MBeanOperationInfo.INFO),
				new MBeanOperationInfo(
						"asyncLaunch",
						"the method for a syncLaunch",
						ArrayUtils
								.addAll(new MBeanParameterInfo[] { new MBeanParameterInfo(
										"restartId", "java.lang.String",
										"the restarting Id") },
										createMBeanParameterInfos()), "void",
						MBeanOperationInfo.INFO),
				new MBeanOperationInfo("asyncLaunch",
						"the method for a syncLaunch",
						ArrayUtils.addAll(createMBeanParameterInfos()), "void",
						MBeanOperationInfo.INFO) };
		return mbeansInfos;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		MBeanAttributeInfo[] attrs = null;
		MBeanConstructorInfo[] ctors = null;
		MBeanOperationInfo[] opers = createMBeanOperationInfo();
		MBeanNotificationInfo[] notifs = null;
		String className = "ProgramAdapter.Adapter";
		String description = null;
		return new MBeanInfo(className, description, attrs, ctors, opers,
				notifs);
	}

	private MBeanParameterInfo[] createMBeanParameterInfos() {
		int lg = this.adapterArgs.size();
		int cpt = 0;
		MBeanParameterInfo[] result = new MBeanParameterInfo[lg];
		for (ArgEntry arg : adapterArgs) {
			// Only simple Type are authorized
			//System.out.println(arg);
			String type = null; 
			if (!arg.type.equals(String.class)
					&& !arg.type.equals(int.class)
					&& !arg.type.equals(long.class)
					&& !arg.type.equals(boolean.class)) {
				type =  String.class.getCanonicalName();
			} else {
				type =  arg.type.getCanonicalName();
			}
			result[cpt++] = new MBeanParameterInfo(arg.name, type, "");
		}
		return result;
	}

	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		// Do Nothing
		return null;
	}

	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {
		// Do Nothing
	}

	@Override
	public AttributeList getAttributes(String[] attributes) {
		AttributeList list = new AttributeList();
		return list;
	}

	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		AttributeList list = new AttributeList();
		return list;
	}
}
