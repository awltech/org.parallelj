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

public final class QuartzUtils {
	
	public static final String RETURN_CODE = "RETURN_CODE";
	public static final String USER_RETURN_CODE = "USER_RETURN_CODE";
		private static final String JOB_ID_KEY = "_RESTARTED_FIRE_INSTANCE_ID_";
		public static final String PROCEDURES_IN_ERROR = "PROCEDURES_IN_ERROR";
		public static final String CONTEXT = "CONTEXT";
	
	private QuartzUtils() {
	}

	public static String getRestartedFireInstanceIdKey() {
		return JOB_ID_KEY;
	}

}
