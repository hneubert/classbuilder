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

package classbuilder.impl;

import static classbuilder.IClass.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import classbuilder.IClass;

public class VMConst {
	public final static short DEBUG = 0x2000;
	
	public final static short FINAL = 0x0010;
	public final static short INTERFACE = 0x0200;
	public final static short ABSTRACT = 0x0400;
	public final static short ENUM = 0x4000;
	
	public static final int OBJECT = -1;
	public static final int BOOLEAN = 1;
	public static final int BYTE = 2;
	public static final int SHORT = 3;
	public static final int CHAR = 4;
	public static final int INT = 5;
	public static final int LONG = 6;
	public static final int FLOAT = 7;
	public static final int DOUBLE = 8;
	
	private static final int[][] ASSIGNABLE = {{}, // source -> dest
		//  BOOL BYTE SHORT CHAR INT LONG FLOAT DOUBLE
		{0,  0,  -1,  -1,   -1,  -1, -1,  -1,   -1}, // BOOLEAN
		{0, -1,   0,   1,   -1,   3,  4,   5,    6}, // BYTE
		{0, -1,  -1,   0,   -1,   2,  3,   4,    5}, // SHORT
		{0, -1,  -1,  -1,    0,   1,  2,   3,    4}, // CHAR
		{0, -1,  -1,  -1,   -1,   0,  1,   2,    3}, // INT
		{0, -1,  -1,  -1,   -1,  -1,  0,   1,    2}, // LONG
		{0, -1,  -1,  -1,   -1,  -1, -1,   0,    1}, // FLOAT
		{0, -1,  -1,  -1,   -1,  -1, -1,  -1,    0}  // DOUBLE
	};
	
	public static String getModifier(int modifier) {
		String s = "";
		if ((modifier & PUBLIC) != 0) s+= "public ";
		if ((modifier & PRIVATE) != 0) s+= "private ";
		if ((modifier & PROTECTED) != 0) s+= "protected ";
		if ((modifier & STATIC) != 0) s+= "static ";
		if ((modifier & FINAL) != 0) s+= "final ";
		//if ((modifier & SUPER) != 0) s+= "super ";
		if ((modifier & VOLATILE) != 0) s+= "volatile ";
		if ((modifier & TRANSIENT) != 0) s+= "transient ";
		if ((modifier & NATIVE) != 0) s+= "native ";
		if ((modifier & INTERFACE) != 0) s+= "interface ";
		if ((modifier & ABSTRACT) != 0) s+= "abstract ";
		if ((modifier & STRICT) != 0) s+= "strict ";
		return s;
	}
	
	public static String getTypeName(IClass component, Class<?> cls) {
		if (cls == null) return "void";
		if (cls == boolean.class) return "boolean";
		if (cls == byte.class) return "byte";
		if (cls == short.class) return "short";
		if (cls == int.class) return "int";
		if (cls == long.class) return "long";
		if (cls == float.class) return "float";
		if (cls == double.class) return "double";
		if (cls == char.class) return "char";
		if (cls == IClass.CURRENT_CLASS_TYPE) {
			if (component != null) return component.getSimpleName();
			return "<current type>";
		}
		if (cls == IClass.CURRENT_CLASS_ARRAY_TYPE) {
			if (component != null) return component.getSimpleName() + "[]";
			return "<current type>[]";
		}
		String name = cls.getName();
		String array = "";
		while (name.startsWith("[")) {
			name = name.substring(1);
			array += "[]";
		}
		if (name.startsWith("L")) name = name.substring(1, name.length() - 1);
		if (name.equals("Z")) name = "boolean";
		if (name.equals("B")) name = "byte";
		if (name.equals("C")) name = "char";
		if (name.equals("D")) name = "double";
		if (name.equals("F")) name = "float";
		if (name.equals("I")) name = "int";
		if (name.equals("J")) name = "long";
		if (name.equals("S")) name = "short";
		return name + array;
	}
	
	public static String getConst(Object value) {
		String s = value.toString();
		if (value instanceof String) {
			s = "\"" + s + "\"";
		} else if (value instanceof Long) {
			s = s + "L";
		} else if (value instanceof Float) {
			s = s + "f";
		}
		return s;
	}
	
