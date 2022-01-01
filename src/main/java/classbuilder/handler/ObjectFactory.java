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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import classbuilder.BuilderException;
import classbuilder.ClassFactory;
import classbuilder.IClass;
import classbuilder.IConstructor;
import classbuilder.IMethod;
import classbuilder.Value;
import classbuilder.handler.impl.DefaultConstructorHandler;
import classbuilder.handler.impl.DefaultHandlerContext;
import classbuilder.handler.impl.InstantiationHelper;
import classbuilder.handler.impl.MethodInvocationHelper;
import classbuilder.impl.VMConst;

/**
 * The ObjectFactory creates sub classes by a primary types and additional interfaces, which are annotated with 
 * class, constructor, method and proxy handlers. This allows an aspect oriented programming.
 */
public class ObjectFactory {
	
	private static class AnnotationInfo {
		public boolean isHandler;
		public Handler[] handler;
		
		public AnnotationInfo(boolean isHandler, Handler[] handler) {
			this.isHandler = isHandler;
			this.handler = handler;
		}
	}
	
	private static class HandlerData {
		private ObjectFactory objectFactory;
		private IClass subclass;
		private Map<String, Object> metadata;
		
		public Map<HandlerContext, HandlerContext> classHandlers;
		public Map<MethodId, MethodInvocationHelper> methodHandlers;
		public Map<MethodId, HandlerContext> constructorHandlers;
		
		public Set<Class<?>> ignore;
		public Map<MethodId, Set<Class<?>>> methodIgnore;
		
		private static Set<Class<?>> ignoreAll;
		
		static {
			ignoreAll = new HashSet<Class<?>>();
		}
		
		public HandlerData(ObjectFactory objectFactory, IClass subclass, Map<String, Object> metadata) {
			this.objectFactory = objectFactory;
			this.subclass = subclass;
			this.metadata = metadata;
			
			classHandlers = new TreeMap<HandlerContext, HandlerContext>();
			methodHandlers = new HashMap<MethodId, MethodInvocationHelper>();
			constructorHandlers = new HashMap<MethodId, HandlerContext>();
			ignore = new HashSet<Class<?>>();
			methodIgnore = new HashMap<MethodId, Set<Class<?>>>();
		}
		
		public void add(Object target, Ignore ignore) {
			MethodId methodId = null;
			
			if (target instanceof Constructor) {
				methodId = MethodId.getMethodId((Constructor<?>)target);
			} else if (target instanceof Method) {
				methodId = MethodId.getMethodId((Method)target);
			} else if (target instanceof Class) {
				if (ignore.value().length == 0) {
					this.ignore = ignoreAll;
					classHandlers.clear();
					constructorHandlers.clear();
					methodHandlers.clear();
				} else if (this.ignore != ignoreAll) {
					for (Class<?> cls : ignore.value()) {
						Iterator<HandlerContext> it = classHandlers.values().iterator();
						while (it.hasNext()) {
							HandlerContext ctx = it.next();
							if (ctx.getHandler() == cls) it.remove();
						}
						it = constructorHandlers.values().iterator();
						while (it.hasNext()) {
							HandlerContext ctx = it.next();
							if (ctx.getHandler() == cls) it.remove();
						}
						Iterator<MethodInvocationHelper> mi = methodHandlers.values().iterator();
						while (mi.hasNext()) {
							MethodInvocationHelper ctx = mi.next();
							if (ctx.getMethodHandler() != null && ctx.getMethodHandler().getHandler() == cls) ctx.setMethodHandler(null);
							ctx.removeProxyHandler(cls);
						}
						this.ignore.add(cls);
					}
				}
			}
			if (methodId != null) {
				if (ignore.value().length == 0) {
					methodIgnore.put(methodId, ignoreAll);
					if (target instanceof Constructor) constructorHandlers.remove(methodId);
					if (target instanceof Method) methodHandlers.remove(methodId);
				} else {
					Set<Class<?>> set = methodIgnore.get(methodId);
					if (set == null) {
						set = new HashSet<Class<?>>();
						methodIgnore.put(methodId, set);
					}
					if (set != ignoreAll) {
						for (Class<?> cls : ignore.value()) {
							set.add(cls);
							if (target instanceof Constructor) {
								HandlerContext ctx = constructorHandlers.get(methodId);
								if (ctx.getHandler() == cls) constructorHandlers.remove(methodId);
							}
							if (target instanceof Method) {
								MethodInvocationHelper ctx = methodHandlers.get(methodId);
								if (ctx.getMethodHandler() != null && ctx.getMethodHandler().getHandler() == cls) ctx.setMethodHandler(null);
								ctx.removeProxyHandler(cls);
							}
						}
					}
				}
			}
		}
		
