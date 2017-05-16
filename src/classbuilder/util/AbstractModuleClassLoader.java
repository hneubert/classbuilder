package classbuilder.util;

import java.util.HashMap;
import java.util.Map;

import classbuilder.BuilderCompilerException;
import classbuilder.IClassLoader;
import classbuilder.IClass;

/**
 * The module class loader supports class loading in modular environments.
 */
public abstract class AbstractModuleClassLoader implements IClassLoader {
	
	private Map<ClassLoader, IClassLoader> classLoaderMap;
	
	/**
	 * Creates a new module class loader.
	 */
	public AbstractModuleClassLoader() {
		classLoaderMap = new HashMap<ClassLoader, IClassLoader>();
	}
	
	@Override
	public Class<?> addClass(IClass cls) throws BuilderCompilerException {
		ClassLoader cl = getClassLoader(cls);
		IClassLoader classLoader = classLoaderMap.get(cl);
		if (cl == null) {
			classLoader = createClassLoader(cl);
			classLoaderMap.put(cl, classLoader);
		}
		return classLoader.addClass(cls);
	}
	
	/**
	 * Returns the specific module class loader for the new class.<br>
	 * The class loader can be identified by package name or super class.
	 */
	protected abstract ClassLoader getClassLoader(IClass cls);
	
	/**
	 * Creates a new IClassLoader instance for the specific module class loader;
	 */
	protected IClassLoader createClassLoader(ClassLoader classLoader) {
		return new SimpleClassLoader(classLoader);
	}
}
