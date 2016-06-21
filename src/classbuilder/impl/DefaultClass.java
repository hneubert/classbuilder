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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import classbuilder.BuilderCompilerException;
import classbuilder.BuilderException;
import classbuilder.BuilderModifierException;
import classbuilder.BuilderNameException;
import classbuilder.BuilderTypeException;
import classbuilder.ClassFactory;
import classbuilder.IAnnotation;
import classbuilder.IClass;
import classbuilder.IConstructor;
import classbuilder.IField;
import classbuilder.IMethod;
import classbuilder.Variable;
import classbuilder.impl.DefaultMethod.FragmentType;

public class DefaultClass implements IClass {
	protected String name;
	protected String pkg;
	
	protected List<Class<?>> interfaces;
	protected List<DefaultField> fields;
	protected List<DefaultMethod> functions;
	protected List<DefaultMethod> constructors;
	protected List<DefaultAnnotation> annotations;
	
	protected Class<?> superClass;
	protected int flags;
	
	protected DefaultMethod staticInitializer;
	
	protected ConstantPool constantPool;
	
	protected ClassFactory classFactory;
	
	protected static Method defineClass;
	
	static {
		try {
			defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
			defineClass.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("ClassLoader.defineClass not accessible", e);
		} catch (SecurityException e) {
			throw new RuntimeException("ClassLoader.defineClass not accessible", e);
		}
	}
	
	public DefaultClass(ClassFactory classFactory, int flags, String pkg, String name, Class<?> superClass, Class<?> ...intf) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		this.name = name;
		this.pkg = pkg;
		this.flags = flags;
		this.superClass = superClass;
		this.classFactory = classFactory;
		
		/*if ((flags & ENUM) != 0) {
			flags |= FINAL;
		} else*/ if ((flags & INTERFACE) != 0) {
			this.flags |= ABSTRACT;
			superClass = Object.class;
			this.superClass = Object.class;
		} else {
			flags |= SUPER;
			if (superClass == null || superClass.isInterface()) throw new BuilderTypeException(this, superClass);
		}
		
		if (superClass == null || superClass.isAnnotation() || superClass.isArray() || superClass.isEnum() || 
			superClass.isPrimitive() || superClass.isLocalClass() || (superClass.isMemberClass() && (superClass.getModifiers() & STATIC) == 0) || 
			superClass.isAnonymousClass() || (superClass.getModifiers() & FINAL) != 0) {
			throw new BuilderTypeException(this, superClass);
		}
		
		interfaces = new ArrayList<Class<?>>();
		fields = new ArrayList<DefaultField>();
		functions = new ArrayList<DefaultMethod>();
		constructors = new ArrayList<DefaultMethod>();
		annotations = new ArrayList<DefaultAnnotation>();
		constantPool = new ConstantPool(classFactory.getClassLoader());
		
		validate((this.flags & ~(VMConst.DEBUG | SUPER)), PUBLIC | ABSTRACT | FINAL | INTERFACE);
		validateName(name);
		validatePackageName(pkg);
		
