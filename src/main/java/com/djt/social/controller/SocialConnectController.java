/**
 * 
 */
package com.djt.social.controller;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.core.GenericTypeResolver;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import com.djt.social.cache.IAccessTokenCache;
import com.djt.social.model.AccessTokenResponse;
import com.djt.social.persistence.ISocialConnectionRepositoryFactoryLocator;

/**
 * Generic UI controller for managing the account-to-service-provider connection flow.
 * <ul>
 * <li>GET /connect/{providerId}  - Get a web page showing connection status to {providerId}.</li>
 * <li>POST /connect/{providerId} - Initiate an connection with {providerId}.</li>
 * <li>GET /connect/{providerId}?oauth_verifier||code - Receive {providerId} authorization callback and establish the connection.</li>
 * <li>DELETE /connect/{providerId} - Disconnect from {providerId}.</li>
 * </ul>
 */
@Controller
@RequestMapping("/connect")
public class SocialConnectController {

	/* */
	private static final String PROVIDER_ERROR_ATTRIBUTE = "social.provider.error";
	
	
	/* */
	private Logger logger = Logger.getLogger(SocialConnectController.class);

	/* */
	private final ISocialConnectionRepositoryFactoryLocator socialConnectionRepositoryFactoryLocator;

	/* */
	private final MultiValueMap<Class<?>, ConnectInterceptor<?>> connectInterceptors = new LinkedMultiValueMap<Class<?>, ConnectInterceptor<?>>();

	/* */
	private final ConnectSupport webSupport = new ConnectSupport();

	/* */
	private final UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	/* */
	private final ConnectionRepository connectionRepository;
	
	/* */
	private IAccessTokenCache accessTokenCache;
	
	
	/**
	 * @param socialConnectionRepositoryFactoryLocator the locator for connection factory instances needed to establish connections
	 * @param connectionRepository the current user's connection repository needed to persist connections; must be a proxy to a request-scoped bean
	 * @param accessTokenCache
	 */
	@Inject
	public SocialConnectController(
		ISocialConnectionRepositoryFactoryLocator socialConnectionRepositoryFactoryLocator, 
		ConnectionRepository connectionRepository,
		IAccessTokenCache accessTokenCache) {
		
		this.socialConnectionRepositoryFactoryLocator = socialConnectionRepositoryFactoryLocator;
		this.connectionRepository = connectionRepository;
		this.accessTokenCache = accessTokenCache;
	}

	/**
	 * Configure the list of connect interceptors that should receive callbacks during the connection process.
	 * Convenient when an instance of this class is configured using a tool that supports JavaBeans-based configuration.
	 * @param interceptors the connect interceptors to add
	 * @deprecated Use {@link #setConnectInterceptors(List)} instead.
	 */
	@Deprecated
	public void setInterceptors(List<ConnectInterceptor<?>> interceptors) {
		setConnectInterceptors(interceptors);
	}
	
	/**
	 * Configure the list of connect interceptors that should receive callbacks during the connection process.
	 * Convenient when an instance of this class is configured using a tool that supports JavaBeans-based configuration.
	 * @param interceptors the connect interceptors to add
	 */
	public void setConnectInterceptors(List<ConnectInterceptor<?>> interceptors) {
		for (ConnectInterceptor<?> interceptor : interceptors) {
			addInterceptor(interceptor);
		}
	}

	/**
	 * Configures the base secure URL for the application this controller is being used in e.g. <code>https://myapp.com</code>. Defaults to null.
	 * If specified, will be used to generate OAuth callback URLs.
	 * If not specified, OAuth callback URLs are generated from web request info. 
	 * You may wish to set this property if requests into your application flow through a proxy to your application server.
	 * In this case, the request URI may contain a scheme, host, and/or port value that points to an internal server not appropriate for an external callback URL.
	 * If you have this problem, you can set this property to the base external URL for your application and it will be used to construct the callback URL instead.
	 * @param applicationUrl the application URL value
	 */
	public void setApplicationUrl(String applicationUrl) {
		webSupport.setApplicationUrl(applicationUrl);
	}
	
