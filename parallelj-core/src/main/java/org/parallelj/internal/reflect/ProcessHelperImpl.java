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
package org.parallelj.internal.reflect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.MessageKind;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProcessor;
import org.parallelj.internal.util.sm.StateEvent;
import org.parallelj.internal.util.sm.StateListener;
import org.parallelj.internal.util.sm.StateMachines;
import org.parallelj.mirror.Process;
import org.parallelj.mirror.ProcessState;

public class ProcessHelperImpl<E> implements ProcessHelper<E>,
		StateListener<KProcess, ProcessState> {

	/**
	 * Lock used to synchronize methods
	 */
	private Lock lock = new ReentrantLock();

	/**
	 * Condition used to manage the {@link #join()} method.
	 */
	private Condition join = lock.newCondition();

	/**
	 * The process to be executed.
	 */
	private KProcess process;

	/**
	 * The processor that will execute {@link #process}.
	 */
	private KProcessor processor;

	public ProcessHelperImpl(KProcess process) {
		this.process = process;
		StateMachines.addStateListener(this.process, this);
	}

	@Override
	public ProcessHelper<E> abort() {
		this.suspend();
		this.process.abort();
		this.resume();
		return this;
	}

	@Override
	public ProcessState getState() {
		return this.process.getState();
	}

	@SuppressWarnings("unchecked")
	@Override
	public E context() {
		return (E) this.process.getContext();
	}

	@Override
	public ProcessHelper<E> resume() {
		if (this.processor != null) {
			this.processor.resume();
		}
		return this;
	}

	@Override
	public ProcessHelper<E> execute() {
		this.execute(null);
		return this;
	}

	@Override
	public ProcessHelper<E> execute(ExecutorService service) {
		if (this.processor == null) {
			this.processor = new KProcessor(service);
			this.processor.execute(this.process);
		}
		return this;
	}

	@Override
	public ProcessHelper<E> suspend() {
		if (this.processor != null) {
			this.processor.suspend();
		}
		return this;
	}

	@Override
	public ProcessHelper<E> terminate() {
		this.process.terminate();
		return this;
	}

	@Override
	public void stateChanged(StateEvent<KProcess, ProcessState> event) {
		switch (event.getState()) {
		case TERMINATED:
		case COMPLETED:
		case ABORTED:
			try {
				this.lock.lock();
				this.join.signalAll();
			} finally {
				this.lock.unlock();
			}

		}
	}

	@Override
	public ProcessHelper<E> join() {
		try {
			this.lock.lock();
			switch (this.process.getState()) {
			case PENDING:
			case RUNNING:
				this.join.await();
			}
		} catch (InterruptedException e) {
			MessageKind.W0004.format(e);
		} finally {
			this.lock.unlock();
		}
		return this;
	}

	@Override
	public Process getProcess() {
		return this.process;
	}

}
