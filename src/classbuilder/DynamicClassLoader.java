package classbuilder;

import java.net.URL;
import java.security.ProtectionDomain;

/**
 * The DynamicClassLoader interface represents class loader which exports some protected methods.
 */
public interface DynamicClassLoader {
	
	/**
	 * Invoke the defineClass method of the underlying class loader.
	 * @param name name of the class
	 * @param b byte buffer
	 * @param off offset
	 * @param len length
	 * @return the new class object
	 */
	public Class<?> addClass(String name, byte[] b, int off, int len);
	
	/**
	 * Invoke the defineClass method of the underlying class loader.
	 * @param name name of the class
	 * @param b byte buffer
	 * @param off offset
	 * @param len length
	 * @param protectionDomain protection domain
	 * @return the new class object
	 */
	public Class<?> addClass(String name, byte[] b, int off, int len,  ProtectionDomain protectionDomain);
	
	/**
	 * Invoke the definePackage method of the underlying class loader.
	 * @param name
	 * @param specTitle specification title
	 * @param specVersion specification version
	 * @param specVendor specification vendor
	 * @param implTitle implementation title
	 * @param implVersion implementation version
	 * @param implVendor implementation vendor
	 * @param sealBase seal base
	 * @return the new package object
	 */
	public Package addPackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase);
	
	/**
	 * Returns the underlaying class loder.
	 * @return class loder
	 */
	public ClassLoader getClassLoader();
}
