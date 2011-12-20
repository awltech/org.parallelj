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

package org.parallelj.tracknrestart;

import org.parallelj.internal.util.Formatter;
import org.parallelj.internal.util.Formatter.Format;

/**
 * This enumeration contains the types of messages logged by //J Track&Restart.
 * 
 * @author Atos Worldline
 * @since 0.5.0
 */
public enum TrackNRestartMessageKind {
	
		@Format("Failed to close Connection")
		ETNRJDBC0001,
		@Format("Unexpected exception closing Connection. This is often due to a Connection being returned after or during shutdown.")
		ETNRJDBC0002,
		@Format("Failed to override connection auto commit/transaction isolation.")
		WTNRJDBC0003,
		
		@Format("Registering Quartz Job Track&Restart Plug-in.")
		ITNRPLUGIN0001,
		@Format("Unexpected exception.")
		ETNRPLUGIN0002,
		@Format("Job %s added.")
		ITNRPLUGIN0003,
		@Format("%s is running in simple tracking (non-restarting) mode.")
		ITNRPLUGIN0004,
		@Format("At least one %s execution already exists in tracking history.")
		WTNRPLUGIN0005,
		@Format("Replacing %s after resolving restarted id from keyword _LAST_ to #%s.")
		ITNRPLUGIN0006,
		@Format("Unable to restart %s caused by previous execution id #%s not found in tracking history.")
		ETNRPLUGIN0007,
		@Format("Deleting %s to prevent unexpected execution.")
		WTNRPLUGIN0008,

		@Format("Restarting %s #%s.")
		ITNRPLUGIN0010,

		@Format("%s is running in simple tracking (non-restarting) mode.")
		ITNRPLUGIN0012,
		@Format("Unable to restart %s caused by no previous execution in tracking history.")
		ETNRPLUGIN0013,
		@Format("Deleting %s caused exception.")
		ETNRPLUGIN0014,
		@Format("First tracked execution of %s.")
		ITNRPLUGIN0015
		;
	/**
	 * Method used to format a message
	 * 
	 * @param args
	 *            the arguments used to format the message
	 * @return the formatted message
	 */
	public String format(Object... args) {
		// delegates to formatter
		return formatter.print(this, args);
	}
	
	static Formatter<TrackNRestartMessageKind> formatter = new Formatter<TrackNRestartMessageKind>(TrackNRestartMessageKind.class);

}
