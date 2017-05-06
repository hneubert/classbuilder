package classbuilder.util;

import java.net.URL;
import java.security.ProtectionDomain;

import classbuilder.DynamicClassLoader;

/**
 * Special class loader which exports some protected class loader methods.
 */
public class DefaultDynamicClassLoader extends ClassLoader implements DynamicClassLoader {
	
	/**
	 * Creates an new dynamic class loader using the current thread class loader as the parent class loader.
	 */
	public DefaultDynamicClassLoader() {
		super(Thread.currentThread().getContextClassLoader());
	}
	
	/**
	 * Creates an new dynamic class loader.
	 * @param parent parent class loader
	 */
	public DefaultDynamicClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	@Override
	public Class<?> addClass(String name, byte[] b, int off, int len) {
		return super.defineClass(name, b, off, len);
	}
	
	@Override
	public Class<?> addClass(String name, byte[] b, int off, int len,  ProtectionDomain protectionDomain) {
		return super.defineClass(name, b, off, len, protectionDomain);
	}
	
	@Override
	public Package addPackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) {
		return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
	}
	
	@Override
	public ClassLoader getClassLoader() {
		return this;
	}
}
