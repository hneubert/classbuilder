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
 * Basic exception for implementation errors.
 */
public class BuilderException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private Object source;
	
	/**
	 * Creates a new BuilderException.
	 * @param source error source object
	 * @param message error message
	 */
	public BuilderException(Object source, String message) {
		super(getMessage(source, message));
		this.source = source;
	}
	
	/**
	 * Creates a new BuilderException.
	 * @param source error source object
	 * @param message error message
	 * @param exception root cause
	 */
	public BuilderException(Object source, String message, Exception exception) {
		super(getMessage(source, message), exception);
		this.source = source;
	}
	
	/**
	 * Returns the source object for the actual exception (IClass/IMethod/IConstructor/IField/null).
	 * @return source object or null
	 */
	public Object getSource() {
		return source;
	}
	
	private static String getMessage(Object source, String message) {
		if (source instanceof IClass) {
			return message + " (at " + ((IClass)source).getName() + ")";
		} else if (source instanceof IMethod) {
			IMethod method = (IMethod)source;
			return message + " (at " + method.getDeclaringClass().getName() + "." + method.getName() + ")";
		} else if (source instanceof IField) {
			IField field = (IField)source;
			return message + " (at " + field.getDeclaringClass().getName() + "." + field.getName() + ")";
		} else {
			return message;
		}
	}
	
}
