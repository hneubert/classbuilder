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

import java.lang.reflect.Method;

import classbuilder.BuilderException;
import classbuilder.IMethod;
import classbuilder.Variable;

/**
 * A MethodReference represents a method handler or an original method implementation.
 */
public class MethodReference {
	private IMethod method;
	private IMethod next;
	private Method superMethod;
	
	public MethodReference(IMethod method, IMethod next) {
		this.method = method;
		this.next = next;
	}
	
	public MethodReference(IMethod method, Method superMethod) {
		this.method = method;
		this.superMethod = superMethod;
	}
	
	/**
	 * Invokes the underlaying method handler or original method implementation.
	 * @param args parameters
	 * @return return value
	 * @throws BuilderException implementation error
	 * @throws HandlerException handler error
	 */
	public Variable invoke(Object... args) throws BuilderException, HandlerException {
		if (hasReturn()) {
			if (next != null) {
				Variable variable = method.addVar(next.getReturnType());
				variable.set(method.invoke(next, args));
				return variable;
			} else if (superMethod != null) {
				Variable variable = method.addVar(superMethod.getReturnType());
				variable.set(method.Super().invoke(superMethod, args));
				return variable;
			} else {
				throw new HandlerException(HandlerException.NO_METHOD_IMPLEMENTATION, method.getName());
			}
		} else {
			if (next != null) {
				method.invoke(next, args);
			} else if (superMethod != null) {
				method.Super().invoke(superMethod, args);
			} else {
				throw new HandlerException(HandlerException.NO_METHOD_IMPLEMENTATION, method.getName());
			}
			return null;
		}
	}
	
	public boolean hasReturn() {
		Class<?> returnType = method.getReturnType();
		return returnType != null && returnType != void.class && returnType != Void.class;
	}
}
