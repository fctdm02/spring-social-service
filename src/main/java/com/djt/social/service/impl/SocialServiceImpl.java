/**
 * 
 */
package com.djt.social.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jws.WebService;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.flickr.api.Flickr;
import org.springframework.social.flickr.api.impl.FlickrTemplate;
import org.springframework.social.instagram.api.Instagram;
import org.springframework.social.instagram.api.impl.InstagramTemplate;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;

import com.djt.social.adapter.ISocialApiAdapter;
import com.djt.social.adapter.impl.FacebookSocialApiAdapter;
import com.djt.social.adapter.impl.FlickrSocialApiAdapter;
import com.djt.social.adapter.impl.InstagramSocialApiAdapter;
import com.djt.social.adapter.impl.TwitterSocialApiAdapter;
import com.djt.social.cache.IAccessTokenCache;
import com.djt.social.cache.ISocialMediaQueryCache;
import com.djt.social.config.EnvironmentPropertyPlaceholderConfigurer;
import com.djt.social.event.ISocialEventHelper;
import com.djt.social.event.ISocialEventListener;
import com.djt.social.event.ISocialEventProducer;
import com.djt.social.event.SocialEvent;
import com.djt.social.model.AccessTokenResponse;
import com.djt.social.model.InstallApiKeyResponse;
import com.djt.social.model.SocialAlbumsResponse;
import com.djt.social.model.SocialFriendsResponse;
import com.djt.social.model.SocialPhotosResponse;
import com.djt.social.model.SocialProfileResponse;
import com.djt.social.model.SocialVideosResponse;
import com.djt.social.model.StatusResponse;
import com.djt.social.model.VersionResponse;
import com.djt.social.persistence.ISocialConnectionRepositoryFactory;
import com.djt.social.persistence.ISocialProviderApiKeyRepository;
import com.djt.social.service.ISocialService;

/**
 * 
 * @author Tom.Myers
 *
 */
@Service("socialServiceImpl")
@WebService(serviceName = "socialService", endpointInterface = "com.djt.social.service.ISocialService")
public final class SocialServiceImpl implements ISocialService, ISocialEventProducer {

	/* */
	private static final String SOCIAL_SERVICE_PAGE_SIZE_SYSTEM_PROPERTY_VALUE = System.getProperty(ISocialService.SOCIAL_SERVICE_DEFAULT_PAGE_SIZE_SYSTEM_PROPERTY_NAME);

	/* */
	private static final String BUILD_PROPERTIES_RESOURCE_NAME = "build.properties";
	
	/* */
	private static final String BUILD_VERSION_PROPERTY_NAME = "build.version";

	/* */
	private static final String BUILD_TIMESTAMP_PROPERTY_NAME = "build.timestamp";

	/* */
	private static final char COLON = ':';

	/* */
	private static final char FORWARD_SLASH = '/';
	
	/* */
	private static final String HTTP = "http://";

	/* */
	private static final String HTTPS = "https://";
	
	/* */
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");;
 	
	/* */
	private Logger logger = Logger.getLogger(SocialServiceImpl.class);
	
	/* */
	private ISocialProviderApiKeyRepository socialProviderApiKeyRepository;
		
	/* */
	private ISocialConnectionRepositoryFactory socialConnectionRepositoryFactory;
	
	/* */
	private ISocialMediaQueryCache socialMediaQueryCache;

	/* */
	private IAccessTokenCache accessTokenCache;
	
	/* */
	private List<List<String>> socialProviderEndpointMonitoringList;
	
	/* */
	private String socialUrlSalt;
	
	/* */
	private ISocialEventHelper socialEventHelper;
	
	/* */
	private boolean enableAccessTokenPersistence;
	
	/* */
	private EnvironmentPropertyPlaceholderConfigurer environmentPropertyPlaceholderConfigurer = new EnvironmentPropertyPlaceholderConfigurer();
	
	/**
	 * 
	 */
	public SocialServiceImpl() {
		
	}
		
