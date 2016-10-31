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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import classbuilder.BuilderAccessException;
import classbuilder.BuilderSyntaxException;
import classbuilder.BuilderTypeException;
import classbuilder.IAnnotation;
import classbuilder.IClass;
import classbuilder.IConstructor;
import classbuilder.IField;
import classbuilder.IMethod;
import classbuilder.LValue;
import classbuilder.RValue;
import classbuilder.Variable;
import classbuilder.impl.DebugData.LineNumberEntry;

public class DefaultMethod implements IConstructor, VariableInfo {
	public enum FragmentType {
		IF,
		ELSE,
		ELSE_IF,
		WHILE,
		FOR_EACH,
		TRY,
		CATCH,
		FUNCTION,
		CONSTRUCTOR
	}
	
	private static class FragmentData {
		public static int CANCELED = 1;
		public static int CLOSED = 2;
		
		private FragmentType type;
		private TryCatchBlock tryCatch;
		private List<Integer> breakList;
		private List<Integer> continueList;
		
		public int closeMark;
		public int closeState;
		
		public BitSet varBase; // readable from parent
		public BitSet varMark; // mark &= readable -> set at every path (try/catch or if/(elseif)/else)
		public BitSet varReadable; // current readable
		
		public FragmentData(FragmentType type, TryCatchBlock tryCatch, FragmentData parent) {
			this.type = type;
			this.tryCatch = tryCatch;
			if (parent != null) {
				varBase = (BitSet)parent.varReadable.clone();
				varReadable = (BitSet)parent.varReadable.clone();
			} else {
				varBase = new BitSet();
				varReadable = new BitSet();
			}
			varMark = null;
		}
		
		public FragmentType getType() {
			return type;
		}
		
		public void setType(FragmentType type) {
			this.type = type;
			closeMark = closeState;
			closeState = 0;
			if (varMark == null) {
				varMark = (BitSet)varReadable.clone();
			} else {
				varMark.and(varReadable);
			}
			varReadable.and(varBase); // varReadable = varBase
		}
		
		public TryCatchBlock getTryCatch() {
			return tryCatch;
		}
		
		public void Break(int pos) {
			if (breakList == null) {
				breakList = new ArrayList<Integer>();
			}
			breakList.add(pos);
		}
		
		public void Continue(int pos) {
			if (continueList == null) {
				continueList = new ArrayList<Integer>();
			}
			continueList.add(pos);
		}
		
		public void End(int pos) {
			if (breakList == null) {
				breakList = new ArrayList<Integer>();
			}
			breakList.add(pos);
		}
		
		public List<Integer> getBreakList() {
			return breakList;
		}
		
		public List<Integer> getContinueList() {
			return continueList;
		}
	}
	
	// basics
	private String name;
	private Class<?> returnType;
	private int flags;
	private FragmentType type;
	private IClass component;
	
	// locals/parameters
	private List<Variable> vars;
	private List<Variable> params;
	private short localIndex = 1;
	private List<DefaultAnnotation> annotations;
	private Class<?>[] parameterTypes;
	private Variable[] parameters;
	
	// code generation
	private Stack<FragmentData> fragmentData;
	private FragmentData fragment;
	private ConstantPool constantPool;
	private InstructionWriter out;
	private DebugData debug;
	private List<TryCatchBlock> tryCatchList;
	private List<DefaultLValue> instructions;
	private boolean superInvoked;
	
	public DefaultMethod(FragmentType type, int flags, String name, Class<?> returnType, IClass component, ConstantPool constantPool, Class<?> ...params) {
		boolean first;
		String s;
		
		this.constantPool = constantPool;
		this.type = type;
		
		this.flags = flags;
		this.name = name;
		this.returnType = returnType;
		this.component = component;
		this.params = new ArrayList<Variable>();
		this.superInvoked = false;
		
		fragmentData = new Stack<FragmentData>();
		preprocessFragment(FragmentType.FUNCTION, null);
		
		for (Class<?> param : params) {
			DefaultVariable p = new DefaultVariable(this, "$" + localIndex, param, localIndex++);
			set(p.getIndex());
			if (param == long.class || param == double.class) localIndex++;
			this.params.add(p);
		}
		
		vars = new ArrayList<Variable>();
		
		out = new InstructionWriter();
		tryCatchList = new ArrayList<TryCatchBlock>();;
		debug = new DebugData();
		instructions = new ArrayList<DefaultLValue>();
		annotations = new ArrayList<DefaultAnnotation>();
		
		if ((flags & VMConst.DEBUG) != 0) {
			debug.addLine("");
			
			if ("<clinit>".equals(name)) {
				s = "static {";
			} else {
				if (type == FragmentType.FUNCTION) {
					s = VMConst.getModifier(flags) + VMConst.getTypeName(component, returnType) + " " + name + "(";
				} else {
					s = VMConst.getModifier(flags) + component.getSimpleName() + "(";
				}
				
				first = true;
				for (int i = 0; i < this.params.size(); i++) {
					Variable var = this.params.get(i);
					if (type == FragmentType.CONSTRUCTOR && component.isEnum() && i < 2) continue;
					if (!first) s += ", ";
					s += var.getType().getName() + " " + var.getName();
					first = false;
				}
				s += ") {";
			}
			debug.addLine(s);
			debug.incrementLevel();
		}
	}
	
