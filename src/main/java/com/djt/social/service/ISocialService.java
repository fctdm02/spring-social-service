/**
 * 
 */
package com.djt.social.service;

import javax.jws.WebService;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.djt.social.model.AccessTokenResponse;
import com.djt.social.model.InstallApiKeyResponse;
import com.djt.social.model.SocialAlbumsResponse;
import com.djt.social.model.SocialFriendsResponse;
import com.djt.social.model.SocialPhotosResponse;
import com.djt.social.model.SocialProfileResponse;
import com.djt.social.model.SocialVideosResponse;
import com.djt.social.model.StatusResponse;
import com.djt.social.model.VersionResponse;

/**
 * This service performs interactions with the various "Software as a Service"
 * (Saas) social media providers and presents the caller with a normalized object model and service interface.
 * </p>
 * Since not all social media services provide exactly the same information, when a particular attribute
 * or method on this service API does not pertain to a particular service provider, the service may 
 * return an empty string or throw a <code>NotSupportedOperationException</code> accordingly.  In the case of
 * the latter, there may exist a "isOperationSuchAndSuchSupported()" method.
 * </p>
 * The required permissions for interaction are listed after the provider name:
 * <ul>
 *   <li>Facebook: "user_photos" or "friends_photos"</li>
 *   <li>Flickr: "read"</li>
 *   <li>Instagram: </li>
 *   <li>Photobucket </li>
 *   <li>Google/Youtube: </li>
 *   <li>Foursquare: </li>
 *   <li>Twitter: </li>
 * </ul>
 * 
 * @author Tom.Myers
 *
 */
@WebService
@Path("/")
@Produces({"application/json"})
public interface ISocialService {
	
	/**
	 * Used for URL encoding/decoding REST parameters.
	 */
	String DEFAULT_URL_ENCODING_CHARSET = "UTF-8";
	
	/**
	 * Used for overriding the default page size for retrieving lists of social albums, social photos and social videos.
	 * The minimum value is <code>MINIMUM_PAGE_SIZE</code> and the maximum value is <code>MAXIMUM_PAGE_SIZE</code>. 
	 */
	String SOCIAL_SERVICE_DEFAULT_PAGE_SIZE_SYSTEM_PROPERTY_NAME = "com.djt.social.service.default.page.size";
	
	
	
	/**
	 *  Specifies the absolute smallest page size that can be requested.
	 */
	int MINIMUM_PAGE_SIZE = 5; // TODO: TDM: BUSINESS QUESTION: Have the business validate this.
	
	/**
	 * Specifies the absolute largest page size that can be requested.
	 */
	int MAXIMUM_PAGE_SIZE = 50; // TODO: TDM: BUSINESS QUESTION: Have the business validate this.
	
	/** 
	 * This can be overridden by setting the <code>PAGE_SYSTEM_SYSTEM_PROPERTY_NAME</code> system property. 
	 */
	int DEFAULT_PAGE_SIZE = 25; // TODO: TDM: BUSINESS QUESTION: Have the business validate this.
	
	
	
	/** */
	String PROVIDER_NAME_FACEBOOK = "facebook";

	/** */
	String PROVIDER_NAME_FLICKR = "flickr";

	/** */
	String PROVIDER_NAME_INSTAGRAM = "instagram";

	/** */
	String PROVIDER_NAME_PHOTOBUCKET = "photobucket";

	/** */
	String PROVIDER_NAME_YOUTUBE = "youtube";

	/** */
	String PROVIDER_NAME_FOURSQUARE = "foursquare";

	/** */
	String PROVIDER_NAME_TWITTER = "twitter";

	
	
	/** Used for monitoring */
	String FACEBOOK_PROVIDER_AUTHORIZE_ENDPOINT = "https://www.facebook.com/dialog/oauth";

	/** Used for monitoring */
	String INSTAGRAM_PROVIDER_AUTHORIZE_ENDPOINT = "https://api.instagram.com/oauth/authorize";
	
	/** Used for monitoring */
	String FLICKR_PROVIDER_AUTHORIZE_ENDPOINT = "http://www.flickr.com/services/oauth/authorize";
	

	
	/** Used for monitoring */
	String PROVIDER_ENDPOINT_SCHEME_HTTP = "http";
	
