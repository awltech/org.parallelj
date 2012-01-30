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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

public class Types {

	public static final Class<?>[] primitives = new Class<?>[] { boolean.class, byte.class,
	        char.class, double.class, float.class, int.class, long.class, short.class, String.class };

	public static final Class<?>[] wrappers = new Class<?>[] { Boolean.class, Byte.class,
	        Character.class, Double.class, Float.class, Integer.class, Long.class, Short.class };

	public static final Class<?>[] temporals = new Class<?>[] { Date.class, Calendar.class,
	        java.sql.Date.class, Time.class, Timestamp.class };

	public static final Class<?>[] lobs = new Class<?>[] { byte[].class, Byte[].class,
	        char[].class, Character[].class };

	public static final Class<?>[] collections = new Class<?>[] { Collection.class, Set.class,
	        List.class, Map.class };

	/**
	 * @param t
	 * @return
	 * @see javax.lang.model.util.Types#asElement(javax.lang.model.type.TypeMirror)
	 */
	public static Element asElement(TypeMirror t) {
		return Environment.getEnvironment().getTypeUtils().asElement(t);
	}

	/**
	 * @param containing
	 * @param element
	 * @return
	 * @see javax.lang.model.util.Types#asMemberOf(javax.lang.model.type.DeclaredType,
	 *      javax.lang.model.element.Element)
	 */
	public static TypeMirror asMemberOf(DeclaredType containing, Element element) {
		return Environment.getEnvironment().getTypeUtils().asMemberOf(containing, element);
	}

	/**
	 * @param p
	 * @return
	 * @see javax.lang.model.util.Types#boxedClass(javax.lang.model.type.PrimitiveType)
	 */
	public static TypeElement boxedClass(PrimitiveType p) {
		return Environment.getEnvironment().getTypeUtils().boxedClass(p);
	}

	/**
	 * @param t
	 * @return
	 * @see javax.lang.model.util.Types#capture(javax.lang.model.type.TypeMirror)
	 */
	public static TypeMirror capture(TypeMirror t) {
		return Environment.getEnvironment().getTypeUtils().capture(t);
	}

	/**
	 * @param t1
	 * @param t2
	 * @return
	 * @see javax.lang.model.util.Types#contains(javax.lang.model.type.TypeMirror,
	 *      javax.lang.model.type.TypeMirror)
	 */
	public static boolean contains(TypeMirror t1, TypeMirror t2) {
		return Environment.getEnvironment().getTypeUtils().contains(t1, t2);
	}

	/**
	 * @param t
	 * @return
	 * @see javax.lang.model.util.Types#directSupertypes(javax.lang.model.type.TypeMirror)
	 */
	public static List<? extends TypeMirror> directSupertypes(TypeMirror t) {
		return Environment.getEnvironment().getTypeUtils().directSupertypes(t);
	}

	/**
	 * @param t
	 * @return
	 * @see javax.lang.model.util.Types#erasure(javax.lang.model.type.TypeMirror)
	 */
	public static TypeMirror erasure(TypeMirror t) {
		return Environment.getEnvironment().getTypeUtils().erasure(t);
	}

	/**
	 * @param componentType
	 * @return
	 * @see javax.lang.model.util.Types#getArrayType(javax.lang.model.type.TypeMirror)
	 */
	public static ArrayType getArrayType(TypeMirror componentType) {
		return Environment.getEnvironment().getTypeUtils().getArrayType(componentType);
	}

	/**
	 * @param containing
	 * @param typeElem
	 * @param typeArgs
	 * @return
	 * @see javax.lang.model.util.Types#getDeclaredType(javax.lang.model.type.DeclaredType,
	 *      javax.lang.model.element.TypeElement,
	 *      javax.lang.model.type.TypeMirror[])
	 */
	public static DeclaredType getDeclaredType(DeclaredType containing, TypeElement typeElem,
	        TypeMirror... typeArgs) {
		return Environment.getEnvironment().getTypeUtils().getDeclaredType(containing, typeElem,
		        typeArgs);
	}

	/**
	 * @param typeElem
	 * @param typeArgs
	 * @return
	 * @see javax.lang.model.util.Types#getDeclaredType(javax.lang.model.element.TypeElement,
	 *      javax.lang.model.type.TypeMirror[])
	 */
	public static DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror... typeArgs) {
		return Environment.getEnvironment().getTypeUtils().getDeclaredType(typeElem, typeArgs);
	}

	/**
	 * @param kind
	 * @return
	 * @see javax.lang.model.util.Types#getNoType(javax.lang.model.type.TypeKind)
	 */
	public static NoType getNoType(TypeKind kind) {
		return Environment.getEnvironment().getTypeUtils().getNoType(kind);
	}

	/**
	 * @return
	 * @see javax.lang.model.util.Types#getNullType()
	 */
	public static NullType getNullType() {
		return Environment.getEnvironment().getTypeUtils().getNullType();
	}

	/**
	 * @param kind
	 * @return
	 * @see javax.lang.model.util.Types#getPrimitiveType(javax.lang.model.type.TypeKind)
	 */
	public static PrimitiveType getPrimitiveType(TypeKind kind) {
		return Environment.getEnvironment().getTypeUtils().getPrimitiveType(kind);
	}

	/**
	 * @param extendsBound
	 * @param superBound
	 * @return
	 * @see javax.lang.model.util.Types#getWildcardType(javax.lang.model.type.TypeMirror,
	 *      javax.lang.model.type.TypeMirror)
	 */
	public static WildcardType getWildcardType(TypeMirror extendsBound, TypeMirror superBound) {
		return Environment.getEnvironment().getTypeUtils()
		        .getWildcardType(extendsBound, superBound);
	}

	/**
	 * @param t1
	 * @param t2
	 * @return
	 * @see javax.lang.model.util.Types#isAssignable(javax.lang.model.type.TypeMirror,
	 *      javax.lang.model.type.TypeMirror)
	 */
	public static boolean isAssignable(TypeMirror t1, TypeMirror t2) {
		return Environment.getEnvironment().getTypeUtils().isAssignable(t1, t2);
	}

	/**
	 * @param t1
	 * @param t2
	 * @return
	 * @see javax.lang.model.util.Types#isSameType(javax.lang.model.type.TypeMirror,
	 *      javax.lang.model.type.TypeMirror)
	 */
	public static boolean isSameType(TypeMirror t1, TypeMirror t2) {
		return Environment.getEnvironment().getTypeUtils().isSameType(t1, t2);
	}

	/**
	 * @param m1
	 * @param m2
	 * @return
	 * @see javax.lang.model.util.Types#isSubsignature(javax.lang.model.type.ExecutableType,
	 *      javax.lang.model.type.ExecutableType)
	 */
	public static boolean isSubsignature(ExecutableType m1, ExecutableType m2) {
		return Environment.getEnvironment().getTypeUtils().isSubsignature(m1, m2);
	}

	/**
	 * @param t1
	 * @param t2
	 * @return
	 * @see javax.lang.model.util.Types#isSubtype(javax.lang.model.type.TypeMirror,
	 *      javax.lang.model.type.TypeMirror)
	 */
	public static boolean isSubtype(TypeMirror t1, TypeMirror t2) {
		return Environment.getEnvironment().getTypeUtils().isSubtype(t1, t2);
	}

	/**
	 * @param t
	 * @return
	 * @see javax.lang.model.util.Types#unboxedType(javax.lang.model.type.TypeMirror)
	 */
	public static PrimitiveType unboxedType(TypeMirror t) {
		return Environment.getEnvironment().getTypeUtils().unboxedType(t);
	}

}
