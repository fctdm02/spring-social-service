/**
 * 
 */
package com.djt.social.adapter;

import com.djt.social.cache.ISocialMediaQueryCache;
import com.djt.social.model.SocialAlbumsResponse;
import com.djt.social.model.SocialFriendsResponse;
import com.djt.social.model.SocialPhotosResponse;
import com.djt.social.model.SocialProfileResponse;
import com.djt.social.model.SocialVideosResponse;

/**
 * 
 * @author Tom.Myers
 *
 */
public interface ISocialApiAdapter {
	
	/** */
	String DEFAULT_ID = "0"; 
	
	/** */
	String DEFAULT_PHOTO_ALBUM_NAME = "Default Photo Album"; 

	/** */
	String DEFAULT_VIDEO_ALBUM_NAME = "Default Video Album"; 
	
	/** */
	String DEFAULT_LINK = "";
	
	/**
	 * 
	 * @return
	 */
	String getPromotionDeployPath();
	
	/**
	 * 
	 * @return
	 */
	String getProviderId(); 

	/**
	 * 
	 * @return
	 */
	String getAccessToken(); 
	
	/**
	 * 
	 * @return
	 */
	ISocialMediaQueryCache getSocialMediaQueryCache();	
	
	/**
	 * 
	 * @return
	 */
	SocialProfileResponse getSocialProfile();

	/**
	 * 
	 * @return
	 */
	SocialAlbumsResponse getSocialPhotoAlbums();
	
	/**
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	SocialAlbumsResponse getSocialPhotoAlbums(
		int offset, 
		int limit);
	
	/**
	 * 
	 * @param id
	 * @param offset
	 * @param limit
	 * @return
	 */
	SocialPhotosResponse getSocialPhotosFromSocialPhotoAlbum(
		String id, 
		int offset, 
		int limit);

	/**
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	SocialAlbumsResponse getSocialVideoAlbums();
	
	/**
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	SocialAlbumsResponse getSocialVideoAlbums(
		int offset, 
		int limit);
	
	/**
	 * 
	 * @param id
	 * @param offset
	 * @param limit
	 * @return
	 */
	SocialVideosResponse getSocialVideosFromSocialVideoAlbum(
		String id, 
		int offset, 
		int limit);

	/**
	 * @param id The id of the user with whom to request friends/followers of (needed for instagram only)
	 * @param offset
	 * @param limit
	 * @return
	 */
	SocialFriendsResponse getSocialFriends(
		String id,
		int offset, 
		int limit);
	
	// TODO: Add get photo albums of friend
}