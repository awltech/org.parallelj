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

/**
 * Represent an element contained in a {@link KProgram}.
 * 
 * @author Atos Worldline
 */
public abstract class KElement {

	/**
	 * The {@link KProgram} that owns this element.
	 */
	protected final KProgram program;
	
	/**
	 * The position of this element in the program.
	 */
	int index;	

	/**
	 * The name of the element.
	 * 
	 * Optional.
	 */
	protected String name;

	/**
	 * List of incoming links: coming from {@link KCondition}.
	 */
	List<KInputLink> inputLinks = new ArrayList<KInputLink>();

	/**
	 * List of outgoing links: going to {@link KCondition}.
	 */
	List<KOutputLink> outputLinks = new ArrayList<KOutputLink>();

	/**
	 * 
	 * @param program
	 * 
	 * @throws IllegalArgumentException
	 *             if program is <code>null</code>
	 */
	protected KElement(KProgram program) {
		if (program == null) {
			// TODO add message kind
			throw new IllegalArgumentException("program is null");
		}
		this.program = program;
		this.program.addElement(this);
	}

	/**
	 * @return the program
	 */
	public KProgram getProgram() {
		return this.program;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	void addInputLink(KInputLink link) {
		this.inputLinks.add(link);
	}

	void addOutputLink(KOutputLink link) {
		this.outputLinks.add(link);
	}

	/**
	 * @return the list of output links
	 */
	public List<KOutputLink> getOutputLinks() {
		return outputLinks;
	}

	/**
	 * @return the list of input links
	 */
	public List<KInputLink> getInputLinks() {
		return inputLinks;
	}
	
	/**
	 * @return the index
	 */
	int getIndex() {
		return this.index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	void setIndex(int index) {
		this.index = index;
	}
	
	protected void init(KProcess process) {
		
	}
	

	protected Object getMarking(KProcess process) {
		return process.markings[this.index];
	}
	
	protected void setMarking(KProcess process, Object marking) {
		process.markings[this.index] = marking;
	}

	@Override
	public String toString() {
		return String.format("%s[name='%s']", this.getClass().getSimpleName(),
				this.name);
	}

}