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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import classbuilder.BuilderTypeException;
import classbuilder.IAnnotation;
import classbuilder.IClass;
import classbuilder.IField;

public class DefaultField implements IField {
	protected String name;
	protected Class<?> type;
	protected IClass declaringClass;
	protected int modifiers;
	protected Object value;
	protected List<DefaultAnnotation> annotations;
	protected ConstantPool constantPool;
	
	public DefaultField(IClass declaringClass, int modifiers, String name, Class<?> type, Object value, ConstantPool constantPool) {
		this.name = name;
		this.type = type;
		this.declaringClass = declaringClass;
		this.modifiers = modifiers;
		this.value = value;
		this.constantPool = constantPool;
		annotations = new ArrayList<DefaultAnnotation>();
	}
	
	@Override
	public Class<?> getType() {
		return type;
	}
	
	@Override
	public IClass getDeclaringClass() {
		return declaringClass;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public int getModifiers() {
		return modifiers;
	}
	
	public Object getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return declaringClass.getName() + "." + name;
	}
	
	public String write() {
		String text = VMConst.getModifier(modifiers);
		text += VMConst.getTypeName(type) + " " + name;
		if (value != null) {
			if (value instanceof String) {
				text += " = \"" + value + "\"";
			} else {
				text += " = " + value;
			}
		}
		return "\t" + text + ";\n";
	}
	
	@Override
	public IAnnotation addAnnotation(Class<?> type) throws BuilderTypeException {
		DefaultAnnotation annotation = new DefaultAnnotation(this, type, ElementType.FIELD, constantPool);
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
		lineNumber += 1 + getAnnotations().size();
		boolean constantValue = (getModifiers() & IClass.STATIC) != 0 && getValue() != null;
		int attr = getDefaultAnnotations().size();
		if (constantValue) attr++;
		classFile.writeShort(getModifiers());//access_flags;
		classFile.writeShort(constantPool.addString(getName()));//name_index;
		classFile.writeShort(constantPool.addString(VMConst.getClassName(getType())));//descriptor_index;
		classFile.writeShort(attr);//attributes_count
		if (annotations.size() > 0) {
			DefaultAnnotation.writeAnnotations(classFile, constantPool, annotations);
		}
		if (constantValue) {
			classFile.writeShort(constantPool.addString("ConstantValue"));//u2 attribute_name_index;
			classFile.writeInt(2);//u4 attribute_length;
			classFile.writeShort(constantPool.add(getValue()));//u2 constantvalue_index;
		}
		return lineNumber;
	}
	
}