	/** Used for monitoring */
	String PROVIDER_ENDPOINT_SCHEME_HTTPS = "https";
	
	/** Used for monitoring */
	int PROVIDER_ENDPOINT_DEFAULT_PORT = -1; 
	
	
	
	/** */
	String GET_SOCIAL_PROFILE_REST_SIGNATURE = "/profile";

	
	
	/** */
	String GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE = "/photoalbums";
	
	/** */
	String GET_SOCIAL_PHOTOS_FROM_SOCIAL_PHOTO_ALBUM_REST_SIGNATURE = "/photos";
	
	
	
	/** */
	String GET_SOCIAL_VIDEO_ALBUMS_REST_SIGNATURE = "/videoalbums";
	
	/** */
	String GET_SOCIAL_VIDEOS_FROM_SOCIAL_VIDEO_ALBUM_REST_SIGNATURE = "/videos";
	
	
	
	/** */
	String GET_SOCIAL_FRIENDS_REST_SIGNATURE = "/friends";

	
	
	/** */
	String INSTALL_SOCIAL_PROVIDER_API_KEY_FOR_PROMOTION_REST_SIGNATURE = "/install";
	
	/** */
	String GET_STORED_ACCESS_TOKEN_REST_SIGNATURE = "/accesstoken";

	/** */
	String DELETE_STORED_ACCESS_TOKEN_REST_SIGNATURE = "/deleteaccesstoken";
	
	/** */
	String GET_STATUS_REST_SIGNATURE = "/status";

	/** */
	String GET_VERSION_REST_SIGNATURE = "/version";
	
	/** */
	String CACHE_REMOVE_ALBUM_REST_SIGNATURE = "/cacheremovealbum";

	/** */
	String CACHE_REMOVE_ALBUM_PHOTOS_REST_SIGNATURE = "/cacheremovealbumphotos";
	
	/** */
	String CACHE_REMOVE_PHOTOS_REST_SIGNATURE = "/cacheremovephotos";

	/** */
	String CACHE_REMOVE_PROFILE_REST_SIGNATURE = "/cacheremoveprofile";

	/** */
	String CACHE_REMOVE_ALL_REST_SIGNATURE = "/cacheremoveall";
	
	
	/** */
	String PROVIDER_ID = "providerId";

	/** */
	String ID = "id";
	
	/** */
	String OFFSET = "offset";
	
	/** */
	String LIMIT = "limit";
	
	/** */
	String ACCESS_TOKEN = "accessToken";
	
	/**
	 * Only needed for OAuth1 service providers (e.g. Flickr/Twitter) 
	 */
	String ACCESS_TOKEN_SECRET = "accessTokenSecret";

	/** */
	String SOURCE_URL = "sourceUrl";
	
	/** */
	String TARGET_FILEPATH = "targetFilepath";

	/** */
	String PROMOTION_DEPLOY_PATH = "promotionDeployPath";
	
	/** */
	String API_KEY = "apiKey";
	
	/** */
	String API_KEY_SECRET = "apiKeySecret";

	
	/**
	 * @param promotionDeployPath The deploy path of the promotion that the user with the given accessToken is interacting with
	 * @param providerId
	 * @param accessToken
	 * @param accessTokenSecret
	 * @return
	 */
	@GET
	@Path(GET_SOCIAL_PROFILE_REST_SIGNATURE)
	SocialProfileResponse getSocialProfile(
		@QueryParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath,
		@QueryParam(PROVIDER_ID) String providerId,
		@QueryParam(ACCESS_TOKEN) String accessToken,
		@QueryParam(ACCESS_TOKEN_SECRET) String accessTokenSecret);
	
