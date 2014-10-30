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

import org.parallelj.mirror.HandlerLoopPolicy;

/**
 * A {@link KHandler} is a special kind of {@link KProcedure} that manages
 * {@link KCall} on error.
 * 
 * @author Atos Worldline
 * 
 */
public class KHandler extends KProcedure {

	/**
	 * Marking that stores the {@link KCall} on error.
	 * 
	 * @author Atos Worldline
	 */
	class HandlerMarking extends Marking {

		/**
		 * List of {@link KCall} on error.
		 */
		List<KCall> onError = new ArrayList<KCall>();
	}

	/**
	 * input parameter that will store the {@link KCall} on error.
	 */
	KInputParameter onError = new KInputParameter();

	/**
	 * The Exception handling policy.
	 */
	protected HandlerLoopPolicy handlerLoopPolicy = HandlerLoopPolicy.RESUME;

	/**
	 * Create a new {@link KHandler}
	 * 
	 * @param program
	 *            the program containing this handler
	 */
	public KHandler(KProgram program) {
		super(program);
		this.setType("<handler>");
		this.addInputParameter(this.onError);
		super.setJoin(new KJoin() {

			@Override
			public void join(KCall call) {
				KCall c = KHandler.this.getMarking(call.getProcess()).onError
						.remove(0);
				KHandler.this.onError.set(call, c.getException());
			}

			/**
			 * TODO javadoc
			 */
			@Override
			public boolean isEnabled(KProcess process) {
				boolean isEnabled = !KHandler.this.getMarking(process).onError.isEmpty();
				boolean isBusyOrEnable=false;
				
				if (isEnabled) {
					// Does we have to wait for the end of the Procedure before activate the Handler??
					if (KHandler.this.getHandlerLoopPolicy().isWaitingPolicy()) {
						for (KCall call : KHandler.this.getMarking(process).onError) {
							if (call.getProcedure().isBusy(process) || call.getProcedure().isEnabled(process)) {
								isBusyOrEnable=true;
								break;
							}
						}
					}
				}
				return !isBusyOrEnable && isEnabled;
			}

			@Override
			public KElement getProcedure() {
				return KHandler.this;
			}
		});
	}

	@Override
	public void setJoin(KJoin join) {
		// TODO add message kind
		throw new IllegalArgumentException("TODO");
	}

	@Override
	protected Marking newMarking(KProcess process) {
		return new HandlerMarking();
	}

	@Override
	protected HandlerMarking getMarking(KProcess process) {
		return (HandlerMarking) super.getMarking(process);
	}

	/**
	 * Add a call on error
	 * 
	 * @param call
	 *            the call on error
	 */
	public void addCallOnError(KCall call) {
		this.getMarking(call.getProcess()).onError.add(call);
	}

	public HandlerLoopPolicy getHandlerLoopPolicy() {
		return this.handlerLoopPolicy;
	}

	public void setHandlerLoopPolicy(HandlerLoopPolicy policy) {
		this.handlerLoopPolicy = policy;
	}
}
