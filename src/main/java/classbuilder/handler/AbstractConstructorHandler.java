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
import classbuilder.IConstructor;
import classbuilder.util.ConstructorDelegate;

/**
 * This class provides a skeletal implementation of a ConstructorHandler interface and wraps an IClass and an IConstructor object, to simplify the implementation.
 */
public abstract class AbstractConstructorHandler extends ConstructorDelegate implements ConstructorHandler {
	
	/**
	 * Sole constructor.
	 */
	public AbstractConstructorHandler() {
		super(null, null);
	}
	
	@Override
	public void handle(HandlerContext context, IClass cls, IConstructor constructor) throws BuilderException, HandlerException {
		super.constructor = constructor;
		super.method = constructor;
		super.cls = cls;
		handle(context);
	}
	
	/**
	 * Implements a constructor.
	 * @param context handler context
	 * @throws BuilderException implementation error
	 * @throws HandlerException handler error
	 */
	public abstract void handle(HandlerContext context) throws BuilderException, HandlerException;
	
}
