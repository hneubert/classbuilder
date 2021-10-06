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

package classbuilder.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import classbuilder.IClass;
import classbuilder.IMethod;

/**
 * The MethodId class represents a single method signature.
 */
public class MethodId {
	public static final int NON_ABSTRACT = 0x00010000;
	
	private static final Class<?>[] noParameters = new Class<?>[] {};
	
	private int hashCode;
	private Class<?>[] types;
	private String name;
	private Class<?> returnType;
	private Object declaration;
	
	private MethodId(String name, Class<?>[] types, Class<?> returnType, Object declaration) {
		if (types == null) {
			types = noParameters;
		}
		this.types = types;
		if (name == null) {
			name = "<init>";
		}
		this.name = name;
		hashCode = name.hashCode();
		for (Class<?> type : types) {
			hashCode ^= type.hashCode();
		}
		this.returnType = returnType;
		this.declaration = declaration;
	}
	
	/**
	 * Creates a new MethodId by a Method object.
	 * @param method an existing method
	 * @return new MethodId object
	 */
	public static MethodId getMethodId(Method method) {
		return new MethodId(method.getName(), method.getParameterTypes(), method.getReturnType(), method);
	}
	
	/**
	 * Creates a new MethodId by a Constructor object.
	 * @param constructor an existing constructor
	 * @return new MethodId object
	 */
	public static MethodId getMethodId(Constructor<?> constructor) {
		return new MethodId("<init>", constructor.getParameterTypes(), null, constructor);
	}
	
	/**
	 * Creates a new MethodId object.
	 * @param name method name
	 * @param types parameter types
	 * @param returnType return tyoe
	 * @return new MethodId object
	 */
	public static MethodId getMethodId(String name, Class<?>[] types, Class<?> returnType) {
		return new MethodId(name, types, returnType, null);
	}
	
	public int getModifier() {
		return 0;
	}
	
	/**
	 * Returns the method name.
	 * @return method name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the method parameter types.
	 * @return method parameter types
	 */
	public Class<?>[] getTypes() {
		return types;
	}
	
	/**
	 * Returns the method return type.
	 * @return method return type
	 */
	public Class<?> getReturnType() {
		return returnType;
	}
	
	/**
	 * Returns the declaring class or interface.
	 * @return declaring class or interface
	 */
	public Object getDeclaration() {
		return declaration;
	}
	
	/**
	 * Calculates all method signatures for a method, proxy or constructor handler.
	 * @param handlerContext handler context
	 * @return list of method signatures
	 * @throws HandlerException invalid Handler annotation
	 */
	public static Collection<MethodId> getMethods(HandlerContext handlerContext) throws HandlerException {
		boolean override = false;
		if (ProxyHandler.class.isAssignableFrom(handlerContext.getHandler())) {
			override = true;
		}
		return getMethods(handlerContext, override);
	}
	
	/**
	 * Calculates all method signatures for a method, proxy or constructor handler.
	 * @param handlerContext handler context
	 * @param override include non abstract methods, if true
	 * @return list of method signatures
	 * @throws HandlerException invalid Handler annotation
	 */
	public static Collection<MethodId> getMethods(HandlerContext handlerContext, boolean override) throws HandlerException {
		if (ConstructorHandler.class.isAssignableFrom(handlerContext.getHandler())) {
			if (handlerContext.getAnnotatedElement() instanceof Class) {
				Class<?> cls = (Class<?>)handlerContext.getAnnotatedElement();
				Collection<MethodId> result = new ArrayList<MethodId>();
				for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
					if ((constructor.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0) {
						result.add(MethodId.getMethodId(constructor));
					}
				}
				return result;
			} else if (handlerContext.getAnnotatedElement() instanceof Constructor) {
				return Arrays.asList(MethodId.getMethodId((Constructor<?>)handlerContext.getAnnotatedElement()));
			} else {
				throw new HandlerException("constructor-handlers are only on super-classes or super-constructors allowed");
			}
		} else {
			if (handlerContext.getAnnotatedElement() instanceof Class) {
				return MethodId.getAllMethods(handlerContext, override);
			} else if (handlerContext.getAnnotatedElement() instanceof Method) {
				return Arrays.asList(MethodId.getMethodId((Method)handlerContext.getAnnotatedElement()));
			} else {
				return null;
			}
		}
	}
	