		if (intf != null) {
			for (Class<?> i : intf) {
				if (i == null || !i.isInterface()) throw new BuilderTypeException(this, i);
				interfaces.add(i);
			}
		}
	}
	
	@Override
	public String getName() {
		if (pkg == null) {
			return name;
		} else {
			return pkg + "." + name;
		}
	}
	
	@Override
	public String getSimpleName() {
		return name;
	}
	
	@Override
	public Class<?> getSuperclass() {
		return superClass;
	}
	
	@Override
	public int getModifiers() {
		return flags;
	}
	
	@Override
	public IField getField(String name) throws NoSuchFieldException {
		for (DefaultField field : fields) {
			if (field.getName().equals(name)) {
				return field;
			}
		}
		throw new NoSuchFieldException(name);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection<IField> getFields() {
		return (List<IField>)(List<?>)fields;
	}
	
	@Override
	public IMethod getMethod(String name, Class<?>[] types) throws NoSuchMethodException {
		IMethod method = null;
		int best = 1000;
		
		for (IMethod m : functions) {
			if (!m.getName().equals(name)) continue;
			Variable[] dest = m.getParameters();
			if (types.length != dest.length) continue;
			int q = 0;
			
			for (int i = 0; i < types.length; i++) {
				int level = VMConst.isAssignable(types[i], dest[i].getType());
				if (level == -1) {
					q = -1;
					break;
				}
				q += level;
			}
			
			//int q = VMConst.testFucntion(args, m.getParameterTypes());
			if (q != -1 && best > q) {
				best = q;
				method = m;
			}
		}
		if (method != null) return method;
		throw new NoSuchMethodException();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection<IMethod> getMethods() {
		return (List<IMethod>)(List<?>)functions;
	}
	
	@Override
	public IConstructor getConstructor(Class<?>[] types) throws NoSuchMethodException {
		IConstructor method = null;
		int best = 1000;
		
		for (IConstructor m : constructors) {
			//if (!m.getName().equals(name)) break;
			Variable[] dest = m.getParameters();
			if (types.length != dest.length) break;
			int q = 0;
			
			for (int i = 0; i < types.length; i++) {
				int level = VMConst.isAssignable(types[i], dest[i].getType());
				if (level == -1) {
					q = -1;
					break;
				}
				q += level;
			}
			
			//int q = VMConst.testFucntion(args, m.getParameterTypes());
			if (q != -1 && best > q) {
				best = q;
				method = m;
			}
		}
		if (method != null) return method;
		throw new NoSuchMethodException();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection<IConstructor> getConstructors() {
		return (List<IConstructor>)(List<?>)constructors;
	}
	
	@Override
	public IField addField(int flags, Class<?> type, String name) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		validate(flags, PUBLIC | PROTECTED | PRIVATE | STATIC);
		validateName(name);
		testField(name);
		if (type == null || type == void.class || type == Void.class) throw new BuilderTypeException(this, type);
		DefaultField field = new DefaultField(this, flags, name, type, null, constantPool);
		fields.add(field);
		return field;
	}
	
	@Override
	public IField addField(int flags, String name, Object value) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		flags |= STATIC;
		if ((flags & INTERFACE) != 0) {
			validate(flags, PUBLIC | STATIC | FINAL);
			flags |= PUBLIC | STATIC | FINAL;
		}
		validate(flags, PUBLIC | PROTECTED | PRIVATE | STATIC | FINAL);
		validateName(name);
		testField(name);
		if (value == null) throw new BuilderTypeException(this, "<null>");
		Class<?> type = VMConst.getPrimitiveType(value.getClass());
		int level = VMConst.getLevel(type);
		if (level == -1 && type != String.class) throw new BuilderTypeException(this, type);
		DefaultField field = new DefaultField(this, flags, name, type, value, constantPool);
		fields.add(field);
		return field;
	}
	
//	public IField addEnumField(String name, Object args) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException {
//		DefaultField field = new DefaultField(this, ENUM | FINAL | PUBLIC | STATIC, name, Enum.class, null, constantPool);
//		IMethod s = Static();
//		s.get(field).set(s.New(Enum.class, args));
//		fields.add(field);
//		return field;
//	}
	
	@Override
	public IConstructor addConstructor(int flags, Class<?> ...params) throws BuilderModifierException, BuilderTypeException, BuilderNameException {
//		if ((this.flags & INTERFACE) != 0) throw new BuilderSyntaxException(this, BuilderSyntaxException);
		validate(flags, PUBLIC | PROTECTED | PRIVATE);
		validateParams(params);
		for (IConstructor constructor : constructors) {
			testConstructor(constructor, params);
		}
		DefaultMethod function = new DefaultMethod(FragmentType.CONSTRUCTOR, flags | (this.flags & VMConst.DEBUG), "<init>", null, this, constantPool, params);
		constructors.add(function);
		return function;
	}

	@Override
	public IMethod addMethod(int flags, String name, Class<?> ...params) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		return addMethod(flags, null, name, params);
	}

	@Override
	public IMethod addMethod(int flags, Class<?> returnType, String name, Class<?> ...params) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		if ((this.flags & INTERFACE) != 0) {
			validate(flags, PUBLIC | ABSTRACT);
			flags |= PUBLIC | ABSTRACT;
		}
		validate(flags, PUBLIC | PROTECTED | PRIVATE | STRICT | ABSTRACT | STATIC | NATIVE | FINAL | SYNCHRONIZED);
		validateName(name);
		validateParams(params);
		testMethod(name, params);
		if (returnType == null || returnType == void.class || returnType == Void.class) returnType = null;
		DefaultMethod function = new DefaultMethod(FragmentType.FUNCTION, flags | (this.flags & VMConst.DEBUG), name, returnType, this, constantPool, params);
		functions.add(function);
		return function;
	}


	@Override
	public Class<?> build() throws BuilderCompilerException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer;
		
		if ((flags & VMConst.ABSTRACT) == 0) {
			// nicht ausreichend protected wird nicht beachtet
			for (Method method : superClass.getMethods()) {
				if ((method.getModifiers() & VMConst.ABSTRACT) != 0) {
					try {
						getMethod(method.getName(), method.getParameterTypes());
					} catch (NoSuchMethodException e) {
						throw new BuilderCompilerException(this, "no implementation present: " + method.toGenericString(), e);
					}
				}
			}
			for (Class<?> intf : interfaces) {
				for (Method method : intf.getMethods()) {
					try {
						try {
							superClass.getMethod(method.getName(), method.getParameterTypes());
						} catch (NoSuchMethodException e) {
							getMethod(method.getName(), method.getParameterTypes());
						}
					} catch (NoSuchMethodException e) {
						throw new BuilderCompilerException(this, "no implementation present: " + method.toGenericString(), e);
					}
				}
			}
		}
		
		try {
			if (constructors.isEmpty() && (flags & INTERFACE) == 0) {
				IMethod c = addConstructor(PUBLIC);
					c.Super().invoke("<init>");
				c.End();
			}
		} catch (BuilderException e) {
			throw new BuilderCompilerException(this, e.getMessage(), e);
		}
		
