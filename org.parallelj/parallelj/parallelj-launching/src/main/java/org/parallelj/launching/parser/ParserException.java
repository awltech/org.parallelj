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
package org.parallelj.launching.parser;

public class ParserException extends Exception {

	private String formatedMessage;
	private Exception exception;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7793932389206020914L;

	public ParserException(final String formatedMessage) {
		super(formatedMessage);
		this.formatedMessage = formatedMessage;
	}

	public ParserException(final String formatedMessage, final Exception exception) {
		super(formatedMessage, exception);
		this.formatedMessage = formatedMessage;
		this.exception = exception;
	}

	public String getFormatedMessage() {
		return this.formatedMessage;
	}

	public Throwable getException() {
		return this.exception;
	}

	public void setException(final Exception exception) {
		this.exception = exception;
	}

	public void setFormatedMessage(final String formatedMessage) {
		this.formatedMessage = formatedMessage;
	}
}
