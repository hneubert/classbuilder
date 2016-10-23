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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import classbuilder.BuilderAccessException;
import classbuilder.BuilderCompilerException;
import classbuilder.BuilderModifierException;
import classbuilder.BuilderNameException;
import classbuilder.BuilderSyntaxException;
import classbuilder.BuilderTypeException;
import classbuilder.ClassFactory;
import classbuilder.IAnnotation;
import classbuilder.IClass;
import classbuilder.IConstructor;
import classbuilder.IField;
import classbuilder.IMethod;

/**
 * The ClassDelegate class delegates all methods of IClass and can be inherited.
 */
public class ClassDelegate implements IClass {
	
	protected IClass cls;
	
	/**
	 * Creates a new ClassDelegate object.
	 * @param cls IClass object
	 */
	public ClassDelegate(IClass cls) {
		this.cls = cls;
	}
	
	@Override
	public IField addField(int modifiers, String name, Object value) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		return cls.addField(modifiers, name, value);
	}
	
	@Override
	public IField addEnumConstant(String name, Object... args) throws BuilderNameException, BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
		return cls.addEnumConstant(name, args);
	}
	
	@Override
	public Class<?> build() throws BuilderCompilerException {
		return cls.build();
	}

	@Override
	public void write(OutputStream out) throws BuilderCompilerException, IOException {
		cls.write(out);
	}

	@Override
	public String getName() {
		return cls.getName();
	}

	@Override
	public String getSimpleName() {
		return cls.getSimpleName();
	}
	
	@Override
	public boolean isEnum() {
		return cls.isEnum();
	}
	
	@Override
	public boolean isInterface() {
		return cls.isInterface();
	}

	@Override
	public int getModifiers() {
		return cls.getModifiers();
	}

	@Override
	public IField getField(String name) throws NoSuchFieldException {
		return cls.getField(name);
	}

	@Override
	public Collection<IField> getFields() {
		return cls.getFields();
	}

	@Override
	public IMethod getMethod(String name, Class<?>[] paramTypes) throws NoSuchMethodException {
		return cls.getMethod(name, paramTypes);
	}

	@Override
	public Collection<IMethod> getMethods() {
		return cls.getMethods();
	}

	@Override
	public IConstructor getConstructor(Class<?>[] paramTypes) throws NoSuchMethodException {
		return cls.getConstructor(paramTypes);
	}

	@Override
	public Collection<IConstructor> getConstructors() {
		return cls.getConstructors();
	}

	@Override
	public void writeSource(OutputStream out) throws IOException {
		
	}

	@Override
	public Class<?> getSuperclass() {
		return cls.getSuperclass();
	}

	@Override
	public String getPackage() {
		return cls.getPackage();
	}

	@Override
	public IMethod Static() {
		return cls.Static();
	}

	@Override
	public IField addField(int modifiers, Class<?> type, String name) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		return cls.addField(modifiers, type, name);
	}

	@Override
	public IConstructor addConstructor(int modifiers, Class<?>... params) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		return cls.addConstructor(modifiers, params);
	}

	@Override
	public IMethod addMethod(int modifiers, String name, Class<?>... params) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		return cls.addMethod(modifiers, name, params);
	}

	@Override
	public IMethod addMethod(int modifiers, Class<?> returnType, String name, Class<?>... params) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		return cls.addMethod(modifiers, returnType, name, params);
	}

	@Override
	public IAnnotation addAnnotation(Class<?> type) throws BuilderTypeException {
		return cls.addAnnotation(type);
	}

	@Override
	public Collection<IAnnotation> getAnnotations() {
		return cls.getAnnotations();
	}

	@Override
	public void addInterface(Class<?> intf) throws BuilderTypeException {
		cls.addInterface(intf);
	}
	
	@Override
	public Collection<Class<?>> getInterfaces() {
		return cls.getInterfaces();
	}
	
	@Override
	public ClassFactory getClassFactory() {
		return cls.getClassFactory();
	}
}
