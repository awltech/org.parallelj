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

import org.parallelj.While;
import org.parallelj.tools.AbstractProcessor;
import org.parallelj.tools.util.Elements;

/**
 * Check the constraints of {@link While}.
 * 
 * @author Laurent Legrand
 * 
 * @see <a
 *      href="http://www.parallelj.org/display/core/While#Constraints">//J - Core</a>
 * 
 */
@SupportedAnnotationTypes({ "org.parallelj.While" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class WhileConstraints extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(While.class)) {
			this.verify(element, element.getAnnotation(While.class).value());
		}
		return true;
	}

	/**
	 * 
	 * 
	 * @param type
	 */
	private void verify(Element source, String name) {
		Element type = Elements.getAncestorOrSelf(source, ElementKind.CLASS);

		for (Element e : Elements.getChildren(type, ElementKind.METHOD)) {
			if (Elements.getPropertyName(e.getSimpleName().toString()).equals(
					name)) {
				return;
			}
		}
		this.processingEnv.getMessager().printMessage(
				Kind.WARNING,
				String.format("%s: No property called '%s' in this class", this
						.getClass().getSimpleName(), name), source);

	}

}
