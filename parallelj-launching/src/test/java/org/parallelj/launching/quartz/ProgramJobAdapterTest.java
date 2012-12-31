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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.parallelj.Programs;
import org.parallelj.launching.ProceduresOnError;
import org.parallelj.launching.programs.ProgramWithErrors;
import org.parallelj.launching.programs.ProgramWithoutErrors;

public class ProgramJobAdapterTest {

	@Test
	public void testLaunchWithoutErrors() {
		ProgramWithoutErrors prg = new ProgramWithoutErrors();
		ExecutorService service = Executors.newCachedThreadPool();
		Programs.as(prg).execute(service).join();
		ProceduresOnError errors = prg.getOnMynErrors();
		assertNull(errors);
	}
	
	@Test
	public void testLaunchWithErrors() {
		ProgramWithErrors prg = new ProgramWithErrors();
		ExecutorService service = Executors.newCachedThreadPool();
		Programs.as(prg).execute(service).join();
		ProceduresOnError errors = prg.getOnMynErrors();
		assertNotNull(errors);
		assertEquals(errors.getNumberOfProceduresInError(), 2);
		//assertTrue(errors.isErrorForProcedureOfType(Runnable.class));
		assertTrue(errors.isErrorOfType(RuntimeException.class));

		//assertTrue(errors.isErrorForProcedureOfType(Callable.class));
		assertTrue(errors.isErrorOfType(Exception.class));

		service.shutdown();
	}

}
