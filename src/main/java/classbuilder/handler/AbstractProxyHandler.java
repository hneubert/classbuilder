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
import classbuilder.IClass;
import classbuilder.IMethod;
import classbuilder.Variable;
import classbuilder.util.MethodDelegate;

/**
 * This class provides a skeletal implementation of a ProxyHandler interface and wraps an IClass, an IMethod and a MethodReference object, to simplify the implementation.
 */
public abstract class AbstractProxyHandler extends MethodDelegate implements IMethod, IClass, MethodReference, ProxyHandler {
	
	private MethodReference target;
	
	/**
	 * Sole constructor.
	 */
	public AbstractProxyHandler() {
		super(null, null);
	}
	
	@Override
	public void handle(HandlerContext context, IClass cls, IMethod method, Method declaration, MethodReference target) throws BuilderException, HandlerException {
		super.method = method;
		super.cls = cls;
		this.target = target;
		handle(context);
	}
	
	/**
	 * Implements a method wrapper.
	 * @param context handler context
	 * @throws BuilderException implementation error
	 * @throws HandlerException handler error
	 * @see #invoke(Object[])
	 * @see #getParameters()
	 * @see #Return(Object)
	 * @see #Return()
	 */
	public abstract void handle(HandlerContext context) throws BuilderException, HandlerException;
	
	@Override
	public Variable invoke(Object ...args) throws BuilderException, HandlerException {
		return target.invoke(args);
	}
	
}
