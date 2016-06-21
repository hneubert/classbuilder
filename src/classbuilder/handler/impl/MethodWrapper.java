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

package classbuilder.handler.impl;

import java.util.ArrayList;

import classbuilder.BuilderAccessException;
import classbuilder.BuilderSyntaxException;
import classbuilder.BuilderTypeException;
import classbuilder.IMethod;
import classbuilder.Variable;
import classbuilder.impl.DefaultMethod;
import classbuilder.impl.InstructionWriter;
import classbuilder.impl.VMConst;
import classbuilder.util.MethodDelegate;

public class MethodWrapper extends MethodDelegate {
	
	private Variable result;
	private ArrayList<Integer> offsets;
	
	public MethodWrapper(IMethod method, Variable result) {
		super(method.getDeclaringClass(), method);
		
		offsets = new ArrayList<Integer>();
		this.result = result;
	}
	
	@Override
	public void Return() throws BuilderSyntaxException, BuilderTypeException {
		DefaultMethod m = (DefaultMethod)method;
		InstructionWriter writer = m.getWriter();
		offsets.add(writer.getPos());
		writer.write(VMConst.GOTO, (short)0);
	}
	
	@Override
	public void Return(Object value) throws BuilderSyntaxException, BuilderTypeException {
		DefaultMethod m = (DefaultMethod)method;
		if (result != null) {
			try {
				result.set(value);
			} catch (BuilderAccessException e) {
				throw new BuilderSyntaxException(method, e.getMessage(), e);
			}
		}
		InstructionWriter writer = m.getWriter();
		offsets.add(writer.getPos());
		writer.write(VMConst.GOTO, (short)0);
	}
	
	public void writeOffsets() {
		try {
			DefaultMethod m = (DefaultMethod)method;
			InstructionWriter writer = m.getWriter();
			for (Integer offset : offsets) {
				writer.writeOffset(offset + 1, (short)(writer.getPos() - offset));
			}
			offsets.clear();
		} catch (BuilderSyntaxException e) {
			
		}
	}
	
}
