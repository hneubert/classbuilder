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

package classbuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

/**
 * The IClass interface represents a class.
 */
public interface IClass {
	/**
	 * Modifier for public visibility.
	 */
	public final static short PUBLIC = 0x0001;
	
	/**
	 * Modifier for private-visibility.
	 */
	public final static short PRIVATE = 0x0002;
	
	/**
	 * Modifier for protected-visibility.
	 */
	public final static short PROTECTED = 0x0004;
	
	/**
	 * Modifier for static fields or methods.
	 */
	public final static short STATIC = 0x0008;
	
	/** 
	 * Modifier for unmodifiably fields or methods.
	 */
	public final static short FINAL = 0x0010;
	
	public final static short SUPER = 0x0020;
	
	/**
	 * Modifier for volatile fields.
	 */
	public final static short VOLATILE = 0x0040;
	
	/** 
	 * Modifier for non serializable fields.
	 */
	public final static short TRANSIENT = 0x0080;
	
	/** 
	 * Modifier for native methods.
	 */
	public final static short NATIVE = 0x0100;
	
	/** 
	 * Modifier for interfaces.
	 */
	public final static short INTERFACE = 0x0200;
	
	/** 
	 * Nodifier for abstract classes and methods.
	 */
	public final static short ABSTRACT = 0x0400;
	
	/** 
	 * Modifier for strict math.
	 */
	public final static short STRICT = 0x0800;
	
	/**
	 * Modifier for synchroniszed methods.
	 */
	public final static short SYNCHRONIZED = 0x1000;
	
	/**
	 * Modifier for enums and enum fields.
	 */
	public final static short ENUM = 0x4000;
	
	/**
	 * Creates a new initalized field.
	 * <p>
	 * The field is implicit public, static and final.
	 * The data type is implicit and must be primitive or a string.
	 * Only this method can create final fields.
	 * @param modifiers modifiers
	 * @param name field name
	 * @param value initial value
	 * @return new initalized field
	 * @throws BuilderTypeException invalid data type
	 * @throws BuilderNameException invalid field name
	 * @throws BuilderModifierException invalid modifier
	 */
	public IField addField(int modifiers, String name, Object value) throws BuilderModifierException, BuilderNameException, BuilderTypeException;
	
	/**
	 * Creates a new enum constant.
	 * @param name enum constant name
	 * @param args constructor arguments
	 * @return new enum constant
	 * @throws BuilderNameException invalid field name
	 * @throws BuilderSyntaxException enum field not allowed or creation failed
	 * @throws BuilderAccessException variable not initialized or method not found or inaccessible
	 * @throws BuilderTypeException invalid argument type
	 */
	public IField addEnumConstant(String name, Object... args) throws BuilderNameException, BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Gnerates and loads this class.
	 * @return new class
	 * @throws BuilderCompilerException compilation error
	 */
	public Class<?> build() throws BuilderCompilerException;
	
	/**
	 * Writes the class date to an output stream.
	 * @param out destination
	 * @throws BuilderCompilerException compilation error
	 * @throws IOException stream error
	 */
	public void write(OutputStream out) throws BuilderCompilerException, IOException;
	
	/**
	 * Returns the full class name.
	 * @return full class name
	 */
	public String getName();
	
	/**
	 * Returns the simple class name.
	 * @return simple class name
	 */
	public String getSimpleName();
	
	/**
	 * Returns true, if this class is an enum.
	 * @return true, if this class is an enum
	 */
	public boolean isEnum();
	
	/**
	 * Returns true, if this class is an interface.
	 * @return true, if this class is an interface
	 */
	public boolean isInterface();
	
	/**
	 * Returns the modifiers.
	 * @return modifiers
	 */
	public int getModifiers();
	
	/**
	 * Returns a declared field.
	 * @param name field name
	 * @return declared field
	 * @throws NoSuchFieldException no such field present
	 */
	public IField getField(String name) throws NoSuchFieldException;
	
	/**
	 * Returns all declared fields.
	 * @return all declared fields
	 */
	public Collection<IField> getFields();
	