	/**
	 * Adds a ConnectInterceptor to receive callbacks during the connection process.
	 * Useful for programmatic configuration.
	 * @param interceptor the connect interceptor to add
	 */
	public void addInterceptor(ConnectInterceptor<?> interceptor) {
		Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(interceptor.getClass(), ConnectInterceptor.class);
		connectInterceptors.add(serviceApiType, interceptor);
	}

	/**
	 * Process the authorization callback from an OAuth 1 service provider.
	 * Called after the user authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the user's local account to the account they hold at the service provider
	 * Removes the request token from the session since it is no longer valid after the connection is established.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET, params="oauth_token")
	public RedirectView oauth1Callback(
		@PathVariable String providerId, 
		NativeWebRequest request) {

		String promotionDeployPath = (String)request.getAttribute("promotionDeployPath", RequestAttributes.SCOPE_GLOBAL_SESSION);
		
		OAuth1ConnectionFactory<?> connectionFactory = (OAuth1ConnectionFactory<?>)socialConnectionRepositoryFactoryLocator.getConnectionFactory(
			promotionDeployPath,
			providerId);
		
		Connection<?> connection = webSupport.completeConnection(connectionFactory, request);
		addConnection(connection, connectionFactory, request);

		return connectionStatusRedirect(providerId, request);
	}

	/**
	 * Process the authorization callback from an OAuth 2 service provider.
	 * Called after the user authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the user's local account to the account they hold at the service provider.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET, params="code")
	public RedirectView oauth2Callback(
		@PathVariable String providerId, 
		NativeWebRequest request) {
		
		String promotionDeployPath = (String)request.getAttribute("promotionDeployPath", RequestAttributes.SCOPE_GLOBAL_SESSION);
		
		OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>)socialConnectionRepositoryFactoryLocator.getConnectionFactory(
			promotionDeployPath,
			providerId);
		
		Connection<?> connection = webSupport.completeConnection(connectionFactory, request);
		addConnection(connection, connectionFactory, request);

		return connectionStatusRedirect(providerId, request);
	}

	/**
	 * Remove all provider connections for a user account.
	 * The user has decided they no longer wish to use the service provider from this application.
	 * Note: requires {@link HiddenHttpMethodFilter} to be registered with the '_method' request parameter set to 'DELETE' to convert web browser POSTs to DELETE requests.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.DELETE)
	public String removeConnections(@PathVariable String providerId, NativeWebRequest request) {
		
		Object promotionDeployPath = request.getParameter("promotionDeployPath");
		if (promotionDeployPath == null) {
			promotionDeployPath = request.getAttribute("promotionDeployPath", RequestAttributes.SCOPE_REQUEST);
		}
		if (promotionDeployPath == null) {
			String errorMessage = "'promotionDeployPath' not found in request, either as query parameter or form element!  Operation will fail.";
			logger.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		request.setAttribute("promotionDeployPath", promotionDeployPath, RequestAttributes.SCOPE_GLOBAL_SESSION);

		request.setAttribute("providerId", providerId, RequestAttributes.SCOPE_GLOBAL_SESSION);
						
		Object uid = request.getParameter("uid");
		if (uid == null) {
			uid = request.getAttribute("uid", RequestAttributes.SCOPE_REQUEST); 
		}
		if (uid == null) {
			String errorMessage = "'uid' not found in request, either as query parameter or form element!  Operation will fail.";
			logger.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		request.setAttribute("uid", uid, RequestAttributes.SCOPE_GLOBAL_SESSION);
				
		connectionRepository.removeConnections(providerId);
		
		return "status";
	}

	/**
	 * Remove a single provider connection associated with a user account.
	 * The user has decided they no longer wish to use the service provider account from this application.
	 * Note: requires {@link HiddenHttpMethodFilter} to be registered with the '_method' request parameter set to 'DELETE' to convert web browser POSTs to DELETE requests.
	 */
	@RequestMapping(value="/{providerId}/{providerUserId}", method=RequestMethod.DELETE)
	public String removeConnection(@PathVariable String providerId, @PathVariable String providerUserId, NativeWebRequest request) {
		
		Object promotionDeployPath = request.getParameter("promotionDeployPath");
		if (promotionDeployPath == null) {
			promotionDeployPath = request.getAttribute("promotionDeployPath", RequestAttributes.SCOPE_REQUEST);
		}
		if (promotionDeployPath == null) {
			String errorMessage = "'promotionDeployPath' not found in request, either as query parameter or form element!  Operation will fail.";
			logger.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		request.setAttribute("promotionDeployPath", promotionDeployPath, RequestAttributes.SCOPE_GLOBAL_SESSION);

		request.setAttribute("providerId", providerId, RequestAttributes.SCOPE_GLOBAL_SESSION);
				
		Object uid = request.getParameter("uid");
		if (uid == null) {
			uid = request.getAttribute("uid", RequestAttributes.SCOPE_REQUEST); 
		}
		if (uid == null) {
			String errorMessage = "'uid' not found in request, either as query parameter or form element!  Operation will fail.";
			logger.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		request.setAttribute("uid", uid, RequestAttributes.SCOPE_GLOBAL_SESSION);
				
		connectionRepository.removeConnection(new ConnectionKey(providerId, providerUserId));
		
		return "status";
	}

	/**
	 * Returns the view name of a page to display for a provider when the user is not connected to the provider.
	 * Typically this page would offer the user an opportunity to create a connection with the provider.
	 * Defaults to "connect/{providerId}Connect". May be overridden to return a custom view name.
	 * @param providerId the ID of the provider to display the connection status for.
	 */
	protected String connectView(String providerId) {
		return getViewPath() + providerId + "Connect";		
	}

	/**
	 * Returns the view name of a page to display for a provider when the user is connected to the provider.
	 * Typically this page would allow the user to disconnect from the provider.
	 * Defaults to "connect/{providerId}Connected". May be overridden to return a custom view name.
	 * @param providerId the ID of the provider to display the connection status for.
	 */
	protected String connectedView(String providerId) {
		return getViewPath() + providerId + "Connected";		
	}

	/**
	 * Returns a RedirectView with the URL to redirect to after a connection is created or deleted.
	 * Defaults to "/connect/{providerId}" relative to DispatcherServlet's path. 
	 * May be overridden to handle custom redirection needs.
	 * @param providerId the ID of the provider for which a connection was created or deleted.
	 * @param request the NativeWebRequest used to access the servlet path when constructing the redirect path.
	 */
	protected RedirectView connectionStatusRedirect(String providerId, NativeWebRequest request) {
		HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
		String path = "/connect/" + providerId + getPathExtension(servletRequest);
		if (prependServletPath(servletRequest)) {
			path = servletRequest.getServletPath() + path;
		}
		return new RedirectView(path, true);
	}

	// internal helpers
	
	/*
	 * 
	 * @param request
	 * @return
	 */
	private boolean prependServletPath(HttpServletRequest request) {
		return !this.urlPathHelper.getPathWithinServletMapping(request).equals("");
	}
	
	/*
	 * Determines the path extension, if any.
	 * Returns the extension, including the period at the beginning, or an empty string if there is no extension.
	 * This makes it possible to append the returned value to a path even if there is no extension.
	 */
	private String getPathExtension(HttpServletRequest request) {
		String fileName = WebUtils.extractFullFilenameFromUrlPath(request.getRequestURI());		
		String extension = StringUtils.getFilenameExtension(fileName);
		return extension != null ? "." + extension : "";
	}

	/*
	 * 
	 * @return
	 */
	private String getViewPath() {
		return "connect/";
	}
	
	/*
	 * 
	 * @param connection
	 * @param connectionFactory
	 * @param request
	 */
	private void addConnection(Connection<?> connection, ConnectionFactory<?> connectionFactory, WebRequest request) {
		
		ConnectionData connectionData = connection.createData();
		String accessToken = connectionData.getAccessToken();
		String accessTokenSecret = connectionData.getSecret();

		AccessTokenResponse accessTokenResponse = new AccessTokenResponse(
			null, 
			accessToken, 
			accessTokenSecret);

		String promotionDeployPath = (String)request.getAttribute("promotionDeployPath", RequestAttributes.SCOPE_GLOBAL_SESSION);
		String providerId = (String)request.getAttribute("providerId", RequestAttributes.SCOPE_GLOBAL_SESSION);
		String uid = (String)request.getAttribute("uid", RequestAttributes.SCOPE_GLOBAL_SESSION);
		
		this.accessTokenCache.putAccessTokenResponse(
			promotionDeployPath, 
			providerId, 
			uid, 
			accessTokenResponse);
		
		try {
					
			if (this.accessTokenCache.getEnableAccessTokenPersistence()) {
				this.connectionRepository.addConnection(connection);
			}

			postConnect(connectionFactory, connection, request);
						
		} catch (DuplicateConnectionException e) {

			if (this.accessTokenCache.getEnableAccessTokenPersistence()) {
				
				logger.debug("Connection already exists for promotionDeployPath: [" + promotionDeployPath + "], providerId: [" + providerId + "] and uid: [" + uid + "], updating stored access token.");
				this.connectionRepository.updateConnection(connection);						
			}
		}
	}

	/*
	 * 
	 * @param connectionFactory
	 * @param parameters
	 * @param request
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void preConnect(ConnectionFactory<?> connectionFactory, MultiValueMap<String, String> parameters, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
			interceptor.preConnect(connectionFactory, parameters, request);
		}
	}

	/*
	 * 
	 * @param connectionFactory
	 * @param connection
	 * @param request
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void postConnect(ConnectionFactory<?> connectionFactory, Connection<?> connection, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
			interceptor.postConnect(connection, request);
		}
	}

	/*
	 * 
	 * @param connectionFactory
	 * @return
	 */
	private List<ConnectInterceptor<?>> interceptingConnectionsTo(ConnectionFactory<?> connectionFactory) {
		Class<?> serviceType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(), ConnectionFactory.class);
		List<ConnectInterceptor<?>> typedInterceptors = connectInterceptors.get(serviceType);
		if (typedInterceptors == null) {
			typedInterceptors = Collections.emptyList();
		}
		return typedInterceptors;
	}

	/*
	 * 
	 * @param request
	 */
	private void setNoCache(NativeWebRequest request) {
		HttpServletResponse response = request.getNativeResponse(HttpServletResponse.class);
		if (response != null) {
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 1L);
			response.setHeader("Cache-Control", "no-cache");
			response.addHeader("Cache-Control", "no-store");
		}
	}
	

