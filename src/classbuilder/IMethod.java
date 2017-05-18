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

package classbuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * The IMethod interface represents a method.
 */
public interface IMethod {
	
	/**
	 * Returns the method name.
	 * @return method name
	 */
	public String getName();
	
	/**
	 * Returns the modifiers.
	 * @return modifiers
	 */
	public int getModifiers();
	
	/**
	 * Returns the declaring class.
	 * @return declaring class
	 */
	public IClass getDeclaringClass();
	
	/**
	 * Returns the return type.
	 * @return return type or null
	 */
	public Class<?> getReturnType();
	
	/**
	 * Creates a new variable.
	 * @param type data type
	 * @return a new variable
	 * @throws BuilderTypeException invalid type
	 * @throws BuilderSyntaxException method is already closed
	 */
	public Variable addVar(Class<?> type) throws BuilderSyntaxException, BuilderTypeException;
	
	/**
	 * Returns all parameters.
	 * @return all parameters
	 */
	public Variable[] getParameters();
	
	/**
	 * Returns all parameter types.
	 * @return all parameter types
	 */
	public Class<?>[] getParameterTypes();
	
	/**
	 * Returns a specific parameter.
	 * @param index index of the parameter (starts with 0)
	 * @return a parameter
	 */
	public Variable getParameter(int index);
	
	/**
	 * Returns all variables.
	 * @return all variables
	 */
	public List<Variable> getLocals();
	
	/**
	 * Adds an annotation.
	 * @param type annotation type
	 * @return new annotation
	 * @throws BuilderTypeException invalid annotation type
	 */
	public IAnnotation addAnnotation(Class<?> type) throws BuilderTypeException;
	
	/**
	 * Returns all annotations.
	 * @return all annotations
	 */
	public Collection<IAnnotation> getAnnotations();
	
	/**
	 * Returns true, if the method is closed.
	 * @return true, if the method is closed
	 */
	public boolean isClosed();
	
	/**
	 * Returns true, if the method is a constructor.
	 * @return true, if the method is a constructor
	 */
	public boolean isConstructor();
	
	/**
	 * Creates a new object or array instance.
	 * @param type object or array type
	 * @param args constructor arguments or array size
	 * @return new object or array
	 * @throws BuilderTypeException invalid array or object type
	 * @throws BuilderAccessException no such constructor
	 * @throws BuilderSyntaxException method already closed
	 */
	public RValue New(Class<?> type, Object ...args) throws BuilderSyntaxException, BuilderAccessException, BuilderTypeException;
	
	/**
	 * Throws an exception.
	 * @param exception a r-value of type Throwable
	 * @throws BuilderTypeException exception is no Throwable
	 * @throws BuilderSyntaxException method already closed
	 */
	public void Throw(RValue exception) throws BuilderSyntaxException, BuilderTypeException;
	
	/**
	 * Starts a new if-block.
	 * @param condition a r-value of type boolean or Boolean
	 * @throws BuilderTypeException condition is no boolean or Boolean
	 * @throws BuilderSyntaxException method already closed
	 */
	public void If(RValue condition) throws BuilderSyntaxException, BuilderTypeException;
	
	/**
	 * Closes the current if-block and starts an else-block.
	 * @throws BuilderSyntaxException method is already closed or no if-block present
	 */
	public void Else() throws BuilderSyntaxException;
	
	/**
	 * Closes the current if-block and starts an else-if-block.
	 * @param condition a r-value of type boolean or Boolean
	 * @throws BuilderTypeException condition is no boolean or Boolean
	 * @throws BuilderSyntaxException method already closed
	 */
	public void ElseIf(RValue condition) throws BuilderSyntaxException, BuilderTypeException;
	
	/**
	 * Starts a new for-each-loop.
	 * @param iterable a r-value of type Iterable or an array
	 * @return a variable of type Object or the array element type, which contains the element data
	 * @throws BuilderTypeException iterable is no Iterable or an array
	 * @throws BuilderSyntaxException method already closed
	 */
	public Variable ForEach(RValue iterable) throws BuilderSyntaxException, BuilderTypeException;
	
	/**
	 * Starts a new for-each-loop with explicit type cast.
	 * @param iterable a r-value of type Iterable or an array
	 * @param elementType element type
	 * @return a variable of type 'elementType' or the array element type, which contains the element data
	 * @throws BuilderTypeException iterable is no Iterable or an array
	 * @throws BuilderSyntaxException method already closed
	 */
	public Variable ForEach(RValue iterable, Class<?> elementType) throws BuilderSyntaxException, BuilderTypeException;
	
	/**
	 * Starts a new while-block.
	 * @param condition a r-value of type boolean or Boolean
	 * @throws BuilderSyntaxException method already closed
	 * @throws BuilderTypeException condition is no boolean or Boolean
	 */
	public void While(RValue condition) throws BuilderSyntaxException, BuilderTypeException;
	
	/**
	 * Break the current loop.
	 * @throws BuilderSyntaxException method already closed or no loop present
	 */
	public void Break() throws BuilderSyntaxException;
	
	/**
	 * Continue the current loop.
	 * @throws BuilderSyntaxException method already closed or no loop present
	 */
	public void Continue() throws BuilderSyntaxException;
	