	/**
	 * Returns a declared method.
	 * @param name method name
	 * @param paramTypes parameter types
	 * @return declared method
	 * @throws NoSuchMethodException no such method present
	 */
	public IMethod getMethod(String name, Class<?>[] paramTypes) throws NoSuchMethodException;
	
	/**
	 * Returns all declared methods.
	 * @return all declared methods
	 */
	public Collection<IMethod> getMethods();
	
	/**
	 * Returns a declared constructor.
	 * @param paramTypes parameter types
	 * @return declared constructor
	 * @throws NoSuchMethodException no such constructor present
	 */
	public IConstructor getConstructor(Class<?>[] paramTypes) throws NoSuchMethodException;
	
	/**
	 * Returns all declared constructors.
	 * @return all declared constructors
	 */
	public Collection<IConstructor> getConstructors();
	
	/**
	 * Writes the source to an output stream.
	 * @param out outout stream
	 * @throws IOException stream error
	 */
	public void writeSource(OutputStream out) throws IOException;
	
	/**
	 * Returns the super class or null for interfaces.
	 * @return super class or null
	 */
	public Class<?> getSuperclass();
	
	/**
	 * Returns the package name.
	 * @return package name
	 */
	public String getPackage();
	
	/**
	 * Returns the static initializer.
	 * @return static initializer
	 */
	public IMethod Static();
	
	/**
	 * Create a new field.
	 * @param modifiers modifiers
	 * @param name field name
	 * @param type field type
	 * @return new field
	 * @throws BuilderTypeException invalid field type
	 * @throws BuilderNameException invalid field name
	 * @throws BuilderModifierException invalid modifier
	 */
	public IField addField(int modifiers, Class<?> type, String name) throws BuilderModifierException, BuilderNameException, BuilderTypeException;
	
	/**
	 * Creates a new constructor.
	 * @param modifiers modifiers
	 * @param params parameter types
	 * @return new constructor
	 * @throws BuilderNameException constructor allready defined
	 * @throws BuilderTypeException invalid parameter type
	 * @throws BuilderModifierException invalid modifier
	 */
	public IConstructor addConstructor(int modifiers, Class<?> ...params) throws BuilderModifierException, BuilderNameException, BuilderTypeException;
	
	/**
	 * Creates a new method without return value.
	 * @param modifiers modifiers
	 * @param name der method name
	 * @param params parameter types
	 * @return new methods
	 * @throws BuilderNameException invalid method name
	 * @throws BuilderTypeException invalid parameter type
	 * @throws BuilderModifierException invalid modifier
	 */
	public IMethod addMethod(int modifiers, String name, Class<?> ...params) throws BuilderModifierException, BuilderNameException, BuilderTypeException;
	
	/**
	 * Creates a new method with return value.
	 * @param modifiers modifiers
	 * @param returnType return type
	 * @param name der method name
	 * @param params parameter types
	 * @return new methods
	 * @throws BuilderNameException invalid method name
	 * @throws BuilderTypeException invalid parameter type
	 * @throws BuilderModifierException invalid modifier
	 */
	public IMethod addMethod(int modifiers, Class<?> returnType, String name, Class<?> ...params) throws BuilderModifierException, BuilderNameException, BuilderTypeException;
	
	/**
	 * Adds an annotation.
	 * @param type annotation type
	 * @return new annotation
	 * @throws BuilderTypeException invalid annotation type
	 */
	public IAnnotation addAnnotation(Class<?> type) throws BuilderTypeException;
	
	/**
	 * Returns all annotations.
	 * @return all annotations
	 */
	public Collection<IAnnotation> getAnnotations();
	
	/**
	 * Adds an interface.
	 * @param intf interface
	 * @throws BuilderTypeException no interface type
	 */
	public void addInterface(Class<?> intf) throws BuilderTypeException;
	
	/**
	 * Returns all declared interfaces.
	 * @return all declared interfaces
	 */
	public Collection<Class<?>> getInterfaces();
	
	/**
	 * Return current ClassFactory.
	 * @return current ClassFactory
	 */
	public ClassFactory getClassFactory();
}