	/**
	 * @param socialProviderApiKeyRepository
	 * @param socialConnectionRepositoryFactory
	 * @param socialMediaQueryCache
	 * @param accessTokenCache
	 * @param socialUrlSalt
	 * @param socialEventHelper
	 * @param enableAccessTokenPersistence
	 */
	public SocialServiceImpl(
		ISocialProviderApiKeyRepository socialProviderApiKeyRepository,
		ISocialConnectionRepositoryFactory socialConnectionRepositoryFactory,
		ISocialMediaQueryCache socialMediaQueryCache,
		IAccessTokenCache accessTokenCache,
		String socialUrlSalt,
		ISocialEventHelper socialEventHelper,
		String enableAccessTokenPersistence) {
		
		this.socialProviderApiKeyRepository = socialProviderApiKeyRepository;
		this.socialConnectionRepositoryFactory = socialConnectionRepositoryFactory;
		this.socialMediaQueryCache = socialMediaQueryCache;
		this.accessTokenCache = accessTokenCache;
		this.socialUrlSalt = socialUrlSalt;
		this.socialEventHelper = socialEventHelper;
		this.enableAccessTokenPersistence = Boolean.parseBoolean(enableAccessTokenPersistence);
		
		this.socialProviderEndpointMonitoringList = new ArrayList<List<String>>();
		this.socialProviderEndpointMonitoringList.add(buildSocialProviderEndpointMonitoringMap(ISocialService.PROVIDER_NAME_FACEBOOK, ISocialService.FACEBOOK_PROVIDER_AUTHORIZE_ENDPOINT));
		this.socialProviderEndpointMonitoringList.add(buildSocialProviderEndpointMonitoringMap(ISocialService.PROVIDER_NAME_INSTAGRAM, ISocialService.INSTAGRAM_PROVIDER_AUTHORIZE_ENDPOINT));
		this.socialProviderEndpointMonitoringList.add(buildSocialProviderEndpointMonitoringMap(ISocialService.PROVIDER_NAME_FLICKR, ISocialService.FLICKR_PROVIDER_AUTHORIZE_ENDPOINT));
	}
	
	/*
	 * 
	 * @return
	 */
	private boolean getEnableAccessTokenPersistence() {
		
		String value = environmentPropertyPlaceholderConfigurer.resolvePlaceholder(EnvironmentPropertyPlaceholderConfigurer.SOCIAL_ENABLE_ACCESS_TOKEN_PERSISTENCE_KEY);
		if (value != null && !value.isEmpty()) {
			return Boolean.parseBoolean(value);
		}
		return this.enableAccessTokenPersistence;
	}
	