	public static String getClassName(Class<?> type) {
		Class<?> cls = (Class<?>)type;
		if (cls == null || cls.isPrimitive()) {
			if (cls == boolean.class) return "Z";
			if (cls == byte.class) return "B";
			if (cls == char.class) return "C";
			if (cls == double.class) return "D";
			if (cls == float.class) return "F";
			if (cls == int.class) return "I";
			if (cls == long.class) return "J";
			if (cls == short.class) return "S"; 
			return "V"; 
		} else if (cls.isArray()) {
			return cls.getName().replace('.', '/');
		} else {
			return "L" + cls.getName().replace('.', '/') + ";";
		}
	}
	
	public static String getName(String name) {
		int i = 0;
		char c = name.charAt(i);
		while (c == '[') {
			c = name.charAt(i++);
		}
		if (c == 'L') {
			return name.substring(0, name.indexOf(';') + 1);
		} else if (i != 0) {
			return name.substring(0, i + 1);
		} else {
			return name.substring(0, 1);
		}
	}
	
	public static int isAssignable(IClass cls, Class<?> source, Class<?> dest) {
		//if (source == null || dest == null) return -1;
		if (dest == null) return -1;
		if (source == null) {
			if (dest.isPrimitive()) return -1;
			return 1;
		}
		if (source == dest) return 0;
		if (dest == Object.class) return 9;
		
		int src = getLevel(source);
		int dst = getLevel(dest);
		if (src != -1 && src == dst) return 0;
		
		if (dest.isPrimitive() && source.isPrimitive()) {
			return ASSIGNABLE[src][dst];
		}
		if (source == IClass.CURRENT_CLASS_TYPE) {
			if (dest.isAssignableFrom(cls.getSuperclass())) return 1;
			for (Class<?> intf : cls.getInterfaces()) {
				if (dest.isAssignableFrom(intf)) return 1;
			}
		} else {
			if (dest.isAssignableFrom(source)) return 1;
		}
		return -1;
	}
	
	public static int getLevel(Class<?> cls) {
		if (cls == boolean.class || cls == Boolean.class) return BOOLEAN;
		if (cls == byte.class || cls == Byte.class) return BYTE;
		if (cls == short.class || cls == Short.class) return SHORT;
		if (cls == char.class || cls == Character.class) return CHAR;
		if (cls == int.class || cls == Integer.class) return INT;
		if (cls == long.class || cls == Long.class) return LONG;
		if (cls == float.class || cls == Float.class) return FLOAT;
		if (cls == double.class || cls == Double.class) return DOUBLE;
		return OBJECT;
	}
	
	public static Class<?> getPrimitiveType(Class<?> cls) {
		if (cls == Boolean.class) return boolean.class;
		if (cls == Byte.class) return byte.class;
		if (cls == Short.class) return short.class;
		if (cls == Character.class) return char.class;
		if (cls == Integer.class) return int.class;
		if (cls == Long.class) return long.class;
		if (cls == Float.class) return float.class;
		if (cls == Double.class) return double.class;
		return cls;
	}
	
	public static Class<?> getWrapperType(Class<?> cls) {
		if (cls == boolean.class) return Boolean.class;
		if (cls == byte.class) return Byte.class;
		if (cls == short.class) return Short.class;
		if (cls == char.class) return Character.class;
		if (cls == int.class) return Integer.class;
		if (cls == long.class) return Long.class;
		if (cls == float.class) return Float.class;
		if (cls == double.class) return Double.class;
		return cls;
	}
	
	public static int testFucntion(IClass cls, Class<?>[] source, Class<?>[] dest) {
		if (source.length != dest.length) return -1;
		int q = 0;
		
		for (int i = 0; i < source.length; i++) {
			int level = isAssignable(cls, source[i], dest[i]);
			if (level == -1) {
				return -1;
			}
			q += level;
		}
		
		return q;
	}
	