	@Override
	public int getModifiers() {
		return flags;
	}
	
	@Override
	public IClass getDeclaringClass() {
		return component;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Class<?> getReturnType() {
		return returnType;
	}
	
	@Override
	public Variable[] getParameters() {
		if (parameters == null || parameters.length != params.size()) {
			if (this.isConstructor() && component.isEnum()) {
				parameters = new Variable[params.size() - 2];
				for (int i = 2; i < params.size(); i++) {
					parameters[i - 2] = params.get(i);
				}
			} else {
				parameters = params.toArray(new Variable[params.size()]);
			}
		}
		return parameters;
	}
	
	@Override
	public Class<?>[] getParameterTypes() {
		if (parameterTypes == null) {
			parameterTypes = new Class<?>[params.size()];
			for (int i = 0; i < params.size(); i++) {
				parameterTypes[i] = params.get(i).getType();
			}
		}
		return parameterTypes;
	}
	
	@Override
	public List<Variable> getLocals() {
		return vars;
	}
	
	public Variable getParameter(int index) {
		if (index >= params.size()) throw new IndexOutOfBoundsException("invalid parameter index: " + index + " (paramter count: " + params.size() + ")");
		if (this.isConstructor() && component.isEnum()) {
			if (index < -2) throw new IndexOutOfBoundsException("no negative parameter index allowed: " + index);
			return params.get(index + 2);
		} else {
			if (index < 0) throw new IndexOutOfBoundsException("no negative parameter index allowed: " + index);
			return params.get(index);
		}
	}
	
	@Override
	public Variable addVar(Class<?> varType) throws BuilderSyntaxException, BuilderTypeException {
		testClosed();
		if (varType == null || varType == void.class || varType == Void.class) throw new BuilderTypeException(this, varType);
		return addVar(varType, false);
	}
	
	private DefaultVariable addVar(Class<?> varType, boolean hidden) throws BuilderSyntaxException {
		if (params.size() + vars.size() + 1 >= 64) throw new BuilderSyntaxException(this, BuilderSyntaxException.TOO_MANY_VARIABLES);
		DefaultVariable var = new DefaultVariable(this, "$" + localIndex, varType, localIndex++);
		if (varType == long.class || varType == double.class) localIndex++;
		vars.add(var);
		if ((flags & VMConst.DEBUG) != 0 && !hidden) {
			debug.addLine(var.toString());
		}
		return var;
	}
	
	@Override
	public void invokeSuper(Object... args) throws BuilderSyntaxException, BuilderAccessException, BuilderTypeException {
		testClosed();
		testStatic();
		if (!isConstructor() || out.getPos() != 0) throw new BuilderSyntaxException(this, BuilderSyntaxException.NO_SUPER_CONSTRUCTOR_ALLOWED);
		if (superInvoked) throw new BuilderSyntaxException(this, BuilderSyntaxException.SUPER_ALREADY_CALLED);
		Super().invoke("<init>", args);
		superInvoked = true;
	}
	
	public byte[] getCode() {
		return out.toByteArray();
	}
	
	public List<TryCatchBlock> getTryCatch() {
		return tryCatchList;
	}
	
	public DebugData getDebugData() {
		return debug;
	}
	
	@Override
	public RValue This() throws BuilderSyntaxException, BuilderAccessException {
		testClosed();
		testStatic();
		DefaultLValue value = new DefaultLValue(this, (DefaultLValue)null, NodeType.THIS, null, component, IClass.CURRENT_CLASS_TYPE);
		instructions.add(value);
		return value;
	}
	
	@Override
	public RValue Super() throws BuilderSyntaxException, BuilderAccessException {
		testClosed();
		testStatic();
		DefaultLValue value = new DefaultLValue(this, null, NodeType.SUPER, null, null, component.getSuperclass());
		instructions.add(value);
		return value;
	}
	
	@Override
	public RValue New(Class<?> type, Object ...args) throws BuilderSyntaxException, BuilderAccessException, BuilderTypeException {
		testClosed();
		if (type == null || type == void.class || type == Void.class || type.isPrimitive()) throw new BuilderTypeException(this, type);
		DefaultLValue value;
		if (type.isArray()) {
			if (args.length != 1) {
				throw new BuilderTypeException(this, BuilderTypeException.ARRAY_LENGTH_REQUIRED);
			}
			value = (DefaultLValue)$(args[0]);
			if (value.getType() == NodeType.NULL || (!int.class.isAssignableFrom(value.getVarType()) && !Integer.class.isAssignableFrom(value.getVarType()))) {
				throw new BuilderTypeException(this, BuilderTypeException.ARRAY_LENGTH_REQUIRED);
			}
			return new DefaultLValue(this, null, NodeType.NEW, null, value, type);
		} else {
			if (type == IClass.CURRENT_CLASS_TYPE) {
				if (component.isInterface() || (component.getModifiers() & VMConst.ABSTRACT) != 0) throw new BuilderTypeException(this, component.getName());
			} else {
				if (type.isInterface() || (type.getModifiers() & VMConst.ABSTRACT) != 0) throw new BuilderTypeException(this, type);
			}
			value = new DefaultLValue(this, null, NodeType.NEW, null, null, type);
			return value.invoke("<init>", args);
		}
	}
	
	@Override
	public void Throw(RValue exception) throws BuilderSyntaxException, BuilderTypeException {
		testClosed();
		exception = $(exception);
		DefaultLValue value, lv = (DefaultLValue)$(exception);
		
		if (lv.getType() == NodeType.NULL) throw new BuilderTypeException(this, null);
		if (!Throwable.class.isAssignableFrom(lv.getVarType())) throw new BuilderTypeException(this, lv.getVarType(), Throwable.class);
		
		value = new DefaultLValue(this, null, NodeType.THROW, null, null, lv.getVarType());
		value.setNext(lv.getRoot(), true);
		instructions.add(value);
		fragment.closeState = FragmentData.CLOSED;
	}
	
	@Override
	public void If(RValue condition) throws BuilderSyntaxException, BuilderTypeException {
		testClosed();
		condition = $(condition);
		DefaultLValue lv = (DefaultLValue)condition;
		if (lv.getType() == NodeType.NULL) throw new BuilderTypeException(this, null);
		if (!boolean.class.isAssignableFrom(lv.getVarType()) && !Boolean.class.isAssignableFrom(lv.getVarType())) {
			throw new BuilderTypeException(this, lv.getVarType(), boolean.class);
		}
		
		((DefaultLValue)condition).remove();
		build();
		
		((DefaultLValue)condition).getRoot().build(null, out, constantPool, true);
		
		if ((flags & VMConst.DEBUG) != 0) {
			debug.addLine("if (" + ((DefaultLValue)condition).getRoot().toString(null) + ") {", out.getPos());
			debug.incrementLevel();
		}
		
		out.write(VMConst.IFEQ, InstructionWriter.PUSH);	// if (!condition)
															//   goto end;
		
		preprocessFragment(FragmentType.IF, null);
	}
	
	@Override
	public void ElseIf(RValue condition) throws BuilderSyntaxException, BuilderTypeException {
		testClosed2();
		if (getType() != FragmentType.IF && getType() != FragmentType.ELSE_IF) {
			throw new BuilderSyntaxException(this, BuilderSyntaxException.ELSE_NOT_ALLOWED);
		}
		
		((DefaultLValue)condition).remove();
		build();
		fragment.setType(FragmentType.ELSE_IF);
		
		if (fragment.closeState == 0) {
			fragment.End(out.getPos());
			out.write(VMConst.GOTO, (short)0);
		}
		out.pop();
		
		((DefaultLValue)condition).getRoot().build(null, out, constantPool, true);
		
		if ((flags & VMConst.DEBUG) != 0) {
			debug.decrementLevel();
			debug.addLine("} else if (" + ((DefaultLValue)condition).getRoot().toString(null) + ") {", out.getPos());
			debug.incrementLevel();
		}
		
		out.write(VMConst.IFEQ, InstructionWriter.PUSH);	// if (!condition)
															//   goto end;
	}
	
	@Override
	public void Else() throws BuilderSyntaxException {
		testClosed2();
		if (getType() != FragmentType.IF && getType() != FragmentType.ELSE_IF) {
			throw new BuilderSyntaxException(this, BuilderSyntaxException.ELSE_NOT_ALLOWED);
		}
		
		build();
		fragment.setType(FragmentType.ELSE);
		
		if (fragment.closeState == 0) {
			fragment.End(out.getPos());
			out.write(VMConst.GOTO, (short)0);
		}
		out.pop();
		
		if ((flags & VMConst.DEBUG) != 0) {
			debug.decrementLevel();
			debug.addLine("} else {");
			debug.incrementLevel();
		}
	}
	
	@Override
	public Variable ForEach(RValue iterable) throws BuilderSyntaxException, BuilderTypeException {
		return ForEach(iterable, Object.class);
	}
	
	@Override
	public Variable ForEach(RValue iterable, Class<?> elementType) throws BuilderSyntaxException, BuilderTypeException {
		testClosed();
		iterable = $(iterable);
		boolean array = ((DefaultLValue)iterable).getVarType().isArray();
		if (((DefaultLValue)iterable).getType() == NodeType.NULL) throw new BuilderTypeException(this, "<null>");
		if (elementType == null || elementType == Void.class || elementType.isPrimitive()) throw new BuilderTypeException(this, elementType);
		if (!(Iterable.class.isAssignableFrom(((DefaultLValue)iterable).getVarType()) || array)) {
			throw new BuilderTypeException(this, ((DefaultLValue)iterable).getVarType(), Object[].class, Iterable.class);
		}
		((DefaultLValue)iterable).remove();
		build();
		if (array) ((DefaultLValue)iterable).getRoot().build(null, out, constantPool, true);
		
		DefaultVariable var = null;
		if (array) {
			int level = DefaultLValue.getLevelPrimitive(((DefaultLValue)iterable).getVarType().getComponentType());
			if (level == 1) var = addVar(boolean.class, true);
			if (level == 2) var = addVar(byte.class, true);
			if (level == 3) var = addVar(short.class, true);
			if (level == 4) var = addVar(char.class, true);
			if (level == 5) var = addVar(int.class, true);
			if (level == 6) var = addVar(long.class, true);
			if (level == 7) var = addVar(float.class, true);
			if (level == 8) var = addVar(double.class, true);
			if (level == -1 || var == null) var = addVar(Object.class, true);
		} else {
			var = addVar(elementType, true);
		}
		set(var.getIndex());
		
		try {
			if ((flags & VMConst.DEBUG) != 0) {
				debug.addLine("for (Object " + var.getName() + " : " + ((DefaultLValue)iterable).getRoot().toString(null) + ") {", out.getPos());
				debug.incrementLevel();
			}
			
			if (array) {
				DefaultVariable i = addVar(int.class, true);		// int i = 0
				out.write(VMConst.ICONST_0);
				out.write(VMConst.ISTORE, (byte)i.getIndex());
				
				out.push();
				
				out.write(VMConst.DUP);				// i == array.length
				out.write(VMConst.ARRAYLENGTH);
				out.write(VMConst.ILOAD, (byte)i.getIndex());
				out.write(VMConst.IF_ICMPEQ, InstructionWriter.PUSH_SWAP);	// if (!condition)
													//   goto end;
				
				out.write(VMConst.DUP);				// var = array[i]
				out.write(VMConst.ILOAD, (byte)i.getIndex());
				int level = DefaultLValue.getLevelPrimitive(var.getType());
				if (level == 1 && level == 2) out.write(VMConst.BALOAD);
				if (level == 3) out.write(VMConst.SALOAD);
				if (level == 4) out.write(VMConst.CALOAD);
				if (level == 5) out.write(VMConst.IALOAD);
				if (level == 6) out.write(VMConst.LALOAD);
				if (level == 7) out.write(VMConst.FALOAD);
				if (level == 8) out.write(VMConst.DALOAD);
				if (level == -1) out.write(VMConst.AALOAD);
				
				if (level > 0 && level < 6) out.write(VMConst.ISTORE, (byte)var.getIndex());
				if (level == 6) out.write(VMConst.LSTORE, (byte)var.getIndex());
				if (level == 7) out.write(VMConst.FSTORE, (byte)var.getIndex());
				if (level == 8) out.write(VMConst.DSTORE, (byte)var.getIndex());
				if (level == -1) out.write(VMConst.ASTORE, (byte)var.getIndex());
				
				out.write(VMConst.IINC, (short)(i.getIndex() * 0x100 + 1)); // i++
			} else {
				iterable.invoke("iterator");
				((DefaultLValue)iterable).getRoot().build(null, out, constantPool, true);
				
				out.push();
				out.write(VMConst.DUP);				// it.hasNext();
				out.write(VMConst.INVOKEINTERFACE, constantPool.add(Iterator.class.getMethod("hasNext")));
				out.write((byte)1, (byte)0);
				
				out.write(VMConst.IFEQ, InstructionWriter.PUSH_SWAP);	// if (!condition)
																		//   goto end;
				out.write(VMConst.DUP);									// var = it.next();
				out.write(VMConst.INVOKEINTERFACE, constantPool.add(Iterator.class.getMethod("next")));
				out.write((byte)1, (byte)0);
				if (elementType != Object.class) {
					out.write(VMConst.CHECKCAST, constantPool.add(elementType));
				}
				out.write(VMConst.ASTORE, (byte)var.getIndex());
			}
		} catch (NoSuchMethodException e) {
			throw new BuilderSyntaxException(this, 0);
		} catch (BuilderAccessException e) {
			throw new BuilderSyntaxException(this, 0);
		}
		
		preprocessFragment(FragmentType.FOR_EACH, null);
		return var;
	}
	
	@Override
	public void While(RValue condition) throws BuilderSyntaxException, BuilderTypeException {
		testClosed();
		condition = $(condition);
		DefaultLValue lv = (DefaultLValue)condition;
		if (lv.getType() == NodeType.NULL) throw new BuilderTypeException(this, null, boolean.class);
		if (!boolean.class.isAssignableFrom(lv.getVarType()) && !Boolean.class.isAssignableFrom(lv.getVarType())) {
			throw new BuilderTypeException(this, ((DefaultLValue)condition).getVarType(), boolean.class);
		}
		
		((DefaultLValue)condition).remove();
		build();
		
		if ((flags & VMConst.DEBUG) != 0) {
			debug.addLine("while (" + ((DefaultLValue)condition).getRoot().toString(null) + ") {", out.getPos());
			debug.incrementLevel();
		}
		
		out.push();												// start:
		((DefaultLValue)condition).getRoot().build(null, out, constantPool, true);
		
		out.write(VMConst.IFEQ, InstructionWriter.PUSH_SWAP);	// if (!condition)
																//   goto end;
		preprocessFragment(FragmentType.WHILE, null);
	}
	
	@Override
	public void Break() throws BuilderSyntaxException {
		testClosed();
		FragmentData data = getLoop();
		if (data == null) {
			throw new BuilderSyntaxException(this, BuilderSyntaxException.BREAK_NOT_ALLOWED);
		}
		fragment.closeState = FragmentData.CANCELED;
		build();
		data.Break(out.getPos());
		out.write(VMConst.GOTO, (short)0);
	}
	
	@Override
	public void Continue() throws BuilderSyntaxException {
		testClosed();
		FragmentData data = getLoop();
		if (data == null) {
			throw new BuilderSyntaxException(this, BuilderSyntaxException.CONTINUE_NOT_ALLOWED);
		}
		fragment.closeState = FragmentData.CANCELED;
		build();
		data.Continue(out.getPos());
		out.write(VMConst.GOTO, (short)0);
	}
	
	@Override
	public void Try() throws BuilderSyntaxException {
		testClosed();
		build();
		
		TryCatchBlock tryCatch = new TryCatchBlock();
		tryCatch.setStart((short)(out.getPos()));
		tryCatchList.add(tryCatch);
		if ((flags & VMConst.DEBUG) != 0) {
			debug.addLine("try {");
			debug.incrementLevel();
		}
		preprocessFragment(FragmentType.TRY, tryCatch);
	}
	
	@Override
	public Variable Catch(Class<?> exception) throws BuilderSyntaxException, BuilderTypeException {
		testClosed2();
		if (getType() != FragmentType.TRY) {
			throw new BuilderSyntaxException(this, BuilderSyntaxException.CATCH_NOT_ALLOWED);
		}
		if (exception == null) throw new BuilderTypeException(this, null);
		if (!Throwable.class.isAssignableFrom(exception)) throw new BuilderTypeException(exception, Throwable.class);
		
		build();
		fragment.setType(FragmentType.CATCH);
		
		TryCatchBlock tryCatch = fragment.getTryCatch();
		
		tryCatch.setEnd((short)out.getPos());
		
		DefaultVariable var = addVar(exception, true);
		set(var.getIndex());
		if (fragment.closeState == 0) out.write(VMConst.GOTO, InstructionWriter.PUSH);
		
		if ((flags & VMConst.DEBUG) != 0) {
			debug.decrementLevel();
			debug.addLine("} catch (" + var.getType().getName() + " $" + var.getName() + ") {");
			debug.incrementLevel();
		}
		
		tryCatch.setException(exception);
		tryCatch.setHandler((short)out.getPos());
		
		out.write(VMConst.ASTORE, (byte)var.getIndex());
		
		return var;
	}
	
	@Override
	public void Return() throws BuilderSyntaxException, BuilderTypeException {
		testClosed();
		if (returnType != null && returnType != void.class && returnType != Void.class) throw new BuilderTypeException(this, returnType);
		instructions.add(new DefaultLValue(this, null, NodeType.RETURN, null, null, null));
		fragment.closeState = FragmentData.CLOSED;
	}
	
	@Override
	public void Return(Object value) throws BuilderSyntaxException, BuilderTypeException {
		testClosed();
		if (returnType == null || returnType == void.class || returnType == Void.class) throw new BuilderTypeException(this, returnType);
		DefaultLValue lv = new DefaultLValue(this, null, NodeType.RETURN, null, null, returnType);
		DefaultLValue v = (DefaultLValue)$(value);
		if (VMConst.isAssignable(component, v.getVarType(), returnType) == -1) throw new BuilderTypeException(this, returnType);
		v = (DefaultLValue)v.cast(returnType);
		lv.setNext(v.getRoot(), true);
		instructions.add(lv);
		fragment.closeState = FragmentData.CLOSED;
	}
	
	@Override
	public LValue get(IField field) throws BuilderSyntaxException, BuilderAccessException {
		testClosed();
		if (field == null) throw new BuilderAccessException(this, BuilderAccessException.FIELD_NOT_FOUND, "<null>");
		if (field.getDeclaringClass() != getDeclaringClass()) throw new BuilderAccessException(this, BuilderAccessException.FIELD_NOT_FOUND, field.getName());
		if ((field.getModifiers() & IClass.STATIC) != 0) {
			try {
				return $(component).get(field.getName());
			} catch (BuilderTypeException e) {
				throw new BuilderAccessException(this, -1, "<not accessible>", e);
			}
		} else {
			return This().get(field.getName());
		}
	}
	
	@Override
	public LValue get(String field) throws BuilderSyntaxException, BuilderAccessException {
		try {
			Object f = DefaultLValue.getField(this, IClass.CURRENT_CLASS_TYPE, field, false);
			if (f instanceof IField) {
				return get((IField)f);
			} else {
				return get((Field)f);
			}
		} catch (NoSuchFieldException e) {
			throw new BuilderAccessException(this, BuilderAccessException.FIELD_NOT_FOUND, field);
		}
	}
	
	@Override
	public LValue get(Field field) throws BuilderSyntaxException, BuilderAccessException {
		if (!field.getDeclaringClass().isAssignableFrom(component.getSuperclass())) throw new BuilderAccessException(this, BuilderAccessException.FIELD_NOT_FOUND, field.getName());
		if ((field.getModifiers() & IClass.STATIC) != 0) {
			try {
				return $(field.getDeclaringClass()).get(field);
			} catch (BuilderTypeException e) {
				throw new BuilderAccessException(this, -1, "<not accessible>", e);
			}
		} else {
			return This().get(field);
		}
	}
	
	@Override
	public RValue $(Object value) throws BuilderSyntaxException, BuilderTypeException {
		testClosed();
		DefaultLValue node;
		
		if (value == null) {
			node = new DefaultLValue(this, (DefaultLValue)null, NodeType.NULL, null, null, null);
		} else if (value instanceof Variable) {
			Variable var = (Variable)value;
			node = new DefaultLValue(this, (DefaultLValue)null, NodeType.LGET, value, null, var.getType());
			instructions.add(node);
		} else if (value instanceof RValue) {
			return (RValue)value;
		} else if (value instanceof Class) {
			node = new DefaultLValue(this, null, NodeType.CLASS, null, value, Class.class);
			instructions.add(node);
		} else if (value == component) {
			node = new DefaultLValue(this, null, NodeType.CLASS, null, IClass.CURRENT_CLASS_TYPE, Class.class);
			instructions.add(node);
		} else {
			if (value instanceof Boolean) {
				node = new DefaultLValue(this, (DefaultLValue)null, NodeType.CONST, null, value, boolean.class);
			} else if (value instanceof Byte) {
				node = new DefaultLValue(this, (DefaultLValue)null, NodeType.CONST, null, value, byte.class);
			} else if (value instanceof Short) {
				node = new DefaultLValue(this, (DefaultLValue)null, NodeType.CONST, null, value, short.class);
			} else if (value instanceof Integer) {
				node = new DefaultLValue(this, (DefaultLValue)null, NodeType.CONST, null, value, int.class);
			} else if (value instanceof Long) {
				node = new DefaultLValue(this, (DefaultLValue)null, NodeType.CONST, null, value, long.class);
			} else if (value instanceof Float) {
				node = new DefaultLValue(this, (DefaultLValue)null, NodeType.CONST, null, value, float.class);
			} else if (value instanceof Double) {
				node = new DefaultLValue(this, (DefaultLValue)null, NodeType.CONST, null, value, double.class);
			} else if (value instanceof Character) {
				node = new DefaultLValue(this, (DefaultLValue)null, NodeType.CONST, null, value, char.class);
			} else if (value instanceof String) {
				node = new DefaultLValue(this, (DefaultLValue)null, NodeType.CONST, null, value, String.class);
			} else {
				throw new BuilderTypeException(this, BuilderTypeException.UNSUPPORTED_TYPE);
			}
		}
		
		return node;
	}
		
	@Override
	public RValue invoke(IMethod method, Object ...params) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		testClosed();
		if (method == null) throw new BuilderAccessException(this, BuilderAccessException.METHOD_NOT_FOUND, "<null>");
		if (method.getDeclaringClass() != component) throw new BuilderAccessException(this, BuilderAccessException.METHOD_NOT_FOUND, method.getName());
		if ((method.getModifiers() & IClass.STATIC) != 0) {
			try {
				return ((DefaultLValue)$(component)).invoke(method, params);
			} catch (BuilderTypeException e) {
				throw new BuilderAccessException(this, -1, "<not accessible>", e);
			}
		} else {
			return ((DefaultLValue)This()).invoke(method, params);
		}
	}
	
