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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import classbuilder.IClass;
import classbuilder.IField;
import classbuilder.IMethod;
import classbuilder.Variable;

public class ConstantPool {
	protected static final byte CONSTANT_Class = 7;
	protected static final byte CONSTANT_Fieldref = 9;
	protected static final byte CONSTANT_Methodref = 10;
	protected static final byte CONSTANT_InterfaceMethodref = 11;
	protected static final byte CONSTANT_String = 8;
	protected static final byte CONSTANT_Integer = 3;
	protected static final byte CONSTANT_Float = 4;
	protected static final byte CONSTANT_Long = 5;
	protected static final byte CONSTANT_Double = 6;
	protected static final byte CONSTANT_NameAndType = 12;
	protected static final byte CONSTANT_Utf8 = 1;
	
	protected Map<Const, Const> constantPool;
	protected short poolIndex;
	protected List<Const> entryList;
	
	protected static class Const {
		public byte tag;
		public short index;
		public Object value;
		public short ref1, ref2;
		
		public Const(byte tag, short index, Object value, int ref1, int ref2) {
			this.tag = tag;
			this.ref1 = (short)ref1;
			this.ref2 = (short)ref2;
			this.index = index;
			this.value = value;
		}
		
		@Override
		public int hashCode() {
			if (tag == CONSTANT_NameAndType) {
				return tag | (ref1 >> 8) | (ref2 >> 16);
			} else {
				return value.hashCode();
			}
		}
		
		@Override
		public boolean equals(Object obj) {
			Const c = ((Const)obj);
			
			if (tag != c.tag) return false;
			
			if (tag == CONSTANT_NameAndType || 
				((tag == CONSTANT_Fieldref || tag == CONSTANT_Methodref || tag == CONSTANT_InterfaceMethodref) && (ref1 != 0 && c.ref1 != 0))) { // hack for enhance
				if (ref1 == c.ref1 && ref2 == c.ref2) {
					return true;
				}
			} else {
				if (value.equals(c.value)) {
					return true;
				}
			}
			
			return false;
		}
	}
	
	public ConstantPool(ClassLoader classLoader) {
//		this.classLoader = classLoader;
		constantPool = new LinkedHashMap<Const, Const>();
		entryList = new ArrayList<Const>();
		poolIndex = 1;
	}
	
	public short getSize() {
		return poolIndex;
	}
	
	private short addConstant(byte tag, short index, Object value, int ref1, int ref2) {
		Const entry, c = new Const(tag, index, value, ref1, ref2);
		String name, sig;
		Object cls;
		short r1, r2;
		short i;
		
		entry = constantPool.get(c);
		if (entry != null) {
			return entry.index;
		} else {
			switch (tag) {
			case CONSTANT_String :
				c.ref1 = addConstant(CONSTANT_Utf8, poolIndex, value, 0, 0);
				break;
			case CONSTANT_Class :
				if (value instanceof Class) {
					c.ref1 = addConstant(CONSTANT_Utf8, poolIndex, ((Class<?>)value).getName().replace('.', '/'), 0, 0);
				} else if (value instanceof IClass) {
					c.ref1 = addConstant(CONSTANT_Utf8, poolIndex, ((IClass)value).getName().replace('.', '/'), 0, 0);
				}
				break;
			case CONSTANT_Fieldref :
				if (value instanceof Field) {
					cls = ((Field)value).getDeclaringClass();
					name =  ((Field)value).getName();
					sig = VMConst.getClassName(((Field)value).getType());
				} else /*if (value instanceof IField)*/ {
					cls = ((IField)value).getDeclaringClass();
					name = ((IField)value).getName();
					sig = VMConst.getClassName(((IField)value).getType());
				}
				r1 = addConstant(CONSTANT_Utf8, poolIndex, name, 0, 0);
				r2 = addConstant(CONSTANT_Utf8, poolIndex, sig, 0, 0);
				r1 = addConstant(CONSTANT_NameAndType, poolIndex, value, r1, r2);
				c.ref1 = addConstant(CONSTANT_Class, poolIndex, cls, 0, 0);
				c.ref2 = r1;
				break;
			case CONSTANT_Methodref :
			case CONSTANT_InterfaceMethodref :
				if (value instanceof Method) {
					Method method = (Method)value;
					cls = method.getDeclaringClass();
					name = method.getName();
					
					sig = "(";
					Class<?>[] ac = method.getParameterTypes();
					for (i = 0; i < ac.length; i++) {
						sig += VMConst.getClassName(ac[i]);
					}
					sig += ")" + VMConst.getClassName(method.getReturnType());
				} else if (value instanceof Constructor) {
					Constructor<?> method = (Constructor<?>)value;
					cls = method.getDeclaringClass();
					name = "<init>";
					
					sig = "(";
					Class<?>[] ac = method.getParameterTypes();
					for (i = 0; i < ac.length; i++) {
						sig += VMConst.getClassName(ac[i]);
					}
					sig += ")" + VMConst.getClassName(void.class);
				} else /*if (value instanceof Function)*/ {
					IMethod method = (IMethod)value;
					cls = method.getDeclaringClass();
					name = method.getName();
					
					sig = "(";
					for (Variable var : method.getParameters()) {
						sig += VMConst.getClassName(var.getType());
					}
					sig += ")" + VMConst.getClassName(method.getReturnType());
				}
				r1 = addConstant(CONSTANT_Utf8, poolIndex, name, 0, 0);
				r2 = addConstant(CONSTANT_Utf8, poolIndex, sig, 0, 0);
				r1 = addConstant(CONSTANT_NameAndType, poolIndex, value, r1, r2);
				c.ref1 = addConstant(CONSTANT_Class, poolIndex, cls, 0, 0);
				c.ref2 = r1;
				break;
			}
			c.index = poolIndex;
			entryList.add(c);
			poolIndex++;
			if (tag == CONSTANT_Double || tag == CONSTANT_Long) {
				poolIndex++;
				entryList.add(null);
			}
			constantPool.put(c, c);
			return c.index;
		}
	}
	
