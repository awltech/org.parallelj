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
package org.parallelj.launching;


public class LaunchError extends Error {

	private String formatedMessage;
	private Error error;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7793932389206020914L;

	public LaunchError(final String formatedMessage) {
		super(formatedMessage);
		this.formatedMessage = formatedMessage;
	}

	public LaunchError(final String formatedMessage, final Error cause) {
		super(formatedMessage, cause);
		this.formatedMessage = formatedMessage;
		this.error = cause;
	}

	public LaunchError(Error e) {
		super(e);
		this.error=e;
	}

	public String getFormatedMessage() {
		return formatedMessage;
	}

	public Error getError() {
		return error;
	}

	public void setFormatedMessage(final String formatedMessage) {
		this.formatedMessage = formatedMessage;
	}

	public void setError(final Error error) {
		this.error = error;
	}

}
