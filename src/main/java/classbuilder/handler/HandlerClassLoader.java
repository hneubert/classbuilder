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

import classbuilder.BuilderException;
import classbuilder.util.SimpleClassLoader;

/**
 * The HandlerClassLoader generates and loads automatically sub classes.
 */
public class HandlerClassLoader extends SimpleClassLoader {
	
	private ObjectFactory objectFactory;
	
	/**
	 * Creates a new HandlerClassLoader.
	 * @param objectFactory an ObjectFactory
	 */
	public HandlerClassLoader(ObjectFactory objectFactory) {
		super();
		this.objectFactory = objectFactory;
	}
	
	/**
	 * Creates a new HandlerClassLoader.
	 * @param parent the parent class loader
	 * @param objectFactory an ObjectFactory
	 */
	public HandlerClassLoader(ClassLoader parent, ObjectFactory objectFactory) {
		super(parent);
		this.objectFactory = objectFactory;
	}
	
	/**
	 * Creates a sub class.
	 * @param name class name
	 * @return generierte a sub class
	 */
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		int pos = name.lastIndexOf('$');
		if (pos != -1) {
			String className = name.substring(0, pos);
			Class<?> superclass = null;
			try {
				superclass = super.loadClass(className);
			} catch (ClassNotFoundException e) {
				// inner class hack
				pos = className.lastIndexOf('.');
				String className2 = className.substring(0, pos) + "$" + className.substring(pos + 1);
				try {
					superclass = super.loadClass(className2);
				} catch (Exception e2) {
					throw new ClassNotFoundException(className);
				}
			}
			try {
				return objectFactory.getSubclass(superclass, null, null);
			} catch (BuilderException e) {
				throw new ClassNotFoundException(name, e);
			} catch (HandlerException e) {
				throw new ClassNotFoundException(name, e);
			}
		}
		throw new ClassNotFoundException(name);
	}
}
