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
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import org.parallelj.ForEach;
import org.parallelj.tools.AbstractProcessor;
import org.parallelj.tools.util.Elements;
import org.parallelj.tools.util.Types;

/**
 * Check the constraints of {@link ForEach}.
 * 
 * @author Laurent Legrand
 * 
 * @see <a href="http://www.parallelj.org/display/core/ForEach#Constraints">//J - Begin</a>
 * 
 */
@SupportedAnnotationTypes({ "org.parallelj.ForEach" })
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ForEachConstraints extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(ForEach.class)) {
			this.verifyProperty(element, element.getAnnotation(ForEach.class)
					.value());
		}
		return true;
	}

	/**
	 * 
	 * 
	 * @param type
	 */
	private void verifyProperty(Element source, String name) {
		Element type = Elements.getAncestorOrSelf(source, ElementKind.CLASS);
		for (Element e : Elements.getChildren(type, ElementKind.FIELD)) {
			if (e.getSimpleName().contentEquals(name)) {
				TypeMirror iterable = Types.erasure(Elements
						.getTypeElement(Iterable.class.getCanonicalName()).asType());
				
				TypeMirror erasure = Types.erasure(e.asType());
				
				if (!Types.isAssignable(erasure, iterable)) {
					this.processingEnv
							.getMessager()
							.printMessage(
									Kind.WARNING,
									String.format(
											"%s: The property named '%s' is not a java.lang.Iterable",
											this.getClass().getSimpleName(),
											name, type), source);
				}
				return;
			}
		}
		this.processingEnv.getMessager().printMessage(
				Kind.WARNING,
				String.format("%s: No property named '%s' found in '%s'", this
						.getClass().getSimpleName(), name, type), source);

		// this.processingEnv.getElementUtils().printElements(new
		// PrintWriter(System.out), type);

	}
}
