/**
 * 
 */
package com.djt.social.cache;

import java.util.List;

import com.djt.social.model.SocialAlbum;
import com.djt.social.model.SocialPhoto;
import com.djt.social.model.SocialProfile;


/**
 * 
 * @author Tom.Myers
 *
 */
public interface ISocialMediaQueryCache {

	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @return
	 */
	List<SocialAlbum> getSocialPhotoAlbums(
		String promotionDeployPath, 
		String providerId, 
		String accessToken);
	
	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param socialAlbums
	 */
	void putSocialPhotoAlbums(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		List<SocialAlbum> socialAlbums);

	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 */
	void removeSocialPhotoAlbums(
		String promotionDeployPath, 
		String providerId, 
		String accessToken);
	
	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param albumId
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<SocialPhoto> getSocialPhotosFromSocialPhotoAlbum(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		String albumId,
		int offset,
		int limit);
	
	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param albumId
	 * @param offset
	 * @param limit
	 * @param socialPhotos
	 */
	void putSocialPhotosFromSocialPhotoAlbum(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		String albumId,
		int offset,
		int limit,
		List<SocialPhoto> socialPhotos);
	
	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param albumId
	 */
	void removeSocialPhotosFromSocialPhotoAlbum(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		String albumId);
	
	/**
	 * For those providers with no concept of "album"
	 *  
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @return
	 */
	List<SocialPhoto> getSocialPhotos(
		String promotionDeployPath, 
		String providerId, 
		String accessToken);
	
	/**
	 * For those providers with no concept of "album"
	 *  
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param socialPhotos
	 */
	void putSocialPhotos(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		List<SocialPhoto> socialPhotos);

	/**
	 * For those providers with no concept of "album"
	 *  
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 */
	void removeSocialPhotos(
		String promotionDeployPath, 
		String providerId, 
		String accessToken);
	
	/**
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @return
	 */
	SocialProfile getSocialProfile(
		String promotionDeployPath, 
		String providerId, 
		String accessToken);
	
	/**
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param socialProfile
	 */
	void putSocialProfile(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		SocialProfile socialProfile);
	
	/**
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 */
	void removeSocialProfile(
		String promotionDeployPath, 
		String providerId, 
		String accessToken);
	
	/**
	 */
	void removeAll();	
}