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
package org.parallelj.launching.transport.jmx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;

import org.apache.commons.lang3.ArrayUtils;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.inout.Argument;
import org.parallelj.launching.parser.NopParser;
import org.parallelj.launching.quartz.Launch;
import org.parallelj.launching.quartz.LaunchException;
import org.parallelj.launching.quartz.Launcher;
import org.parallelj.launching.quartz.QuartzUtils;
import org.parallelj.launching.remote.RemoteProgram;
import org.quartz.Job;
import org.quartz.JobDataMap;

/**
 * Dynamic MBean to register a Program as a MBean
 * 
 */
public class DynamicLegacyProgram implements DynamicMBean {
	/**
	 * The adapter class
	 */
	private RemoteProgram remoteProgram;

	private JmxCommand[] cmds;
	private MBeanOperationInfo[] operations;

	/**
	 * Default constructor
	 * 
	 * @param adapterClass
	 *            the Program's adapter type
	 * @param adapterArgs
	 */
	public DynamicLegacyProgram(RemoteProgram remoteProgram) {
		this.remoteProgram = remoteProgram;

		// Get all available Commands
		this.cmds = JmxCommands.getCommands().values()
				.toArray(new JmxCommand[] {});
		Arrays.sort(this.cmds);

		this.operations = new MBeanOperationInfo[this.cmds.length];
		int opIndex = 0;
		for (JmxCommand cmd : this.cmds) {
			final List<MBeanParameterInfo> parameters = new ArrayList<MBeanParameterInfo>();
			for (JmxOption option : JmxOptions.getOptions()) {
				// Options "id" and "args" doesn't have to be shown using Jmx
				final MBeanParameterInfo param = new MBeanParameterInfo(
						option.getName(), "java.lang.String",
						option.getDescription());
				parameters.add(param);
			}
			final MBeanOperationInfo operation = new MBeanOperationInfo(
					cmd.getType(), cmd.getUsage(), ArrayUtils.addAll(
							parameters.toArray(new MBeanParameterInfo[] {}),
							createMBeanParameterInfos()), "java.lang.String",
					MBeanOperationInfo.INFO);
			operations[opIndex++] = operation;
		}
	}

	/**
	 * Initialize the JobDataMap with the Program arguments
	 * 
	 * @param job
	 *            The JobDetail for the JobDataMap initialization
	 * @param params
	 *            The parameters Objects for the Program
	 * @param signature
	 *            The parameters type
	 * @throws MBeanException
	 *             If an error appends when initializing the JobDataMap
	 */
	protected JobDataMap buildJobDataMap(final JmxCommand jmxCommand,
			final Object[] params) throws MBeanException {
		final JobDataMap jobDataMap = new JobDataMap();

		try {
			int ind = 0;

			// Options are before the AdapterArguments
			for (JmxOption option : JmxOptions.getOptions()) {
				option.process(jobDataMap, String.valueOf(params[ind++]));
			}

			for (Argument arg : this.remoteProgram.getArguments()) {
				arg.setValueUsingParser(String.valueOf(params[ind++]));
			}
		} catch (Exception e) {
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
	public final Object invoke(final String actionName, final Object[] params,
			final String[] signature) throws MBeanException,
			ReflectionException {
		// Get the JmxCommand
		JmxCommand curCmd = null;
		for (JmxCommand cmd : this.cmds) {
			if (cmd.getType().equals(actionName)) {
				curCmd = cmd;
				break;
			}
		}

		final boolean isSync = actionName.startsWith("sync");
		try {
			// initialize arguments for lunching (using Quartz)
			final JobDataMap jobDataMap = buildJobDataMap(curCmd, params);

			@SuppressWarnings("unchecked")
			final Launch launch = Launcher.getLauncher()
					.newLaunch((Class<Job>) remoteProgram.getAdapterClass()).addDatas(jobDataMap);
			if (isSync) {
				// Launch and wait until terminated
				launch.synchLaunch();
				return LaunchingMessageKind.IQUARTZ0003.getFormatedMessage(
						remoteProgram.getAdapterClass().getCanonicalName(), launch.getLaunchId(),
						launch.getLaunchResult().get(QuartzUtils.RETURN_CODE),
						launch.getLaunchResult().get(QuartzUtils.USER_RETURN_CODE));
			} else {
				// Launch and continue
				launch.aSynchLaunch();
				return LaunchingMessageKind.IQUARTZ0002.getFormatedMessage(
						remoteProgram.getAdapterClass().getCanonicalName(), launch.getLaunchId());
			}
		} catch (LaunchException e) {
			LaunchingMessageKind.EQUARTZ0003.format(actionName, e);
		}
		
		return null;
	}

	/**
	 * Generate the available methods description for each Program registered as
	 * a MBean with this dynamic MBean
	 * 
	 * @return an array of MBeanOperationInfo
	 */
	private MBeanOperationInfo[] createMBeanOperationInfo() {
		return this.operations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getMBeanInfo()
	 */
	@Override
	public final MBeanInfo getMBeanInfo() {
		final MBeanOperationInfo[] opers = createMBeanOperationInfo();
		return new MBeanInfo(remoteProgram.getAdapterClass().getCanonicalName(), null, null, null, opers, null);
	}

	/**
	 * Generate MBean parameter info for all Program field annotated with @In
	 * 
	 * @return an array of MBeanParameterInfo
	 */
	private MBeanParameterInfo[] createMBeanParameterInfos() {
		final int lenght =this.remoteProgram.getArguments() != null ? this.remoteProgram.getArguments().size()
				: 0;
		int cpt = 0;
		MBeanParameterInfo[] result = new MBeanParameterInfo[lenght];
		if (this.remoteProgram.getArguments() != null) {
			for (Argument arg : this.remoteProgram.getArguments()) {
				// Only simple Type are authorized for JMX
				String type = null;
				
				String descriptionWithParserFormat = "Parser  '%s' will be used to set this parameter as it is defined in @In annotation for this field.";
				String descriptionWithoutParserFormat = "No parser will be used for this field as it is a primitive one.";
				String descriptionValueValue = descriptionWithoutParserFormat;
				if (!arg.getParser().getCanonicalName().equals(NopParser.class.getCanonicalName())) {
					descriptionValueValue = String.format(descriptionWithParserFormat, arg.getParser().getCanonicalName());
				}
				if (!arg.getType().equals(String.class)
						&& !arg.getType().equals(int.class)
						&& !arg.getType().equals(long.class)
						&& !arg.getType().equals(boolean.class)) {
					type = String.class.getCanonicalName();
				} else {
					type = arg.getType().getCanonicalName();
				}
				
				result[cpt++] = new MBeanParameterInfo(arg.getName(), type, descriptionValueValue);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
	 */
	@Override
	public final Object getAttribute(final String attribute)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		// Do Nothing
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 */
	@Override
	public void setAttribute(final Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {
		// Do Nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 */
	@Override
	public final AttributeList getAttributes(final String[] attributes) {
		return new AttributeList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.management.DynamicMBean#setAttributes(javax.management.AttributeList
	 * )
	 */
	@Override
	public final AttributeList setAttributes(final AttributeList attributes) {
		return new AttributeList();
	}
}
