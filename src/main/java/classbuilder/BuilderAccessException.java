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

/**
 * Thrown when a method, fiels or constructor is inaccessable.
 */
public class BuilderAccessException extends BuilderException {
	private static final long serialVersionUID = 1L;
	
	public static final int FIELD_NOT_ACCESSABLE =		300;
	public static final int FIELD_NOT_FOUND =			301;
	public static final int FIELD_NOT_STATIC =			302;
	public static final int FIELD_STATIC =				303;
	
	public static final int METHOD_NOT_ACCESSABLE =		304;
	public static final int METHOD_NOT_FOUND =			305;
	public static final int METHOD_NOT_STATIC = 		306;
	public static final int METHOD_STATIC =				307;
	
	public static final int VARIABLE_NOT_INITIALIZED =	308;
	
	private int error;
	
	/**
	 * Creates a new BuilderAccessException.
	 * @param source error source object
	 * @param error specific error id
	 */
	public BuilderAccessException(Object source, int error) {
		super(source, buildMessage(error, ""));
		this.error = error;
	}
	
	/**
	 * Creates a new BuilderAccessException.
	 * @param source error source object
	 * @param error specific error id
	 * @param exception root cause
	 */
	public BuilderAccessException(Object source, int error, Exception exception) {
		super(source, buildMessage(error, ""), exception);
		this.error = error;
	}
	
	/**
	 * Creates a new BuilderAccessException.
	 * @param source error source object
	 * @param error specific error id
	 * @param value additional value
	 */
	public BuilderAccessException(Object source, int error, String value) {
		super(source, buildMessage(error, value));
		this.error = error;
	}
	
	/**
	 * Creates a new BuilderAccessException.
	 * @param source error source object
	 * @param error specific error id
	 * @param value additional value
	 * @param exception root cause
	 */
	public BuilderAccessException(Object source, int error, String value, Exception exception) {
		super(source, buildMessage(error, value), exception);
		this.error = error;
	}
	
	/**
	 * Returns a specific error id.
	 * @return error id
	 */
	public int getError() {
		return error;
	}
	
	private static String buildMessage(int error, String value) {
		switch (error) {
		case FIELD_NOT_ACCESSABLE :
			return "field not accessible" + addValue(value);
		case FIELD_NOT_FOUND :
			return "unknown field: " + value;
		case FIELD_NOT_STATIC :
			return "field is not static" + addValue(value);
		case FIELD_STATIC :
			return "field is static" + addValue(value);
		case METHOD_NOT_ACCESSABLE :
			return "method not accessible" + addValue(value);
		case METHOD_NOT_FOUND :
			return "unknown method: " + value;
		case METHOD_NOT_STATIC :
			return "method is not static" + addValue(value);
		case METHOD_STATIC :
			return "method is static" + addValue(value);
		case VARIABLE_NOT_INITIALIZED :
			return "variable not initalized" + addValue(value);
		default:
			return "unknown error";
		}
	}
	
	private static String addValue(String value) {
		if (value != null) return ": " + value;
		return "";
	}
	
}