	/**
	 * @param promotionDeployPath The deploy path of the promotion that the user with the given accessToken is interacting with
	 * @param providerId The name of the service provider to interact with.  
	 * @param offset the offset into the list of albums
	 * @param limit the maximum number of albums to return
	 * @param accessToken The access token associated with a user's current connection with a social media site
	 * @param accessToken The access token secret associated with a user's current connection with a social media site
	 * @return
	 */
	@GET
    @Path(GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE)
	SocialAlbumsResponse getSocialPhotoAlbums(
		@QueryParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath,
		@QueryParam(PROVIDER_ID) String providerId,
		@QueryParam(OFFSET) int offset, 
		@QueryParam(LIMIT) int limit,
		@QueryParam(ACCESS_TOKEN) String accessToken,
		@QueryParam(ACCESS_TOKEN_SECRET) String accessTokenSecret);

	/**
	 * @param promotionDeployPath The deploy path of the promotion that the user with the given accessToken is interacting with
	 * @param providerId The name of the service provider to interact with
	 * @param id the album ID
	 * @param offset the offset into the list of photos
	 * @param limit the maximum number of photos to return
	 * @param accessToken The access token associated with a user's current connection with a social media site
	 * @param accessToken The access token secret associated with a user's current connection with a social media site
	 * @return 
	 */
    @GET
    @Path(GET_SOCIAL_PHOTOS_FROM_SOCIAL_PHOTO_ALBUM_REST_SIGNATURE)
	SocialPhotosResponse getSocialPhotosFromSocialPhotoAlbum(
		@QueryParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath,
		@QueryParam(PROVIDER_ID) String providerId,	
		@QueryParam(ID) String id, 
		@QueryParam(OFFSET) int offset, 
		@QueryParam(LIMIT) int limit,
		@QueryParam(ACCESS_TOKEN) String accessToken,
		@QueryParam(ACCESS_TOKEN_SECRET) String accessTokenSecret);		

	/**
	 * @param promotionDeployPath The deploy path of the promotion that the user with the given accessToken is interacting with
	 * @param providerId The name of the service provider to interact with.  
	 * @param offset the offset into the list of albums
	 * @param limit the maximum number of albums to return
	 * @param accessToken The access token associated with a user's current connection with a social media site
	 * @param accessToken The access token secret associated with a user's current connection with a social media site
	 * @return 
	 */
    @GET
    @Path(GET_SOCIAL_VIDEO_ALBUMS_REST_SIGNATURE)
	SocialAlbumsResponse getSocialVideoAlbums(
		@QueryParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath,
		@QueryParam(PROVIDER_ID) String providerId,
		@QueryParam(OFFSET) int offset, 
		@QueryParam(LIMIT) int limit,
		@QueryParam(ACCESS_TOKEN) String accessToken,
		@QueryParam(ACCESS_TOKEN_SECRET) String accessTokenSecret);

	/**
	 * @param promotionDeployPath The deploy path of the promotion that the user with the given accessToken is interacting with
	 * @param providerId The name of the service provider to interact with
	 * @param id the album ID
	 * @param offset the offset into the list of videos
	 * @param limit the maximum number of videos to return
	 * @param accessToken The access token associated with a user's current connection with a social media site
	 * @param accessToken The access token secret associated with a user's current connection with a social media site
	 * @return 
	 */
    @GET
    @Path(GET_SOCIAL_VIDEOS_FROM_SOCIAL_VIDEO_ALBUM_REST_SIGNATURE)
	SocialVideosResponse getSocialVideosFromSocialVideoAlbum(
		@QueryParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath,
		@QueryParam(PROVIDER_ID) String providerId,	
		@QueryParam(ID) String id, 
		@QueryParam(OFFSET) int offset, 
		@QueryParam(LIMIT) int limit,
		@QueryParam(ACCESS_TOKEN) String accessToken,
		@QueryParam(ACCESS_TOKEN_SECRET) String accessTokenSecret);
		
	/**
	 * @param promotionDeployPath The deploy path of the promotion that the user with the given accessToken is interacting with
	 * @param providerId The name of the service provider to interact with
	 * @param id the social user's ID with which to retrieve friends of
	 * @param offset the offset into the list of friends
	 * @param limit the maximum number of friends to return
	 * @param accessToken The access token associated with a user's current connection with a social media site
	 * @param accessToken The access token secret associated with a user's current connection with a social media site
	 * @return 
	 */
    @GET
    @Path(GET_SOCIAL_FRIENDS_REST_SIGNATURE)
    SocialFriendsResponse getSocialFriends(
    	@QueryParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath,
    	@QueryParam(PROVIDER_ID) String providerId,
		@QueryParam(ID) String id, 
		@QueryParam(OFFSET) int offset, 
		@QueryParam(LIMIT) int limit,
		@QueryParam(ACCESS_TOKEN) String accessToken,
		@QueryParam(ACCESS_TOKEN_SECRET) String accessTokenSecret);
    