		public void add(Annotation annotation, Handler handlerAnnotation, Object target) throws HandlerException {
			Class<?> handlerType = handlerAnnotation.value();
			if (ignore == ignoreAll || ignore.contains(handlerType)) return;
			DefaultHandlerContext ctx = new DefaultHandlerContext(objectFactory, subclass, annotation, handlerType, handlerAnnotation.priority(), target, metadata);
			
			if (ClassHandler.class.isAssignableFrom(handlerType)) {
				HandlerContext c = classHandlers.get(ctx);
				if (c == null || c.getPriority() <= ctx.getPriority()) {
					classHandlers.put(ctx, ctx);
				}
			}
			
			Collection<MethodId> methods = null;
			if (MethodSelector.class.isAssignableFrom(handlerType)) {
				try {
					methods = ((MethodSelector)handlerType.getConstructor().newInstance()).getMethods(ctx);
				} catch (Exception e) {
					throw new HandlerException("method selector instanziation faild: ", e);
				}
			}
			
			if (ConstructorHandler.class.isAssignableFrom(handlerType)) {
				if (methods == null) {
					methods = MethodId.getMethods(ctx);
				}
				for (MethodId method : methods) {
					if (ignoreMethod(method, handlerType)) continue;
					HandlerContext c = constructorHandlers.get(method);
					if (c == null || c.getPriority() <= ctx.getPriority()) {
						constructorHandlers.put(method, ctx);
					}
				}
			} else if (MethodHandler.class.isAssignableFrom(handlerType)) {
				if (methods == null) {
					methods = MethodId.getMethods(ctx);
				}
				for (MethodId method : methods) {
					if (ignoreMethod(method, handlerType)) continue;
					MethodInvocationHelper helper = methodHandlers.get(method);
					if (helper == null) {
						helper = new MethodInvocationHelper();
						helper.setTarget(method);
						methodHandlers.put(method, helper);
					}
					helper.setMethodHandler(ctx);
				}
			} else if (ProxyHandler.class.isAssignableFrom(handlerType)) {
				if (methods == null) {
					methods = MethodId.getMethods(ctx);
				}
				for (MethodId method : methods) {
					if (ignoreMethod(method, handlerType)) continue;
					MethodInvocationHelper helper = methodHandlers.get(method);
					if (helper == null) {
						helper = new MethodInvocationHelper();
						helper.setTarget(method);
						methodHandlers.put(method, helper);
					}
					helper.addProxyHandler(ctx);
				}
			}
		}
		
