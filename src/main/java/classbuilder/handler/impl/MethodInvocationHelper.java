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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import classbuilder.handler.HandlerContext;
import classbuilder.handler.MethodId;

public class MethodInvocationHelper {
	
	private Method declaration;
	private HandlerContext methodHandler;
	private List<HandlerContext> proxyList;
	
	public MethodInvocationHelper() {
		proxyList = new ArrayList<HandlerContext>();
	}
	
	public void setMethodHandler(HandlerContext methodHandler) {
		if (this.methodHandler == null || methodHandler == null || this.methodHandler.getPriority() <= methodHandler.getPriority()) {
			this.methodHandler = methodHandler;
		}
	}
	
	public void setTarget(MethodId target) {
		if (target.getDeclaration() instanceof Method) {
			declaration = (Method)target.getDeclaration();
		}
	}
	
	public HandlerContext getMethodHandler() {
		return methodHandler;
	}
	
	public Method getDeclaration() {
		return declaration;
	}
	
	public void addProxyHandler(HandlerContext proxyHandler) {
		for (int i = 0; i < proxyList.size(); i++) {
			HandlerContext c = proxyList.get(i);
			if (c.getHandler() == proxyHandler.getHandler()) {
				if (c.getPriority() <= proxyHandler.getPriority()) {
					proxyList.set(i, proxyHandler);
				}
				return;
			}
		}
		proxyList.add(proxyHandler);
	}
	
	public void removeProxyHandler(Class<?> cls) {
		Iterator<HandlerContext> it = proxyList.iterator();
		while (it.hasNext()) {
			HandlerContext c = it.next();
			if (c.getHandler() == cls) {
				it.remove();
				return;
			}
		}
	}
	
	public List<HandlerContext> getProxyList() {
		return proxyList;
	}
}
