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

import java.lang.annotation.Annotation;
import java.util.Map;

import classbuilder.IClass;

/**
 * The HandlerContext contains basic inforantion for handlers.
 * 
 * @see ClassHandler
 * @see ConstructorHandler
 * @see MethodHandler
 * @see ProxyHandler
 */
public interface HandlerContext extends Comparable<HandlerContext> {
	
	/**
	 * Returns a user defined handler annotation or null.
	 * @return user defined handler annotation or null
	 * 
	 * @see Handler
	 */
	public Annotation getAnnotation();
	
	/**
	 * Returns the handler class.
	 * @return handler class
	 */
	public Class<?> getHandler();
	
	/**
	 * Returns the annotated element.
	 * @return annotated element
	 */
	public Object getAnnotatedElement();
	
	/**
	 * Returns the handler priority.
	 * @return handler priority
	 */
	public int getPriority();
	
	/**
	 * Returns the current ObjectFactory.
	 * @return ObjectFactory
	 */
	public ObjectFactory getObjectFactory();
	
	/**
	 * Returns the current sub class.
	 * @return sub class
	 */
	public IClass getSubclass();
	
	/**
	 * Returns additional meta-data
	 * @return meta-data
	 */
	public Map<String, Object> getMetadata();
}
