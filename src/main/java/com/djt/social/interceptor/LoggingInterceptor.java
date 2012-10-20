/**
 * 
 */
package com.djt.social.interceptor;

import java.util.Arrays;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

/**
 * 
 * @author Tom.Myers
 */
public class LoggingInterceptor implements MethodInterceptor {
    
	/* */
	private static final int STRING_BUILDER_BUFFER_SIZE = 512;
	
    /*
     * (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        StringBuilder sb = new StringBuilder(STRING_BUILDER_BUFFER_SIZE);
        Class<?> clazz = methodInvocation.getMethod().getDeclaringClass();
        Logger logger = Logger.getLogger(clazz);

        Object[] argumentsArray = methodInvocation.getArguments();
        List<Object> argumentsList = Arrays.asList(argumentsArray);
        
        sb.append("REQUEST: ");
        sb.append(methodInvocation.getMethod().getName());
        sb.append("(): PARAMETERS: ");
        sb.append(argumentsList.toString());
        logger.debug(sb.toString());
        
        Object returnValue = methodInvocation.proceed();

    	sb.setLength(0);
        sb.append("RESPONSE: ");
        sb.append(methodInvocation.getMethod().getName());
        sb.append("(): ");
        sb.append(returnValue);    
        logger.debug(sb.toString());
    	
    	return returnValue;
    }
}