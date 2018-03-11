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

package classbuilder.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import classbuilder.BuilderAccessException;
import classbuilder.BuilderSyntaxException;
import classbuilder.BuilderTypeException;
import classbuilder.Assignable;
import classbuilder.Value;
import classbuilder.Variable;

public class DefaultVariable implements Variable {
	protected String name;
	protected Class<?> varType;
	protected short index;
	protected VariableInfo method;
	
	public DefaultVariable(VariableInfo method, String name, Class<?> varType, short index) {
		this.method = method;
		this.name = name;
		this.varType = varType;
		this.index = index;
		
		if (name == null) {
			this.name = "$" + index;
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Class<?> getType() {
		return varType;
	}
	
	@Override
	public String toString() {
		return VMConst.getTypeName(method.getDeclaringClass(), varType) + " " + name + ";";
	}
	
	public short getIndex() {
		return index;
	}
	
	@Override
	public void set(Object value) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		((Assignable)method.$(this)).set(value);
		method.set(index);
	}
	
	@Override
	public Assignable get(String name) throws BuilderSyntaxException, BuilderAccessException {
		check();
		try {
			return method.$(this).get(name);
		} catch (BuilderTypeException e) {
			throw new BuilderSyntaxException(method, e.getMessage(), e);
		}
	}
	
	@Override
	public Assignable get(Object index) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).get(index);
	}
	
	@Override
	public Value invoke(String name, Object... args) throws BuilderSyntaxException, BuilderAccessException {
		check();
		try {
			return method.$(this).invoke(name, args);
		} catch (BuilderTypeException e) {
			throw new BuilderSyntaxException(method, e.getMessage(), e);
		}
	}
	
	@Override
	public Value invoke(Method method, Object... args) throws BuilderSyntaxException, BuilderAccessException {
		check();
		try {
			return this.method.$(this).invoke(method, args);
		} catch (BuilderTypeException e) {
			throw new BuilderSyntaxException(method, e.getMessage(), e);
		}
	}
	
	@Override
	public Value length() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).length();
	}
	
	@Override
	public Value cast(Class<?> type) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).cast(type);
	}
	
	@Override
	public Value add(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).add(a);
	}
	
	@Override
	public Value sub(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).sub(a);
	}
	
	@Override
	public Value mul(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).mul(a);
	}
	
	@Override
	public Value div(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).div(a);
	}
	
	@Override
	public Value mod(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).mod(a);
	}
	
	@Override
	public Value and(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).and(a);
	}
	
	@Override
	public Value or(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).or(a);
	}
	
	@Override
	public Value xor(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).xor(a);
	}
	
	@Override
	public Value shr(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).shr(a);
	}
	
	@Override
	public Value shl(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).shl(a);
	}
	
	@Override
	public Value ushr(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).ushr(a);
	}
	
	@Override
	public Value not() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).not();
	}
	
	@Override
	public Value neg() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).neg();
	}
	
	@Override
	public Value instanceOf(Class<?> a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).instanceOf(a);
	}
	
	@Override
	public Value equal(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).equal(a);
	}
	
	@Override
	public Value notEqual(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).notEqual(a);
	}
	
	@Override
	public Value less(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).less(a);
	}
	
	@Override
	public Value greater(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).greater(a);
	}
	
	@Override
	public Value lessEqual(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).lessEqual(a);
	}
	
	@Override
	public Value greaterEqual(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).greaterEqual(a);
	}

	@Override
	public Assignable get(Field field) throws BuilderSyntaxException, BuilderAccessException {
		check();
		try {
			return method.$(this).get(field);
		} catch (BuilderTypeException e) {
			throw new BuilderSyntaxException(method, e.getMessage(), e);
		}
	}
	
	@Override
	public Value isNull() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).isNull();
	}
	
	@Override
	public Value isNotNull() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).isNotNull();
	}
	
	@Override
	public Class<?> getVarType() {
		return varType;
	}
	
	public boolean isInitialized() {
		return method.get(index);
	}
	
	private void check() throws BuilderAccessException {
		if (!method.get(index)) {
			throw new BuilderAccessException(method, BuilderAccessException.VARIABLE_NOT_INITIALIZED, name + " (" + varType.getName() + ")");
		}
	}
}
