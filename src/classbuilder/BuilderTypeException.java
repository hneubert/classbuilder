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

import classbuilder.impl.VMConst;

/**
 * Thrown when an invalid type is present.
 */
public class BuilderTypeException extends BuilderException {
	private static final long serialVersionUID = 1L;
	
	public static final String UNSUPPORTED_TYPE =			"primitive, String, null or Var needed";
	public static final String BOOLEAN_OR_INTEGER_REQUIRED ="boolean or integer required";
	public static final String NUMERIC_REQUIRED =			"numeric required";
	public static final String BOOLEAN_OR_NUMERIC_REQUIRED ="boolean or numeric required";
	public static final String OBJECT_REQUIRED =			"object required";
	public static final String NO_ARRAY =					"value is not an array";
	public static final String ARRAY_LENGTH_REQUIRED =		"array length (int) required";
	public static final String CLASS_REQUIRED =				"class requred";
	
	/**
	 * Creates a new BuilderTypeException.
	 * @param source error source object
	 * @param message error message
	 */
	public BuilderTypeException(Object source, String message) {
		super(source, message);
	}
	
	/**
	 * Creates a new BuilderTypeException.
	 * @param source error source object
	 * @param message error message
	 * @param exception root cause
	 */
	public BuilderTypeException(Object source, String message, Exception exception) {
		super(source, message, exception);
	}
	
	/**
	 * Creates a new BuilderTypeException.
	 * @param source error source object
	 * @param found actual type
	 * @param required required type
	 */
	public BuilderTypeException(Object source, Class<?> found, Class<?> ...required) {
		super(source, "invalid type: " + VMConst.getTypeName(found) /*+ " required type: " + required.getName()*/);
	}
	
}
