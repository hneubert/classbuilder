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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import classbuilder.BuilderException;
import classbuilder.ClassFactory;
import classbuilder.handler.HandlerException;
import classbuilder.handler.ObjectFactory;

/**
 * The Generator builds class and java-files at build-time.
 */
public class Generator {
	private ClassFactory classFactory;
	private ClassLoader classLoader;
	private ObjectFactory objectFactory;
	private String filter;
	
	/**
	 * Creates a new Generator object.
	 * @param classpath class-file destination path for java-files
	 * @param sourcepath java-files destination path
	 * @param filter ant style filter expressen
	 */
	public Generator(String classpath, String sourcepath, String filter) {
		if (filter != null) {
			filter = filter.replace('/', '.');
			filter = filter.replace('\\', '.');
			filter = filter.replace(".", "\\.");
			
			filter = filter.replace("**", "?");
			filter = filter.replace('*', '!');
			
			filter = filter.replace("?", ".*");
			filter = filter.replace("!", "[^\\.]*");
		}
		this.filter = filter;
		classLoader = Thread.currentThread().getContextClassLoader();
		
		classFactory = new ClassFactory();
		classFactory.setClassLoader(classLoader);
		classFactory.setClassPath(classpath);
		classFactory.setSourcePath(sourcepath);
		objectFactory = new ObjectFactory();
		objectFactory.setClassFactory(classFactory);
	}
	
	/**
	 * External build-scripts must invoke this main-method.<br>
	 * usage: java -jar cbgen.jar [options] [hadler]<br>
	 *   -f expr  class name based filter<br>
	 *            example: org.classbuilder.**.*Service<br>
	 *   -s dir   directory for generated source files<br>
	 *   -d dir   directory for generated class files<br>
	 * @param args additional arguments
	 * @throws Exception any error
	 */
	public static void main(String[] args) throws Exception {
		String filter = null;
		String source = null;
		String dest = null;
		String classpath = System.getProperty("java.class.path");
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f")) {
				filter = args[++i];
			} else if (args[i].equals("-s")) {
				source = args[++i];
			} else if (args[i].equals("-d")) {
				dest = args[++i];
			} else if (args[i].equals("-h")) {
				printHelp();
			}
		}
		
		new Generator(dest, source, filter).scanClasspath(classpath.split(File.pathSeparator));
	}
	
	private static void printHelp() {
		System.out.println("usage: java -jar cbgen.jar [options] [hadler]");
		System.out.println("  -f expr  class name based filter");
		System.out.println("           example: org.classbuilder.**.*Service");
		System.out.println("  -s dir   directory for generated source files");
		System.out.println("  -d dir   directory for generated class files");
		System.out.println("examples:");
		System.out.println("java -jar cbgen.jar project/bin");
		System.out.println("java -jar cbgen.jar project/bin/Foo.class project/bin/Bar.class");
		System.out.println("java -jar cbgen.jar project/project.jar");
	}
	
	private void scanClasspath(String[] pathList) throws IOException {
		for (String path : pathList) {
			File file = new File(path);
			if (!file.exists()) {
				// error
			} else if (file.isDirectory()) {
				scanDirectory("", file);
			} else if (file.isFile() && path.endsWith(".jar")) {
				JarFile jar = new JarFile(file);
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					loadClass(entry.getName());
				}
				jar.close();
			} else {
				// error
			}
		}
	}
	
	private void scanDirectory(String pkg, File dir) {
		for (File file : dir.listFiles()) {
			String name = pkg;
			if (pkg == null || pkg.length() == 0) {
				name = file.getName();
			} else {
				name += "." + file.getName();
			}
			if (file.isDirectory()) {
				scanDirectory(name, file);
			} else {
				loadClass(name);
			}
		}
	}
	
	private void loadClass(String classname) {
		if (!classname.endsWith(".class")) {
			return;
		}
		
		classname = classname.substring(0, classname.length() - 6);
		classname = classname.replace('/', '.');
		classname = classname.replace('$', '.');
		if (filter == null || classname.matches(filter)) {
			try {
				Class<?> cls = classLoader.loadClass(classname);
				objectFactory.getSubclass(cls, null, null);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (BuilderException e) {
				e.printStackTrace();
			} catch (HandlerException e) {
				e.printStackTrace();
			}
		}
	}
}
