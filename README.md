# Class-Builder

The class builder API can be used for generating new classes at runtime. 
But this is no byte code engineering library for byte code manipulation of existing classes.  
You can use inheritance to extends existing classes.

Features:
* debug support
* source generation
* no file system access required
* fail fast
* fluent API for expression
* handler API for aspect oriented programming
* compatible with future java versions

Usage:
* replacement of reflection
* replacement of interpreters/parsers

Download:
* [classbuilder-1.2.0.jar](https://github.com/hneubert/classbuilder/raw/master/classbuilder-1.2.0.jar)
* [classbuilder-src-1.2.0.jar](https://github.com/hneubert/classbuilder/raw/master/classbuilder-src-1.2.0.jar)
* [classbuilder-doc-1.2.0.zip](https://github.com/hneubert/classbuilder/raw/master/classbuilder-doc-1.2.0.zip)
* [classbuilder-demo-1.2.0.zip](https://github.com/hneubert/classbuilder/raw/master/classbuilder-demo-1.2.0.zip)


## Example

### Step 1: create a method-handler

```java
public class GetterHandler extends AbstractMethodHandler {
	
    // create a generic method implementation
	@Override
	public void handle(HandlerContext context) throws BuilderException, HandlerException {
		String field = Introspector.decapitalize(getName().substring(3));
        
        // return field;
		Return(get(field));
	}
}
```

### Step 2: create an annotation

```java
// connect the annotation with the method-handler
@Handler(GetterHandler.class)
@Target(value=ElementType.METHOD)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Getter {
	
}
```

### Step 3: use it

```java
public class TestBean {
	protected int foo = 42;
	
	@Getter
	public abstract int getFoo();
}

public class TestCase {
	
	public void test() {
		// create an object factory
        ObjectFactory factory = new ObjectFactory();
		
        // create an enhanced TestBean
		TestBean testBean = (TestBean)factory.create(TestBean.class);
		
        // invoke getFoo()
		System.out.println(testBean.getFoo());
	}
}
```

# 1 Class-Builder API

## 1.1 Create new classes

You can use the ClassFactory to create new classes. 
The ClassFactory has different properties:

parameter | description
----------|------------
CLASS_PATH | Path to the destination directory for the .class-files (optional).
SOURCE_PATH | Path to the destination directory for the .class-files (optional). This property must set for debugging.
CLASS_LOADER | The class loader, which is used for loading generated classes (optional). Default value: current thread class loader.

New classes can be created with the ObjectFactory.createClass method.
Possible class members:
* methods
* constructors
* fields
* annotations

Example:

```java
// create a new ClassFactory
ClassFactory classFactory = new ClassFactory();

// create a new class
IClass cls = classFactory.addClass(PUBLIC, "pkg", "Test", Object.class);

// create a new method, which prints 'hello'
IMethod method = cls.addMethod(PUBLIC, "foo");
	method.$(System.class).get("out").invoke("println", "hello");
method.End();

// build the class
Class<?> newClass = cls.build();
```

## 1.2 Create expressions

The interface Value represents expressions. 
Values are constants, variables, fields, method invocations with a return value or by operators connected values.  
You can use the IMethod.$() method, to convert constants to values. 
Many methods have parameters of type Object, which converts constants implicit.

```java
IMethod m = cls.addMethod(PUBLIC, "foo");

// create a new variable
Variable i = m.addVar(int.class); // int i;
Variable j = m.addVar(int.class); // int j

// write and read a variable
i.set(5); // i = 5;
j.set(i); // j = i;

// use operators
i.set(i.add(j)); // i = i + j;

// type cast
Variable l = m.addVar(long.class); // int l;
l.set(5); // l = 5;
i.set(l.cast(int.class)); i = (int)l;

// create new objects
Variable i = m.addVariable(Integer.class); // Integer i;
i.set(m.New(Integer.class, 5)); // i = new Integer(5);

// invoke methods
list.invoke("add", "test"); // list.add("test");

// this reference
m.This().get("field").set(1); // this.field = 1;

// super reference
m.Super().invoke("foo"); // super.foo();

// return values
m.Return(0); // return 0;

// throw an exception
m.Throw(m.New(Exception.class, "error")); // throw new Exception("error");

// read/write fields
Variable out m.addVariable(PrintStream.class); // PrintStream out;
out = $(System.class).get("out"); // out = System.out;
$(System.class).get("out").set(out); // System.out = out;

// use arrays
Variable strings = m.addVaraiable(String[].class); // String[] strings;
strings.set(m.New(String[].class, 5)); // strings = new Strings[3];
strings.get(0).set("hello"); // strings[0] = "hello";
m.$(System.class).get("out").invoke("println", strings.get(0)); // System.out.println(strings[0]);
m.$(System.class).get("out").invoke("println", strings.length()); // System.out.println(strings.length);
```

Operators:

operator | method | description
---------|--------|------------
~/\! | not | logical negation
&amp;/&amp;&amp; | and | logical and
\|/\|\| | or | logical or
^ | xor | logical exclusive or
&lt;&lt; | shl | shift left
&gt;&gt; | shr | shift arithmetic right
&gt;&gt;&gt; | ushr | shift right
\- | neg | arithmetical negation
\+ | add | add
\- | sub | subtract
\* | mul | multiply
/ | div | divide
% | mod | remainder
== | equal | equal
\!= | notEqual | not equal
&lt; | less | less
&gt; | greater | greater
&lt;= | lessEqual | less or equal
&gt;= | greaterEqual | greater or equal
(&lt;type&gt;) | cast | type cast
instanceof | instanceOf | check type

Limitations:
* no implicit operator priority (a.add(1).mul(2) means (a \+ 1) \* 2)
* logical expressions will be completely executed (ex. if (a == null &amp;&amp; a.length &gt;= 0))

## 1.3 Structures
Methods were implemented by the IMethod interfaces, which can be used to create different structures and complex expression by a fluent API.
There exists two kinds of values:
* values (Value): constants, assignable values and by operators connected values. values can be assigned to assignable values.
* assignable values (Assignable): this values are writable like variables, method arguments, fields or array elements.

**Read method parameters:**
```java
IMethod.getParameter(int index)
```

**Create new variables:**
```java
IMethod.addVar(Class<?> type)
```

**if-else block:**
```java
// if (true) {
m.If(true);
    ...
// } else if (false) {
m.ElseIf(false);
    ...
// } else {
m.Else();
    ...
// }
m.End();
```

**while loop:**
```java
// while (true) {
m.While();
    ...
// }
m.End();
```

**for-each loop:**
```java
// for (Object e : list) {
Variable e = m.ForEach(list);
    ...
// }
m.End();
````

**try-catch block:**
```java
// try {
m.Try()
    ...
// } catch (Exception e) {
Variable e = m.Catch(Exception.class)
    ...
// }
m.End()
```


# 2 Handler API

The handler API is for aspect oriented programming. 
The ObjectFactory creates new classes and objects by handling annotations.

Different handler types:
* class handler: implements different class members
* constructor handler: implements constructors
* method handler: implements methods
* proxy handler: implements method wrappers

## 2.1 The ObjectFactory

The ObjectFactory creates sub classes by handling annotations at super classes and interfaces. 
This annotations are user-defined and triggers the different handlers, which are associated with the annotations.

Example:
```java
@Handler(MyHandler.class)
public @interface MyHandlerAnnotation {

}
```

This annotations are annotated with the associated handler and can used at types (super class or interfaces), constructors, methods and fields.

Example for a super class (primary type):
```java
public abstract class MyType {
	@MyHandlerAnnotation
	public abstract void foo();
}
```

Instantiation:
```java
// create a new ObjectFactory
ObjectFactory factory = new ObjectFactory();

// create a new object of the extended MyType class
MyType obj = factory.create(MyType.class);
```

## 2.2 Class handlers

Class handler can used to add constructors, methods, fields and annotations. 
Each class handler will be executed only one times.

## 2.3 Constructor handlers

Constructor handlers implements constructors. 
They can be annotated ad super constructors or must implement the MethodSelector interface.
If there is no constructor handler present, an empty constructor is implemented implicit.

## 2.4 Method handlers

Method handlers implements methods. 
If a method is annotated, then this method is implemented. 
If the super class or an interface is annotated, then all abstracted methods were implemented. 
Alternatively, you can implement the MethodSelector interface. 
There is only one method handler per method possible.

Example:
```java
public class MyMethodHandler extends AbstractMethodHandler {
	@Override
	public void handle(HandlerContext context) throws BuilderException, HandlerException {
		// print out hello
		$(System.class).get("out").invoke("println", "hello");
	}
}
```

## 2.5 Proxy handlers

Proxy handlers implements method wrappers. 
If a method is annotated, then this method is wrapped. 
If the super class or an interface is annotated, then all abstracted methods were wrapped. 
Alternatively, you can implement the MethodSelector interface. 
There is multiple proxy handlers per method possible.

Example:
```java
public class MyProxyHandler extends AbstractProxyHandler {
	@Override
	public void handle(HandlerContext context) throws BuilderException, HandlerException {
		...
		// invoke the next proxy handler or the method handler or the super method
		Variable result = invoke((Object[])getParameters());
		...
		// return a value or null
		Return(result);
	}
}
```
