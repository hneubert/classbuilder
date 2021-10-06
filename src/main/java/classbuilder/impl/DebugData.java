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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import classbuilder.impl.DebugData.LineNumberEntry;

public class DebugData implements Iterable<LineNumberEntry> {
	public static class LineNumberEntry {
		private short offset;
		private short line;
		
		public LineNumberEntry(int line, int offset) {
			this.line = (short)line;
			this.offset = (short)offset;
		}
		
		public short getLine() {
			return line;
		}
		
		public int getOffset() {
			return offset;
		}
	}
	
	protected List<LineNumberEntry> lines;
	protected int level;
	protected int countLines;
	protected ByteArrayOutputStream data;
	protected Writer out;
	
	public DebugData() {
		lines = new ArrayList<LineNumberEntry>();
		data = new ByteArrayOutputStream();
		out = new OutputStreamWriter(data);
		level = 1;
		countLines = 1;
	}
	
	public void incrementLevel() {
		level++;
	}
	
	public void decrementLevel() {
		level--;
	}
	
	public void addLine(String text, int offset) {
		addLine(text);
		lines.add(new LineNumberEntry(countLines, offset));
	}
	
	public void addLine(String text) {
		try {
			out.write(addTabs(level));
			out.write(text);
			out.write("\n");
		} catch (IOException e) {
			// nothing to do
		}
		countLines++;
	}
	
	public List<LineNumberEntry> getLines() {
		return lines;
	}
	
	public Iterator<LineNumberEntry> iterator() {
		return lines.iterator();
	}
	
	public void writeData(DataOutputStream out, int offset) throws IOException {
		out.writeInt(countLines * 4 + 2);
		out.writeShort(countLines);
    	for (LineNumberEntry line : lines) {
    		if (line.getOffset() != -1) {
    			out.writeShort(line.getOffset()); // start_pc
    			out.writeShort(line.getLine() + offset); // line_number
    		}
    	}
	}
	
	public int countLines() {
		return countLines - 1;
	}
	
	public void writeSource(OutputStream out) throws IOException {
		this.out.close();
		data.writeTo(out);
	}
	
	private String addTabs(int level) {
		String s = "";
		for (int i = 0; i < level; i++) s += "\t";
		return s;
	}
}
