/**
 * 
 */
package com.djt.social.filter;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * 
 * @author Steve.Carter
 *
 */
public final class GenericResponseWrapper extends HttpServletResponseWrapper {
	
	/* */
	private ByteArrayOutputStream output;
	
	/* */
	private int contentLength;
	
	/* */
	private String contentType;

	/**
	 * 
	 * @param response
	 */
	public GenericResponseWrapper(HttpServletResponse response) {
		
		super(response);
	    output=new ByteArrayOutputStream();
	} 

	/**
	 * 
	 * @return
	 */
	public byte[] getData() { 
	    return output.toByteArray(); 
	} 

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletResponseWrapper#getOutputStream()
	 */
	@Override
	public ServletOutputStream getOutputStream() { 
	    return new FilterServletOutputStream(output); 
	} 
	  
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletResponseWrapper#getWriter()
	 */
	@Override
	public PrintWriter getWriter() { 
	    return new PrintWriter(getOutputStream(),true); 
	} 

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletResponseWrapper#setContentLength(int)
	 */
	@Override
	public void setContentLength(int length) { 
	    this.contentLength = length;
	    super.setContentLength(length); 
	} 

	/**
	 * @return
	 */
	public int getContentLength() { 
	    return contentLength; 
	} 

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletResponseWrapper#setContentType(java.lang.String)
	 */
	@Override
	public void setContentType(String type) { 
	    this.contentType = type;
	    super.setContentType(type); 
	} 

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletResponseWrapper#getContentType()
	 */
	@Override
	public String getContentType() { 
	    return contentType; 
	} 
} 
