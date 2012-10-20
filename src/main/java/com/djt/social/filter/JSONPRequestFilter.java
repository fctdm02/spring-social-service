/**
 * 
 */
package com.djt.social.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * 
 * @author Steve.Carter
 *
 */
public final class JSONPRequestFilter implements Filter {

	/* */
	private static final Logger logger = Logger.getLogger(JSONPRequestFilter.class);

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@SuppressWarnings("unused")
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.debug("Initializing JSONP filter");
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {

	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(
		ServletRequest request, 
		ServletResponse response,
		FilterChain chain) 
	throws 
		IOException, 
		ServletException {
		
		if (!(request instanceof HttpServletRequest)) {
			throw new ServletException("This filter can only process HttpServletRequest requests");
		}

		HttpServletRequest httpRequest = (HttpServletRequest)request;
		
		if (isJSONPRequest(httpRequest)) {

			OutputStream out = null;
			try {
				logger.debug("Rewriting response as JSONP");
				out = response.getOutputStream();
				out.write(getCallbackMethod(httpRequest).getBytes());
				out.write("(".getBytes());
				GenericResponseWrapper wrapper = new GenericResponseWrapper((HttpServletResponse) response); 
				chain.doFilter(request,wrapper);
				out.write(wrapper.getData());
				out.write(");".getBytes());
			} finally {
				if (out != null) {
					out.flush();
					out.close();
				}
			}
			
		} else {
			chain.doFilter(request, response);
		}
	}

	/*
	 * 
	 * @param httpRequest
	 * @return
	 */
	private String getCallbackMethod(HttpServletRequest httpRequest) {
		return httpRequest.getParameter("callback");
	}

	/*
	 * 
	 * @param httpRequest
	 * @return
	 */
	private boolean isJSONPRequest(HttpServletRequest httpRequest) {
		String callbackMethod = getCallbackMethod(httpRequest);
		return (callbackMethod != null && callbackMethod.length() > 0);
	}
}