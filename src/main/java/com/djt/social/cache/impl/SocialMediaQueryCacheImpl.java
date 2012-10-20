/**
 * 
 */
package com.djt.social.cache.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.djt.social.cache.ISocialMediaQueryCache;
import com.djt.social.model.SocialAlbum;
import com.djt.social.model.SocialPhoto;
import com.djt.social.model.SocialProfile;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * 
 * @author Tom.Myers
 */
public final class SocialMediaQueryCacheImpl implements ISocialMediaQueryCache {

	/* */
	private static final String CACHE_KEY_DELIMITER = "|";
		
	/* */
	private static final int STRING_BUILDER_BUFFER_SIZE = 512;
		
	
	/* */
	private Logger logger = Logger.getLogger(AccessTokenCacheImpl.class);	
	
	/* 
	 * Keyed by promotionDeployPath|providerId|accessToken
	 */
	private Cache<String, List<SocialAlbum>> socialPhotoAlbumsCache;

	/* 
	 * Keyed by promotionDeployPath|providerId|accessToken|albumId
	 */
	private Cache<String, List<SocialPhoto>> socialPhotosCache;
	
	/* 
	 * Keyed by promotionDeployPath|providerId|accessToken
	 */
	private Cache<String, SocialProfile> socialProfileCache;
	
	
	/* 
	 * This property can be overridden via dependency injection in the spring context. 
	 */
	private int cacheTimeoutInMinutes;
	
	/* 
	 * This property can be overridden via dependency injection in the spring context. 
	 */
	private int maxCacheSize;