	// TDM Changes below
	
	
	/**
	 * Process a connect form submission by commencing the process of establishing a connection to the provider on behalf of the member.
	 * For OAuth1, fetches a new request token from the provider, temporarily stores it in the session, then redirects the member to the provider's site for authorization.
	 * For OAuth2, redirects the user to the provider's site for authorization.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.POST)
	public RedirectView connect(@PathVariable String providerId, NativeWebRequest request) {
				
		setNoCache(request);
		
		Object data = request.getParameter("data");
		if (data == null) {
			data = request.getAttribute("data", RequestAttributes.SCOPE_REQUEST); 
		}
		if (data == null) {
			String errorMessage = "'data' not found in request, either as query parameter or form element!  Promotion user experience may not work properly.";
			logger.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		request.setAttribute("data", data, RequestAttributes.SCOPE_GLOBAL_SESSION);
		
		
		Object cid = request.getParameter("cid");
		if (cid == null) {
			cid = request.getAttribute("cid", RequestAttributes.SCOPE_REQUEST); 
		}
		if (cid == null) {
			String errorMessage = "'cid' not found in request, either as query parameter or form element!  Promotion user experience may not work properly.";
			logger.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		request.setAttribute("cid", cid, RequestAttributes.SCOPE_GLOBAL_SESSION);

		
		Object siteUrl = request.getParameter("siteUrl");
		if (siteUrl == null) {
			siteUrl = request.getAttribute("siteUrl", RequestAttributes.SCOPE_REQUEST); 
		}
		if (siteUrl == null) {
			String errorMessage = "'siteUrl' not found in request, either as query parameter or form element!  Promotion user experience may not work properly.";
			logger.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		request.setAttribute("siteUrl", siteUrl, RequestAttributes.SCOPE_GLOBAL_SESSION);
		
		
		Object promotionDeployPath = request.getParameter("promotionDeployPath");
		if (promotionDeployPath == null) {
			promotionDeployPath = request.getAttribute("promotionDeployPath", RequestAttributes.SCOPE_REQUEST);
		}
		if (promotionDeployPath == null) {
			String errorMessage = "'promotionDeployPath' not found in request, either as query parameter or form element!  Authentication with social provider will fail.";
			logger.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		request.setAttribute("promotionDeployPath", promotionDeployPath, RequestAttributes.SCOPE_GLOBAL_SESSION);

		request.setAttribute("providerId", providerId, RequestAttributes.SCOPE_GLOBAL_SESSION);
				
		Object uid = request.getParameter("uid");
		if (uid == null) {
			uid = request.getAttribute("uid", RequestAttributes.SCOPE_REQUEST); 
		}
		if (uid == null) {
			String errorMessage = "'uid' not found in request, either as query parameter or form element!  Authentication with social provider will fail.";
			logger.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		request.setAttribute("uid", uid, RequestAttributes.SCOPE_GLOBAL_SESSION);
		
		
		ConnectionFactory<?> connectionFactory = socialConnectionRepositoryFactoryLocator.getConnectionFactory(
			promotionDeployPath.toString(), 
			providerId);
		
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>(); 
		preConnect(connectionFactory, parameters, request);
		try {
			return new RedirectView(webSupport.buildOAuthUrl(connectionFactory, request, parameters));
		} catch (Exception e) {
			request.setAttribute(PROVIDER_ERROR_ATTRIBUTE, e, RequestAttributes.SCOPE_SESSION);
			return connectionStatusRedirect(providerId, request);
		}
	}

	/**
	 * Render the status of the connections to the service provider to the user as HTML in their web browser.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET)
	public String connectionStatus(@PathVariable String providerId, NativeWebRequest request, Model model) {

		setNoCache(request);
		
		String promotionDeployPath = (String)request.getAttribute("promotionDeployPath", RequestAttributes.SCOPE_GLOBAL_SESSION);
		String uid = (String)request.getAttribute("uid", RequestAttributes.SCOPE_GLOBAL_SESSION);
		String cid = (String)request.getAttribute("cid", RequestAttributes.SCOPE_GLOBAL_SESSION);
		String siteUrl = (String)request.getAttribute("siteUrl", RequestAttributes.SCOPE_GLOBAL_SESSION);
		String data = (String)request.getAttribute("data", RequestAttributes.SCOPE_GLOBAL_SESSION);
		
		String providerUserId = "";
		String displayName = "";
		String profileUrl = "";
		String accessToken = "";
		String accessTokenSecret = "";
		String errorMessage = "";
		
		AccessTokenResponse accessTokenResponse = this.accessTokenCache.getAccessTokenResponse(promotionDeployPath, providerId, uid);
		
		if (accessTokenResponse != null) {

			accessToken = accessTokenResponse.getAccessToken();
			accessTokenSecret = accessTokenResponse.getAccessTokenSecret();
			errorMessage = "";
			logger.debug("Successfully connected uid: [" + uid + "] for promotion: [" + promotionDeployPath + "] to provider: [" + providerId + "].  Returning access token to client.");
			
		} else if (this.accessTokenCache.getEnableAccessTokenPersistence()) {
			
			List<Connection<?>> connections = connectionRepository.findConnections(providerId);
			if (connections != null && connections.size() > 0) {
				
				Connection<?> socialConnection = connections.get(0);
				ConnectionData connectionData = socialConnection.createData();

				providerUserId = connectionData.getProviderUserId();
				displayName = connectionData.getDisplayName();
				profileUrl = connectionData.getProfileUrl();
				accessToken= connectionData.getAccessToken();
				accessTokenSecret = connectionData.getSecret();
				errorMessage = "";
				logger.debug("Successfully connected uid: [" + uid + "] for promotion: [" + promotionDeployPath + "] to provider: [" + providerId + "].  Returning access token to client.");
							
				connectionRepository.updateConnection(socialConnection);
			}
		}
		
		model.addAttribute("error", errorMessage);
		model.addAttribute("cid", cid);
		model.addAttribute("siteUrl", siteUrl);
		model.addAttribute("data", data);
		model.addAttribute("promotionDeployPath", promotionDeployPath);
		model.addAttribute("providerId", providerId);
		model.addAttribute("uid", uid);
		model.addAttribute("providerUserId", providerUserId);
		model.addAttribute("displayName", displayName);
		model.addAttribute("profileUrl", profileUrl);
		model.addAttribute("accessToken", accessToken);
		model.addAttribute("accessTokenSecret", accessTokenSecret);
		
		return "socialAuthResponse";
	}
}