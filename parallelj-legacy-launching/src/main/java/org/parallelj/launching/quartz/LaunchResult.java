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
package org.parallelj.launching.quartz;

import org.quartz.JobDataMap;

/**
 * The Result Object of a Program launching.
 * 
 */
@Deprecated
public class LaunchResult {
	/**
	 * The JobId generated by Quartz.
	 */
	private String jobId;

	/**
	 * The result from Object of a launch as a Quartz Data Map.
	 */
	private JobDataMap result;

	/**
	 * Default Constructor.
	 * 
	 * @param jobId
	 *            The JobIj generated by Quartz.
	 * @param result
	 *            The result Object as a Quartz Data Map.
	 */
	public LaunchResult(final String jobId, final JobDataMap result) {
		this.jobId = jobId;
		this.result = result;
	}

	/**
	 * Get the JobId
	 * 
	 * @return the JobId
	 */
	public String getJobId() {
		return jobId;
	}

	/**
	 * Get the result object from the Launch execution.
	 * 
	 * @return the result object from the Launch Execution.
	 */
	public JobDataMap getResult() {
		return result;
	}
}
