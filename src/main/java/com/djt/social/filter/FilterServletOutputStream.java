/**
 * 
 */
package com.djt.social.filter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * 
 * @author Steve.Carter
 *
 */
public final class FilterServletOutputStream extends ServletOutputStream {

	/* */
	private DataOutputStream stream; 

	/**
	 * 
	 * @param output
	 */
	public FilterServletOutputStream(OutputStream output) { 
		stream = new DataOutputStream(output); 
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException  { 
		stream.write(b); 
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException  { 
		stream.write(b); 
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException  { 
		stream.write(b,off,len); 
	} 
}