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

public class CompositeElementBuilder extends ElementBuilder {

	ElementBuilder[] builders;

	public CompositeElementBuilder(ElementBuilder... builders) {
		this.builders = builders;
	}

	public ElementBuilder start() {
		for (ElementBuilder b : this.builders) {
			b.start();
		}
		return this;
	}

	public ElementBuilder procedures() {
		for (ElementBuilder b : this.builders) {
			b.procedures();
		}
		return this;
	}
	
	public ElementBuilder handlers() {
		for (ElementBuilder b : this.builders) {
			b.handlers();
		}
		return this;
	}

	public ElementBuilder conditions() {
		for (ElementBuilder b : this.builders) {
			b.conditions();
		}
		return this;
	}

	public ElementBuilder links() {
		for (ElementBuilder b : this.builders) {
			b.links();
		}
		return this;
	}

	public ElementBuilder joins() {
		for (ElementBuilder b : this.builders) {
			b.joins();
		}
		return this;
	}

	public ElementBuilder splits() {
		for (ElementBuilder b : this.builders) {
			b.splits();
		}
		return this;
	}

	public ElementBuilder complete() {
		for (ElementBuilder b : this.builders) {
			b.complete();
		}
		return this;
	}

	@Override
	public void setBuilder(ProgramBuilder builder) {
		super.setBuilder(builder);
		for (ElementBuilder b : this.builders) {
			b.setBuilder(builder);
		}

	}

}
