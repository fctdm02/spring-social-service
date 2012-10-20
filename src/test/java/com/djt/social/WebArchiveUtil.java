/**
 * 
 */
package com.djt.social;

import java.io.File;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.springframework.social.flickr.api.Flickr;
import org.springframework.social.instagram.api.Instagram;

import com.djt.social.adapter.ISocialApiAdapter;
import com.djt.social.adapter.impl.AbstractSocialApiAdapter;
import com.djt.social.adapter.impl.FacebookSocialApiAdapter;
import com.djt.social.adapter.impl.FlickrSocialApiAdapter;
import com.djt.social.adapter.impl.InstagramSocialApiAdapter;
import com.djt.social.adapter.impl.TwitterSocialApiAdapter;
import com.djt.social.cache.IAccessTokenCache;
import com.djt.social.cache.ISocialMediaQueryCache;
import com.djt.social.cache.impl.AccessTokenCacheImpl;
import com.djt.social.cache.impl.SocialMediaQueryCacheImpl;
import com.djt.social.config.EnvironmentPropertyPlaceholderConfigurer;
import com.djt.social.config.Log4JInitServlet;
import com.djt.social.controller.ConnectSupport;
import com.djt.social.controller.OperationsController;
import com.djt.social.controller.SocialConnectController;
import com.djt.social.interceptor.LoggingInterceptor;
import com.djt.social.model.AccessTokenResponse;
import com.djt.social.model.InstallApiKeyResponse;
import com.djt.social.model.JsonUtil;
import com.djt.social.model.SocialAlbum;
import com.djt.social.model.SocialAlbumsResponse;
import com.djt.social.model.SocialConnection;
import com.djt.social.model.SocialConnectionData;
import com.djt.social.model.SocialFriendsResponse;
import com.djt.social.model.SocialMedia;
import com.djt.social.model.SocialPhoto;
import com.djt.social.model.SocialPhotosResponse;
import com.djt.social.model.SocialProfile;
import com.djt.social.model.SocialProfileResponse;
import com.djt.social.model.SocialVideo;
import com.djt.social.model.SocialVideosResponse;
import com.djt.social.model.StatusResponse;
import com.djt.social.model.VersionResponse;
import com.djt.social.persistence.ISocialConnectionRepositoryFactory;
import com.djt.social.persistence.ISocialConnectionRepositoryFactoryLocator;
import com.djt.social.persistence.ISocialProviderApiKeyRepository;
import com.djt.social.persistence.impl.SocialConnectionRepositoryFactoryImpl;
import com.djt.social.persistence.impl.SocialConnectionRepositoryFactoryLocatorImpl;
import com.djt.social.persistence.impl.SocialConnectionRepositoryImpl;
import com.djt.social.persistence.impl.SocialProviderApiKeyRepositoryImpl;
import com.djt.social.service.ISocialService;
import com.djt.social.service.impl.SocialServiceImpl;

/**
 * 
 * @author Tom.Myers
 * 
 */
public class WebArchiveUtil {

	/* */
	static {
		WAR = buildWar();
	}
	
	/* */
	private static WebArchive WAR;

	/** */
	public static final String JAVA_SRC = "src/main/java";
	
	/** */
	public static final String WEBAPP_SRC = "src/main/webapp";

	/**
	 * 
	 * @return
	 */
	public static WebArchive getWar() {
		
		return WAR;
	}
	
