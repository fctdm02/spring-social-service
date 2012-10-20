/**
 * 
 */
package com.djt.social.cache;

import com.djt.social.model.AccessTokenResponse;

/**
 * 
 * @author Tom.Myers
 *
 */
public interface IAccessTokenCache {
	
	/**
	 * @param promotionDeployPath
	 * @param providerId
	 * @param uid
	 * @return
	 */
	AccessTokenResponse getAccessTokenResponse(
		String promotionDeployPath, 
		String providerId, 
		String uid);
	
	/**
	 * @param promotionDeployPath
	 * @param providerId
	 * @param uid
	 * @param accessTokenResponse
	 */
	void putAccessTokenResponse(
		String promotionDeployPath, 
		String providerId, 
		String uid, 
		AccessTokenResponse accessTokenResponse);
	
	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param uid
	 */
	void removeAccessTokenResponse(
		String promotionDeployPath, 
		String providerId, 
		String uid);			
	
	/**
	 * 
	 * @return
	 */
	boolean getEnableAccessTokenPersistence();
	
	/**
	 */
	void removeAll();	
}