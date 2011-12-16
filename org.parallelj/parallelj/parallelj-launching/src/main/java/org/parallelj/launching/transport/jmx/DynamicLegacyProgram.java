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
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.parser.NopParser;
import org.parallelj.launching.quartz.Launch;
import org.parallelj.launching.quartz.LaunchException;
import org.parallelj.launching.quartz.Launcher;
import org.parallelj.launching.transport.ArgEntry;
import org.quartz.Job;
import org.quartz.JobDataMap;

/**
 * Dynamic MBean to register a Program as a MBean
 * 
 */
public class DynamicLegacyProgram implements DynamicMBean {
	public static final String JOB_ID_KEY = "restartedFireInstanceId";

	/**
	 * The adapter class 
	 */
	private Class<? extends Adapter> adapterClass;
	
	/**
	 * 
	 */
	private List<ArgEntry> adapterArgs;

	/**
	 * Default constructor
	 * 
	 * @param adapterClass
	 *            the Program's adapter type
	 * @param adapterArgs
	 */
	public DynamicLegacyProgram(Class<? extends Adapter> adapterClass,
			List<ArgEntry> adapterArgs) {
		this.adapterClass = adapterClass;
		this.adapterArgs = adapterArgs;
	}

	/**
	 * Initialize the JobDataMap with the Program arguments
	 * 
	 * @param job The JobDetail for the JobDataMap initialization
	 * @param params The parameters Objects for the Program
	 * @param signature The parameters type
	 * @throws MBeanException If an error appends when initializing the JobDataMap
	 */
	protected JobDataMap buildJobDataMap(Object[] params) throws MBeanException {
		JobDataMap jobDataMap = new JobDataMap();
		/*
		 * if no restartId: this.adapterArgs.size() == params[].length ==
		 * signature[].length if a restartId: this.adapterArgs.size()+1 ==
		 * params[].length == signature[].length In this case, params[0] is the
		 * restartId If it is not the case, there is an error in initializing
		 * JMX description methods for the Adpater MBean.
		 */
		try {
			// Is there a restartId?
			int ind = 0;
			if (params.length == this.adapterArgs.size() + 1) {
				jobDataMap.put(JOB_ID_KEY, params[ind++]);
			}

			for (ArgEntry arg : this.adapterArgs) {
				// Do we have to use a Parser?
				Object obj = null;
				if (!arg.getParser().equals(NopParser.class)) {
					obj = arg.getParser().newInstance()
							.parse(String.valueOf(params[ind++]));
				} else {
					obj = params[ind++];
				}
				jobDataMap.put(arg.getName(), obj);
			}
		} catch (InstantiationException e) {
			throw new MBeanException(e);
		} catch (IllegalAccessException e) {
			throw new MBeanException(e);
		}
		return jobDataMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#invoke(java.lang.String,
	 * java.lang.Object[], java.lang.String[])
	 */
	@Override
	public final Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		boolean isSync = actionName.startsWith("sync");
		Object result = null;
		try {
			// initialize arguments for Quartz
			JobDataMap jobDataMap = buildJobDataMap(params);

			@SuppressWarnings("unchecked")
			Launch launch = Launcher.getLauncher()
					.newLaunch((Class<Job>) adapterClass)
					.addDatas(jobDataMap);
			if (isSync) {
				// Launch and wait until terminated
				launch.synchLaunch();
				return LaunchingMessageKind.IQUARTZ0003.getFormatedMessage(adapterClass.getCanonicalName(), launch.getLaunchId());
			} else {
				// Launch and continue
				launch.aSynchLaunch();
				return LaunchingMessageKind.IQUARTZ0002.getFormatedMessage(adapterClass.getCanonicalName(), launch.getLaunchId());
			}
		} catch (LaunchException e) {
			LaunchingMessageKind.EQUARTZ0003.format(actionName, e);
		}
		return result;
	}

	/**
	 * Generate the available methods description for each Program registered as
	 * a MBean with this dynamic MBean
	 * 
	 * @return an array of MBeanOperationInfo
	 */
	private MBeanOperationInfo[] createMBeanOperationInfo() {
		MBeanOperationInfo[] mbeansInfos = new MBeanOperationInfo[] {
				new MBeanOperationInfo("syncLaunch",
						"the method for a syncLaunch",
						ArrayUtils.addAll(createMBeanParameterInfos()),
						"java.lang.String", MBeanOperationInfo.INFO),
				new MBeanOperationInfo("asyncLaunch",
						"the method for a syncLaunch",
						ArrayUtils.addAll(createMBeanParameterInfos()), "void",
						MBeanOperationInfo.INFO),
				new MBeanOperationInfo(
						"syncLaunch",
						"the method for a syncLaunch",
						ArrayUtils
								.addAll(new MBeanParameterInfo[] { new MBeanParameterInfo(
										"rid", "java.lang.String",
										"the restarting Id") },
										createMBeanParameterInfos()),
						"java.lang.String", MBeanOperationInfo.INFO),
				new MBeanOperationInfo(
						"asyncLaunch",
						"the method for a syncLaunch",
						ArrayUtils
								.addAll(new MBeanParameterInfo[] { new MBeanParameterInfo(
										"rid", "java.lang.String",
										"the restarting Id") },
										createMBeanParameterInfos()), "void",
						MBeanOperationInfo.INFO) };
		return mbeansInfos;
	}

	/* (non-Javadoc)
	 * @see javax.management.DynamicMBean#getMBeanInfo()
	 */
	@Override
	public final MBeanInfo getMBeanInfo() {
		MBeanAttributeInfo[] attrs = null;
		MBeanConstructorInfo[] ctors = null;
		MBeanOperationInfo[] opers = createMBeanOperationInfo();
		MBeanNotificationInfo[] notifs = null;
		String className = "ProgramAdapter.Adapter";
		String description = null;
		return new MBeanInfo(className, description, attrs, ctors, opers,
				notifs);
	}

	/**
	 * Generate MBean parameter info for all Program field annotated with @In 
	 * 
	 * @return an array of MBeanParameterInfo
	 */
	private MBeanParameterInfo[] createMBeanParameterInfos() {
		int lg = this.adapterArgs.size();
		int cpt = 0;
		MBeanParameterInfo[] result = new MBeanParameterInfo[lg];
		for (ArgEntry arg : adapterArgs) {
			// Only simple Type are authorized
			// System.out.println(arg);
			String type = null;
			if (!arg.getType().equals(String.class)
					&& !arg.getType().equals(int.class)
					&& !arg.getType().equals(long.class)
					&& !arg.getType().equals(boolean.class)) {
				type = String.class.getCanonicalName();
			} else {
				type = arg.getType().getCanonicalName();
			}
			result[cpt++] = new MBeanParameterInfo(arg.getName(), type, "");
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
	 */
	@Override
	public final Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		// Do Nothing
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 */
	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {
		// Do Nothing
	}

	/* (non-Javadoc)
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 */
	@Override
	public final AttributeList getAttributes(String[] attributes) {
		AttributeList list = new AttributeList();
		return list;
	}

	/* (non-Javadoc)
	 * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
	 */
	@Override
	public final AttributeList setAttributes(AttributeList attributes) {
		AttributeList list = new AttributeList();
		return list;
	}
}