		private boolean ignoreMethod(MethodId method, Class<?> handler) {
			Set<Class<?>> set = methodIgnore.get(method);
			if (set != null && set.contains(handler)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	private static Class<?>[] noInterfaces = new Class<?>[] {};
	
	private ClassFactory classFactory;
	private Class<?>[] interfaces;
	private Map<String, Object> metadata;
	private String suffix;
	
	// cache
	private Map<Class<?>, Class<?>> typeMap;
	private Map<Class<?>, InstantiationHelper> helperMap;
	
	private Map<Class<?>, AnnotationInfo> annotationCache;
	
	/**
	 * Sole constructor, which creates a new ClassFactory instance.
	 */
	public ObjectFactory() {
		classFactory = new ClassFactory();
		classFactory.setClassLoader(new HandlerClassLoader(Thread.currentThread().getContextClassLoader(), this));
		interfaces = new Class[0];
		typeMap = new ConcurrentHashMap<Class<?>, Class<?>>();
		helperMap = new ConcurrentHashMap<Class<?>, InstantiationHelper>();
		suffix = "generated";
		annotationCache = new ConcurrentHashMap<Class<?>, AnnotationInfo>();
		this.metadata = new ConcurrentHashMap<String, Object>();
	}
	
	/**
	 * Releases all references to a generated sub class by its primary type.
	 * @param primaryType super class or interface
	 */
	public void release(Class<?> primaryType) {
		typeMap.remove(primaryType);
	}
	
	/**
	 * Releases all references to all generated sub class.
	 */
	public void release() {
		typeMap.clear();
	}
	
	/**
	 * Creates an new sub class by a primary type, which can be a super class or interface.
	 * @param primaryType super class or interface
	 * @param interfaces additional interfaces, overrides interfaces property of ObjectFactory
	 * @param metadata additional meta-data, overrides metadata property of ObjectFactory
	 * @return generierte generated sub class
	 * @throws BuilderException implementation error
	 * @throws HandlerException handler error
	 */
	public Class<?> getSubclass(Class<?> primaryType, Class<?>[] interfaces, Map<String, Object> metadata) throws BuilderException, HandlerException {
		Class<?> subclass = typeMap.get(primaryType);
		if (subclass == null) {
			synchronized (this) {
				subclass = typeMap.get(primaryType);
				if (subclass != null) {
					return subclass;
				}
				if (interfaces == null) interfaces = noInterfaces;
				if (metadata == null) metadata = this.metadata;
				subclass = createSubclass(primaryType, interfaces, metadata);
				typeMap.put(primaryType, subclass);
				boolean defaultOnly = true;
				if (subclass.getDeclaredConstructors().length > 1) {
					defaultOnly = false;
				} else {
					try {
						subclass.getConstructor();
					} catch (Exception e) {
						defaultOnly = false;
					}
				}
				if (!defaultOnly) {
					try {
						helperMap.put(primaryType, (InstantiationHelper)createInstantiationHelper(subclass).getConstructor().newInstance());
					} catch (BuilderException e) {
						throw e;
					} catch (Exception e) {
						throw new HandlerException("instantiation faild", e);
					}
				}
			}
		}
		return subclass;
	}
	
	/**
	 * Creates an new sub class by a primary type, which can be a super class or interface.
	 * @param primaryType super class or interface
	 * @return generierte generated sub class
	 * @throws BuilderException implementation error
	 * @throws HandlerException handler error
	 */
	public Class<?> getSubclass(Class<?> primaryType) throws BuilderException, HandlerException {
		return getSubclass(primaryType, interfaces, metadata);
	}
	
	/**
	 * Creates a new instance of a sub class by a primary type.
	 * @param <T> super class or interface
	 * @param primaryType super class or interface
	 * @param parameters constructor parameters
	 * @return new sub class instance
	 * @throws BuilderException implementation error
	 * @throws HandlerException handler error
	 */
	@SuppressWarnings("unchecked")
	public <T> T create(Class<T> primaryType, Object... parameters) throws BuilderException, HandlerException {
		try {
			Class<?> type = getSubclass(primaryType, interfaces, metadata);
			InstantiationHelper helper = helperMap.get(primaryType);
			if (helper != null) {
				T obj = (T)helper.__newInstance(parameters);
				if (obj == null) throw new HandlerException("no appropriate constructor");
				return obj;
			}
			return (T)type.getConstructor().newInstance();
		} catch (BuilderException e) {
			throw e;
		} catch (HandlerException e) {
			throw e;
		} catch (Exception e) {
			throw new HandlerException("instantiation faild: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Creates a new instance of a sub class by a primary type.
	 * @param <T> super class or interface
	 * @param primaryType super class or interface
	 * @return new sub class instance
	 * @throws BuilderException implementation error
	 * @throws HandlerException handler error
	 */
	@SuppressWarnings("unchecked")
	public <T> T create(Class<T> primaryType) throws BuilderException, HandlerException {
		try {
			Class<?> type = getSubclass(primaryType, interfaces, metadata);
			return (T)type.getConstructor().newInstance();
		} catch (BuilderException e) {
			throw e;
		} catch (HandlerException e) {
			throw e;
		} catch (Exception e) {
			throw new HandlerException("instantiation faild: " + e.getMessage(), e);
		}
	}
	
	private Class<?> createSubclass(Class<?> primaryType, Class<?>[] interfaces, Map<String, Object> metadata) throws BuilderException, HandlerException {
		IClass cls;
		String pkg = primaryType.getName().substring(0, primaryType.getName().length() - primaryType.getSimpleName().length() - 1);
		if (primaryType.isInterface()) {
			Class<?>[] interfacesTemp = Arrays.copyOf(interfaces, interfaces.length + 1);
			interfacesTemp[interfacesTemp.length - 1] = primaryType;
			cls = classFactory.createClass(IClass.PUBLIC, pkg, primaryType.getSimpleName() + "$" + suffix, Object.class, interfacesTemp);
		} else {
			cls = classFactory.createClass(IClass.PUBLIC, pkg, primaryType.getSimpleName() + "$" + suffix, primaryType, interfaces);
		}
		
		if (Serializable.class.isAssignableFrom(cls.getSuperclass())) {
			try {
				Field field = cls.getSuperclass().getDeclaredField("serialVersionUID");
				field.setAccessible(true);
				cls.addField(IClass.PRIVATE | IClass.STATIC | IClass.FINAL, "serialVersionUID", field.get(null));
			} catch (Exception e) {
				
			}
		}
		
		HandlerData data = new HandlerData(this, cls, metadata);
		
		for (Class<?> intf : interfaces) {
			handleType(intf, data);
		}
		
		handleType(primaryType, data);
		
		if (data.constructorHandlers.isEmpty()) {
			for (Constructor<?> constructor : cls.getSuperclass().getConstructors()) {
				DefaultConstructorHandler handler = new DefaultConstructorHandler();
				IConstructor ctor = cls.addConstructor(IClass.PUBLIC, constructor.getParameterTypes());
					handler.handle(null, cls, ctor);
				ctor.End();
			}
		}
		
		for (HandlerContext ctx : data.classHandlers.values()) {
			Class<?> handlerType = ctx.getHandler();
			try {
				ClassHandler handler = (ClassHandler)handlerType.getConstructor().newInstance();
				handler.handle(ctx, cls);
			} catch (BuilderException e) {
				throw e;
			} catch (HandlerException e) {
				throw e;
			} catch (Exception e) {
				throw new HandlerException("class handler instanziation faild: ", e);
			}
		}
		
		for (Entry<MethodId, MethodInvocationHelper> entry : data.methodHandlers.entrySet()) {
			MethodId id = entry.getKey();
			IMethod method;
			if (id.getReturnType() != null) {
				method = cls.addMethod(IClass.PUBLIC, id.getReturnType(), id.getName(), id.getTypes());
			} else {
				method = cls.addMethod(IClass.PUBLIC, id.getName(), id.getTypes());
			}
			entry.getValue().handle(method);
			method.End();
		}
		
		for (Entry<MethodId, HandlerContext> entry : data.constructorHandlers.entrySet()) {
			MethodId id = entry.getKey();
			IConstructor constructor;
			constructor = cls.addConstructor(IClass.PUBLIC, id.getTypes());
			Class<?> handlerType = entry.getValue().getHandler();
			try {
				ConstructorHandler handler = (ConstructorHandler)handlerType.getConstructor().newInstance();
				handler.handle(entry.getValue(), cls, constructor);
			} catch (BuilderException e) {
				throw e;
			} catch (HandlerException e) {
				throw e;
			} catch (Exception e) {
				throw new HandlerException("class handler instanziation faild: ", e);
			}
			constructor.End();
		}
		
		return cls.build();
	}
	
	private void handleType(Class<?> type, HandlerData data) throws HandlerException {
		Class<?> superClass = type.getSuperclass();
		if (superClass != Object.class && superClass != null) {
			handleType(superClass, data);
		}
		
		for (Class<?> intf : type.getInterfaces()) {
			handleType(intf, data);
		}
		
		findHandler(type.getDeclaredAnnotations(), type, data);
		
		for (Method method : type.getDeclaredMethods()) {
			findHandler(method.getDeclaredAnnotations(), method, data);
		}
		
		for (Field field : type.getDeclaredFields()) {
			findHandler(field.getDeclaredAnnotations(), field, data);
		}
		
		for (Constructor<?> constructor : type.getDeclaredConstructors()) {
			findHandler(constructor.getDeclaredAnnotations(), constructor, data);
		}
	}
	
	private void findHandler(Annotation[] annotations, Object target, HandlerData data) throws HandlerException {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == Ignore.class) {
				data.add(target, (Ignore)annotation);
			}
		}
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == Handlers.class) {
				for (Handler handler : ((Handlers)annotation).value()) {
					data.add(annotation, handler, target);
				}
			} else if (annotation.annotationType() == Handler.class) {
				data.add(annotation, (Handler)annotation, target);
			} else {
				AnnotationInfo info = annotationCache.get(annotation.annotationType());
				Class<?> annotationType = annotation.annotationType();
				if (info == null) {
					Handler[] handlerAnnotation = annotationType.getAnnotationsByType(Handler.class);
					info = new AnnotationInfo(handlerAnnotation != null, handlerAnnotation);
					annotationCache.put(annotationType, info);
				}
				if (info.isHandler) {
					for (Handler handler : info.handler) {
						data.add(annotation, handler, target);
					}
				}
			}
		}
	}
	
	private Class<?> createInstantiationHelper(Class<?> cls) throws BuilderException {
		IClass helper = classFactory.createClass(IClass.PUBLIC, "generated", cls.getSimpleName() + "$InstantiationHelper", Object.class, InstantiationHelper.class);
		
		IMethod ctor = helper.addMethod(IClass.PUBLIC, Object.class, "__newInstance", Object[].class);
		for (Constructor<?> constructor : cls.getConstructors()) {
			int i = 0;
			ctor.If(ctor.getParameter(0).length().equal(constructor.getParameterTypes().length));
				Value rv = null;
				for (Class<?> type : constructor.getParameterTypes()) {
					Class<?> wrapper = VMConst.getWrapperType(type);
					if (rv == null) {
						rv = ctor.getParameter(0).get(i).instanceOf(wrapper);
						if (type == wrapper) {
							rv = rv.or(ctor.getParameter(0).get(i).isNull());
						}
					} else {
						if (type == wrapper) {
							rv = rv.and(ctor.getParameter(0).get(i).instanceOf(wrapper).or(ctor.getParameter(0).get(i).isNull()));
						} else {
							rv = rv.and(ctor.getParameter(0).get(i).instanceOf(wrapper));
						}
					}
					i++;
				}
				if (rv == null) {
					ctor.Return(ctor.New(cls));
				} else {
					ctor.If(rv);
						i = 0;
						Value[] args = new Value[constructor.getParameterTypes().length];
						for (Class<?> type : constructor.getParameterTypes()) {
							args[i] = ctor.getParameter(0).get(i++).cast(type);
						}
						ctor.Return(ctor.New(cls, (Object[])args));
					ctor.End();
				}
			ctor.End();
		}
		ctor.Return(null);
		ctor.End();
		return helper.build();
	}
	
	/**
	 * Returns the underlaying ClassFactory
	 * @return underlaying ClassFactory
	 */
	public ClassFactory getClassFactory() {
		return classFactory;
	}
	
	/**
	 * Sets the underlaying ClassFactory
	 * @param classFactory underlaying ClassFactory
	 */
	public void setClassFactory(ClassFactory classFactory) {
		this.classFactory = classFactory;
	}
	
	/**
	 * Returns a list of additional interfaces, which should be implemented by every sub class.
	 * @return additional interfaces
	 */
	public Class<?>[] getInterfaces() {
		return interfaces;
	}
	
	/**
	 * Returns a list of additional interfaces, which should be implemented by every sub class.
	 * @param interfaces additional interfaces
	 */
	public void setInterfaces(Class<?>[] interfaces) {
		this.interfaces = interfaces;
	}
	
	/**
	 * Returns additional meta-data.
	 * @return additional meta-data
	 */
	public Map<String, Object> getMetadata() {
		return metadata;
	}
	
	/**
	 * Sets additional meta-data.
	 * @param metadata additional meta-data
	 */
	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}
	
	/**
	 * Returns the class name suffix
	 * @return class name suffix
	 */
	public String getSuffix() {
		return suffix;
	}
	
	/**
	 * Sets the class name suffix
	 * @param suffix class name suffix
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
}
