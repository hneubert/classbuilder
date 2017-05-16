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

import java.security.ProtectionDomain;

import classbuilder.impl.DefaultClass;
import classbuilder.impl.VMConst;
import classbuilder.util.SimpleClassLoader;

/**
 * The ClassFactory generates new classes.
 */
public class ClassFactory {
	
	private String classPath = null;
	private String sourcePath = null;
	private IClassLoader classLoader = null;
	private ProtectionDomain protectionDomain = null;
	private short debug = 0;
	
	/**
	 * Sole constructor.
	 */
	public ClassFactory() {
		
	}
	
	/**
	 * Creates an new class.
	 * @param modifier modifiers
	 * @param pkg package name
	 * @param name class name
	 * @param superClass super class or null for interfaces
	 * @param intf additional interfaces
	 * @return eine new IClass object
	 * @throws BuilderModifierException invalid modifier
	 * @throws BuilderNameException der invalid class or package name
	 * @throws BuilderTypeException invalid super class or interface type
	 */
	public IClass createClass(int modifier, String pkg, String name, Class<?> superClass, Class<?> ...intf) throws BuilderModifierException, BuilderNameException, BuilderTypeException {
		return new DefaultClass(this, modifier | debug, pkg, name, superClass, intf);
	}
	
	/**
	 * Returns class path for generated .class-files.
	 * @return class path for generated .class-files
	 */
	public String getClassPath() {
		return classPath;
	}
	
	/**
	 * Sets class path for generated .class-files.
	 * @param classPath class path for generated .class-files
	 */
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
	
	/**
	 * Returns source path for generated .java-files.
	 * @return source path for generated .java-files
	 */
	public String getSourcePath() {
		return sourcePath;
	}
	
	/**
	 * Sets source path for generated .java-files.
	 * This property must be set for debugging.
	 * @param sourcePath source path for generated .java-files
	 */
	public void setSourcePath(String sourcePath) {
		if (sourcePath != null) {
			debug = VMConst.DEBUG;
		} else {
			debug = 0;
		}
		this.sourcePath = sourcePath;
	}
	
	/**
	 * Returns the class loader
	 * @return class loader
	 */
	public IClassLoader getClassLoader() {
		if (classLoader == null) {
			classLoader = new SimpleClassLoader();
		}
		return classLoader;
	}
	
	/**
	 * Sets the class loader.
	 * The default class loader is a sub class loader of the current thread class loader.
	 * @param classLoader class loader
	 */
	public void setClassLoader(IClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	/**
	 * Returns the default protection domain.
	 * @return protection domain or null
	 */
	public ProtectionDomain getProtectionDomain() {
		return protectionDomain;
	}
	
	/**
	 * Sets the default protection domain.
	 * @param protectionDomain protection domain or null
	 */
	public void setProtectionDomain(ProtectionDomain protectionDomain) {
		this.protectionDomain = protectionDomain;
	}
}
