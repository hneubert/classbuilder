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

package classbuilder.handler.impl;

import java.lang.annotation.Annotation;
import java.util.Map;

import classbuilder.IClass;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.ObjectFactory;

public class DefaultHandlerContext implements HandlerContext {
	private Annotation annotation;
	private Class<?> handler;
	private Object annotatedElement;
	private Map<String, Object> metadata;
	private ObjectFactory objectFactory;
	private IClass subclass;
	private int priority;
	
	public DefaultHandlerContext(ObjectFactory objectFactory, IClass subclass, Annotation annotation, Class<?> handler, int priority, Object annotatedElement, Map<String, Object> metadata) {
		this.objectFactory = objectFactory;
		this.subclass = subclass;
		this.annotation = annotation;
		this.handler = handler;
		this.priority = priority;
		this.annotatedElement = annotatedElement;
		this.metadata = metadata;
	}
	
	@Override
	public Annotation getAnnotation() {
		return annotation;
	}
	
	@Override
	public Class<?> getHandler() {
		return handler;
	}
	
	@Override
	public Object getAnnotatedElement() {
		return annotatedElement;
	}
	
	@Override
	public int getPriority() {
		return priority;
	}
	
	@Override
	public Map<String, Object> getMetadata() {
		return metadata;
	}
	
	@Override
	public int hashCode() {
		return handler.hashCode();
	}
	
	public ObjectFactory getObjectFactory() {
		return objectFactory;
	}
	
	public IClass getSubclass() {
		return subclass;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof HandlerContext) {
			HandlerContext ctx = (HandlerContext)other;
			if (ctx.getHandler() == handler) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public int compareTo(HandlerContext other) {
		return priority - other.getPriority();
	}
}
