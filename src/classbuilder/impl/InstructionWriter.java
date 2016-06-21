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

import java.util.Arrays;
import java.util.Stack;

public class InstructionWriter {
	public static final short POP = 0x7FFF;
	public static final short PUSH = 0x7FFE;
	public static final short PUSH_SWAP = 0x7FFD;
	
	protected Stack<Integer> stack;
	protected byte[] data;
	protected int pos;
	protected int size;
	
	public InstructionWriter() {
		stack = new Stack<Integer>();
		data = new byte[512];
		pos = 0;
		size = 512;
	}
	
	public void write(byte inst) {
		resize();
		data[pos++] = inst;
	}
	
	private void writeAt(int pos, byte inst) {
		resize();
		data[pos] = inst;
	}
	
	private void resize() {
		if (pos + 1 == size) {
			data = Arrays.copyOf(data, size + 512);
			size += 512;
		}
	}
	
	public void push() {
		stack.push(pos);
	}
	
	public void pop() {
		short i = (short)(int)stack.pop();
		writeAt(i, (byte)(((pos - i + 1) >> 8) & 0xFF));
		writeAt(i + 1, (byte)((pos - i + 1) & 0xFF));
	}
	
	public int swap() {
		int a = stack.pop();
		int b = stack.pop();
		stack.push(a);
		stack.push(b);
		return pos;
	}
	
	public int peek() {
		return stack.peek();
	}
	
	public int getPos() {
		return pos;
	}
	
	public void write(byte inst, byte value) {
		byte b = replace(inst, value);
		if (b != 0) {
			write(b);
		} else {
			write(inst);
			write(value);
		}
	}
	
	public void write(byte inst, short value) {
		switch (value) {
		case POP :
			write(inst);
			value = (short)((int)stack.pop() - pos + 1);
			write((byte)((value >> 8) & 0xFF));
			write((byte)(value & 0xFF));
			break;
		case PUSH :
			write(inst);
			stack.push((int)pos);
			write((byte)0);
			write((byte)0);
			break;
		case PUSH_SWAP :
			write(inst);
			stack.push((int)pos);
			swap();
			write((byte)0);
			write((byte)0);
			break;
		default :
			if (inst == VMConst.LDC_W && value < 256) {
				write(VMConst.LDC);
				write((byte)(value & 0xFF));
			} else {
				write(inst);
				write((byte)((value >> 8) & 0xFF));
				write((byte)(value & 0xFF));
			}
		}
	}
	
	public void writeOffset(int pos, short offset) {
		writeAt(pos, (byte)(((offset) >> 8) & 0xFF));
		writeAt(pos + 1, (byte)((offset) & 0xFF));
	}
	
	public byte[] toByteArray() {
		return Arrays.copyOf(data, pos);
	}
	
	private byte replace(byte op, byte value) {
		switch (op) {
		case VMConst.ISTORE :
			if (value < 4) return (byte)(VMConst.ISTORE_0 + value);
			break;
		case VMConst.LSTORE :
			if (value < 4) return (byte)(VMConst.LSTORE_0 + value);
			break;
		case VMConst.FSTORE :
			if (value < 4) return (byte)(VMConst.FSTORE_0 + value);
			break;
		case VMConst.DSTORE :
			if (value < 4) return (byte)(VMConst.DSTORE_0 + value);
			break;
		case VMConst.ASTORE :
			if (value < 4) return (byte)(VMConst.ASTORE_0 + value);
			break;
		case VMConst.ILOAD :
			if (value < 4) return (byte)(VMConst.ILOAD_0 + value);
			break;
		case VMConst.LLOAD :
			if (value < 4) return (byte)(VMConst.LLOAD_0 + value);
			break;
		case VMConst.FLOAD :
			if (value < 4) return (byte)(VMConst.FLOAD_0 + value);
			break;
		case VMConst.DLOAD :
			if (value < 4) return (byte)(VMConst.DLOAD_0 + value);
			break;
		case VMConst.ALOAD :
			if (value < 4) return (byte)(VMConst.ALOAD_0 + value);
			break;
		}
		return 0;
	}
	
	public void prepend(InstructionWriter inst) {
		int offset = inst.pos;
		if (size < pos + offset) {
			data = Arrays.copyOf(data, pos + offset);
		}
		for (int i = pos - 1; i >= 0; i--) {
			data[i + offset] = data[i];
		}
		byte[] buffer = inst.data;
		for (int i = 0; i < offset; i++) {
			data[i] = buffer[i];
		}
		pos += offset;
	}
	
}