	/**
	 * @param cacheTimeoutInMinutes 
	 * @param maxCacheSize
	 */
	public SocialMediaQueryCacheImpl(int cacheTimeoutInMinutes, int maxCacheSize) {
				
		this.cacheTimeoutInMinutes = cacheTimeoutInMinutes;
		this.maxCacheSize = maxCacheSize;
		
		this.socialPhotoAlbumsCache = CacheBuilder.newBuilder().expireAfterWrite(this.cacheTimeoutInMinutes, TimeUnit.MINUTES)
            .maximumSize(this.maxCacheSize)
            .build(new CacheLoader<String, List<SocialAlbum>>() {
				@SuppressWarnings("unused")
				@Override
                public List<SocialAlbum> load(String queryKey) throws Exception {
					return null;
                }
            });
		
		this.socialPhotosCache = CacheBuilder.newBuilder().expireAfterWrite(this.cacheTimeoutInMinutes, TimeUnit.MINUTES)
            .maximumSize(this.maxCacheSize)
            .build(new CacheLoader<String, List<SocialPhoto>>() {
				@SuppressWarnings("unused")
				@Override
                public List<SocialPhoto> load(String queryKey) throws Exception {
					return null;
                }
            });
		
		this.socialProfileCache = CacheBuilder.newBuilder().expireAfterWrite(this.cacheTimeoutInMinutes, TimeUnit.MINUTES)
            .maximumSize(this.maxCacheSize)
            .build(new CacheLoader<String, SocialProfile>() {
				@SuppressWarnings("unused")
				@Override
                public SocialProfile load(String queryKey) throws Exception {
					return null;
                }
            });
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#get(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<SocialAlbum> getSocialPhotoAlbums(
		String promotionDeployPath, 
		String providerId, 
		String accessToken) {

    	String key = buildKey(promotionDeployPath, providerId, accessToken); 
    	List<SocialAlbum> socialAlbums = this.socialPhotoAlbumsCache.getIfPresent(key);
    	if (socialAlbums == null) {
    		logger.debug("SOCIAL PHOTO ALBUMS CACHE MISS: FOR: [" + key + "].");
    	} else {
    		logger.debug("SOCIAL PHOTO ALBUMS CACHE HIT: FOR: [" + key + "].");
    	}
    	return socialAlbums;
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#putSocialPhotoAlbums(java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	public void putSocialPhotoAlbums(
		String promotionDeployPath, 
		String providerId, 
		String accessToken,
		List<SocialAlbum> socialAlbums) {

    	String key = buildKey(promotionDeployPath, providerId, accessToken);
		this.socialPhotoAlbumsCache.put(key, socialAlbums);
	}	
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#removeSocialPhotoAlbums(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void removeSocialPhotoAlbums(
		String promotionDeployPath, 
		String providerId, 
		String accessToken) {

    	String key = buildKey(promotionDeployPath, providerId, accessToken);
    	logger.debug("FLUSH SOCIAL PHOTO ALBUMS CACHE FOR: [" + key + "].");
		this.socialPhotoAlbumsCache.invalidate(key);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#getSocialPhotosFromSocialPhotoAlbum(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<SocialPhoto> getSocialPhotosFromSocialPhotoAlbum(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		String albumId) {

    	String key = buildKey(promotionDeployPath, providerId, accessToken, albumId); 
    	List<SocialPhoto> socialPhotos = this.socialPhotosCache.getIfPresent(key);
    	if (socialPhotos == null) {
    		logger.debug("SOCIAL PHOTOS CACHE MISS: FOR: [" + key + "].");
    	} else {
    		logger.debug("SOCIAL PHOTOS CACHE HIT: FOR: [" + key + "].");
    	}
    	return socialPhotos;
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#getSocialPhotosFromSocialPhotoAlbum(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int)
	 */
	public List<SocialPhoto> getSocialPhotosFromSocialPhotoAlbum(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		String albumId,
		int offset,
		int limit) {

    	String key = buildKey(promotionDeployPath, providerId, accessToken, albumId, offset, limit); 
    	List<SocialPhoto> socialPhotos = this.socialPhotosCache.getIfPresent(key);
    	if (socialPhotos == null) {
    		logger.debug("SOCIAL PHOTOS CACHE MISS: FOR: [" + key + "].");
    	} else {
    		logger.debug("SOCIAL PHOTOS CACHE HIT: FOR: [" + key + "].");
    	}
    	return socialPhotos;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#putSocialPhotosFromSocialPhotoAlbum(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, java.util.List)
	 */
	public void putSocialPhotosFromSocialPhotoAlbum(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		String albumId, 
		int offset,
		int limit,
		List<SocialPhoto> socialPhotos) {
		
    	String key = buildKey(promotionDeployPath, providerId, accessToken, albumId, offset, limit); 
		this.socialPhotosCache.put(key, socialPhotos);
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#removeSocialPhotosFromSocialPhotoAlbum(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unused")
	public void removeSocialPhotosFromSocialPhotoAlbum(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		String albumId) {
		
		// TODO: TDM: See if there is a better way to do this.
    	logger.debug("FLUSHING ALL SOCIAL PHOTOS FROM CACHE.");
    	this.socialPhotosCache.invalidateAll();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#getSocialPhotos(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<SocialPhoto> getSocialPhotos(
		String promotionDeployPath, 
		String providerId, 
		String accessToken) {
		
    	String key = buildKey(promotionDeployPath, providerId, accessToken); 
    	List<SocialPhoto> socialPhotos = this.socialPhotosCache.getIfPresent(key);
    	if (socialPhotos == null) {
    		logger.debug("SOCIAL PHOTOS CACHE MISS: FOR: [" + key + "].");
    	} else {
    		logger.debug("SOCIAL PHOTOS CACHE HIT: FOR: [" + key + "].");
    	}
    	return socialPhotos;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#putSocialPhotos(java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	public void putSocialPhotos(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		List<SocialPhoto> socialPhotos) {

    	String key = buildKey(promotionDeployPath, providerId, accessToken); 
		this.socialPhotosCache.put(key, socialPhotos);
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#removeSocialPhotos(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void removeSocialPhotos(
		String promotionDeployPath, 
		String providerId, 
		String accessToken) {

    	String key = buildKey(promotionDeployPath, providerId, accessToken);
    	logger.debug("FLUSH SOCIAL PHOTOS: FOR: [" + key + "].");
		this.socialPhotosCache.invalidate(key);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#getSocialProfile(java.lang.String, java.lang.String, java.lang.String)
	 */
	public SocialProfile getSocialProfile(
		String promotionDeployPath, 
		String providerId, 
		String accessToken) {
		
    	String key = buildKey(promotionDeployPath, providerId, accessToken); 
    	SocialProfile socialProfile = this.socialProfileCache.getIfPresent(key);
    	if (socialProfile == null) {
    		logger.debug("SOCIAL PROFILE CACHE MISS: FOR: [" + key + "].");
    	} else {
    		logger.debug("SOCIAL PROFILE CACHE HIT: FOR: [" + key + "].");
    	}
    	return socialProfile;
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#putSocialProfile(java.lang.String, java.lang.String, java.lang.String, com.djt.social.model.SocialProfile)
	 */
	public void putSocialProfile(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		SocialProfile socialProfile) {

    	String key = buildKey(promotionDeployPath, providerId, accessToken); 
		this.socialProfileCache.put(key, socialProfile);
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#removeSocialProfile(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void removeSocialProfile(
		String promotionDeployPath, 
		String providerId, 
		String accessToken) {

    	String key = buildKey(promotionDeployPath, providerId, accessToken);
    	logger.debug("FLUSH SOCIAL PROFILE CACHE FOR: [" + key + "].");
		this.socialProfileCache.invalidate(key);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.cache.ISocialMediaQueryCache#removeAll()
	 */
	public void removeAll() {
		
		logger.debug("FLUSH ALL CACHES.");
		this.socialPhotoAlbumsCache.invalidateAll();
		this.socialPhotosCache.invalidateAll();
		this.socialProfileCache.invalidateAll();
	}
	
	/*
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @return
	 */
	private String buildKey(
		String promotionDeployPath,
		String providerId,
		String accessToken) {
		
		List<String> keyList = new ArrayList<String>();
		keyList.add(promotionDeployPath);
		keyList.add(providerId);
		keyList.add(accessToken);
		return buildKey(keyList);
	}

	/*
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param albumId
	 * @return
	 */
	private String buildKey(
		String promotionDeployPath,
		String providerId,
		String accessToken,
		String albumId) {
		
		List<String> keyList = new ArrayList<String>();
		keyList.add(promotionDeployPath);
		keyList.add(providerId);
		keyList.add(accessToken);
		keyList.add(albumId);
		return buildKey(keyList);
	}

	/*
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param albumId
	 * @param offset
	 * @param limit
	 * @return
	 */
	private String buildKey(
		String promotionDeployPath,
		String providerId,
		String accessToken,
		String albumId,
		int offset,
		int limit) {
		
		List<String> keyList = new ArrayList<String>();
		keyList.add(promotionDeployPath);
		keyList.add(providerId);
		keyList.add(accessToken);
		keyList.add(albumId);
		keyList.add(Integer.toString(offset));
		keyList.add(Integer.toString(limit));
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