	/*
	 * 
	 * @param providerName
	 * @param providerEndpoint
	 * @return
	 */
	private List<String> buildSocialProviderEndpointMonitoringMap(String providerName, String providerEndpoint) {
		List<String> list = new ArrayList<String>();
		list.add(providerName);
		list.add(providerEndpoint);
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.ISocialService#getSocialProfile(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public SocialProfileResponse getSocialProfile(
		String promotionDeployPath,
		String providerId,
		String accessToken,
		String accessTokenSecret) {
		
		try {
			return getApiAdapter(promotionDeployPath, providerId, accessToken, accessTokenSecret).getSocialProfile();
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("getSocialProfile(promotionDeployPath=");
			sb.append(promotionDeployPath);
			sb.append(", providerId=");
			sb.append(providerId);
			sb.append(", accessToken=");
			sb.append(accessToken);
			sb.append(", accessTokenSecret=");
			sb.append(accessTokenSecret);
			sb.append("), error=");
			sb.append(e.getMessage());
			String errorMessage = sb.toString();
			logger.error(errorMessage);
			return new SocialProfileResponse(errorMessage, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.ISocialService#getSocialPhotoAlbums(java.lang.String, java.lang.String, int, int, java.lang.String, java.lang.String)
	 */
	public SocialAlbumsResponse getSocialPhotoAlbums(
		String promotionDeployPath,
		String providerId,
		int offset, 
		int limit,
		String accessToken, 
		String accessTokenSecret) {

		int pageSize = -1;
		try {
			ISocialApiAdapter socialAdapter = this.getApiAdapter(promotionDeployPath, providerId, accessToken, accessTokenSecret);
						
			// If the passed in limit is 0, then we want to return a list of *all* albums (i.e. disregard pagination)
			SocialAlbumsResponse socialAlbumsResponse = null;
			if (limit == 0) {
				socialAlbumsResponse = socialAdapter.getSocialPhotoAlbums();
			} else {
				pageSize = getPageSize(limit);
				socialAlbumsResponse = socialAdapter.getSocialPhotoAlbums(offset, pageSize);
			}
			return socialAlbumsResponse;
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("getSocialPhotoAlbums(promotionDeployPath=");
			sb.append(promotionDeployPath);
			sb.append(", providerId=");
			sb.append(providerId);
			sb.append(", offset=");
			sb.append(offset);
			sb.append(", limit=");
			sb.append(limit);
			sb.append(", actualPageSize=");
			sb.append(pageSize);
			sb.append(", accessToken=");
			sb.append(accessToken);
			sb.append(", accessTokenSecret=");
			sb.append(accessTokenSecret);
			sb.append("), error=");
			sb.append(e.getMessage());
			String errorMessage = sb.toString();
			logger.error(errorMessage);
			return new SocialAlbumsResponse(errorMessage, 0, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.ISocialService#getSocialPhotosFromSocialPhotoAlbum(java.lang.String, java.lang.String, java.lang.String, int, int, java.lang.String, java.lang.String)
	 */
	public SocialPhotosResponse getSocialPhotosFromSocialPhotoAlbum(
		String promotionDeployPath,	
		String providerId,
		String id, 
		int offset, 
		int limit,
		String accessToken, 
		String accessTokenSecret) {

		int pageSize = -1;
		try {
			ISocialApiAdapter socialAdapter = this.getApiAdapter(promotionDeployPath, providerId, accessToken, accessTokenSecret);
			pageSize = getPageSize(limit);
			return socialAdapter.getSocialPhotosFromSocialPhotoAlbum(id, offset, pageSize);
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("getSocialPhotosFromSocialPhotoAlbum(promotionDeployPath=");
			sb.append(promotionDeployPath);
			sb.append(", providerId=");
			sb.append(providerId);
			sb.append(", offset=");
			sb.append(offset);
			sb.append(", limit=");
			sb.append(limit);
			sb.append(", actualPageSize=");
			sb.append(pageSize);
			sb.append(", accessToken=");
			sb.append(accessToken);
			sb.append(", accessTokenSecret=");
			sb.append(accessTokenSecret);
			sb.append("), error=");
			sb.append(e.getMessage());
			String errorMessage = sb.toString();
			logger.error(errorMessage);
			return new SocialPhotosResponse(errorMessage, 0, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.ISocialService#getSocialVideoAlbums(java.lang.String, java.lang.String, int, int, java.lang.String, java.lang.String)
	 */
	public SocialAlbumsResponse getSocialVideoAlbums(
		String promotionDeployPath,	
		String providerId,
		int offset, 
		int limit,
		String accessToken, 
		String accessTokenSecret) {

		int pageSize = -1;
		try {
			ISocialApiAdapter socialAdapter = this.getApiAdapter(promotionDeployPath, providerId, accessToken, accessTokenSecret);
			
			// If the passed in limit is 0, then we want to return a list of *all* albums (i.e. disregard pagination)
			SocialAlbumsResponse socialAlbumsResponse = null;
			if (limit == 0) {
				socialAlbumsResponse = socialAdapter.getSocialVideoAlbums();
			} else {
				pageSize = getPageSize(limit);
				socialAlbumsResponse = socialAdapter.getSocialVideoAlbums(offset, pageSize);
			}
			return socialAlbumsResponse;
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("getSocialVideoAlbums(promotionDeployPath=");
			sb.append(promotionDeployPath);
			sb.append(", providerId=");
			sb.append(providerId);
			sb.append(", offset=");
			sb.append(offset);
			sb.append(", limit=");
			sb.append(limit);
			sb.append(", actualPageSize=");
			sb.append(pageSize);
			sb.append(", accessToken=");
			sb.append(accessToken);
			sb.append(", accessTokenSecret=");
			sb.append(accessTokenSecret);
			sb.append("), error=");
			sb.append(e.getMessage());
			String errorMessage = sb.toString();
			logger.error(errorMessage);
			return new SocialAlbumsResponse(errorMessage, 0, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.ISocialService#getSocialVideosFromSocialVideoAlbum(java.lang.String, java.lang.String, java.lang.String, int, int, java.lang.String, java.lang.String)
	 */
	public SocialVideosResponse getSocialVideosFromSocialVideoAlbum(
		String promotionDeployPath,
		String providerId,
		String id, 
		int offset, 
		int limit,
		String accessToken, 
		String accessTokenSecret) {

		int pageSize = -1;
		try {
			ISocialApiAdapter socialAdapter = this.getApiAdapter(promotionDeployPath, providerId, accessToken, accessTokenSecret);
			pageSize = getPageSize(limit);
			return socialAdapter.getSocialVideosFromSocialVideoAlbum(id, offset, pageSize);
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("getSocialVideosFromSocialVideoAlbum(promotionDeployPath=");
			sb.append(promotionDeployPath);
			sb.append(", providerId=");
			sb.append(providerId);
			sb.append(", offset=");
			sb.append(offset);
			sb.append(", limit=");
			sb.append(limit);
			sb.append(", actualPageSize=");
			sb.append(pageSize);
			sb.append(", accessToken=");
			sb.append(accessToken);
			sb.append(", accessTokenSecret=");
			sb.append(accessTokenSecret);
			sb.append("), error=");
			sb.append(e.getMessage());
			String errorMessage = sb.toString();
			logger.error(errorMessage);
			return new SocialVideosResponse(errorMessage, 0, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.ISocialService#getSocialFriends(java.lang.String, java.lang.String, java.lang.String, int, int, java.lang.String, java.lang.String)
	 */
	public SocialFriendsResponse getSocialFriends(
		String promotionDeployPath,	
		String providerId,
		String id, 
		int offset, 
		int limit,
		String accessToken, 
		String accessTokenSecret) {

		int pageSize = -1;
		try {
			ISocialApiAdapter socialAdapter = this.getApiAdapter(promotionDeployPath, providerId, accessToken, accessTokenSecret);
			pageSize = getPageSize(limit);
			return socialAdapter.getSocialFriends(id, offset, pageSize);
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("getSocialFriends(promotionDeployPath=");
			sb.append(promotionDeployPath);
			sb.append(", providerId=");
			sb.append(providerId);
			sb.append(", offset=");
			sb.append(offset);
			sb.append(", limit=");
			sb.append(limit);
			sb.append(", actualPageSize=");
			sb.append(pageSize);
			sb.append(", accessToken=");
			sb.append(accessToken);
			sb.append(", accessTokenSecret=");
			sb.append(accessTokenSecret);
			sb.append("), error=");
			sb.append(e.getMessage());
			String errorMessage = sb.toString();
			logger.error(errorMessage);
			return new SocialFriendsResponse(errorMessage, 0, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.ISocialService#getStoredAccessToken(java.lang.String, java.lang.String, java.lang.String)
	 */
	public AccessTokenResponse getStoredAccessToken(
		String promotionDeployPath, 
		String providerId, 
		String id) {

		AccessTokenResponse accessTokenResponse = null;
		
		try {
			
			accessTokenResponse = this.accessTokenCache.getAccessTokenResponse(promotionDeployPath, providerId, id);
			if (accessTokenResponse == null) {

				String accessToken = null;
				String accessTokenSecret = null;
				if (getEnableAccessTokenPersistence()) {

					String errorMessage = null;
					
					ConnectionRepository connectionRepository = this.socialConnectionRepositoryFactory.createConnectionRepository(promotionDeployPath, id);
					
					Class<?> apiType = null;
					if (providerId.equalsIgnoreCase(ISocialService.PROVIDER_NAME_FACEBOOK)) {
						apiType = Facebook.class;
					} else if (providerId.equalsIgnoreCase(ISocialService.PROVIDER_NAME_FLICKR)) {
						apiType = Flickr.class;
					} else if (providerId.equalsIgnoreCase(ISocialService.PROVIDER_NAME_INSTAGRAM)) {
						apiType = Instagram.class;
					} else if (providerId.equalsIgnoreCase(ISocialService.PROVIDER_NAME_TWITTER)) {
						apiType = Twitter.class;
					} else {
						errorMessage = "Unsupported social media service provider requested: [" + providerId + "] for promotion: [" + promotionDeployPath + "] and uid: [" + id + "].";
						logger.error(errorMessage);
					}
					
					if (apiType != null) {
					
						Connection<?> socialConnection = connectionRepository.findPrimaryConnection(apiType);
						if (socialConnection != null) {

							ConnectionData connectionData = socialConnection.createData();
							accessToken = connectionData.getAccessToken();
							accessTokenSecret = connectionData.getSecret();

							accessTokenResponse = new AccessTokenResponse(
								null, 
								accessToken, 
								accessTokenSecret);
							
							this.accessTokenCache.putAccessTokenResponse(
								promotionDeployPath, 
								providerId, 
								id, 
								accessTokenResponse);
						} else {
							accessTokenResponse = new AccessTokenResponse(
								"No access token exists for id: [" + id + "], providerId: [" + providerId + "] and promoDeployPath[: " + promotionDeployPath + "].", 
								accessToken, 
								accessTokenSecret);
						}
					}
					
					if (accessTokenResponse == null) {
						accessTokenResponse = new AccessTokenResponse(
							errorMessage, 
							accessToken, 
							accessTokenSecret);
					}
					
				} else {
					
					accessTokenResponse = new AccessTokenResponse(
						"No access token exists for id: [" + id + "], providerId: [" + providerId + "] and promoDeployPath[: " + promotionDeployPath + "].", 
						accessToken, 
						accessTokenSecret);
				}
			}
			
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("getStoredAccessToken(promotionDeployPath=");
			sb.append(promotionDeployPath);
			sb.append(", providerId=");
			sb.append(providerId);
			sb.append(providerId);
			sb.append(", id=");
			sb.append(id);
			sb.append("), error=");
			sb.append(e.getMessage());
			String errorMessage = sb.toString();
			logger.error(errorMessage);
			accessTokenResponse = new AccessTokenResponse(e.getMessage(), null, null);
		}
		
		return accessTokenResponse;
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialService#deleteStoredAccessToken(java.lang.String, java.lang.String, java.lang.String)
	 */
    public StatusResponse deleteStoredAccessToken(
    	String promotionDeployPath,
    	String providerId,
    	String id) {
    	
    	StatusResponse statusResponse = null;

    	try {
    		
			ConnectionRepository connectionRepository = this.socialConnectionRepositoryFactory.createConnectionRepository(promotionDeployPath, id);
			connectionRepository.removeConnections(providerId);
			
			this.accessTokenCache.removeAccessTokenResponse(promotionDeployPath, providerId, id);
			
    		StringBuilder sb = new StringBuilder();
    		sb.append("Deleted access token for promotionDeployPath: [");
    		sb.append(promotionDeployPath);
    		sb.append("] and social media provider: [");
    		sb.append(providerId);
    		sb.append("] and userId: [");
    		sb.append(id);
    		sb.append("].");
    		String result = sb.toString();
    		logger.info(result);
    		statusResponse = new StatusResponse(null, result);
    		
    	} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("deleteStoredAccessToken(promotionDeployPath=");
			sb.append(promotionDeployPath);
			sb.append(", providerId=");
			sb.append(providerId);
			sb.append(", userId=");
			sb.append(id);
			sb.append("), error=");
			sb.append(e.getMessage());
			String errorMessage = sb.toString();
			logger.error(errorMessage);
    		statusResponse = new StatusResponse(errorMessage, null);
    	}
    	
    	return statusResponse;
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.ISocialService#installSocialProviderApiKeyForPromotion(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
    public InstallApiKeyResponse installSocialProviderApiKeyForPromotion(
    	String promotionDeployPath,
    	String providerId,
    	String apiKey,
    	String apiKeySecret) {
    	
    	InstallApiKeyResponse installApiKeyResponse = null;
    	
    	try {
        	this.socialProviderApiKeyRepository.insertOrUpdateProviderApiKey(
            		promotionDeployPath, 
            		providerId, 
            		apiKey, 
            		apiKeySecret);
            
        	
        	// Notify any listeners of the fact that a Social Provider API Key was just installed.
        	// (e.g. SocialConnectionFactoryRegistry will need to re-initialize)
        	String uid = null;
        	String eventType = SocialEvent.SOCIAL_PROVIDER_API_INSTALLED;
        	String eventDetails = "API Key=" + apiKey + ", API Key Secret=" + apiKeySecret;
        	
        	SocialEvent socialEvent = this.createSocialEvent(
        		promotionDeployPath, 
        		providerId, 
        		uid, eventType, eventDetails);
        	
        	this.fireSocialEvent(socialEvent);

        	
    		StringBuilder sb = new StringBuilder();
    		sb.append("Installed API Key for social media provider: [");
    		sb.append(providerId);
    		sb.append("] for promotion: [");
    		sb.append(promotionDeployPath);
    		sb.append("] with API Key: [");
    		sb.append(apiKey);
    		sb.append("] and API Key Secret: [");
    		sb.append(apiKeySecret);
    		sb.append("].");
    		String result = sb.toString();
    		logger.info(result);
    		installApiKeyResponse = new InstallApiKeyResponse(null, result);
    		
    	} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("installSocialProviderApiKeyForPromotion(promotionDeployPath=");
			sb.append(promotionDeployPath);
			sb.append(", providerId=");
			sb.append(providerId);
			sb.append(", apiKey=");
			sb.append(apiKey);
			sb.append(", apiKeySecret=");
			sb.append(apiKeySecret);
			sb.append("), error=");
			sb.append(e.getMessage());
			String errorMessage = sb.toString();
			logger.error(errorMessage);
			installApiKeyResponse = new InstallApiKeyResponse(errorMessage, null);
    	}
    	
    	return installApiKeyResponse;
    }
    
    /*
     * (non-Javadoc)
     * @see com.djt.social.service.api.ISocialService#getStatus()
     */
    public StatusResponse getStatus() {

    	StatusResponse statusResponse = null;
    	try {
    		StringBuilder sb = new StringBuilder();
    		Iterator<List<String>> outerListIterator = this.socialProviderEndpointMonitoringList.iterator();
    		while (outerListIterator.hasNext()) {
    		
    			List<String> innerList = outerListIterator.next();
    			String providerName = innerList.get(0);
    			String providerEndpoint = innerList.get(1);
    		
    			sb.append(testSocialProviderEndpoint(providerName, providerEndpoint));
    			
    			if (outerListIterator.hasNext()) {
    				sb.append(",");
    				sb.append(LINE_SEPARATOR);
    			}
    		}
    		statusResponse = new StatusResponse(null, sb.toString());
    	} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("getStatus(), error=");
			sb.append(e.getMessage());
			String errorMessage = sb.toString();
			logger.error(errorMessage, e);
    		statusResponse = new StatusResponse(errorMessage, null);
    	}    	
    	return statusResponse;
    }

    /*
     * 
     * @param providerName
     * @param providerEndpoint
     * @return
     */
    private String testSocialProviderEndpoint(String providerName, String providerEndpoint) {
    	
    	StringBuilder sb = new StringBuilder();
        DefaultHttpClient httpClient = null;
        try {

        	String hostname = parseProviderEndpointHostname(providerEndpoint);
        	int port = parseProviderEndpointPort(providerEndpoint);
        	String scheme = parseProviderEndpointScheme(providerEndpoint);
        	String requestPath = parseProviderEndpointRequestPath(providerEndpoint, hostname);
        	       	
            HttpHost httpHost = new HttpHost(hostname, port, scheme);
            HttpGet httpGetRequest = new HttpGet(requestPath);

            httpClient = new DefaultHttpClient();            
            HttpResponse httpResponse = httpClient.execute(httpHost, httpGetRequest);

            sb.append("providerName=");
            sb.append(providerName);            
            sb.append(", endPoint=");
            sb.append(providerEndpoint);
            sb.append(", status=");
            sb.append(httpResponse.getStatusLine());

        } catch (ClientProtocolException cpe) {
            sb.append(providerName);
            sb.append(": ERROR=");
            sb.append(cpe.getMessage());
            logger.error("Could not test social provider endpoint: " + cpe.getMessage(), cpe);
		} catch (IOException ioe) {
            sb.append(providerName);
            sb.append(": ERROR=");
            sb.append(ioe.getMessage());
            logger.error("Could not test social provider endpoint: " + ioe.getMessage(), ioe);
		} finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();	
			}
        }
        return sb.toString();
    }
    
    /*
     * 
     * @param providerEndpoint
     * @return
     */
    private String parseProviderEndpointHostname(String providerEndpoint) {

    	int beginIndex = 0;
    	if (providerEndpoint.contains(HTTPS)) {
    		beginIndex = HTTPS.length();
    	} else {
    		beginIndex = HTTP.length();
    	}
    	
		int endIndex = providerEndpoint.indexOf(FORWARD_SLASH, beginIndex);
    	
		return providerEndpoint.substring(beginIndex, endIndex);
    }

    /*
     * 
     * @param providerEndpoint
     * @return
     */
    private int parseProviderEndpointPort(String providerEndpoint) {

    	int port = ISocialService.PROVIDER_ENDPOINT_DEFAULT_PORT;

    	int beginIndex = 0;
    	if (providerEndpoint.contains(HTTPS)) {
    		beginIndex = HTTPS.length();
    	} else {
    		beginIndex = HTTP.length();
    	}
    	
    	int colonIndex = providerEndpoint.indexOf(COLON, beginIndex);
    	if (colonIndex > 0) {
    	
    		int slashIndex = providerEndpoint.indexOf(FORWARD_SLASH);
    		port = Integer.parseInt(providerEndpoint.substring(colonIndex, slashIndex));
    	}
    	
    	return port;
    }

    /*
     * 
     * @param providerEndpoint
     * @return
     */
    private String parseProviderEndpointScheme(String providerEndpoint) {

    	String scheme = null;
    	
    	if (providerEndpoint.contains(PROVIDER_ENDPOINT_SCHEME_HTTPS)) {
    		scheme = PROVIDER_ENDPOINT_SCHEME_HTTPS; 
    	} else {
    		scheme = PROVIDER_ENDPOINT_SCHEME_HTTP;
    	}
    	
    	return scheme;
    }
    
    /*
     * 
     * @param providerEndpoint
     * @param hostname
     * @param scheme
     * @return
     */
    private String parseProviderEndpointRequestPath(String providerEndpoint, String hostname) {
    	
    	String requestPath = null;

    	String prefix = null;
    	if (providerEndpoint.contains(HTTPS)) {
    		prefix = HTTPS + hostname;
    	} else {
    		prefix = HTTP + hostname;
    	}
    	
    	requestPath = providerEndpoint.substring(prefix.length());
    	
    	return requestPath;
    }
    
    /*
     * (non-Javadoc)
     * @see com.djt.social.service.api.ISocialService#getVersion()
     */
    public VersionResponse getVersion() {

    	VersionResponse versionResponse = null;
    	String errorMessage = null;
    	InputStream inputStream = null;
    	
    	try {
    		String version = null;
    		String timestamp = null;
    		
    		Properties properties = new Properties();
    		inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(BUILD_PROPERTIES_RESOURCE_NAME);
    		if (inputStream != null) {
        		properties.load(inputStream);
        		version = properties.getProperty(BUILD_VERSION_PROPERTY_NAME);
        		timestamp = properties.getProperty(BUILD_TIMESTAMP_PROPERTY_NAME);
    		} else {
    			errorMessage = "Could not load: [" + BUILD_PROPERTIES_RESOURCE_NAME + "] from classpath.";
    		}
    		
    		versionResponse = new VersionResponse(errorMessage, version, timestamp);
    	} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("getVersion(), error=");
			sb.append(e.getMessage());
			errorMessage = sb.toString();
			logger.error(errorMessage);
    		versionResponse = new VersionResponse(errorMessage, null, null);
    	} finally {
    		if (inputStream != null) {
    			try {
    				inputStream.close();
    			} catch (IOException ioe) {
    				logger.error("Could not close resource stream: " + BUILD_PROPERTIES_RESOURCE_NAME, ioe);
    			}
    		}
    	}
    	
    	return versionResponse;
    }

    /*
     * (non-Javadoc)
     * @see com.djt.social.service.ISocialService#removeSocialPhotoAlbumsFromCache(java.lang.String, java.lang.String, java.lang.String)
     */
	public void removeSocialPhotoAlbumsFromCache(
		String promotionDeployPath, 
		String providerId, 
		String accessToken) {
	
		this.socialMediaQueryCache.removeSocialPhotoAlbums(
			promotionDeployPath, 
			providerId, 
			accessToken);
	}
    
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialService#removeSocialPhotosFromSocialPhotoAlbumFromCache(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void removeSocialPhotosFromSocialPhotoAlbumFromCache(
		String promotionDeployPath, 
		String providerId, 
		String accessToken, 
		String albumId) {
		
		this.socialMediaQueryCache.removeSocialPhotosFromSocialPhotoAlbum(
			promotionDeployPath, 
			providerId, 
			accessToken, 
			albumId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialService#removeSocialPhotosFromCache(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void removeSocialPhotosFromCache(
		String promotionDeployPath, 
		String providerId, 
		String accessToken) {
		
		this.socialMediaQueryCache.removeSocialPhotos(
			promotionDeployPath, 
			providerId, 
			accessToken);
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialService#removeSocialProfileFromCache(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void removeSocialProfileFromCache(
		String promotionDeployPath, 
		String providerId, 
		String accessToken) {
		
		this.socialMediaQueryCache.removeSocialProfile(
			promotionDeployPath, 
			providerId, 
			accessToken);
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialService#removeAllFromCache()
	 */
	public void removeAllFromCache() {
		
		this.accessTokenCache.removeAll();
		this.socialMediaQueryCache.removeAll();
	}
	
	/*
	 * 
	 * @param limit
	 * @return
	 */
	private int getPageSize(int limit) {

		int pageSize = limit;
		if (limit < 0 && SOCIAL_SERVICE_PAGE_SIZE_SYSTEM_PROPERTY_VALUE != null && !SOCIAL_SERVICE_PAGE_SIZE_SYSTEM_PROPERTY_VALUE.isEmpty()) {
			try {
				pageSize = Integer.parseInt(SOCIAL_SERVICE_PAGE_SIZE_SYSTEM_PROPERTY_VALUE);
			} catch (NumberFormatException nfe) {
				logger.error("Could not parse system property Social Service page size override value: " + SOCIAL_SERVICE_PAGE_SIZE_SYSTEM_PROPERTY_VALUE, nfe);
			}
		}
		
		return validatePageSize(pageSize);
	}

	/*
	 * 
	 * @param pageSize
	 * @return
	 */
	private int validatePageSize(int pageSize) {

		int pageSizeToReturn = pageSize;
		int defaultPageSize = ISocialService.DEFAULT_PAGE_SIZE;
		
		if (pageSize < ISocialService.MINIMUM_PAGE_SIZE || pageSize > ISocialService.MAXIMUM_PAGE_SIZE) {
			
			StringBuilder sb = new StringBuilder();
			sb.append("Social Service page size must be a number between [");
			sb.append(ISocialService.MINIMUM_PAGE_SIZE);
			sb.append("] and [");
			sb.append(ISocialService.MAXIMUM_PAGE_SIZE);
			sb.append("], using default value of [");
			sb.append(defaultPageSize);
			sb.append("], offending value was: ");
			sb.append(pageSize);
			sb.append("].");
			String errorMessage = sb.toString();
			logger.error(errorMessage);
			
			pageSizeToReturn = defaultPageSize; 
		}
		
		return pageSizeToReturn;
	}

	/*
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param accessTokenSecret
	 * @return
	 */
	private ISocialApiAdapter getApiAdapter(
		String promotionDeployPath,
		String providerId, 
		String accessToken, 
		String accessTokenSecret) {

		Map<String, String> apiKeyPairMap = this.socialProviderApiKeyRepository.getProviderApiKeyPair(promotionDeployPath, providerId);
		if (apiKeyPairMap != null && apiKeyPairMap.size() > 0) {

			String apiKey = apiKeyPairMap.get(ISocialProviderApiKeyRepository.API_KEY);
			String apiKeySecret = apiKeyPairMap.get(ISocialProviderApiKeyRepository.API_KEY_SECRET);
			
			if (providerId.equalsIgnoreCase(ISocialService.PROVIDER_NAME_FACEBOOK)) {
				
				return new FacebookSocialApiAdapter(
					promotionDeployPath,
					providerId,
					accessToken,
					socialMediaQueryCache,
					socialUrlSalt,
					new FacebookTemplate(accessToken));
				
			} else if (providerId.equalsIgnoreCase(ISocialService.PROVIDER_NAME_FLICKR)) {

				return new FlickrSocialApiAdapter(
					promotionDeployPath,
					providerId,
					accessToken,
					socialMediaQueryCache,
					socialUrlSalt,
					new FlickrTemplate(
						apiKey,
						apiKeySecret,
						accessToken, 
						accessTokenSecret));

			} else if (providerId.equalsIgnoreCase(ISocialService.PROVIDER_NAME_INSTAGRAM)) {
				
				return new InstagramSocialApiAdapter(
						promotionDeployPath,
						providerId,
						accessToken,
						socialMediaQueryCache,
						socialUrlSalt,
						new InstagramTemplate(
							ISocialService.PROVIDER_NAME_INSTAGRAM, 
							accessToken));
								
			} else if (providerId.equalsIgnoreCase(ISocialService.PROVIDER_NAME_TWITTER)) {
				
				return new TwitterSocialApiAdapter(
					promotionDeployPath,
					providerId,
					accessToken,
					socialMediaQueryCache,
					socialUrlSalt,
					new TwitterTemplate(
						apiKey,
						apiKeySecret,
						accessToken, 
						accessTokenSecret));
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("No API Key was found for social provider: [");
		sb.append(providerId);
		sb.append("] and promotion: [");
		sb.append(promotionDeployPath);
		sb.append("]. Please install the appropriate API Keys and try again.");
		throw new IllegalStateException(sb.toString());		
	}
	
	/*
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param uid
	 * @param eventType
	 * @param eventDetails
	 * @return
	 */
	private SocialEvent createSocialEvent(
		String promotionDeployPath,
		String providerId,
		String uid,
		String eventType,
		String eventDetails) {
		
		return new SocialEvent(
			promotionDeployPath,
			providerId,
			uid,
			eventType,
			eventDetails);
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.event.ISocialEventProducer#fireSocialEvent(com.djt.social.event.SocialEvent)
	 */
	public void fireSocialEvent(SocialEvent socialEvent) {
		
		// Notify each registered listener of the event that was just fired.
		Iterator<ISocialEventListener> iterator = this.socialEventHelper.getSocialEventListeners().iterator();
		while (iterator.hasNext()) {
			
			ISocialEventListener socialEventListener = iterator.next();
			socialEventListener.socialEventOccurred(socialEvent);
		}
	}
}