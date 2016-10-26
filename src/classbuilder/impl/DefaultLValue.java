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

import static classbuilder.IClass.PRIVATE;
import static classbuilder.IClass.PUBLIC;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import classbuilder.BuilderAccessException;
import classbuilder.BuilderSyntaxException;
import classbuilder.BuilderTypeException;
import classbuilder.IClass;
import classbuilder.IConstructor;
import classbuilder.IField;
import classbuilder.IMethod;
import classbuilder.LValue;
import classbuilder.RValue;
import classbuilder.Variable;

public class DefaultLValue implements LValue {
	protected DefaultLValue root;
	protected NodeType type;
	protected Object value;
	protected DefaultLValue next;
	protected Object id;
	protected Class<?> varType;
	protected boolean removed = false;
	protected boolean used = false;
	protected boolean visible = true;
	protected IMethod fragment;
	
	public DefaultLValue(IMethod fragment, DefaultLValue root, NodeType type, Object id, Object value, Class<?> varType) {
		this.type = type;
		this.value = value;
		this.root = root;
		this.id = id;
		this.varType = varType;
		this.fragment = fragment;
		
		if (value instanceof DefaultLValue[]) {
			for (DefaultLValue lv : (DefaultLValue[])value) {
				lv.removed = true;
			}
		} else if (value instanceof DefaultLValue) {
			((DefaultLValue)value).removed = true;
		}
		
		if (root == null) {
			this.root = this;
		} else {
			this.root = root.getRoot();
		}
	}
	
	public DefaultLValue getNext() {
		return next;
	}
	
	public Object getId() {
		return next;
	}
	
	@Override
	public Class<?> getVarType() {
		return varType;
	}
	
	@Override
	public LValue get(String name) throws BuilderSyntaxException, BuilderAccessException {
		DefaultLValue next = null;
		
		if (name == null) throw new BuilderAccessException(fragment, BuilderAccessException.FIELD_NOT_FOUND, "<null>");
		if (varType == null || varType.isPrimitive() || varType.isArray()) throw new BuilderAccessException(fragment, BuilderAccessException.FIELD_NOT_FOUND, getVarTypeName());
		
		try {
			Object f;
			if (this.type == NodeType.CLASS) {
				f = getField(fragment, (Class<?>)value, name, type == NodeType.SUPER);
			} else {
				f = getField(fragment, varType, name, type == NodeType.SUPER);
			}
			Class<?> type;
			boolean isStatic = false;
			
			if (f instanceof IField) {
				type = ((IField)f).getType();
				if ((((IField)f).getModifiers() & IClass.STATIC) != 0) isStatic = true;
			} else {
				type = ((Field)f).getType();
				if ((((Field)f).getModifiers() & IClass.STATIC) != 0) isStatic = true;
			}
			if (isStatic) {
				if (this.type != NodeType.CLASS) throw new BuilderAccessException(this, BuilderAccessException.FIELD_STATIC, name);
				next = new DefaultLValue(fragment, root, NodeType.SGET, f, null, type);
			} else {
				if (this.type == NodeType.CLASS) throw new BuilderAccessException(this, BuilderAccessException.FIELD_NOT_STATIC, name);
				next = new DefaultLValue(fragment, root, NodeType.FGET, f, null, type);
			}
		} catch (NoSuchFieldException e) {
			throw new BuilderAccessException(fragment, BuilderAccessException.FIELD_NOT_FOUND, name, e);
		}
		setNext(next, false);
		return next;
	}
	
