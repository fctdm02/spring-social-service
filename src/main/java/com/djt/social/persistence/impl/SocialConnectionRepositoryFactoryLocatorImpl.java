/**
 * 
 */
package com.djt.social.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.flickr.connect.FlickrConnectionFactory;
import org.springframework.social.instagram.connect.InstagramConnectionFactory;

import com.djt.social.event.ISocialEventHelper;
import com.djt.social.event.ISocialEventListener;
import com.djt.social.event.SocialEvent;
import com.djt.social.persistence.ISocialConnectionRepositoryFactoryLocator;
import com.djt.social.persistence.ISocialProviderApiKeyRepository;
import com.djt.social.service.ISocialService;

/**
 * 
 * @author Tom.Myers
 *
 */
public final class SocialConnectionRepositoryFactoryLocatorImpl implements ISocialConnectionRepositoryFactoryLocator, ISocialEventListener {

	/* */
	private Logger logger = Logger.getLogger(SocialConnectionRepositoryFactoryLocatorImpl.class);
	
	/**
	 * Keyed by promotion deploy path.  The value is the set of connection factories for a given promotion.
	 * 
	 * The "installSocialProviderApiKeyForPromotion()" method in "ISocialService" is what is used to install these API keys. 
	 * 
	 * It is assumed that the promotion "roll" process will have steps to installl these keys.
	 * 
	 * The empty string will denote the default API keys whose values are stored in the code (i.e. for test purposes)
	 */
	private Map<String, ConnectionFactoryRegistry> connectionFactoryRegistryMap = new HashMap<String, ConnectionFactoryRegistry>();

	/* */
	private ISocialProviderApiKeyRepository socialProviderApiKeyRepository;
	
	/* */
	private final ISocialEventHelper socialEventHelper;
	
	/**
	 * @param socialProviderApiKeyRepository
	 * @param socialEventHelper
	 */
	public SocialConnectionRepositoryFactoryLocatorImpl(
		ISocialProviderApiKeyRepository socialProviderApiKeyRepository,
		ISocialEventHelper socialEventHelper) {
		
		this.socialProviderApiKeyRepository = socialProviderApiKeyRepository;
		this.socialEventHelper = socialEventHelper;
		this.socialEventHelper.addSocialEventListener(this);

		initializeConnectionFactories();
	}

	/*
	 * 
	 */
	public void initializeConnectionFactories() {
		
		// Load all of the registered API keys for all of the deployed promotions (including test ones) and then create the appropriate 
		// spring social connection factory so that all of the spring social connection factories for a given promotion are 
		// all stored in a single list.  This list of connection factories is then set for a connection factory registry for
		// the promotion as a whole. In other words, there is a connection factory registry for each promotion and this registry
		// contains connection factory objects for each supported social media provider.
		Map<String, Set<ConnectionFactory<?>>> connectionFactoriesMap = new HashMap<String, Set<ConnectionFactory<?>>>();

		
		List<Map<String, String>> promotionSocialProviderApiKeys = this.socialProviderApiKeyRepository.getAllProviderApiKeyPairs();
		Iterator<Map<String, String>> promotionSocialProviderApiKeysIterator = promotionSocialProviderApiKeys.iterator();
		while (promotionSocialProviderApiKeysIterator.hasNext()) {
			
			Map<String, String> map = promotionSocialProviderApiKeysIterator.next();
			
			String promotionDeployPath = map.get(ISocialProviderApiKeyRepository.PROMO_DEPLOY_PATH);
			String providerId = map.get(ISocialProviderApiKeyRepository.PROVIDER_ID);
			String apiKey = map.get(ISocialProviderApiKeyRepository.API_KEY);
			String apiKeySecret = map.get(ISocialProviderApiKeyRepository.API_KEY_SECRET);
					
			Set<ConnectionFactory<?>> connectionFactories = connectionFactoriesMap.get(promotionDeployPath);
			if (connectionFactories == null) {
				
				connectionFactories = new HashSet<ConnectionFactory<?>>();
				connectionFactoriesMap.put(promotionDeployPath, connectionFactories);
			}
			
			if (providerId.equalsIgnoreCase(ISocialService.PROVIDER_NAME_FACEBOOK)) {

				logger.info("Initializing spring social service provider connection factory: [" + ISocialService.PROVIDER_NAME_FACEBOOK + "] for promotion: [" + promotionDeployPath + "].");
				connectionFactories.add(new FacebookConnectionFactory(apiKey, apiKeySecret));
				
			} else if (providerId.equalsIgnoreCase(ISocialService.PROVIDER_NAME_FLICKR)) {

				logger.info("Initializing spring social service provider connection factory: [" + ISocialService.PROVIDER_NAME_FLICKR + "] for promotion: [" + promotionDeployPath + "].");
				connectionFactories.add(new FlickrConnectionFactory(apiKey, apiKeySecret));
				
			} else if (providerId.equalsIgnoreCase(ISocialService.PROVIDER_NAME_INSTAGRAM)) {

				logger.info("Initializing spring social service provider connection factory: [" + ISocialService.PROVIDER_NAME_INSTAGRAM + "] for promotion: [" + promotionDeployPath + "].");
				connectionFactories.add(new InstagramConnectionFactory(apiKey, apiKeySecret));
				
			/*	
			} else if (providerId.equalsIgnoreCase(ISocialService.PROVIDER_NAME_TWITTER)) {

				logger.info("Initializing spring social service provider connection factory: [" + ISocialService.PROVIDER_NAME_TWITTER + "] for promotion: [" + promotionDeployPath + "].");
				connectionFactories.add(new TwitterConnectionFactory(apiKey, apiKeySecret));
			*/
				
			} else {
				
				StringBuilder sb = new StringBuilder();
				sb.append("Unsupported social media provider id: [");
				sb.append(providerId);
				sb.append("] found for promotion: [");
				sb.append(promotionDeployPath);
				sb.append("] with API Key: [");
				sb.append(apiKey);
				sb.append("] and API Key Secret: [");
				sb.append(apiKeySecret);
				sb.append("].");
				logger.error(sb.toString());
			}
		}
		
		// Now that all of the connection factories for all of the providers have been created for each promotion, 
		// create a connection factory registry for each deployed promotion. 
		Iterator<String> connectionFactoriesMapIterator = connectionFactoriesMap.keySet().iterator();
		while (connectionFactoriesMapIterator.hasNext()) {
			
			String promotionDeployPath = connectionFactoriesMapIterator.next();
			
			List<ConnectionFactory<?>> connectionFactoriesList = new ArrayList<ConnectionFactory<?>>();
			
			Set<ConnectionFactory<?>> connectionFactoriesSet = connectionFactoriesMap.get(promotionDeployPath); 
			Iterator<ConnectionFactory<?>> connectionFactoriesSetIterator = connectionFactoriesSet.iterator();
			while (connectionFactoriesSetIterator.hasNext()) {
				connectionFactoriesList.add(connectionFactoriesSetIterator.next());
			}
								
			ConnectionFactoryRegistry connectionFactoryRegistry = new ConnectionFactoryRegistry();
			connectionFactoryRegistryMap.put(promotionDeployPath, connectionFactoryRegistry);

			logger.info("Initializing spring social service provider connection factories for promotion: [" + promotionDeployPath + "].");
			connectionFactoryRegistry.setConnectionFactories(connectionFactoriesList);
		}
	}
	
