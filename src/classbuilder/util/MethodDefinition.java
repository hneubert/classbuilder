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

package classbuilder.util;

import classbuilder.BuilderException;
import classbuilder.IClass;
import classbuilder.IMethod;

/**
 * MethodDefinition is a helper class for method implementation.
 */
public abstract class MethodDefinition extends MethodDelegate implements IMethod, IClass {
	
	/**
	 * Creates a new method without a return value.
	 * @param cls IClass object
	 * @param modifiers modifiers
	 * @param name method name
	 * @param params parameter types
	 * @throws BuilderException implementation error
	 */
	public MethodDefinition(IClass cls, int modifiers, String name, Class<?> ...params) throws BuilderException {
		super(cls, cls.addMethod(modifiers, name, params));
		implement();
		method.End();
	}
	
	/**
	 * Creates a new method with a return value.
	 * @param cls IClass object
	 * @param modifiers modifiers
	 * @param returnType return type
	 * @param name method name
	 * @param params parameter types
	 * @throws BuilderException implementation error
	 */
	public MethodDefinition(IClass cls, int modifiers, Class<?> returnType, String name, Class<?> ...params) throws BuilderException {
		super(cls, cls.addMethod(modifiers, returnType, name, params));
		implement();
		method.End();
	}
	
	/**
	 * Implements a method.
	 * @throws BuilderException implementation error
	 */
	protected abstract void implement() throws BuilderException;
	
}