	/**
	 * Starts a new try-block.
	 * @throws BuilderSyntaxException method already closed
	 */
	public void Try() throws BuilderSyntaxException;
	
	/**
	 * Starts a new try-block and prepare it for a finally block.
	 * @throws BuilderSyntaxException method already closed
	 */
	public void TryWithFinally() throws BuilderSyntaxException;
	
	/**
	 * Closes the current try-block and starts a catch-block.
	 * @param exception an exception type
	 * @return a variable, which contains the exception
	 * @throws BuilderTypeException exception is no exception type
	 * @throws BuilderSyntaxException no try block or method already closed
	 */
	public Variable Catch(Class<?> exception) throws BuilderSyntaxException, BuilderTypeException;
	
	/**
	 * Closes the current try or catch-block and starts a finally-block.
	 * @throws BuilderSyntaxException 
	 */
	public void Finally() throws BuilderSyntaxException;
	
	/**
	 * Starts a synchronized-block.
	 * @param object object for synchronization
	 * @throws BuilderSyntaxException method already closed
	 * @throws BuilderTypeException no object type
	 */
	public void Synchronized(RValue object) throws BuilderSyntaxException, BuilderTypeException;
	
	/**
	 * Returns from the method without a return value.
	 * @throws BuilderTypeException return value required
	 * @throws BuilderSyntaxException method already closed
	 */
	public void Return() throws BuilderSyntaxException, BuilderTypeException;
	
	/**
	 * Returns from the method with a return value.
	 * @param value the return value
	 * @throws BuilderTypeException invalid return type or no return type
	 * @throws BuilderSyntaxException method already closed
	 */
	public void Return(Object value) throws BuilderSyntaxException, BuilderTypeException;
	
	/**
	 * Creates a l-value for the given declared field.
	 * @param field a declared field
	 * @return a l-value
	 * @throws BuilderSyntaxException method already closed
	 * @throws BuilderAccessException field not present or not accessible
	 */
	public LValue get(IField field) throws BuilderSyntaxException, BuilderAccessException;
	
	/**
	 * Creates a l-value for the given declared or super field.
	 * @param field a field name of a declared or super field
	 * @return a l-value
	 * @throws BuilderSyntaxException method already closed
	 * @throws BuilderAccessException field not present or not accessible
	 */
	public LValue get(String field) throws BuilderSyntaxException, BuilderAccessException;
	
	/**
	 * Creates a l-value for the given super field.
	 * @param field a super field
	 * @return a l-value
	 * @throws BuilderSyntaxException method already closed
	 * @throws BuilderAccessException field not present or not accessible
	 */
	public LValue get(Field field) throws BuilderSyntaxException, BuilderAccessException;
	
	/**
	 * Creates a r-value from a constant value
	 * <p>
	 * Allowed types:
	 * <ul>
	 * <li>Boolean, boolean
	 * <li>Byte, byte
	 * <li>Short, short
	 * <li>Integer, int
	 * <li>Long, long
	 * <li>Float, float
	 * <li>Double, double
	 * <li>String
	 * <li>Class
	 * <li>null
	 * <li>RValue: does nothing
	 * </ul>
	 * @param value a constant, string, class or null
	 * @return a r-value
	 * @throws BuilderTypeException invalid data type
	 * @throws BuilderSyntaxException method already closed
	 */
	public RValue $(Object value) throws BuilderSyntaxException, BuilderTypeException;
	
	/**
	 * Invokes a declared method.
	 * @param method a declared method
	 * @param args method arguments: constants or r-values
	 * @return return value: a r-value or null
	 * @throws BuilderSyntaxException method already closed
	 * @throws BuilderAccessException unknown or inaccessible method
	 * @throws BuilderTypeException invalid parameter type
	 */
	public RValue invoke(IMethod method, Object ...args) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Invokes a declared or super method.
	 * @param method a declared or super method
	 * @param args method arguments: constants or r-values
	 * @return return value: a r-value or null
	 * @throws BuilderSyntaxException method already closed
	 * @throws BuilderAccessException unknown or inaccessible method
	 * @throws BuilderTypeException invalid parameter type
	 */
	public RValue invoke(String method, Object ...args) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Invokes a super method.
	 * @param method a super method
	 * @param args method arguments: constants or r-values
	 * @return return value: a r-value or null
	 * @throws BuilderSyntaxException method already closed
	 * @throws BuilderAccessException unknown or inaccessible method
	 * @throws BuilderTypeException invalid parameter type
	 */
	public RValue invoke(Method method, Object ...args) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Closes a block or a non abstract method.
	 * @throws BuilderSyntaxException the method is already closed or a try block is opened
	 */
	public void End() throws BuilderSyntaxException;
	
	/**
	 * Returns a 'this' reference.
	 * @return a 'this' reference
	 * @throws BuilderSyntaxException method already closed
	 * @throws BuilderAccessException invocation in a static method
	 */
	public RValue This() throws BuilderSyntaxException, BuilderAccessException;
	
	/**
	 * Returns a 'super' reference.
	 * @return a 'super' reference
	 * @throws BuilderSyntaxException die method already closed
	 * @throws BuilderAccessException invocation in a static method
	 */
	public RValue Super() throws BuilderSyntaxException, BuilderAccessException;
	
}