	/**
	 * 
	 * @param connectionFactories
	 */
	@SuppressWarnings("unused")
	public void setConnectionFactories(List<ConnectionFactory<?>> connectionFactories) {
		throw new IllegalStateException("Connection Factories are initialized by installing API keys for a given promotion path on ISocialService.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionFactoryLocator#getConnectionFactory(java.lang.String)
	 */
	@SuppressWarnings("unused")
	public ConnectionFactory<?> getConnectionFactory(String providerId) {
		throw new IllegalStateException("Unsupported operation.  Use getConnectionFactory(String promotionDeployPath, String providerId) instead.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionFactoryLocator#getConnectionFactory(java.lang.String)
	 */
	public ConnectionFactory<?> getConnectionFactory(String promotionDeployPath, String providerId) {
				
		logger.debug("getConnectionFactory(): promotionDeployPath: " + promotionDeployPath);
		ConnectionFactoryRegistry connectionFactoryRegistry = this.connectionFactoryRegistryMap.get(promotionDeployPath);

		String errorMessage = "No API Keys have been registered for promotionDeployPath: [" + promotionDeployPath + "] for social media service provider: [" + providerId + "]. Please create/register a set of API keys and try again.";
		
		if (connectionFactoryRegistry == null) {
			throw new IllegalStateException(errorMessage);
		}
		
		try {
			return connectionFactoryRegistry.getConnectionFactory(providerId);
		} catch (IllegalArgumentException iae) {
			throw new IllegalStateException(errorMessage, iae);
		}
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionFactoryLocator#getConnectionFactory(java.lang.Class)
	 */
	@SuppressWarnings("unused")
	public <A> ConnectionFactory<A> getConnectionFactory(Class<A> apiType) {
		throw new IllegalStateException("registeredProviderIds(): Unsupported operation.  Use getConnectionFactory(String promotionDeployPath, Class<A> apiType) instead.");
	}		
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionFactoryLocator#getConnectionFactory(java.lang.Class)
	 */
	public <A> ConnectionFactory<A> getConnectionFactory(String promotionDeployPath, Class<A> apiType) {
		
		logger.debug("getConnectionFactory(): promotionDeployPath: " + promotionDeployPath);
		ConnectionFactoryRegistry connectionFactoryRegistry = this.connectionFactoryRegistryMap.get(promotionDeployPath);

		if (connectionFactoryRegistry == null) {
			throw new IllegalStateException("No API Keys have been registered for promotion: [" + promotionDeployPath + "] for social media service provider: [" + apiType.getSimpleName() + "]. Please create/register a set of API keys and try again.");
		}
		return connectionFactoryRegistry.getConnectionFactory(apiType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionFactoryLocator#registeredProviderIds()
	 */
	public Set<String> registeredProviderIds() {
		throw new IllegalStateException("registeredProviderIds(): Unsupported operation.  Use registeredProviderIds(String promotionDeployPath) instead.");
	}		
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionFactoryLocator#registeredProviderIds()
	 */
	public Set<String> registeredProviderIds(String promotionDeployPath) {
		
		ConnectionFactoryRegistry connectionFactoryRegistry = this.connectionFactoryRegistryMap.get(promotionDeployPath);

		if (connectionFactoryRegistry == null) {
			throw new IllegalStateException("No API Keys have been registered for promotion: [" + promotionDeployPath + "]. Please create/register a set of API keys and try again.");
		}

		return connectionFactoryRegistry.registeredProviderIds();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.event.ISocialEventListener#socialEventOccurred(com.djt.social.event.SocialEvent)
	 */
	public void socialEventOccurred(SocialEvent socialEvent) {
		
		if (socialEvent.getEventType().equals(SocialEvent.SOCIAL_PROVIDER_API_INSTALLED)) {
			
			logger.info("Event occured for new Social Provider API Key: [" + socialEvent.getEventDetails() + "], re-initializing connection factory registry.");
			initializeConnectionFactories();
		}
	}
}