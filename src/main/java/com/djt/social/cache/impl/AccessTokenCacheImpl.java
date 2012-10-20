/**
 * 
 */
package com.djt.social.cache.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.djt.social.cache.IAccessTokenCache;
import com.djt.social.config.EnvironmentPropertyPlaceholderConfigurer;
import com.djt.social.model.AccessTokenResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * 
 * @author Tom.Myers
 */
public final class AccessTokenCacheImpl implements IAccessTokenCache {
	
	/* */
	private static final String CACHE_KEY_DELIMITER = "|";

	/* */
	private static final int STRING_BUILDER_BUFFER_SIZE = 512;
		
	/* */
	private Logger logger = Logger.getLogger(AccessTokenCacheImpl.class);	
	
	/* 
	 * Keyed by promotionDeployPath|providerId|uid
	 */
	private Cache<String, AccessTokenResponse> accessTokenResponseCache;

	/* 
	 * This property can be overridden via dependency injection in the spring context. 
	 */
	private int cacheTimeoutInMinutes;
	
	/* 
	 * This property can be overridden via dependency injection in the spring context. 
	 */
	private int maxCacheSize;

	/* */
	private boolean enableAccessTokenPersistence;
	
	/* */
	private EnvironmentPropertyPlaceholderConfigurer environmentPropertyPlaceholderConfigurer = new EnvironmentPropertyPlaceholderConfigurer();
	

	/**
	 * @param cacheTimeoutInMinutes 
	 * @param maxCacheSize
	 * @param enableAccessTokenPersistence
	 */
	public AccessTokenCacheImpl(
		int cacheTimeoutInMinutes, 
		int maxCacheSize,
		boolean enableAccessTokenPersistence) {
				
		this.cacheTimeoutInMinutes = cacheTimeoutInMinutes;
		this.maxCacheSize = maxCacheSize;
		this.enableAccessTokenPersistence = enableAccessTokenPersistence; 

		this.accessTokenResponseCache = CacheBuilder.newBuilder().expireAfterWrite(this.cacheTimeoutInMinutes, TimeUnit.MINUTES)
            .maximumSize(this.maxCacheSize)
            .build(new CacheLoader<String, AccessTokenResponse>() {
				@SuppressWarnings("unused")
				@Override
                public AccessTokenResponse load(String queryKey) throws Exception {
					return null;
                }
            });
	}
	    
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.IAccessTokenCache#getAccessTokenResponse(java.lang.String, java.lang.String, java.lang.String)
	 */
	public AccessTokenResponse getAccessTokenResponse(
		String promotionDeployPath, 
		String providerId, 
		String uid) {
		
    	String key = buildKey(promotionDeployPath, providerId, uid); 
    	AccessTokenResponse accessTokenResponse = this.accessTokenResponseCache.getIfPresent(key);
    	if (accessTokenResponse == null) {
    		logger.debug("ACCESS TOKEN RESPONSE CACHE MISS: for: [" + key + "].");
    	} else {
    		logger.debug("ACCESS TOKEN RESPONSE CACHE HIT: for: [" + key + "].");
    	}
    	return accessTokenResponse;
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.IAccessTokenCache#putAccessTokenResponse(java.lang.String, java.lang.String, java.lang.String, com.djt.social.model.AccessTokenResponse)
	 */
	public void putAccessTokenResponse(
		String promotionDeployPath, 
		String providerId, 
		String uid, 
		AccessTokenResponse accessTokenResponse) {
		
    	String key = buildKey(promotionDeployPath, providerId, uid); 
		this.accessTokenResponseCache.put(key, accessTokenResponse);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.IAccessTokenCache#removeAccessTokenResponse(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void removeAccessTokenResponse(
		String promotionDeployPath, 
		String providerId, 
		String uid) {

    	String key = buildKey(promotionDeployPath, providerId, uid); 
		this.accessTokenResponseCache.invalidate(key);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.IAccessTokenCache#getEnableAccessTokenPersistence()
	 */
	public boolean getEnableAccessTokenPersistence() {
		
		String value = environmentPropertyPlaceholderConfigurer.resolvePlaceholder(EnvironmentPropertyPlaceholderConfigurer.SOCIAL_ENABLE_ACCESS_TOKEN_PERSISTENCE_KEY);
		if (value != null && !value.isEmpty()) {
			return Boolean.parseBoolean(value);
		}
		return this.enableAccessTokenPersistence;
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.IAccessTokenCache#removeAll()
	 */
	public void removeAll() {
		this.accessTokenResponseCache.invalidateAll();
	}
	
	/*
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param uid
	 * @return
	 */
	private String buildKey(
		String promotionDeployPath,
		String providerId,
		String uid) {
		
		List<String> keyList = new ArrayList<String>();
		keyList.add(promotionDeployPath);
		keyList.add(providerId);
		keyList.add(uid);
		return buildKey(keyList);
	}
	
	/*
	 * @param keyList
	 * @return
	 */
	private String buildKey(List<String> keyList) {
		
    	StringBuilder sb = new StringBuilder(STRING_BUILDER_BUFFER_SIZE);
		Iterator<String> iterator = keyList.iterator();
		while (iterator.hasNext()) {
			
			String keyComponent = iterator.next();
	    	sb.append(keyComponent);
	    	if (iterator.hasNext()) {
	    		sb.append(CACHE_KEY_DELIMITER);	
	    	}
		}		
    	return sb.toString(); 
	}
}