package classbuilder.test.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

import classbuilder.BuilderException;
import classbuilder.handler.AbstractMethodHandler;
import classbuilder.handler.Handler;
import classbuilder.handler.HandlerClassLoader;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;
import classbuilder.handler.ObjectFactory;

public class FactoryTestCase {
	
	private static ArrayList<String> metadata = new ArrayList<String>();
	
	public static class TestHandler extends AbstractMethodHandler {
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			for (Entry<String, Object> entry : context.getMetadata().entrySet()) {
				metadata.add(entry.getKey() + "." + entry.getValue());
			}
			Return();
		}
	}
	
	@Handler(TestHandler.class)
	public static interface TestInterface {
		public void foo();
	}
	
	@Handler(TestHandler.class)
	public static abstract class ConstructorTest {
		private Object value;
		
		public ConstructorTest(String value) {
			this.value = value;
		}
		
		public ConstructorTest(int value) {
			this.value = value;
		}
		
		public ConstructorTest(String value, Integer i) {
			this.value = value;
		}
		
		public ConstructorTest(Integer i, String value) {
			this.value = value;
		}
		
		public Object getValue() {
			return value;
		}
		
		public abstract void foo();
	}
	
	@Test
	public void interfacesTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		factory.setInterfaces(new Class<?>[] {Serializable.class, Closeable.class});
		Class<?>[] interfaces = (Class<?>[])factory.getInterfaces();
		Assert.assertArrayEquals(new Class<?>[] {Serializable.class, Closeable.class}, interfaces);
		
		Object obj = factory.create(TestInterface.class);
		Assert.assertNotNull(obj);
		Assert.assertTrue(obj instanceof TestInterface);
		Assert.assertTrue(obj instanceof Serializable);
		Assert.assertTrue(obj instanceof Closeable);
	}
	
	@Test
	public void getSubclassTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		Class<?> subclass = factory.getSubclass(TestInterface.class, null, null);
		Assert.assertNotNull(subclass);
		Assert.assertTrue(TestInterface.class.isAssignableFrom(subclass));
		
		subclass = factory.getSubclass(TestInterface.class);
		Assert.assertNotNull(subclass);
		Assert.assertTrue(TestInterface.class.isAssignableFrom(subclass));
	}
	
	@Test
	public void createTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		TestInterface obj = factory.create(TestInterface.class);
		Assert.assertNotNull(obj);
		Assert.assertTrue(obj instanceof TestInterface);
		
		TestInterface obj2 = factory.create(TestInterface.class);
		Assert.assertNotNull(obj2);
		
		Assert.assertNotEquals(obj, obj2);
	}
	
	@Test
	public void constructorTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		ConstructorTest obj = factory.create(ConstructorTest.class, "A");
		Assert.assertNotNull(obj);
		Assert.assertEquals("A", obj.getValue());

		obj = factory.create(ConstructorTest.class, (Object)null);
		Assert.assertNotNull(obj);
		Assert.assertEquals(null, obj.getValue());
		
		ConstructorTest obj2 = factory.create(ConstructorTest.class, 1);
		Assert.assertNotNull(obj2);
		Assert.assertEquals(1, obj2.getValue());
		
		factory.create(ConstructorTest.class, 1, "A");
		factory.create(ConstructorTest.class, 1, null);
		factory.create(ConstructorTest.class, null, "A");
		factory.create(ConstructorTest.class, "A", 1);
		factory.create(ConstructorTest.class, "A", null);
		factory.create(ConstructorTest.class, null, 1);
		
	}
	
	@Handler(TestHandler.class)
	public static abstract class SerialisationTest implements Serializable {
		protected static final long serialVersionUID = 1L;
		public int i = 42;
		public abstract void foo();
	}
	
	public byte[] prepareSerialisationTest() throws FileNotFoundException, IOException, BuilderException, HandlerException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bo);
		ObjectFactory factory = new ObjectFactory();
		factory.setSuffix("Test"); // the full class name must be equal
		SerialisationTest test = factory.create(SerialisationTest.class);
		out.writeObject(test);
		out.close();
		return bo.toByteArray();
	}
	
	public static class MyObjectInputStream extends ObjectInputStream {
		public MyObjectInputStream(InputStream in) throws IOException {
			super(in);
		}
		protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			return Class.forName(desc.getName(), false, Thread.currentThread().getContextClassLoader());
		}
	}
	
	@Test
	public void serialisationTest() throws FileNotFoundException, IOException, ClassNotFoundException, BuilderException, HandlerException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		ObjectFactory factory = new ObjectFactory();
		factory.setSuffix("Test"); // the full class name must be equal
		ClassLoader classLoader = new HandlerClassLoader(factory);
		Thread.currentThread().setContextClassLoader(classLoader);
		
		ObjectInputStream in = new MyObjectInputStream(new ByteArrayInputStream(prepareSerialisationTest()));
		SerialisationTest test = (SerialisationTest)in.readObject();
		Assert.assertEquals(42, test.i);
		test.foo();
		in.close();
		
		Thread.currentThread().setContextClassLoader(cl);
	}
	
	@Test
	public void metaDataTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		factory.getMetadata().put("a", "1");
		factory.getMetadata().put("b", "2");
		
		HashMap<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("b", "3");
		metadata.put("c", "4");
		factory.getSubclass(ConstructorTest.class, null, metadata);
		
		Assert.assertTrue(FactoryTestCase.metadata.contains("b.3"));
		Assert.assertTrue(FactoryTestCase.metadata.contains("c.4"));
	}
	
}
