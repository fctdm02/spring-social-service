/**
 * 
 */
package com.djt.social.model;

import org.springframework.social.connect.ConnectionData;

/**
 * 
 * @author Tom.Myers
 *
 */
public final class SocialConnectionData extends ConnectionData {
	
	/* */
	private static final long serialVersionUID = 1L;
	
	/* */
	private String promotionDeployPath;

	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param providerUserId
	 * @param displayName
	 * @param profileUrl
	 * @param imageUrl
	 * @param accessToken
	 * @param secret
	 * @param refreshToken
	 * @param expireTime
	 */
	public SocialConnectionData(
		String promotionDeployPath,
		String providerId, 
		String providerUserId, 
		String displayName, 
		String profileUrl, 
		String imageUrl, 
		String accessToken, 
		String secret, 
		String refreshToken, 
		Long expireTime) {
		
		super(
			providerId, 
			providerUserId,
			displayName,
			profileUrl,
			imageUrl,
			accessToken,
			secret,
			refreshToken,
			expireTime);
		
		this.promotionDeployPath = promotionDeployPath;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPromotionDeployPath() {
		return this.promotionDeployPath;
	}
}