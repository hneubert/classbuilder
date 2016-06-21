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

/**
 * The MethodHandler interface allows implementing methods.<br>
 * A method handler can optinally implement the MethodSelector interface.
 * 
 * Example:
 * 	public static class GetterHandler extends AbstractMethodHandler {
 * 		{@literal @}Override
 * 		public void handle(HandlerContext context) throws BuilderException, HandlerException {
 * 			String name = getName().substring(3);
 * 			String fieldName = name.substring(0, 1).toLowerCase() + name.substring(1);
 * 			
 * 			Return(get(fieldName));
 * 		}
 * 	}
 * 
 * @see MethodSelector
 * @see AbstractMethodHandler
 * @see ConstructorHandler
 * @see ProxyHandler
 */
public interface MethodHandler {
	/**
	 * Implements a method.
	 * @param context handler context
	 * @param cls current class
	 * @param method current method
	 * @param declaration declared method or null
	 * @throws BuilderException implementation error
	 * @throws HandlerException handler error
	 */
	public void handle(HandlerContext context, IClass cls, IMethod method, Method declaration) throws BuilderException, HandlerException;
}