	/**
	 * 
	 * @return
	 */
	private static WebArchive buildWar() {

		WAR = ShrinkWrap.create(WebArchive.class, "spring-social-service.war")

				.addPackages(true, Flickr.class.getPackage())
				.addPackages(true, Instagram.class.getPackage())
								
				.addClass(AbstractSocialApiAdapter.class)
				.addClass(FacebookSocialApiAdapter.class)
				.addClass(FlickrSocialApiAdapter.class)
				.addClass(InstagramSocialApiAdapter.class)
				.addClass(TwitterSocialApiAdapter.class)
				
				.addClass(ISocialApiAdapter.class)
				
				.addClass(AccessTokenCacheImpl.class)
				.addClass(SocialMediaQueryCacheImpl.class)
				
				.addClass(IAccessTokenCache.class)
				.addClass(ISocialMediaQueryCache.class)
				
				.addClass(EnvironmentPropertyPlaceholderConfigurer.class)
				.addClass(Log4JInitServlet.class)
				
				.addClass(ConnectSupport.class)
				.addClass(OperationsController.class)
				.addClass(SocialConnectController.class)
				
				.addClass(LoggingInterceptor.class)
				
				.addClass(AccessTokenResponse.class)
				.addClass(InstallApiKeyResponse.class)
				.addClass(JsonUtil.class)
				.addClass(SocialAlbum.class)
				.addClass(SocialAlbumsResponse.class)
				.addClass(SocialConnection.class)
				.addClass(SocialConnectionData.class)
				.addClass(SocialFriendsResponse.class)
				.addClass(SocialMedia.class)
				.addClass(SocialPhoto.class)
				.addClass(SocialPhotosResponse.class)
				.addClass(SocialProfile.class)
				.addClass(SocialProfileResponse.class)
				.addClass(SocialVideo.class)
				.addClass(SocialVideosResponse.class)
				.addClass(StatusResponse.class)
				.addClass(VersionResponse.class)

				.addClass(ISocialConnectionRepositoryFactory.class)
				.addClass(ISocialConnectionRepositoryFactoryLocator.class)
				.addClass(ISocialProviderApiKeyRepository.class)
				.addAsResource(new File(JAVA_SRC, "com/djt/social/persistence/SocialService_tables.sql"))

				.addClass(SocialConnectionRepositoryFactoryImpl.class)
				.addClass(SocialConnectionRepositoryFactoryLocatorImpl.class)
				.addClass(SocialConnectionRepositoryImpl.class)
				.addClass(SocialProviderApiKeyRepositoryImpl.class)
				
				.addClass(SocialServiceImpl.class)
				.addClass(ISocialService.class)
				
				.addAsManifestResource(new File(WEBAPP_SRC, "META-INF/cxf/org.apache.cxf.Logger"))
				.addAsManifestResource(new File(WEBAPP_SRC, "META-INF/MANIFEST.MF"))

				.addAsWebResource(new File("src/main/webapp/", "welcome.html"), ArchivePaths.create("welcome.html"))
				
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "accessTokenResponse.jsp"), ArchivePaths.create("views/accessTokenResponse.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "friends.jsp"), ArchivePaths.create("views/friends.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "home.jsp"), ArchivePaths.create("views/home.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "installResponse.jsp"), ArchivePaths.create("views/installResponse.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "photoalbums.jsp"), ArchivePaths.create("views/photoalbums.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "photos.jsp"), ArchivePaths.create("views/photos.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "profile.jsp"), ArchivePaths.create("views/profile.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "socialAuthResponse.jsp"), ArchivePaths.create("views/socialAuthResponse.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "statusResponse.jsp"), ArchivePaths.create("views/statusResponse.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "status.jsp"), ArchivePaths.create("views/status.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "versionResponse.jsp"), ArchivePaths.create("views/versionResponse.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "videoalbums.jsp"), ArchivePaths.create("views/videoalbums.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/", "videos.jsp"), ArchivePaths.create("views/videos.jsp"))
								
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/connect/", "facebookConnect.jsp"), ArchivePaths.create("views/connect/facebookConnect.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/connect/", "facebookConnected.jsp"), ArchivePaths.create("views/connect/facebookConnected.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/connect/", "flickrConnect.jsp"), ArchivePaths.create("views/connect/flickrConnect.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/connect/", "flickrConnected.jsp"), ArchivePaths.create("views/connect/flickrConnected.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/connect/", "instagramConnect.jsp"), ArchivePaths.create("views/connect/instagramConnect.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/connect/", "instagramConnected.jsp"), ArchivePaths.create("views/connect/instagramConnected.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/connect/", "twitterConnect.jsp"), ArchivePaths.create("views/connect/twitterConnect.jsp"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/views/connect/", "twitterConnected.jsp"), ArchivePaths.create("views/connect/twitterConnected.jsp"))
				
				.addAsWebInfResource(new File(WEBAPP_SRC, "WEB-INF/applicationContext-service.xml"))
				.addAsWebInfResource(new File(WEBAPP_SRC, "WEB-INF/servletContext.xml"))
				
				.setWebXML(new File("src/test/webapp/WEB-INF/web.xml"));
	            
		return WAR;
	}
}