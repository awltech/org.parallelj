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
package org.parallelj.tools.util;

import java.beans.Introspector;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public class Elements {

	/**
	 * Return the ancestor (aka enclosing element) with a given kind.
	 * 
	 * @param element
	 * @param kind
	 * @return
	 */
	public static Element getAncestorOrSelf(Element element, ElementKind kind) {
		while (element != null) {
			if (element.getKind() == kind) {
				return element;
			}
			element = element.getEnclosingElement();
		}
		return null;
	}

	public static List<Element> getChildren(Element element, ElementKind kind) {
		List<Element> list = new ArrayList<Element>();
		for (Element e : element.getEnclosedElements()) {
			if (e.getKind() == kind) {
				list.add(e);
			}
		}
		return list;
	}

	/**
	 * @param e
	 * @return
	 * @see javax.lang.model.util.Elements#getAllAnnotationMirrors(javax.lang.model.element.Element)
	 */
	public static List<? extends AnnotationMirror> getAllAnnotationMirrors(
			Element e) {
		return Environment.getEnvironment().getElementUtils()
				.getAllAnnotationMirrors(e);
	}

	/**
	 * @param type
	 * @return
	 * @see javax.lang.model.util.Elements#getAllMembers(javax.lang.model.element.TypeElement)
	 */
	public static List<? extends Element> getAllMembers(TypeElement type) {
		return Environment.getEnvironment().getElementUtils()
				.getAllMembers(type);
	}

	/**
	 * @param type
	 * @return
	 * @see javax.lang.model.util.Elements#getBinaryName(javax.lang.model.element.TypeElement)
	 */
	public static Name getBinaryName(TypeElement type) {
		return Environment.getEnvironment().getElementUtils()
				.getBinaryName(type);
	}

	/**
	 * @param value
	 * @return
	 * @see javax.lang.model.util.Elements#getConstantExpression(java.lang.Object)
	 */
	public static String getConstantExpression(Object value) {
		return Environment.getEnvironment().getElementUtils()
				.getConstantExpression(value);
	}

	/**
	 * @param e
	 * @return
	 * @see javax.lang.model.util.Elements#getDocComment(javax.lang.model.element.Element)
	 */
	public static String getDocComment(Element e) {
		return Environment.getEnvironment().getElementUtils().getDocComment(e);
	}

	/**
	 * @param a
	 * @return
	 * @see javax.lang.model.util.Elements#getElementValuesWithDefaults(javax.lang.model.element.AnnotationMirror)
	 */
	public static Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(
			AnnotationMirror a) {
		return Environment.getEnvironment().getElementUtils()
				.getElementValuesWithDefaults(a);
	}

	/**
	 * @param cs
	 * @return
	 * @see javax.lang.model.util.Elements#getName(java.lang.CharSequence)
	 */
	public static Name getName(CharSequence cs) {
		return Environment.getEnvironment().getElementUtils().getName(cs);
	}

	/**
	 * @param name
	 * @return
	 * @see javax.lang.model.util.Elements#getPackageElement(java.lang.CharSequence)
	 */
	public static PackageElement getPackageElement(CharSequence name) {
		return Environment.getEnvironment().getElementUtils()
				.getPackageElement(name);
	}

	/**
	 * @param type
	 * @return
	 * @see javax.lang.model.util.Elements#getPackageOf(javax.lang.model.element.Element)
	 */
	public static PackageElement getPackageOf(Element type) {
		return Environment.getEnvironment().getElementUtils()
				.getPackageOf(type);
	}

	/**
	 * @param name
	 * @return
	 * @see javax.lang.model.util.Elements#getTypeElement(java.lang.CharSequence)
	 */
	public static TypeElement getTypeElement(CharSequence name) {
		return Environment.getEnvironment().getElementUtils()
				.getTypeElement(name);
	}

	/**
	 * @param hider
	 * @param hidden
	 * @return
	 * @see javax.lang.model.util.Elements#hides(javax.lang.model.element.Element,
	 *      javax.lang.model.element.Element)
	 */
	public static boolean hides(Element hider, Element hidden) {
		return Environment.getEnvironment().getElementUtils()
				.hides(hider, hidden);
	}

	/**
	 * @param e
	 * @return
	 * @see javax.lang.model.util.Elements#isDeprecated(javax.lang.model.element.Element)
	 */
	public static boolean isDeprecated(Element e) {
		return Environment.getEnvironment().getElementUtils().isDeprecated(e);
	}

	/**
	 * @param overrider
	 * @param overridden
	 * @param type
	 * @return
	 * @see javax.lang.model.util.Elements#overrides(javax.lang.model.element.ExecutableElement,
	 *      javax.lang.model.element.ExecutableElement,
	 *      javax.lang.model.element.TypeElement)
	 */
	public static boolean overrides(ExecutableElement overrider,
			ExecutableElement overridden, TypeElement type) {
		return Environment.getEnvironment().getElementUtils()
				.overrides(overrider, overridden, type);
	}

	/**
	 * @param w
	 * @param elements
	 * @see javax.lang.model.util.Elements#printElements(java.io.Writer,
	 *      javax.lang.model.element.Element[])
	 */
	public static void printElements(Writer w, Element... elements) {
		Environment.getEnvironment().getElementUtils()
				.printElements(w, elements);
	}

	/**
	 * Return the property name of the element.
	 * 
	 * The element should be a getter or setter method.
	 * 
	 * @param e
	 *            the initial name
	 * @return the property name or the element name.
	 */
	public static String getPropertyName(String name) {
		if (name.startsWith("get") || name.startsWith("get")) {
			return Introspector.decapitalize(name.substring(3));
		}
		if (name.startsWith("is")) {
			return Introspector.decapitalize(name.substring(2));
		}
		return name;
	}

}
