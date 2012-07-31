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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.launching.programs.ProgramWithErrors;
import org.parallelj.launching.programs.ProgramWithoutErrors;

public class ProgramJobAdapterTest {

	@Test
	public void testLaunchWithoutErrors() {
		ProgramWithoutErrors prg = new ProgramWithoutErrors();
		ExecutorService service = Executors.newCachedThreadPool();
		ProcessHelper<ProgramWithoutErrors> p = Programs.as(prg).execute(service).join();
		Map<String, Set<String>> res=ProgramJobsAdapter.getProceduresInErrors(p.getProcess());
		assertNotNull(res);
		assertEquals(res.size(), 0);
	}
	
	@Test
	public void testLaunchWithErrors() {
		ProgramWithErrors prg = new ProgramWithErrors();
		ExecutorService service = Executors.newCachedThreadPool();
		ProcessHelper<ProgramWithErrors> p = Programs.as(prg).execute(service).join();
		Map<String, Set<String>> res=ProgramJobsAdapter.getProceduresInErrors(p.getProcess());
		assertNotNull(res);
		assertEquals(res.size(), 2);
		assertEquals(res.keySet().contains(Runnable.class.getCanonicalName()), true);
		assertNotNull(res.get(Runnable.class.getCanonicalName()));
		assertEquals(res.get(Runnable.class.getCanonicalName()).size(), 1);
		assertEquals(res.get(Runnable.class.getCanonicalName()).contains(RuntimeException.class.getCanonicalName()), true);
		
		assertEquals(res.keySet().contains(Callable.class.getCanonicalName()), true);
		assertNotNull(res.get(Callable.class.getCanonicalName()));
		assertEquals(res.get(Callable.class.getCanonicalName()).size(), 1);
		assertEquals(res.get(Callable.class.getCanonicalName()).contains(Exception.class.getCanonicalName()), true);
		service.shutdown();
	}

}
