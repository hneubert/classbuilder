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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import classbuilder.BuilderException;
import classbuilder.IMethod;
import classbuilder.Variable;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;
import classbuilder.handler.MethodHandler;
import classbuilder.handler.MethodId;
import classbuilder.handler.MethodReference;
import classbuilder.handler.ProxyHandler;

public class MethodInvocationHelper implements MethodReference {
	
	private Method declaration;
	private MethodId target;
	private HandlerContext methodHandler;
	private IMethod method;
	private Variable[] parameters;
	private List<HandlerContext> proxyList;
	private Variable result;
	
	private MethodWrapper methodWrapper;
	
	private int index = 0;
	
	public MethodInvocationHelper() {
		proxyList = new ArrayList<HandlerContext>();
	}
	
	public void setMethodHandler(HandlerContext methodHandler) {
		if (this.methodHandler == null || methodHandler == null || this.methodHandler.getPriority() <= methodHandler.getPriority()) {
			this.methodHandler = methodHandler;
		}
	}
	
	public void setTarget(MethodId target) {
		this.target = target;
		if (target.getDeclaration() instanceof Method) {
			declaration = (Method)target.getDeclaration();
		}
	}
	
	public HandlerContext getMethodHandler() {
		return methodHandler;
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
	
	public String getName() {
		return target.getName();
	}
	
//	public MethodId getMethod() {
//		return target;
//	}
	
	private Variable[] getParameters() {
		if (parameters == null) {
			parameters = new Variable[method.getParameters().length];
			int i = 0;
			for (Variable var : method.getParameters()) {
				parameters[i++] = var;
			}
		}
		return parameters;
	}
	
	public void handle(IMethod method) throws BuilderException, HandlerException {
		this.method = method;
		Collections.sort(proxyList);
		if (method.getReturnType() != null) {
			result = method.addVar(method.getReturnType());
		}
		methodWrapper = new MethodWrapper(method, result);
		invoke((Object[])getParameters());
		//methodWrapper.writeOffsets();
		if (result != null) {
			if (!result.isInitialized()) {
				throw new HandlerException(HandlerException.RESULT_VALUE_NEVER_SET, method.getName());
			}
			method.Return(result);
		} else {
			method.Return();
		}
	}
	
	public Variable invoke(Object ...args) throws BuilderException, HandlerException {
		for (int i = 0; i < args.length; i++) {
			if (args[i] != method.getParameter(i)) {
				method.getParameter(i).set(args[i]);
			}
		}
		
		try {
			if (index < proxyList.size()) {
				index++;
				ProxyHandler handler = (ProxyHandler)proxyList.get(proxyList.size() - index).getHandler().newInstance();
				handler.handle(proxyList.get(proxyList.size() - index), method.getDeclaringClass(), methodWrapper, declaration, this);
				index--;
				methodWrapper.writeOffsets();
			} else if (methodHandler != null) {
				MethodHandler handler = (MethodHandler)methodHandler.getHandler().newInstance();
				handler.handle(methodHandler, method.getDeclaringClass(), methodWrapper, declaration);
				methodWrapper.writeOffsets();
			} else if (target != null) {
				if (result == null) {
					method.Super().invoke(target.getName(), (Object[])getParameters());
				} else {
					result.set(method.Super().invoke(target.getName(), (Object[])getParameters()));
				}
			}
		} catch (InstantiationException e) {
			throw new HandlerException("handler instanziation faild: ", e);
		} catch (IllegalAccessException e) {
			throw new HandlerException("handler instanziation faild: ", e);
		}
		return result;
	}
	
}
