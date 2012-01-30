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
package org.parallelj.internal.kernel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.mirror.Event;
import org.parallelj.mirror.EventListener;
import org.parallelj.mirror.ExecutorServiceKind;
import org.parallelj.mirror.Reflection;

public class KReflectionTest {
	
	class MyEventListener implements EventListener {
		
		List<Event<?>> events = new ArrayList<Event<?>>();
		
		@Override
		public void handleEvent(Event<?> event) {
			System.out.println("handleEvent: " + event);
			this.events.add(event);
		}
	}

	@Test
	public void test() {
		KProgram program = new KProgram();
		Assert.assertTrue(KReflection.getInstance().getPrograms()
				.contains(program));
	}

	@Test
	public void newProcessor() {
		Reflection reflection = KReflection.getInstance();

		Assert.assertNotNull(reflection.newProcessor(ExecutorServiceKind.NONE));
		Assert.assertNotNull(reflection
				.newProcessor(ExecutorServiceKind.SINGLE_THREAD_EXECUTOR));
		Assert.assertNotNull(reflection
				.newProcessor(ExecutorServiceKind.CACHED_THREAD_POOL));
		Assert.assertNotNull(reflection.newProcessor(
				ExecutorServiceKind.FIXED_THREAD_POOL, 10));
		Assert.assertNotNull(reflection.newProcessor(
				ExecutorServiceKind.PROVIDED, Executors.newCachedThreadPool()));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidFixedThreadPoolEmpty() {
		KReflection.getInstance().newProcessor(ExecutorServiceKind.FIXED_THREAD_POOL);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidFixedThreadPoolMismatch() {
		KReflection.getInstance().newProcessor(ExecutorServiceKind.FIXED_THREAD_POOL, "mismatch");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidProvidedEmpty() {
		KReflection.getInstance().newProcessor(ExecutorServiceKind.PROVIDED);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidProvidedMismatch() {
		KReflection.getInstance().newProcessor(ExecutorServiceKind.PROVIDED, "mismatch");
	}
	
	@Test
	public void listener() throws Exception {
		MyEventListener listener = new MyEventListener();
		KReflection.getInstance().addEventListener(listener);
		KProgram program = new KProgram();
		new KProcessor().execute(program.newProcess(null));
		Assert.assertFalse(listener.events.isEmpty());
	}
	
	

}
