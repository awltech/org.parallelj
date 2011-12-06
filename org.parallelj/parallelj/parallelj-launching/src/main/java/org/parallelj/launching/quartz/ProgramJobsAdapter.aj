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

package org.parallelj.launching.quartz;

import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.quartz.Job;
//import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

/**
 * Implements the Job.execute(..) method
 * 
 * 
 */
public aspect ProgramJobsAdapter {

	declare precedence :
		org.parallelj.launching.quartz.JobsAdapter;

	after(Job self, JobExecutionContext context) : 
		execution( public void  Job.execute(..)) 
			&& (within(@org.parallelj.Program *) || within(JobsAdapter)) 
				&& args(context) && this(self) {

		System.out.println("Running Jobs... : " + self);
		/*
		JobDataMap map = context.getJobDetail().getJobDataMap();
		for (String key : map.keySet()) {
			System.out.println("JobDatas => key:" + key + "  -  obj:"
					+ map.get(key));
		}
		*/

		System.out.println("Executing " + this);
		ProcessHelper<?> p = Programs.as((Adapter) self).execute().join();

		// TODO: Set the result (as a String) of the Job...
		context.setResult(String.valueOf(p.getState()));
	}

}