	public static boolean isAccessable(Method method, String pkg, boolean protectedAccess) {
		int modifiers = method.getModifiers();
		if ((modifiers & IClass.PRIVATE) != 0) return false;
		return	(modifiers & IClass.PUBLIC) != 0 || 
				((modifiers & IClass.PROTECTED) != 0 && protectedAccess) || 
				((modifiers & IClass.PROTECTED) == 0 && method.getDeclaringClass().getPackage().getName().equals(pkg));
	}
	
	public static boolean isAccessable(Field field, String pkg, boolean protectedAccess) {
		int modifiers = field.getModifiers();
		if ((modifiers & IClass.PRIVATE) != 0) return false;
		return	(modifiers & IClass.PUBLIC) != 0 || 
				((modifiers & IClass.PROTECTED) != 0 && protectedAccess) || 
				((modifiers & IClass.PROTECTED) == 0 && field.getDeclaringClass().getPackage().getName().equals(pkg));
	}
	
	//public static final byte NOP = (byte)0x00;
	public static final byte ACONST_NULL = (byte)0x01;
	public static final byte ICONST_M1 = (byte)0x02;
	public static final byte ICONST_0 = (byte)0x03;
	public static final byte ICONST_1 = (byte)0x04;
	public static final byte ICONST_2 = (byte)0x05;
	public static final byte ICONST_3 = (byte)0x06;
	public static final byte ICONST_4 = (byte)0x07;
	public static final byte ICONST_5 = (byte)0x08;
	public static final byte LCONST_0 = (byte)0x09;
	public static final byte LCONST_1 = (byte)0x0a;
	public static final byte FCONST_0 = (byte)0x0b;
	public static final byte FCONST_1 = (byte)0x0c;
	public static final byte FCONST_2 = (byte)0x0d;
	public static final byte DCONST_0 = (byte)0x0e;
	public static final byte DCONST_1 = (byte)0x0f;
	public static final byte BIPUSH = (byte)0x10;
	public static final byte SIPUSH = (byte)0x11;
	public static final byte LDC = (byte)0x12;
	public static final byte LDC_W = (byte)0x13;
	public static final byte LDC2_W = (byte)0x14;
	public static final byte ILOAD = (byte)0x15;
	public static final byte LLOAD = (byte)0x16;
	public static final byte FLOAD = (byte)0x17;
	public static final byte DLOAD = (byte)0x18;
	public static final byte ALOAD = (byte)0x19;
	public static final byte ILOAD_0 = (byte)0x1a;
	public static final byte ILOAD_1 = (byte)0x1b;
	public static final byte ILOAD_2 = (byte)0x1c;
	public static final byte ILOAD_3 = (byte)0x1d;
	public static final byte LLOAD_0 = (byte)0x1e;
	public static final byte LLOAD_1 = (byte)0x1f;
	public static final byte LLOAD_2 = (byte)0x20;
	public static final byte LLOAD_3 = (byte)0x21;
	public static final byte FLOAD_0 = (byte)0x22;
	public static final byte FLOAD_1 = (byte)0x23;
	public static final byte FLOAD_2 = (byte)0x24;
	public static final byte FLOAD_3 = (byte)0x25;
	public static final byte DLOAD_0 = (byte)0x26;
	public static final byte DLOAD_1 = (byte)0x27;
	public static final byte DLOAD_2 = (byte)0x28;
	public static final byte DLOAD_3 = (byte)0x29;
	public static final byte ALOAD_0 = (byte)0x2a; // this
	public static final byte ALOAD_1 = (byte)0x2b;
	public static final byte ALOAD_2 = (byte)0x2c;
	public static final byte ALOAD_3 = (byte)0x2d;
	public static final byte IALOAD = (byte)0x2e;
	public static final byte LALOAD = (byte)0x2f;
	public static final byte FALOAD = (byte)0x30;
	public static final byte DALOAD = (byte)0x31;
	public static final byte AALOAD = (byte)0x32;
	public static final byte BALOAD = (byte)0x33;
	public static final byte CALOAD = (byte)0x34;
	public static final byte SALOAD = (byte)0x35;
	public static final byte ISTORE = (byte)0x36;
	public static final byte LSTORE = (byte)0x37;
	public static final byte FSTORE = (byte)0x38;
	public static final byte DSTORE = (byte)0x39;
	public static final byte ASTORE = (byte)0x3a;
	public static final byte ISTORE_0 = (byte)0x3b;
	public static final byte ISTORE_1 = (byte)0x3c;
	public static final byte ISTORE_2 = (byte)0x3d;
	public static final byte ISTORE_3 = (byte)0x3e;
	public static final byte LSTORE_0 = (byte)0x3f;
	public static final byte LSTORE_1 = (byte)0x40;
	public static final byte LSTORE_2 = (byte)0x41;
	public static final byte LSTORE_3 = (byte)0x42;
	public static final byte FSTORE_0 = (byte)0x43;
	public static final byte FSTORE_1 = (byte)0x44;
	public static final byte FSTORE_2 = (byte)0x45;
	public static final byte FSTORE_3 = (byte)0x46;
	public static final byte DSTORE_0 = (byte)0x47;
	public static final byte DSTORE_1 = (byte)0x48;
	public static final byte DSTORE_2 = (byte)0x49;
	public static final byte DSTORE_3 = (byte)0x4a;
	public static final byte ASTORE_0 = (byte)0x4b;
	public static final byte ASTORE_1 = (byte)0x4c;
	public static final byte ASTORE_2 = (byte)0x4d;
	public static final byte ASTORE_3 = (byte)0x4e;
	public static final byte IASTORE = (byte)0x4f;
	public static final byte LASTORE = (byte)0x50;
	public static final byte FASTORE = (byte)0x51;
	public static final byte DASTORE = (byte)0x52;
	public static final byte AASTORE = (byte)0x53;
	public static final byte BASTORE = (byte)0x54;
	public static final byte CASTORE = (byte)0x55;
	public static final byte SASTORE = (byte)0x56;
	public static final byte POP = (byte)0x57;
	public static final byte POP2 = (byte)0x58;
	public static final byte DUP = (byte)0x59;
	//public static final byte DUP_X1 = (byte)0x5a;
	//public static final byte DUP_X2 = (byte)0x5b;
	//public static final byte DUP2 = (byte)0x5c;
	//public static final byte DUP2_X1 = (byte)0x5d;
	//public static final byte DUP2_X2 = (byte)0x5e;
	//public static final byte SWAP = (byte)0x5f;
	public static final byte IADD = (byte)0x60;
	public static final byte LADD = (byte)0x61;
	public static final byte FADD = (byte)0x62;
	public static final byte DADD = (byte)0x63;
	public static final byte ISUB = (byte)0x64;
	public static final byte LSUB = (byte)0x65;
	public static final byte FSUB = (byte)0x66;
	public static final byte DSUB = (byte)0x67;
	public static final byte IMUL = (byte)0x68;
	public static final byte LMUL = (byte)0x69;
	public static final byte FMUL = (byte)0x6a;
	public static final byte DMUL = (byte)0x6b;
	public static final byte IDIV = (byte)0x6c;
	public static final byte LDIV = (byte)0x6d;
	public static final byte FDIV = (byte)0x6e;
	public static final byte DDIV = (byte)0x6f;
	public static final byte IREM = (byte)0x70;
	public static final byte LREM = (byte)0x71;
	public static final byte FREM = (byte)0x72;
	public static final byte DREM = (byte)0x73;
	public static final byte INEG = (byte)0x74;
	public static final byte LNEG = (byte)0x75;
	public static final byte FNEG = (byte)0x76;
	public static final byte DNEG = (byte)0x77;
	public static final byte ISHL = (byte)0x78;
	public static final byte LSHL = (byte)0x79;
	public static final byte ISHR = (byte)0x7a;
	public static final byte LSHR = (byte)0x7b;
	public static final byte IUSHR = (byte)0x7c;
	public static final byte LUSHR = (byte)0x7d;
	public static final byte IAND = (byte)0x7e;
	public static final byte LAND = (byte)0x7f;
	public static final byte IOR = (byte)0x80;
	public static final byte LOR = (byte)0x81;
	public static final byte IXOR = (byte)0x82;
	public static final byte LXOR = (byte)0x83;
	public static final byte IINC = (byte)0x84;
	public static final byte I2L = (byte)0x85;
	public static final byte I2F = (byte)0x86;
	public static final byte I2D = (byte)0x87;
	public static final byte L2I = (byte)0x88;
	public static final byte L2F = (byte)0x89;
	public static final byte L2D = (byte)0x8a;
	public static final byte F2I = (byte)0x8b;
	public static final byte F2L = (byte)0x8c;
	public static final byte F2D = (byte)0x8d;
	public static final byte D2I = (byte)0x8e;
	public static final byte D2L = (byte)0x8f;
	public static final byte D2F = (byte)0x90;
	public static final byte I2B = (byte)0x91;
	public static final byte I2C = (byte)0x92;
	public static final byte I2S = (byte)0x93;
	public static final byte LCMP = (byte)0x94;
	public static final byte FCMPL = (byte)0x95;
	//public static final byte FCMPG = (byte)0x96;
	public static final byte DCMPL = (byte)0x97;
	//public static final byte DCMPG = (byte)0x98;
	public static final byte IFEQ = (byte)0x99;
	public static final byte IFNE = (byte)0x9a;
	public static final byte IFLT = (byte)0x9b;
	public static final byte IFGE = (byte)0x9c;
	public static final byte IFGT = (byte)0x9d;
	public static final byte IFLE = (byte)0x9e;
	public static final byte IF_ICMPEQ = (byte)0x9f;
	public static final byte IF_ICMPNE = (byte)0xa0;
	public static final byte IF_ICMPLT = (byte)0xa1;
	public static final byte IF_ICMPGE = (byte)0xa2;
	public static final byte IF_ICMPGT = (byte)0xa3;
	public static final byte IF_ICMPLE = (byte)0xa4;
	public static final byte IF_ACMPEQ = (byte)0xa5;
	public static final byte IF_ACMPNE = (byte)0xa6;
	public static final byte GOTO = (byte)0xa7;
	//public static final byte JSR = (byte)0xa8;
	//public static final byte RET = (byte)0xa9;
	public static final byte TABLESWITCH = (byte)0xaa;
	//public static final byte LOOKUPSWITCH = (byte)0xab;
	public static final byte IRETURN = (byte)0xac;
	public static final byte LRETURN = (byte)0xad;
	public static final byte FRETURN = (byte)0xae;
	public static final byte DRETURN = (byte)0xaf;
	public static final byte ARETURN = (byte)0xb0;
	public static final byte RETURN = (byte)0xb1;
	public static final byte GETSTATIC = (byte)0xb2;
	public static final byte PUTSTATIC = (byte)0xb3;
	public static final byte GETFIELD = (byte)0xb4;
	public static final byte PUTFIELD = (byte)0xb5;
	public static final byte INVOKEVIRTUAL = (byte)0xb6;
	public static final byte INVOKESPECIAL = (byte)0xb7;
	public static final byte INVOKESTATIC = (byte)0xb8;
	public static final byte INVOKEINTERFACE = (byte)0xb9;
	public static final byte NEW = (byte)0xbb;
	public static final byte NEWARRAY = (byte)0xbc;
	public static final byte ANEWARRAY = (byte)0xbd;
	public static final byte ARRAYLENGTH = (byte)0xbe;
	public static final byte ATHROW = (byte)0xbf;
	public static final byte CHECKCAST = (byte)0xc0;
	public static final byte INSTANCEOF = (byte)0xc1;
	//public static final byte MONITORENTER = (byte)0xc2;
	//public static final byte MONITOREXIT = (byte)0xc3;
	//public static final byte WIDE = (byte)0xc4;
	//public static final byte MULTIANEWARRAY = (byte)0xc5;
	public static final byte IFNULL = (byte)0xc6;
	public static final byte IFNONNULL = (byte)0xc7;
	//public static final byte GOTO_W = (byte)0xc8;
	//public static final byte JSR_W = (byte)0xc9;
	//public static final byte BREAKPOINT = (byte)0xca;
	//public static final byte IMPDEP1 = (byte)0xfe;
	//public static final byte IMPDEP2 = (byte)0xff;
}
