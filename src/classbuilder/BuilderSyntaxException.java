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
 * Thrown when a forbidden IClass or IMethod method is called.
 */
public class BuilderSyntaxException extends BuilderException {
	private static final long serialVersionUID = 1L;
	
	public static final int ELSE_NOT_ALLOWED =			100;	// Else
	public static final int CATCH_NOT_ALLOWED =			101;	// Catch
	//public static final int ALREADY_CLOSED =			102;	// End
	public static final int EXPRESSION_ALREADY_IN_USE =	103;
	public static final int EXPRESSION_ALREADY_WRITTEN =104;
	public static final int FREESTANDING_EXPRESSION =	105;
	public static final int FUNCTION_IS_CLOSED =		106;	// <all>
	public static final int NO_CLASS_PRESENT =			107;
	public static final int NO_METHOD_PRESENT =			108;
	public static final int FIELD_NOT_ALLOWED =			109;	// evtl. weglassen
	public static final int METHOD_NOT_ALLOWED =		110;
	public static final int NO_SUPER_CONSTRUCTOR_ALLOWED = 111;
	public static final int NO_LVALUE = 				112;
	public static final int NO_SUPER_CONSTRUCTOR =		113;
	public static final int BREAK_NOT_ALLOWED =			114;
	public static final int CONTINUE_NOT_ALLOWED =		115;
	public static final int UNUSED_EXPRESSION =			116;
	public static final int RETURN_VALUE_REQUIRED =		117;
	public static final int FRAGMENT_IS_CLOSED =		118;
	public static final int TOO_MANY_VARIABLES =		119;
	public static final int ENUM_CONST_CREATION_FAILD =	120;
	public static final int ENUM_CONST_NOT_ALLOWED =	121;
	public static final int SUPER_ALREADY_CALLED =		122;
	
	private int error;
	
	/**
	 * Creates a new BuilderSyntaxException.
	 * @param source error source object
	 * @param error specific error id
	 */
	public BuilderSyntaxException(Object source, int error) {
		super(source, buildMessage(error, ""));
		this.error = error;
	}
	
	/**
	 * Creates a new BuilderSyntaxException.
	 * @param source error source object
	 * @param error specific error id
	 * @param exception root cause
	 */
	public BuilderSyntaxException(Object source, int error, Exception exception) {
		super(source, buildMessage(error, ""), exception);
		this.error = error;
	}
	
	/**
	 * Creates a new BuilderSyntaxException.
	 * @param source error source object
	 * @param error specific error id
	 * @param value additional value
	 */
	public BuilderSyntaxException(Object source, int error, String value) {
		super(source, buildMessage(error, value));
		this.error = error;
	}
	
	/**
	 * Creates a new BuilderSyntaxException.
	 * @param source error source object
	 * @param message error message
	 * @param exception root cause
	 */
	public BuilderSyntaxException(Object source, String message, Exception exception) {
		super(source, message, exception);
		this.error = 0;
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
		case NO_CLASS_PRESENT :
			return "no class defined";
		case NO_METHOD_PRESENT :
			return "no function defined";
		case FIELD_NOT_ALLOWED :
			return "fields only on class level allowed";
		case METHOD_NOT_ALLOWED :
			return "methods only on class level allowed";
		case NO_SUPER_CONSTRUCTOR_ALLOWED :
			return "no super constructor allowed";
		case ELSE_NOT_ALLOWED :
			return "else only on if segment allowed";
		case CATCH_NOT_ALLOWED :
			return "catch only on try segment allowed";
		case NO_SUPER_CONSTRUCTOR :
			return "no super constructor defined";
		case EXPRESSION_ALREADY_IN_USE :
			return "expression already in use";
		case EXPRESSION_ALREADY_WRITTEN :
			return "expression already written";
		case FREESTANDING_EXPRESSION :
			return "freestanding r-value";
		case FUNCTION_IS_CLOSED :
			return "method is closed";
		case NO_LVALUE :
			return "not assignable";
		case BREAK_NOT_ALLOWED :
			return "break only on while or for-each segment allowed";
		case CONTINUE_NOT_ALLOWED :
			return "continue only on while or for-each segment allowed";
		case UNUSED_EXPRESSION :
			return "unused expression: " + value;
		case RETURN_VALUE_REQUIRED :
			return "return value required";
		case FRAGMENT_IS_CLOSED :
			return "the current fragment is closed, End() expected";
		case TOO_MANY_VARIABLES :
			return "too many variables, only 64 variables are allowed";
		case ENUM_CONST_CREATION_FAILD :
			return "enum constant could not be created: " + value;
		case ENUM_CONST_NOT_ALLOWED :
			return "enum constant not allowed";
		case SUPER_ALREADY_CALLED :
			return "super constructor already called";
		default:
			return "unknown error";	
		}
	}
	
}
