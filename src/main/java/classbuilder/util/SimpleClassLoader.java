package classbuilder.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import classbuilder.BuilderCompilerException;
import classbuilder.BuilderException;
import classbuilder.IClassLoader;
import classbuilder.IClass;

/**
 * Special class loader which exports some protected class loader methods.
 */
public class SimpleClassLoader extends ClassLoader implements IClassLoader {
	
	/**
	 * Creates an new dynamic class loader using the current thread class loader as the parent class loader.
	 */
	public SimpleClassLoader() {
		super(Thread.currentThread().getContextClassLoader());
	}
	
	/**
	 * Creates an new dynamic class loader.
	 * @param parent parent class loader
	 */
	public SimpleClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	@Override
	public Class<?> addClass(IClass cls) throws BuilderCompilerException {
		String classPath = cls.getClassFactory().getClassPath();
		String sourcePath = cls.getClassFactory().getSourcePath();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			cls.write(out);
		} catch (IOException e) {
			throw new BuilderCompilerException(this, e.getMessage(), e);
		} catch (BuilderException e) {
			throw new BuilderCompilerException(this, e.getMessage(), e);
		}
		byte[] buffer = out.toByteArray();
		
		if (classPath != null) {
			Path path = Paths.get(classPath + File.separator + cls.getName().replace('.', File.separatorChar) + ".class");
			
			if (!Files.exists(path.getParent())) {
				try {
					Files.createDirectories(path.getParent());
				} catch (IOException e) {
					throw new BuilderCompilerException(this, e.getMessage(), e);
				}
			}
			
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(path.toFile());
				fos.write(buffer);
				fos.close();
			} catch (Exception e) {
				throw new BuilderCompilerException(this, e.getMessage(), e);
			}
		}
		
		if (sourcePath != null) {
			Path path = Paths.get(sourcePath + File.separator + cls.getName().replace('.', File.separatorChar) + ".java");
			
			if (!Files.exists(path.getParent())) {
				try {
					Files.createDirectories(path.getParent());
				} catch (IOException e) {
					throw new BuilderCompilerException(this, e.getMessage(), e);
				}
			}
			
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(path.toFile());
				cls.writeSource(fos);
				fos.close();
			} catch (Exception e) {
				throw new BuilderCompilerException(this, e.getMessage(), e);
			}
		}
		
		if (cls.getProtectionDomain() == null) {
			return super.defineClass(cls.getName(), out.toByteArray(), 0, out.size());
		} else {
			return super.defineClass(cls.getName(), out.toByteArray(), 0, out.size(), cls.getProtectionDomain());
		}
	}
}