	@Override
	public RValue invoke(String method, Object ...args) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		DefaultLValue[] newArgs;
		Class<?>[] ac;
		int i;
		
		if (name == null) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_FOUND, "<null>");
		// <init> -> ctor
		
		newArgs = new DefaultLValue[args.length];
		ac = new Class[args.length];
		
		for (i = 0; i < args.length; i++) {
			DefaultLValue lv = (DefaultLValue)$(args[i]);
			ac[i] = lv.varType;
			newArgs[i] = lv;
		}
		
		try {
			IMethod m = component.getMethod(method, ac);
			return invoke(m, (Object[])newArgs);
		} catch (NoSuchMethodException e) {
			try {
				Method m = (Method)DefaultLValue.getMethod(this, component.getSuperclass(), method, ac, false);
				return invoke(m, (Object[])newArgs);
			} catch (NoSuchMethodException e1) {
				throw new BuilderAccessException(this, BuilderAccessException.METHOD_NOT_FOUND, method);
			}
		}
	}
	
	@Override
	public RValue invoke(Method method, Object ...args) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		if (!method.getDeclaringClass().isAssignableFrom(component.getSuperclass())) throw new BuilderAccessException(this, BuilderAccessException.METHOD_NOT_FOUND, method.getName());
		if ((method.getModifiers() & IClass.STATIC) != 0) {
			return $(component).invoke(method, args);
		} else {
			return This().invoke(method, args);
		}
	}
	
	@Override
	public void End() throws BuilderSyntaxException {
		int addReturn = -1;
		testClosed2();
		build();
		
		int pos;
		switch (getType()) {
		case FUNCTION :
			if (returnType != null) {
				if (fragment.closeState != FragmentData.CLOSED) throw new BuilderSyntaxException(this, BuilderSyntaxException.RETURN_VALUE_REQUIRED);
			} else {
				if (isConstructor() && out.getPos() == 0) {
					try {
						invokeSuper();
					} catch (BuilderAccessException e) {
						throw new BuilderSyntaxException(this, BuilderSyntaxException.NO_SUPER_CONSTRUCTOR);
					} catch (BuilderTypeException e) {
						throw new BuilderSyntaxException(this, BuilderSyntaxException.NO_SUPER_CONSTRUCTOR);
					}
					build();
				}
				if (fragment.closeState != FragmentData.CLOSED) {
					addReturn = out.getPos();
					out.write(VMConst.RETURN);
				}
			}
			break;
		case IF :
			out.pop();
		case ELSE :
		case ELSE_IF :
			writeEnd(fragment.getBreakList(), out.getPos());
			break;
		case WHILE :
			pos = out.peek();
			writeContinue(fragment.getContinueList(), pos);
			writeBreak(fragment.getBreakList(), out.getPos());
			out.write(VMConst.GOTO, InstructionWriter.POP);
			out.pop();
			break;
		case FOR_EACH :
			pos = out.peek();
			writeContinue(fragment.getContinueList(), pos);
			writeBreak(fragment.getBreakList(), out.getPos());
			out.write(VMConst.GOTO, InstructionWriter.POP);
			out.pop();
			out.write(VMConst.POP); // <- array or iterator, so pop always ok
			break;
		case CATCH :
			if (fragment.closeMark == 0) out.pop();
			break;
		default:
			break;
		}
		
		if ((flags & VMConst.DEBUG) != 0) {
			debug.decrementLevel();
			if (addReturn == -1) {
				debug.addLine("}");
			} else {
				debug.addLine("}", addReturn);
			}
		}
		postprocessFragment();
	}
	
	private void writeEnd(List<Integer> list, int pos) {
		if (list != null) {
			for (Integer b : list) {
				out.writeOffset(b + 1, (short)(pos - b));
			}
		}
	}
	
	private void writeBreak(List<Integer> list, int pos) {
		if (list != null) {
			for (Integer b : list) {
				out.writeOffset(b + 1, (short)(pos - b + 3));
			}
		}
	}
	
	private void writeContinue(List<Integer> list, int pos) {
		if (list != null) {
			for (Integer b : list) {
				out.writeOffset(b + 1, (short)(pos - b));
			}
		}
	}
	
	private void build() throws BuilderSyntaxException {
		for (DefaultLValue node : instructions) {
			if (!node.isRemoved()) {
				int offset = out.getPos();
				
				if (isConstructor() && !superInvoked) {
					DefaultLValue spr = new DefaultLValue(null, null, NodeType.SUPER, null, null, component.getSuperclass());
					try {
						spr.invoke("<init>");
						spr.build(null, out, constantPool, false);
						superInvoked = true;
					} catch (BuilderAccessException e) {
						throw new BuilderSyntaxException(this, BuilderSyntaxException.NO_SUPER_CONSTRUCTOR, e);
					} catch (BuilderTypeException e) {
						throw new BuilderSyntaxException(this, BuilderSyntaxException.NO_SUPER_CONSTRUCTOR, e);
					}
				}
				
				node.build(null, out, constantPool, false);
				
				if ((flags & VMConst.DEBUG) != 0 && node.isVisible()) {
						debug.addLine(node.toString(null) + ";", offset);
				}
			}
		}
		instructions.clear();
	}
	
	public FragmentType getType() {
		if (fragmentData.size() == 0) {
			return type;
		} else {
			return fragment.getType();
		}
	}
	
	private FragmentData getLoop() {
		for (int i = fragmentData.size() - 1; i >= 0; i--) {
			FragmentData data = fragmentData.elementAt(i);
			if (data.getType() == FragmentType.WHILE || data.getType() == FragmentType.FOR_EACH) return data;
		}
		return null;
	}
	
	public void writeSource(OutputStream out) throws IOException {
		debug.writeSource(out);
	}
	
	public boolean isClosed() {
		return fragmentData.isEmpty();
	}
	
	public InstructionWriter getWriter() throws BuilderSyntaxException {
		testClosed();
		build();
		return out;
	}
	
	@Override
	public boolean isConstructor() {
		return type == FragmentType.CONSTRUCTOR;
	}
	
	private void testClosed2() throws BuilderSyntaxException {
		if (fragmentData.isEmpty()) throw new BuilderSyntaxException(this, BuilderSyntaxException.FUNCTION_IS_CLOSED);
		if ((flags & IClass.ABSTRACT) != 0) throw new BuilderSyntaxException(this, BuilderSyntaxException.FUNCTION_IS_CLOSED);
	}
	
	private void testClosed() throws BuilderSyntaxException {
		if (fragment.closeState != 0) throw new BuilderSyntaxException(this, BuilderSyntaxException.FRAGMENT_IS_CLOSED);
		testClosed2();
	}
	
	private void testStatic() throws BuilderAccessException {
		if ((flags & IClass.STATIC) != 0) throw new BuilderAccessException(this, BuilderAccessException.METHOD_STATIC);
	}
	
	public void prepend(InstructionWriter inst) {
		out.prepend(inst);
	}
	
	@Override
	public IAnnotation addAnnotation(Class<?> type) throws BuilderTypeException {
		DefaultAnnotation annotation = new DefaultAnnotation(this, type, ElementType.METHOD, constantPool);
		annotations.add(annotation);
		return annotation;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection<IAnnotation> getAnnotations() {
		return (List<IAnnotation>)(List<?>)annotations;
	}
	
	public List<DefaultAnnotation> getDefaultAnnotations() {
		return annotations;
	}
	
	public int write(DataOutputStream classFile, int lineNumber) throws IOException {
		classFile.writeShort(getModifiers());//access_flags;
		classFile.writeShort(constantPool.addString(getName()));//name_index;
		
		String sig = "(";
		int locals = 0;
		for (Variable var : params) {
			sig += VMConst.getClassName(var.getType());
			locals++;
			if (var.getType() == long.class || var.getType() == double.class) locals++;
		}
		sig += ")";
		if (getReturnType() != null) {
			sig += VMConst.getClassName(getReturnType());
		} else {
			sig += "V";
		}
		classFile.writeShort(constantPool.addString(sig));//descriptor_index;
		
		int attrCount = 0;
		if (getDefaultAnnotations().size() > 0) attrCount++;
		if ((getModifiers() & VMConst.ABSTRACT) != 0 /*|| attributes != null*/) {
			classFile.writeShort(attrCount);//attributes_count;
		} else {
			classFile.writeShort(attrCount + 1);//attributes_count;
			
			byte [] code = getCode();
			short exceptions = (short)getTryCatch().size();
			
			DebugData debug = getDebugData();
			
			int localsSize = params.size() + getLocals().size() + 1;
			
			classFile.writeShort(constantPool.addString("Code"));//attribute_name_index;
			if ((flags & VMConst.DEBUG) != 0) {
				classFile.writeInt(12 + code.length + exceptions * 8 + debug.getLines().size() * 4 + 8 + localsSize * 10 + 8);//attribute_length;
			} else {
				classFile.writeInt(12 + code.length + exceptions * 8);//attribute_length;
			}
			classFile.writeShort(16);//max_stack;
			for (Variable var : getLocals()) {
				locals++;
				if (var.getType() == long.class || var.getType() == double.class) locals++;
			}
			classFile.writeShort(locals + 1);//max_locals;
			classFile.writeInt(code.length);//code_length;
			classFile.write(code);//code[code_length];
			
			classFile.writeShort(exceptions);//exception_table_length;
			List<TryCatchBlock> exceptionList = getTryCatch();
			Collections.sort(exceptionList);
			for (TryCatchBlock tryCatch : exceptionList) {
				classFile.writeShort(tryCatch.getStart());
				classFile.writeShort(tryCatch.getEnd());
				classFile.writeShort(tryCatch.getHandler());
				classFile.writeShort(constantPool.add(tryCatch.getException()));
			}
			
			if ((flags & VMConst.DEBUG) != 0) {
				classFile.writeShort(2);//attributes_count;
				
				// line numbers
				classFile.writeShort(constantPool.addString("LineNumberTable"));
				classFile.writeInt(debug.getLines().size() * 4 + 2);
				classFile.writeShort(debug.getLines().size());
				lineNumber += getAnnotations().size();
				for (LineNumberEntry line : debug) {
					if (line.getOffset() != -1) {
						classFile.writeShort(line.getOffset()); // start_pc
						classFile.writeShort(line.getLine() + lineNumber - 1); // line_number
					}
				}
				
		    	classFile.writeShort(constantPool.addString("LocalVariableTable")); //attribute_name_index;
				classFile.writeInt(localsSize * 10 + 2); //attribute_length;
				classFile.writeShort(localsSize); //local_variable_table_length;
				
				classFile.writeShort(0); //start_pc;
				classFile.writeShort(code.length); //length;
				classFile.writeShort(constantPool.addString("this")); //name_index;
				classFile.writeShort(constantPool.addString("L" + getName().replace('.', '/') + ";")); //descriptor_index;
				classFile.writeShort(0); //index;
				for (Variable variable : params) {
					DefaultVariable var = (DefaultVariable)variable;
					classFile.writeShort(0); //start_pc;
					classFile.writeShort(code.length); //length;
					classFile.writeShort(constantPool.addString("$" + var.getIndex())); //name_index;
					classFile.writeShort(constantPool.addString(VMConst.getClassName(var.getType()))); //descriptor_index;
					classFile.writeShort(var.getIndex()); //index;
				}
				for (Variable variable : getLocals()) {
					DefaultVariable var = (DefaultVariable)variable;
					classFile.writeShort(0); //start_pc;
					classFile.writeShort(code.length); //length;
					classFile.writeShort(constantPool.addString("$" + var.getIndex())); //name_index;
					classFile.writeShort(constantPool.addString(VMConst.getClassName(var.getType()))); //descriptor_index;
					classFile.writeShort(var.getIndex()); //index;
				}
				
				lineNumber += debug.countLines();
			} else {
				classFile.writeShort(0);//attributes_count;
			}
		}
		
		if (getDefaultAnnotations().size() > 0) {
			DefaultAnnotation.writeAnnotations(classFile, constantPool, annotations);
		}
		return lineNumber;
	}
	
	@Override
	public String toString() {
		return component.getName() + "." + getName();
	}
	
	private void preprocessFragment(FragmentType type, TryCatchBlock tryCatch) {
		FragmentData fragment = new FragmentData(type, tryCatch, this.fragment);
		fragmentData.push(fragment);
		this.fragment = fragment;
	}
	
	private void postprocessFragment() {
		FragmentData old = fragment;
		fragmentData.pop();
		if (fragmentData.size() > 0) {
			fragment = fragmentData.peek();
			if (old.getType() == FragmentType.ELSE || old.getType() == FragmentType.CATCH) {
				old.varMark.and(old.varReadable);
				fragment.varReadable.or(old.varMark);
			}
		}
	}
	
	@Override
	public void set(int index) {
		fragment.varReadable.set(index);
	}
	
	@Override
	public boolean get(int index) {
		return fragment.varReadable.get(index);
	}
	
	public void hideLast() {
		for (int i = instructions.size() - 1; i >= 0; i--) {
			if (!instructions.get(i).isRemoved()) {
				instructions.get(i).setVisible(false);
				break;
			}
		}
	}
}
