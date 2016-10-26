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
import classbuilder.LValue;
import classbuilder.RValue;
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
		((LValue)method.$(this)).set(value);
		method.set(index);
	}
	
	@Override
	public LValue get(String name) throws BuilderSyntaxException, BuilderAccessException {
		check();
		try {
			return method.$(this).get(name);
		} catch (BuilderTypeException e) {
			throw new BuilderSyntaxException(method, e.getMessage(), e);
		}
	}
	
	@Override
	public LValue get(Object index) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).get(index);
	}
	
	@Override
	public RValue invoke(String name, Object... args) throws BuilderSyntaxException, BuilderAccessException {
		check();
		try {
			return method.$(this).invoke(name, args);
		} catch (BuilderTypeException e) {
			throw new BuilderSyntaxException(method, e.getMessage(), e);
		}
	}
	
	@Override
	public RValue invoke(Method method, Object... args) throws BuilderSyntaxException, BuilderAccessException {
		check();
		try {
			return this.method.$(this).invoke(method, args);
		} catch (BuilderTypeException e) {
			throw new BuilderSyntaxException(method, e.getMessage(), e);
		}
	}
	
	@Override
	public RValue length() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).length();
	}
	
	@Override
	public RValue cast(Class<?> type) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).cast(type);
	}
	
	@Override
	public RValue add(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).add(a);
	}
	
	@Override
	public RValue sub(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).sub(a);
	}
	
	@Override
	public RValue mul(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).mul(a);
	}
	
	@Override
	public RValue div(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).div(a);
	}
	
	@Override
	public RValue mod(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).mod(a);
	}
	
	@Override
	public RValue and(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).and(a);
	}
	
	@Override
	public RValue or(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).or(a);
	}
	
	@Override
	public RValue xor(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).xor(a);
	}
	
	@Override
	public RValue shr(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).shr(a);
	}
	
	@Override
	public RValue shl(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).shl(a);
	}
	
	@Override
	public RValue ushr(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).ushr(a);
	}
	
	@Override
	public RValue not() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).not();
	}
	
	@Override
	public RValue neg() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).neg();
	}
	
	@Override
	public RValue instanceOf(Class<?> a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).instanceOf(a);
	}
	
	@Override
	public RValue equal(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).equal(a);
	}
	
	@Override
	public RValue notEqual(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).notEqual(a);
	}
	
	@Override
	public RValue less(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).less(a);
	}
	
	@Override
	public RValue greater(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).greater(a);
	}
	
	@Override
	public RValue lessEqual(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).lessEqual(a);
	}
	
	@Override
	public RValue greaterEqual(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).greaterEqual(a);
	}

	@Override
	public LValue get(Field field) throws BuilderSyntaxException, BuilderAccessException {
		check();
		try {
			return method.$(this).get(field);
		} catch (BuilderTypeException e) {
			throw new BuilderSyntaxException(method, e.getMessage(), e);
		}
	}
	
	@Override
	public RValue isNull() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		check();
		return method.$(this).isNull();
	}
	
	@Override
	public RValue isNotNull() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
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
