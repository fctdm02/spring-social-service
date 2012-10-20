/**
 * 
 */
package com.djt.social.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.djt.social.model.AccessTokenResponse;
import com.djt.social.model.InstallApiKeyResponse;
import com.djt.social.model.SocialAlbumsResponse;
import com.djt.social.model.SocialConnection;
import com.djt.social.model.SocialFriendsResponse;
import com.djt.social.model.SocialPhotosResponse;
import com.djt.social.model.SocialProfileResponse;
import com.djt.social.model.SocialVideosResponse;
import com.djt.social.model.StatusResponse;
import com.djt.social.model.VersionResponse;
import com.djt.social.service.ISocialService;

/**
 * 
 * @author Tom.Myers
 *
 */
@Controller
@RequestMapping("/operations")
public class OperationsController implements ApplicationContextAware {

	/* */
	private static final String HOME = "home";

	/* */
	private static final String INSTALL_RESPONSE = "installResponse";
	
	/* */
	private static final String ACCESS_TOKEN_RESPONSE = "accessTokenResponse";

	/* */
	private static final String STATUS_RESPONSE = "statusResponse";

	/* */
	private static final String VERSION_RESPONSE = "versionResponse";
	
	/* */
	private static final String PROFILE = "profile";
	
	/* */
	private static final String PROFILE_RESPONSE = "profileResponse";
	
	/* */
	private static final String ALBUMS_RESPONSE = "albumsResponse";

	/* */
	private static final String PHOTO_ALBUMS = "photoalbums";
	
	/* */
	private static final String VIDEO_ALBUMS = "videoalbums";

	/* */
	private static final String PHOTOS_RESPONSE = "photosResponse";
	
	/* */
	private static final String PHOTOS = "photos";

	/* */
	private static final String VIDEOS_RESPONSE = "videosResponse";
	
	/* */
	private static final String VIDEOS = "videos";

	/* */
	private static final String FRIENDS_RESPONSE = "friendsResponse";
	
	/* */
	private static final String FRIENDS = "friends";

	/* */
	private static final String SOCIAL_CONNECTION = "socialConnection";
	
	/* */
	private static final String PROVIDER_ID = "providerId";

	/* */
	private ApplicationContext applicationContext;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
	
