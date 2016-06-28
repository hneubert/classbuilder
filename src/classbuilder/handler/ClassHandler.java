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

import classbuilder.BuilderException;
import classbuilder.IClass;

/**
 * The ClassHandler interface allows adding methods, constructors, fields and annotations to a class.
 * <br>
 * Example:<pre><code>
 * 	public static class ClassHandler extends AbstractClassHandler {
 * 		{@literal @}Override
 * 		public void handle(HandlerContext context) throws BuilderException, HandlerException {
 * 			addAnnotation(AnyAnnotation.class);
 * 			...
 * 		}
 * 	}</code></pre>
 * 
 * @see MethodHandler
 * @see ConstructorHandler
 * @see ProxyHandler
 */
public interface ClassHandler {
	/**
	 * Implements a class.
	 * @param context handler context
	 * @param cls current class
	 * @throws BuilderException implementation error
	 * @throws HandlerException handler error
	 */
	public void handle(HandlerContext context, IClass cls) throws BuilderException, HandlerException;
}
