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

package classbuilder.handler;

/**
 * Thrown when a handler or the ObjectFactory fails.
 * 
 * @see classbuilder.BuilderException
 */
public class HandlerException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public static final int RESULT_VALUE_NEVER_SET	= 1000;
	
	private int error;
	
	/**
	 * Creates a new HandlerException.
	 * @param error specific error id
	 * @param value additional value
	 */
	public HandlerException(int error, String value) {
		super(buildMessage(error, value));
		this.error = error;
	}
	
	/**
	 * Creates a new HandlerException.
	 * @param error specific error id
	 * @param value additional value
	 * @param exception root cause
	 */
	public HandlerException(int error, String value, Exception exception) {
		super(buildMessage(error, value), exception);
		this.error = error;
	}
	
	/**
	 * Creates a new HandlerException.
	 * @param message error message
	 */
	public HandlerException(String message) {
		super(message);
	}
	
	/**
	 * Creates a new HandlerException.
	 * @param message error message
	 * @param exception root cause
	 */
	public HandlerException(String message, Exception exception) {
		super(message, exception);
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
		case RESULT_VALUE_NEVER_SET :
			return "result value never set" + addValue(value);
		default:
			return "unknown error";
		}
	}
	
	private static String addValue(String value) {
		if (value != null) return ": " + value;
		return "";
	}
	
}
