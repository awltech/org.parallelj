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
package org.parallelj.tracknrestart.option;

import org.parallelj.launching.transport.jmx.JmxOption;
import org.parallelj.tracknrestart.plugins.TrackNRestartPluginAll;
import org.quartz.JobDataMap;

public class RidJmxOption implements JmxOption {

	@Override
	public void process(JobDataMap jobDataMap, String value) {
		if (value != null && !value.equals("null") && value.length()>0) {
			jobDataMap.put(TrackNRestartPluginAll.RESTARTED_FIRE_INSTANCE_ID, value);
		}
	}

	@Override
	public String getName() {
		return "rid";
	}

	@Override
	public String getDescription() {
		return "Restart Id of an already launched Program";
	}
}
