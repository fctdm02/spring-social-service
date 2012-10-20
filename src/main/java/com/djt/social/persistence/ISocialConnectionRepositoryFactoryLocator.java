package com.djt.social.persistence;

import java.util.Set;

import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;

/**
 * 
 * @author Tom.Myers
 *
 */
public interface ISocialConnectionRepositoryFactoryLocator extends ConnectionFactoryLocator {

	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @return
	 */
	ConnectionFactory<?> getConnectionFactory(String promotionDeployPath, String providerId);
	
	/**
	 * 
	 * @param promotionDeployPath
	 * @param apiType
	 * @return
	 */
	<A> ConnectionFactory<A> getConnectionFactory(String promotionDeployPath, Class<A> apiType);
	
	/**
	 * 
	 * @param promotionDeployPath
	 * @return
	 */
	Set<String> registeredProviderIds(String promotionDeployPath);
}