//		InstructionWriter dynamicOut = new InstructionWriter();
//		InstructionWriter staticOut = new InstructionWriter();
//		for (DefaultField field : fields) {
//			try {
//				if (field.getValue() != null) {
//					if ((field.getModifiers() & STATIC) == 0) {
//						DefaultLValue thisNode = new DefaultLValue(null, null, NodeType.THIS, field, null, null);
//						DefaultLValue node = new DefaultLValue(null, null, NodeType.FSET, field, null, field.getType());
//						node.setNext(new DefaultLValue(null, node, NodeType.CONST, null, field.getValue(), field.getType()), false);
//						thisNode.setNext(node, false);
//						thisNode.build(null, dynamicOut, constantPool, false);
//					} else {
//						DefaultLValue node = new DefaultLValue(null, null, NodeType.SSET, field, null, field.getType());
//						node.setNext(new DefaultLValue(null, node, NodeType.CONST, null, field.getValue(), field.getType()), false);
//						node.build(null, staticOut, constantPool, false);
//					}
//				}
//			} catch (BuilderException e) {
//				throw new BuilderCompilerException(this, e.getMessage(), e);
//			}
//		}
//		
//		if (dynamicOut.getPos() != 0) {
//			for (DefaultMethod method : constructors) {
//				method.prepend(dynamicOut);
//			}
//		}
//		
//		if (staticOut.getPos() != 0) {
//			if (staticInitializer == null)
//				try {
//					Static().End();
//				} catch (BuilderSyntaxException e) {
//					throw new BuilderCompilerException(this, e.getMessage(), e);
//				}
//			staticInitializer.prepend(staticOut);
//		}
		
		testAnnotations(annotations);
		for (DefaultField field : fields) {
			testAnnotations(field.getDefaultAnnotations());
		}
		for (DefaultMethod method : functions) {
			if (!method.isClosed() && (method.getModifiers() & IClass.ABSTRACT) == 0) throw new BuilderCompilerException(this, BuilderCompilerException.METHOD_NOT_CLOSED, method.getName());
			testAnnotations(method.getDefaultAnnotations());
		}
		for (DefaultMethod method : constructors) {
			if (!method.isClosed() && (method.getModifiers() & IClass.ABSTRACT) == 0) throw new BuilderCompilerException(this, BuilderCompilerException.METHOD_NOT_CLOSED, method.getName());
			testAnnotations(method.getDefaultAnnotations());
		}
		
		try {
			write(out);
		} catch (IOException e) {
			throw new BuilderCompilerException(this, e.getMessage(), e);
		} catch (BuilderException e) {
			throw new BuilderCompilerException(this, e.getMessage(), e);
		}
		buffer = out.toByteArray();
		
		if (classFactory.getClassPath() != null) {
			Path path = Paths.get(classFactory.getClassPath() + File.separator + getName().replace('.', File.separatorChar) + ".class");
			
			if (!Files.exists(path.getParent())) {
				try {
					Files.createDirectories(path.getParent());
				} catch (IOException e) {
					throw new BuilderCompilerException(this, e.getMessage(), e);
				}
			}
			
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(path.toFile());
				fos.write(buffer);
				fos.close();
			} catch (Exception e) {
				throw new BuilderCompilerException(this, e.getMessage(), e);
			}
		}
		
		if (classFactory.getSourcePath() != null) {
			Path path = Paths.get(classFactory.getSourcePath() + File.separator + getName().replace('.', File.separatorChar) + ".java");
			
			if (!Files.exists(path.getParent())) {
				try {
					Files.createDirectories(path.getParent());
				} catch (IOException e) {
					throw new BuilderCompilerException(this, e.getMessage(), e);
				}
			}
			
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(path.toFile());
				writeSource(fos);
				fos.close();
			} catch (Exception e) {
				throw new BuilderCompilerException(this, e.getMessage(), e);
			}
		}
		
		try {
			return (Class<?>)defineClass.invoke(classFactory.getClassLoader(), getName(), buffer, 0, buffer.length);
		} catch (IllegalAccessException e) {
			throw new BuilderCompilerException(this, e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new BuilderCompilerException(this, e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new BuilderCompilerException(this, e.getTargetException().getMessage(), e);
		}
	}
	
	private void testAnnotations(List<DefaultAnnotation> annotations) throws BuilderCompilerException {
		for (DefaultAnnotation annotation : annotations) {
			annotation.validate();
		}
	}
	
	@Override
	public void write(OutputStream out) throws BuilderCompilerException, IOException {
		DataOutputStream classFile;
		byte [] main;
		byte [] cp;
		
		classFile = new DataOutputStream(out);
		
		classFile.writeInt(0xCAFEBABE);
		classFile.writeShort(0x0000);
		classFile.writeShort(50);
		
		main = writeMain();
		
		cp = constantPool.write();
		
		classFile.writeShort(constantPool.getSize()); // cp-size
		classFile.write(cp);
		
		classFile.write(main);
		classFile.close();
	}
	
	private byte [] writeMain() throws BuilderCompilerException, IOException {
		DataOutputStream classFile;
		ByteArrayOutputStream out;
		int lineNumber = 3 + annotations.size();
		
		out = new ByteArrayOutputStream();
		classFile = new DataOutputStream(out);
		
		// flags
		classFile.writeShort(flags & (~VMConst.DEBUG));
		
		// this
		classFile.writeShort(constantPool.add(this));
		
		// super
		classFile.writeShort(constantPool.add(superClass));
		
		// interfaces
		classFile.writeShort(interfaces.size());
		for (Class<?> cls : interfaces) {
			classFile.writeShort(constantPool.add(cls));
		}
		
		// fields
		classFile.writeShort(fields.size());
		for (DefaultField field : fields) {
			lineNumber = field.write(classFile, lineNumber);
		}
		
		// methods
		List<DefaultMethod> allMethods = new ArrayList<DefaultMethod>();
		allMethods.addAll(constructors);
		allMethods.addAll(functions);
		
		classFile.writeShort(allMethods.size());
		for (DefaultMethod method : allMethods) {
			lineNumber = method.write(classFile, lineNumber);
		}
		
		int attr = 0;
//		if (attributes != null) attr += attributes.length;
		if (annotations.size() > 0) {
			classFile.writeShort(attr + 1);//attributes_count;
			DefaultAnnotation.writeAnnotations(classFile, constantPool, annotations);
		} else {
			classFile.writeShort(attr);//attributes_count;
		}
//		if (attributes != null) {
//			for (DefaultAttribute a : attributes) {
//				classFile.writeShort(a.getTag());
//				classFile.writeInt(a.getData().length);
//				classFile.write(a.getData());
//			}
//		}
		
		classFile.close();
		
		return out.toByteArray();
	}
	
	private void validatePackageName(String name) throws BuilderNameException {
		if (name == null) throw new BuilderNameException(this, "<null>");
		if (name.equals("")) throw new BuilderNameException(this, "<empty>");
		if (name.startsWith(".")) throw new BuilderNameException(this, name);
		if (name.endsWith(".")) throw new BuilderNameException(this, name);
		for (int i = 0; i < name.length(); i++) {
			int k = name.charAt(i);
			if (k == '$' || k == '_' || k == '.') continue;
			if (k >= '0' && k <= '9' && i != 0) continue;
			if (k >= 'a' && k <= 'z') continue;
			if (k >= 'A' && k <= 'Z') continue;
			throw new BuilderNameException(this, name);
		}
	}
	
	private void validateName(String name) throws BuilderNameException {
		validatePackageName(name);
		if (name.indexOf('.') != -1) {
			throw new BuilderNameException(this, name);
		}
	}
	
	private void validateParams(Class<?>[] params) throws BuilderTypeException {
		for (Class<?> type : params) {
			if (type == null || type == void.class || type == Void.class) throw new BuilderTypeException(this, type);
		}
	}
	
	@Override
	public void writeSource(OutputStream out) throws IOException {
		boolean first = true;
		OutputStreamWriter writer = new OutputStreamWriter(out);
		String s;
		
		writer.write("package " + pkg + ";\n");
		writer.write("\n");
		for (DefaultAnnotation annotation : annotations) {
			writer.write(annotation.toString() + "\n");
		}
		s = VMConst.getModifier(flags) + "class " + name + " ";
		for (Class<?> cls : interfaces) {
			if (first) s += "implements ";
			if (!first) s += ", ";
			s += cls.getCanonicalName();
			first = false;
		}
		s += " {\n";
		writer.write(s);
		
		for (DefaultField field : fields) {
			writer.write(field.write());
		}
		
		writer.flush();
		for (DefaultMethod function : constructors) {
			function.writeSource(out);
		}
		for (DefaultMethod function : functions) {
			function.writeSource(out);
		}
		
		writer.write("}");
		
		writer.close();
	}
	
	private void testField(String name) throws BuilderNameException {
		for (IField field : fields) {
			if (field.getName().equals(name)) throw new BuilderNameException(this, name);
		}
		Class<?> cls = superClass;
		while (cls != null) {
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				if ((field.getModifiers() & PRIVATE) != 0 && field.getName().equals(name)) throw new BuilderNameException(this, name);
			}
			cls = cls.getSuperclass();
		}
	}
	
	private void testMethod(String name, Class<?>[] types) throws BuilderNameException, BuilderModifierException {
		try {
			// protected?!
			Method method = superClass.getMethod(name, types);
			if ((method.getModifiers() & FINAL) != 0) {
				throw new BuilderModifierException(this, VMConst.getModifier(FINAL));
			} else if ((method.getModifiers() & (STATIC | PRIVATE)) == STATIC) {
				throw new BuilderModifierException(this, VMConst.getModifier(STATIC));
			}
		} catch (NoSuchMethodException e) {
			
		} catch (SecurityException e) {
			
		}
		for (IMethod method : functions) {
			if (method.getName().equals(name)) {
				testConstructor(method, types);
			}
		}
	}
	
	private void testConstructor(IMethod method, Class<?>[] types) throws BuilderNameException {
		if (method.getParameters().length != types.length) return;
		for (int i = 0; i < types.length; i++) {
			if (method.getParameter(i).getType() != types[i]) return;
		}
		throw new BuilderNameException(this, method.getName());
	}
	
	private void validate(int modifier, int allowed) throws BuilderModifierException {
		if ((modifier & 0xFFFFE000) != 0) throw new BuilderModifierException(this, "<unknown modifier>");
		
		if ((modifier & PUBLIC)				!= 0 && ((allowed & PUBLIC) == 0 || (modifier & (PRIVATE | PROTECTED)) != 0)) throw new BuilderModifierException(this, "public");
		if ((modifier & PRIVATE)			!= 0 && ((allowed & PRIVATE) == 0 || (modifier & (PUBLIC | PROTECTED)) != 0)) throw new BuilderModifierException(this, "private");
		if ((modifier & PROTECTED)			!= 0 && ((allowed & PROTECTED) == 0 || (modifier & (PRIVATE | PUBLIC)) != 0)) throw new BuilderModifierException(this, "protected");
		
		if ((modifier & STATIC)				!= 0 && ((allowed & STATIC) == 0 || (modifier & ABSTRACT) != 0)) throw new BuilderModifierException(this, "static");
		if ((modifier & ABSTRACT)			!= 0 && ((allowed & ABSTRACT) == 0 || (modifier & (STATIC | FINAL)) != 0)) throw new BuilderModifierException(this, "abstract");
		
		if ((modifier & FINAL)				!= 0 && (allowed & FINAL) == 0) throw new BuilderModifierException(this, "final");
		if ((modifier & SUPER)				!= 0 && (allowed & SUPER) == 0) throw new BuilderModifierException(this, "super");
		if ((modifier & VOLATILE)			!= 0 && (allowed & VOLATILE) == 0) throw new BuilderModifierException(this, "volatile");
		if ((modifier & TRANSIENT)			!= 0 && (allowed & TRANSIENT) == 0) throw new BuilderModifierException(this, "transient");
		if ((modifier & NATIVE)				!= 0 && (allowed & NATIVE) == 0) throw new BuilderModifierException(this, "native");
		if ((modifier & INTERFACE)			!= 0 && ((allowed & INTERFACE) == 0 || (modifier & ABSTRACT) == 0)) throw new BuilderModifierException(this, "interface");
		if ((modifier & STRICT)				!= 0 && (allowed & STRICT) == 0) throw new BuilderModifierException(this, "strict");
		if ((modifier & SYNCHRONIZED)		!= 0 && (allowed & SYNCHRONIZED) == 0) throw new BuilderModifierException(this, "synchronized");
	}
	
	@Override
	public IMethod Static() {
		if (staticInitializer == null) {
			staticInitializer = new DefaultMethod(FragmentType.FUNCTION, STATIC, "<clinit>", null, this, constantPool);
			functions.add(staticInitializer);
		}
		return staticInitializer;
	}
	
	@Override
	public String getPackage() {
		return pkg;
	}
	
	public Collection<Class<?>> getInterfaces() {
		return interfaces;
	}
	
	@Override
	public IAnnotation addAnnotation(Class<?> type) throws BuilderTypeException {
		DefaultAnnotation annotation = new DefaultAnnotation(this, type, ElementType.TYPE, constantPool);
		annotations.add(annotation);
		return annotation;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection<IAnnotation> getAnnotations() {
		return (List<IAnnotation>)(List<?>)annotations;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public ClassFactory getClassFactory() {
		return classFactory;
	}
}