	/**
	 * Calculates all method signatures for a method, proxy or constructor handler.<br>
	 * This method optionally includes non abstract methods.
	 * @param handlerContext handler context
	 * @param override include non abstract methods, if true
	 * @return list of method signatures
	 */
	public static Collection<MethodId> getAllMethods(HandlerContext handlerContext, boolean override) {
		Set<MethodId> set = new HashSet<MethodId>();
		Class<?> type = handlerContext.getSubclass().getSuperclass();
		
		for (Class<?> intf : handlerContext.getSubclass().getInterfaces()) {
			for (Method method : intf.getMethods()) {
				set.add(MethodId.getMethodId(method));
			}
		}
		
		while (type != null && type != Object.class) {
			for (Class<?> intf : type.getInterfaces()) {
				for (Method method : intf.getMethods()) {
					set.add(MethodId.getMethodId(method));
				}
			}
			type = type.getSuperclass();
		}
		
		type = handlerContext.getSubclass().getSuperclass();
		while (type != null && type != Object.class) {
			for (Method method : type.getDeclaredMethods()) {
				if ((method.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0) {
					if ((method.getModifiers() & Modifier.ABSTRACT) != 0 || override) {
						set.add(MethodId.getMethodId(method));
					} else {
						set.remove(MethodId.getMethodId(method));
					}
				}
			}
			type = type.getSuperclass();
		}
		
		return set;
	}
	
	/**
	 * Calculates a filtered list of method signatures for a method, proxy or constructor handler.<br>
	 * This method optionally includes non abstract methods.
	 * @param handlerContext handler context
	 * @param override include non abstract methods, if true
	 * @param filter user defined method filter
	 * @return list of method signatures
	 * 
	 * @see MethodFilter
	 */
	public static Collection<MethodId> getMethods(HandlerContext handlerContext, boolean override, MethodFilter filter) {
		Collection<MethodId> methods = getAllMethods(handlerContext, override);
		Collection<MethodId> result = new ArrayList<MethodId>();
		for (MethodId method : methods) {
			if (filter.checkMethod(method)) {
				result.add(method);
			}
		}
		return result;
	}
	
	public static Collection<MethodId> findMethod(Collection<MethodId> methods, int modifier, Class<?> returnType, String prefix, String suffix, Class<?>[] parameterTypes) {
		ArrayList<MethodId> list = new ArrayList<MethodId>();
		for (MethodId m : methods) {
			if (isMethod(m, modifier, returnType, prefix, suffix, parameterTypes)) {
				list.add(m);
			}
		}
		return list;
	}
	
	public static MethodId findMethods(Collection<MethodId> methods, int modifier, Class<?> returnType, String prefix, String suffix, Class<?>[] parameterTypes) {
		for (MethodId m : methods) {
			if (isMethod(m, modifier, returnType, prefix, suffix, parameterTypes)) {
				return m;
			}
		}
		return null;
	}
	
	public static boolean isMethod(MethodId method, int modifier, Class<?> returnType, String prefix, String suffix, Class<?>[] parameterTypes) {
		if ((method.getModifier() & modifier & (IClass.PUBLIC | IClass.PROTECTED)) == 0) return false;
		if ((method.getModifier() & IClass.ABSTRACT) != 0) {
			if ((modifier & IClass.ABSTRACT) == 0) return false;
		} else {
			if ((modifier & NON_ABSTRACT) == 0) return false;
		}
		if (returnType != null && returnType != method.getReturnType()) return false;
		if (prefix != null && !method.getName().startsWith(prefix)) return false;
		if (suffix != null && !method.getName().endsWith(suffix)) return false;
		if (parameterTypes != null) {
			if (parameterTypes.length != method.getTypes().length) return false;
			for (int i = 0; i < parameterTypes.length; i++) {
				if (parameterTypes[i] != method.getTypes()[i]) {
					return false;
				}
			}
		}
		return true;
	}

	
	@Override
	public boolean equals(Object other) {
		String name;
		Class<?>[] types;
		if (other instanceof Method) {
			Method method = (Method)other;
			name = method.getName();
			types = method.getParameterTypes();
		} else if (other instanceof Constructor) {
			Constructor<?> constructor = (Constructor<?>)other;
			name = constructor.getName();
			types = constructor.getParameterTypes();
		} else if (other instanceof IMethod) {
			IMethod method = (IMethod)other;
			name = method.getName();
			types = method.getParameterTypes();
		} else if (other instanceof MethodId) {
			MethodId method = (MethodId)other;
			name = method.name;
			types = method.types;
		} else {
			return false;
		}
		if (this.name.equals(name) && Arrays.equals(this.types, types)) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
}