    /**
     * @param promotionDeployPath The deploy path of the promotion that the user with the given accessToken is interacting with
     * @param providerId
     * @param apiKey
     * @param apiKeySecret
     * @return
     */
    @POST
    @Path(INSTALL_SOCIAL_PROVIDER_API_KEY_FOR_PROMOTION_REST_SIGNATURE)
    InstallApiKeyResponse installSocialProviderApiKeyForPromotion(
    	@FormParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath,
    	@FormParam(PROVIDER_ID) String providerId,
    	@FormParam(API_KEY) String apiKey,
    	@FormParam(API_KEY_SECRET) String apiKeySecret); 

    /**
     * @param promotionDeployPath The deploy path of the promotion that the user with the given accessToken is interacting with
     * @param providerId The name of the provider (e.g. "facebook", "flickr", "instagram", etc.)
     * @param id The unique identifier (a.k.a. uid) as known to TB2 of the end-user
     * @return
     */
    @GET
    @Path(GET_STORED_ACCESS_TOKEN_REST_SIGNATURE)
    AccessTokenResponse getStoredAccessToken(
    	@QueryParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath,
    	@QueryParam(PROVIDER_ID) String providerId,
    	@QueryParam(ID) String id);

    /**
     * 
     * @param promotionDeployPath
     * @param providerId
     * @param id
     * @return
     */
    @POST
    @Path(DELETE_STORED_ACCESS_TOKEN_REST_SIGNATURE)
    StatusResponse deleteStoredAccessToken(
    	@FormParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath,
    	@FormParam(PROVIDER_ID) String providerId,
    	@FormParam(ID) String id);
    		
    /**
     * 
     * @return
     */
    @GET
    @Path(GET_STATUS_REST_SIGNATURE)
    StatusResponse getStatus();
    
    /**
     * 
     * @return
     */
    @GET
    @Path(GET_VERSION_REST_SIGNATURE)
    VersionResponse getVersion();
    
	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 */
    @GET
    @Path(CACHE_REMOVE_ALBUM_REST_SIGNATURE)
	void removeSocialPhotoAlbumsFromCache(
		@QueryParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath, 
		@QueryParam(PROVIDER_ID) String providerId, 
		@QueryParam(ACCESS_TOKEN) String accessToken);
	
	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param albumId
	 */
    @GET
    @Path(CACHE_REMOVE_ALBUM_PHOTOS_REST_SIGNATURE)
    void removeSocialPhotosFromSocialPhotoAlbumFromCache(
		@QueryParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath, 
		@QueryParam(PROVIDER_ID) String providerId, 
		@QueryParam(ACCESS_TOKEN) String accessToken,
		@QueryParam(ID) String albumId);
	
	/**
	 * For those providers with no concept of "album"
	 *  
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 */
    @GET
    @Path(CACHE_REMOVE_PHOTOS_REST_SIGNATURE)
	void removeSocialPhotosFromCache(
		@QueryParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath, 
		@QueryParam(PROVIDER_ID) String providerId, 
		@QueryParam(ACCESS_TOKEN) String accessToken);

	/**
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 */
    @GET
    @Path(CACHE_REMOVE_PROFILE_REST_SIGNATURE)
	void removeSocialProfileFromCache(
		@QueryParam(PROMOTION_DEPLOY_PATH) String promotionDeployPath, 
		@QueryParam(PROVIDER_ID) String providerId, 
		@QueryParam(ACCESS_TOKEN) String accessToken);
    
	/**
	 */
    @GET
    @Path(CACHE_REMOVE_ALL_REST_SIGNATURE)
	void removeAllFromCache();
}