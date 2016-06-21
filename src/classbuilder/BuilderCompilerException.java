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
 * Thrown when class creation or loading fails.
 */
public class BuilderCompilerException extends BuilderException {
	private static final long serialVersionUID = 1L;
	
	public static final int ANNOTATION_VALUE_REQUIRED = 200;
	public static final int METHOD_NOT_CLOSED =			201;
	
	private int error;
	
	/**
	 * Creates a new BuilderCompilerException.
	 * @param source error source object
	 * @param message error message
	 * @param exception root cause
	 */
	public BuilderCompilerException(Object source, String message, Exception exception) {
		super(source, message, exception);
	}
	
	/**
	 * Creates a new BuilderCompilerException.
	 * @param source error source object
	 * @param error specific error id
	 */
	public BuilderCompilerException(Object source, int error) {
		super(source, buildMessage(error, ""));
		this.error = error;
	}
	
	/**
	 * Creates a new BuilderCompilerException.
	 * @param source error source object
	 * @param error specific error id
	 * @param value additional value
	 */
	public BuilderCompilerException(Object source, int error, String value) {
		super(source, buildMessage(error, value));
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
		case ANNOTATION_VALUE_REQUIRED :
			return "annotation value required: ";
		case METHOD_NOT_CLOSED :
			return "method not closed: " + value;
		default:
			return "unknown error";	
		}
	}
	
}
