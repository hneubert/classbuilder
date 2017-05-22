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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import classbuilder.BuilderAccessException;
import classbuilder.BuilderSyntaxException;
import classbuilder.BuilderTypeException;
import classbuilder.IAnnotation;
import classbuilder.IClass;
import classbuilder.IField;
import classbuilder.IMethod;
import classbuilder.LValue;
import classbuilder.RValue;
import classbuilder.Variable;

/**
 * The MethodDelegate class delegates all methods of IMethod and IClass and can be inherited.
 */
public class MethodDelegate extends ClassDelegate implements IMethod {
	
	protected IMethod method;
	
	/**
	 * Creates a new MethodDelegate object.
	 * @param cls IClass object
	 * @param method IMethod object
	 */
	public MethodDelegate(IClass cls, IMethod method) {
		super(cls);
		this.method = method;
	}
	
	@Override
	public String getName() {
		return method.getName();
	}
	
	@Override
	public int getModifiers() {
		return method.getModifiers();
	}
	
	@Override
	public IClass getDeclaringClass() {
		return method.getDeclaringClass();
	}
	
	@Override
	public Class<?> getReturnType() {
		return method.getReturnType();
	}
	
	@Override
	public Variable addVar(Class<?> type) throws BuilderSyntaxException, BuilderTypeException {
		return method.addVar(type);
	}
	
	@Override
	public Variable[] getParameters() {
		return method.getParameters();
	}
	
	@Override
	public Class<?>[] getParameterTypes() {
		return method.getParameterTypes();
	}
	
	@Override
	public Variable getParameter(int index) {
		return method.getParameter(index);
	}
	
	@Override
	public List<Variable> getLocals() {
		return method.getLocals();
	}
	
	@Override
	public IAnnotation addAnnotation(Class<?> type) throws BuilderTypeException {
		return method.addAnnotation(type);
	}
	
	@Override
	public Collection<IAnnotation> getAnnotations() {
		return method.getAnnotations();
	}
	
	@Override
	public boolean isClosed() {
		return method.isClosed();
	}
	
	@Override
	public boolean isConstructor() {
		return method.isConstructor();
	}
	
	@Override
	public RValue New(Class<?> type, Object... args) throws BuilderSyntaxException, BuilderAccessException, BuilderTypeException {
		return method.New(type, args);
	}
	
	@Override
	public void Throw(RValue exception) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		method.Throw(exception);
	}
	
	@Override
	public void If(RValue condition) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		method.If(condition);
	}
	
	@Override
	public void Else() throws BuilderSyntaxException {
		method.Else();
	}
	
	@Override
	public void ElseIf(RValue condition) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		method.ElseIf(condition);
	}
	
	@Override
	public Variable ForEach(RValue iterable) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return method.ForEach(iterable);
	}
	
	@Override
	public void While(RValue condition) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		method.While(condition);
	}
	
	@Override
	public void Break() throws BuilderSyntaxException {
		method.Break();
	}
	
	@Override
	public void Continue() throws BuilderSyntaxException {
		method.Continue();
	}
	
	@Override
	public void Try() throws BuilderSyntaxException {
		method.Try();
	}
	
	@Override
	public void TryWithFinally() throws BuilderSyntaxException {
		method.TryWithFinally();
	}
	
	@Override
	public Variable Catch(Class<?> exception) throws BuilderSyntaxException, BuilderTypeException {
		return method.Catch(exception);
	}
	
	@Override
	public void Finally() throws BuilderSyntaxException {
		method.Finally();
	}
	
	@Override
	public void Synchronized(RValue object) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		method.Synchronized(object);
	}
	
	@Override
	public void Return() throws BuilderSyntaxException, BuilderTypeException {
		method.Return();
	}
	
	@Override
	public void Return(Object value) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		method.Return(value);
	}
	
	@Override
	public LValue get(IField field) throws BuilderSyntaxException, BuilderAccessException {
		return method.get(field);
	}
	
	@Override
	public LValue get(String field) throws BuilderSyntaxException, BuilderAccessException {
		return method.get(field);
	}
	
	@Override
	public LValue get(Field field) throws BuilderSyntaxException, BuilderAccessException {
		return method.get(field);
	}
	
	@Override
	public RValue $(Object value) throws BuilderSyntaxException, BuilderTypeException {
		return method.$(value);
	}
	
	@Override
	public RValue invoke(IMethod method, Object... args) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return this.method.invoke(method, args);
	}
	
	@Override
	public RValue invoke(String method, Object ...args) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return this.method.invoke(method, args);
	}
	
	@Override
	public RValue invoke(Method method, Object ...args) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return this.method.invoke(method, args);
	}
	
	@Override
	public void End() throws BuilderSyntaxException {
		method.End();
	}
	
	@Override
	public RValue This() throws BuilderSyntaxException, BuilderAccessException {
		return method.This();
	}
	
	@Override
	public RValue Super() throws BuilderSyntaxException, BuilderAccessException {
		return method.Super();
	}

	@Override
	public Variable ForEach(RValue iterable, Class<?> elementType) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return method.ForEach(iterable, elementType);
	}
	
}
