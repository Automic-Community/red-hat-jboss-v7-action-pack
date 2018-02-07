package com.uc4.ara.common.unicode;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class UnicodeOutputStreamWriter extends Writer {

	private OutputStreamWriter writer;

	public UnicodeOutputStreamWriter(FileDescriptor fdObj,
			String defaultEncoding, boolean bom) throws IOException {
		this(new FileOutputStream(fdObj), defaultEncoding, bom);
	}

	public UnicodeOutputStreamWriter(OutputStream os, String defaultEncoding,
			boolean bom) throws IOException {

		if (defaultEncoding.isEmpty()) {
			writer = new OutputStreamWriter(os);
		} else {
			CharsetEncoder enc = Charset.forName(defaultEncoding).newEncoder();
			writer = new OutputStreamWriter(os, enc);
			if (bom) {
				writer.write('\uFEFF');
			}
		}

	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		writer.write(cbuf, off, len);
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void close() throws IOException {
		//writer.close();
	}

}
