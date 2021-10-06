/* Copyright (c) 2016 Holger Neubert
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF 
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package classbuilder;

import java.util.Collection;

/**
 * The IAnnotation interface represents an annotation
 */
public interface IAnnotation {
	
	/**
	 * Returns the annotation type.
	 * @return annotation type
	 */
	public Class<?> getType();
	
	/**
	 * Sets an annotation parameter.
	 * <p>
	 * Allowed types:
	 * <ul>
	 * <li>boolean
	 * <li>byte
	 * <li>short
	 * <li>int
	 * <li>long
	 * <li>float
	 * <li>double
	 * <li>String
	 * <li>Class
	 * <li>annotations
	 * <li>arrays
	 * </ul>
	 * @param name parameter name
	 * @param value parameter value
	 * @throws BuilderTypeException invalid parameter type
	 * @throws BuilderAccessException invalid parameter name
	 */
	public void setValue(String name, Object value) throws BuilderTypeException, BuilderAccessException;
	
	/**
	 * Returns the value of a parameter.
	 * @param name parameter name
	 * @return parameter value
	 */
	public Object getValue(String name);
	
	/**
	 * Returns all parameter names.
	 * @return all parameter names
	 */
	public Collection<String> getNames();
	
	/**
	 * Returns the number of parameters.
	 * @return number of parameters
	 */
	public int countValues();
	
	/**
	 * Creates a new element annotation.
	 * @param type annotation type
	 * @return new annotation
	 * @throws BuilderTypeException invalid annotation type
	 */
	public IAnnotation createAnnotaton(Class<?> type) throws BuilderTypeException;
}
