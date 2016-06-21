package classbuilder.test.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import classbuilder.BuilderException;
import classbuilder.handler.AbstractClassHandler;
import classbuilder.handler.Handler;
import classbuilder.handler.HandlerContext;
import classbuilder.handler.HandlerException;
import classbuilder.handler.ObjectFactory;
import classbuilder.handler.impl.DefaultHandlerContext;

public class HandlerContextTestCase {
	
	public static class TestHandler extends AbstractClassHandler {
		@Override
		public void handle(HandlerContext context) throws BuilderException, HandlerException {
			Assert.assertEquals(ClassHandlerTest.class, context.getAnnotatedElement());
			Assert.assertEquals(MyClassHandler.class, context.getAnnotation().annotationType());
			Assert.assertEquals(TestHandler.class, context.getHandler());
			Assert.assertNotNull(context.getMetadata());
			Assert.assertEquals("value", context.getMetadata().get("key"));
			Assert.assertEquals(5, context.getPriority());
			Assert.assertNotNull(context.getSubclass());
		}
	}
	
	@Handler(value=TestHandler.class, priority=5)
	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyClassHandler {
		
	};
	
	@MyClassHandler
	public interface ClassHandlerTest {
		
	}
	
	@Test
	public void classHandlerTest() throws BuilderException, HandlerException {
		ObjectFactory factory = new ObjectFactory();
		
		factory.getMetadata().put("key", "value");
		factory.create(ClassHandlerTest.class);
	}
	
	@Test
	public void handlerContextSortTest() {
		ArrayList<HandlerContext> list = new ArrayList<HandlerContext>();
		list.add(new DefaultHandlerContext(null, null, null, null, 1, null, null));
		list.add(new DefaultHandlerContext(null, null, null, null, 2, null, null));
		
		Collections.sort(list);
		
		for (HandlerContext ctx : list) {
			System.out.println(ctx.getPriority());
		}
	}
	
}