	public short addString(String text) {
		return addConstant(CONSTANT_Utf8, poolIndex, text, 0, 0);
	}
	
	public short add(Object value) {
		if (value instanceof String) {
			return addConstant(CONSTANT_String, poolIndex, value, 0, 0);
		} else if (value instanceof Class) {
			return addConstant(CONSTANT_Class, poolIndex, value, 0, 0);
		} else if (value instanceof ParameterizedType) {
			return addConstant(CONSTANT_Class, poolIndex, ((ParameterizedType)value).getRawType(), 0, 0);
		} else if (value instanceof Boolean) {
			if ((Boolean)value) {
				return addConstant(CONSTANT_Integer, poolIndex, 1, 0, 0);
			} else {
				return addConstant(CONSTANT_Integer, poolIndex, 0, 0, 0);
			}
		} else if (value instanceof Byte) {
				return addConstant(CONSTANT_Integer, poolIndex, ((Byte)value).intValue(), 0, 0);
		} else if (value instanceof Short) {
			return addConstant(CONSTANT_Integer, poolIndex, ((Short)value).intValue(), 0, 0);
		} else if (value instanceof Character) {
			return addConstant(CONSTANT_Integer, poolIndex, (int)((Character)value).charValue(), 0, 0);
		} else if (value instanceof Integer) {
			return addConstant(CONSTANT_Integer, poolIndex, value, 0, 0);
		} else if (value instanceof Double) {
			return addConstant(CONSTANT_Double, poolIndex, value, 0, 0);
		} else if (value instanceof Float) {
			return addConstant(CONSTANT_Float, poolIndex, value, 0, 0);
		} else if (value instanceof Long) {
			return addConstant(CONSTANT_Long, poolIndex, value, 0, 0);
		} else if (value instanceof Method) {
			if (((Method)value).getDeclaringClass().isInterface()) {
				return addConstant(CONSTANT_InterfaceMethodref, poolIndex, value, 0, 0);
			} else {
				return addConstant(CONSTANT_Methodref, poolIndex, value, 0, 0);
			}
		} else if (value instanceof Field) {
			return addConstant(CONSTANT_Fieldref, poolIndex, value, 0, 0);
		} else if (value instanceof Constructor) {
			return addConstant(CONSTANT_Methodref, poolIndex, value, 0, 0);
		} else if (value instanceof IMethod) {
			if ((((IMethod)value).getDeclaringClass().getModifiers() & VMConst.INTERFACE) != 0) {
				return addConstant(CONSTANT_InterfaceMethodref, poolIndex, value, 0, 0);
			} else {
				return addConstant(CONSTANT_Methodref, poolIndex, value, 0, 0);
			}
		} else if (value instanceof IField) {
			return addConstant(CONSTANT_Fieldref, poolIndex, value, 0, 0);
		} else if (value instanceof IClass) {
			return addConstant(CONSTANT_Class, poolIndex, value, 0, 0);
		} else {
			throw new RuntimeException("invalid constant pool entry: " + value.getClass().getName());
		}
	}
	
	public String getString(short index) {
		return (String)entryList.get(index).value;
	}
	
	public int getInt(short index) {
		return (Integer)entryList.get(index).value;
	}
	
	public float getFloat(short index) {
		return (Float)entryList.get(index).value;
	}
	
	public long getLong(short index) {
		return (Long)entryList.get(index).value;
	}
	
	public double getDouble(short index) {
		return (Double)entryList.get(index).value;
	}
	
	public byte[] write() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(data);
		
		try {
			for (Const c : constantPool.values()) {
				out.write(c.tag);
				switch (c.tag) {
				case CONSTANT_Utf8 :
					byte[] buffer = ((String)c.value).getBytes("UTF-8");
					out.writeShort(buffer.length);
					out.write(buffer);
					break;
				case CONSTANT_Class :
				case CONSTANT_String :
					out.writeShort(c.ref1);
					break;
				case CONSTANT_Fieldref :
				case CONSTANT_Methodref :
				case CONSTANT_InterfaceMethodref :
				case CONSTANT_NameAndType :
					out.writeShort(c.ref1);
					out.writeShort(c.ref2);
					break;
				case CONSTANT_Integer :
					out.writeInt((Integer)c.value);
					break;
				case CONSTANT_Float :
					out.writeFloat((Float)c.value);
					break;
				case CONSTANT_Long :
					out.writeLong((Long)c.value);
					break;
				case CONSTANT_Double :
					out.writeDouble((Double)c.value);
					break;
				default :
					
				}
			}
			out.close();
		} catch (IOException e) {
			
		}
		
		return data.toByteArray();
	}
}
