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

import java.util.HashMap;
import java.util.Map;

import org.parallelj.launching.errors.ProceduresOnError;

/**
 * The Result Object of a Program launching.
 * 
 */
public class LaunchResult {

	private Map<String, Object> outputParameters = new HashMap<String, Object>();

	private ProceduresOnError proceduresInError;

	private String returnCode;

	private ProgramReturnCodes statusCode = ProgramReturnCodes.NOTSTARTED;

	public LaunchResult() {
	}

	/**
	 * Return the {@link org.parallelj.Program Program} attributes annotated
	 * with {@link Out} and the corresponding values
	 * 
	 * @return a {@link Map} of attribute names and their values.
	 */
	public Map<String, Object> getOutputParameters() {
		return outputParameters;
	}

	/**
	 * Get the value of the {@link org.parallelj.Program Program} attribute
	 * annotated with {@link ReturnCode}
	 * 
	 * @return the value of the attribute annotated with {@link ReturnCode}
	 */
	public String getReturnCode() {
		return this.returnCode;
	}

	/**
	 * Setter method for the attribute returnCode.
	 * 
	 * @param returnCode
	 */
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	/**
	 * Setter method for the attribute outputParameters.
	 * 
	 * @param outputParameters
	 */
	public void setOutputParameters(Map<String, Object> outputParameters) {
		this.outputParameters = outputParameters;
	}

	/**
	 * Setter method for the attribute proceduresInError.
	 * 
	 * @param proceduresInError
	 */
	public void setProceduresInError(ProceduresOnError allProceduresInError) {
		this.proceduresInError = allProceduresInError;
	}

	/**
	 * Get the value of the {@link org.parallelj.Program Program} attribute
	 * annotated with {@link OnError} This attribute is automatically
	 * initialized if an unexpected Exception is thrown during the
	 * {@link org.parallelj.Program Program} execution.
	 * 
	 * @return the value of attribute annotated with {@link OnError} as a
	 *         {@link ProceduresOnError}
	 */
	public ProceduresOnError getProceduresInError() {
		return proceduresInError;
	}

	/**
	 * Get the status of the {@link org.parallelj.Program Program} execution.
	 * This value is automatically initialized if an unexpected Exception is thrown during the
	 * {@link org.parallelj.Program Program} execution.
	 * 
	 * @return the status of the {@link org.parallelj.Program Program} execution as a
	 *         {@link ProgramReturnCodes ProgramReturnCode}
	 */
	public ProgramReturnCodes getStatusCode() {
		return statusCode;
	}

	/**
	 * Setter method for the attribute statusCode.
	 * 
	 * @param statusCode
	 */
	public void setStatusCode(ProgramReturnCodes statusCode) {
		this.statusCode = statusCode;
	}

}
