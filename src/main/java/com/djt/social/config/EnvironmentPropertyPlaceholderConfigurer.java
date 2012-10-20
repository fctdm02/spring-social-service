/**
 * 
 */
package com.djt.social.config;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * 
 * @author Tom.Myers
 * 
 */
public final class EnvironmentPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	/* */
	public static final String SOCIAL_ENABLE_ACCESS_TOKEN_PERSISTENCE_KEY = "social.enableAccessTokenPersistence";

	/* */
	public static final String SOCIAL_ENABLE_ACCESS_TOKEN_PERSISTENCE_DEFAULT_VALUE = "false";

	
	/* */
	private static final String PASSWORD = "password";
	
	/* */
	private static final String PROTECTED_VALUE = "PROTECTED_VALUE";
	
	/* */
	private Logger logger = Logger.getLogger(EnvironmentPropertyPlaceholderConfigurer.class);
	
	/* */
	private static final Properties DEFAULT_PROPERTIES = new Properties();

	/* */
	static {
		
		// DataSource
		//
		// See: http://www.mchange.com/projects/c3p0/#configuration
		//
		// Basic Pool Configuration
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.acquireIncrement", "5");
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.initialPoolSize", "1");
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.maxPoolSize", "24");
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.minPoolSize", "1");
		
		// Managing Pool Size and Connection Age
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.maxConnectionAge", "120");
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.maxIdleTime", "60");
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.maxIdleTimeExcessConnections", "0");

		// Configuring Connection Testing
		DEFAULT_PROPERTIES.put("social.datasource.c3po.preferredTestQuery", "SELECT 1");
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.idleConnectionTestPeriod", "30");
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.testConnectionOnCheckin", "true");
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.testConnectionOnCheckout", "true");

		// Configuring Statement Pooling
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.maxStatementsPerConnection", "12");
				
		// Configuring Recovery From Database Outages
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.acquireRetryAttempts", "30");
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.acquireRetryDelay", "1000");
		DEFAULT_PROPERTIES.put("social.dataSource.c3po.breakAfterAcquireFailure", "false");
		
		
		// Caching
		DEFAULT_PROPERTIES.put("social.socialMediaCache.expirationMinutes", "15");
		DEFAULT_PROPERTIES.put("social.socialMediaCache.maxNumberElements", "2000");
		
		DEFAULT_PROPERTIES.put("social.accessTokenCache.expirationMinutes", "15");
		DEFAULT_PROPERTIES.put("social.accessTokenCache.maxNumberElements", "2000");
		
		// Access Token Persistence
		DEFAULT_PROPERTIES.put(SOCIAL_ENABLE_ACCESS_TOKEN_PERSISTENCE_KEY, SOCIAL_ENABLE_ACCESS_TOKEN_PERSISTENCE_DEFAULT_VALUE);
		
		DEFAULT_PROPERTIES.put("dataSource.host", "localhost");
		DEFAULT_PROPERTIES.put("social.dataSource.username", "root");
		DEFAULT_PROPERTIES.put("social.dataSource.password", "password");
		DEFAULT_PROPERTIES.put("social.url.salt", "ABC123");
	}
	
	
    /**
     * 
     * @param compuwareSecurityConfiguration
     */
    public EnvironmentPropertyPlaceholderConfigurer() {
    }

    /*
     * (non-Javadoc)
     * @see com.compuware.frameworks.security.api.configuration.ICompuwareSecurityConfigurationPropertyPlaceholderConfigurer#resolvePlaceholder(java.lang.String)
     */
    public final String resolvePlaceholder(String key) {
        return this.resolvePlaceholder(key, null, 0);
    }
    
    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#resolvePlaceholder(java.lang.String, java.util.Properties, int)
     */
    @SuppressWarnings("unused")
	public final String resolvePlaceholder(String key, Properties parmProperties, int systemPropertiesMode) {
        
    	String value = System.getProperty(key);
    	
    	// This is overkill, but attempt to resolve via all lowercase and then all uppercase before trying OS environment variable.
    	if (value == null) {
    		value = System.getProperty(key.toLowerCase());
    		if (value == null) {
    			value = System.getProperty(key.toUpperCase());
    		}
    	}
    	
    	if (value != null) {

        	value = value.trim();
    		logger.info("Resolved property: [" + key + "] to: [" + getDisplayValue(key, value) + "] via JRE System Property.");
    		
    	} else {
    		
            Map<String, String> environmentVariablesMap = System.getenv();
            value = environmentVariablesMap.get(key);
            
            if (value == null) {
        		value = environmentVariablesMap.get(key.toLowerCase());
        		if (value == null) {
        			value = environmentVariablesMap.get(key.toUpperCase());
        		}
            }
            
            if (value != null) {
            	
            	value = value.trim();
            	logger.info("Resolved property: [" + key + "] to: [" + getDisplayValue(key, value) + "] via OS Environment Variable.");
            	
            }
    	}

        if (value == null) {
        	
        	// As a last resort, see if the properties are defined in our default properties.
        	value = DEFAULT_PROPERTIES.getProperty(key);
            if (value == null) {
        		value = DEFAULT_PROPERTIES.getProperty(key.toLowerCase());
        		if (value == null) {
        			value = DEFAULT_PROPERTIES.getProperty(key.toUpperCase());
        		}
            }

            if (value != null) {
            	
            	value = value.trim();
            	logger.info("Resolved property: [" + key + "] to: [" + getDisplayValue(key, value) + "] via application-defined defaults.");
            	
            } else {
            
            	throw new IllegalStateException("Unable to resolve property: [" + key + "].  Please ensure that this JRE System Property is specified in Catalina.properties (or equivalent OS environment variable is defined) and try again.");
            }
        }
        
        return value;
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    private String getDisplayValue(String key, String value) {

		if (key.contains(PASSWORD)) {
			return PROTECTED_VALUE;
		} else {
			return value;
		}
    }
}