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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import classbuilder.BuilderAccessException;
import classbuilder.BuilderCompilerException;
import classbuilder.BuilderTypeException;
import classbuilder.IAnnotation;

public class DefaultAnnotation implements IAnnotation {
	protected Class<?> type;
	protected Map<String, Object> values;
	protected ConstantPool constantPool;
	protected Object target;
	
	public DefaultAnnotation(Object target, Class<?> type, ElementType elementType, ConstantPool constantPool) throws BuilderTypeException {
		if (type == null) throw new BuilderTypeException(target, "<null>");
		if (!type.isAnnotation()) throw new BuilderTypeException(target, type);
		Retention retention = type.getAnnotation(Retention.class);
		if (retention == null || retention.value() != RetentionPolicy.RUNTIME) throw new BuilderTypeException(target, type);
		values = new HashMap<String, Object>();
		this.type = type;
		this.constantPool = constantPool;
		this.target = target;
		
		Target targetAnnotation = type.getAnnotation(Target.class);
		if (targetAnnotation != null && elementType != null) {
			for (ElementType element : targetAnnotation.value()) {
				if (element == elementType) return;
			}
			throw new BuilderTypeException(target, type);
		}
	}
	
	@Override
	public Class<?> getType() {
		return type;
	}
	
	@Override
	public void setValue(String name, Object value) throws BuilderTypeException, BuilderAccessException {
		Method method;
		try {
			method = type.getMethod(name);
		} catch (NoSuchMethodException e) {
			throw new BuilderAccessException(target, BuilderAccessException.METHOD_NOT_FOUND, e);
		} catch (SecurityException e) {
			throw new BuilderAccessException(target, BuilderAccessException.METHOD_NOT_FOUND, e);
		}
		checkType(method.getReturnType(), value);
		values.put(name, value);
	}
	
	private void checkType(Class<?> type, Object value) throws BuilderTypeException {
		if (value == null) {
			throw new BuilderTypeException(target, "<null>");
		}
		int level = VMConst.getLevel(value.getClass());
		if (level != -1) {
			if (level != -1 && level != VMConst.getLevel(type)) throw new BuilderTypeException(target, value.getClass());
		} else {
			if (value instanceof String || value instanceof Enum || value instanceof Class) {
				if (type != value.getClass()) throw new BuilderTypeException(target, value.getClass());
			} else if (value instanceof IAnnotation) {
				if (type != ((IAnnotation)value).getType()) throw new BuilderTypeException(target, ((IAnnotation) value).getType());
			} else if (value.getClass().isArray()) {
				for (int i = 0; i < Array.getLength(value); i++) {
					checkType(type.getComponentType(), Array.get(value, i));
				}
			} else {
				throw new BuilderTypeException(target, value.getClass());
			}
		}
	}
	
	@Override
	public Object getValue(String name) {
		return values.get(name);
	}
	
	@Override
	public Collection<String> getNames() {
		return values.keySet();
	}
	
	@Override
	public int countValues() {
		return values.size();
	}
	
	public int getSize() {
		int size = countValues() * 2 + 4;
		for (Object value : values.values()) {
			size += getSize(value);
		}
		return size;
	}
	
	private int getSize(Object value) {
		if (value instanceof Enum) {
			return 5;
		} else if (value instanceof DefaultAnnotation) {
			return ((DefaultAnnotation)value).getSize() + 1;
		} else if (value.getClass().isArray()) {
			int size = 3;
			for (int i = 0; i < Array.getLength(value); i++) {
				size += getSize(Array.get(value, i));
			}
			return size;
		} else {
			return 3;
		}
	}
	
