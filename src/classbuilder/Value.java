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

/**
 * The interface represents a value. Values are:
 * <ul>
 * <li>mathematical expressions
 * <li>logical expressions
 * <li>constants
 * <li>variables
 * <li>array-elements
 * <li>fields
 * </ul>
 */
public interface Value {
	/**
	 * Returns the data type.
	 * @return data type
	 */
	public Class<?> getVarType();
	
	/**
	 * Returns a field.
	 * @param name field name
	 * @return an assignable value representing a field
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderAccessException variable not initialized or field not found or inaccessable
	 */
	public Assignable get(String name) throws BuilderSyntaxException, BuilderAccessException;
	
	/**
	 * Returns a field.
	 * @param field a field
	 * @return an assignable value representing a field
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderAccessException variable not initialized or field not found or inaccessable
	 */
	public Assignable get(Field field) throws BuilderSyntaxException, BuilderAccessException;
	
	/**
	 * Returns an array element.
	 * @param index an int value representing the index
	 * @return an assignable value representing an array element
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException no array
	 * @throws BuilderAccessException variable not initialized or field not found or inaccessable
	 */
	public Assignable get(Object index) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Invokes a method.
	 * @param name method name
	 * @param args arguments: constants or values
	 * @return a value representing the return value or null
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderAccessException variable not initialized or method not found or inaccessable
	 * @throws BuilderTypeException invalid argument type
	 */
	public Value invoke(String name, Object ...args) throws BuilderSyntaxException, BuilderAccessException, BuilderTypeException;
	
	/**
	 * Invokes a method.
	 * @param method a method
	 * @param args arguments: constants or values
	 * @return a value representing the return value or null
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderAccessException variable not initialized or field not found or inaccessable
	 * @throws BuilderTypeException invalid argument type
	 */
	public Value invoke(Method method, Object ...args) throws BuilderSyntaxException, BuilderAccessException, BuilderTypeException;
	
	/**
	 * Returns the array length.
	 * @return array length
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException no array
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value length() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Casts a value to another type.
	 * @param type new type
	 * @return the value with a new type
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException invalid type cast
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value cast(Class<?> type) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Adds this value and another value or constant.
	 * @param a another value or constant
	 * @return sum
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non numeric type
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value add(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Substracts this value and another value or constant.
	 * @param a another value or constant
	 * @return difference
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non numeric type
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value sub(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Multiplies this value and another value or constant.
	 * @param a another value or constant
	 * @return product
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non numeric type
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value mul(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Devides this value and another value or constant.
	 * @param a another value or constant
	 * @return quotient
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non numeric type
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value div(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Calculates the remainder from this value and another value or constant.
	 * @param a another value or constant
	 * @return remainder
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non numeric type
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value mod(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Calculates logical or from this value and another value or constant.
	 * @param a another value or constant
	 * @return logical or
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non integral numerical or boolean value
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value and(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Calculates logical and from this value and another value or constant.
	 * @param a another value or constant
	 * @return logical and
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non integral numerical or boolean value
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value or(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Calculates logical exclusive or from this value and another value or constant.
	 * @param a another value or constant
	 * @return logical exclusive or
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non integral numerical or boolean value
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value xor(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Shifts a value n bits arithmetic right.
	 * @param n another value or constant
	 * @return right shifted value
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non integral numerical value
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value shr(Object n) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Shifts a value n bits left.
	 * @param n another value or constant
	 * @return left shifted value
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non integral numerical value
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value shl(Object n) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Shifts a value n bits right.
	 * @param n another value or constant
	 * @return right shifted value
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non integral numerical value
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value ushr(Object n) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Negates this value logical.
	 * @return this value logical negated
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non integral numeric or boolean value
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value not() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Negates this value arithmetical.
	 * @return this value arithmetical negated
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non integral numeric or boolean value
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value neg() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Returns true, if this value is an instance of the given type.
	 * @param a a class type
	 * @return true, if this value is an instance of the given type
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException invalid type
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value instanceOf(Class<?> a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Returns true, if this value is equal to a.
	 * @param a another value or constant
	 * @return true, if this value is equal to a
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException dieser R-Wert und der Parameter sind nicht kompatiebel oder kein numerischer oder boolscher Ausdruck
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value equal(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Returns true, if this value is not equal to a.
	 * @param a another value or constant
	 * @return true, if this value is not equal to a
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException dieser R-Wert und der Parameter sind nicht kompatiebel oder kein numerischer oder boolscher Ausdruck
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value notEqual(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Returns true, if this value is less than a.
	 * @param a another value or constant
	 * @return true, if this value is less than a
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non numerical value
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value less(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Returns true, if this value is greater than a.
	 * @param a another value or constant
	 * @return a true, if this value is greater than a
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non numerical value
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value greater(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Returns true, if this value less than or is equal to a.
	 * @param a another value or constant
	 * @return true, if this value less than or is equal to a
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException non numerical value
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value lessEqual(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Returns true, if this value greater than or is equal to a.
	 * @param a another value or constant
	 * @return true, if this value greater than or is equal to a
	 * @throws BuilderSyntaxException der R-Wert wurde bereits compiliert
	 * @throws BuilderTypeException non numerical value
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value greaterEqual(Object a) throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Returns true, if this value is null.
	 * @return true, if this value is null
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException no object or array
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value isNull() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
	
	/**
	 * Returns true, if this value is not null.
	 * @return true, if this value is not null
	 * @throws BuilderSyntaxException value allready compiled
	 * @throws BuilderTypeException no object or array
	 * @throws BuilderAccessException variable not initialized
	 */
	public Value isNotNull() throws BuilderSyntaxException, BuilderTypeException, BuilderAccessException;
}
