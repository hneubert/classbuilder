package classbuilder;

/**
 * The IClassLoader interface represents class loader for IClass objects.
 */
public interface IClassLoader {
	
	/**
	 * Invoke the defineClass method of the underlying class loader.
	 * @param cls class
	 * @return the new class object
	 */
	public Class<?> addClass(IClass cls) throws BuilderCompilerException;
}