	public void validate() throws BuilderCompilerException {
		Method[] methods = type.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getDefaultValue() == null && !values.containsKey(method.getName())) {
				throw new BuilderCompilerException(target, BuilderCompilerException.ANNOTATION_VALUE_REQUIRED, type.getSimpleName() + "." + method.getName());
			}
		}
	}
	
	@Override
	public IAnnotation createAnnotaton(Class<?> type) throws BuilderTypeException {
		return new DefaultAnnotation(target, type, null, constantPool);
	}
	
	@Override
	public String toString() {
		String text = "@" + type.getName() + "(";
		boolean first = true;
		for (Entry<String, Object> entry : values.entrySet()) {
			if (!first) text += ",";
			text += entry.getKey() +"=" + toString(entry.getValue());
			first = false;
		}
		return text + ")";
	}
	
	private String toString(Object value) {
		String text;
		if (value instanceof String) {
			text = "\"" + value + "\"";
		} else if (value instanceof Enum) {
			text = value.getClass().getName() + "." + ((Enum<?>)value).name();
		} else if (value.getClass().isArray()) {
			text = "{";
			boolean first = true;
			for (int i = 0; i < Array.getLength(value); i++) {
				if (!first) text += ",";
				text += toString(Array.get(value, i));
				first = false;
			}
			text += "}";
		} else {
			text = value.toString();
		}
		return text;
	}
	
	public void write(DataOutputStream classFile) throws IOException {
		writeAnnotation(classFile, this);
	}
	
	private void writeAnnotation(DataOutputStream classFile, DefaultAnnotation annotation) throws IOException {
		classFile.writeShort(constantPool.addString(VMConst.getClassName(annotation.getType())));//u2 type_index;
		classFile.writeShort(annotation.countValues());			//u2 num_element_value_pairs;
		
		for (String name : annotation.getNames()) {
			writeAnnotationValue(classFile, name, annotation.getValue(name));
		}
	}
	
	private void writeAnnotationValue(DataOutputStream classFile, String name, Object value) throws IOException {
		Class<?> valueType = value.getClass();
		if (name != null) {
			classFile.writeShort(constantPool.addString(name));	//u2 element_name_index;
		}
		if (valueType == Byte.class) {
			classFile.writeByte('B');
			classFile.writeShort(constantPool.add(value));
		} else if (valueType == Short.class) {
			classFile.writeByte('S');
			classFile.writeShort(constantPool.add(value));
		} else if (valueType == Integer.class) {
			classFile.writeByte('I');
			classFile.writeShort(constantPool.add(value));
		} else if (valueType == Long.class) {
			classFile.writeByte('J');
			classFile.writeShort(constantPool.add(value));
		} else if (valueType == Float.class) {
			classFile.writeByte('F');
			classFile.writeShort(constantPool.add(value));
		} else if (valueType == Double.class) {
			classFile.writeByte('D');
			classFile.writeShort(constantPool.add(value));
		} else if (valueType == Boolean.class) {
			classFile.writeByte('Z');
			classFile.writeShort(constantPool.add(value));
		} else if (valueType == Character.class) {
			classFile.writeByte('C');
			classFile.writeShort(constantPool.add(value));
		} else if (valueType == String.class) {
			classFile.writeByte('s');
			classFile.writeShort(constantPool.addString((String)value));
		} else if (valueType == Class.class) {
			classFile.writeByte('c');
			classFile.writeShort(constantPool.add(value));
		} else if (value instanceof Enum) {
			Enum<?> e = (Enum<?>)value;
			classFile.writeByte('e');
			classFile.writeShort(constantPool.addString(valueType.getName()));
			classFile.writeShort(constantPool.addString(e.name()));
		} else if (valueType.isArray()) {
			classFile.writeByte('[');
			classFile.writeShort(Array.getLength(value));
			for (int i = 0; i < Array.getLength(value); i++) {
				writeAnnotationValue(classFile, null, Array.get(value, i));
			}
		} else if (value instanceof DefaultAnnotation) {
			DefaultAnnotation a = (DefaultAnnotation)value;
			classFile.writeByte('@');
			writeAnnotation(classFile, a);
		}
	}
	
	public static void writeAnnotations(DataOutputStream classFile, ConstantPool constantPool, List<DefaultAnnotation> annotations) throws IOException {
		int size = 2;
		for (DefaultAnnotation annotation : annotations) {
			size += annotation.getSize();
		}
		
		classFile.writeShort(constantPool.addString("RuntimeVisibleAnnotations"));//u2 attribute_name_index;
		classFile.writeInt(size);					//u4 attribute_length;
		classFile.writeShort(annotations.size());	//u2 num_annotations;
													//annotation annotations[num_annotations];
		for (DefaultAnnotation annotation : annotations) {
			annotation.write(classFile);;
		}
	}
	
}
