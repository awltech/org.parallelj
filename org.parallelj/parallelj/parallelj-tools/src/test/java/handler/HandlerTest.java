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

package handler;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.tools.constraint.HandlerConstraints;

import util.ProcessorTest;

public class HandlerTest extends ProcessorTest {

	@Override
	protected Iterable<? extends Processor> getProcessors() {
		return Arrays.asList(new HandlerConstraints());
	}
	
	@Test
	public void procedureNotFound() throws Exception {

		List<Diagnostic<? extends JavaFileObject>> diagnostics = this
				.compile("/handler/ProcedureNotFound.java");
		// System.out.println(diagnostics);

		Assert.assertTrue(this.assertDiagnostic(diagnostics,
				HandlerConstraints.class));
	}
	
	@Test
	public void noProcedure() throws Exception {

		List<Diagnostic<? extends JavaFileObject>> diagnostics = this
				.compile("/handler/NoProcedure.java");
		// System.out.println(diagnostics);

		Assert.assertTrue(this.assertDiagnostic(diagnostics,
				HandlerConstraints.class));
	}
	

}
