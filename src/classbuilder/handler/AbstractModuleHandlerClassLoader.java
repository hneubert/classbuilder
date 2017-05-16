package classbuilder.handler;

import classbuilder.IClassLoader;
import classbuilder.util.AbstractModuleClassLoader;

/**
 * The AbstractModuleHandlerClassLoader is a modular handler class loader.
 */
public abstract class AbstractModuleHandlerClassLoader extends AbstractModuleClassLoader {
	
	private ObjectFactory objectFactory;
	
	/**
	 * Creates a new class loader.
	 */
	public AbstractModuleHandlerClassLoader(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}
	
	@Override
	protected IClassLoader createClassLoader(ClassLoader classLoader) {
		return new HandlerClassLoader(classLoader, objectFactory);
	}
}
