/**
 * 
 */
package com.djt.social.config;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 
 * @author Tom.Myers
 * 
 */
public class Log4JInitServlet extends HttpServlet {

	/* */
	private static final long serialVersionUID = 1L;

	/**
	 * @param servletConfig
	 * @throws ServletException
	 */
	public void init(ServletConfig servletConfig) throws ServletException {
		
		initLog4jProperties(servletConfig);
		super.init(servletConfig);
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter printWriter = response.getWriter();
		printWriter.println("This is the Log4JInitServlet<br/>");
		
		String rootLogLevel = request.getParameter("rootLogLevel");
		String reloadLogPropertiesFile = request.getParameter("reloadPropertiesFile");
		String logCategoryName = request.getParameter("logCategoryName");
		String logCategoryLevel = request.getParameter("logCategoryLevel");

		String message = null;
		Logger logger = Logger.getLogger(Log4JInitServlet.class);
		if (reloadLogPropertiesFile != null) {

			message = "Reloading log4j properties file.<br/>";
			printWriter.println(message);
			logger.info(message);			
			
			initLog4jProperties(getServletConfig(), printWriter);
		}
		
		if (rootLogLevel != null) {
			
			message = "Attempting to change root log level to: [" + rootLogLevel + "].<br/>";
			printWriter.println(message);
			logger.info(message);			
			
			Logger rootLogger = Logger.getRootLogger();
			setLogLevel(rootLogger, rootLogLevel);
		} 
		
		if (logCategoryName != null && logCategoryLevel != null) {
						
			Logger categoryLogger = Logger.getLogger(logCategoryName);
			if (categoryLogger != null) {
							
				message = "Attempting to change log level for category: [" + logCategoryName + "] to: [" + logCategoryLevel + "].<br/>";
				printWriter.println(message);
				logger.info(message);			
				
				setLogLevel(categoryLogger, logCategoryLevel);
			}
		}
	}

	/*
	 * @param logger 
	 * @param rootlogLevel
	 */
	private void setLogLevel(Logger logger, String logLevel) {
		
		if ("DEBUG".equalsIgnoreCase(logLevel)) {
			logger.setLevel(Level.DEBUG);
		} else if ("INFO".equalsIgnoreCase(logLevel)) {
			logger.setLevel(Level.INFO);
		} else if ("WARN".equalsIgnoreCase(logLevel)) {
			logger.setLevel(Level.WARN);
		} else if ("ERROR".equalsIgnoreCase(logLevel)) {
			logger.setLevel(Level.ERROR);
		} else if ("FATAL".equalsIgnoreCase(logLevel)) {
			logger.setLevel(Level.FATAL);
		}
	}

	/*
	 * 
	 * @param servletConfig
	 * @param printWriter
	 */
	private void initLog4jProperties(ServletConfig servletConfig) {
		this.initLog4jProperties(servletConfig, null);
	}
	
	/*
	 * 
	 * @param servletConfig
	 * @param printWriter
	 */
	private void initLog4jProperties(ServletConfig servletConfig, PrintWriter printWriter) {

		printMessage(printWriter, System.out, "Log4JInitServlet is initializing log4j");
		String log4jPropertiesLocation = servletConfig.getInitParameter("log4j-properties-location");
		if (log4jPropertiesLocation == null) {
			printMessage(printWriter, System.err, "*** No log4j-properties-location init param, so initializing log4j with BasicConfigurator");
			BasicConfigurator.configure();
		} else {
			String log4jPropertiesFilename = servletConfig.getServletContext().getRealPath("/") + log4jPropertiesLocation;
			File log4jPropertiesFile = new File(log4jPropertiesFilename);
			if (log4jPropertiesFile.exists()) {
				printMessage(printWriter, System.out, "Initializing log4j with: " + log4jPropertiesFilename);
				PropertyConfigurator.configure(log4jPropertiesFilename);
			} else {
				printMessage(printWriter, System.err, "*** " + log4jPropertiesFilename + " file not found, so initializing log4j with BasicConfigurator");
				BasicConfigurator.configure();
			}
		}
	}
	
	/*
	 * 
	 * @param printWriter
	 * @param printStream
	 * @param message
	 */
	private void printMessage(PrintWriter printWriter, PrintStream printStream, String message) {
		
		if (printWriter != null) {
			printWriter.println(message + "<br/>");
		} else if (printStream != null) {
			printStream.println(message);
		}
	}
}