	@Override
	public LValue get(Field field) throws BuilderSyntaxException, BuilderAccessException {
		Class<?> type = varType;
		if (this.type == NodeType.CLASS) {
			type = (Class<?>)value;
		}
		if (type == IClass.CURRENT_CLASS_TYPE) {
			type = fragment.getDeclaringClass().getSuperclass();
		}
		if (field == null) throw new BuilderAccessException(fragment, BuilderAccessException.FIELD_NOT_FOUND, "<null>");
		if (!field.getDeclaringClass().isAssignableFrom(type)) throw new BuilderAccessException(fragment, BuilderAccessException.FIELD_NOT_FOUND, field.getName());
		if (this.type == NodeType.CLASS && (field.getModifiers() & Modifier.STATIC) == 0) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_STATIC, field.getName());
		if (this.type != NodeType.CLASS && (field.getModifiers() & Modifier.STATIC) != 0) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_STATIC, field.getName());
		if (!VMConst.isAccessable(field, fragment.getDeclaringClass().getPackage(), field.getDeclaringClass().isAssignableFrom(fragment.getDeclaringClass().getSuperclass()))) throw new BuilderAccessException(fragment, BuilderAccessException.FIELD_NOT_ACCESSABLE, field.getName());
		DefaultLValue next;
		if ((field.getModifiers() & Modifier.STATIC) == 0) {
			next = new DefaultLValue(fragment, root, NodeType.FGET, field, null, field.getType());
		} else {
			next = new DefaultLValue(fragment, root, NodeType.SGET, field, null, field.getType());
		}
		setNext(next, false);
		return next;
	}
	
	private boolean isStatic(int modifiers) {
		return (modifiers & IClass.STATIC) != 0;
	}
	
	@Override
	public LValue get(Object index) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		if (index == null) throw new BuilderTypeException(fragment, "<null>");
		if (varType != null && varType.isArray()) {
			DefaultLValue lv = (DefaultLValue)fragment.$(index);
			check(lv);
			if (lv.getVarType() != int.class && lv.getVarType() != Integer.class) throw new BuilderTypeException(fragment, lv.getVarType());
			DefaultLValue next = new DefaultLValue(fragment, root, NodeType.AGET, null, lv, varType.getComponentType());
			setNext(next, true);
			return next;
		} else {
			throw new BuilderTypeException(fragment, varType);
		}
	}
	
	public RValue invoke(IMethod method, Object ...args) throws BuilderSyntaxException, BuilderAccessException, BuilderTypeException {
		if (method == null) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_FOUND, "<null>");
		if (method.getDeclaringClass() != fragment.getDeclaringClass()) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_FOUND, method.getName());
		if (type == NodeType.CLASS && (method.getModifiers() & Modifier.STATIC) == 0) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_STATIC, method.getName());
		if (type != NodeType.CLASS && (method.getModifiers() & Modifier.STATIC) != 0) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_STATIC, method.getName());
		
		Variable[] types = method.getParameters();
		DefaultLValue[] newArgs;
		int i;
		
		if (types.length != args.length) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_FOUND, Integer.toString(args.length));
		newArgs = new DefaultLValue[args.length];
		
		for (i = 0; i < args.length; i++) {
			DefaultLValue lv = ((DefaultLValue)fragment.$(args[i]));
			check(lv);
			newArgs[i] = cast(lv, types[i].getType()).getRoot();
		}
		
		addIMethod(newArgs, method);
		
		DefaultLValue next = new DefaultLValue(fragment, root, NodeType.INVOKE, method, newArgs, method.getReturnType());
		setNext(next, false);
		
		return next;
	}
	
	@Override
	public RValue invoke(Method method, Object ...args) throws BuilderSyntaxException, BuilderAccessException, BuilderTypeException {
		Class<?> varType = this.varType;
		if (this.type == NodeType.CLASS) {
			varType = (Class<?>)value;
		}
		if (varType == IClass.CURRENT_CLASS_TYPE) {
			varType = fragment.getDeclaringClass().getSuperclass();
		}
		if (method == null) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_FOUND, "<null>");
		if (!method.getDeclaringClass().isAssignableFrom(varType)) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_FOUND);
		if (type == NodeType.CLASS && (method.getModifiers() & Modifier.STATIC) == 0) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_STATIC, method.getName());
		if (type != NodeType.CLASS && (method.getModifiers() & Modifier.STATIC) != 0) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_STATIC, method.getName());
		if (!VMConst.isAccessable(method, fragment.getDeclaringClass().getPackage(), method.getDeclaringClass().isAssignableFrom(fragment.getDeclaringClass().getSuperclass()))) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_ACCESSABLE);
		
		Class<?>[] types = method.getParameterTypes();
		DefaultLValue[] newArgs;
		int i;
		
		if (types.length != args.length) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_FOUND, Integer.toString(args.length));
		newArgs = new DefaultLValue[args.length];
		
		for (i = 0; i < args.length; i++) {
			DefaultLValue lv = ((DefaultLValue)fragment.$(args[i]));
			check(lv);
			newArgs[i] = cast(lv, types[i]).getRoot();
		}
		
		DefaultLValue next = new DefaultLValue(fragment, root, NodeType.INVOKE, method, newArgs, method.getReturnType());
		setNext(next, false);
		
		return next;
	}
	
	@Override
	public RValue invoke(String name, Object... args) throws BuilderSyntaxException, BuilderAccessException, BuilderTypeException {
		DefaultLValue[] newArgs;
		DefaultLValue next = null;
		Class<?>[] types = null;
		Class<?>[] ac;
		int i;
		
		if (varType == null) throw new BuilderTypeException(fragment, "<null>");
		if (name == null) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_FOUND, "<null>");
		// <init> -> ctor
		
		newArgs = new DefaultLValue[args.length];
		ac = new Class[args.length];
		
		for (i = 0; i < args.length; i++) {
			DefaultLValue lv = ((DefaultLValue)fragment.$(args[i]));
			check(lv);
			ac[i] = lv.varType;
			newArgs[i] = lv;
		}
		
		try {
			Object m;
			if (this.type == NodeType.NEW || "<init>".equals(name)) {
				m = getConstructor(fragment, varType, ac, type == NodeType.SUPER);
			} else if (this.type == NodeType.CLASS) {
				m = getMethod(fragment, (Class<?>)value, name, ac, type == NodeType.SUPER);
			} else {
				m = getMethod(fragment, varType, name, ac, type == NodeType.SUPER);
			}
			Class<?> type = varType;
			boolean isStatic = false;
			
			if (m instanceof IMethod) {
				IMethod method = (IMethod)m;
				if (!method.isConstructor()) type = method.getReturnType();
				types = method.getParameterTypes();
				if ((method.getModifiers() & IClass.STATIC) != 0) isStatic = true;
			} else if (m instanceof Method) {
				type = ((Method)m).getReturnType();
				types = ((Method)m).getParameterTypes();
				if ((((Method)m).getModifiers() & IClass.STATIC) != 0) isStatic = true;
			} else if (m instanceof Constructor) {
				types = ((Constructor<?>)m).getParameterTypes();
			}
			
			if (isStatic) {
				if (this.type != NodeType.CLASS) throw new BuilderAccessException(this, BuilderAccessException.METHOD_STATIC, name);
			} else {
				if (this.type == NodeType.CLASS) throw new BuilderAccessException(this, BuilderAccessException.METHOD_NOT_STATIC, name);
			}
			for (i = 0; i < types.length; i++) newArgs[i] = cast(newArgs[i], types[i]).getRoot();
			next = new DefaultLValue(fragment, root, NodeType.INVOKE, m, newArgs, type);
		} catch (NoSuchMethodException e) {
			throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_FOUND, name, e);
		}
		setNext(next, false);
		return next;
	}
	
	private DefaultLValue addIMethod(DefaultLValue[] newArgs, IMethod method) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		if (type == NodeType.THIS && isStatic(method.getModifiers())) type = NodeType.CLASS;
		if (type == NodeType.CLASS && !isStatic(method.getModifiers())) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_STATIC, method.getName());
		Variable[] types = method.getParameters();
		for (int i = 0; i < types.length; i++) newArgs[i] = cast(newArgs[i], types[i].getType()).getRoot();
		return new DefaultLValue(fragment, root, NodeType.INVOKE, method, newArgs, method.getReturnType());
	}
	
	public static Object getMethod(IMethod fragment, Class<?> cls, String name, Class<?>[] types, boolean superAccess) throws NoSuchMethodException, BuilderAccessException {
		boolean protectedAccess = superAccess;
		if (cls == IClass.CURRENT_CLASS_TYPE) {
			protectedAccess = true;
			try {
				return fragment.getDeclaringClass().getMethod(name, types);
			} catch (NoSuchMethodException e) {
				cls = fragment.getDeclaringClass().getSuperclass();
			}
		}
		
		Method method = null;
		int value = 1000;
		
		try {
			method = cls.getMethod(name, types);
		} catch (NoSuchMethodException e) {}
		
		if (method == null) {
			for (Method m : cls.getMethods()) {
				if (!m.getName().equals(name)) continue;
				int q = VMConst.testFucntion(fragment.getDeclaringClass(), types, m.getParameterTypes());
				if (q == -1 || q >= value) continue;
				value = q;
				method = m;
			}
		}
		
		if (method == null) {
			while (cls != null) {
				for (Method m : cls.getDeclaredMethods()) {
					if ((m.getModifiers() & (PRIVATE | PUBLIC)) != 0) continue;
					if (!m.getName().equals(name)) continue;
					int q = VMConst.testFucntion(fragment.getDeclaringClass(), types, m.getParameterTypes());
					if (q == -1 || q >= value) continue;
					value = q;
					method = m;
				}
				cls = cls.getSuperclass();
			}
		}
		
		if (method == null) throw new NoSuchMethodException(name);
		if (!VMConst.isAccessable(method, fragment.getDeclaringClass().getPackage(), protectedAccess)) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_ACCESSABLE);
		return method;
	}
	
	public Object getConstructor(IMethod fragment, Class<?> cls, Class<?>[] types, boolean protectedAccess) throws NoSuchMethodException, BuilderAccessException {
		if (cls == IClass.CURRENT_CLASS_TYPE) {
			protectedAccess = true;
			return fragment.getDeclaringClass().getConstructor(types);
		}
		
		Constructor<?> method = null;
		int value = 1000;
		
		try {
			method = cls.getDeclaredConstructor(types);
		} catch (NoSuchMethodException e) {}
		
		if (method == null) {
			for (Constructor<?> m : cls.getDeclaredConstructors()) {
				int q = VMConst.testFucntion(fragment.getDeclaringClass(), types, m.getParameterTypes());
				if (q == -1 || q >= value) continue;
				value = q;
				method = m;
			}
		}
		
		if (method == null) throw new NoSuchMethodException("<init>");
		if ((method.getModifiers() & IClass.PRIVATE) != 0  || ((method.getModifiers() & IClass.PROTECTED) != 0 && !protectedAccess && !method.getDeclaringClass().getPackage().getName().equals(fragment.getDeclaringClass().getPackage()))) throw new BuilderAccessException(fragment, BuilderAccessException.METHOD_NOT_ACCESSABLE);
		return method;
	}
	
	public static Object getField(IMethod fragment, Class<?> cls, String name, boolean superAccess) throws NoSuchFieldException, BuilderAccessException {
		boolean protectedAccess = superAccess;
		if (cls == IClass.CURRENT_CLASS_TYPE) {
			protectedAccess = true;
			try {
				return fragment.getDeclaringClass().getField(name);
			} catch (NoSuchFieldException e) {
				cls = fragment.getDeclaringClass().getSuperclass();
			}
		}
		
		Field field = null;
		
		try {
			field = cls.getField(name);
		} catch (NoSuchFieldException e) {}
		
		if (field == null) {
			while (cls != null) {
				try {
					field = cls.getDeclaredField(name);
				} catch (NoSuchFieldException e) {}
				cls = cls.getSuperclass();
			}
		}
		
		if (field == null) throw new NoSuchFieldException(name);
		if (!VMConst.isAccessable(field, fragment.getDeclaringClass().getPackage(), protectedAccess)) throw new BuilderAccessException(fragment, BuilderAccessException.FIELD_NOT_ACCESSABLE);
		return field;
	}
	
	private boolean isCastable(Class<?> source, Class<?> dest) {
		if (source == dest) return true;
		if (dest == Object.class || source == Object.class) return true;
		
		int src = VMConst.getLevel(source);
		int dst = VMConst.getLevel(dest);
		if (src == dst) return true;
		if (src > 1 && dst > 1 && source.isPrimitive() && dest.isPrimitive()) return true;
		
		if (dest.isAssignableFrom(source)) return true;
		if (source.isAssignableFrom(dest)) return true;
		return false;
	}
	
	@Override
	public void set(Object value) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		DefaultLValue lv = (DefaultLValue)fragment.$(value);
		check(lv);
		
		if (VMConst.isAssignable(fragment.getDeclaringClass(), lv.varType, varType) == -1) {
			throw new BuilderTypeException(fragment, lv.varType);
		}
		
		lv = cast(lv, varType);
		
		if (id instanceof Field) if ((((Field)id).getModifiers() & IClass.FINAL) != 0) throw new BuilderAccessException(fragment, BuilderAccessException.FIELD_NOT_ACCESSABLE);
		if (id instanceof IField) if ((((IField)id).getModifiers() & IClass.FINAL) != 0 && (((IField)id).getModifiers() & IClass.ENUM) == 0) throw new BuilderAccessException(fragment, BuilderAccessException.FIELD_NOT_ACCESSABLE);
		
		if (type == NodeType.FGET) {
			type = NodeType.FSET;
			setNext(lv.getRoot(), true);
		} else if (type == NodeType.AGET) {
			type = NodeType.ASET;
			setNext(lv.getRoot(), true);
		} else if (type == NodeType.SGET) {
			type = NodeType.SSET;
			setNext(lv.getRoot(), true);
		} else if (type == NodeType.LGET) {
			type = NodeType.LSET;
			setNext(lv.getRoot(), true);
		} else {
			throw new BuilderSyntaxException(fragment, BuilderSyntaxException.NO_LVALUE, lv.getVarType().getName());
		}
	}
	
	@Override
	public RValue length() throws BuilderSyntaxException, BuilderTypeException {
		if (varType != null && varType.isArray()) {
			DefaultLValue lv = new DefaultLValue(fragment, root, NodeType.LENGTH, null, null, int.class);
			setNext(lv, false);//this.next = lv;
			return lv;
		} else {
			throw new BuilderTypeException(fragment, BuilderTypeException.NO_ARRAY);
		}
	}
	
	@Override
	public RValue cast(Class<?> type) throws BuilderSyntaxException, BuilderTypeException {
		if (type == null) throw new BuilderTypeException(fragment, "<null>");
		if (this.type == NodeType.THIS) {
			boolean castable = false;
			if (isCastable(varType, type)) {
				castable = true;
			} else {
				for (Class<?> intf : this.fragment.getDeclaringClass().getInterfaces()) {
					if (isCastable(intf, type)) {
						castable = true;
						break;
					}
				}
			}
			if (!castable) throw new BuilderTypeException(fragment, varType);
			return cast(this, type);
		} else {
			if (!isCastable(varType, type)) {
				throw new BuilderTypeException(fragment, varType);
			}
			return cast(this, type);
		}
	}
	
	public DefaultLValue getRoot() {
		return root;
	}
	
	public void remove() {
		root.removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void setNext(DefaultLValue next, boolean setUsed) throws BuilderSyntaxException {
		if (this.next != null) throw new BuilderSyntaxException(fragment, BuilderSyntaxException.EXPRESSION_ALREADY_IN_USE);
		if (this.root.used) throw new BuilderSyntaxException(fragment, BuilderSyntaxException.EXPRESSION_ALREADY_WRITTEN);
		this.next = next;
		if (setUsed) next.used = true;
		if (next != null) next.removed = true;
	}
	
	protected NodeType getType() {
		return type;
	}
	
	public void build(DefaultLValue parent, InstructionWriter out, ConstantPool constantPool, boolean isValue) throws BuilderSyntaxException {
		short index;
		int level;
		DefaultVariable var;
		
		used = true;
		switch (type) {
		case AGET :
			level = getLevelPrimitive(varType);
			((DefaultLValue)value).build(this, out, constantPool, true);
			if (level > 0 && level < 3) out.write(VMConst.BALOAD);
			if (level == 3) out.write(VMConst.SALOAD);
			if (level == 4) out.write(VMConst.CALOAD);
			if (level == 5) out.write(VMConst.IALOAD);
			if (level == 6) out.write(VMConst.LALOAD);
			if (level == 7) out.write(VMConst.FALOAD);
			if (level == 8) out.write(VMConst.DALOAD);
			if (level == -1) out.write(VMConst.AALOAD);
			if (next != null) {
				doNext(this, out, constantPool, isValue);
			} else if (!root.isRemoved()) {
				throw new BuilderSyntaxException(fragment, BuilderSyntaxException.FREESTANDING_EXPRESSION);
			}
			break;
		case LGET :
			var = (DefaultVariable)id;
			level = getLevelPrimitive(var.getType());
			if (level > 0 && level < 6) out.write(VMConst.ILOAD, (byte)var.getIndex());
			if (level == 6) out.write(VMConst.LLOAD, (byte)var.getIndex());
			if (level == 7) out.write(VMConst.FLOAD, (byte)var.getIndex());
			if (level == 8) out.write(VMConst.DLOAD, (byte)var.getIndex());
			if (level == -1) out.write(VMConst.ALOAD, (byte)var.getIndex());
			if (next != null) {
				doNext(this, out, constantPool, isValue);
			} else if (!root.isRemoved()) {
				throw new BuilderSyntaxException(fragment, BuilderSyntaxException.FREESTANDING_EXPRESSION);
			}
			break;
		case FGET :
			out.write(VMConst.GETFIELD, constantPool.add(id));
			if (next != null) {
				doNext(this, out, constantPool, isValue);
			} else if (!root.isRemoved()) {
				throw new BuilderSyntaxException(fragment, BuilderSyntaxException.FREESTANDING_EXPRESSION);
			}
			break;
		case SGET :
			index = constantPool.add(id);
			out.write(VMConst.GETSTATIC, index);
			doNext(this, out, constantPool, isValue);
			break;
		case ASET :
			level = getLevelPrimitive(varType);
			((DefaultLValue)value).build(this, out, constantPool, true);
			next.build(this, out, constantPool, true);
			if (level > 0 && level < 3) out.write(VMConst.BASTORE);
			if (level == 3) out.write(VMConst.SASTORE);
			if (level == 4) out.write(VMConst.CASTORE);
			if (level == 5) out.write(VMConst.IASTORE);
			if (level == 6) out.write(VMConst.LASTORE);
			if (level == 7) out.write(VMConst.FASTORE);
			if (level == 8) out.write(VMConst.DASTORE);
			if (level == -1) out.write(VMConst.AASTORE);
			break;
		case LSET :
			var = (DefaultVariable)id;
			level = getLevelPrimitive(var.getType());
			next.build(this, out, constantPool, true);
			if (level > 0 && level < 6) out.write(VMConst.ISTORE, (byte)var.getIndex());
			if (level == 6) out.write(VMConst.LSTORE, (byte)var.getIndex());
			if (level == 7) out.write(VMConst.FSTORE, (byte)var.getIndex());
			if (level == 8) out.write(VMConst.DSTORE, (byte)var.getIndex());
			if (level == -1) out.write(VMConst.ASTORE, (byte)var.getIndex());
			break;
		case FSET :
			next.build(this, out, constantPool, true);
			out.write(VMConst.PUTFIELD, constantPool.add(id));
			break;
		case SSET :
			next.build(this, out, constantPool, true);
			out.write(VMConst.PUTSTATIC, constantPool.add(id));
			break;
		case INVOKE :
			DefaultLValue [] args;
			int i;
			
			args = (DefaultLValue[])value;
			for (i = 0; i < args.length; i++) {
				args[i].build(this, out, constantPool, true);
			}
			
			index = constantPool.add(id);
			if ((id instanceof Method && (((Method)id).getModifiers() & Modifier.STATIC) != 0) ||
				(id instanceof IMethod && (((IMethod)id).getModifiers() & Modifier.STATIC) != 0)) {
				out.write(VMConst.INVOKESTATIC, index);
			} else if (parent.getType() == NodeType.SUPER || id instanceof Constructor<?> || id instanceof IConstructor) {
				out.write(VMConst.INVOKESPECIAL, index);
			} else {
				if (parent.varType.isInterface() && parent.varType != IClass.CURRENT_CLASS_TYPE) {
					out.write(VMConst.INVOKEINTERFACE, index);
					out.write((byte)(args.length + 1));
					out.write((byte)0);
				} else {
					out.write(VMConst.INVOKEVIRTUAL, index);
				}
			}
			if (next != null) {
				doNext(this, out, constantPool, isValue);
			} else if (!root.removed && varType != null && varType != void.class && 
					(id instanceof Method || (id instanceof IMethod && !((IMethod)id).isConstructor()))) {
				if (varType == long.class || varType == double.class) {
					out.write(VMConst.POP2);
				} else {
					out.write(VMConst.POP);
				}
			}
			break;
		case CLASS :
			if (next != null) {
				doNext(this, out, constantPool, isValue);
			} else {
				out.write(VMConst.LDC_W, constantPool.add(value));
			}
			break;
		case SUPER :
		case THIS :
			out.write(VMConst.ALOAD_0);
			doNext(this, out, constantPool, isValue);
			break;
		case CONST :
			if (value instanceof Boolean) {
				if ((Boolean)value) {
					out.write(VMConst.ICONST_1);
				} else {
					out.write(VMConst.ICONST_0);
				}
			} else if (value instanceof Long) {
				long v = (Long)value;
				if (v == 0) {
					out.write((byte)(VMConst.LCONST_0));
				} else if (v == 1) {
					out.write((byte)(VMConst.LCONST_1));
				} else {
					out.write(VMConst.LDC2_W, constantPool.add(value));
				}
			} else if (value instanceof Float) {
				float v = (Float)value;
				if (v == 0) {
					out.write((byte)(VMConst.FCONST_0));
				} else if (v == 1) {
					out.write((byte)(VMConst.FCONST_1));
				} else if (v == 2) {
					out.write((byte)(VMConst.FCONST_2));
				} else {
					out.write(VMConst.LDC_W, constantPool.add(value));
				}
			} else if (value instanceof Double) {
				double v = (Double)value;
				if (v == 0) {
					out.write((byte)(VMConst.DCONST_0));
				} else if (v == 1) {
					out.write((byte)(VMConst.DCONST_1));
				} else {
					out.write(VMConst.LDC2_W, constantPool.add(value));
				}
			} else if (value instanceof String) {
				out.write(VMConst.LDC_W, constantPool.add(value));
			} else {
				int v = 0;
				if (value instanceof Byte) v = (Byte)value;
				if (value instanceof Short) v = (Short)value;
				if (value instanceof Integer) v = (Integer)value;
				if (value instanceof Character) v = (Character)value;
				if (v >= -1 && v <= 5) {
					out.write((byte)(VMConst.ICONST_0 + v));
				} else if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE) {
					out.write(VMConst.BIPUSH, (byte)v);
				} else if (v >= Short.MIN_VALUE && v <= Short.MAX_VALUE) {
					out.write(VMConst.SIPUSH, (short)v);
				} else {
					out.write(VMConst.LDC_W, constantPool.add(value));
				}
			}
			doNext(this, out, constantPool, isValue);
			break;
		case NULL :
			out.write(VMConst.ACONST_NULL);
			doNext(this, out, constantPool, isValue);
			break;
		case CAST :
			if (id != null) {
				if ((Byte)id == VMConst.CHECKCAST) {
					out.write((Byte)id, constantPool.add(varType));
				} else {
					out.write((Byte)id);
				}
			}
			doNext(this, out, constantPool, isValue);
			break;
		case NOT :
			byte op = (Byte)id;
			if (op == VMConst.IXOR) {
				level = VMConst.getLevel(varType);
				if (level == 1) out.write(VMConst.ICONST_1);
				if (level == 2) out.write(VMConst.BIPUSH, (byte)0xFF);
				if (level == 3) out.write(VMConst.BIPUSH, (short)0xFFFF);
				if (level == 4) out.write(VMConst.BIPUSH, (short)0xFFFF);
				if (level == 5) out.write(VMConst.ICONST_M1);
			}
			if (op == VMConst.LXOR) out.write(VMConst.LDC2_W, constantPool.add(-1L));
			out.write(op);
			doNext(this, out, constantPool, isValue);
			break;
		case NEG :
			out.write((Byte)id);
			doNext(this, out, constantPool, isValue);
			break;
		case AND :
		case OR :
		case XOR :
		case SHL :
		case SHR :
		case USHR :
		case ADD :
		case SUB :
		case MUL :
		case DIV :
		case MOD :
			((DefaultLValue)value).getRoot().build(null, out, constantPool, true);
			out.write((Byte)id);
			doNext(this, out, constantPool, isValue);
			break;
		case EQUAL :
		case UNEQUAL :
		case LOWER :
		case GREATER :
		case LOWER_EQUAL :
		case GREATER_EQUAL :
			level = VMConst.getLevel(parent.varType);
			((DefaultLValue)value).getRoot().build(null, out, constantPool, true);
			if (level == 6) out.write(VMConst.LCMP);
			if (level == 7) out.write(VMConst.FCMPL);
			if (level == 8) out.write(VMConst.DCMPL);
			out.write((Byte)id, (short)7);
			out.write(VMConst.ICONST_0);
			out.write(VMConst.GOTO, (short)4);
			out.write(VMConst.ICONST_1); // <- [pc: 11, same_locals_1_stack_item, stack: {int}]
			// next <- same
			doNext(this, out, constantPool, isValue);
			break;
		case IS_NULL :
		case IS_NOT_NULL :
			out.write((Byte)id, (short)7);
			out.write(VMConst.ICONST_0);
			out.write(VMConst.GOTO, (short)4);
			out.write(VMConst.ICONST_1); // <- [pc: 11, same_locals_1_stack_item, stack: {int}]
			doNext(this, out, constantPool, isValue);
			break;
		case RETURN :
			if (varType == null) {
				out.write(VMConst.RETURN);
			} else {
				level = getLevelPrimitive(varType);
				next.build(null, out, constantPool, true);
				if (level == -1) out.write(VMConst.ARETURN);
				if (level < 6 && level > 0) out.write(VMConst.IRETURN);
				if (level == 6) out.write(VMConst.LRETURN);
				if (level == 7) out.write(VMConst.FRETURN);
				if (level == 8) out.write(VMConst.DRETURN);
			}
			break;
		case INSTANCEOF :
			out.write(VMConst.INSTANCEOF, constantPool.add(value));
			doNext(this, out, constantPool, isValue);
			break;
		case NEW:
			if (varType.isArray()) {
				((DefaultLValue)value).build(this, out, constantPool, true);
				if (varType.getComponentType().isPrimitive()) {
					out.write(VMConst.NEWARRAY, getArrayType(varType.getComponentType()));
				} else {
					out.write(VMConst.ANEWARRAY, constantPool.add(varType.getComponentType()));
				}
				doNext(null, out, constantPool, isValue);
			} else {
				out.write(VMConst.NEW, constantPool.add(varType));
				out.write(VMConst.DUP);
				doNext(this, out, constantPool, isValue);
			}
			break;
		case THROW :
			doNext(null, out, constantPool, isValue);
			out.write(VMConst.ATHROW);
			break;
		case LENGTH :
			out.write(VMConst.ARRAYLENGTH);
			doNext(null, out, constantPool, isValue);
			break;
		default:
			break;
		}
	}
	
	private void doNext(DefaultLValue parent, InstructionWriter out, ConstantPool constantPool, boolean isValue) throws BuilderSyntaxException {
		if (next == null && !isValue) {
			throw new BuilderSyntaxException(fragment, BuilderSyntaxException.UNUSED_EXPRESSION, root.toString());
		}
		if (next != null) next.build(this, out, constantPool, isValue);
	}
	
	private Class<?> compareClasses(Class<?> a, Class<?> b) throws BuilderTypeException {
		int i, j;
		
		if (a == null || b == null) throw new BuilderTypeException(fragment, "<null>");
		
		i = VMConst.getLevel(a);
		j = VMConst.getLevel(b);
		
		if (i > 0 && j > 0) {
			if (i == VMConst.BOOLEAN || j == VMConst.BOOLEAN) {
				if (i != VMConst.BOOLEAN) throw new BuilderTypeException(fragment, a);
				if (j != VMConst.BOOLEAN) throw new BuilderTypeException(fragment, b);
				return boolean.class;
			} else if (i <= VMConst.INT && j <= VMConst.INT) {
				return int.class;
			} else {
				if (i > j) return a;
				return b;
			}
		} else {
			if (i <= 0) throw new BuilderTypeException(fragment, a);
			else throw new BuilderTypeException(fragment, b);
		}
	}
	
	private byte getArrayType(Class<?> cls) {
		if (cls == boolean.class) return (byte)4;
		if (cls == byte.class) return (byte)8;
		if (cls == short.class) return (byte)9;
		if (cls == char.class) return (byte)5;
		if (cls == int.class) return (byte)10;
		if (cls == long.class) return (byte)11;
		if (cls == float.class) return (byte)6;
		if (cls == double.class) return (byte)7;
		return (byte)10;
	}
	
	public static int getLevelPrimitive(Class<?> cls) {
		if (cls == null || !cls.isPrimitive()) return VMConst.OBJECT;
		return VMConst.getLevel(cls);
	}

	private DefaultLValue unwrap(DefaultLValue value) throws BuilderSyntaxException {
		Class<?> cls = value.varType;
		
		try {
			if (cls == null || cls.isPrimitive()) return value;
			if (cls == Boolean.class) value = (DefaultLValue)value.invoke("booleanValue");
			if (cls == Byte.class) value = (DefaultLValue)value.invoke("byteValue");
			if (cls == Short.class) value = (DefaultLValue)value.invoke("shortValue");
			if (cls == Integer.class) value = (DefaultLValue)value.invoke("intValue");
			if (cls == Long.class) value = (DefaultLValue)value.invoke("longValue");
			if (cls == Float.class) value = (DefaultLValue)value.invoke("floatValue");
			if (cls == Double.class) value = (DefaultLValue)value.invoke("doubleValue");
			if (cls == Character.class) value = (DefaultLValue)value.invoke("charValue");
		} catch (BuilderAccessException e) {
			throw new BuilderSyntaxException(fragment, e.getMessage(), e);
		} catch (BuilderTypeException e) {
			throw new BuilderSyntaxException(fragment, e.getMessage(), e);
		}
		
		return value;
	}
	
	private DefaultLValue wrap(DefaultLValue value) throws BuilderSyntaxException {
		Class<?> cls = value.varType;
		
		try {
			if (cls == boolean.class) return (DefaultLValue)fragment.$(Boolean.class).invoke("valueOf", value);
			if (cls == byte.class) return (DefaultLValue)fragment.$(Byte.class).invoke("valueOf", value);
			if (cls == short.class) return (DefaultLValue)fragment.$(Short.class).invoke("valueOf", value);
			if (cls == int.class) return (DefaultLValue)fragment.$(Integer.class).invoke("valueOf", value);
			if (cls == long.class) return (DefaultLValue)fragment.$(Long.class).invoke("valueOf", value);
			if (cls == float.class) return (DefaultLValue)fragment.$(Float.class).invoke("valueOf", value);
			if (cls == double.class) return (DefaultLValue)fragment.$(Double.class).invoke("valueOf", value);
			if (cls == char.class) return (DefaultLValue)fragment.$(Character.class).invoke("valueOf", value);
		} catch (BuilderAccessException e) {
			throw new BuilderSyntaxException(fragment, e.getMessage(), e);
		} catch (BuilderTypeException e) {
			throw new BuilderSyntaxException(fragment, e.getMessage(), e);
		}
		
		return value;
	}
	
	private byte getCmp(NodeType op, int type) throws BuilderSyntaxException, BuilderTypeException {
		if (type == -1) {
			switch (op) {
			case EQUAL : return VMConst.IF_ACMPEQ;
			case UNEQUAL : return VMConst.IF_ACMPNE;
			case INSTANCEOF : return VMConst.INSTANCEOF;
			case IS_NULL : return VMConst.IFNULL;
			case IS_NOT_NULL : return VMConst.IFNONNULL;
			default : throw new BuilderTypeException(fragment, "<unknown type>");
			}
		} else if (type < 6) {
			switch (op) {
			case EQUAL : return VMConst.IF_ICMPEQ;
			case UNEQUAL : return VMConst.IF_ICMPNE;
			case LOWER : return VMConst.IF_ICMPLT;
			case GREATER : return VMConst.IF_ICMPGT;
			case LOWER_EQUAL : return VMConst.IF_ICMPLE;
			case GREATER_EQUAL : return VMConst.IF_ICMPGE;
			default : throw new BuilderTypeException(fragment, "<unknown type>");
			}
		} else {
			switch (op) {
			case EQUAL : return VMConst.IFEQ;
			case UNEQUAL : return VMConst.IFNE;
			case LOWER : return VMConst.IFLT;
			case GREATER : return VMConst.IFGT;
			case LOWER_EQUAL : return VMConst.IFLE;
			case GREATER_EQUAL : return VMConst.IFGE;
			default : throw new BuilderTypeException(fragment, "<unknown type>");
			}
		}
	}
	
	private byte getOp(NodeType op, int type) throws BuilderSyntaxException {
		switch (type) {
		case 1 :
		case 2 :
		case 3 :
		case 4 :
		case 5 :
			switch (op) {
			case NOT : return VMConst.IXOR;
			case AND : return VMConst.IAND;
			case OR : return VMConst.IOR;
			case XOR : return VMConst.IXOR;
			case SHL : return VMConst.ISHL;
			case SHR : return VMConst.ISHR;
			case USHR : return VMConst.IUSHR;
			case NEG : return VMConst.INEG;
			case ADD : return VMConst.IADD;
			case SUB : return VMConst.ISUB;
			case MUL : return VMConst.IMUL;
			case DIV : return VMConst.IDIV;
			case MOD : return VMConst.IREM;
			default : throw new BuilderSyntaxException(fragment, 0);
			}
		case 6 :
			switch (op) {
			case NOT : return VMConst.LXOR;
			case AND : return VMConst.LAND;
			case OR : return VMConst.LOR;
			case XOR : return VMConst.LXOR;
			case SHL : return VMConst.LSHL;
			case SHR : return VMConst.LSHR;
			case USHR : return VMConst.LUSHR;
			case NEG : return VMConst.LNEG;
			case ADD : return VMConst.LADD;
			case SUB : return VMConst.LSUB;
			case MUL : return VMConst.LMUL;
			case DIV : return VMConst.LDIV;
			case MOD : return VMConst.LREM;
			default : throw new BuilderSyntaxException(fragment, 0);
			}
		case 7 :
			switch (op) {
			case NEG : return VMConst.FNEG;
			case ADD : return VMConst.FADD;
			case SUB : return VMConst.FSUB;
			case MUL : return VMConst.FMUL;
			case DIV : return VMConst.FDIV;
			case MOD : return VMConst.FREM;
			default: throw new BuilderSyntaxException(fragment, 0);
			}
		case 8:
			switch (op) {
			case NEG : return VMConst.DNEG;
			case ADD : return VMConst.DADD;
			case SUB : return VMConst.DSUB;
			case MUL : return VMConst.DMUL;
			case DIV : return VMConst.DDIV;
			case MOD : return VMConst.DREM;
			default : throw new BuilderSyntaxException(fragment, 0);
			}
		default : throw new BuilderSyntaxException(fragment, 0);
		}
	}
	
	private DefaultLValue cast(DefaultLValue source, Class<?> dest) throws BuilderSyntaxException, BuilderTypeException {
		if (source.varType == null && !dest.isPrimitive()) return source;
		if (source.getVarType() == dest) return source;
		if (dest.isAssignableFrom(source.getVarType())) return source;
		
		if (dest.isPrimitive() && source.getVarType() == Object.class) {
			source = cast(source, VMConst.getWrapperType(dest));
		}
		
		int level = VMConst.getLevel(source.varType);
		int destLevel = VMConst.getLevel(dest);
		DefaultLValue lv = null;
		
		if (source.varType.isPrimitive() && !dest.isPrimitive()) {
			return wrap(source);
		} else if (dest.isPrimitive()) {
			source = unwrap(source);
			
			if (level > 1 && level < 6) {
				if (destLevel == 6)					lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.I2L, null, long.class);
				if (destLevel == 7)					lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.I2F, null, float.class);
				if (destLevel == 8)					lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.I2D, null, double.class);
			} else if (level == 6) {
				if (destLevel > 1 && destLevel < 6)	lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.L2I, null, int.class);
				if (destLevel == 7)					lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.L2F, null, float.class);
				if (destLevel == 8)					lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.L2D, null, double.class);
			} else if (level == 7) {
				if (destLevel > 1 && destLevel < 6)	lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.F2I, null, int.class);
				if (destLevel == 6)					lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.F2L, null, long.class);
				if (destLevel == 8)					lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.F2D, null, double.class);
			} else if (level == 8) {
				if (destLevel > 1 && destLevel < 6)	lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.D2I, null, int.class);
				if (destLevel == 6)					lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.D2L, null, long.class);
				if (destLevel == 7)					lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.D2F, null, float.class);
			}
			if (lv != null) {
				source.setNext(lv, false); // getRoot nicht erforderlich
				source = lv;
			}
			lv = null;
			
			if (destLevel != level) {
				if (destLevel == VMConst.BYTE)	lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.I2B, null, byte.class);
				if (destLevel == VMConst.SHORT)	lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.I2S, null, short.class);
				if (destLevel == VMConst.CHAR)	lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.I2C, null, char.class);
				if (level > 1 && level < 5)	{
					lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, null, null, dest);
				}
			}
		} else {
			lv = new DefaultLValue(fragment, source.getRoot(), NodeType.CAST, VMConst.CHECKCAST, null, dest);
		}
		if (lv != null) {
			source.setNext(lv, false);
			source = lv;
		}
		return source;
	}
	
	public String toString(DefaultLValue parent) {
		return toString(parent, "");
	}
	
	@Override
	public String toString() {
		return toString(null);
	}
	
	public String toString(DefaultLValue parent, String source) {
		String s = "";
		Variable var;
		
		switch (type) {
		case AGET :
			s = source + "[" + ((DefaultLValue)value).toString(this, "") + "]";
			if (next != null) s = next.toString(this, s);
			break;
		case LGET :
			if (next == null || next.type != NodeType.LSET) {
				var = (Variable)value;
				s = source + ((Variable)id).getName();
			} else {
				s = source;
			}
			if (next != null) s = next.toString(this, s);
			break;
		case FGET :
		case SGET :
			if (id instanceof Field) s = source + "." + ((Field)id).getName();
			if (id instanceof IField) s = source + "." + ((IField)id).getName();
			if (next != null) s = next.toString(this, s);
			break;
		case ASET :
			s = source + "[" + ((DefaultLValue)value).toString(this, "") + "] = ";
			if (next != null) s += next.toString(this, "");
			break;
		case LSET :
			var = (Variable)id;
			s = var.getName() + " = ";
			if (next != null) s += next.toString(this, "");
			break;
		case FSET :
		case SSET :
			if (id instanceof Field) s = source + "." + ((Field)id).getName() + " = ";
			if (id instanceof IField) s = source + "." + ((IField)id).getName() + " = ";
			if (next != null) s += next.toString(this, "");
			break;
		case INVOKE :
			DefaultLValue [] args;
			int i;
			
			if (id instanceof Method) s = source + "." + ((Method)id).getName() + "(";
			if (id instanceof Constructor) s = source + "(";
			if (id instanceof IMethod) s = source + ((IMethod)id).getName() + "(";
			args = (DefaultLValue[])value;
			for (i = 0; i < args.length; i++) {
				if (i > 0) s += ", ";
				s += args[i].toString(null, "");
			}
			s += ")";
			if (next != null) s = next.toString(this, s);
			break;
		case CLASS :
			if (value == IClass.CURRENT_CLASS_TYPE) {
				s = source + fragment.getDeclaringClass().getSimpleName();
			} else {
				s = source + ((Class<?>)value).getName();
			}
			if (next != null) s = next.toString(this, s);
			break;
		case SUPER :
			s = source + "super";
			if (next != null) s = next.toString(this, s);
			break;
		case THIS :
			s = source + "this";
			if (next != null) s = next.toString(this, s);
			break;
		case CONST :
			s = source + VMConst.getConst(value);
			if (next != null) s = source + next.toString(this, s);
			break;
		case NULL :
			s = "null";
			break;
		case CAST :
			s = "((" + getVarTypeName() + ")" + source + ")";
			if (next != null) s = next.toString(this, s);
			break;
		case NOT :
		case NEG :
			s = "!" + source;
			if (next != null) s += next.toString(this, s);
			break;
		case AND :
			s = source + " && " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case OR :
			s = source + " || " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case XOR :
			s = source + " ^ " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case ADD :
			s = source + " + " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case SUB :
			s = source + " - " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case MUL :
			s = source + " * " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case DIV :
			s = source + " / " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case MOD :
			s = source + " % " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case EQUAL :
			s = source + " == " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case UNEQUAL :
			s = source + " != " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case LOWER :
			s = source + " < " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case GREATER :
			s = source + " > " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case LOWER_EQUAL :
			s = source + " <= " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case GREATER_EQUAL :
			s = source + " >= " + ((DefaultLValue)value).getRoot().toString(this, "");
			if (next != null) s += next.toString(this, s);
			break;
		case RETURN :
			s = source + "return";
			if (next != null) s = "return " + next.toString(this, "");
			break;
		case NEW:
			if (varType.isArray()) {
				s = source + "new " + VMConst.getTypeName(varType.getComponentType());
				s += "[" + ((DefaultLValue)value).toString(this, "") + "]";
			} else if (!varType.isPrimitive()) {
				s = source + "new " + VMConst.getTypeName(varType);
			}
			if (next != null) s += next.toString(this, "");
			break;
		case THROW :
			s = "throw ";
			if (next != null) s += next.toString(this, "");
			break;
		case LENGTH :
			s = ".length";
			break;
		case IS_NULL :
			s = source + " == null";
			break;
		case IS_NOT_NULL :
			s = source + " != null";
			break;
		default:
			break;
		}
		
		return s;
	}

	@Override
	public RValue add(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.ADD, a);
	}

	@Override
	public RValue sub(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.SUB, a);
	}

	@Override
	public RValue mul(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.MUL, a);
	}

	@Override
	public RValue div(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.DIV, a);
	}

	@Override
	public RValue mod(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.MOD, a);
	}

	@Override
	public RValue and(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.AND, a);
	}

	@Override
	public RValue or(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.OR, a);
	}

	@Override
	public RValue xor(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.XOR, a);
	}

	@Override
	public RValue shr(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.SHR, a);
	}

	@Override
	public RValue shl(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.SHL, a);
	}

	@Override
	public RValue ushr(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.USHR, a);
	}

	@Override
	public RValue not() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		// a -> lv -> getRoot
		return getResultType(NodeType.NOT, null);
	}

	@Override
	public RValue neg() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		// a -> lv -> getRoot
		return getResultType(NodeType.NEG, null);
	}

	@Override
	public RValue instanceOf(Class<?> a) throws BuilderSyntaxException, BuilderTypeException {
		// a -> lv -> getRoot
		if (varType == null || varType == void.class || varType.isPrimitive()) throw new BuilderTypeException(fragment, BuilderTypeException.OBJECT_REQUIRED);
		if (a == null || a == void.class) throw new BuilderTypeException(fragment, BuilderTypeException.CLASS_REQUIRED);
		DefaultLValue v = new DefaultLValue(fragment, root, NodeType.INSTANCEOF, null, VMConst.getWrapperType(a), boolean.class);
		setNext(v, false);
		return v;
	}

	@Override
	public RValue equal(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.EQUAL, a);
	}

	@Override
	public RValue notEqual(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.UNEQUAL, a);
	}

	@Override
	public RValue less(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.LOWER, a);
	}

	@Override
	public RValue greater(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.GREATER, a);
	}

	@Override
	public RValue lessEqual(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.LOWER_EQUAL, a);
	}

	@Override
	public RValue greaterEqual(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.GREATER_EQUAL, a);
	}
	
	@Override
	public RValue isNull() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.IS_NULL, null);
	}
	
	@Override
	public RValue isNotNull() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return getResultType(NodeType.IS_NOT_NULL, null);
	}
	
	private DefaultLValue getResultType(NodeType nodeType, Object right) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		DefaultLValue r = (DefaultLValue)fragment.$(right), l;
		check(r);
		Class<?> resultType;
		l = unwrap(this);
		this.root.remove();
		if (right != null) {
			r = unwrap((DefaultLValue)r);
			r.getRoot().removed = true;
		}
		
		switch (nodeType) {
		case NOT :
			if (!testVarType(varType, VMConst.BOOLEAN, VMConst.LONG)) throw new BuilderTypeException(fragment, BuilderTypeException.BOOLEAN_OR_INTEGER_REQUIRED);
			break;
		case AND :
		case OR :
		case XOR :
			if (!testVarType(varType, VMConst.BOOLEAN, VMConst.LONG)) throw new BuilderTypeException(fragment, BuilderTypeException.BOOLEAN_OR_INTEGER_REQUIRED);
			if (!testVarType(r.varType, VMConst.BOOLEAN, VMConst.LONG)) throw new BuilderTypeException(fragment, BuilderTypeException.BOOLEAN_OR_INTEGER_REQUIRED);
			break;
		case SHR :
		case USHR :
		case SHL :
			if (!testVarType(varType, VMConst.BYTE, VMConst.LONG)) throw new BuilderTypeException(fragment, BuilderTypeException.BOOLEAN_OR_INTEGER_REQUIRED);
			if (!testVarType(r.varType, VMConst.BYTE, VMConst.INT)) throw new BuilderTypeException(fragment, BuilderTypeException.BOOLEAN_OR_INTEGER_REQUIRED);
			break;
		case NEG :
			if (!testVarType(varType, VMConst.BYTE, VMConst.DOUBLE)) throw new BuilderTypeException(fragment, BuilderTypeException.NUMERIC_REQUIRED);
			break;
		case ADD :
		case SUB :
		case MUL :
		case DIV :
		case MOD :
			if (!testVarType(varType, VMConst.BYTE, VMConst.DOUBLE)) throw new BuilderTypeException(fragment, BuilderTypeException.NUMERIC_REQUIRED);
			if (!testVarType(r.varType, VMConst.BYTE, VMConst.DOUBLE)) throw new BuilderTypeException(fragment, BuilderTypeException.NUMERIC_REQUIRED);
			break;
		case EQUAL :
		case UNEQUAL :
			if (!testVarType(varType, VMConst.OBJECT, VMConst.DOUBLE)) throw new BuilderTypeException(fragment, BuilderTypeException.BOOLEAN_OR_NUMERIC_REQUIRED);
			if (!testVarType(r.varType, VMConst.OBJECT, VMConst.DOUBLE)) throw new BuilderTypeException(fragment, BuilderTypeException.BOOLEAN_OR_NUMERIC_REQUIRED);
			break;
		case GREATER :
		case LOWER :
		case GREATER_EQUAL :
		case LOWER_EQUAL :
			if (!testVarType(varType, VMConst.BYTE, VMConst.DOUBLE)) throw new BuilderTypeException(fragment, BuilderTypeException.BOOLEAN_OR_NUMERIC_REQUIRED);
			if (!testVarType(r.varType, VMConst.BYTE, VMConst.DOUBLE)) throw new BuilderTypeException(fragment, BuilderTypeException.BOOLEAN_OR_NUMERIC_REQUIRED);
			break;
		case INSTANCEOF :
			if (varType == null || varType == void.class) throw new BuilderTypeException(fragment, BuilderTypeException.OBJECT_REQUIRED);
			if (r.type != NodeType.CLASS) throw new BuilderTypeException(fragment, BuilderTypeException.CLASS_REQUIRED);
			break;
		case IS_NULL :
		case IS_NOT_NULL :
			if (varType == null || varType == void.class || varType.isPrimitive()) throw new BuilderTypeException(fragment, BuilderTypeException.OBJECT_REQUIRED);
		default:
			break;
		}
		
		if (nodeType == NodeType.NEG || nodeType == NodeType.NOT || nodeType == NodeType.IS_NULL || nodeType == NodeType.IS_NOT_NULL) {
			int level = VMConst.getLevel(l.varType);
			if (level > VMConst.BOOLEAN && level < VMConst.INT) {
				resultType = int.class;
			} else {
				resultType = l.varType;
			}
		} else if (nodeType == NodeType.SHR || nodeType == NodeType.USHR || nodeType == NodeType.SHL) {
			int level = VMConst.getLevel(l.varType);
			if (level == VMConst.LONG) {
				resultType = long.class;
			} else {
				resultType = int.class;
			}
		} else {
			resultType = compareClasses(l.varType, r.varType);
			r = cast(r, resultType);
		}
		l = cast(l, resultType);
		
		r = r.getRoot();
		r.used = true;
		DefaultLValue v;
		
		if (nodeType == NodeType.EQUAL || nodeType == NodeType.UNEQUAL || nodeType == NodeType.LOWER || nodeType == NodeType.GREATER ||
			nodeType == NodeType.LOWER_EQUAL || nodeType == NodeType.GREATER_EQUAL || nodeType == NodeType.INSTANCEOF || 
			nodeType == NodeType.IS_NULL || nodeType == NodeType.IS_NOT_NULL) {
			v = new DefaultLValue(fragment, root, nodeType, getCmp(nodeType, VMConst.getLevel(resultType)), r, boolean.class);
			resultType = boolean.class;
		} else {
			v = new DefaultLValue(fragment, root, nodeType, getOp(nodeType, VMConst.getLevel(resultType)), r, resultType);
		}
		
		l.setNext(v, false);
		
		return v;
	}
	
	private boolean testVarType(Class<?> type, int from, int to) {
		int level = VMConst.getLevel(type);
		if (level >= from && level <= to) return true;
		return false;
	}
	
	private String getVarTypeName() {
		if (varType == null) return "<none>";
		return varType.getName();
	}
	
	private void check(DefaultLValue lv) throws BuilderAccessException {
		if (lv.getType() == NodeType.LGET) {
			if (!((Variable)lv.id).isInitialized()) {
				throw new BuilderAccessException(fragment, BuilderAccessException.VARIABLE_NOT_INITIALIZED, ((Variable)lv.id).getName());
			}
		}
	}
}
