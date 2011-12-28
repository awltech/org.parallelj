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
package foreach;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.tools.constraint.ForEachConstraints;

import util.ProcessorTest;

public class ForEachTest extends ProcessorTest {

	@Override
	protected Iterable<? extends Processor> getProcessors() {
		return Arrays.asList(new ForEachConstraints());
	}

	@Test
	public void unknown() throws Exception {

		List<Diagnostic<? extends JavaFileObject>> diagnostics = this
				.compile("/foreach/UnknownPropertyForEach.java");

		Assert.assertTrue(this.assertDiagnostic(diagnostics,
				ForEachConstraints.class));
	}

	@Test
	public void notIterable() throws Exception {

		List<Diagnostic<? extends JavaFileObject>> diagnostics = this
				.compile("/foreach/NotIterableForEach.java");

		Assert.assertTrue(this.assertDiagnostic(diagnostics,
				ForEachConstraints.class));
	}

	@Test
	public void good() throws Exception {

		List<Diagnostic<? extends JavaFileObject>> diagnostics = this
				.compile("/foreach/GoodForEach.java");

		Assert.assertTrue(diagnostics.isEmpty());
	}

}
