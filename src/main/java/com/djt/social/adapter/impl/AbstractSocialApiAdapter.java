package com.djt.social.adapter.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;

import com.djt.social.adapter.ISocialApiAdapter;
import com.djt.social.cache.ISocialMediaQueryCache;
import com.djt.social.model.SocialAlbum;
import com.djt.social.model.SocialAlbumsResponse;
import com.djt.social.model.SocialFriendsResponse;
import com.djt.social.model.SocialPhoto;
import com.djt.social.model.SocialPhotosResponse;
import com.djt.social.model.SocialProfile;
import com.djt.social.model.SocialVideo;
import com.djt.social.model.SocialVideosResponse;

/**
 * 
 * @author Tom.Myers
 */
public abstract class AbstractSocialApiAdapter implements ISocialApiAdapter {

	/* */
	private static final String UNUSED = "unused";
	
	/* */
	private static final String UTF_8 = "UTF-8";

	/* */
	private static final String SHA_1 = "SHA-1";
	
	/* */
	private String promotionDeployPath;
	
	/* */
	private String providerId;

	/* */
	private String accessToken;
	
	/* */
	private ISocialMediaQueryCache socialMediaQueryCache;
	
	/* */
	private String socialUrlSalt;
	
	
	/**
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param socialMediaQueryCache
	 * @param socialUrlSalt
	 */
	public AbstractSocialApiAdapter(
		String promotionDeployPath,
		String providerId,
		String accessToken,
		ISocialMediaQueryCache socialMediaQueryCache,
		String socialUrlSalt) {
		
		this.promotionDeployPath = promotionDeployPath;
		this.providerId = providerId;
		this.accessToken = accessToken;
		this.socialMediaQueryCache = socialMediaQueryCache;
		this.socialUrlSalt = socialUrlSalt;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.adapter.ISocialApiAdapter#getPromotionDeployPath()
	 */
	public final String getPromotionDeployPath() {
		return this.promotionDeployPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.adapter.ISocialApiAdapter#getProviderId()
	 */
	public final String getProviderId() {
		return this.providerId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.adapter.ISocialApiAdapter#getAccessToken()
	 */
	public final String getAccessToken() {
		return this.accessToken;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.adapter.ISocialApiAdapter#getSocialMediaQueryCache()
	 */
	public ISocialMediaQueryCache getSocialMediaQueryCache() {
		return socialMediaQueryCache;
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialApiAdapter#getSocialPhotoAlbums()
	 */
	public SocialAlbumsResponse getSocialPhotoAlbums() {

		return buildSocialPhotoAlbums();
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialApiAdapter#getSocialPhotoAlbums(int, int)
	 */
	@SuppressWarnings(UNUSED)
	public SocialAlbumsResponse getSocialPhotoAlbums(
		int offset, 
		int limit) {

		return buildSocialPhotoAlbums();
	}

	/*
	 * 
	 * @return
	 */
	private SocialAlbumsResponse buildSocialPhotoAlbums() {

		List<SocialAlbum> socialAlbums = new ArrayList<SocialAlbum>();
		
		SocialAlbum defaultPhotoAlbum = new SocialAlbum(
			DEFAULT_ID, 
			DEFAULT_PHOTO_ALBUM_NAME, 
			DEFAULT_LINK);
		socialAlbums.add(defaultPhotoAlbum);
		
		return new SocialAlbumsResponse("", socialAlbums.size(), socialAlbums);
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialApiAdapter#getSocialPhotosFromSocialPhotoAlbum(java.lang.String, int, int)
	 */
	@SuppressWarnings(UNUSED)
	public SocialPhotosResponse getSocialPhotosFromSocialPhotoAlbum(
		String id, 
		int offset, 
		int limit) {

		return new SocialPhotosResponse("", 0, new ArrayList<SocialPhoto>());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialApiAdapter#getSocialVideoAlbums()
	 */
	public SocialAlbumsResponse getSocialVideoAlbums()  {

		return buildSocialVideoAlbums();
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialApiAdapter#getSocialVideoAlbums(int, int)
	 */
	@SuppressWarnings(UNUSED)
	public SocialAlbumsResponse getSocialVideoAlbums(
		int offset, 
		int limit) {

		return buildSocialVideoAlbums();
	}

	/*
	 * 
	 * @return
	 */
	private SocialAlbumsResponse buildSocialVideoAlbums() {

		List<SocialAlbum> socialAlbums = new ArrayList<SocialAlbum>();
		
		SocialAlbum defaultVideoAlbum = new SocialAlbum(
			DEFAULT_ID, 
			DEFAULT_VIDEO_ALBUM_NAME, 
			DEFAULT_LINK);
		socialAlbums.add(defaultVideoAlbum);
		
		return new SocialAlbumsResponse("", socialAlbums.size(), socialAlbums);
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialApiAdapter#getSocialVideosFromSocialVideoAlbum(java.lang.String, int, int)
	 */
	@SuppressWarnings(UNUSED)
	public SocialVideosResponse getSocialVideosFromSocialVideoAlbum(
		String id, 
		int offset, 
		int limit) {

		return new SocialVideosResponse("", 0, new ArrayList<SocialVideo>());
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.adapter.ISocialApiAdapter#getSocialFriends(java.lang.String, int, int)
	 */
	@SuppressWarnings(UNUSED)
	public SocialFriendsResponse getSocialFriends(
			String id,
			int offset, 
			int limit) {
		
		return buildSocialFriends();
	}
	
	/*
	 * 
	 * @return
	 */
	private SocialFriendsResponse buildSocialFriends() {

		return new SocialFriendsResponse("", 0, new ArrayList<SocialProfile>());
	}

	/*
	 * 
	 * @param url
	 * @return
	 */
	protected String buildSocialMediaUrlChecksum(String url) {
		
		return buildSocialMediaUrlChecksum(url, this.socialUrlSalt);
	}

	/**
	 * 
	 * @param url
	 * @param salt
	 * @return
	 */
	public static String buildSocialMediaUrlChecksum(String url, String salt) {
		
		String errorMessage = "Unable to compute checksum for social media url: [" + url + "], error: ";
		try {
		
			byte[] saltByteArray = salt.getBytes(UTF_8);
			MessageDigest messageDigest = MessageDigest.getInstance(SHA_1);

		    // Update digest object with byte array of clear text string and salt
			messageDigest.reset();
			messageDigest.update(url.getBytes(UTF_8));
			messageDigest.update(saltByteArray);
			
			// Complete hash computation, this results in binary data
			byte[] checksumByteArray = messageDigest.digest();
			
			return Hex.encodeHexString(checksumByteArray);
						
		} catch (UnsupportedEncodingException uee) {
			throw new IllegalStateException(errorMessage + uee.getMessage(), uee);
		} catch (NoSuchAlgorithmException nsae) {
			throw new IllegalStateException(errorMessage + nsae.getMessage(), nsae);
		}
	}
}