		this.applicationContext = applicationContext;
	}
	
	/**
	 * 
	 * @return
	 */
	private ISocialService getSocialService() {
		
		return (ISocialService)this.applicationContext.getBean("socialServiceProxy");
	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value="/home", method = RequestMethod.GET)
	public String home() {
		return HOME;
	}

	/**
	 * 
	 * @param socialConnection
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/install")
	public String installSocialProviderApiKeyForPromotion(SocialConnection socialConnection, Model model) {
		
		InstallApiKeyResponse installApiKeyResponse = getSocialService().installSocialProviderApiKeyForPromotion(
			socialConnection.getPromotionDeployPath(), 
			socialConnection.getProviderId(), 
			socialConnection.getApiKey(), 
			socialConnection.getApiKeySecret());

		model.addAttribute(INSTALL_RESPONSE, installApiKeyResponse);
		model.addAttribute(SOCIAL_CONNECTION, socialConnection);
		
		return INSTALL_RESPONSE;
	}
	
	/**
	 * 
	 * @param socialConnection
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/accesstoken")
	public String getStoredAccessToken(SocialConnection socialConnection, Model model) {
		
		AccessTokenResponse accessTokenResponse = getSocialService().getStoredAccessToken(
			socialConnection.getPromotionDeployPath(),	
			socialConnection.getProviderId(), 
			socialConnection.getId());

		model.addAttribute(ACCESS_TOKEN_RESPONSE, accessTokenResponse);
		model.addAttribute(SOCIAL_CONNECTION, socialConnection);
		
		return ACCESS_TOKEN_RESPONSE;
	}

	/**
	 * 
	 * @param socialConnection
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/deleteaccesstoken")
	public String deleteStoredAccessToken(SocialConnection socialConnection, Model model) {
		
		StatusResponse statusResponse = getSocialService().deleteStoredAccessToken(
			socialConnection.getPromotionDeployPath(),	
			socialConnection.getProviderId(), 
			socialConnection.getId());

		model.addAttribute(STATUS_RESPONSE, statusResponse);
		model.addAttribute(SOCIAL_CONNECTION, socialConnection);
		
		return STATUS_RESPONSE;
	}
	
	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/status")
	public String getStatus(Model model) {
		
		StatusResponse statusResponse = getSocialService().getStatus();

		model.addAttribute(STATUS_RESPONSE, statusResponse);
		
		return STATUS_RESPONSE;
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/version")
	public String getVersion(Model model) {
		
		VersionResponse versionResponse = getSocialService().getVersion();

		model.addAttribute(VERSION_RESPONSE, versionResponse);
		
		return VERSION_RESPONSE;
	}
	
	/**
	 * 
	 * @param socialConnection
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/profile")
	public String getProfile(SocialConnection socialConnection, Model model) {
		
		SocialProfileResponse profileResponse = getSocialService().getSocialProfile(
			socialConnection.getPromotionDeployPath(),	
			socialConnection.getProviderId(), 
			socialConnection.getAccessToken(), 
			socialConnection.getAccessTokenSecret());

		model.addAttribute(PROFILE_RESPONSE, profileResponse);
		model.addAttribute(SOCIAL_CONNECTION, socialConnection);
		
		return PROFILE;
	}
	
	/**
	 * 
	 * @param socialConnection
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/photoalbums")
	public String getPhotoAlbums(SocialConnection socialConnection, Model model) {
		
		SocialAlbumsResponse albumsResponse = getSocialService().getSocialPhotoAlbums(
			socialConnection.getPromotionDeployPath(),
			socialConnection.getProviderId(), 
			socialConnection.getOffset(), 
			socialConnection.getLimit(), 
			socialConnection.getAccessToken(), 
			socialConnection.getAccessTokenSecret());

		model.addAttribute(ALBUMS_RESPONSE, albumsResponse);
		model.addAttribute(SOCIAL_CONNECTION, socialConnection);
		
		return PHOTO_ALBUMS;
	}
	
	/**
	 * 
	 * @param socialConnection
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/photos")
	public String getPhotosFromAlbum(SocialConnection socialConnection, Model model) {
		
		SocialPhotosResponse photosResponse = getSocialService().getSocialPhotosFromSocialPhotoAlbum(
			socialConnection.getPromotionDeployPath(),
			socialConnection.getProviderId(), 
			socialConnection.getId(), 
			socialConnection.getOffset(), 
			socialConnection.getLimit(), 
			socialConnection.getAccessToken(), 
			socialConnection.getAccessTokenSecret());

		model.addAttribute(PHOTOS_RESPONSE, photosResponse);
		model.addAttribute(SOCIAL_CONNECTION, socialConnection);
		
		return PHOTOS;
	}
	
	/**
	 * 
	 * @param socialConnection
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/videoalbums")
	public String getVideoAlbums(SocialConnection socialConnection, Model model) {
		
		SocialAlbumsResponse socialAlbumsResponse = getSocialService().getSocialVideoAlbums(
			socialConnection.getPromotionDeployPath(),
			socialConnection.getProviderId(), 
			socialConnection.getOffset(), 
			socialConnection.getLimit(), 
			socialConnection.getAccessToken(), 
			socialConnection.getAccessTokenSecret());

		model.addAttribute(ALBUMS_RESPONSE, socialAlbumsResponse);
		model.addAttribute(SOCIAL_CONNECTION, socialConnection);
		
		return VIDEO_ALBUMS;
	}
	
	/**
	 * 
	 * @param socialConnection
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/videos")
	public String getVideosFromAlbum(SocialConnection socialConnection, Model model) {
		
		SocialVideosResponse videosResponse = getSocialService().getSocialVideosFromSocialVideoAlbum(
			socialConnection.getPromotionDeployPath(),
			socialConnection.getProviderId(), 
			socialConnection.getId(), 
			socialConnection.getOffset(), 
			socialConnection.getLimit(), 
			socialConnection.getAccessToken(), 
			socialConnection.getAccessTokenSecret());

		model.addAttribute(VIDEOS_RESPONSE, videosResponse);
		model.addAttribute(SOCIAL_CONNECTION, socialConnection);
		
		return VIDEOS;
	}
	
	/**
	 * 
	 * @param socialConnection
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/friends")
	public String getFriends(SocialConnection socialConnection, Model model) {
		
		SocialFriendsResponse friendsResponse = getSocialService().getSocialFriends(
			socialConnection.getPromotionDeployPath(),
			socialConnection.getProviderId(), 
			socialConnection.getId(), 
			socialConnection.getOffset(), 
			socialConnection.getLimit(), 
			socialConnection.getAccessToken(), 
			socialConnection.getAccessTokenSecret());

		model.addAttribute(FRIENDS_RESPONSE, friendsResponse);
		model.addAttribute(SOCIAL_CONNECTION, socialConnection);
		model.addAttribute(PROVIDER_ID, socialConnection.getProviderId());
		
		return FRIENDS;
	}
	
	/**
	 * 
	 * @param socialConnection
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/connectionstatus")
	public String getConnectionStatus(SocialConnection socialConnection, Model model) {
		
		model.addAttribute(SOCIAL_CONNECTION, socialConnection);
		return "status";
	}
}