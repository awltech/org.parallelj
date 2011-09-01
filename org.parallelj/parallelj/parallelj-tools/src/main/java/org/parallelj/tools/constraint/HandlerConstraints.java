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

package org.parallelj.tools.constraint;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.parallelj.ForEach;
import org.parallelj.Handler;
import org.parallelj.tools.AbstractProcessor;
import org.parallelj.tools.util.Elements;

/**
 * Check the constraints of {@link ForEach}.
 * 
 * @author Laurent Legrand
 * 
 * @see <a
 *      href="http://www.parallelj.org/display/core/HandlerEach#Constraints">//J - Core</a>
 * 
 */
@SupportedAnnotationTypes({ "org.parallelj.Handler" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class HandlerConstraints extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(Handler.class)) {
			this.verify(element, element.getAnnotation(Handler.class).value());
		}
		return true;
	}

	/**
	 * 
	 * 
	 * @param type
	 */
	private void verify(Element source, String[] names) {
		Element type = Elements.getAncestorOrSelf(source, ElementKind.CLASS);

		if (names.length == 0) {
			this.processingEnv.getMessager().printMessage(
					Kind.WARNING,
					String.format("%s: procedures must be defined", this
							.getClass().getSimpleName()), source);
			return;

		}

		for (String name : names) {
			boolean found = false;
			for (Element e : Elements.getChildren(type, ElementKind.METHOD)) {
				if (e.getSimpleName().contentEquals(name)) {
					found = true;
				}
				break;
			}
			if (!found) {
				this.processingEnv.getMessager().printMessage(
						Kind.WARNING,
						String.format(
								"%s: No procedure called '%s' in this class",
								this.getClass().getSimpleName(), name), source);

			}
		}
	}
}
